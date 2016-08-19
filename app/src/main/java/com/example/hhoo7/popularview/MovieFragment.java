package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

public class MovieFragment extends Fragment {

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RefreshDate getMovieDate = new RefreshDate();
        getMovieDate.execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ArrayList<PopularMovie> mArrayList = new ArrayList<PopularMovie>();
        mArrayList.add(new PopularMovie(R.drawable.im_01));
        mArrayList.add(new PopularMovie(R.drawable.im_02));
        mArrayList.add(new PopularMovie(R.drawable.im_03));
        mArrayList.add(new PopularMovie(R.drawable.im_04));

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        PopularMoviewAdapter mPopularMoviewAdapter = new PopularMoviewAdapter(getActivity(), mArrayList);

        GridView gridView = (GridView) rootView.findViewById(R.id.moview_gridview);
        gridView.setAdapter(mPopularMoviewAdapter);

        return rootView;
    }
}
