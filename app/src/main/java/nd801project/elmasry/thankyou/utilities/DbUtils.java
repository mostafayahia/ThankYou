package nd801project.elmasry.thankyou.utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;

import static nd801project.elmasry.thankyou.data.FavoritesContract.FavoritesEntry.*;


public class DbUtils {

    /**
     * return true if the song stored in favorites
     * @param activity
     * @param videoId
     * @return
     */
    public static  boolean isSongInFavorites(Activity activity, String videoId) {
        if (TextUtils.isEmpty(videoId)) return false;

        Cursor cursor = activity.getContentResolver().query(CONTENT_URI, new String[]{"_id"},
                COLUMN_VIDEO_ID + "=?", new String[]{videoId}, null);

        boolean inFavorites = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();

        return inFavorites;
    }

    /**
     * return true if insertion was successful
     * @param activity
     * @param songVideoInfo
     * @return
     */
    public static boolean insertInFavorites(Activity activity, SongVideoInfo songVideoInfo) {
        if (songVideoInfo == null) return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_VIDEO_ID, songVideoInfo.getVideoId());
        contentValues.put(COLUMN_VIDEO_TITLE, songVideoInfo.getVideoTitle());
        contentValues.put(COLUMN_VIDEO_THUMBNAIL_URL, songVideoInfo.getVideoThumbnailUrl());

        Uri uri = activity.getContentResolver().insert(CONTENT_URI, contentValues);

        return uri != null;
    }

    /**
     * return true if the deletion was successful
     * @param activity
     * @param videoId
     * @return
     */
    public static boolean deleteFromFavorites(Activity activity, String videoId) {
        if (TextUtils.isEmpty(videoId)) return false;

        int rowDeleted = activity.getContentResolver().delete(CONTENT_URI, COLUMN_VIDEO_ID + "=?",
                new String[]{videoId});

        return rowDeleted > 0;
    }

    /**
     * return an array of songVideoInfo of all favorites which are stored in the database
     * @param activity
     * @return
     */
    public static SongVideoInfo[] getAllFavoriteSongs(Activity activity) {
        Cursor cursor = activity.getContentResolver().query(CONTENT_URI, null,
                null, null, null);

        SongVideoInfo[] songVideoInfoArray = null;
        if (cursor != null && cursor.getCount() > 0) {
            final int favoriteSongsNum = cursor.getCount();
             songVideoInfoArray = new SongVideoInfo[favoriteSongsNum];

            final int videoIdIndex = cursor.getColumnIndex(COLUMN_VIDEO_ID);
            final int videoTitleIndex = cursor.getColumnIndex(COLUMN_VIDEO_TITLE);
            final int videoThumbnailUrlIndex = cursor.getColumnIndex(COLUMN_VIDEO_THUMBNAIL_URL);

            while (cursor.moveToNext()) {
                String videoId = cursor.getString(videoIdIndex);
                String videoTitle = cursor.getString(videoTitleIndex);
                String videoThumbnailUrl = cursor.getString(videoThumbnailUrlIndex);

                songVideoInfoArray[cursor.getPosition()] = new SongVideoInfo(videoId, videoTitle, videoThumbnailUrl);
            }
        }

        if (cursor != null) cursor.close();

        return songVideoInfoArray;
    }
}
