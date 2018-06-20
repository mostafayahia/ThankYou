package nd801project.elmasry.thankyou.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    public static final String AUTHORITY = "nd801project.elmasry.thankyou";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String FAVORITES_PATH = "favorites";

    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITES_PATH).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_VIDEO_ID = "videoId";
        public static final String COLUMN_VIDEO_TITLE = "videoTitle";
        public static final String COLUMN_VIDEO_THUMBNAIL_URL = "videoThumbnailUrl";
    }

}
