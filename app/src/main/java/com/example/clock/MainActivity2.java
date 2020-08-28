package com.example.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    private EditText minput;
    private TextView mcountdown;
    private Button mstart;
    private Button mreset;
    private Button mset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimeRunning;

    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        minput = findViewById(R.id.input);
        mcountdown = findViewById(R.id.countdown);
        mstart = findViewById(R.id.start);
        mreset = findViewById(R.id.reset);
        mset = findViewById(R.id.set);

        mset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = minput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(MainActivity2.this, "Can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(MainActivity2.this, "Enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                minput.setText("");



            }
        });

        mstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeRunning) {
                    pauseTimer();
                }else{
                    startTimer();
                }

            }
        });
        mreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

    }
    private void setTime(long milliseconds){
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();

    }
    private void startTimer(){
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }


            @Override
            public void onFinish() {
                mTimeRunning = false;
                updatebuttons();


            }

        }.start();
        mTimeRunning = true;
        updatebuttons();

    }
    private void pauseTimer(){
        mCountDownTimer.cancel();
        mTimeRunning = false;
        updatebuttons();
    }
    private void resetTimer(){
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        mreset.setVisibility(View.INVISIBLE);
        mstart.setVisibility(View.VISIBLE);

    }

    private  void updateCountDownText(){
        int hours = (int) (mTimeLeftInMillis/1000)/3600;
        int minutes = (int) ((mTimeLeftInMillis/1000) % 3600) /60;
        int seconds = (int) (mTimeLeftInMillis/1000) % 60;
        String timeLeftFormatted;
        if (hours>0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d",hours,minutes,seconds);
        }else{
            timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);

        }


        mcountdown.setText(timeLeftFormatted);
    }
    private void updatebuttons(){
        if (mTimeRunning){
            minput.setVisibility(View.INVISIBLE);
            mset.setVisibility(View.INVISIBLE);
            mreset.setVisibility(View.INVISIBLE);
            mstart.setText("Pause");
        }else{
            minput.setVisibility(View.VISIBLE);
            mset.setVisibility(View.VISIBLE);
            mstart.setText("Start");

            if (mTimeLeftInMillis < 1000) {
                mstart.setVisibility(View.INVISIBLE);

            }else{
                mstart.setVisibility(View.VISIBLE);
            }
            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mreset.setVisibility(View.VISIBLE);

            }else {
                mreset.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view !=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }






    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis",mStartTimeInMillis);
        editor.putLong("milllisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimeRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();
        if (mCountDownTimer !=null){
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);

        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimeRunning = prefs.getBoolean("timerRunning",false);
        updateCountDownText();
        updatebuttons();

        if (mTimeRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis<0){
                mTimeLeftInMillis = 0;
                mTimeRunning = false;
                updateCountDownText();
                updatebuttons();
            }else{
                startTimer();
            }
        }
    }
}


