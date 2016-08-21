package com.example.hhoo7.popularview;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            String posterPath = intent.getStringExtra("poster");

            View rootView = inflater.inflate(R.layout.fragment_detail,container,false);
            ImageView posterImage = (ImageView) rootView.findViewById(R.id.detail_poster_imageview);
            loadPoster(posterPath,posterImage);

            TextView titleText = (TextView) rootView.findViewById(R.id.detail_title_textview);
            titleText.setText(intent.getStringExtra("title"));
            TextView overViewText = (TextView) rootView.findViewById(R.id.detail_overview_textview);
            overViewText.setText(intent.getStringExtra("overView"));
            TextView voteAverage = (TextView) rootView.findViewById(R.id.detail_voteAverager_textview);
            voteAverage.setText(intent.getStringExtra("voteAverage"));
            TextView releaseDate = (TextView) rootView.findViewById(R.id.detail_releaseDate_textview);
            releaseDate.setText(intent.getStringExtra("releaseDate"));

            return rootView;
        }

        private void loadPoster(String posterUri, ImageView currentView) {
            Picasso.with(getActivity().getBaseContext()).load(posterUri).into(currentView);
        }

    }

}
