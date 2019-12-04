package com.example.colortimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.colortimer.db.TimeMarkDB;
import com.example.colortimer.db.TimeMarkDao;
import com.example.colortimer.model.TimeMark;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.colortimer.Time.makeShortTimeString;

public class ResultActivity extends AppCompatActivity {

    private TextView workTime, restTime;
    private TimeMarkDao dao;
    private static String TAG = "ResultActivity";

    MaterialCalendarView materialCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        workTime = findViewById(R.id.workTime);
        restTime = findViewById(R.id.restTime);
        materialCalendarView = findViewById(R.id.calenderView);

        // 달력
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2019, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        // 달력 꾸미기
        materialCalendarView.addDecorators(
                new SundayDecorator(this),
                new SaturdayDecorator(this),
                new OneDayDecorator());

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget
                                     , @NonNull CalendarDay date, boolean selected) {
                // onDate 안


            }
        });

        // connect db
        dao = TimeMarkDB.getInstance(this).timeMarkDao();

        long usedWorkTime = 0;
        long usedRestTime = 0;

        List<TimeMark> totalResult = dao.getTimeMark();
        Toast.makeText(getApplicationContext(), "총 row 수 : " + totalResult.size(),Toast.LENGTH_SHORT).show();

        List<TimeMark> workResult = dao.getWorkTime();

        for(int i = 0; i < workResult.size(); i++){
            Log.i(TAG, "usedWorkTime row : " + workResult.get(i).getUsedTime());
            usedWorkTime += workResult.get(i).getUsedTime();
        }

        List<TimeMark> restResult = dao.getRestMark();

        for(int i = 0; i < restResult.size(); i++){
            Log.i(TAG, "usedRestTime row : " + workResult.get(i).getUsedTime());
            usedRestTime += restResult.get(i).getUsedTime();
        }

        // 날짜 변환
        long sec = usedWorkTime / 1000;
        long min = usedWorkTime / (1000 * 60);
        long hour = usedWorkTime / (1000 * 60 * 60);
        String resultTime = makeShortTimeString(getApplicationContext(), usedWorkTime / 1000);

        workTime.setText(resultTime);

        sec = usedRestTime / 1000;
        min = usedRestTime / (1000 * 60);
        hour = usedRestTime / (1000 * 60 * 60);
        resultTime = makeShortTimeString(getApplicationContext(), usedRestTime / 1000);

        restTime.setText(resultTime);

    }
}
