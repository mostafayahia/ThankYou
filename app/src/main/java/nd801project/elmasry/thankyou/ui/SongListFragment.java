package nd801project.elmasry.thankyou.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nd801project.elmasry.thankyou.R;
import nd801project.elmasry.thankyou.model.SongVideoInfo;

public class SongListFragment extends Fragment {

    RecyclerView mRecyclerView;
    SongVideoAdapter mSongVideoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

        // setting properties for the RecyclerView
        mSongVideoAdapter = new SongVideoAdapter(getContext());
        mRecyclerView = rootView.findViewById(R.id.song_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mSongVideoAdapter);

        return rootView;
    }

    public void setSongVideoInfoArray(SongVideoInfo[] songVideoInfoArray) {
        mSongVideoAdapter.setSongVideoInfoArray(songVideoInfoArray);
    }

}
