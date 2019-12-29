package com.example.lb2.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lb1.R;
import com.example.lb2.DatabaseHelper;
import com.example.lb2.model.Importance;
import com.example.lb2.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    EditText name;
    EditText description;
    Spinner importance;
    Note note;
    Button save;
    Button datetime;
    ImageView imageView;
    int imageId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        datetime = findViewById(R.id.date);
        imageView = findViewById(R.id.imageView);
        importance = findViewById(R.id.importance);
        save = findViewById(R.id.save);

        ArrayAdapter<Importance> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Importance.values());
        importance.setAdapter(adapter);


        if (getIntent().getSerializableExtra("Note") != null) {
            note = (Note) getIntent().getSerializableExtra("Note");
            imageId = note.getPicResource();
            name.setText(note.getName());
            description.setText(note.getDescription());
            importance.setSelection(note.getImportance().ordinal());
            imageView.setImageURI(Uri.parse("content://media/external/images/media/" + note.getPicResource()));
            datetime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(note.getDate()));
    }
        else{
            datetime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                Note extraNote = null;
                try {
                    extraNote = new Note(note == null ? -1 : note.getId(),name.getText().toString(),description.getText().toString(), (Importance) importance.getSelectedItem(),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(datetime.getText().toString()), imageId);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                data.putExtra("Note", extraNote);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });

        datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate(v);
            }
        });
    }

    public void chooseDate(View view) {
        final Calendar date = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(NoteActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                note.setDate(calendar.getTime());
                datetime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(note.getDate()));
            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void pickFromGallery(){

        Intent intent=new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,1);

    }
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 1:
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
                    String[] split = selectedImage.toString().split("/");
                    imageId = Integer.valueOf(split[split.length-1]);
                    break;
            }

    }


}
