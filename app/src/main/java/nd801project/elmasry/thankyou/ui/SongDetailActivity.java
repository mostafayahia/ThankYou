package nd801project.elmasry.thankyou.ui;

import android.content.res.Configuration;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import nd801project.elmasry.thankyou.utilities.DbUtils;
import nd801project.elmasry.thankyou.utilities.HelperUtils;
import nd801project.elmasry.thankyou.utilities.PreferenceUtils;
import nd801project.elmasry.thankyou.widget.ThankUWidgetService;
import timber.log.Timber;

public class SongDetailActivity extends AppCompatActivity implements SongDetailFragment.SongVideoEndedCallback {

    public static final String EXTRA_SONG_VIDEO_POSITION = "nd801project.elmasry.thankyou.extra.SONG_VIDEO_POSITION";
    public static final String EXTRA_SONG_VIDEO_INFO_LIST = "nd801project.elmasry.thankyou.extra.SONG_VIDEO_INFO_LIST";
    public static final String EXTRA_SONG_VIDEO_INFO = "nd801project.elmasry.thankyou.extra.SONG_VIDEO_INFO";

    private static final String SONG_VIDEO_POSITION_KEY = "song_video_position";

    private List<SongVideoInfo> mSongVideoInfoList;
    private int mSongVideoPosition = RecyclerView.NO_POSITION; // has an invalid value by default
    private SongDetailFragment mSongDetailFragment;

    private static final boolean IS_FULL_SCREEN_IN_LANDSCAPE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        mSongDetailFragment = (SongDetailFragment) getFragmentManager().findFragmentById(R.id.song_detail_fragment);

        mSongVideoInfoList = getIntent().getParcelableArrayListExtra(EXTRA_SONG_VIDEO_INFO_LIST);
        if (mSongVideoInfoList == null) {
            Timber.w("songVideoInfoList is null");
        }

        SongVideoInfo songVideoInfo = getIntent().getParcelableExtra(EXTRA_SONG_VIDEO_INFO);

        if (mSongVideoInfoList == null && songVideoInfo != null) {
            // widget case
            // in this case we want **only** play and display the given song so we hide next and
            // previous buttons
            findViewById(R.id.next_fab).setVisibility(View.GONE);
            findViewById(R.id.previous_fab).setVisibility(View.GONE);
            mSongDetailFragment.setSongVideoInfo(songVideoInfo, IS_FULL_SCREEN_IN_LANDSCAPE);
            // save last seen song video info and the song position and start widget service action if necessary
            saveLastSeenSongVideo(songVideoInfo);
        } else {
            // NOT widget case
            // the mSongVideoPosition may be changed using the previous and next buttons so we need to
            // save it and restore it
            if (savedInstanceState == null) {
                mSongVideoPosition = getIntent().getIntExtra(EXTRA_SONG_VIDEO_POSITION, RecyclerView.NO_POSITION);

                if (mSongVideoPosition < 0) {
                    Timber.e("songVideoPosition wasn't sent as extra");
                    return;
                }

                songVideoInfo = mSongVideoInfoList.get(mSongVideoPosition);
                mSongDetailFragment.setSongVideoInfo(songVideoInfo, IS_FULL_SCREEN_IN_LANDSCAPE);
                // save last seen song video info and the song position and start widget service action if necessary
                saveLastSeenSongVideo(songVideoInfo);
            } else {
                if (savedInstanceState.containsKey(SONG_VIDEO_POSITION_KEY))
                    mSongVideoPosition = savedInstanceState.getInt(SONG_VIDEO_POSITION_KEY);
            }
        }

        // hiding the status bar only in landscape mode
        View decorView = getWindow().getDecorView();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Hide the status bar and up button container.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            findViewById(R.id.up_container).setVisibility(View.GONE);
        } else {
            // show the status bar and up button container
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
            findViewById(R.id.up_container).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSongVideoPosition >= 0)
            outState.putInt(SONG_VIDEO_POSITION_KEY, mSongVideoPosition);
    }

    public void upButtonHandler(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    public void previousButtonHandler(View view) {
        if (mSongVideoPosition <= 0) {
            HelperUtils.showSnackbar(this, R.string.no_previous_song);
            return;
        } else {
            mSongDetailFragment.setSongVideoInfo(mSongVideoInfoList.get(--mSongVideoPosition), IS_FULL_SCREEN_IN_LANDSCAPE);
        }

        // save last seen song video info and the song position and start widget service action if necessary
        SongVideoInfo songVideoInfo = mSongVideoInfoList.get(mSongVideoPosition);
        saveLastSeenSongVideo(songVideoInfo);
    }

    public void nextButtonHandler(View view) {
        if (mSongVideoPosition >= mSongVideoInfoList.size()-1) {
            HelperUtils.showSnackbar(this, R.string.no_next_song);
            return;
        } else {
            mSongDetailFragment.setSongVideoInfo(mSongVideoInfoList.get(++mSongVideoPosition), IS_FULL_SCREEN_IN_LANDSCAPE);
        }

        // save last seen song video info and the song position and start widget service action if necessary
        SongVideoInfo songVideoInfo = mSongVideoInfoList.get(mSongVideoPosition);
        saveLastSeenSongVideo(songVideoInfo);
    }

    private void saveLastSeenSongVideo(SongVideoInfo songVideoInfo) {
        PreferenceUtils.setLastSeenSongVideo(this, mSongVideoPosition, songVideoInfo);
        if (!DbUtils.hasFavoriteSongs(this))
            ThankUWidgetService.startActionDisplayLastSeenSong(this);
    }

    @Override
    public void onSongVideoEnded() {
        if (mSongVideoInfoList == null) return;
        // we will automatically play the next song in the list if applicable
        nextButtonHandler(null);
    }
}
