/*
 * Copyright (C) 2018 Yahia H. El-Tayeb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
