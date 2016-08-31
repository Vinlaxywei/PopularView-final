//package com.example.hhoo7.popularview;
//
//import android.app.Fragment;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//public class DetailActivity extends AppCompatActivity {
//    private String LOG_TAG = DetailFragment.class.getSimpleName();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, new DetailFragment())
//                .commit();
//    }
//
//    /*
//    * 电影详情界面
//    * */
//    public static class DetailFragment extends Fragment {
//
//        /*
//        * 构造函数
//        * */
//        public DetailFragment() {
//        }
//
//        @Nullable
//        @Override
//        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            //定义Intent对象，用于获取传入的数据
//            Intent intent = getActivity().getIntent();
//            MovieData movieData = (MovieData) intent.getSerializableExtra("movieData");
//
//            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
//
//            //调用函数加载图片到视图上
//            ImageView posterImage = (ImageView) rootView.findViewById(R.id.detail_poster_imageview);
//            String posterPath = movieData.getPosterUri();
//            loadPoster(posterPath, posterImage);
//
//            //使用传入的信息加载到 电影名称
//            TextView movieTitle = (TextView) rootView.findViewById(R.id.detail_title_textview);
//            movieTitle.setText(movieData.getMovieTitle());
//
//            //使用传入的信息加载到 剧情简介
//            TextView overView = (TextView) rootView.findViewById(R.id.detail_overview_textview);
//            overView.setText(movieData.getOverView());
//
//            //使用传入的信息加载到 电影评分
//            TextView voteAverage = (TextView) rootView.findViewById(R.id.detail_voteAverager_textview);
//            voteAverage.setText(movieData.getVoteAverage());
//
//            //使用传入的信息加载到 电影发布日期
//            TextView releaseDate = (TextView) rootView.findViewById(R.id.detail_releaseDate_textview);
//            releaseDate.setText(movieData.getReleaseDate());
//
//            LinearLayout mLayout = (LinearLayout) rootView.findViewById(R.id.detail_LL_layout);
//            mLayout.addView(addPreview());
//
//            return rootView;
//        }
//
//        /*
//        * 动态添加一个预告片格局
//        * */
//        public LinearLayout addPreview() {
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            LinearLayout previewLayout = new LinearLayout(getActivity());
//            previewLayout.setLayoutParams(lp);
//            previewLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//            ImageView imageView = new ImageView(getActivity());
//            imageView.setImageResource(R.drawable.ic_play_arrow_black_48dp);
//            imageView.setPadding(0,0,20,0);
//            TextView textView = new TextView(getActivity());
//            textView.setText("askdljaslkdkald");
//            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//            textView.setGravity(Gravity.CENTER);
//
//            previewLayout.addView(imageView);
//            previewLayout.addView(textView);
//
//            return previewLayout;
//        }
//
//        //自定义函数，调用第三方库Picasso，用于解析图片，并加载到ImageView中
//        private void loadPoster(String posterUri, ImageView currentView) {
//            Picasso.with(getActivity().getBaseContext()).load(posterUri)
//                    //如果图片正在下载，将会显示这张图片
//                    .placeholder(R.drawable.im_loading)
//                    //如果图片下载失败，将会显示这张图片
//                    .error(R.drawable.im_error)
//                    .into(currentView);
//        }
//
//    }
//
//}
