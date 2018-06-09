package nd801project.elmasry.thankyou.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SongVideoInfo implements Parcelable {
    private String videoId;
    private String videoTitle;
    private String videoThumbnailUrl;

    public SongVideoInfo(String videoId, String videoTitle, String videoThumbnailUrl) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoThumbnailUrl = videoThumbnailUrl;
    }

    protected SongVideoInfo(Parcel in) {
        videoId = in.readString();
        videoTitle = in.readString();
        videoThumbnailUrl = in.readString();
    }

    public static final Creator<SongVideoInfo> CREATOR = new Creator<SongVideoInfo>() {
        @Override
        public SongVideoInfo createFromParcel(Parcel in) {
            return new SongVideoInfo(in);
        }

        @Override
        public SongVideoInfo[] newArray(int size) {
            return new SongVideoInfo[size];
        }
    };

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public void setVideoThumbnailUrl(String videoThumbnailUrl) {
        this.videoThumbnailUrl = videoThumbnailUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(videoId);
        parcel.writeString(videoTitle);
        parcel.writeString(videoThumbnailUrl);
    }
}
