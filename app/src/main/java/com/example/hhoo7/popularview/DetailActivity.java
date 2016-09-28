package com.example.hhoo7.popularview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    private String LOG_TAG = DetailFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 将电影 Uri 封装到 Bundle
        Bundle argument = new Bundle();
        argument.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

        // 创建 fragment 实例，并将数据传入
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(argument);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment)
                .commit();

    }

}
