package com.example.hhoo7.popularview.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by hhoo7 on 2016/8/29.
 */
public class TestUtil extends AndroidTestCase {

    private static final String LOG_TAG = "test";

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        //将原始资料用valueSet方法以一套键值的方式提取出来，然后赋值到另一个set对象上
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        //遍历set对象，检查添加到数据库的资料是否和原始资料有出入
        for (Map.Entry<String, Object> entry : valueSet) {
            //获取列名
            String columnName = entry.getKey();
            System.out.print("lieming+"+columnName);
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public void testjson() {
        String locationQuery = "94043";

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        String format = "json";
        String units = "metric";
        int numDays = 14;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String FORECAST_BASE_URL =
                    "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APPID_PARAM = "APPID";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, locationQuery)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARAM, "2251f8129f2bd0066d38b1b7b5401aa9")
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
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
                return;
            }
            forecastJsonStr = buffer.toString();
            assertFalse(forecastJsonStr, 5 == 5);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
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

    public static ContentValues createTrailerTableTestValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, 331412);
        contentValues.put(MovieContract.TrailerEntry.COLUMN_VIDEO_LINK, "HTTP://YOUTUBE 01");
        contentValues.put(MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE, "JUNGLE BOOK");
        return contentValues;
    }

    public static ContentValues createDetailTableTestValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, "SUPERMAN");
        contentValues.put(MovieContract.DetailEntry.COLUMN_POSTER_PATH, "posterpath");
        contentValues.put(MovieContract.DetailEntry.COLUMN_OVER_VIEW, "over view");
        contentValues.put(MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE, 7.2);
        contentValues.put(MovieContract.DetailEntry.COLUMN_RELEASE_DATE, "2015-02-03");
        contentValues.put(MovieContract.DetailEntry.COLUMN_MOVIE_ID, 1233123);
        contentValues.put(MovieContract.DetailEntry.COLUMN_POPULARITY, 30.222);
        contentValues.put(MovieContract.DetailEntry.COLUMN_FAVORITE, 0);
        return contentValues;
    }

}
