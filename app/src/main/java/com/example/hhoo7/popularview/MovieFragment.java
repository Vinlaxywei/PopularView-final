package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.hhoo7.popularview.data.MovieDbHelper;

public class MovieFragment extends Fragment {
    private String LOG_TAG = MovieFragment.class.getSimpleName();
    MovieDbHelper dbHelper;
    static SQLiteDatabase db;

    /*
    * 构造函数
    * */
    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbHelper = new MovieDbHelper(getActivity());
        db = dbHelper.getWritableDatabase();
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

        Cursor mCursor = null;
        //读取用户的偏好选项，获取相应的数据库内容
        switch (PublicMethod.getModePreference(getActivity())) {
            case "popular":
                mCursor = db.query(
                        MovieContract.DetailEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        MovieContract.DetailEntry.COLUMN_POPULARITY + " DESC");
                break;
            case "top_rated":
                mCursor = db.query(
                        MovieContract.DetailEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE + " DESC");
                break;
        }
        //定义一个CursorAdapter的子类ForecastAdapter，将相应的游标传递过去
        ForecastAdapter mForecastAdapter = new ForecastAdapter(getActivity(), mCursor, 0);

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
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra("movieData",mMovieDataAdapter.getItem(i));
//                startActivity(intent);

            }
        });

        return rootView;
    }

    /*
    * 启动时更新视图.
    * */
    @Override
    public void onStart() {
        super.onStart();
        upData();
    }

    /*
    * 将更新数据的具体细节封装成一个函数。
    * if判断：调用检查网络的方法，检查设备网络状况，无网络时弹出提示
    * */
    public void upData() {
        if (isOnline()) {
            RefreshData getMovieDate = new RefreshData(getActivity());
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

}
