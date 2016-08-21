package com.example.hhoo7.popularview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
* 自定义类，继承自ArrayAdapter
* */
public class movieDataAdapter extends ArrayAdapter<MovieData> {
    private static String LOG_TAG = movieDataAdapter.class.getSimpleName();

    public movieDataAdapter(Activity context, ArrayList<MovieData> movieDatas) {
        super(context,0, movieDatas);
    }

    /*
    * 获取数据填充视图
    *
    * @param position：当前的MovieData对象
    * @param converView：当前传入的view
    * @param parent：父容器视图
    * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_item, parent, false);
        }

        //获取当前MovieData对象
        MovieData movieData = getItem(position);
        ImageView posterView = (ImageView) convertView.findViewById(R.id.poster_imageview);
        //调用函数加载图片到视图上
        loadPoster(movieData.getPosterUri(),posterView);

        return convertView;
    }

    //自定义函数，调用第三方库Picasso，用于解析图片，并加载到ImageView中
    private void loadPoster(String posterUri, ImageView currentView) {
        Picasso.with(getContext()).load(posterUri).into(currentView);
    }
}
