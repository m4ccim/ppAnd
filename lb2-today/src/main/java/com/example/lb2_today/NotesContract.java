package com.example.lb2_today;

import android.content.ContentUris;
import android.net.Uri;

public class NotesContract {

    static final String TABLE_NAME = "notes";
    static final String CONTENT_AUTHORITY = "com.example.lb2";
    static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE= "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static class Columns{
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMPORTANCE = "importance";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PICTURE = "picture";

        private Columns(){

        }
    }
    static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);
    // создает uri с помощью id
    static Uri buildNoteUri(long taskId){
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }
    // получает id из uri
    static long getNoteId(Uri uri){
        return ContentUris.parseId(uri);
    }

}
