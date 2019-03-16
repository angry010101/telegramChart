package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.ArrayList;
import java.util.List;

public class GraphProgressBar extends View {
    private MathPlot mp ;
    private List<Plot> plots = new ArrayList<>();

    Paint grayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    boolean isChangingStart = false;
    boolean isChangingEnd = false;
    int progress=40,progressEnd=80;

    int offsetProgressPx = 200;
    int offsetProgressElems = 20;
    int minOffsetElems = 6;
    boolean isChangingOffset = false;
    private int progressStartPx =0;
    private int progressEndPx =offsetProgressElems;
    int startpos,endpos;
    int height,width;
    int delta = 25,delta_o=40;
    private int topMargin = 8;

    public int getProgressStartPx() {
        return (int)((((double)(progress))/100)*this.getWidth());
    }

    public int getProgressEndPx() {
        return ((int)((((((double)(progressEnd))/100))*this.getWidth()))) - borderWidth;
    }

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
        mp = new MathPlot(width,height,topMargin);
        this.invalidate();

    }

    private void init(){
        paint.setColor(Color.GREEN);
        paint.setTextSize(30);
        startpos = getProgressStartPx();
        endpos = getProgressEndPx();

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
        bluePaint.setAlpha(20);
        //grayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        bluePaint.setAntiAlias(true);


    }



    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        Log.d("VIEW: ","TOUCH");
        int x = (int) event.getX();
        int y = (int) event.getX();
        switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if (Math.abs(x-startpos)<delta){
                        isChangingStart = true;
                    }
                    else if (Math.abs(x-endpos)<delta){
                        Log.d("VIEW: ","CHANIGNG END");
                        isChangingEnd = true;
                    }
                    else
                    if (Math.abs(x-(startpos+endpos)/2)<delta_o && !isChangingEnd && !isChangingStart){
                        Log.d("VIEW: ","CHANIGNG OFFSET");
                        isChangingOffset = true;
                    }
                    Log.d("VIEW: ","DOWN " + "x: " +x +  " " + endpos + "de:" + delta);
                    break;
                case MotionEvent.ACTION_UP: // отпускание
                case MotionEvent.ACTION_CANCEL:
                    isChangingStart = false;
                    isChangingEnd = false;
                    isChangingOffset = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isChangingStart){
                        handleStartMovement(event);
                    }
                    else if (isChangingEnd){
                        handleEndMovement(event);
                    }
                    else if (isChangingOffset){
                        handleOffsetMovement(event);
                    }
                    break;
                    default:
                        Log.d("VIEW: ","HAPPENED");
        }
        invalidate();
        return true;
        //return super.onTouchEvent(event);
    }




    private void handleStartMovement(MotionEvent event){
        float x = event.getX();
        boolean direction = x-getProgressEndPx() > 0;
        if ((progress<=0 && direction) || (progress>=100  && direction) ||
                (Math.abs(progressEnd-progress) < minOffsetElems && direction)) return;
        int d =  (int) (((double)(event.getX())/(this.getWidth()))*100);
        if (d <= 0) {
            progress = 0;
        }
        else {
            if (d >= progressEnd-minOffsetElems) {
                progress = progressEnd-minOffsetElems;
            }
            else progress = d;
        }

        startpos = (int)event.getX();
        if (progressChangedListener != null){
            progressChangedListener.onStartProgressChanged(
                    this,progress, getProgressEndPx(),
                    offsetProgressElems);
        }
        Log.d("VIEW: ","progress: " + progress);
    }

    private void handleOffsetMovement(MotionEvent event){
        float x = event.getX();

        int st = getProgressStartPx();
        int end =getProgressEndPx();

        boolean direction = x-(st+end)/2 > 0;


        Log.d("VIEW: ","progress: " + getProgressStartPx() + " " + getProgressEndPx());
        if ((getProgressEndPx()>=this.getWidth())
                && direction||
                (getProgressStartPx() <=0 && !direction))
            return;

        //fix
        int m = (getProgressEndPx()+getProgressStartPx());
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
        if (progressChangedListener != null){
            progressChangedListener.onOffsetProgressChanged(
                    this,progress, progressEnd,
                    offsetProgressElems);
        }

        this.invalidate();
        Log.d("VIEW: ","delta1: " + d);

        Log.d("VIEW: ","progress: " + progress);
    }

    private void handleEndMovement(MotionEvent event){
        Log.d("VIEW: ","HANDLING END");
        float x = event.getX();
        boolean direction = x-getProgressEndPx() > 0;
        if ((progressEnd>=100 && direction) || (progressEnd<=0  && !direction) ||
                (Math.abs(progressEnd-progress) < minOffsetElems && !direction)) return;
        int d =  (int)(((x-getProgressEndPx())*100.0/this.getWidth()));
        if (progressEnd+d > 100) {
            progressEnd = 100;
        }
        else {
            if (progressEnd+d < progress+minOffsetElems) {
                progressEnd = progress+minOffsetElems;
            }
            else progressEnd += d;
        }
        //progressEndPx = (int) (((double)(event.getX())/(this.getWidth()))*100);
        //progressEnd = (int) (((double)(event.getX())/(this.getWidth()))*100);
        endpos= (int)event.getX();

        if (progressChangedListener != null){
            progressChangedListener.onEndProgressChanged(
                    this,progress, progressEnd,
                    offsetProgressElems);
        }
        Log.d("VIEW: ","progress: " + progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int h2 = this.getHeight()/2;

        startpos = getProgressStartPx();
        endpos = getProgressEndPx();


        drawBackground(canvas);

//        int view_offset_s = getProgressStartPx();
//        int view_offset_e = getProgressEndPx();
//        int s = view_offset_s;
//        int e = view_offset_e;
//        canvas.drawCircle(s,h2,25,paint);
//        canvas.drawCircle(e,h2,25,paint);

    }

    private void drawBackground(Canvas canvas) {

        mp.drawCharts(plots,canvas,paint);
        this.drawSlider(canvas,grayPaint);
    }

    private final int borderWidth=16;
    private int sliderTopBorder=4;
    private void drawSlider(Canvas canvas, Paint grayPaint) {
        int p = getProgressStartPx();
        int e = getProgressEndPx();
        canvas.drawRect(0,0,p,height,grayPaint);
        canvas.drawRect(e,0,width,height,grayPaint);

        canvas.drawRect(p+borderWidth,0,e,sliderTopBorder,bluePaint);
        canvas.drawRect(p+borderWidth,height-sliderTopBorder,e,height,bluePaint);


        canvas.drawRect(p,0,p+borderWidth,height,bluePaint);
        canvas.drawRect(e,0,e+borderWidth,height,bluePaint);
    }

    public interface ProgressChangedListener{
        public void onStartProgressChanged(View v,int p1,int p2,int offset);
        public void onEndProgressChanged(View v,int p1,int p2,int offset);
        public void onOffsetProgressChanged(View v,int p1,int p2,int offset);
    }
}


