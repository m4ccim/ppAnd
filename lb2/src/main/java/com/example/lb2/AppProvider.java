package com.example.lb2;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public class AppProvider extends ContentProvider {
    static final String TABLE_NAME = "notes";
    static final String CONTENT_AUTHORITY = "com.example.lb2";
    static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE= "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static class Columns{
        public static final String COLUMN_ID = "_id";
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

    private DatabaseHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int NOTES = 100;
    public static final int NOTES_ID = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // content://com.metanit.tasktimer.provider/NOTES
        matcher.addURI(CONTENT_AUTHORITY, TABLE_NAME, NOTES);
        // content://com.metanit.tasktimer.provider/NOTES/8
        matcher.addURI(CONTENT_AUTHORITY, TABLE_NAME + "/#", NOTES_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = sUriMatcher.match(uri);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch(match){
            case NOTES:
                queryBuilder.setTables(TABLE_NAME);
                break;
            case NOTES_ID:
                queryBuilder.setTables(TABLE_NAME);
                long taskId = getNoteId(uri);
                queryBuilder.appendWhere(Columns.COLUMN_ID + " = " + taskId);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+ uri);
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch(match){
            case NOTES:
                return CONTENT_TYPE;
            case NOTES_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: "+ uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db;
        Uri returnUri;
        long recordId;

        switch(match){
            case NOTES:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TABLE_NAME, null, values);
                if(recordId > 0){
                    returnUri = buildNoteUri(recordId);
                }
                else{
                    throw new android.database.SQLException("Failed to insert: " + uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+ uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        String selectionCriteria = selection;

        if(match != NOTES && match != NOTES_ID)
            throw new IllegalArgumentException("Unknown URI: "+ uri);

        if(match== NOTES_ID) {
            long taskId = getNoteId(uri);
            selectionCriteria = Columns.COLUMN_ID + " = " + taskId;
            if ((selection != null) && (selection.length() > 0)) {
                selectionCriteria += " AND (" + selection + ")";
            }
        }
        return db.delete(TABLE_NAME, selectionCriteria, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String selectionCriteria = selection;

        if(match != NOTES && match != NOTES_ID)
            throw new IllegalArgumentException("Unknown URI: "+ uri);

        if(match== NOTES_ID) {
            long taskId = getNoteId(uri);
            selectionCriteria = Columns.COLUMN_ID + " = " + taskId;
            if ((selection != null) && (selection.length() > 0)) {
                selectionCriteria += " AND (" + selection + ")";
            }
        }
        return db.update(TABLE_NAME, values, selectionCriteria, selectionArgs);
    }
}