package com.example.hhoo7.popularview;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.hhoo7.popularview.data.DatabaseContract;
import com.example.hhoo7.popularview.service.MovieService;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private String LOG_TAG = MovieFragment.class.getSimpleName();

    private MovieAdapter mForecastAdapter;
    private RecyclerView mRecyclerView;

    private static final int MOVIE_LOADER = 0;

    /*
    * 将表格的列集封装成一个数组
    * */
    public static final String[] DETAIL_COLUMNS = {
            DatabaseContract.DetailEntry.COLUMN_MOVIE_ID,
            DatabaseContract.DetailEntry.COLUMN_MOVIE_TITLE,
            DatabaseContract.DetailEntry.COLUMN_POSTER_PATH,
            DatabaseContract.DetailEntry.COLUMN_VOTE_AVERAGE,
            DatabaseContract.DetailEntry.COLUMN_POPULARITY,
            DatabaseContract.DetailEntry.COLUMN_OVER_VIEW,
            DatabaseContract.DetailEntry.COLUMN_RELEASE_DATE,
            DatabaseContract.DetailEntry.COLUMN_RUNTIME,
            DatabaseContract.DetailEntry.COLUMN_FAVORITE
    };

    // 给出镜像列的列号
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_POSTER_PATH = 2;
    static final int COL_VOTE_AVERAGE = 3;
    static final int COL_POPULARITY = 4;
    static final int COL_OVER_VIEW = 5;
    static final int COL_RELEASE_dATE = 6;
    static final int COL_RUNTIME = 7;
    static final int COL_FAVORITE = 8;

    // 预告片表格镜像
    public static final String[] TRAILERS_COLUMNS = {
            DatabaseContract.TrailerEntry.COLUMN_MOVIE_ID,
            DatabaseContract.TrailerEntry.COLUMN_VIDEO_TITLE,
            DatabaseContract.TrailerEntry.COLUMN_VIDEO_LINK
    };
    //    static final int COL_MOVIE_ID = 0;
    static final int COL_VIDEO_TITLE = 1;
    static final int COL_VIDEO_LINK = 2;

    // 评论表格镜像
    public static final String[] REVIEWS_COLUMNS = {
            DatabaseContract.ReviewEntry.COLUMN_MOVIE_ID,
            DatabaseContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            DatabaseContract.ReviewEntry.COLUMN_REVIEW_CONTENT
    };
    //    static final int COL_MOVIE_ID = 0;
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;

    private static final String SELETOR_KEY = "key";
    private int mPosition = RecyclerView.NO_POSITION;

    // 这个变量用于存储用户当前的电影排序方式。
    private static String mMode;

    // 构建回调函数，当用户点击时，能够传递当前电影的Uri
    public interface CallBack {
        void onItemSelected(Uri dateUri, MovieAdapter.MovieViewHolder viewHolder);
    }

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                upData();
                onModeChange();
                return true;

            case R.id.action_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;

            case R.id.action_clean:
                // 首先将所有电影数据写入0值，也就是变成未收藏的状态
                ContentValues contentValue = new ContentValues();
                contentValue.put(DatabaseContract.DetailEntry.COLUMN_FAVORITE, 0);
                getActivity().getContentResolver().update(DatabaseContract.DetailEntry.CONTENT_URI, contentValue, null, null);

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
        mForecastAdapter = new MovieAdapter(getActivity(), new MovieAdapter.AdapterOnClickHandler() {
            @Override
            public void onClickHandler(String movieId, MovieAdapter.MovieViewHolder viewHolder) {
                ((CallBack) getActivity()).onItemSelected((DatabaseContract.DetailEntry.buildMovieIdUri(
                        movieId)),viewHolder);

                // 保存当前位置
                mPosition = viewHolder.getAdapterPosition();
            }
        });

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_gridview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mForecastAdapter);

        // 检查空值，如果不为空，则滑动到之前的位置
        if (savedInstanceState != null && savedInstanceState.containsKey(SELETOR_KEY)) {
            mPosition = savedInstanceState.getInt(SELETOR_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 当视图切换时，检查用户当前位置，如果位置有效，则将其存储下来
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELETOR_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    /*
    * Activity 重载 onCreate 方法初始化 Loader
    * Fragment 重载 onActivityCreated 方法初始化 Loader
    * */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        upData();
    }

    private void upData() {
        if (Utility.isOnline(getActivity())) {
            Intent intent = new Intent(getContext(), MovieService.class);
            getContext().startService(intent);
        } else {
            Toast.makeText(getActivity(),
                    R.string.toast_display_offLine,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    void onModeChange() {
        if (Utility.getModeFromPreference(getActivity()).equals(getString(R.string.pref_movieSort_myFavorite))) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        } else {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;

        // switch判断：根据用户选择的电影排序方式，返回对应的 loader
        switch (Utility.getModeFromPreference(getActivity())) {
            case "popular":
                cursorLoader = new CursorLoader(getActivity(), DatabaseContract.DetailEntry.CONTENT_URI, null, null, null, DatabaseContract.DetailEntry.COLUMN_POPULARITY + " DESC");
                break;
            case "top_rated":
                cursorLoader = new CursorLoader(getActivity(), DatabaseContract.DetailEntry.CONTENT_URI, null, null, null, DatabaseContract.DetailEntry.COLUMN_VOTE_AVERAGE + " DESC");
                break;
            case "myFavorite":
                cursorLoader = new CursorLoader(
                        getActivity(),
                        DatabaseContract.DetailEntry.CONTENT_URI,
                        DETAIL_COLUMNS,
                        DatabaseContract.DetailEntry.COLUMN_FAVORITE + " = ? ",
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
                mRecyclerView.smoothScrollToPosition(mPosition);
            }
        } else if (!mode.equals(mMode)) {
            mRecyclerView.smoothScrollToPosition(RecyclerView.SCROLLBAR_POSITION_DEFAULT);
        }
        mMode = mode;

        // 当用户选择“我的收藏”电影排序时，检查当前是否有收藏电影，如果没有则弹出相应提示
        if (!data.moveToFirst() && MainActivity.mMode.equals(getString(R.string.pref_movieSort_myFavorite))) {
            Toast.makeText(getActivity(), R.string.toast_display_noFavorite, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }
}
