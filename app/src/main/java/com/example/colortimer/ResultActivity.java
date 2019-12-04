package com.example.colortimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.colortimer.db.TimeMarkDB;
import com.example.colortimer.db.TimeMarkDao;
import com.example.colortimer.model.TimeMark;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.colortimer.Time.makeShortTimeString;

public class ResultActivity extends AppCompatActivity implements OnItemSelectedListener{

    private TextView workTime, restTime;
    private TimeMarkDao dao;
    private static String TAG = "ResultActivity";
    private Date mSelectedDate;
    private long start;
    private long end;

    protected Typeface tfLight;
    MaterialCalendarView materialCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        workTime = findViewById(R.id.workTime);
        restTime = findViewById(R.id.restTime);
        materialCalendarView = findViewById(R.id.calenderView);

        //tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        // connect db
        dao = TimeMarkDB.getInstance(this).timeMarkDao();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        // 달력
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.THURSDAY)
                .setMinimumDate(CalendarDay.from(2019, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        // 달력 꾸미기
        materialCalendarView.addDecorators(
                new SundayDecorator(this));

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget
                                     , @NonNull CalendarDay date, boolean selected) {
                // onDate 안
                if(materialCalendarView.getSelectionMode() == MaterialCalendarView.SELECTION_MODE_SINGLE){
                    long usedWorkTime = 0;
                    long usedRestTime = 0;
                    long usedFromToTime = 0;

                    Toast.makeText(getApplicationContext(), "선택한 날짜 : " +
                            date.getMonth() + "월" + date.getDay(), Toast.LENGTH_SHORT ).show();

                    Log.i(TAG, "선택한 날짜 : " +
                            date.getMonth() + "월" + date.getDay());

                    // 해당 날짜
                    Calendar cal = Calendar.getInstance();
                    cal.set(date.getYear(), date.getMonth(), date.getDay()
                            , 0, 0, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    start = cal.getTimeInMillis();
                    Log.i(TAG, "처음 날짜 long 변환 : " + start);

                    DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); // HH=24h, hh=12h
                    String str = df.format(start);
                    Log.i(TAG, "처음 날짜 date 변환 : " + str);

                    // 그 다음 날짜
                    cal.set(date.getYear(), date.getMonth(), date.getDay() + 1
                            , 0, 0, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    end = cal.getTimeInMillis();
                    Log.i(TAG, "다음 날짜 long 변환 : " + end);

                    str = df.format(end);
                    Log.i(TAG, "다음 날짜 date 변환 : " + str);

                    List<TimeMark> fromToResult = dao.getTimeMarkFromTo(start, end);
                    for(int i = 0; i < fromToResult.size(); i++){
                        //Log.i(TAG, "usedFromToTime row : " + fromToResult.get(i).getUsedTime());

                        if(fromToResult.get(i).getWorkState().equals("work"))
                            usedWorkTime += fromToResult.get(i).getUsedTime();
                        else
                            usedRestTime += fromToResult.get(i).getUsedTime();
                    }

                    String resultTime = makeShortTimeString(getApplicationContext(), usedWorkTime / 1000);
                    workTime.setText(resultTime);

                    float workFloat = convertStringToFloat(resultTime);

                    resultTime = makeShortTimeString(getApplicationContext(), usedRestTime / 1000);
                    restTime.setText(resultTime);

                    float restFloat = convertStringToFloat(resultTime);
                    // 남은 시간 계산
                    float otherFloat = 24 - (workFloat + restFloat);


                    // today 파이 차트 ******************************************************************
                    PieChart chart = (PieChart) findViewById(R.id.pieChart);

                    //chart.setCenterTextTypeface(tfLight);
                    chart.setCenterText(generateCenterSpannableText());

                    chart.setDrawHoleEnabled(true);
                    chart.setHoleColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));

                    chart.setTransparentCircleColor(Color.WHITE);
                    chart.setTransparentCircleAlpha(110);

                    chart.setHoleRadius(58f);
                    chart.setTransparentCircleRadius(61f);

                    chart.setDrawCenterText(true);

                    chart.setRotationAngle(0);
                    // enable rotation of the chart by touch
                    chart.setRotationEnabled(true);
                    chart.setHighlightPerTapEnabled(true);

                    // 차트 색상 설정
                    int[] colors = getApplicationContext().getResources().getIntArray(R.array.color_array);
                    List<Integer> resultColor = ColorTemplate.createColors(colors);

                    // 데이터 설정
                    ArrayList NoOfEmp = new ArrayList();

                    NoOfEmp.add(new PieEntry(workFloat, 0));
                    NoOfEmp.add(new PieEntry(restFloat, 1));
                    NoOfEmp.add(new PieEntry(otherFloat, 2));
                    PieDataSet dataSet = new PieDataSet(NoOfEmp, "Used Time");

                    PieData data = new PieData();
                    data.setDataSet(dataSet);
                    chart.setData(data);
                    dataSet.setColors(resultColor);

                    chart.animateY(1300, Easing.EaseInOutQuad);
                }
                // week 바 차트 *********************************************************************
                // 선택한 날짜







            }
        });

        materialCalendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {

                // 1. 셀렉트한 날짜 가져오기





                // 2. 날짜마다 시간 구하기
                // 3. 리스트 넣기 전에 바 차트에 어떻게 데이터 넣는지 확인 후 형태 결정
                // 4. 바 차트 넣기
                // 5. 완성
                // 6. 테스트


            }
        });


        // 2. 해당 날짜로 db 에서 가져오기 테스트






        // total
