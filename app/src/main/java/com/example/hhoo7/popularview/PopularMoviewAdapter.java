package com.example.hhoo7.popularview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by hhoo7 on 2016/8/6.
 */
public class PopularMoviewAdapter extends ArrayAdapter<PopularMoview> {
    //创建一个日志标签，标签名称跟随类名
    private static String LOG_TAG = PopularMoviewAdapter.class.getSimpleName();

    public PopularMoviewAdapter(Activity context, List<PopularMoview> popularMoviews) {
        super(context,0,popularMoviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取摆放位置
        PopularMoview popularMoview = getItem(position);

        //如果视图存在则获取并添加到布局文件list_item_moview中
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_moview, parent, false);
        }

        //获取图片资源并填充到布局文件的视图中
        ImageView posterView = (ImageView) convertView.findViewById(R.id.list_item_poster);
        posterView.setImageResource(popularMoview.image);

        return convertView;
    }
}
