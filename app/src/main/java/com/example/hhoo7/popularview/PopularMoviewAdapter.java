package com.example.hhoo7.popularview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/*
* 自定义类，继承自ArrayAdapter
* 用于加载PopularMovie类到GridView中
* */
public class PopularMoviewAdapter extends ArrayAdapter<PopularMovie> {
    private static String LOG_TAG = PopularMoviewAdapter.class.getSimpleName();

    public PopularMoviewAdapter(Activity context, ArrayList<PopularMovie> popularMovies) {
        super(context,0, popularMovies);
    }

    /*
    * 获取数据填充视图
    *
    * @param position：当前的PopularMovie类
    * @param converView：当前传入的view
    * @param parent：父容器视图
    * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_item, parent, false);
        }

        PopularMovie popularMovie = getItem(position);

        ImageView posterView = (ImageView) convertView.findViewById(R.id.poster_item);
        posterView.setImageResource(popularMovie.getImage());

        return convertView;
    }
}
