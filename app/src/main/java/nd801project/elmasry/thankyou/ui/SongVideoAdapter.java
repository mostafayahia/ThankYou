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

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import timber.log.Timber;

public class SongVideoAdapter extends RecyclerView.Adapter<SongVideoAdapter.SongVideoAdapterViewHolder> {

    private SongVideoInfo[] mSongVideoInfoArray;
    private final Context mContext;

    public SongVideoAdapter(Context context) {
        mContext = context;
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
        String thumbnailUrl = mSongVideoInfoArray[position].getVideoThumbnailUrl();

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
        if (mSongVideoInfoArray == null) return 0;
        return mSongVideoInfoArray.length;
    }

    public void setSongVideoInfoArray(SongVideoInfo[] songVideoInfoArray) {
        mSongVideoInfoArray = songVideoInfoArray;
        notifyDataSetChanged();
    }

    class SongVideoAdapterViewHolder extends RecyclerView.ViewHolder {

        public final ImageView songVideoImageView;

        public SongVideoAdapterViewHolder(View itemView) {
            super(itemView);
            songVideoImageView = itemView.findViewById(R.id.song_video_image_view);
        }
    }
}
