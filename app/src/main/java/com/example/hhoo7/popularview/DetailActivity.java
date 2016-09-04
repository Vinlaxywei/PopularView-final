package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hhoo7.popularview.data.MovieContract;

public class DetailActivity extends AppCompatActivity {
    private String LOG_TAG = DetailFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new DetailFragment())
                .commit();
    }

    /*
    * 电影详情界面
    * */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final int DETAIL_LOADER = 1;

        /*
        * 构造函数
        * */
        public DetailFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        /*
        * 动态添加一个预告片格局
        * */
        public LinearLayout addTrailerView(final String title, final String videoLink) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout TrailerView = new LinearLayout(getActivity());
            TrailerView.setLayoutParams(lp);
            TrailerView.setOrientation(LinearLayout.HORIZONTAL);
            TrailerView.setPadding(0, 20, 0, 0);

            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
            imageView.setPadding(50, 0, 0, 0);
            TextView textView = new TextView(getActivity());
            textView.setText(title);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
//            textView.setGravity(Gravity.LEFT);

            TrailerView.addView(imageView);
            TrailerView.addView(textView);
            TrailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));
                    startActivity(intent);
                }
            });

            return TrailerView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            return new CursorLoader(getActivity(), intent.getData(), MovieFragment.DETAIL_COLUMN, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (!cursor.moveToFirst()) {
                return;
            }

            ImageView posterImage = (ImageView) getView().findViewById(R.id.detail_poster_imageview);
            String posterPath = cursor.getString(MovieFragment.COL_POSTER_PATH);
            Utility.loadPicture(getActivity(), posterPath, posterImage);

            TextView movieTitle = (TextView) getView().findViewById(R.id.detail_title_textview);
            movieTitle.setText(cursor.getString(MovieFragment.COL_MOVIE_TITLE));

            TextView overView = (TextView) getView().findViewById(R.id.detail_overview_textview);
            overView.setText(cursor.getString(MovieFragment.COL_OVER_VIEW));

            TextView voteAverage = (TextView) getView().findViewById(R.id.detail_voteAverager_textview);
            voteAverage.setText(cursor.getString(MovieFragment.COL_VOTE_AVERAGE));

            TextView releaseDate = (TextView) getView().findViewById(R.id.detail_releaseDate_textview);
            releaseDate.setText(cursor.getString(MovieFragment.COL_RELEASE_dATE));

            Cursor mCursor = getActivity().getContentResolver().query(
                    MovieContract.TrailerEntry.CONTENT_URI,
                    MovieFragment.TRAILERS_COLUMN,
                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                    new String[]{cursor.getString(MovieFragment.COL_MOVIE_ID)},
                    null
            );

            if (mCursor.moveToFirst()) {
                String videoLink = null;
                do {
                    LinearLayout mLayout = (LinearLayout) getView().findViewById(R.id.detail_LL_layout);
                    videoLink = "https://www.youtube.com/watch?v=" + mCursor.getString(MovieFragment.COL_VIDEO_LINK);
                    mLayout.addView(addTrailerView(mCursor.getString(MovieFragment.COL_VIDEO_TITLE),videoLink));
                } while (mCursor.moveToNext());
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
