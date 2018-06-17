package nd801project.elmasry.thankyou.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import timber.log.Timber;

public class SongDetailFragment extends Fragment implements
        YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener, View.OnClickListener {

    private static final String IS_PLAYING_KEY = "is_playing";

    private String mVideoId;

    private YouTubePlayerFragment mYouTubePlayerFragment;
    private YouTubePlayer mPlayer;
    private String mDeveloperKey;
    private int mPlayingPositionMillis;

    private boolean mAutoPlayVideo = true; // we want to auto play by default

    private Bundle mSavedInstanceState;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.share_fab:
                shareButtonHandler();
                break;
        }
    }

    interface SongVideoEndedCallback {
        void onSongVideoEnded();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof SongVideoEndedCallback)) {
            throw new ClassCastException("the host activity must implement SongVideoEndedCallback");
        }
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
        rootView.findViewById(R.id.share_fab).setOnClickListener(this);

        this.mSavedInstanceState = savedInstanceState;

        return rootView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!TextUtils.isEmpty(mVideoId)) {
            this.mPlayer = youTubePlayer;
            mPlayer.setPlayerStateChangeListener(this);

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
        String videoTitle = songVideoInfo.getVideoTitle();
        ((TextView) getActivity().findViewById(R.id.song_title_text_view)).setText(videoTitle);

        mAutoPlayVideo = true; // we want the video to be auto-play by default

        if (mPlayer == null) {
            mYouTubePlayerFragment.initialize(mDeveloperKey, this);
        } else {
            loadVideo(0);
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

    public void shareButtonHandler() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String videoLink = "https://www.youtube.com/watch?v=" + mVideoId;
        sendIntent.putExtra(Intent.EXTRA_TEXT, videoLink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
