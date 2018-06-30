package nd801project.elmasry.thankyou.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import nd801project.elmasry.thankyou.utilities.DbUtils;
import nd801project.elmasry.thankyou.utilities.HelperUtils;
import nd801project.elmasry.thankyou.utilities.NetworkUtils;
import nd801project.elmasry.thankyou.utilities.PreferenceUtils;
import nd801project.elmasry.thankyou.utilities.YoutubeApiJsonUtils;
import nd801project.elmasry.thankyou.widget.ThankUWidgetService;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements SongVideoAdapter.ImageViewClickCallback,
        SongDetailFragment.SongVideoEndedCallback, SongVideoAdapter.ImageViewSelectedCallback, ThankUTabHelper.TabSelectedCallback {

    private static final String SONG_VIDEO_INFO_LIST_KEY = "song_video_info_list";
    private static final String SELECTED_TAB_POSITION_KEY = "selected_tab_position";
    private static final String SONG_VIDEO_POSITION_KEY = "song_video_position";
    private static final String IS_SONG_DETAIL_LAYOUT_VISIBLE_KEY = "is_song_detail_layout_visible";

    public static final String EXTRA_SONG_VIDEO_POSITION = "nd801project.elmasry.thankyou.extra.SONG_VIDEO_POSITION";

    List<SongVideoInfo> mSongVideoInfoList;
    private SongListFragment mSongListFragment;
    private SongDetailFragment mSongDetailFragment;

    private boolean mTwoPane;
    private LinearLayout mSongDetailLayout;
    private int mSongVideoPosition = RecyclerView.NO_POSITION; // by default: has an invalid position
    boolean mIsSongDetailLayoutVisible;

    private ThankUTabHelper mThankUTabHelper;

    private static final boolean IS_VIDEO_FULL_SCREEN_IN_LANDSCAPE = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        mSongListFragment = (SongListFragment) getFragmentManager().findFragmentById(R.id.song_list_fragment);
        int selectedTabPosition = 0; // default value

        // handle the tablet case
        mSongDetailLayout = findViewById(R.id.song_detail_fragment);
        if (mSongDetailLayout != null) {
            mTwoPane = true;

            mSongDetailFragment = (SongDetailFragment) getFragmentManager().findFragmentById(R.id.song_detail_fragment);

            int songVideoPosition = RecyclerView.NO_POSITION;
            if (getIntent() != null) {
                // == widget case ==
                songVideoPosition = getIntent().getIntExtra(EXTRA_SONG_VIDEO_POSITION, RecyclerView.NO_POSITION);
            }

            // hide next and previous button from the fragment
            findViewById(R.id.next_fab).setVisibility(View.GONE);
            findViewById(R.id.previous_fab).setVisibility(View.GONE);

            // we will hide song detail layout at the beginning because No song is selected yet
            if (savedInstanceState == null) {
                setSongDetailLayoutVisibility(View.INVISIBLE);
            }

            // restore song video position in the list after rotating the device as well as detail layout visibility
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(SONG_VIDEO_POSITION_KEY))
                    mSongVideoPosition = savedInstanceState.getInt(SONG_VIDEO_POSITION_KEY);
                if (savedInstanceState.containsKey(IS_SONG_DETAIL_LAYOUT_VISIBLE_KEY)) {
                    mIsSongDetailLayoutVisible = savedInstanceState.getBoolean(IS_SONG_DETAIL_LAYOUT_VISIBLE_KEY);
                    if (mIsSongDetailLayoutVisible)
                        mSongDetailLayout.setVisibility(View.VISIBLE);
                    else
                        mSongDetailLayout.setVisibility(View.INVISIBLE);
                }
            } else if (mTwoPane && songVideoPosition >= 0) {
                // == widget case ==
                mSongVideoPosition = songVideoPosition;
            }

        } else {
            mTwoPane = false;
        }


        if (HelperUtils.isDeviceOnline(this)) {
            String apiKey = getString(R.string.developer_key);
            // making sure the developer key is set in strings.xml file
            if (apiKey.equals(getString(R.string.placeholder))) {
                Timber.e("ERROR: YOU MUST SET YOUR DEVELOPER KEY IN STRING.XML FILE");
            } else {
                // retrieve song video info array when rotating the device
                if (savedInstanceState != null && savedInstanceState.containsKey(SONG_VIDEO_INFO_LIST_KEY)) {
                    mSongVideoInfoList = savedInstanceState.getParcelableArrayList(SONG_VIDEO_INFO_LIST_KEY);
                    mSongListFragment.setSongVideoInfoList(mSongVideoInfoList, this,
                            this);
                } else {
                    if (mTwoPane && mSongVideoPosition >= 0) {
                        // == widget case ==
                        if (DbUtils.hasFavoriteSongs(this)) {
                            selectedTabPosition = ThankUTabHelper.POSITION_TAB_FAV;
                            mSongVideoInfoList = DbUtils.getAllFavoriteSongs(this);
                            mSongListFragment.setSongVideoInfoList(mSongVideoInfoList, this,
                                    this);
                            mSongListFragment.setSelectedItem(mSongVideoPosition);
                            setSongDetailLayoutVisibility(View.VISIBLE);
                            mSongDetailFragment.setSongVideoInfo(mSongVideoInfoList.get(mSongVideoPosition),
                                    IS_VIDEO_FULL_SCREEN_IN_LANDSCAPE);

                        } else {
                            new FetchSongsInfo().execute(apiKey);
                        }

                    } else {
                        // == NOT widget case ==
                        new FetchSongsInfo().execute(apiKey);
                    }
                }
            }
        } else {
            // there is no internet connection in this case, so we will notify the user
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
            return; // No point for continue
        }

        // regarding the app tabs
        if (savedInstanceState != null) {
            selectedTabPosition = savedInstanceState.getInt(SELECTED_TAB_POSITION_KEY);
        }
        mThankUTabHelper = new ThankUTabHelper(this, this, selectedTabPosition);


    }

    /**
     * set visibility for song detail layout
     *
     * @param visibility must be either View.VISIBLE or View.INVISIBLE
     */
    private void setSongDetailLayoutVisibility(int visibility) {
        if (visibility != View.VISIBLE && visibility != View.INVISIBLE) {
            throw new IllegalArgumentException("the argument must be either View.VISIBLE or View.INVISIBLE");
        }

        mSongDetailLayout.setVisibility(visibility);
        mIsSongDetailLayoutVisible = (visibility == View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSongVideoInfoList != null) {
            outState.putParcelableArrayList(SONG_VIDEO_INFO_LIST_KEY, (ArrayList) mSongVideoInfoList);
        }

        if (mTwoPane) {
            if (mSongVideoPosition >= 0)
                outState.putInt(SONG_VIDEO_POSITION_KEY, mSongVideoPosition);
            outState.putBoolean(IS_SONG_DETAIL_LAYOUT_VISIBLE_KEY, mIsSongDetailLayoutVisible);
        }

        outState.putInt(SELECTED_TAB_POSITION_KEY, mThankUTabHelper.getSelectedTabPosition());
    }

    @Override
    public void imageViewClickHandler(int position, ImageView clickedImageView) {
        if (!mTwoPane) {
            // making click effect for the image view
            Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            clickedImageView.startAnimation(animFadeIn);

            // start detail activity with the song video info list and song position
            Intent detailIntent = new Intent(this, SongDetailActivity.class);
            detailIntent.putExtra(SongDetailActivity.EXTRA_SONG_VIDEO_POSITION, position);
            detailIntent.putParcelableArrayListExtra(SongDetailActivity.EXTRA_SONG_VIDEO_INFO_LIST,
                    (ArrayList) mSongVideoInfoList);
            startActivity(detailIntent);
        } else {
            // making sure the song detail layout is visible
            setSongDetailLayoutVisibility(View.VISIBLE);

            mSongVideoPosition = position;
            mSongListFragment.setSelectedItem(position);
            mSongDetailFragment.setSongVideoInfo(mSongVideoInfoList.get(position),
                    IS_VIDEO_FULL_SCREEN_IN_LANDSCAPE);

            if (mTwoPane && mThankUTabHelper.getSelectedTabPosition() == ThankUTabHelper.POSITION_TAB_ALL) {
                // save last seen song video info and the song position and start widget service action if necessary
                SongVideoInfo songVideoInfo = mSongVideoInfoList.get(mSongVideoPosition);
                saveLastSeenSongVideo(songVideoInfo);
            }
        }
    }

    @Override
    public void tabSelectedHandler(TabLayout.Tab tab) {
        if (mTwoPane) {
            // stop and release any youtube player
            mSongDetailFragment.releasePlayer();

            // hide song detail layout
            setSongDetailLayoutVisibility(View.INVISIBLE);
        }


        // we want to get rid from any songs in the recycler view if exist
        mSongListFragment.setSongVideoInfoList(null, null, null);

        // reset mSongVideoPosition to no position
        mSongVideoPosition = RecyclerView.NO_POSITION;

        // making animation for the song list layout when switching between the tabs
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        final FrameLayout songListLayout = findViewById(R.id.song_list_fragment);
        songListLayout.startAnimation(animation);


        int position = tab.getPosition();
        switch (position) {
            case ThankUTabHelper.POSITION_TAB_ALL:
                handleAllTabSelected();
                break;
            case ThankUTabHelper.POSITION_TAB_FAV:
                handleFavTabSelected();
                break;
        }

    }

    private void handleFavTabSelected() {
        List<SongVideoInfo> songVideoInfoList = DbUtils.getAllFavoriteSongs(this);
        if (songVideoInfoList != null && songVideoInfoList.size() > 0) {
            mSongVideoInfoList = songVideoInfoList;
            mSongListFragment.setSongVideoInfoList(mSongVideoInfoList, this,
                    this);
        } else {
            HelperUtils.showSnackbar(this, R.string.no_favorite_songs);
        }
    }

    private void handleAllTabSelected() {
        if (!HelperUtils.isDeviceOnline(this)) {
            HelperUtils.showSnackbar(this, R.string.no_internet_connection);
            return;
        }

        String apiKey = getString(R.string.developer_key);
        // making sure the developer key is set in strings.xml file
        if (apiKey.equals(getString(R.string.placeholder))) {
            Timber.e("ERROR: YOU MUST SET YOUR DEVELOPER KEY IN STRING.XML FILE");
        } else {
            new FetchSongsInfo().execute(apiKey);
        }
    }

    @Override
    public void onSongVideoEnded() {
        if (mSongVideoPosition >= mSongVideoInfoList.size() - 1) {
            HelperUtils.showSnackbar(this, R.string.no_next_song);
            return;
        } else {
            mSongDetailFragment.setSongVideoInfo(mSongVideoInfoList.get(++mSongVideoPosition),
                    IS_VIDEO_FULL_SCREEN_IN_LANDSCAPE);
            mSongListFragment.setSelectedItem(mSongVideoPosition);
        }

        if (mTwoPane && mThankUTabHelper.getSelectedTabPosition() == ThankUTabHelper.POSITION_TAB_ALL) {
            // save last seen song video info and the song position and start widget service action if necessary
            SongVideoInfo songVideoInfo = mSongVideoInfoList.get(mSongVideoPosition);
            saveLastSeenSongVideo(songVideoInfo);
        }
    }

    @Override
    public void imageViewSelectedHandler(ImageView imageView, boolean isSelected) {
        if (!mTwoPane) return;

        if (isSelected)
            imageView.setAlpha(0.7f);
        else
            imageView.setAlpha(1.0f);
    }

    private void saveLastSeenSongVideo(SongVideoInfo songVideoInfo) {
        PreferenceUtils.setLastSeenSongVideo(this, mSongVideoPosition, songVideoInfo);
        if (!DbUtils.hasFavoriteSongs(this))
            ThankUWidgetService.startActionDisplayLastSeenSong(this);
    }


    private class FetchSongsInfo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.loading_indicator_progress_bar).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String apiKey = strings[0];
            try {
                return NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(apiKey));
            } catch (IOException e) {
                Timber.e("Error in getting json response from the building url");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonResponse) {

            findViewById(R.id.loading_indicator_progress_bar).setVisibility(View.INVISIBLE);

            if (jsonResponse == null) return;

            try {
                SongVideoInfo[] songVideoInfoArray = YoutubeApiJsonUtils.getSongVideoInfoArrayFromJson(jsonResponse);

                if (songVideoInfoArray != null && songVideoInfoArray.length > 0) {
                    mSongVideoInfoList = new ArrayList<>();

                    // eliminate private videos from the array
                    for (int i = 0; i < songVideoInfoArray.length; i++) {
                        String videoTitle = songVideoInfoArray[i].getVideoTitle();
                        if (!videoTitle.equalsIgnoreCase(getString(R.string.private_video_title)))
                            mSongVideoInfoList.add(songVideoInfoArray[i]);
                    }

                    mSongListFragment.setSongVideoInfoList(mSongVideoInfoList,
                            MainActivity.this, MainActivity.this);

                    if (mSongVideoPosition >= 0 && mTwoPane) {
                        // == widget case ==
                        mSongListFragment.setSelectedItem(mSongVideoPosition);
                        setSongDetailLayoutVisibility(View.VISIBLE);
                        mSongDetailFragment.setSongVideoInfo(mSongVideoInfoList.get(mSongVideoPosition),
                                IS_VIDEO_FULL_SCREEN_IN_LANDSCAPE);
                    }
                }

            } catch (JSONException e) {
                Timber.e("Error in getting song video info array from json response");
                e.printStackTrace();
            }
        }
    }

}
