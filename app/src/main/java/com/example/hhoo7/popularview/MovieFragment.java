package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.hhoo7.popularview.data.MovieContract;
import com.example.hhoo7.popularview.data.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieFragment extends Fragment {
    private String LOG_TAG = MovieFragment.class.getSimpleName();
    MovieDbHelper dbHelper;
    SQLiteDatabase db;

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
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra("movieData",mMovieDataAdapter.getItem(i));
//                startActivity(intent);


//            }
//        });

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
            RefreshDate getMovieDate = new RefreshDate();
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
    * 此函数继承自AsyncTask，用于后台发送URL请求，并对返回的json进行解析
    * 将解析后的电影信息储存在一个二维数组
    * */
    public class RefreshDate extends AsyncTask<Void, Void, Void> {
        /*
        * @param movieJsonStr：api提供商服务器返回的json字符串数据
        * @param mode：电影清单类型
        * @param page：电影页数
        * @param language：电影信息的语言
        * */
        private String LOG_TAG = RefreshDate.class.getSimpleName();
        private String movieJsonStr;
        private String mode;
        private int page;
        private String language;


        /*
        * 后台任务：发送URL到Api提供商
        * @return 返回一个String类型的二维数组，存放有电影信息，用于更新视图
        * */
        @Override
        protected Void doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //清空存放json的对象，避免数据混淆
            movieJsonStr = null;

            //获取电影清单类型
            mode = PublicMethod.getModePreference(getActivity());
            page = 1;
            //获取电影信息语言
            language = PublicMethod.getlanguagePreference(getActivity());

            try {
                /*
                * 构建基础URL
                * */
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + mode + "?";
                final String APIKEY_PARAM = "api_key";
                final String PAGE_PARAM = "page";
                final String LANGUAGE_PARAM = "language";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.TheMovieDb_Key)
                        .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                        .appendQueryParameter(LANGUAGE_PARAM, language)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, "发送请求的URL: " + url);

                //发送请求
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                getMovieDataFromJson(movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                e.printStackTrace();
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        /*
        * 自定义函数：用于解析json，提取信息
        * */
        private void getMovieDataFromJson(String movieJsonStr) throws JSONException {
            int insertCount = 0;
            try {

                //提取整个json字符串
                JSONObject movieData = new JSONObject(movieJsonStr);
                //提取json字符串中的列表
                JSONArray resultArray = movieData.getJSONArray("results");

                //定义一个String类型的二维数组，用于存放电影信息
                String[][] resultStrs = new String[resultArray.length()][5];
                //电影海报的尺寸包括w154、w185、w342、w500、w780、original。这里使用适合大多数手机的尺寸：w185
                SharedPreferences posterSizePref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String posterSize = posterSizePref.getString(getString(R.string.pref_posterSize_key), getString(R.string.pref_posterSize_defalutValue));

                //用一个遍历把json列表中的电影数据提取出来，
                for (int i = 0; i < resultArray.length(); i++) {
                    //提取列表index
                    JSONObject movieDataInfo = resultArray.getJSONObject(i);


                    String posterPath = "http://image.tmdb.org/t/p/w" + posterSize;
                    String movieTitle = null;
                    String movieOverView = null;
                    String voteAverage = null;
                    String releaseDate = null;
                    String movieID = null;
                    String popularity = null;

                    //解析提取电影海报uri
                    posterPath += movieDataInfo.getString("poster_path");
                    //解析提取电影名称
                    movieTitle = movieDataInfo.getString("title");
                    //解析提取电影剧情简介
                    movieOverView = movieDataInfo.getString("overview");
                    //解析提取用户评分
                    voteAverage = movieDataInfo.getString("vote_average");
                    //解析提取发布日期
                    releaseDate = movieDataInfo.getString("release_date");
                    //解析提取movieID
                    movieID = movieDataInfo.getString("id");
                    //解析提取电影热度
                    popularity = movieDataInfo.getString("popularity");

                    Cursor checkCursor = db.query(
                            MovieContract.DetailEntry.TABLE_NAME,
                            null,
                            MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ? ",
                            new String[]{movieID},
                            null,
                            null,
                            null
                    );

                    //检查数据库是否有这条数据，没有则插入。
                    if (!checkCursor.moveToFirst()) {
                        /*
                        * 将解析提取出的数据添加进入数据库的指定列表中
                        * */
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, movieTitle);
                        values.put(MovieContract.DetailEntry.COLUMN_POSTER_PATH, posterPath);
                        values.put(MovieContract.DetailEntry.COLUMN_OVER_VIEW, movieOverView);
                        values.put(MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                        values.put(MovieContract.DetailEntry.COLUMN_RELEASE_DATE, releaseDate);
                        values.put(MovieContract.DetailEntry.COLUMN_MOVIE_ID, movieID);
                        values.put(MovieContract.DetailEntry.COLUMN_POPULARITY, popularity);
                        long testRowId = db.insert(MovieContract.DetailEntry.TABLE_NAME, null, values);
                        if (testRowId == -1) {
                            Log.d(LOG_TAG, "error：插入失败，返回：" + testRowId);
                        } else {
                            insertCount++;
                            Log.d("数据库", "插入条目： " + insertCount);
                        }
                    }
                }
            } catch (JSONException E) {
                Log.e(LOG_TAG, E.getMessage(), E);
                E.printStackTrace();
            }
        }

    }
}
