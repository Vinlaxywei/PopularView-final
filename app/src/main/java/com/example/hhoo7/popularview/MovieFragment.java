package com.example.hhoo7.popularview;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.hhoo7.popularview.data.MovieContract;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = MovieFragment.class.getSimpleName();
    // 这个Adapter将使用cursor提取数据，并填充到gridview中
    private MovieAdapter mForecastAdapter;
    // Loader专用识别号
    private static final int MOVIEW_LOADER = 0;

    private GridView gridView;

    /*
    * 将表格的列集封装成一个数组，并给出镜像的列号
    * */
    public static final String[] DETAIL_COLUMNS = {
            MovieContract.DetailEntry.COLUMN_MOVIE_ID,
            MovieContract.DetailEntry.COLUMN_MOVIE_TITLE,
            MovieContract.DetailEntry.COLUMN_POSTER_PATH,
            MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.DetailEntry.COLUMN_POPULARITY,
            MovieContract.DetailEntry.COLUMN_OVER_VIEW,
            MovieContract.DetailEntry.COLUMN_RELEASE_DATE,
            MovieContract.DetailEntry.COLUMN_RUNTIME,
            MovieContract.DetailEntry.COLUMN_FAVORITE
    };
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_POSTER_PATH = 2;
    static final int COL_VOTE_AVERAGE = 3;
    static final int COL_POPULARITY = 4;
    static final int COL_OVER_VIEW = 5;
    static final int COL_RELEASE_dATE = 6;
    static final int COL_RUNTIME = 7;
    static final int COL_FAVORITE = 8;

    public static final String[] TRAILERS_COLUMNS = {
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE,
            MovieContract.TrailerEntry.COLUMN_VIDEO_LINK
    };
    //    static final int COL_MOVIE_ID = 0;
    static final int COL_VIDEO_TITLE = 1;
    static final int COL_VIDEO_LINK = 2;

    public static final String[] REVIEWS_COLUMNS = {
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT
    };
    //    static final int COL_MOVIE_ID = 0;
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;

    // 这个变量将用于储存视图恢复时的view的当前位置的key
    private static final String SELETOR_KEY = "key";
    // 这个变量用于存储当前滑动位置，为了防止用户并没有点击任何一部电影，这里默认设置为无效位置
    private int mPosition = GridView.INVALID_POSITION;

    // 这个变量用于存储用户当前的电影排序方式。
    private static String mMode;

    // 构建回调函数，当用户点击时，能够获取当前电影的Uri
    public interface CallBack {
        public void onItemSelected(Uri dateUri);
    }

    /*
    * 构造函数
    * */
    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /*
    * 调用菜单文件
    * */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 刷新按钮：启动AsyncTask，更新数据及视图
            case R.id.action_refresh:
                upData();
                return true;

            // 设置按钮：跳转到设置界面
            case R.id.action_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;

            /*
            * 清理按钮：一键清除所有收藏的电影。
            * */
            case R.id.action_clean:
                /*
                * 首先将所有电影数据写入0值，也就是变成未收藏的状态
                * */
                ContentValues contentValue = new ContentValues();
                contentValue.put(MovieContract.DetailEntry.COLUMN_FAVORITE, 0);
                getActivity().getContentResolver().update(MovieContract.DetailEntry.CONTENT_URI, contentValue, null, null);
                // 弹出Toast提醒，提醒用户相应成功
                Toast.makeText(getActivity(), "所有收藏已清除", Toast.LENGTH_SHORT).show();
                // 刷新视图
                onModeChange();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 实例化Adapter。
        mForecastAdapter = new MovieAdapter(getActivity(), null, 0);

        /*
        * 调用GridView，绑定适配器
        * */
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        gridView.setAdapter(mForecastAdapter);

        /*
        * GridView设置点击监听器，触发后传递解析后的电影相关信息，并调转到电影详情界面
        * */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 获取当前 cursor
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    // 使用回调函数的方法将当前点击的电影的Uri传递出去
                    ((CallBack) getActivity()).onItemSelected((MovieContract.DetailEntry.buildMovieIdUri(
                            cursor.getString(cursor.getColumnIndex(MovieContract.DetailEntry.COLUMN_MOVIE_ID)))));
                }
                // 保存当前位置
                mPosition = i;
            }

        });

        // 检查空值，如果不为空，则滑动到之前的位置
        if (savedInstanceState != null && savedInstanceState.containsKey(SELETOR_KEY)) {
            mPosition = savedInstanceState.getInt(SELETOR_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 当视图切换时，检查用户当前位置，如果位置有效，则将其存储下来
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELETOR_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        /*
        * 判断当前布局模式，如果是平板则设定三列显示模式
        * */
        if (MainActivity.mTwoPane) {
            gridView.setNumColumns(3);
        } else {
            gridView.setNumColumns(2);
        }

        // 进入视图第一时间启动Loader更新
        onModeChange();
        super.onStart();
    }

    /*
    * Activity 重载 onCreate 方法初始化 Loader
    * Fragment 重载 onActivityCreated 方法初始化 Loader
    * */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /*
    * 将更新数据的具体细节封装成一个函数。
    * if判断：调用检查网络的方法，检查设备网络状况，无网络时弹出提示
    * */
    public void upData() {
        if (isOnline()) {
            ReNewData getMovieDate = new ReNewData(getActivity());
            getMovieDate.execute();
        } else {
            Toast.makeText(getActivity(),
                    R.string.toast_display_offOline,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /*
    * @return 设备网络情况
    * */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*
    * 除了onCreate方法之外，这个方法也将初始化的操作封装起来，便于其他地方调用
    * */
    void onModeChange() {
        if (Utility.getModeFromPreference(getActivity()).equals(getString(R.string.pref_movieSort_myFavorite))) {
            getLoaderManager().restartLoader(MOVIEW_LOADER, null, this);
        } else {
            upData();
            getLoaderManager().restartLoader(MOVIEW_LOADER, null, this);
        }

    }

    /*
    * 重写创建Loader的方法
    * */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;

        // switch判断：根据用户选择的电影排序方式，给出相应的加载器
        switch (Utility.getModeFromPreference(getActivity())) {
            case "popular":
                cursorLoader = new CursorLoader(getActivity(), MovieContract.DetailEntry.CONTENT_URI, null, null, null, MovieContract.DetailEntry.COLUMN_POPULARITY + " DESC");
                break;
            case "top_rated":
                cursorLoader = new CursorLoader(getActivity(), MovieContract.DetailEntry.CONTENT_URI, null, null, null, MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE + " DESC");
                break;
            case "myFavorite":
                cursorLoader = new CursorLoader(
                        getActivity(),
                        MovieContract.DetailEntry.CONTENT_URI,
                        null,
                        MovieContract.DetailEntry.COLUMN_FAVORITE + " = ? ",
                        new String[]{String.valueOf(1)},
                        null);
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);

        /*
        * 根据存储的滑动位置，进行相应的调整
        * */
        String mode = Utility.getModeFromPreference(getActivity());
        if (mode.equals(mMode) && mMode != null) {
            if (mPosition != GridView.INVALID_POSITION) {
                gridView.smoothScrollToPosition(mPosition);
            }
        } else if (!mode.equals(mMode)) {
            gridView.smoothScrollToPosition(GridView.SCROLLBAR_POSITION_DEFAULT);
        }
        mMode = mode;

        // 当用户选择“我的收藏”电影排序时，检查当前是否有收藏电影，如果没有则弹出相应提示
        if (!data.moveToFirst() && MainActivity.mMode.equals(getString(R.string.pref_movieSort_myFavorite))) {
            Toast.makeText(getActivity(), "暂无收藏电影", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }
}
