package com.example.hhoo7.popularview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.hhoo7.popularview.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
    * 此函数继承自AsyncTask，用于后台发送URL请求，并对返回的json进行解析
    * 将解析后的电影信息储存在一个二维数组
    * */
public class RefreshData extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private String LOG_TAG = RefreshData.class.getSimpleName();
    private String mode;

    public RefreshData(Context context) {
        mContext = context;
    }

    /*
    * 后台任务：发送URL到Api提供商
    * @return 返回一个String类型的二维数组，存放有电影信息，用于更新视图
    * */
    @Override
    protected Void doInBackground(Void... voids) {
        //第一波发起请求，获取所有电影详情信息等
        //获取电影清单类型
        mode = PublicMethod.getModePreference(mContext);
        //构建URI
        Uri detailDataUri = getUri(mode);
        //调用方法发送API请求、解析JSON数据、并将其写入detail数据库
        String movieDetailJsonStr = sendRequest(detailDataUri);
        if (movieDetailJsonStr != null) {
            try {
                getDetailDataFromJson(movieDetailJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String sendRequest(Uri uri) {
        String jsonStr = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(uri.toString());
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
            jsonStr = buffer.toString();
            return jsonStr;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();
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
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private Uri getUri(String query) {
        //URI示例：http://api.themoviedb.org/3/movie/297761?api_key={api_key}&append_to_response=trailers,reviews
        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + query + "?";
        final String APIKEY_PARAM = "api_key";
        final String ADDITION_PARAM = "append_to_response";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, "d72d827b84fe4d0a366b749494e78e3f")
                .appendQueryParameter(ADDITION_PARAM, "trailers,reviews")
                .build();

        return builtUri;
    }

    /*
    * 自定义函数：用于解析json，提取信息
    * */
    private void getDetailDataFromJson(String movieJsonStr) throws JSONException {
        int insertCount = 0;
        try {
            //提取整个json字符串
            JSONObject movieData = new JSONObject(movieJsonStr);
            //提取json字符串中的列表
            JSONArray resultArray = movieData.getJSONArray("results");

            //电影海报的尺寸包括w154、w185、w342、w500、w780、original。这里默认使用适合大多数手机的尺寸：w185
            String posterSize = PublicMethod.getPosterSizePreference(mContext);

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

                Cursor checkCursor = MovieFragment.db.query(
                        MovieContract.DetailEntry.TABLE_NAME,
                        null,
                        MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movieID},
                        null,
                        null,
                        null
                );

                //检查数据库是否有这条数据，没有则插入。
                if (checkCursor.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.DetailEntry.COLUMN_POSTER_PATH, posterPath);
                    values.put(MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                    values.put(MovieContract.DetailEntry.COLUMN_POPULARITY, popularity);
                    MovieFragment.db.update(
                            MovieContract.DetailEntry.TABLE_NAME,
                            values,
                            MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{movieID});
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, movieTitle);
                    values.put(MovieContract.DetailEntry.COLUMN_POSTER_PATH, posterPath);
                    values.put(MovieContract.DetailEntry.COLUMN_OVER_VIEW, movieOverView);
                    values.put(MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                    values.put(MovieContract.DetailEntry.COLUMN_RELEASE_DATE, releaseDate);
                    values.put(MovieContract.DetailEntry.COLUMN_MOVIE_ID, movieID);
                    values.put(MovieContract.DetailEntry.COLUMN_POPULARITY, popularity);
                    values.put(MovieContract.DetailEntry.COLUMN_POPULARITY, popularity);
                    MovieFragment.db.insert(MovieContract.DetailEntry.TABLE_NAME, null, values);
                    insertCount++;
                }
            }
            Log.d(LOG_TAG, MovieContract.DetailEntry.TABLE_NAME + " 表格新增：" + insertCount + " 条信息");
        } catch (JSONException E) {
            Log.e(LOG_TAG, E.getMessage(), E);
            E.printStackTrace();
        }

    }

//    public long getTrailerDataFromJson(String jsonStr) {
//        long rowId = 0;
//        int insertCount = 0;
//
//        try {
//            //提取整个json字符串
//            JSONObject trailerData = new JSONObject(jsonStr);
//            JSONObject trailers = trailerData.getJSONObject("trailers");
//            JSONArray youtubeResultArray = trailers.getJSONArray("youtube");
//
//            //用一个遍历把json列表中的电影数据提取出来，
//            for (int i = 0; i < youtubeResultArray.length(); i++) {
//                //提取列表index
//                JSONObject keyDataInfo = youtubeResultArray.getJSONObject(i);
//
//                //创建字符串数组，作为存放key的数组
//                String[] keys = new String[youtubeResultArray.length()];
//
//                //解析提取youtube key
//                keys[i] = keyDataInfo.getString("source");
//                insertCount++;
//
//                Cursor cursor = MovieFragment.db.query(
//                        MovieContract.TrailerEntry.TABLE_NAME,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null
//                );
//                Log.d(LOG_TAG, MovieContract.TrailerEntry.TABLE_NAME + " 表格新增：" + keys[i] + " 信息");
//            }
//
//        } catch (JSONException E) {
//            Log.e(LOG_TAG, E.getMessage(), E);
//            E.printStackTrace();
//        }finally {
//            if (rowId == -1) {
//                Log.d(LOG_TAG, "error: the insert is missing");
//            }
//        }
//        return rowId;
//    }

}