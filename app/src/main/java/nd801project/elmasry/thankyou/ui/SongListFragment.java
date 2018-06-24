package nd801project.elmasry.thankyou.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;
import timber.log.Timber;

public class SongListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SongVideoAdapter mSongVideoAdapter;
    private int mSelectedItemPos = RecyclerView.NO_POSITION;

    private static final String SELECTED_ITEM_POSITION_KEY = "selected_item_position";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

        // setting properties for the RecyclerView
        mRecyclerView = rootView.findViewById(R.id.song_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        // restoring the state after rotating the device
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_ITEM_POSITION_KEY))
                mSelectedItemPos = savedInstanceState.getInt(SELECTED_ITEM_POSITION_KEY);

            // using "post()" method here to get smooth scroll to selected item
            // and get the effect to the selected item too.
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    setSelectedItem(mSelectedItemPos);
                }
            });
        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mSelectedItemPos >= 0) {
            outState.putInt(SELECTED_ITEM_POSITION_KEY, mSelectedItemPos);
        }
    }

    public void setSongVideoInfoList(List<SongVideoInfo> songVideoInfoList,
                                     SongVideoAdapter.ImageViewClickCallback imageViewClickCallback,
                                     SongVideoAdapter.ImageViewSelectedCallback imageViewSelectedCallback) {

        mSongVideoAdapter = new SongVideoAdapter(getActivity(),
                imageViewClickCallback, imageViewSelectedCallback);
        mRecyclerView.setAdapter(mSongVideoAdapter);
        mSongVideoAdapter.setSongVideoInfoList(songVideoInfoList);
    }

    public void setSelectedItem(int position) {
        if (position < 0) {
            Timber.e("position can't be negative: " + position);
            return;
        }

        if (mSongVideoAdapter == null || mRecyclerView == null) {
            Timber.e("You must set properties' values first by calling setSongVideoInfoList()");
            return;
        }

        mSelectedItemPos = position;
        mSongVideoAdapter.setSelectedItem(position);
        mRecyclerView.smoothScrollToPosition(position);
    }

}
