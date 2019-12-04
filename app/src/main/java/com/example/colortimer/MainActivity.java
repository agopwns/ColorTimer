package com.example.colortimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.colortimer.db.TimeMarkDB;
import com.example.colortimer.db.TimeMarkDao;
import com.example.colortimer.model.TimeMark;

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
    private ImageView imageViewStartStop, imageViewReport;
    private CountDownTimer countDownTimer;
    private boolean isRepeat = true;
    private boolean isWork = true;
    private boolean isSavingData = false;
    private TimeMarkDao dao;
    private long startTime;
    private long endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieProgressDrawable = new PieProgressDrawable();
        pieProgressDrawable.setColor(ContextCompat.getColor(this, R.color.colorGoogleRed));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        pieProgressDrawable.setBorderWidth(2, dm);
        // add ImageProgressBar by PeanutButter
        timeProgress = (ImageView) findViewById(R.id.time_progress);
        timeProgress.setImageDrawable(pieProgressDrawable);

        // connect db
        dao = TimeMarkDB.getInstance(this).timeMarkDao();

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();


    }

    private void initMyData() {

//        long tempStaTime = 1575299800000L;
//        long tempEndTime = 1575333100000L;
//
//        isWork = true;
//
//        long usedTime = tempEndTime - tempStaTime;
//        String workState = "";
//        if(isWork)
//            workState = "work";
//        else
//            workState = "rest";
//
//        TimeMark timeMark = new TimeMark();
//        timeMark.setStartTime(tempStaTime);
//        timeMark.setEndTime(tempEndTime);
//        timeMark.setUsedTime(usedTime);
//        timeMark.setWorkState(workState);
//        timeMark.setUpload(false);
//        dao.insertTimeMark(timeMark);
//
//        tempStaTime = 1575334100000L;
//        tempEndTime = 1575354800000L;
//
//        isWork = false;
//
//        usedTime = tempEndTime - tempStaTime;
//        workState = "";
//        if(isWork)
//            workState = "work";
//        else
//            workState = "rest";
//
//        TimeMark timeMark2 = new TimeMark();
//        timeMark2.setStartTime(tempStaTime);
//        timeMark2.setEndTime(tempEndTime);
//        timeMark2.setUsedTime(usedTime);
//        timeMark2.setWorkState(workState);
//        timeMark2.setUpload(false);
//        dao.insertTimeMark(timeMark2);
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
        imageViewReport = (ImageView) findViewById(R.id.imageViewReport);
    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        imageViewWorkRest.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
        imageViewRepeat.setOnClickListener(this);
        imageViewReport.setOnClickListener(this);
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
            case R.id.imageViewReport:
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(intent);
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

        endTime = System.currentTimeMillis();
        saveTimeMark();
        startTime = System.currentTimeMillis();

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

            endTime = System.currentTimeMillis();
            saveTimeMark();

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

        startTime = System.currentTimeMillis();
        if(countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

//                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                updateTime((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {

                endTime = System.currentTimeMillis();
                saveTimeMark();

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

    public void saveTimeMark(){

        // 타이머가 자동 재시작시 빠른 접근(밀리 세컨드 단위)으로 여러 번 저장하는 것을 막기 위해
        // 저장 중에는 다른 저장 요청을 받아들이지 않음.
        if(!isSavingData){

            isSavingData = true;

            long usedTime = endTime - startTime;
            String workState = "";
            if(isWork)
                workState = "work";
            else
                workState = "rest";

            TimeMark timeMark = new TimeMark();
            timeMark.setStartTime(startTime);
            timeMark.setEndTime(endTime);
            timeMark.setUsedTime(usedTime);
            timeMark.setWorkState(workState);
            timeMark.setUpload(false);
            dao.insertTimeMark(timeMark);

            isSavingData = false;
        }
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
