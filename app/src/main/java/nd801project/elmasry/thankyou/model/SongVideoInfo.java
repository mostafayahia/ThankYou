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
