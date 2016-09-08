package com.example.hhoo7.popularview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    private String LOG_TAG = DetailFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 将电影 Uri 数据封装到 Bundle
        Bundle argument = new Bundle();
        argument.putParcelable(DetailFragment.DETAIL_URI,getIntent().getData());

        // 创建 fragment 实例，并将数据传入
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(argument);

        //使用片段管理器替换视图
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment)
                .commit();

        //在电影详情界面的左上角添加返回按钮，在这里象征意义大于实际意义
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
