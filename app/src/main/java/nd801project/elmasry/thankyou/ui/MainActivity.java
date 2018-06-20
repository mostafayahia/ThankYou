package nd801project.elmasry.thankyou.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import nd801project.elmasry.thankyou.utilities.DbUtils;
import nd801project.elmasry.thankyou.utilities.HelperUtils;
import nd801project.elmasry.thankyou.utilities.NetworkUtils;
import nd801project.elmasry.thankyou.utilities.YoutubeApiJsonUtils;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements SongVideoAdapter.Callback, TabLayout.OnTabSelectedListener {

    private static final String SONG_VIDEO_INFO_ARRAY_KEY = "song_video_info_array";
    private static final String SELECTED_TAB_POSITION_KEY = "selected_tab_position";

    List<SongVideoInfo> mSongVideoInfoList;
    private SongListFragment mSongListFragment;
    private TabLayout mTabLayout;

    private static final int POSITION_ALL_TAB = 0;
    private static final int POSITION_FAV_TAB = 1;
    private static final int TABS_NUM = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        mSongListFragment = (SongListFragment) getSupportFragmentManager().findFragmentById(R.id.song_list_fragment);

        // create the tabs to be displayed
        mTabLayout = findViewById(R.id.tabs);
        TabLayout.Tab[] tabs = new TabLayout.Tab[TABS_NUM];
        TabLayout.Tab allTab = mTabLayout.newTab().setText(R.string.label_all);
        TabLayout.Tab favTab = mTabLayout.newTab().setText(R.string.label_fav);
        tabs[POSITION_ALL_TAB] = allTab;
        tabs[POSITION_FAV_TAB] = favTab;

        // add the tabs to the tab layout and activate the selected tab
        int selectedTabPosition = 0; // default value
        if (savedInstanceState != null) {
            selectedTabPosition = savedInstanceState.getInt(SELECTED_TAB_POSITION_KEY);
        }
        for (int i = 0; i < TABS_NUM; i++) {
            TabLayout.Tab tab = tabs[i];
            mTabLayout.addTab(tab, i == selectedTabPosition);
        }

        mTabLayout.addOnTabSelectedListener(this);

        if (HelperUtils.isDeviceOnline(this)) {
            String apiKey = getString(R.string.developer_key);
            // making sure the developer key is set in strings.xml file
            if (apiKey.equals(getString(R.string.placeholder))) {
                Timber.e("ERROR: YOU MUST SET YOUR DEVELOPER KEY IN STRING.XML FILE");
            } else {
                // retrieve song video info array when rotating the device
                if (savedInstanceState != null && savedInstanceState.containsKey(SONG_VIDEO_INFO_ARRAY_KEY)) {
                    mSongVideoInfoList = savedInstanceState.getParcelableArrayList(SONG_VIDEO_INFO_ARRAY_KEY);
                    mSongListFragment.setSongVideoInfoList(mSongVideoInfoList, this);
                } else {
                    new FetchSongsInfo().execute(apiKey);
                }
            }
        } else {
            // there is no internet connection in this case, so we will notify the user
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSongVideoInfoList != null) {
            outState.putParcelableArrayList(SONG_VIDEO_INFO_ARRAY_KEY, (ArrayList) mSongVideoInfoList);
        }

        outState.putInt(SELECTED_TAB_POSITION_KEY, mTabLayout.getSelectedTabPosition());
    }

    @Override
    public void songThumbnailClickHandler(int position) {
        // start detail activity with the songVideoInfo as extra
        Intent detailIntent = new Intent(this, SongDetailActivity.class);
        detailIntent.putExtra(SongDetailActivity.EXTRA_SONG_VIDEO_POSITION, position);
        detailIntent.putParcelableArrayListExtra(SongDetailActivity.EXTRA_SONG_VIDEO_INFO_LIST,
                (ArrayList) mSongVideoInfoList);
        startActivity(detailIntent);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // making animation for the song list layout when switching between the tabs
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        final FrameLayout songListLayout = findViewById(R.id.song_list_fragment);
        songListLayout.startAnimation(animation);

        int position = tab.getPosition();
        switch (position) {
            case POSITION_ALL_TAB:
                handleAllTabSelected();
                break;
            case POSITION_FAV_TAB:
                handleFavTabSelected();
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void handleFavTabSelected() {
        SongVideoInfo[] songVideoInfoArray = DbUtils.getAllFavoriteSongs(this);
        if (songVideoInfoArray != null && songVideoInfoArray.length > 0) {
            mSongVideoInfoList = new ArrayList<>(Arrays.asList(songVideoInfoArray));
            mSongListFragment.setSongVideoInfoList(mSongVideoInfoList, this);
        } else {
            HelperUtils.showSnackbar(this, R.string.no_favorite_songs);
            mSongListFragment.setSongVideoInfoList(null, null);
        }
    }

    private void handleAllTabSelected() {
        // we want to get rid from any songs in the recycler view if exist
        mSongListFragment.setSongVideoInfoList(null, null);

        String apiKey = getString(R.string.developer_key);
        // making sure the developer key is set in strings.xml file
        if (apiKey.equals(getString(R.string.placeholder))) {
            Timber.e("ERROR: YOU MUST SET YOUR DEVELOPER KEY IN STRING.XML FILE");
        } else {
            new FetchSongsInfo().execute(apiKey);
        }
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

                    mSongListFragment.setSongVideoInfoList(mSongVideoInfoList, MainActivity.this);
                }

            } catch (JSONException e) {
                Timber.e("Error in getting song video info array from json response");
                e.printStackTrace();
            }
        }
    }

}
