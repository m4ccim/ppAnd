package com.example.lb2;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;

import com.example.lb2.model.Importance;
import com.example.lb2.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int SCHEMA = 1;
    public static final String TABLE = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMPORTANCE = "importance";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PICTURE = "picture";

    private static DatabaseHelper instance = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    static DatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + "( " +
                COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + "TEXT," +
                COLUMN_DESCRIPTION + "TEXT," +
                COLUMN_IMPORTANCE + "TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_PICTURE + "INTEGER" +
                ")");

        db.execSQL("INSERT INTO " + TABLE  +
                " ("+  COLUMN_NAME + ", " +COLUMN_DESCRIPTION +" , "+COLUMN_IMPORTANCE+", "+COLUMN_DATE+", "+COLUMN_PICTURE+") VALUES ('Name', 'Description', 'Low', '', '');");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }

    public long insertNote(Note note) {

        try(SQLiteDatabase db = this.getWritableDatabase()){
            ContentValues values = new ContentValues();

            values.put(COLUMN_NAME, note.getName());
            values.put(COLUMN_DESCRIPTION, note.getDescription());
            values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(note.getDate()));
            values.put(COLUMN_IMPORTANCE, note.getImportance().toString());
            values.put(COLUMN_PICTURE, note.getPicResource());


            return db.insert(TABLE, null, values);
        }
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        try(SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE, null)){
            if (cursor.moveToFirst()) {
                do {
                    Note note = new Note(cursor.getInt(
                            cursor.getColumnIndex(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                            Importance.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_IMPORTANCE))),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE))),
                            cursor.getInt(cursor.getColumnIndex(COLUMN_PICTURE))
                            );


                    notes.add(note);
                } while (cursor.moveToNext());

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public int updateNote(Note note) {
        try(SQLiteDatabase db = this.getWritableDatabase()){

            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, note.getId());
            values.put(COLUMN_NAME, note.getName());
            values.put(COLUMN_DESCRIPTION, note.getDescription());
            values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(note.getDate()));
            values.put(COLUMN_IMPORTANCE, note.getImportance().toString());
            values.put(COLUMN_PICTURE, note.getPicResource());

            return db.update(TABLE, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(note.getId())});
        }

    }

    public void deleteNote(Note note) {
        try(SQLiteDatabase db = this.getWritableDatabase()){
            db.delete(TABLE, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(note.getId())});
        }
    }
}