package com.example.lb3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener{
    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private String songTitle="";
    private static final int NOTIFY_ID=1;
    public boolean shuffle=false;
    private Random rand;
    public boolean repeat=false;

    public void onCreate(){
        super.onCreate();

        songPosn=0;

        player = new MediaPlayer();
        initMusicPlayer();
        rand=new Random();

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

    }
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
               pausePlayer();
            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                go();
            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    public void setRepeat(){
        if(repeat) repeat=false;
        else repeat=true;
    }
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public void playSong(){

        player.reset();

        Song playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();

        long currSong = playSong.getID();

        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {



        createNotification();
        mp.start();

    }

    private String CHANNEL_ID;


    private void createNotificationChannel() {
        CharSequence channelName = CHANNEL_ID;
        String channelDesc = "channelDesc";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDesc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            NotificationChannel currChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (currChannel == null)
                notificationManager.createNotificationChannel(channel);
        }
    }




    public void createNotification() {

        CHANNEL_ID = getString(R.string.app_name);

        createNotificationChannel();

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notIntent.setAction(Intent.ACTION_MAIN);
        notIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendInt = PendingIntent.getActivity(getApplication(), 0,
                notIntent, 0);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            mBuilder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.android_music_player_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                    .setVibrate(new long[]{0L})
                    .setContentText(songTitle);

        Notification not = mBuilder.build();

        startForeground(1337, not);

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }
    public void playNext(){
        if(repeat){
            playSong();
        }
        else if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        playSong();
    }
}
