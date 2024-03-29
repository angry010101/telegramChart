package com.yakymovych.simon.telegramchart.custom.ProgressBar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;
import com.yakymovych.simon.telegramchart.custom.MyScrollView;

import java.util.HashSet;
import java.util.Set;

public class GraphProgressBar extends View {
    public MathPlot mp ;
    private ProgressBarViewPort viewPort;
    private ProgressBarDrawManager progressBarDrawManager;
    private int progressStart =0;
    private int progressEnd=100;

    public final int progressMax = 112;
    private final int topMargin = 8;
    private final int minOffsetElems = 6;
    private Set<String> visiblePlots =new HashSet<>();
    private ProgressChangedListener progressChangedListener = null;
    private Chart chart;


    public void setVisiblePlots(Set<String> visiblePlots) {
        this.visiblePlots = visiblePlots;
    }


    public void setProgressChangedListener(ProgressChangedListener progressChangedListener) {
        this.progressChangedListener = progressChangedListener;
    }

    public GraphProgressBar(Context context) {
        super(context);
        init(context,null);
    }

    public GraphProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public GraphProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    public void setPlots(Chart c) {
        this.chart = c;

     }

    private void initSizes(int colorBorders, int colorShadow){
        int width = this.getWidth();
        int height= this.getHeight();
        this.viewPort = new ProgressBarViewPort(this,width,height, progressStart,progressEnd,progressMax,minOffsetElems);
        this.mp = new MathPlot(width,height,topMargin,topMargin,false,0);
        this.progressBarDrawManager = new ProgressBarDrawManager(mp,width,height,colorBorders,colorShadow);
        //setPlots is called before
        progressBarDrawManager.setChart(this.chart);
        mp.calculateCharts();
        this.invalidate();
    }


    public void setStartAndEnd(int s,int e){
        this.setProgressStartEnd(s,e);
        this.mp.setStartAndEnd(s,e);
        this.invalidate();
    }

    private void init(Context context,AttributeSet attrs){

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                context.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.colorAccent,
                        android.R.attr.colorForeground});
        final int colorShadow = arr.getColor(0, -1);
        final int colorBorders = arr.getColor(1, -1);

        arr.recycle();
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSizes(colorBorders,colorShadow);
            }
        });
    }


    @Override
    public synchronized boolean onTouchEvent(final MotionEvent event) {
        return viewPort.onTouchEvent(event);

    }

    public void handleStartMovement(int moveTo){

        if (moveTo <= 0) {
            progressStart = 0;
        }
        else {
            if (moveTo >= progressEnd-minOffsetElems) {
                progressStart = progressEnd-minOffsetElems;
            }
            else progressStart = moveTo;
        }

        viewPort.setStartpos(progressStart);

        if (progressChangedListener != null){
            progressChangedListener.onStartProgressChanged(
                    this, progressStart, progressEnd);
        }
    }

    public void handleOffsetMovement(int d,boolean direction){
        if (progressEnd+d > progressMax && direction) {
            d=progressMax-progressEnd;
        }
        else {
            if (progressStart +d<0 && !direction) {
                d = -progressStart;
            }
        }
        progressStart += d;
        progressEnd += d;
        viewPort.setStartpos(progressStart);
        viewPort.setEndpos(progressEnd);

        if (progressChangedListener != null){
            progressChangedListener.onOffsetProgressChanged(
                    this, progressStart, progressEnd);
        }
    }

    public void handleStartChanging(){

        this.scrollView.setLocked(true);
    }
    public void handleStopChanging(){
        scrollView.setLocked(false);

        if (progressChangedListener != null){
            progressChangedListener.onStopChanging(
                    this, progressStart, progressEnd);
        }
    }
    public void handleEndMovement(int moveToProgress){

        if (progressEnd+moveToProgress > progressMax) {
            progressEnd = progressMax;
        }
        else {
            if (progressEnd+moveToProgress < progressStart +minOffsetElems) {
                progressEnd = progressStart +minOffsetElems;
            }
            else progressEnd += moveToProgress;
        }
        viewPort.setEndpos(progressEnd);
        if (progressChangedListener != null){
            progressChangedListener.onEndProgressChanged(
                    this, progressStart, progressEnd);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        progressBarDrawManager.draw(canvas,visiblePlots,
                viewPort.getProgressStartPx(progressStart)
                ,viewPort.getProgressEndPx(progressEnd));
    }

    public void setVisiblePlot(String vp, boolean b) {
        if (b)  visiblePlots.add(vp);
        else visiblePlots.remove(vp);


        mp.calcGlobals();
        mp.setyMaxLimit(mp.getYMax());
        mp.setyMinLimit(mp.getYMin());
        mp.calculateCharts();
    }


    public void setProgressStart(int progressStart) {
        this.progressStart = progressStart;
    }

    public void setProgressEnd(int progressEnd) {
        this.progressEnd = progressEnd;
    }

    private void setProgressStartEnd(int s, int e) {
        this.progressStart = s;
        this.progressEnd = e;
    }

    private MyScrollView scrollView;
    public void setScrollView(MyScrollView scrollView) {
        this.scrollView = scrollView;
    }

    public interface ProgressChangedListener{
        void onStartProgressChanged(View v, int p1, int p2);
        void onEndProgressChanged(View v, int p1, int p2);
        void onOffsetProgressChanged(View v, int p1, int p2);
        void onStopChanging(View v, int p1, int p2);
    }
}


