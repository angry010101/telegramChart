package com.yakymovych.simon.telegramchart.custom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.custom.LineChart.LineChart;

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
    private double pxPerAllDates;
    private float animOffsetDates =0;
    private int animAlfa=100;
    boolean draggingStart  =false;

    ValueAnimator alphaAnimatorHide;
    ValueAnimator alphaAnimatorShow;
    ValueAnimator heightAnimator;

    ValueAnimator.AnimatorUpdateListener ll = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            beginAnimation(animation);
        }
    };
    public void startAnimShow(){
        if (alphaAnimatorHide != null && alphaAnimatorHide.isRunning())
            alphaAnimatorHide.pause();
        alphaAnimatorHide = ValueAnimator.ofInt(100,0);
        alphaAnimatorHide.setDuration(500);
        alphaAnimatorHide.addUpdateListener(ll);
        alphaAnimatorHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                alphaAnimatorShow = ValueAnimator.ofInt(0,100);
                alphaAnimatorShow.setDuration(200);
                alphaAnimatorShow.addUpdateListener(ll);
                alphaAnimatorShow.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnimatorHide.start();
    }

    private void beginAnimation(ValueAnimator animation) {
        this.animAlfa = (Integer)animation.getAnimatedValue();
        this.invalidate();
    }

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
        init(context,null);
    }

    public XLabelsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public XLabelsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public XLabelsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }




    public void setStart(int start) {
        this.start = start;
        draggingStart = true;
        this.setDatesStep();
        this.startAnimShow();
    }
    public void setEnd(int end) {
        this.end = end;
        draggingStart = false;
        this.setDatesStep();
        this.startAnimShow();
    }
    private void setDatesStep(){
        this.datesStep = (this.end - this.start)/visibleDatesCount;
    }
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private void initSizes(){
        width = this.getWidth();
        height = this.getHeight();
        pxPerDate = (double)width/visibleDatesCount;
        pxPerAllDates = (double)width/datesCount;
        if (lineChartListener != null){
            lineChartListener.onDidInit();
        }
        this.invalidate();
    }

    private void init(Context context, AttributeSet attrs){
        //int myColor = 0x88f5f8f9;
        paint.setColor(Color.RED);
        paint.setTextSize(20);
        paint.setAntiAlias(true);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                context.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary,
                        android.R.attr.textColorSecondary});
        int primaryColor = arr.getColor(1, -1);

        paint.setColor(primaryColor);

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
        paint.setAlpha(this.animAlfa);
        for (int i =0;i<visibleDatesCount;i++){
            canvas.drawText(this.datesStr.get(start+(i*datesStep)),(int)(i*pxPerDate)+ animOffsetDates,height/2,paint);
        }
    }


    public void moveTo(int p1, int p2) {
        start = p1;
        end = p2;
        this.setDatesStep();
        this.invalidate();
    }
}
