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

package nd801project.elmasry.thankyou.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import timber.log.Timber;

public class SongVideoAdapter extends RecyclerView.Adapter<SongVideoAdapter.SongVideoAdapterViewHolder> {

    private final ImageViewClickCallback mImageViewClickCallback;
    private final ImageViewSelectedCallback mImageViewSelectedCallback;
    private List<SongVideoInfo> mSongVideoInfoList;
    private final Context mContext;
    private int mSelectedPos = RecyclerView.NO_POSITION;

    public SongVideoAdapter(Context context, ImageViewClickCallback imageViewClickCallback,
                            ImageViewSelectedCallback imageViewSelectedCallback) {
        mContext = context;
        mImageViewClickCallback = imageViewClickCallback;
        mImageViewSelectedCallback = imageViewSelectedCallback;
    }

    @NonNull
    @Override
    public SongVideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.song_video_list_item, parent, false);
        return new SongVideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongVideoAdapterViewHolder holder, int position) {
        String thumbnailUrl = mSongVideoInfoList.get(position).getVideoThumbnailUrl();

        if (TextUtils.isEmpty(thumbnailUrl)) {
            Timber.e("thumbnail url is empty or null");
            Picasso.with(mContext)
                    .load(R.drawable.no_thumbnail)
                    .into(holder.songVideoImageView);
        } else {
            Picasso.with(mContext)
                    .load(thumbnailUrl)
                    .error(R.drawable.no_thumbnail)
                    .into(holder.songVideoImageView);
        }

        mImageViewSelectedCallback.imageViewSelectedHandler(holder.songVideoImageView,
                position == mSelectedPos);

    }

    @Override
    public int getItemCount() {
        if (mSongVideoInfoList == null) return 0;
        return mSongVideoInfoList.size();
    }

    public void setSongVideoInfoList(List<SongVideoInfo> songVideoInfoList) {
        mSongVideoInfoList = songVideoInfoList;
        notifyDataSetChanged();
    }

    public void setSelectedItem(int position) {
        notifyItemChanged(mSelectedPos);
        mSelectedPos = position;
        notifyItemChanged(mSelectedPos);
    }

    interface ImageViewClickCallback {
        void imageViewClickHandler(int position, ImageView clickedImageView);
    }

    interface ImageViewSelectedCallback {
        void imageViewSelectedHandler(ImageView imageView, boolean isSelected);
    }


    class SongVideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView songVideoImageView;

        public SongVideoAdapterViewHolder(View itemView) {
            super(itemView);
            songVideoImageView = itemView.findViewById(R.id.song_video_image_view);
            songVideoImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mImageViewClickCallback == null) return;
            mImageViewClickCallback.imageViewClickHandler(getAdapterPosition(), songVideoImageView);
        }
    }
}