//        List<TimeMark> totalResult = dao.getTimeMark();
//        Toast.makeText(getApplicationContext(), "총 row 수 : " + totalResult.size(),Toast.LENGTH_SHORT).show();

        // work all
//        List<TimeMark> workResult = dao.getWorkTime();
//        for(int i = 0; i < workResult.size(); i++){
//            Log.i(TAG, "usedWorkTime row : " + workResult.get(i).getUsedTime());
//
//            DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); // HH=24h, hh=12h
//            String str = df.format(workResult.get(i).getUsedTime());
//            Log.i(TAG, "usedWorkTime 날짜 date 변환 : " + str);
//
//            usedWorkTime += workResult.get(i).getUsedTime();
//
//
//        }

//        List<TimeMark> restResult = dao.getRestMark();
//
//        for(int i = 0; i < restResult.size(); i++){
//            Log.i(TAG, "usedRestTime row : " + restResult.get(i).getUsedTime());
//            usedRestTime += restResult.get(i).getUsedTime();
//        }



        // 날짜 변환
//        long sec = usedWorkTime / 1000;
//        long min = usedWorkTime / (1000 * 60);
//        long hour = usedWorkTime / (1000 * 60 * 60);
//        String resultTime = makeShortTimeString(getApplicationContext(), usedWorkTime / 1000);
//
//        workTime.setText(resultTime);

//        resultTime = makeShortTimeString(getApplicationContext(), usedRestTime / 1000);
//
//        restTime.setText(resultTime);

    }

    private float convertStringToFloat(String resultTime) {
        float workHour = 0;
        // 1시간 이상인 경우
        if(resultTime.split(":").length > 2){

            // 시간
            workHour += Float.parseFloat(resultTime.split(":")[0]);

            // 분
            float temp = Float.parseFloat(resultTime.split(":")[1]) / 60;
            temp = Float.parseFloat(String.format("%.2f", temp));
            workHour += temp;
        } else {

            // 분
            float temp = Float.parseFloat(resultTime.split(":")[0]) / 60;
            temp = Float.parseFloat(String.format("%.2f", temp));
            workHour += temp;
        }

        return workHour;
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("ColorTimer\ndeveloped by SeongJun Ha");
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 10, 0);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 10, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 10, s.length() - 11, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 10, s.length() - 11, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 10, s.length() - 11, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 11, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 11, s.length(), 0);
        return s;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0){
            materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        } else {
            materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
