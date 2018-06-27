package nd801project.elmasry.thankyou.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import nd801project.elmasry.thankyou.ui.MainActivity;
import nd801project.elmasry.thankyou.ui.SongDetailActivity;
import nd801project.elmasry.thankyou.utilities.DbUtils;

/**
 * Implementation of App Widget functionality.
 */
public class ThankYouWidgetProvider extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, SongVideoInfo songVideoInfo, int songVideoPosition) {

        CharSequence songTitle = songVideoInfo.getVideoTitle();

        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);

        PendingIntent pendingIntent;
        if (isTablet) {
            // == tablet case ==
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_SONG_VIDEO_POSITION, songVideoPosition);

            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // == phone case ==
            // the detail activity in this case will display and play **only** the song which is displayed
            // in the app widget
            Intent intent = new Intent(context, SongDetailActivity.class);
            intent.putExtra(SongDetailActivity.EXTRA_SONG_VIDEO_INFO, songVideoInfo);

            // using taskStackBuilder to get the parent activity when back button pressed or navigation up
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(intent);

            pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.thank_you_widget);
        views.setTextViewText(R.id.widget_song_title_text_view, songTitle);
        views.setOnClickPendingIntent(R.id.widget_main_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // according to the value in xml/thank_you_widget_info.xml, the widget will be updated
        // automatically every 24 hour
        if (DbUtils.hasFavoriteSongs(context))
            ThankUWidgetService.startActionDisplayOneOfFavorites(context);
        else
            ThankUWidgetService.startActionDisplayLastSeenSong(context);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds, SongVideoInfo songVideoInfo, int songVideoPosition) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, songVideoInfo, songVideoPosition);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

