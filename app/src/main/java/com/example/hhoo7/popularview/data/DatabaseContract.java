package com.example.hhoo7.popularview.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.example.hhoo7.popularview.provider";

    //content://com.example.hhoo7.popularview
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DETAILE = "detail";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    public static final class DetailEntry implements BaseColumns {
        //内容提供器使用URI  content://com.example.hhoo7.popularview/detail
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DETAILE).build();

        public static final String TABLE_NAME = "detail";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVER_VIEW = "over_view";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RUNTIME = "run_time";
        public static final String COLUMN_FAVORITE = "favorite";

        // 构建查询URI
        public static Uri buildMovieIdUri(String movieid) {
            return CONTENT_URI.buildUpon().appendPath(movieid).build();
        }
    }

    public static final class TrailerEntry implements BaseColumns {
        //内容提供器使用URI  content://com.example.hhoo7.popularview/trailer
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_VIDEO_LINK = "video_link";
        public static final String COLUMN_VIDEO_TITLE = "video_title";

        public static Uri buildMovieIdUri(String movieid) {
            return CONTENT_URI.buildUpon().appendPath(movieid).build();
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        //内容提供器使用  content://com.example.hhoo7.popularview/review
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String TABLE_NAME = "review";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";

    }

}
