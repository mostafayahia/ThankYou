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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.analytics.AnalyticsApplication;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import nd801project.elmasry.thankyou.utilities.DbUtils;
import nd801project.elmasry.thankyou.utilities.HelperUtils;
import nd801project.elmasry.thankyou.widget.ThankUWidgetService;
import timber.log.Timber;

import static com.google.android.youtube.player.YouTubePlayer.*;

public class SongDetailFragment extends Fragment implements
        YouTubePlayer.OnInitializedListener, View.OnClickListener {

    private static final String IS_PLAYING_KEY = "is_playing";
    private static final String SONG_VIDEO_INFO_KEY = "song_video_info";
    private static final String IS_FULL_SCREEN_LANDSCAPE_KEY = "is_full_screen_landscape";

    private String mVideoId;

    private YouTubePlayerFragment mYouTubePlayerFragment;
    private YouTubePlayer mPlayer;
    private String mDeveloperKey;
    private int mPlayingPositionMillis;

    private boolean mAutoPlayVideo;
    private boolean mIsFullScreenInLandscape;

    private SongVideoInfo mSongVideoInfo;
    private ImageView mFavoriteButton;

    private PlayerStateChangeListener mPlayerStateChangeListener;
    private boolean mIsPlaying;
    private Tracker mTracker;


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

        // restoring members' values after rotation the device
        mIsPlaying = false; // default value
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(IS_PLAYING_KEY))
                mIsPlaying = savedInstanceState.getBoolean(IS_PLAYING_KEY);
            if (savedInstanceState.containsKey(SONG_VIDEO_INFO_KEY)) {
                mSongVideoInfo = (SongVideoInfo) savedInstanceState.get(SONG_VIDEO_INFO_KEY);
                mVideoId = mSongVideoInfo.getVideoId();
            }
            mIsFullScreenInLandscape = savedInstanceState.getBoolean(IS_FULL_SCREEN_LANDSCAPE_KEY);
        }


        // regarding analytics ==> Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // regarding analytics
        mTracker.setScreenName("detailFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!TextUtils.isEmpty(mVideoId)) {
            this.mPlayer = youTubePlayer;
            mPlayer.setPlayerStateChangeListener(mPlayerStateChangeListener);

            if (mIsFullScreenInLandscape) {
                boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                mPlayer.setFullscreen(isLandscape);
            }

            if (!wasRestored) {
                loadVideo(mPlayingPositionMillis);
            } else if (mIsPlaying) {
                mPlayer.play();
            }
        } else {
            Timber.e("Error: videoId is null or empty string");
        }
    }

    private void loadVideo(int startFromMillis) {
        if (mAutoPlayVideo) mPlayer.loadVideo(mVideoId, startFromMillis);
        else mPlayer.cueVideo(mVideoId, startFromMillis);
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

            releasePlayer();
        }
    }

    /**
     * stop and release the resources related to youtube player
     */
    public void releasePlayer() {
        if (mPlayer == null) return;

        mPlayer.pause();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!TextUtils.isEmpty(mVideoId))
            init();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Timber.e("Error: Youtube initialization failed: " + youTubeInitializationResult.toString());
    }

    /**
     * setting song video info for this fragment and the video will be auto-play by default
     *
     * @param songVideoInfo
     * @param isFullScreenInLandscape
     */
    public void setSongVideoInfo(SongVideoInfo songVideoInfo, boolean isFullScreenInLandscape) {
        mVideoId = songVideoInfo.getVideoId();
        mSongVideoInfo = songVideoInfo;
        mAutoPlayVideo = true; // we want the video to be auto-play by default
        mIsFullScreenInLandscape = isFullScreenInLandscape;

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

        if (mSongVideoInfo != null) {
            outState.putParcelable(SONG_VIDEO_INFO_KEY, mSongVideoInfo);
        }

        outState.putBoolean(IS_FULL_SCREEN_LANDSCAPE_KEY, mIsFullScreenInLandscape);
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

        // [START custom_event] regarding analytics
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
        // [END custom_event]
    }

    private void favoriteButtonHandler() {
        if (mSongVideoInfo == null || TextUtils.isEmpty(mVideoId)) {
            HelperUtils.showSnackbar(getActivity(), R.string.no_song);
            return;
        }

        // check if the song is in favorites then we will toggle the state of the song
        if (DbUtils.isSongInFavorites(getActivity(), mVideoId)) {
            boolean isDeleted = DbUtils.deleteFromFavorites(getActivity(), mVideoId);
            if (isDeleted) {
                mFavoriteButton.setImageResource(R.drawable.ic_favorite_white);
                HelperUtils.showSnackbar(getActivity(), R.string.removed_from_favorites);

                if (DbUtils.hasFavoriteSongs(getActivity()))
                    ThankUWidgetService.startActionDisplayOneOfFavorites(getActivity());
                else
                    ThankUWidgetService.startActionDisplayLastSeenSong(getActivity());
            }
        } else {
            boolean isInserted = DbUtils.insertInFavorites(getActivity(), mSongVideoInfo);
            if (isInserted) {
                mFavoriteButton.setImageResource(R.drawable.ic_favorite_yellow);
                HelperUtils.showSnackbar(getActivity(), R.string.added_to_favorites);

                ThankUWidgetService.startActionDisplayOneOfFavorites(getActivity());
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
