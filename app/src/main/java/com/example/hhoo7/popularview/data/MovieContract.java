package com.example.hhoo7.popularview.data;

import android.provider.BaseColumns;

/*
* 存放一些常量字符串
* */
public class MovieContract {

    //电影详情表格所需要用到的字符串
    public static final class DetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "detail";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVER_VIEW = "over_view";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_FAVORITE = "favorite";

    }

    //预告片表格所需用到的字符串
    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_MOVIEID_KEY = "movie_id";
        public static final String COLUMN_YOUTUBE_KEY = "youtube_key";
        public static final String COLUMN_TRAILER_TITLE = "trailer_title";
    }

    //评论表格所需用到的字符串
//    public static final class ReviewEntry implements BaseColumns {
//        public static final String TABLE_NAME = "review";
//        public static final String COLUMN_MOVIEID_KEY = "movie_id";
//        public static final String COLUMN_REVIEW_AUTHOR = "author";
//        public static final String COLUMN_REVIEW_CONTENT = "content";
//    }


}
