package com.example.lb2_today;

import android.content.ContentResolver;

import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ListView notesList;

    ArrayList<Note> notes;
    private static final String TAG = "MainActivity";
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        notes = loadData();


        // получаем элемент ListView
        notesList = findViewById(R.id.notesList);
        // создаем адаптер
        noteAdapter = new NoteAdapter(getApplicationContext(), R.layout.list_item, notes);
        // устанавливаем адаптер
        notesList.setAdapter(noteAdapter);
        // слушатель выбора в списке

    }
    private ArrayList<Note> loadData() {

        try {
            String[] projection = {
                    NotesContract.Columns.COLUMN_ID,
                    NotesContract.Columns.COLUMN_NAME,
                    NotesContract.Columns.COLUMN_DESCRIPTION,
                    NotesContract.Columns.COLUMN_IMPORTANCE,
                    NotesContract.Columns.COLUMN_DATE,
                    NotesContract.Columns.COLUMN_PICTURE
            };
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(NotesContract.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            //получаем данные из бд в виде курсора

            notes = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(1);
                    String description = cursor.getString(2);
                    Importance importance = Importance.valueOf(cursor.getString(3));
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(cursor.getString(4));
                    int picture = cursor.getInt(5);
                    if (new SimpleDateFormat("yyyy-MM-dd").format(date)
                            .equals(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())))
                    {
                        notes.add(new Note(name, description, importance, date, picture));
                    }

                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }
        catch (ParseException ex) {
            ex.printStackTrace();
        }

        return notes;

    }
}