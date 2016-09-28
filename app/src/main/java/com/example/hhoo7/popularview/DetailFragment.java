package com.example.hhoo7.popularview;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hhoo7.popularview.data.DatabaseContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    // 加载器编号
    private static final int DETAIL_LOADER = 1;

    private int dp2, dp8;

    private String shareText;

    // 专用URI，当进行数据传递时，将使用此变量作为key
    static final String DETAIL_URI = "detail_uri";
    private Uri mUri;

    private Boolean isLike;

    public DetailFragment() {
    }

    private ImageView posterImage;
    private TextView movieTitle;
    private TextView overView;
    private TextView voteAverage;
    private TextView releaseDate;
    private TextView runTime;
    private Button likeButton;
    private ImageView likeImage;
    private LinearLayout mLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dp2 = Utility.Dp2Px(getActivity(), 2);
        dp8 = Utility.Dp2Px(getActivity(), 8);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_sharea:
                startActivity(createShareUriIntent());
                return true;

            default:
                return false;

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取传递进来数据
        Bundle argument = getArguments();
        if (argument != null) {
            mUri = argument.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        bindView(rootView);

        /*
        * 收藏按钮设置点击事件监听器。
        * 更新数据库 favorite 信息，调用方法更新视图
        * */
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLike) {
                    ContentValues likeValue = new ContentValues();
                    likeValue.put(DatabaseContract.DetailEntry.COLUMN_FAVORITE, 1);
                    getActivity().getContentResolver().update(mUri, likeValue, null, null);
                    isLike = true;
                    changeLikeState();
                    displaylikeMsg(getString(R.string.toast_display_like_msg), Toast.LENGTH_SHORT);
                } else {
                    ContentValues likeValue = new ContentValues();
                    likeValue.put(DatabaseContract.DetailEntry.COLUMN_FAVORITE, 0);
                    getActivity().getContentResolver().update(mUri, likeValue, null, null);
                    isLike = false;
                    changeLikeState();
                    displaylikeMsg(getString(R.string.toast_display_unlike_msg), Toast.LENGTH_SHORT);
                }
            }
        });

        return rootView;
    }

    private void bindView(View rootView) {
        posterImage = (ImageView) rootView.findViewById(R.id.detail_poster_imageview);
        movieTitle = (TextView) rootView.findViewById(R.id.detail_title_textview);
        overView = (TextView) rootView.findViewById(R.id.detail_overview_textview);
        voteAverage = (TextView) rootView.findViewById(R.id.detail_voteAverager_textview);
        releaseDate = (TextView) rootView.findViewById(R.id.detail_releaseDate_textview);
        runTime = (TextView) rootView.findViewById(R.id.detail_runTime_textview);
        likeButton = (Button) rootView.findViewById(R.id.detail_like_button);
        likeImage = (ImageView) rootView.findViewById(R.id.detail_like_image);
        mLayout = (LinearLayout) rootView.findViewById(R.id.detail_content_layout);
    }

    private Toast toast;

    private void displaylikeMsg(String msg, int msgDuration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(), msg, msgDuration);
        toast.show();
    }

    private void changeLikeState() {
        if (isLike) {
            likeImage.setImageResource(R.drawable.ic_star_black_24dp);
            likeButton.setText(R.string.detail_liked);
        } else {
            likeImage.setImageResource(R.drawable.ic_star_border_black_24dp);
            likeButton.setText(getString(R.string.detail_like));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, MovieFragment.DETAIL_COLUMNS, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            appCompatActivity.setSupportActionBar(toolbar);

            if (!MainActivity.mTwoPane) {
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        /*
        * 查询电影收藏状态并更新视图
        * */
        if (0 == cursor.getInt(MovieFragment.COL_FAVORITE)) {
            isLike = false;
        } else {
            isLike = true;
        }
        changeLikeState();

        // ------------------------------填充电影详情布局-----------------------------------------

        String posterPath = cursor.getString(MovieFragment.COL_POSTER_PATH);
        Utility.loadPicture(getActivity(), posterPath, posterImage);
        movieTitle.setText(cursor.getString(MovieFragment.COL_MOVIE_TITLE));
        overView.setText(cursor.getString(MovieFragment.COL_OVER_VIEW));
        voteAverage.setText(String.format("用户评分：%s", cursor.getString(MovieFragment.COL_VOTE_AVERAGE)));
        releaseDate.setText(String.format("发布日期：%s", cursor.getString(MovieFragment.COL_RELEASE_dATE)));
        runTime.setText(String.format("电影时长：%s min", cursor.getString(MovieFragment.COL_RUNTIME)));

        // ------------------------------填充预告片布局-----------------------------------------

        // 布局 title
        TextView trailerHintText = new TextView(getActivity());
        trailerHintText.setTextSize(24f);
        trailerHintText.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_light));
        trailerHintText.setPadding(0, dp8, 0, dp8);

        Cursor mCursor = getActivity().getContentResolver().query(
                DatabaseContract.TrailerEntry.CONTENT_URI,
                MovieFragment.TRAILERS_COLUMNS,
                DatabaseContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{cursor.getString(MovieFragment.COL_MOVIE_ID)},
                null
        );

        // 布局 content
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                trailerHintText.setText(R.string.detail_fragment_trailerText);
                mLayout.addView(trailerHintText);
                String videoLink;
                shareText = String.format("%s\nhttps://www.youtube.com/watch?v=%s",
                        cursor.getString(MovieFragment.COL_MOVIE_TITLE), mCursor.getString(MovieFragment.COL_VIDEO_LINK));
                do {
                    videoLink = String.format("https://www.youtube.com/watch?v=%s", mCursor.getString(MovieFragment.COL_VIDEO_LINK));
                    mLayout.addView(addTrailerView(mCursor.getString(MovieFragment.COL_VIDEO_TITLE), videoLink));
                } while (mCursor.moveToNext());
            } else {
                trailerHintText.setText(R.string.detail_fragment_notYetTrailer);
                mLayout.addView(trailerHintText);
            }
        }

        // ------------------------------填充评论布局-------------------------------------------

        // 布局 title
        TextView reviewHintText = new TextView(getActivity());
        reviewHintText.setTextSize(24f);
        reviewHintText.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_light));
        reviewHintText.setPadding(0, dp8, 0, dp8);

        mCursor = getActivity().getContentResolver().query(
                DatabaseContract.ReviewEntry.CONTENT_URI,
                MovieFragment.REVIEWS_COLUMNS,
                DatabaseContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{cursor.getString(MovieFragment.COL_MOVIE_ID)},
                null
        );

        // 布局 content
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                reviewHintText.setText(R.string.detail_fragment_reviewText);
                mLayout.addView(reviewHintText);
                do {
                    mLayout.addView(addCommentView(mCursor.getString(MovieFragment.COL_REVIEW_AUTHOR), mCursor.getString(MovieFragment.COL_REVIEW_CONTENT)));
                } while (mCursor.moveToNext());
            } else {
                reviewHintText.setText(R.string.detail_fragment_notYetReview);
                mLayout.addView(reviewHintText);
            }
            mCursor.close();
        }

        cursor.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    // 添加预告片 content 布局
    public CardView addTrailerView(final String title, final String videoLink) {

        /*
        * LinerLayout：横向线性布局，从左到右分别是播放按钮，预告片标题
        * */
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp2, 0, dp2, dp8);
        LinearLayout trailerView = new LinearLayout(getActivity());
        trailerView.setLayoutParams(lp);
        trailerView.setOrientation(LinearLayout.HORIZONTAL);
        trailerView.setPadding(0, dp8, 0, dp8);

        /*
        * 用一个imageview显示播放按钮
        * */
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, dp2));

        /*
        * 用一个textview显示预告片标题
        * */
        TextView textView = new TextView(getActivity());
        textView.setText(title);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, dp8));
        textView.setGravity(Gravity.CENTER);

        /*
        * 将 imageview 和 textview 添加到布局中，并设置点击事件
        * 点击事件：发送intent，打开预告片链接
        * */
        trailerView.addView(imageView);
        trailerView.addView(textView);
        trailerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videointent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));
                if (Utility.checkIntent(getActivity(), videointent)) {
                    startActivity(videointent);
                }
            }
        });

        /*
        * 最后用 cardview 包裹布局
        * */
        CardView cardView = new CardView(getActivity());
        cardView.setLayoutParams(lp);
        cardView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.detail_primary_color));
        cardView.addView(trailerView);

        return cardView;
    }

    // 添加评论 content 布局
    public CardView addCommentView(String author, String content) {
        /*
        * 垂直线性布局：从上到下分别是：评论作者，评论内容
        * */
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout reviewView = new LinearLayout(getActivity());
        lp.setMargins(dp2, 0, dp2, dp8);
        reviewView.setLayoutParams(lp);
        reviewView.setOrientation(LinearLayout.VERTICAL);

        /*
        * comment author
        * */
        TextView authorText = new TextView(getActivity());
        TextPaint tp = authorText.getPaint();
        tp.setFakeBoldText(true);
        authorText.setText(author);
        authorText.setTextSize(20f);
        authorText.setPadding(dp8, dp8, dp8, 0);
        authorText.setLayoutParams(lp);

        /*
        * commment content
        * */
        TextView contentText = new TextView(getActivity());
        contentText.setText(content);
        contentText.setPadding(dp8, 0, dp8, dp8);
        contentText.setLayoutParams(lp);

        /*
        * 将评论作者和评论内容添加到线性布局中
        * */
        reviewView.addView(authorText);
        reviewView.addView(contentText);

        // 最后用 cardview 包裹布局
        CardView cardView = new CardView(getActivity());
        cardView.setLayoutParams(lp);
        cardView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.detail_primary_color));
        cardView.addView(reviewView);

        return cardView;
    }

    private Intent createShareUriIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }

}
