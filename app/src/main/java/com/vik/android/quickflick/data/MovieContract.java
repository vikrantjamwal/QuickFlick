package com.vik.android.quickflick.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract{

    private MovieContract(){
    }

    public static final String CONTENT_AUTHORITY = "com.vik.android.quickflick";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public final static String TABLE_NAME = "movies";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MOVIE_TITLE = "title";
        public final static String COLUMN_MOVIE_ID = "id";
        public final static String COLUMN_MOVIE_POSTER = "poster";
    }
}
