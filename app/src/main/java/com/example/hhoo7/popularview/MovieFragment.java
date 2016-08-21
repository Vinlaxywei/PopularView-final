package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
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
    PopularMoviewAdapter mArrayAdapter;

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
                return true;
            case R.id.action_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mArrayAdapter = new PopularMoviewAdapter(getActivity(), new ArrayList<MovieData>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        gridView.setAdapter(mArrayAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("poster", mArrayAdapter.getItem(i).getPosterUri());
                intent.putExtra("title", mArrayAdapter.getItem(i).getMovieTitle());
                intent.putExtra("overView", mArrayAdapter.getItem(i).getOverView());
                intent.putExtra("voteAverage", mArrayAdapter.getItem(i).getVoteAverage());
                intent.putExtra("releaseDate", mArrayAdapter.getItem(i).getReleaseDate());
                Log.d(LOG_TAG, "pu data: " + mArrayAdapter.getItem(i).toString());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        upData();
    }

    public void upData() {
        RefreshDate getMovieDate = new RefreshDate();
        getMovieDate.execute();
    }

    public class RefreshDate extends AsyncTask<Void, Void, String[][]> {
        private String LOG_TAG = RefreshDate.class.getSimpleName();
        private String movieJsonStr;
        private String mode;
        private int page;
        private String language;

        @Override
        protected String[][] doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            movieJsonStr = null;

            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mode = mPref.getString(getString(R.string.pref_movieSort_key), getString(R.string.pref_movieSort_defalutValue));
            page = 1;
            language = mPref.getString(getString(R.string.pref_language_key), getString(R.string.pref_language_defalutValue));

            try {
//                String baseUrl = "http://api.themoviedb.org/3/movie/popular?api_key="+BuildConfig.TheMovieDb_Key+"&page=2&language=zh";

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
                Log.d(LOG_TAG, "URL: " + url);

                // Create the request to the movie db, and open the connection
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
//            Log.d(LOG_TAG, "data:+" + movieJsonStr);
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String[][] getMovieDataFromJson(String movieJsonStr) throws JSONException {
            JSONObject movieData = new JSONObject(movieJsonStr);
            JSONArray resultArray = movieData.getJSONArray("results");

            String[][] resultStrs = new String[resultArray.length()][5];
            final String posterSize = "185";

            for (int i = 0; i < resultArray.length(); i++) {
                String posterPath = "http://image.tmdb.org/t/p/w" + posterSize;
                JSONObject movieDataInfo = resultArray.getJSONObject(i);
                posterPath += movieDataInfo.getString("poster_path");
                resultStrs[i][0] = posterPath;

                //解析电影名称
                String movieTitle = movieDataInfo.getString("title");
//                Log.d(LOG_TAG, "电影名称title：" + movieTitle);
                resultStrs[i][1] = movieTitle;

                //解析电影剧情简介
                String movieOverView = movieDataInfo.getString("overview");
//                Log.d(LOG_TAG, "电影剧情简介" +movieOverView);
                resultStrs[i][2] = movieOverView;

                //解析用户评分
                String voteAverage = movieDataInfo.getString("vote_average");
//                Log.d(LOG_TAG, "电影剧情简介" +voteAverage);
                resultStrs[i][3] = voteAverage;

                //解析发布日期
                String releaseDate = movieDataInfo.getString("release_date");
//                Log.d(LOG_TAG, "电影剧情简介" +releaseDate);
                resultStrs[i][4] = releaseDate;
            }

            Log.d("提取数据预览", resultStrs[0][0] + " + " + resultStrs[0][1] + "剧情简介" +resultStrs[0][3]+resultStrs[0][4]);
            return resultStrs;
        }

        @Override
        protected void onPostExecute(String[][] resultStrs) {
            if (resultStrs != null) {
                mArrayAdapter.clear();
                for (int i = 0; i < resultStrs.length; i++) {
                    mArrayAdapter.add(new MovieData(
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
