package nd801project.elmasry.thankyou.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import nd801project.elmasry.thankyou.utilities.DbUtils;
import nd801project.elmasry.thankyou.utilities.HelperUtils;
import timber.log.Timber;

import static com.google.android.youtube.player.YouTubePlayer.*;

public class SongDetailFragment extends Fragment implements
        YouTubePlayer.OnInitializedListener, View.OnClickListener {

    private static final String IS_PLAYING_KEY = "is_playing";

    private String mVideoId;

    private YouTubePlayerFragment mYouTubePlayerFragment;
    private YouTubePlayer mPlayer;
    private String mDeveloperKey;
    private int mPlayingPositionMillis;

    private boolean mAutoPlayVideo = true; // we want to auto play the video by default

    private Bundle mSavedInstanceState;
    private SongVideoInfo mSongVideoInfo;
    private ImageView mFavoriteButton;

    private PlayerStateChangeListener mPlayerStateChangeListener;



    interface SongVideoEndedCallback {
        void onSongVideoEnded();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // making sure the developer key was set
        mDeveloperKey = getString(R.string.developer_key);
        if (mDeveloperKey.equals(getString(R.string.placeholder))) {
            Timber.e("ERROR: YOU MUST SET YOUR DEVELOPER KEY IN STRINGS.XML FILE");
            return null;
        }

        final View rootView = inflater.inflate(R.layout.fragment_song_detail, container, false);
        mYouTubePlayerFragment = (YouTubePlayerFragment) getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        mPlayerStateChangeListener = new MyPlayerStateChangeListener();
        mFavoriteButton = rootView.findViewById(R.id.favorite_fab);

        rootView.findViewById(R.id.share_fab).setOnClickListener(this);
        rootView.findViewById(R.id.favorite_fab).setOnClickListener(this);

        this.mSavedInstanceState = savedInstanceState;

        return rootView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!TextUtils.isEmpty(mVideoId)) {
            this.mPlayer = youTubePlayer;
            mPlayer.setPlayerStateChangeListener(mPlayerStateChangeListener);

            // making player in full screen in phone case and landscape mode only
            boolean isTablet = getResources().getBoolean(R.bool.isTablet);
            if (!isTablet) {
                boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                mPlayer.setFullscreen(isLandscape);
            }

            if (!wasRestored) {
                loadVideo(mPlayingPositionMillis);
            } else if (mSavedInstanceState != null && mSavedInstanceState.containsKey(IS_PLAYING_KEY)) {
                boolean isPlaying = mSavedInstanceState.getBoolean(IS_PLAYING_KEY);
                if (isPlaying) mPlayer.play();
            }
        } else {
            Timber.e("Error: videoId is null or empty string");
        }
    }

    private void loadVideo(int startFromMillis) {
        if (mAutoPlayVideo) mPlayer.loadVideo(mVideoId, startFromMillis);
        else                mPlayer.cueVideo(mVideoId, startFromMillis);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPlayer != null) {
            // we save the playing position to be able seek the video to this position when the
            // activity changes its state from invisible to visible
            mPlayingPositionMillis = mPlayer.getCurrentTimeMillis();

            // we always want the video NOT playing automatically when the activity changes its state
            // from invisible to visible
            mAutoPlayVideo = false;

            mPlayer.pause();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDeveloperKey.equals(R.string.placeholder)) return;

        mYouTubePlayerFragment.initialize(mDeveloperKey, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Timber.e("Error: Youtube initialization failed: " + youTubeInitializationResult.toString());
    }

    public void setSongVideoInfo(SongVideoInfo songVideoInfo) {
        if (mDeveloperKey.equals(R.string.placeholder)) return;

        mVideoId = songVideoInfo.getVideoId();
        mSongVideoInfo = songVideoInfo;

        init();
    }

    private void init() {

        // setting song video title
        String videoTitle = mSongVideoInfo.getVideoTitle();
        TextView titleTextView = getActivity().findViewById(R.id.song_title_text_view);
        if (!TextUtils.isEmpty(videoTitle)) {
            titleTextView.setText(videoTitle);
        } else {
            titleTextView.setText(R.string.no_title_for_video);
        }

        mAutoPlayVideo = true; // we want the video to be auto-play by default

        if (mPlayer == null) {
            mYouTubePlayerFragment.initialize(mDeveloperKey, this);
        } else {
            loadVideo(0);
        }

        if (DbUtils.isSongInFavorites(getActivity(), mVideoId)) {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_yellow);
        } else {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_white);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPlayer != null) {
            outState.putBoolean(IS_PLAYING_KEY, mPlayer.isPlaying());
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.share_fab:
                shareButtonHandler();
                break;
            case R.id.favorite_fab:
                favoriteButtonHandler();
                break;
        }
    }

    public void shareButtonHandler() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String videoLink = "https://www.youtube.com/watch?v=" + mVideoId;
        sendIntent.putExtra(Intent.EXTRA_TEXT, videoLink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void favoriteButtonHandler() {
        if (mSongVideoInfo == null || TextUtils.isEmpty(mVideoId)) {
            HelperUtils.showSnackbar(getActivity(), R.string.no_song);
            return;
        }

        // we toggle the state of the song
        if (DbUtils.isSongInFavorites(getActivity(), mVideoId)) {
            boolean isDeleted = DbUtils.deleteFromFavorites(getActivity(), mVideoId);
            if (isDeleted) {
                mFavoriteButton.setImageResource(R.drawable.ic_favorite_white);
                HelperUtils.showSnackbar(getActivity(), R.string.removed_from_favorites);
            }
        } else {
            boolean isInserted = DbUtils.insertInFavorites(getActivity(), mSongVideoInfo);
            if (isInserted) {
                mFavoriteButton.setImageResource(R.drawable.ic_favorite_yellow);
                HelperUtils.showSnackbar(getActivity(), R.string.added_to_favorites);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof SongVideoEndedCallback)) {
            throw new ClassCastException("the host activity must implement SongVideoEndedCallback");
        }
    }

    private class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
        }

        @Override
        public void onLoaded(String s) {
        }

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onVideoStarted() {
        }

        @Override
        public void onVideoEnded() {
            SongVideoEndedCallback songVideoEndedCallback = (SongVideoEndedCallback) getActivity();
            songVideoEndedCallback.onSongVideoEnded();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            mPlayer = null;
            Timber.e("Error happened: " + errorReason);
        }
    }
}
