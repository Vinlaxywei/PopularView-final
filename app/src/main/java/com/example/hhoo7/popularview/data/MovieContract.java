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
        public static final String COLUMN_RELEASE_DATE = "date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAVORITE = "favorite";

        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_TRAILERS_FOREIGN_KEY = "trailers";
    }

    //预告片表格所需用到的字符串
    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "youtubetrailers";
        public static final String COLUMN_KEY_ONE = "key_one";
        public static final String COLUMN_KEY_TWO = "key_two";
    }

}
