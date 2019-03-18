package com.yakymovych.simon.telegramchart.custom.ProgressBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;
import com.yakymovych.simon.telegramchart.Utils.ViewPort.ProgressBarViewPort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphProgressBar extends View {
    private MathPlot mp ;
    private List<Plot> plots = new ArrayList<>();
    ProgressBarViewPort viewPort;
    Paint grayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    ProgressBarDrawManager progressBarDrawManager;
    int progress=40,progressEnd=80;

    int offsetProgressElems = 20;
    int minOffsetElems = 6;

    private Set<Integer> visiblePlots =new HashSet<>();

    public void setVisiblePlots(Set<Integer> visiblePlots) {
        this.visiblePlots = visiblePlots;
    }

    int height,width;

    private int topMargin = 8;

    private ProgressChangedListener progressChangedListener = null;

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
        this.width = this.getWidth();
        this.height= this.getHeight();
        this.viewPort = new ProgressBarViewPort(width,height,progress,progressEnd);
        this.mp = new MathPlot(width,height,topMargin);
        this.progressBarDrawManager = new ProgressBarDrawManager(mp,width,height);

        this.invalidate();
    }

    private void init(){
        paint.setColor(Color.GREEN);
        paint.setTextSize(30);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSizes();
            }
        });

        grayPaint.setColor(Color.GRAY);
        grayPaint.setAlpha(60);
        //grayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        grayPaint.setAntiAlias(true);

        bluePaint.setColor(Color.BLUE);
        bluePaint.setAlpha(40);
        //grayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        bluePaint.setAntiAlias(true);


    }



    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        return viewPort.onTouchEvent(this,event);
    }

    public void handleStartMovement(int moveTo){
        if (moveTo <= 0) {
            progress = 0;
        }
        else {
            if (moveTo >= progressEnd-minOffsetElems) {
                progress = progressEnd-minOffsetElems;
            }
            else progress = moveTo;
        }

        viewPort.setStartpos(progress);

        if (progressChangedListener != null){
            progressChangedListener.onStartProgressChanged(
                    this,progress, viewPort.getProgressEndPx(progressEnd),
                    offsetProgressElems);
        }
        Log.d("VIEW: ","progress: " + progress);
    }

    public void handleOffsetMovement(MotionEvent event){
        float x = event.getX();

        int st = viewPort.getProgressStartPx(progress);
        int end =viewPort.getProgressEndPx(progressEnd);

        boolean direction = x-(st+end)/2 > 0;


        if ((end>=this.getWidth())
                && direction||
                (st <=0 && !direction))
            return;


        int m = (st+end);
        int d = (int) ((x-(double)m/2)*(100.0/this.getWidth()));

        if (progressEnd+d > 100 && direction) {
            d=100-progressEnd;
        }
        else {
            if (progress+d<0 && !direction) {
                d = -progress;
            }
        }

        progress += d;
        progressEnd += d;

        viewPort.setStartpos(progress);
        viewPort.setEndpos(progressEnd);
        if (progressChangedListener != null){
            progressChangedListener.onOffsetProgressChanged(
                    this,progress, progressEnd,
                    offsetProgressElems);
        }

        this.invalidate();
        Log.d("VIEW: ","delta1: " + d);

        Log.d("VIEW: ","progress: " + progress);
    }

    public void handleEndMovement(int moveToProgress){

        if (progressEnd+moveToProgress > 100) {
            progressEnd = 100;
        }
        else {
            if (progressEnd+moveToProgress < progress+minOffsetElems) {
                progressEnd = progress+minOffsetElems;
            }
            else progressEnd += moveToProgress;
        }
        viewPort.setEndpos(progressEnd);
        if (progressChangedListener != null){
            progressChangedListener.onEndProgressChanged(
                    this,progress, progressEnd,
                    offsetProgressElems);
        }
        Log.d("VIEW: ","progress: " + progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        progressBarDrawManager.draw(canvas,plots,visiblePlots,
                viewPort.getProgressStartPx(progress)
                ,viewPort.getProgressEndPx(progressEnd));
    }


    public interface ProgressChangedListener{
        public void onStartProgressChanged(View v,int p1,int p2,int offset);
        public void onEndProgressChanged(View v,int p1,int p2,int offset);
        public void onOffsetProgressChanged(View v,int p1,int p2,int offset);
    }
}


