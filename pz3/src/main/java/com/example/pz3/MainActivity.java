package com.example.pz3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button[] buttonNums = new Button[10];
    Button buttonDivide;
    Button buttonMuliply;
    Button buttonPlus;
    Button buttonMinus;
    Button buttonComa;
    Button buttonDel;
    Button buttonEnter;
    TextView textViewSolve;
    TextView textViewResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNums[0] = findViewById(R.id.buttonNum0);
        buttonNums[1] = findViewById(R.id.buttonNum1);
        buttonNums[2] = findViewById(R.id.buttonNum2);
        buttonNums[3] = findViewById(R.id.buttonNum3);
        buttonNums[4] = findViewById(R.id.buttonNum4);
        buttonNums[5] = findViewById(R.id.buttonNum5);
        buttonNums[6] = findViewById(R.id.buttonNum6);
        buttonNums[7] = findViewById(R.id.buttonNum7);
        buttonNums[8] = findViewById(R.id.buttonNum8);
        buttonNums[9] = findViewById(R.id.buttonNum9);
        buttonDivide = findViewById(R.id.buttonDivide);
        buttonMinus = findViewById(R.id.buttonMinus);
        buttonPlus = findViewById(R.id.buttonPlus);
        buttonMuliply = findViewById(R.id.buttonMultiply);
        buttonDel = findViewById(R.id.buttonDel);
        buttonComa = findViewById(R.id.buttonComa);
        buttonEnter = findViewById(R.id.buttonEnter);
        textViewResult = findViewById(R.id.textViewResult);
        textViewSolve = findViewById(R.id.textViewSolve);


        for(Button b: buttonNums){
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textViewSolve.append(((Button)view).getText());
                    solve();

                }
            });
        }

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewSolve.setText(textViewResult.getText());
                textViewResult.setText("");

            }
        });

        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textViewSolve.length()>0)
                textViewSolve.setText(textViewSolve.getText().toString().substring(0, textViewSolve.getText().length() - 1));
                solve();
            }

        });

        buttonDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                textViewSolve.setText("");
                solve();
                return true;
            }
        });
        buttonComa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLastDigit()){
                    String[] numbers = splitNumbers();
                    if(!numbers[numbers.length-1].contains("."))
                        textViewSolve.append(".");
                }
            }
        });
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLastDigit())
                textViewSolve.append("+");
            }
        });
        buttonMuliply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLastDigit())
                textViewSolve.append("*");

            }
        });
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLastDigit() || textViewSolve.length()==0)
                    textViewSolve.append("-");

            }
        });
        buttonDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLastDigit())
                textViewSolve.append("/");
            }
        });


    }

    String[] splitNumbers(){
        return textViewSolve.getText().toString().split("[+\\-/*]+");
    }

    boolean isLastDigit(){
       return textViewSolve.length()> 0 && Character.isDigit(textViewSolve.getText().toString().charAt(textViewSolve.getText().length() - 1));
    }

    void solve(){
        String[] numbersStr = splitNumbers();

        if(numbersStr.length<1 || (numbersStr.length==1 && numbersStr[0].equals("")))
        {
            textViewResult.setText("0.0");
            return;
        }
        if(numbersStr[0].equals(""))
            numbersStr = Arrays.copyOfRange(numbersStr, 1, numbersStr.length);
        List<Double> numbers = new ArrayList<Double>(numbersStr.length);


        for(int i = 0; i<numbersStr.length;i++) {
            try {
                numbers.add(Double.parseDouble(numbersStr[i]));
            } catch (NumberFormatException nfe) {
                System.out.println("I got exception for invalid string " + numbersStr[i]);
            }
        }
        List<String> signs = new ArrayList<String>(Arrays.asList(textViewSolve.getText().toString().split("([0-9]*[.])?[0-9]+")));


        if(signs.size()>0 && signs.get(0).equals("-")){
            numbers.set(0, numbers.get(0)*-1);
        }
        double result = 0.0;
        boolean isFirstStage = true;
        for(int i = 1; i<numbers.size() || isFirstStage;i++){
            if(isFirstStage)
            {
                if(i==numbers.size()) {
                    i=0;
                    isFirstStage=false;
                    result = numbers.get(0);
                    continue;
                }
                if(signs.get(i).equals("-") || signs.get(i).equals("+")) {
                    continue;
                }

            }
            if(!isFirstStage){
                if(signs.get(i).equals("*") || signs.get(i).equals("/")) {
                    continue;
                }
                if(i==1) result = numbers.get(i-1);

            }

            switch (signs.get(i)){
                case "*": {
                    double temp =   numbers.get(i-1)*numbers.get(i);
                    numbers.remove(i);
                    signs.remove(i);
                    numbers.set(i - 1, temp);
                    i--;
                    break;
                }
                case "/": {
                    if(numbers.get(i).equals(0.0)) {
                        textViewResult.setText("Нельзя делить на ноль");
                        return;
                    }
                    double temp = numbers.get(i-1) / numbers.get(i);
                    numbers.remove(i);
                    signs.remove(i);
                    numbers.set(i - 1, temp);
                    i--;
                    break;
                }
                case "-": result-=numbers.get(i); break;
                case "+": result+=numbers.get(i); break;
            }
        }
        textViewResult.setText(String.valueOf(result));


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("textViewSolve", textViewSolve.getText().toString());
        outState.putString("textViewResult", textViewResult.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textViewSolve.setText(savedInstanceState.getString("textViewSolve"));
        textViewResult.setText(savedInstanceState.getString("textViewResult"));
    }
}
