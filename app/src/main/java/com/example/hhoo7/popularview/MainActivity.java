package com.example.hhoo7.popularview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity implements MovieFragment.CallBack {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // 在平板模式下识别电影详情布局
    private final String DETAILFRAGMENT_TAG = "DFTAG";

    // 存储当前用户选择的电影排序模式
    public static String mMode;
    // 存储当前用户设备的布局方式
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 识别设备是否响应平板的自适应布局。如果是平板布局，则祭出双布局模式
        if (findViewById(R.id.movie_detail_container) != null) {
            // 将布局方式及时存放到布尔，省去其他地方进行二次判断
            mTwoPane = true;
            // 进行空值检查，如果不为空则调用fragment管理器替换fragment
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取用户设置的电影排序方式
        String mode = Utility.getModeFromPreference(getBaseContext());

        MovieFragment mf;
        // 检查用户设置是否变更
        if (!mode.equals(mMode)) {
            mf = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (mf != null) {
                // 更新视图
                mf.onModeChange();
            }

            // 这个 if 判断是为了响应用户在“我的收藏”的模式下进入电影详情界面，点击收藏后返回主界面之后即时刷新
        } else if (mode.equals(getString(R.string.pref_movieSort_myFavorite))) {
            mf = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            mf.onModeChange();
        }
        mMode = mode;

    }

    /*
    * 无论使用的是两个布局方式中的其中任何一种，总是使用 MainActivity 充当数据传递者
    * 注意这里实现了 Fragment 的 CallBack 用于获取当前点击的电影的Uri
    * */
    @Override
    public void onItemSelected(Uri dateUri, MovieAdapter.MovieViewHolder viewHolder) {
        /*
        * 如果是平板布局，则将数据使用Bundle封装，并直接传递给电影详情界面的 Fragment
        * */
        if (mTwoPane) {
            Bundle argument = new Bundle();
            argument.putParcelable(DetailFragment.DETAIL_URI, dateUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(argument);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            /*
            * 如果是手机布局，则使用intent将数据封装，传递给电影详情界面的 Activity
            * */
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(dateUri);

            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    new Pair<View, String>(viewHolder.mPosterView,getString(R.string.transitionName_poster))
            );
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        }
    }
}
