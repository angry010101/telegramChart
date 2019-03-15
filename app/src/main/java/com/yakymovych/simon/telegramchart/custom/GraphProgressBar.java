package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GraphProgressBar extends View {

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    boolean isChangingStart = false;
    boolean isChangingEnd = false;
    int progress=40,progressEnd=80;

    int offsetProgressPx = 200;
    int offsetProgressElems = 20;
    int minOffsetElems = 3;
    boolean isChangingOffset = false;
    private int progressStartPx =0;
    private int progressEndPx =offsetProgressElems;
    int startpos,endpos;
    int height;
    int delta = 25,delta_o=20;


    public int getProgressStartPx() {
        return (int)((((double)(progress))/100)*this.getWidth());
    }

    public int getProgressEndPx() {
        return ((int)((((((double)(progressEnd))/100))*this.getWidth())));
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


    private void init(){
        paint.setColor(Color.MAGENTA);
        paint.setTextSize(30);
        height = this.getHeight();

        startpos = getProgressStartPx();
        endpos = getProgressEndPx();
        Log.d("VIEW: ","INIT: " + startpos+ " " + endpos);


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
            if (d >= progressEnd) {
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
            if (progressEnd+d < progress) {
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

        int view_offset_s = getProgressStartPx();
        int view_offset_e = getProgressEndPx();
        int s = view_offset_s;
        int e = view_offset_e;
        canvas.drawCircle(s,h2,25,paint);
        canvas.drawCircle(e,h2,25,paint);
    }
    public interface ProgressChangedListener{
        public void onStartProgressChanged(View v,int p1,int p2,int offset);
        public void onEndProgressChanged(View v,int p1,int p2,int offset);
        public void onOffsetProgressChanged(View v,int p1,int p2,int offset);
    }
}


