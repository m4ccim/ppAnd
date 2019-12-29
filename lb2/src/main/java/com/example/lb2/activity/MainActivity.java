package com.example.lb2.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.lb1.R;
import com.example.lb2.DatabaseHelper;
import com.example.lb2.NoteAdapter;
import com.example.lb2.model.Importance;
import com.example.lb2.model.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // набор данных, которые свяжем со списком
    ListView listView;
    NoteAdapter noteAdapter;
    List<Note> notes;
    int selected = -1;
    ProgressBar progressBar;
    DatabaseHelper databaseHelper;
    private LinearLayout panel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notes = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.notesList);
        panel = findViewById(R.id.panel);

        databaseHelper = new DatabaseHelper(this);

        GetNotesAsyncTask getNotesAsyncTask = new GetNotesAsyncTask();
        getNotesAsyncTask.execute();


        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selected = position;
                Note selectedState = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("Note", selectedState);
                startActivityForResult(intent, 1);



            }
        };
        listView.setOnItemClickListener(itemListener);


        registerForContextMenu(listView);


    }

    private String CHANNEL_ID;

    private void createNotificationChannel() {
        CharSequence channelName = CHANNEL_ID;
        String channelDesc = "channelDesc";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDesc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            NotificationChannel currChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (currChannel == null)
                notificationManager.createNotificationChannel(channel);
        }
    }




    public void createNotification(String message, String description, int picture) {

        CHANNEL_ID = getString(R.string.app_name);
        if (message != null ) {
            createNotificationChannel();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("content://media/external/images/media/"+picture));
            } catch (IOException e) {
                e.printStackTrace();
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bitmap)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(description)
                    .setContentTitle(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            mBuilder.setSound(uri);



            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            int notificationId = (int) (System.currentTimeMillis()/4);
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }



    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.context_importance, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                FilterAsyncTask filterAsyncTask = new FilterAsyncTask();

                switch (item.getItemId()) {
                    case R.id.high:
                        filterAsyncTask.execute(new Wrapper("High",null));
                        break;
                    case R.id.medium:
                        filterAsyncTask.execute(new Wrapper("Medium",null));
                        break;
                    case R.id.low:
                        filterAsyncTask.execute(new Wrapper("Low",null));
                        break;
                    case R.id.notset:
                        filterAsyncTask.execute(new Wrapper(null,""));
                        break;
                    default:
                        break;
                }


                return true;

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
                FilterAsyncTask filterAsyncTask = new FilterAsyncTask();
                filterAsyncTask.execute(new Wrapper(null,newText));
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
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_filter:
                showPopup(findViewById(R.id.action_filter));
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        Note extraNote = (Note) data.getSerializableExtra("Note");
        if (extraNote.getId() == -1) {
            databaseHelper.insertNote(extraNote);
            notes.add(extraNote);
            noteAdapter.notifyListChanged();
        } else {
            databaseHelper.updateNote(extraNote);
            notes.set(noteAdapter.getConstNotePosition(selected), extraNote);
            noteAdapter.notifyListChanged();
        }

        noteAdapter.notifyDataSetChanged();
        ;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        if (v.getId() == R.id.action_filter) {
            inflater.inflate(R.menu.context_importance, menu);
        } else {
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
                databaseHelper.deleteNote(noteAdapter.getItemByPosition(info.position));
                noteAdapter.removeNote(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public static Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime( fecha );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    class GetNotesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            panel.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            notes = databaseHelper.getAllNotes();
            for (Note n : notes)
                if(getZeroTimeDate(n.getDate()).equals(getZeroTimeDate(new Date())))
                createNotification(n.getName(), n.getDescription(), n.getPicResource());

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            noteAdapter = new NoteAdapter(getApplicationContext(), R.layout.list_item, notes);
            listView.setAdapter(noteAdapter);
            panel.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    class Wrapper{
        String importance;
        String search;


        Wrapper(String importance, String search){
            this.importance = importance;
            this.search = search;
        }
    }

    class FilterAsyncTask extends AsyncTask<Wrapper, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            panel.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Wrapper ...params) {

            if(params[0].importance != null)
                noteAdapter.getIFilter().filter(params[0].importance);
            else if (params[0].search != null)
                noteAdapter.getFilter().filter(params[0].search);

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            panel.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}






