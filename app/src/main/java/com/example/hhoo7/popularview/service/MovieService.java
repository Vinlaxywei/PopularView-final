package com.example.hhoo7.popularview.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.hhoo7.popularview.BuildConfig;
import com.example.hhoo7.popularview.Utility;
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


public class MovieService extends IntentService {
    private String LOG_TAG = MovieService.class.getSimpleName();

    public MovieService() {
        super("MovieService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent: ");
        String mode = Utility.getModeFromPreference(this);

        if (!mode.equals("myFavorite")) {
            //构建URI
            Uri detailDataUri = buildUri(mode, "1","en");
            //调用方法发送API请求、解析JSON数据、并将其写入detail数据库
            String movieDetailJsonStr = sendRequest(detailDataUri);
            if (movieDetailJsonStr != null) {
                try {
                    getMovieDetailDataFromJson(movieDetailJsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy: Stop Service");
        super.onDestroy();

    }

    private String sendRequest(Uri uri) {
        String jsonStr = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(uri.toString());
//            Log.d(LOG_TAG, "发送请求的URL: " + url);

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

    /*
    * 此方法接收一个查询参数，用其构建Uri，并返回构建完成的Uri
    * */
    private Uri buildUri(String queryParams, String page,String language) {
        //URI示例：http://api.themoviedb.org/3/movie/{movie_id}?api_key={api_key}&append_to_response=trailers,reviews
        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + queryParams + "?";
        final String APIKEY_PARAM = "api_key";
        final String ADDITION_PARAM = "append_to_response";
        final String PAGE_PARAM = "page";
        final String LANGUAGE_PARAM = "language";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_OPEN_MOVIE_DB_API_KEY)
                .appendQueryParameter(ADDITION_PARAM, "trailers,reviews")
                .appendQueryParameter(PAGE_PARAM, page)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .build();

        return builtUri;
    }

    /*
    * 解析json，提取电影信息，并将其写入数据库
    * */
    private void getMovieDetailDataFromJson(String movieJsonStr) throws JSONException {
        try {
            //提取整个json字符串
            JSONObject movieData = new JSONObject(movieJsonStr);
            //提取json字符串中的列表
            JSONArray resultArray = movieData.getJSONArray("results");

            //电影海报的尺寸包括w154、w185、w342、w500、w780、original。这里默认使用适合大多数手机的尺寸：w185
            String posterSize = Utility.getPosterSizePreference(this);

            //用一个遍历把json列表中的电影数据提取出来，
            for (int i = 0; i < resultArray.length(); i++) {
                //提取列表index
                JSONObject movieDataInfo = resultArray.getJSONObject(i);

                String posterPath = "http://image.tmdb.org/t/p/w" + posterSize;
                String movieTitle;
                String movieOverView;
                String voteAverage;
                String releaseDate;
                String movieID;
                String popularity;

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

                Cursor checkCursor = this.getContentResolver().query(
                        MovieContract.DetailEntry.buildMovieIdUri(movieID),
                        null,
                        null,
                        null,
                        null
                );

                //检查数据库是否有这条数据，有的话则更新一部分信息，没有的话则写入新的信息。
                if (checkCursor.moveToFirst()) {

                    ContentValues values = new ContentValues();
                    values.put(MovieContract.DetailEntry.COLUMN_POSTER_PATH, posterPath);
                    values.put(MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                    values.put(MovieContract.DetailEntry.COLUMN_POPULARITY, popularity);
                    this.getContentResolver().update(
                            MovieContract.DetailEntry.CONTENT_URI,
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
                    values.put(MovieContract.DetailEntry.COLUMN_FAVORITE, 0);
                    this.getContentResolver().insert(MovieContract.DetailEntry.CONTENT_URI, values);

                }
                checkCursor.close();
            }

            /*
            * 使用cursor遍历movieid，调用getTrailerAndReviewFromJson方法进行单部电影的数据写入
            * */
            Cursor cursor = this.getContentResolver().query(
                    MovieContract.DetailEntry.CONTENT_URI,
                    new String[]{MovieContract.DetailEntry.COLUMN_MOVIE_ID},
                    null,
                    null,
                    MovieContract.DetailEntry.COLUMN_POPULARITY + " DESC"
            );
            if (cursor.moveToFirst()) {
                do {
                    getTrailerAndReviewFromJson(cursor.getString(cursor.getColumnIndex(MovieContract.DetailEntry.COLUMN_MOVIE_ID)));
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (JSONException E) {
            Log.e(LOG_TAG, E.getMessage(), E);
            E.printStackTrace();
        }

    }

    /*
    * 解析Json，写入预告片信息和评论信息
    * */
    private void getTrailerAndReviewFromJson(String movieId) throws JSONException {
        // 将传入的movieId作为基础查询参数，发送Uri请求并获取返回的字符串
        String jsonStr = sendRequest(buildUri(movieId, "1","en"));

        // 提取Json中的有用信息，并更新数据库
        try {
            JSONObject jsonData = new JSONObject(jsonStr);
            JSONObject trailers = jsonData.getJSONObject("trailers");
            JSONArray youtubeKeys = trailers.getJSONArray("youtube");

            JSONObject reviews = jsonData.getJSONObject("reviews");
            JSONArray results = reviews.getJSONArray("results");

            /*
            * 更新电影详情中的电影时长信息
            * */
            String runtiem = jsonData.getString("runtime");
            ContentValues detailValue = new ContentValues();
            detailValue.put(MovieContract.DetailEntry.COLUMN_RUNTIME, runtiem);
            this.getContentResolver().update(
                    MovieContract.DetailEntry.CONTENT_URI,
                    detailValue,
                    MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ? ",
                    new String[]{movieId}
            );

            // 更新电影预告片数据
            for (int i = 0; i < youtubeKeys.length(); i++) {
                JSONObject youtubeInfo = youtubeKeys.getJSONObject(i);

                String VideoTitle = youtubeInfo.getString("name");
                String VideoLink = youtubeInfo.getString("source");

                ContentValues trailerValue = new ContentValues();
                trailerValue.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                trailerValue.put(MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE, VideoTitle);
                trailerValue.put(MovieContract.TrailerEntry.COLUMN_VIDEO_LINK, VideoLink);

                Cursor checkCursor = this.getContentResolver().query(
                        MovieContract.TrailerEntry.CONTENT_URI,
                        null,
                        MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE + " = ? and " + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{VideoTitle, movieId},
                        null);

                if (!checkCursor.moveToFirst()) {
                    this.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, trailerValue);
                }
                checkCursor.close();
            }

            // 更新电影评论数据
            for (int i = 0; i < results.length(); i++) {
                JSONObject reviewInfo = results.getJSONObject(i);

                String reviewAuthor = reviewInfo.getString("author");
                String reviewContent = reviewInfo.getString("content");

                ContentValues reviewValue = new ContentValues();
                reviewValue.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                reviewValue.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, reviewAuthor);
                reviewValue.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, reviewContent);

                Cursor checkCursor = this.getContentResolver().query(
                        MovieContract.ReviewEntry.CONTENT_URI,
                        null,
                        MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR + " = ? and " + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{reviewAuthor, movieId},
                        null);

                if (!checkCursor.moveToFirst()) {
                    this.getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, reviewValue);
                }
                checkCursor.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class alarm extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 在启动服务方和被启动服务之前创建一个广播接收器屏障
            Intent sendIntent = new Intent(context,MovieService.class);
            context.startService(sendIntent);
        }
    }
}
