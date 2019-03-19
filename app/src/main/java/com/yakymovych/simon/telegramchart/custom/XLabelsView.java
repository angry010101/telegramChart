package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.custom.LineChart.LineChart;

public class XLabelsView  extends View {
    private int datesCount = 5;
    private double pxPerDate = 5;
    int width,height;
    LineChart.LineChartListener lineChartListener;
    int start,end;

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
    }

    public void setEnd(int end) {
        this.end = end;
    }

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private void initSizes(){
        width = this.getWidth();
        height = this.getHeight();
        pxPerDate = (double)width/datesCount;
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
        //super.onDraw(canvas);
        if (Plot.dates != null && !Plot.dates.isEmpty())
            drawDates(canvas,paint);
    }

    private void drawDates(Canvas canvas,Paint paint) {
        for (int i =0;i<datesCount;i++){
            canvas.drawText(Plot.dates.get(start+i),(int)(i*pxPerDate),height/2,paint);
        }
    }


    public void moveTo(int p1, int p2) {
        start = p1;
        end = p2;
        this.invalidate();
    }
}
