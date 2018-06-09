package nd801project.elmasry.thankyou.ui;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;

import java.io.IOException;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import nd801project.elmasry.thankyou.utilities.HelperUtils;
import nd801project.elmasry.thankyou.utilities.NetworkUtils;
import nd801project.elmasry.thankyou.utilities.YoutubeApiJsonUtils;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String SONG_VIDEO_INFO_ARRAY_KEY = "song_video_info_array";
    SongVideoInfo[] mSongVideoInfoArray;
    private SongListFragment mSongListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        mSongListFragment = (SongListFragment) getSupportFragmentManager().findFragmentById(R.id.song_list_fragment);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_all));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_fav));

        if (HelperUtils.isDeviceOnline(this)) {
            String apiKey = getString(R.string.developer_key);
            // making sure the developer key is set in strings.xml file
            if (apiKey.equals(getString(R.string.placeholder))) {
                Timber.e("ERROR: YOU MUST SET YOUR DEVELOPER KEY IN STRING.XML FILE");
            } else {
                // retrieve song video info array when rotating the device
                if (savedInstanceState != null && savedInstanceState.containsKey(SONG_VIDEO_INFO_ARRAY_KEY)) {
                    mSongVideoInfoArray = (SongVideoInfo[]) savedInstanceState.getParcelableArray(SONG_VIDEO_INFO_ARRAY_KEY);
                    mSongListFragment.setSongVideoInfoArray(mSongVideoInfoArray);
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
        if (mSongVideoInfoArray != null) {
            outState.putParcelableArray(SONG_VIDEO_INFO_ARRAY_KEY, mSongVideoInfoArray);
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
                mSongVideoInfoArray = YoutubeApiJsonUtils.getSongVideoInfoArrayFromJson(jsonResponse);
                mSongListFragment.setSongVideoInfoArray(mSongVideoInfoArray);
            } catch (JSONException e) {
                Timber.e("Error in getting song video info array from json response");
                e.printStackTrace();
            }
        }
    }

}
