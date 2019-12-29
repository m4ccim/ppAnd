package com.example.lb1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // набор данных, которые свяжем со списком
    ListView notesList;

    ArrayList<Note> notes;
    NoteAdapter noteAdapter;
    int selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        notes = loadData();
        if(notes == null) {
            notes = new ArrayList<>();
            setInitialData();
            saveData();
        }
        // получаем элемент ListView
        notesList = findViewById(R.id.notesList);
        // создаем адаптер
         noteAdapter = new NoteAdapter(this, R.layout.list_item, notes);
        // устанавливаем адаптер
        notesList.setAdapter(noteAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selected = noteAdapter.getConstNotePosition(position);
                Note selectedState = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("Note", selectedState);
                startActivityForResult(intent, 1);

                // получаем выбранный пункт

            }
        };
        notesList.setOnItemClickListener(itemListener);


        registerForContextMenu(notesList);
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.context_importance, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.high:
                        noteAdapter.getIFilter().filter("High");
                        return true;
                    case R.id.medium:
                        noteAdapter.getIFilter().filter("Medium");
                        return true;
                    case R.id.low:
                        noteAdapter.getIFilter().filter("Low");
                        return true;
                    case R.id.notset:
                        noteAdapter.getFilter().filter("");
                        return true;
                    default:
                        return false;
                }
            }
        });


        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                selected = -1;
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_filter:
                showPopup(findViewById(R.id.action_filter));
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        Note extraNote = (Note) data.getSerializableExtra("Note");
        if (selected == -1) {
            notes.add(extraNote);
        }
        else {
            notes.set(selected, extraNote);
            noteAdapter.notifyListChanged();
        }
        noteAdapter.notifyDataSetChanged();
        saveData();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        if(v.getId() == R.id.action_filter) {
            inflater.inflate(R.menu.context_importance, menu);
        }
        else {
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.delete:
                deleteNote(noteAdapter.getConstNotePosition((int)info.id));
                noteAdapter.notifyListChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteNote(long id) {
        notes.remove((int)id);
        noteAdapter.notifyDataSetChanged();
        saveData();
    }

    private void setInitialData() {

        notes.add(new Note("Бразилаа", "Бразилиа", Importance.Low, new Date(123254632L), 17513));
        notes.add(new Note("Аргента", "Бразилиа", Importance.Low, new Date(100006546500L),17513));
        notes.add(new Note("Колумбия", "Бразилиа", Importance.High, new Date(12100165432L), 17513));
        notes.add(new Note("Бразилия", "Бразлиа", Importance.Medium, new Date(120106543232L), 17513));
        notes.add(new Note("Аргентина", "Бразили", Importance.Low, new Date(12301001546432L), 17513));
        notes.add(new Note("Колумбия", "Бразилиа", Importance.High, new Date(12010165432L), 17513));
        notes.add(new Note("Бразилия", "Бразииа", Importance.Medium, new Date(123016546501232L), 17513));
        notes.add(new Note("Аргентина", "Бразилиа", Importance.Low, new Date(12330156402L), 17513));
        notes.add(new Note("Колумбия", "Бразилиа", Importance.High, new Date(12010106545032L), 17513));
        notes.add(new Note("Бразилаа", "Бразилиа", Importance.Low, new Date(123254632L), 17513));
    }

    private void saveData() {
        FileOutputStream fos = null;
        try {
            fos = getApplicationContext().openFileOutput("notes", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(notes);
            os.close();
            fos.close();
            Toast.makeText(getApplicationContext(),"data saved", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"data save fail", Toast.LENGTH_SHORT).show();;
        }

    }

    private ArrayList<Note> loadData() {

        try {
            FileInputStream fis = getApplicationContext().openFileInput("notes");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<Note> notes = (ArrayList<Note>) is.readObject();
            is.close();
            fis.close();
            Toast.makeText(getApplicationContext(),"data loaded", Toast.LENGTH_SHORT).show();;
            return notes;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"no data", Toast.LENGTH_SHORT).show();;
            return null;
        }
    }



}

