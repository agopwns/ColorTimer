package com.example.colortimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    ImageView timeProgress;
    PieProgressDrawable pieProgressDrawable;
    private EditText editTextMinute;
    private TextView textViewTime;
    private ImageView imageViewWorkRest, imageViewRepeat;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;
    private boolean isRepeat = true;
    private boolean isWork = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieProgressDrawable = new PieProgressDrawable();
        pieProgressDrawable.setColor(ContextCompat.getColor(this, R.color.colorGoogleRed));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        pieProgressDrawable.setBorderWidth(2, dm);

        timeProgress = (ImageView) findViewById(R.id.time_progress);
        timeProgress.setImageDrawable(pieProgressDrawable);

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();
    }

    /**
     * method to initialize the views
     */
    private void initViews() {

//        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        editTextMinute = (EditText) findViewById(R.id.editTextMinute);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        imageViewWorkRest = (ImageView) findViewById(R.id.imageViewWorkRest);
        imageViewRepeat = (ImageView) findViewById(R.id.imageViewRepeat);
        imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        imageViewWorkRest.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
        imageViewRepeat.setOnClickListener(this);
    }

    /**
     * implemented method to listen clicks
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewWorkRest:
                changeWorkState();
                break;
            case R.id.imageViewStartStop:
                startStop();
                break;
            case R.id.imageViewRepeat:
                changeRepeatState();
                break;
        }
    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    private void changeWorkState(){

        if(isWork){
            isWork = false;
            pieProgressDrawable.setColor(ContextCompat.getColor(this, R.color.colorGoogleBlue));
            imageViewWorkRest.setImageResource(R.drawable.ic_monetization_on_24px);
        } else {
            isWork = true;
            pieProgressDrawable.setColor(ContextCompat.getColor(this, R.color.colorGoogleRed));
            imageViewWorkRest.setImageResource(R.drawable.ic_emoji_food_beverage_24px);
        }
    }

    private void changeRepeatState(){

        if(isRepeat){
            isRepeat = false;
            imageViewRepeat.setImageResource(R.drawable.ic_repeat_none_24px);
        } else {
            isRepeat = true;
            imageViewRepeat.setImageResource(R.drawable.ic_repeat_24px);
        }

    }

    /**
     * method to start and stop count down timer
     */
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            // changing play icon to stop icon
            imageViewStartStop.setImageResource(R.drawable.ic_pause_circle_filled_24px);
            // making edit text not editable
            editTextMinute.setVisibility(View.INVISIBLE);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {

            // changing stop icon to start icon
            imageViewStartStop.setImageResource(R.drawable.ic_play_circle_filled_24px);
            // making edit text editable
            editTextMinute.setVisibility(View.VISIBLE);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
        int time = 0;
        if (!editTextMinute.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(editTextMinute.getText().toString().trim());
        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

//                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                updateTime((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();

                if(!isRepeat){
                    // changing stop icon to start icon
                    imageViewStartStop.setImageResource(R.drawable.ic_play_circle_filled_24px);
                    // making edit text editable
                    editTextMinute.setVisibility(View.VISIBLE);
                    // changing the timer status to stopped
                    timerStatus = TimerStatus.STOPPED;
                } else {
                    startCountDownTimer();
                }


            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        // 한시간을 기준으로 하고 싶기 때문에 max 값을 3600초로 변경
//        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
//        progressBarCircle.setMax((int) 3600);
//        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);

        updateTime((int) timeCountInMilliSeconds / 1000);

    }

    public void updateTime(int progress) {
        pieProgressDrawable.setLevel(progress);
        timeProgress.invalidate();
    }



    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }
}
