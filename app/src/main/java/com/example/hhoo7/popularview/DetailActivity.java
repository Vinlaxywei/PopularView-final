package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private String LOG_TAG = DetailFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new DetailFragment())
                .commit();
    }

    /*
    * 电影详情界面
    * */
    public static class DetailFragment extends Fragment {

        /*
        * 构造函数
        * */
        public DetailFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //定义Intent对象，用于获取传入的数据
            Intent intent = getActivity().getIntent();

            View rootView = inflater.inflate(R.layout.fragment_detail,container,false);

            //调用函数加载图片到视图上
            ImageView posterImage = (ImageView) rootView.findViewById(R.id.detail_poster_imageview);
            String posterPath = intent.getStringExtra("poster");
            loadPoster(posterPath,posterImage);

            //使用传入的信息加载到 电影名称
            TextView titleText = (TextView) rootView.findViewById(R.id.detail_title_textview);
            titleText.setText(intent.getStringExtra("title"));

            //使用传入的信息加载到 剧情简介
            TextView overViewText = (TextView) rootView.findViewById(R.id.detail_overview_textview);
            overViewText.setText(intent.getStringExtra("overView"));

            //使用传入的信息加载到 电影评分
            TextView voteAverage = (TextView) rootView.findViewById(R.id.detail_voteAverager_textview);
            voteAverage.setText(intent.getStringExtra("voteAverage"));

            //使用传入的信息加载到 电影发布日期
            TextView releaseDate = (TextView) rootView.findViewById(R.id.detail_releaseDate_textview);
            releaseDate.setText(intent.getStringExtra("releaseDate"));

            return rootView;
        }

        //自定义函数，调用第三方库Picasso，用于解析图片，并加载到ImageView中
        private void loadPoster(String posterUri, ImageView currentView) {
            Picasso.with(getActivity().getBaseContext()).load(posterUri).into(currentView);
        }

    }

}
