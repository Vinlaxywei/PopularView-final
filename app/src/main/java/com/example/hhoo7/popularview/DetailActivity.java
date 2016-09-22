package com.example.hhoo7.popularview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public class DetailActivity extends AppCompatActivity {
    private String LOG_TAG = DetailFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 将电影 Uri 数据封装到 Bundle
        Bundle argument = new Bundle();
        argument.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

        // 创建 fragment 实例，并将数据传入
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(argument);

        //使用片段管理器替换视图
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment)
                .commit();

    }

    private float downY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        float minScroll = 100;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = y;
                break;
            case MotionEvent.ACTION_UP:
                float scrollY = y - downY;
                if (scrollY > 0 && Math.abs(scrollY) > minScroll+200) {
                    getSupportActionBar().show();
                } else if (scrollY < 0 && Math.abs(scrollY) > minScroll){
                    getSupportActionBar().hide();
                }

        }

        return super.dispatchTouchEvent(ev);
    }
}
