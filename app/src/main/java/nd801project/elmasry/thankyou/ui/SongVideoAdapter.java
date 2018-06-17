package nd801project.elmasry.thankyou.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import timber.log.Timber;

public class SongVideoAdapter extends RecyclerView.Adapter<SongVideoAdapter.SongVideoAdapterViewHolder> {

    private final Callback mCallback;
    private List<SongVideoInfo> mSongVideoInfoList;
    private final Context mContext;

    public SongVideoAdapter(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
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

    interface Callback {
        void songThumbnailClickHandler(int position);
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
            // making click effect for the image view
            Animation animFadein = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
            songVideoImageView.startAnimation(animFadein);

            mCallback.songThumbnailClickHandler(getAdapterPosition());
        }
    }
}
