package nd801project.elmasry.thankyou.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import timber.log.Timber;

public class NetworkUtils {

    public static URL buildUrl(String apiKey) {

        if (apiKey == null)
            throw new IllegalArgumentException("api key can't be null");

        final String BASE_URL = "https://www.googleapis.com/youtube/v3/playlistItems";

        final String PART_PARAM = "part";
        final String MAX_RESULTS_PARAM = "maxResults";
        final String PLAYLIST_ID_PARAM = "playlistId";
        final String API_KEY_PARAM = "key";

        final String part = "snippet";
        final int maxResults = 50;
        final String playlistId = "PLoagsPg26SY7YdytkX5rJhX3aFmbvaZRM";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PART_PARAM, part)
                .appendQueryParameter(MAX_RESULTS_PARAM, String.valueOf(maxResults))
                .appendQueryParameter(PLAYLIST_ID_PARAM, playlistId)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Timber.e("Error in building url using api key");
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }
}
