package nd801project.elmasry.thankyou.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import nd801project.elmasry.thankyou.model.SongVideoInfo;

public class PreferenceUtils {

    private static final String LAST_SEEN_SONG_VIDEO_POSITION_KEY = "last_seen_song_video_position";

    private static final String LAST_SEEN_SONG_VIDEO_ID_KEY = "last_seen_song_video_id";
    private static final String LAST_SEEN_SONG_VIDEO_THUMBNAIL_KEY = "last_seen_song_video_thumbnail";
    private static final String LAST_SEEN_SONG_VIDEO_TITLE_KEY = "last_seen_song_video_title";

    // the default song video info is the first song in the playlist
    private static final SongVideoInfo DEFAULT_VIDEO_SONG_INFO =
            new SongVideoInfo("eLFW---YSWE",
                    "Maher Zain - Ya Nabi Salam Alayka (International Version) | Official Music Video",
                    "https://i.ytimg.com/vi/eLFW---YSWE/hqdefault.jpg");
    private static final int DEFAULT_SONG_VIDEO_POSITION = 0;

    /**
     * store the last seen song video
     * @param context
     * @param songVideoPosition the position of the song in the list
     * @param songVideoInfo
     */
    public static void setLastSeenSongVideo(Context context, int songVideoPosition, SongVideoInfo songVideoInfo) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putString(LAST_SEEN_SONG_VIDEO_ID_KEY, songVideoInfo.getVideoId());
        editor.putString(LAST_SEEN_SONG_VIDEO_TITLE_KEY, songVideoInfo.getVideoTitle());
        editor.putString(LAST_SEEN_SONG_VIDEO_THUMBNAIL_KEY, songVideoInfo.getVideoThumbnailUrl());

        editor.putInt(LAST_SEEN_SONG_VIDEO_POSITION_KEY, songVideoPosition);

        editor.apply();
    }

    /**
     * get last seen song video info object
     * @param context
     * @return
     */
    public static SongVideoInfo getLastSeenSongVideoInfo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String songVideoId = preferences.getString(LAST_SEEN_SONG_VIDEO_ID_KEY, null);
        String songVideoTitle = preferences.getString(LAST_SEEN_SONG_VIDEO_TITLE_KEY, null);
        String songVideoThumbnail = preferences.getString(LAST_SEEN_SONG_VIDEO_THUMBNAIL_KEY, null);

        if (songVideoId == null) return DEFAULT_VIDEO_SONG_INFO;
        else return new SongVideoInfo(songVideoId, songVideoTitle, songVideoThumbnail);
    }

    /**
     * get last seen song video position in the list
     * @param context
     * @return
     */
    public static int getLastSeenSongVideoPosition(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int position = preferences.getInt(LAST_SEEN_SONG_VIDEO_POSITION_KEY, DEFAULT_SONG_VIDEO_POSITION);

        return position;
    }
}
