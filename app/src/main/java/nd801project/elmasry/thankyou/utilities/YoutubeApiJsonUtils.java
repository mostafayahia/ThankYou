package nd801project.elmasry.thankyou.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nd801project.elmasry.thankyou.model.SongVideoInfo;

public class YoutubeApiJsonUtils {

    public static SongVideoInfo[] getSongVideoInfoArrayFromJson(String jsonResponseString) throws JSONException {

        final String YOUTUBE_ITEMS = "items";
        final String YOUTUBE_SNIPPET = "snippet";
        final String YOUTUBE_THUMBNAILS = "thumbnails";
        final String YOUTUBE_THUMBNAIL_HIGH = "high";
        final String YOUTUBE_RESOURCE_ID = "resourceId";
        final String YOUTUBE_VIDEO_ID = "videoId";
        final String YOUTUBE_VIDEO_TITLE = "title";
        final String YOUTUBE_THUMBNAIL_URL = "url";

        final JSONArray itemsJsonArray = new JSONObject(jsonResponseString).getJSONArray(YOUTUBE_ITEMS);

        final int SONGS_NUM = itemsJsonArray.length();

        SongVideoInfo[] songVideoInfoArray = new SongVideoInfo[SONGS_NUM];
        for (int i = 0; i < SONGS_NUM; i++) {
            JSONObject snippetJsonObject = itemsJsonArray.getJSONObject(i).getJSONObject(YOUTUBE_SNIPPET);
            String videoTitle = snippetJsonObject.optString(YOUTUBE_VIDEO_TITLE);

            // Note: there are no thumbnails for the private videos
            JSONObject thumbnailsJsonObject = snippetJsonObject.optJSONObject(YOUTUBE_THUMBNAILS);
            String videoThumbnailUrl = null; // default value is null
            if (thumbnailsJsonObject != null) {
                JSONObject thumbnailJsonObject = thumbnailsJsonObject.optJSONObject(YOUTUBE_THUMBNAIL_HIGH);
                if (thumbnailJsonObject != null)
                    videoThumbnailUrl = thumbnailJsonObject.optString(YOUTUBE_THUMBNAIL_URL);
            }

            String videoId = snippetJsonObject.getJSONObject(YOUTUBE_RESOURCE_ID)
                    .optString(YOUTUBE_VIDEO_ID);

            songVideoInfoArray[i] = new SongVideoInfo(videoId, videoTitle, videoThumbnailUrl);
        }

        return songVideoInfoArray;
    }
}
