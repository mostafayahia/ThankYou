package nd801project.elmasry.thankyou.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import nd801project.elmasry.thankyou.model.SongVideoInfo;
import nd801project.elmasry.thankyou.utilities.DbUtils;
import nd801project.elmasry.thankyou.utilities.PreferenceUtils;

import static nd801project.elmasry.thankyou.data.FavoritesContract.FavoritesEntry.COLUMN_VIDEO_ID;
import static nd801project.elmasry.thankyou.data.FavoritesContract.FavoritesEntry.COLUMN_VIDEO_THUMBNAIL_URL;
import static nd801project.elmasry.thankyou.data.FavoritesContract.FavoritesEntry.COLUMN_VIDEO_TITLE;
import static nd801project.elmasry.thankyou.data.FavoritesContract.FavoritesEntry.CONTENT_URI;

public class ThankUWidgetService extends IntentService {

    private static final String ACTION_DISPLAY_LAST_SEEN_SONG = "nd801project.elmasry.thankyou.action.display_last_seen_song";
    private static final String ACTION_DISPLAY_ONE_OF_FAVORITES = "nd801project.elmasry.thankyou.action_display_one_of_favorites";

    public ThankUWidgetService() {
        super("ThankUWidgetService");
    }

    public static void startActionDisplayLastSeenSong(Context context) {
        Intent intent = new Intent(context, ThankUWidgetService.class);
        intent.setAction(ACTION_DISPLAY_LAST_SEEN_SONG);
        context.startService(intent);
    }

    public static void startActionDisplayOneOfFavorites(Context context) {
        Intent intent = new Intent(context, ThankUWidgetService.class);
        intent.setAction(ACTION_DISPLAY_ONE_OF_FAVORITES);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();

        if (action.equals(ACTION_DISPLAY_LAST_SEEN_SONG)) {
            handleActionDisplayLastSeenSong();
        } else if (action.equals(ACTION_DISPLAY_ONE_OF_FAVORITES)) {
            handleActionDisplayOneOfFavorites();
        } else {
            throw new RuntimeException("unknown action: " + action);
        }
    }


    private void handleActionDisplayLastSeenSong() {
        int songVideoPosition = PreferenceUtils.getLastSeenSongVideoPosition(this);
        SongVideoInfo songVideoInfo = PreferenceUtils.getLastSeenSongVideoInfo(this);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ThankYouWidgetProvider.class));
        ThankYouWidgetProvider.updateAppWidgets(this, appWidgetManager, appWidgetIds, songVideoInfo, songVideoPosition);
    }

    private void handleActionDisplayOneOfFavorites() {

        if (!DbUtils.hasFavoriteSongs(this)) return;

        Cursor data = getContentResolver().query(CONTENT_URI,
                null, null, null, null);


        // we will choose random song to display in the widget
        final int totRows = data.getCount();

        int randomRowPosition = (int) (System.currentTimeMillis() % totRows);
        data.moveToPosition(randomRowPosition);

        final int videoIdIndex = data.getColumnIndex(COLUMN_VIDEO_ID);
        final int videoTitleIndex = data.getColumnIndex(COLUMN_VIDEO_TITLE);
        final int videoThumbnailUrlIndex = data.getColumnIndex(COLUMN_VIDEO_THUMBNAIL_URL);

        String videoId = data.getString(videoIdIndex);
        String videoTitle = data.getString(videoTitleIndex);
        String videoThumbnailUrl = data.getString(videoThumbnailUrlIndex);

        data.close();

        SongVideoInfo songVideoInfo = new SongVideoInfo(videoId, videoTitle, videoThumbnailUrl);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ThankYouWidgetProvider.class));
        ThankYouWidgetProvider.updateAppWidgets(this, appWidgetManager, appWidgetIds, songVideoInfo, randomRowPosition);

    }

}

