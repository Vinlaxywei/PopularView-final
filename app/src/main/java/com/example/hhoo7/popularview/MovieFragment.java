package com.example.hhoo7.popularview;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private MovieAdapter mForecastAdapter;
    private static final int MOVIEW_LOADER = 0;

    public static final String[] DETAIL_COLUMN = {
            MovieContract.DetailEntry.COLUMN_MOVIE_ID,
            MovieContract.DetailEntry.COLUMN_MOVIE_TITLE,
            MovieContract.DetailEntry.COLUMN_POSTER_PATH,
            MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.DetailEntry.COLUMN_POPULARITY,
            MovieContract.DetailEntry.COLUMN_OVER_VIEW,
            MovieContract.DetailEntry.COLUMN_RELEASE_DATE,
    };
//    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_POSTER_PATH = 2;
    static final int COL_VOTE_AVERAGE = 3;
    static final int COL_POPULARITY = 4;
    static final int COL_OVER_VIEW = 5;
    static final int COL_RELEASE_dATE = 6;

    public static final String[] TRAILERS_COLUMN = {
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE,
            MovieContract.TrailerEntry.COLUMN_VIDEO_LINK,
    };
    static final int COL_MOVIE_ID = 0;
    static final int COL_VIDEO_TITLE = 1;
    static final int COL_VIDEO_LINK = 2;

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

    /*
    * 设置菜单选项的触发事件
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //刷新按钮：启动AsyncTask，更新数据及视图
            case R.id.action_refresh:
                upData();
                return true;

            //设置按钮：跳转到设置界面
            case R.id.action_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //定义一个CursorAdapter的子类ForecastAdapter，将相应的游标传递过去
        mForecastAdapter = new MovieAdapter(getActivity(), null, 0);

        /*
        * 调用GridView，绑定适配器
        * */
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        gridView.setAdapter(mForecastAdapter);

        /*
        * GridView设置点击监听器，触发后传递解析后的电影相关信息，并调转到电影详情界面
        * */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.setData(MovieContract.DetailEntry.buildMovieIdUri(
                        cursor.getString(cursor.getColumnIndex(MovieContract.DetailEntry.COLUMN_MOVIE_ID))));
                startActivity(intent);

            }
        });

        return rootView;
    }

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

    void onModeChange() {
        upData();
        getLoaderManager().restartLoader(MOVIEW_LOADER,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;

        switch (Utility.getModeFromPreference(getActivity())) {
            case "popular":
                cursorLoader = new CursorLoader(getActivity(),MovieContract.DetailEntry.CONTENT_URI,null,null,null,MovieContract.DetailEntry.COLUMN_POPULARITY + " DESC");
                break;
            case "top_rated":
                cursorLoader = new CursorLoader(getActivity(),MovieContract.DetailEntry.CONTENT_URI,null,null,null,MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE + " DESC");
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }
}
