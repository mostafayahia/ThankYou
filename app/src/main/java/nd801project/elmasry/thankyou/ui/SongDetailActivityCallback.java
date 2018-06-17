package nd801project.elmasry.thankyou.ui;

import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import timber.log.Timber;

public class SongDetailActivityCallback extends AppCompatActivity implements SongDetailFragment.SongVideoEndedCallback {

    public static final String EXTRA_SONG_VIDEO_POSITION = "nd801project.elmasry.thankyou.extra.SONG_VIDEO_POSITION";
    public static final String EXTRA_SONG_VIDEO_INFO_LIST = "nd801project.elmasry.thankyou.extra.SONG_VIDEO_INFO_LIST";
    private static final String SONG_VIDEO_POSITION_KEY = "song_video_position";

    private static final int INVALID_POSITION = -1;
    private List<SongVideoInfo> mSongVideoInfoList;
    private int mSongVideoPosition;
    private SongDetailFragment mSongDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        mSongDetailFragment = (SongDetailFragment) getFragmentManager().findFragmentById(R.id.song_detail_fragment);

        mSongVideoInfoList = getIntent().getParcelableArrayListExtra(EXTRA_SONG_VIDEO_INFO_LIST);
        if (mSongVideoInfoList == null) {
            Timber.e("songVideoInfoList is null");
            return;
        }

        // the mSongVideoPosition may be changed using the previous and next buttons so we need to
        // save it and restore it
        if (savedInstanceState == null) {
            mSongVideoPosition = getIntent().getIntExtra(EXTRA_SONG_VIDEO_POSITION, INVALID_POSITION);

            if (mSongVideoPosition < 0) {
                Timber.e("songVideoPosition wasn't sent as extra");
                return;
            }
        } else {
            if (savedInstanceState.containsKey(SONG_VIDEO_POSITION_KEY))
                mSongVideoPosition = savedInstanceState.getInt(SONG_VIDEO_POSITION_KEY);
        }

        SongVideoInfo songVideoInfo = mSongVideoInfoList.get(mSongVideoPosition);
        mSongDetailFragment.setSongVideoInfo(songVideoInfo);

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
            Snackbar.make(findViewById(android.R.id.content), R.string.no_previous_song, Snackbar.LENGTH_SHORT).show();
        } else {
            mSongDetailFragment.setSongVideoInfo(mSongVideoInfoList.get(--mSongVideoPosition));
        }
    }

    public void nextButtonHandler(View view) {
        if (mSongVideoPosition >= mSongVideoInfoList.size()-1) {
            Snackbar.make(findViewById(android.R.id.content), R.string.no_next_song, Snackbar.LENGTH_SHORT).show();
        } else {
            mSongDetailFragment.setSongVideoInfo(mSongVideoInfoList.get(++mSongVideoPosition));
        }
    }

    @Override
    public void onSongVideoEnded() {
        // we will automatically play the next song in the list if applicable
        nextButtonHandler(null);
    }
}
