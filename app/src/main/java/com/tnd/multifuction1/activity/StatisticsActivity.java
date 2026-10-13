package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.DbModelSelector;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.CheckResult;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

public class StatisticsActivity extends Activity implements View.OnClickListener {

    private static final String TAG = StatisticsActivity.class.getSimpleName();
    Typeface mTfLight = Typeface.DEFAULT;
    private BarChart mChart;
    private TextView tvDataCount;
    private ImageView ivQueryCondition;
    DbUtils dbUtils = DbHelper.GetInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initView();
        initChart();

        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

//        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart

//        setData(2, 50);
        DbModelSelector selector1 = getBaseCondition("合格");
        DbModelSelector selector2 = getBaseCondition("不合格");
        getDataCountAndShow(selector1, selector2);
    }

    private DbModelSelector getBaseCondition(String value) {

        return Selector.from(CheckResult.class).select("count(*)").where("resultJudge", "=", value);
    }

    private void getDataCountAndShow(DbModelSelector selector1, DbModelSelector selector2) {

        int qualificationCount = 0;
        int disQualificationCount = 0;
        try {
            Cursor cursor = dbUtils.execQuery(selector1.toString());
            while (cursor.moveToNext()) {
                qualificationCount = cursor.getInt(0);
            }
            cursor = dbUtils.execQuery(selector2.toString());
            while (cursor.moveToNext()) {
                disQualificationCount = cursor.getInt(0);
            }
            updateDataCountShow(qualificationCount, disQualificationCount);
            setData(qualificationCount, disQualificationCount);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void updateDataCountShow(int qualificationCount, int disQualificationCount) {

        String str = getResources().getString(R.string.query_data_count);
        tvDataCount.setText(String.format(str, qualificationCount + disQualificationCount));

    }

    private void initChart() {
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);
        mChart.setExtraBottomOffset(50);
        IAxisValueFormatter xAxisFormatter = new MyAxisValueFormatter(new String[]{"合格", "不合格"});

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setTextSize(28);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new DefaultAxisValueFormatter(0);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextSize(28);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setTextSize(28);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setEnabled(false);
    }

    private void initView() {

        mChart = findViewById(R.id.bar_chart);
        ivQueryCondition = findViewById(R.id.iv_query_condition);
        tvDataCount = findViewById(R.id.tv_data_count);
        ivQueryCondition.setOnClickListener(this);
    }

    private void setData(int qualificationCount, int disQualificationCount) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>(2);

        yVals1.add(new BarEntry(0, qualificationCount));
        yVals1.add(new BarEntry(1, disQualificationCount));

        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year 2017");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(28f);
            data.setValueTypeface(mTfLight);
            data.setValueFormatter(new DefaultValueFormatter(0));
            data.setBarWidth(0.7f);

            mChart.setData(data);
        }
        mChart.animateY(2000);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_query_condition:
                showSelectTimeDialog();
                break;
        }
    }

    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();


    private void showSelectTimeDialog() {

        View dateSelectView = View.inflate(this, R.layout.dialog_date_select, null);
        DatePicker startTime = dateSelectView.findViewById(R.id.date_picker_start_time);
        DatePicker endTime = dateSelectView.findViewById(R.id.date_picker_end_time);

        startTime.init(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            }
        });
        endTime.init(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endCalendar.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
            }
        });
        new AlertDialog.Builder(this)
                .setView(dateSelectView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DbModelSelector selector1 = getBaseCondition("合格").and("testTime", ">",
                                startCalendar.getTimeInMillis()).and("testTime", "<", endCalendar.getTimeInMillis());
                        DbModelSelector selector2 = getBaseCondition("不合格").and("testTime", ">",
                                startCalendar.getTimeInMillis()).and("testTime", "<",endCalendar.getTimeInMillis());
                        getDataCountAndShow(selector1, selector2);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }
}
