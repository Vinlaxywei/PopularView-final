package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieFragment extends Fragment {
    private String LOG_TAG = MovieFragment.class.getSimpleName();
    movieDataAdapter mMovieDataAdapter;

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
        //实例化mArrayAdapter
        mMovieDataAdapter = new movieDataAdapter(getActivity(), new ArrayList<MovieData>());

        /*
        * 调用GridView，绑定适配器
        * */
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        gridView.setAdapter(mMovieDataAdapter);

        /*
        * GridView设置点击监听器，触发后传递解析后的电影相关信息，并调转到电影详情界面
        * */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movieData",mMovieDataAdapter.getItem(i));
//                Log.d(LOG_TAG, "传递数据预览: " + mMovieDataAdapter.getItem(i).toString());
                startActivity(intent);
            }
        });

        return rootView;
    }

    /*
    * 启动时更新视图，不过有时会没有响应，不知道是不是墙的原因
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
    public class RefreshDate extends AsyncTask<Void, Void, String[][]> {
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
        protected String[][] doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //清空存放json的对象，避免数据混淆
            movieJsonStr = null;

            //定义SharePreferences的对象，Call：mPref。用于读取设置选项的数据
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //获取电影清单类型
            mode = mPref.getString(getString(R.string.pref_movieSort_key), getString(R.string.pref_movieSort_defalutValue));
            page = 1;
            //获取电影信息语言
            language = mPref.getString(getString(R.string.pref_language_key), getString(R.string.pref_language_defalutValue));

            try {
//                String baseUrl = "http://api.themoviedb.org/3/movie/popular?api_key="+BuildConfig.TheMovieDb_Key+"&page=2&language=zh";

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
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
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
//            Log.d(LOG_TAG, "Data:+" + movieJsonStr);
            try {
                /*
                * 调用函数解析服务器返回的Json数据，并提取电影信息。
                * @return 返回一个String类型的二维数组，存放有电影信息
                * */
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
        * 自定义函数：用于解析json，提取信息
        * */
        private String[][] getMovieDataFromJson(String movieJsonStr) throws JSONException {
            //提取整个json字符串
            JSONObject movieData = new JSONObject(movieJsonStr);
            //提取json字符串中的列表
            JSONArray resultArray = movieData.getJSONArray("results");

            //定义一个String类型的二维数组，用于存放电影信息
            String[][] resultStrs = new String[resultArray.length()][5];
            //电影海报的尺寸包括w154、w185、w342、w500、w780、original。这里使用适合大多数手机的尺寸：w185
            SharedPreferences posterSizePref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String posterSize = posterSizePref.getString(getString(R.string.pref_posterSize_key),getString(R.string.pref_posterSize_defalutValue));

            //用一个遍历把json列表中的电影数据提取出来，
            for (int i = 0; i < resultArray.length(); i++) {
                //提取列表index
                JSONObject movieDataInfo = resultArray.getJSONObject(i);

                //解析提取电影海报uri
                String posterPath = "http://image.tmdb.org/t/p/w" + posterSize;
                if (movieDataInfo.isNull("poster_path")) {
                    Log.d(LOG_TAG, "poster_path is null");
                }else {
                    posterPath += movieDataInfo.getString("poster_path");
                    resultStrs[i][0] = posterPath;
                }

                //解析提取电影名称
                if (movieDataInfo.isNull("title")) {
                    Log.d(LOG_TAG, "title is null");
                }else {
                    String movieTitle = movieDataInfo.getString("title");
                    resultStrs[i][1] = movieTitle;
                }

                //解析提取电影剧情简介
                if (movieDataInfo.isNull("overview")) {
                    Log.d(LOG_TAG, "overview is null");
                }else {
                    String movieOverView = movieDataInfo.getString("overview");
                    resultStrs[i][2] = movieOverView;
                }

                //解析提取用户评分
                if (movieDataInfo.isNull("vote_average")) {
                    Log.d(LOG_TAG, "vote_average is null");
                }else {
                    String voteAverage = movieDataInfo.getString("vote_average");
                    resultStrs[i][3] = voteAverage;
                }

                //解析提取发布日期
                if (movieDataInfo.isNull("release_date")) {
                    Log.d(LOG_TAG, "release_date is null");
                }else {
                    String releaseDate = movieDataInfo.getString("release_date");
                    resultStrs[i][4] = releaseDate;
                }
            }

            Log.d("提取数据预览", resultStrs[0][0] + " + " + resultStrs[0][1] + "  " + resultStrs[0][3] + "  " + resultStrs[0][4]);
            return resultStrs;
        }

        @Override
        protected void onPostExecute(String[][] resultStrs) {
            //检查数组是否为空
            if (resultStrs != null) {
                //清空数组，避免数据混淆
                mMovieDataAdapter.clear();
                //使用一个遍历将电影信息添加到自定义的适配器，适配器将用其更新视图
                for (int i = 0; i < resultStrs.length; i++) {
                    mMovieDataAdapter.add(new MovieData(
                            resultStrs[i][0],
                            resultStrs[i][1],
                            resultStrs[i][2],
                            resultStrs[i][3],
                            resultStrs[i][4]));
                }
            }
        }
    }
}
