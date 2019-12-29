package com.example.pz1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBarRed;
    SeekBar seekBarGreen;
    SeekBar seekBarBlue;
    SeekBar seekBarAlpha;
    View view;
    TextView textView;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.view);
        seekBarRed = findViewById(R.id.seekBarRed);
        seekBarBlue = findViewById(R.id.seekBarBlue);
        seekBarGreen = findViewById(R.id.seekBarGreen);
        seekBarAlpha = findViewById(R.id.seekBarAlpha);
        textView = findViewById(R.id.textViewARGB);

        seekBarAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                view.setBackgroundColor(Color.argb(i,seekBarRed.getProgress(),seekBarGreen.getProgress(),seekBarBlue.getProgress()));
                ChangeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                view.setBackgroundColor(Color.argb(seekBarAlpha.getProgress(),i,seekBarGreen.getProgress(),seekBarBlue.getProgress()));
                ChangeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                view.setBackgroundColor(Color.argb(seekBarAlpha.getProgress(), seekBarRed.getProgress(),i,seekBarBlue.getProgress()));
                ChangeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                view.setBackgroundColor(Color.argb(seekBarAlpha.getProgress(),seekBarRed.getProgress(),seekBarGreen.getProgress(),i));
                ChangeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarBlue.setProgress(100);
        seekBarRed.setProgress(100);
        seekBarGreen.setProgress(100);
        seekBarAlpha.setProgress(255);
    }
    void ChangeText(){
        textView.setText("("+seekBarAlpha.getProgress()+", "+seekBarRed.getProgress()+", "+
                seekBarGreen.getProgress()+", "+seekBarBlue.getProgress()+")");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("argbTextView", textView.getText().toString());
        outState.putInt("red", seekBarRed.getProgress());
        outState.putInt("blue", seekBarBlue.getProgress());
        outState.putInt("green", seekBarGreen.getProgress());
        outState.putInt("alpha", seekBarAlpha.getProgress());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        seekBarRed.setProgress(savedInstanceState.getInt("red"));
        seekBarBlue.setProgress(savedInstanceState.getInt("blue"));
        seekBarGreen.setProgress(savedInstanceState.getInt("green"));
        seekBarAlpha.setProgress(savedInstanceState.getInt("alpha"));
        textView.setText(savedInstanceState.getString("argbTextView"));

    }


}





