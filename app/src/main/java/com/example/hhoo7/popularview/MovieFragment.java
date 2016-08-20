package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mArrayAdapter = new PopularMoviewAdapter(getActivity(), new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        gridView.setAdapter(mArrayAdapter);

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

    public class RefreshDate extends AsyncTask<Void, Void, String[]> {
        private String LOG_TAG = RefreshDate.class.getSimpleName();
        private String movieJsonStr;
        private String mode;
        private int page;
        private String language;

        @Override
        protected String[] doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            movieJsonStr = null;
            mode = "popular";
            page = 1;
            language = "en";

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

        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
            JSONObject movieData = new JSONObject(movieJsonStr);
            JSONArray resultArray = movieData.getJSONArray("results");

            String[] resultStrs = new String[resultArray.length()];

            for (int i = 0; i < resultArray.length(); i++) {
                String posterPath = "http://image.tmdb.org/t/p/w185";
                JSONObject movieDataInfo = resultArray.getJSONObject(i);
                posterPath += movieDataInfo.getString("poster_path");
                resultStrs[i] = posterPath;
            }

//            for (String s : resultStrs) {
//                Log.d(LOG_TAG,"DATA: "+s);
//            }
            Log.d(LOG_TAG, "Poster Uri got it : " + resultStrs[0]);
            return resultStrs;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mArrayAdapter.clear();
                for (String s : strings) {
                    mArrayAdapter.add(s);
                }
            }
        }
    }
}
