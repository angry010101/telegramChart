package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.custom.LineChart.LineChart;
import com.yakymovych.simon.telegramchart.custom.ProgressBar.GraphProgressBar;

import java.util.ArrayList;
import java.util.List;

public class XLabelsView  extends View {
    private int datesCount = 5;
    private int visibleDatesCount = 5;
    private double pxPerDate = 5;
    private int datesStep = 5;
    int width,height;
    LineChart.LineChartListener lineChartListener;
    int start,end;
    List<Long> dates;
    List<String> datesStr;


    public void setDates(List<Double> dates) {
        List<Long> integers = new ArrayList<>();
        for (Double item : dates) {
            integers.add(item.longValue());
        }
        this.datesStr = GraphGenerator.getStringDates(integers);
        this.dates = integers;
        this.setDatesCount(dates.size());
    }

    public void setDatesCount(int datesCount) {
        this.datesCount = datesCount;
        pxPerDate = (double)(this.width)/visibleDatesCount;
        this.invalidate();
    }

    public XLabelsView(Context context) {
        super(context);
        init();
    }

    public XLabelsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XLabelsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public XLabelsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }




    public void setStart(int start) {

        this.start = start;
        this.setDatesStep();
    }
    public void setEnd(int end) {
        this.end = end;
        this.setDatesStep();
    }
    private void setDatesStep(){
        this.datesStep = (this.end - this.start)/visibleDatesCount;
    }
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private void initSizes(){
        width = this.getWidth();
        height = this.getHeight();
        pxPerDate = (double)width/visibleDatesCount;
        if (lineChartListener != null){
            lineChartListener.onDidInit();
        }
        this.invalidate();
    }

    private void init(){
        //int myColor = 0x88f5f8f9;
        paint.setColor(Color.RED);
        paint.setTextSize(20);
        paint.setAntiAlias(true);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSizes();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.dates != null && !this.dates.isEmpty())
            drawDates(canvas,paint);
    }

    private void drawDates(Canvas canvas,Paint paint) {
        Log.d("XLABELSIEW"," " + pxPerDate + " " + this.width + " datesstep " + datesStep);
        for (int i =0;i<visibleDatesCount;i++){
            canvas.drawText(this.datesStr.get(start+(i*datesStep)),(int)(i*pxPerDate),height/2,paint);
        }
    }


    public void moveTo(int p1, int p2) {
        start = p1;
        end = p2;
        this.setDatesStep();
        this.invalidate();
    }
}
