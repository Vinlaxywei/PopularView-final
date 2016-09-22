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
import android.support.v7.widget.CardView;
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

import com.example.hhoo7.popularview.data.MovieContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    // 专用加载器编号
    private static final int DETAIL_LOADER = 1;

    private int dp2, dp8;

    // 成员变量：分享分本
    private String shareText;

    // 专用URI，当进行数据传递时，将使用此变量作为key
    static final String DETAIL_URI = "detail_uri";
    private Uri mUri;

    // 将当前电影的收藏状态存储下来
    private Boolean isLike;

    /*
    * 构造函数
    * */
    public DetailFragment() {
    }

    static class ViewHolder {
        ImageView posterImage;
        TextView movieTitle;
        TextView overView;
        TextView voteAverage;
        TextView releaseDate;
        TextView runTime;
        Button likeButton;
        ImageView likeImage;
        TextView trailerHint;
        LinearLayout mLayout;

        ViewHolder(View view) {
            posterImage = (ImageView) view.findViewById(R.id.detail_poster_imageview);
            movieTitle = (TextView) view.findViewById(R.id.detail_title_textview);
            overView = (TextView) view.findViewById(R.id.detail_overview_textview);
            voteAverage = (TextView) view.findViewById(R.id.detail_voteAverager_textview);
            releaseDate = (TextView) view.findViewById(R.id.detail_releaseDate_textview);
            runTime = (TextView) view.findViewById(R.id.detail_runTime_textview);
            trailerHint = (TextView) view.findViewById(R.id.detail_fragment_trailerText);
            likeButton = (Button) view.findViewById(R.id.detail_like_button);
            likeImage = (ImageView) view.findViewById(R.id.detail_like_image);
            mLayout = (LinearLayout) view.findViewById(R.id.detail_LL_layout);
        }
    }

    ViewHolder detailViewHolder;

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
            //分享按钮，调用方法创建分享文本，然后启动Intent
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
            // 从数据中提取Uri
            mUri = argument.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        detailViewHolder = new ViewHolder(rootView);
        rootView.setTag(detailViewHolder);

        /*
        * 实例化一个cursor对象，用于查询当前电影收藏状态，并根据查询状态调用方法更新相关视图
        * */
        Cursor cursor = null;
        if (mUri != null) {
            cursor = getActivity().getContentResolver().query(mUri, MovieFragment.DETAIL_COLUMNS, null, null, null);
        }
        if (cursor != null) {
            cursor.moveToFirst();
            if (0 == cursor.getInt(MovieFragment.COL_FAVORITE)) {
                isLike = false;
            } else {
                isLike = true;
            }
            changeLikeState();
            cursor.close();
        }

        /*
        * 收藏按钮设置点击事件监听器。
        * 更新数据库 favorite 信息，调用方法更新视图
        * */
        detailViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLike) {
                    ContentValues likeValue = new ContentValues();
                    likeValue.put(MovieContract.DetailEntry.COLUMN_FAVORITE, 1);
                    getActivity().getContentResolver().update(mUri, likeValue, null, null);
                    isLike = true;
                    changeLikeState();
                    displaylikeMsg(getString(R.string.toast_display_like_msg), Toast.LENGTH_LONG);
                } else {
                    ContentValues likeValue = new ContentValues();
                    likeValue.put(MovieContract.DetailEntry.COLUMN_FAVORITE, 0);
                    getActivity().getContentResolver().update(mUri, likeValue, null, null);
                    isLike = false;
                    changeLikeState();
                    displaylikeMsg(getString(R.string.toast_display_unlike_msg), Toast.LENGTH_SHORT);
                }
            }
        });

        return rootView;
    }

    private Toast toast;

    private void displaylikeMsg(String likeMsg, int msgDuration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(), likeMsg, msgDuration);
        toast.show();
    }

    private void changeLikeState() {
        if (isLike) {
            detailViewHolder.likeImage.setImageResource(R.drawable.ic_star_black_24dp);
            detailViewHolder.likeButton.setText(R.string.detail_liked);
        } else {
            detailViewHolder.likeImage.setImageResource(R.drawable.ic_star_border_black_24dp);
            detailViewHolder.likeButton.setText(getString(R.string.detail_like));
        }
    }

    /*
    * 动态添加一个预告片格局
    * */
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
        * 使用一个cardview将线性布局包裹起来，给出 material design 的效果
        * */
        CardView cardView = new CardView(getActivity());
        cardView.setLayoutParams(lp);
        cardView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.mLayout_light));
        cardView.addView(trailerView);

        return cardView;
    }

    /*
    * 动态添加一个评论布局
    * */
    public CardView addReviewView(String author, String content) {
        /*
        * 竖向线性布局：从上到下分别是：评论作者，评论内容
        * */
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout reviewView = new LinearLayout(getActivity());
        lp.setMargins(dp2, 0, dp2, dp8);
        reviewView.setLayoutParams(lp);
        reviewView.setOrientation(LinearLayout.VERTICAL);

        /*
        * 评论作者的的布局
        * */
        TextView authorText = new TextView(getActivity());
        TextPaint tp = authorText.getPaint();
        tp.setFakeBoldText(true);
        authorText.setText(author);
        authorText.setTextSize(20f);
        authorText.setPadding(dp8, dp8, dp8, 0);
        authorText.setLayoutParams(lp);

        /*
        * 评论内容的布局
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

        /*
        * 使用一个cardview将线性布局包裹起来，给出 material design 的效果
        * */
        CardView cardView = new CardView(getActivity());
        cardView.setLayoutParams(lp);
        cardView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.mLayout_light));
        cardView.addView(reviewView);

        return cardView;
    }

    /*
    * 初始化加载器
    * */
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

    /*
    * 填充布局
    * */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        /*
        * 填充电影详情布局
        * */
        String posterPath = cursor.getString(MovieFragment.COL_POSTER_PATH);
        Utility.loadPicture(getActivity(), posterPath, detailViewHolder.posterImage);
        detailViewHolder.movieTitle.setText(cursor.getString(MovieFragment.COL_MOVIE_TITLE));
        detailViewHolder.overView.setText(cursor.getString(MovieFragment.COL_OVER_VIEW));
        detailViewHolder.voteAverage.setText(String.format("用户评分：%s", cursor.getString(MovieFragment.COL_VOTE_AVERAGE)));
        detailViewHolder.releaseDate.setText(String.format("发布日期：%s", cursor.getString(MovieFragment.COL_RELEASE_dATE)));
        detailViewHolder.runTime.setText(String.format("电影时长：%s min", cursor.getString(MovieFragment.COL_RUNTIME)));

        // ------------------------------布局分割线-------------------------------

        /*
        * 填充电影预告片布局
        * */

        Cursor mCursor = getActivity().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                MovieFragment.TRAILERS_COLUMNS,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{cursor.getString(MovieFragment.COL_MOVIE_ID)},
                null
        );

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                String videoLink;
                shareText = String.format("%s\nhttps://www.youtube.com/watch?v=%s",
                        cursor.getString(MovieFragment.COL_MOVIE_TITLE), mCursor.getString(MovieFragment.COL_VIDEO_LINK));
                do {
                    videoLink = String.format("https://www.youtube.com/watch?v=%s", mCursor.getString(MovieFragment.COL_VIDEO_LINK));
                    detailViewHolder.mLayout.addView(addTrailerView(mCursor.getString(MovieFragment.COL_VIDEO_TITLE), videoLink));
                } while (mCursor.moveToNext());
            } else {
                detailViewHolder.trailerHint.setText(R.string.detail_fragment_notYetTrailer);
            }
        }

        // ------------------------------布局分割线-------------------------------

        /*
        * 填充电影评论布局
        * */

        mCursor = getActivity().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                MovieFragment.REVIEWS_COLUMNS,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{cursor.getString(MovieFragment.COL_MOVIE_ID)},
                null
        );

        TextView reviewHintText = new TextView(getActivity());
        reviewHintText.setTextSize(24f);
        reviewHintText.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_light));
        reviewHintText.setPadding(0, dp8, 0, dp8);

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                reviewHintText.setText(R.string.detail_fragment_reviewText);
                detailViewHolder.mLayout.addView(reviewHintText);
                do {
                    detailViewHolder.mLayout.addView(addReviewView(mCursor.getString(MovieFragment.COL_REVIEW_AUTHOR), mCursor.getString(MovieFragment.COL_REVIEW_CONTENT)));
                } while (mCursor.moveToNext());

            } else {
                reviewHintText.setText(R.string.detail_fragment_notYetReview);
                detailViewHolder.mLayout.addView(reviewHintText);
            }
            mCursor.close();
        }

        cursor.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private Intent createShareUriIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }
}
