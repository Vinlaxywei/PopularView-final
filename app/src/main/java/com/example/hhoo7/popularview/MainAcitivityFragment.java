package com.example.hhoo7.popularview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Arrays;

/**
 * Created by hhoo7 on 2016/8/6.
 */
public class MainAcitivityFragment extends Fragment {

    //先创建一个私有的Adapter
    private PopularMoviewAdapter moviewAdapter;

    PopularMoview[] popularMoviews = {
            new PopularMoview(R.drawable.im_01),
            new PopularMoview(R.drawable.im_02),
            new PopularMoview(R.drawable.im_03),
            new PopularMoview(R.drawable.im_04)
    };

    //重载函数
    public MainAcitivityFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //调用freament_main作为主视图，（RelativeLayout中包含一个ListView）
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //传递虚拟数组资源popularMoviews到mouviewAdapter适配器
        moviewAdapter = new PopularMoviewAdapter(getActivity(), Arrays.asList(popularMoviews));

        //获取参考资料到 ListView，并为ListView设定适配器，获取填充ListView视图的资源
        ListView listView = (ListView) rootView.findViewById(R.id.listview_moview);
        listView.setAdapter(moviewAdapter);

        return rootView;
    }
}
