package com.yakymovych.simon.telegramchart.custom.ProgressBar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphProgressBar extends View {
    private MathPlot mp ;
    private List<Plot> plots = new ArrayList<>();
    ProgressBarViewPort viewPort;
    ProgressBarDrawManager progressBarDrawManager;
    int progressStart =0,progressEnd=100;
    private int topMargin = 8;
    int minOffsetElems = 6;
    private Set<Integer> visiblePlots =new HashSet<>();
    private ProgressChangedListener progressChangedListener = null;

    public void setVisiblePlots(Set<Integer> visiblePlots) {
        this.visiblePlots = visiblePlots;
    }


    public void setProgressChangedListener(ProgressChangedListener progressChangedListener) {
        this.progressChangedListener = progressChangedListener;
    }

    public GraphProgressBar(Context context) {
        super(context);
        init();
    }

    public GraphProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPlots(List<Plot> plots) {
        this.plots = plots;
    }

    private void initSizes(){
        int width = this.getWidth();
        int height= this.getHeight();
        this.viewPort = new ProgressBarViewPort(this,width,height, progressStart,progressEnd,minOffsetElems);
        this.mp = new MathPlot(width,height,topMargin);
        this.progressBarDrawManager = new ProgressBarDrawManager(mp,width,height);

        this.invalidate();
    }

    private void init(){
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSizes();
            }
        });
    }


    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
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
                    this, progressStart, viewPort.getProgressEndPx(progressEnd),
                    1);
        }
    }

    public void handleOffsetMovement(int d,boolean direction){
        if (progressEnd+d > 100 && direction) {
            d=100-progressEnd;
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
                    this, progressStart, progressEnd,
                    1);
        }
        this.invalidate();
    }

    public void handleEndMovement(int moveToProgress){
        if (progressEnd+moveToProgress > 100) {
            progressEnd = 100;
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
                    this, progressStart, progressEnd,
                    1);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        progressBarDrawManager.draw(canvas,plots,visiblePlots,
                viewPort.getProgressStartPx(progressStart)
                ,viewPort.getProgressEndPx(progressEnd));
    }


    public interface ProgressChangedListener{
        public void onStartProgressChanged(View v,int p1,int p2,int offset);
        public void onEndProgressChanged(View v,int p1,int p2,int offset);
        public void onOffsetProgressChanged(View v,int p1,int p2,int offset);
    }
}


