package com.example.pz0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;




public class MainActivity extends AppCompatActivity {


    Button[] btns = new Button[9];

    RadioButton radioButton1;
    RadioButton radioButton2;
    SeekBar seekBar;
    TextView textView;

    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.margin_TextView);
        btns[0] = findViewById(R.id.button1);
        btns[1] = findViewById(R.id.button2);
        btns[2] = findViewById(R.id.button3);
        btns[3] = findViewById(R.id.button4);
        btns[4] = findViewById(R.id.button5);
        btns[5] = findViewById(R.id.button6);
        btns[6] = findViewById(R.id.button7);
        btns[7] = findViewById(R.id.button8);
        btns[8] = findViewById(R.id.button9);

        seekBar = findViewById(R.id.seekBar);
        checkBox = findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {

        });

        View.OnClickListener buttonListener = view -> {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (layoutParams.weight == 1)
                layoutParams.weight = 9;
            else layoutParams.weight = 1;

            if(checkBox.isChecked()) {
                Random rnd = new Random();
                view.getBackground().setColorFilter(
                        Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)), PorterDuff.Mode.MULTIPLY);
                int id = view.getId();


            }
            else view.getBackground().clearColorFilter();

            ((Button) view).setText(String.valueOf(((LinearLayout.LayoutParams) view.getLayoutParams()).weight));

        };

        for (Button btn : btns) btn.setOnClickListener(buttonListener);



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                for (Button btn : btns) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btn.getLayoutParams();

                    params.setMargins(i,i,i,i);
                    textView.setText("Margins: "+ i + " dp");
                    btn.setLayoutParams(params);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("buttons", btns);
        outState.putString("marginTextView", textView.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //btns = (Button[])savedInstanceState.getSerializable("buttons");
        textView.setText(savedInstanceState.getString("marginTextView"));
        int i = 0;
        for (Button b : (Button[])savedInstanceState.getSerializable("buttons")) {
            btns[i].setText(b.getText());
            btns[i].getBackground().setColorFilter(b.getBackground().getColorFilter());
            i++;
        }

    }
}

//        int orient = getResources().getConfiguration().orientation;
//        if (orient == Configuration.ORIENTATION_PORTRAIT) {
//            vCounter.setText(String.valueOf(counter));
//        } else {
//            vNumInc.setText(String.valueOf(numInc));
//            vNumDec.setText(String.valueOf(numDec));
//        }



