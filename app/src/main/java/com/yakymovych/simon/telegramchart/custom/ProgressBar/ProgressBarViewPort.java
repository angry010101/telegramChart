package com.yakymovych.simon.telegramchart.custom.ProgressBar;

import android.util.Log;
import android.view.MotionEvent;

import com.yakymovych.simon.telegramchart.Utils.ViewPort.BaseViewPortUtils;

public class ProgressBarViewPort extends BaseViewPortUtils {
    int startpos,endpos;
    boolean isChangingStart = false;
    boolean isChangingEnd = false;
    boolean isChangingOffset = false;
    int w,h;
    int minOffsetPx = 80;
    private final double unitPerPx;
    private final double pxPerUnit;


    public void setStartpos(int startpos) {
        this.startpos = this.getProgressStartPx(startpos);
    }

    public void setEndpos(int endpos) {
        this.endpos = this.getProgressEndPx(endpos);
    }

    public void setMinOffsetPx(int minOffsetElements) {
        this.minOffsetPx = (int) (minOffsetElements * ((double)endpos-startpos)/w);
    }

    int delta = 25;
    int delta_o=50;

    int borderWidth = 16;

    public ProgressBarViewPort(int width, int height,int progressLeft,int progressRight,int minOffsetElements) {
        super();
        w = width;
        h = height;
        startpos = getProgressStartPx(progressLeft);
        endpos = getProgressEndPx(progressRight);
        setMinOffsetPx(minOffsetElements);
        unitPerPx = 100.0/w;
        pxPerUnit = ((double)(w)/100);
    }


    public synchronized boolean onTouchEvent(GraphProgressBar view, MotionEvent event) {
        int x = (int) event.getX();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(x-startpos)<delta){
                    isChangingStart = true;
                }
                else if (Math.abs(x-endpos)<delta){
                    isChangingEnd = true;
                }
                else
                if (Math.abs(x-(startpos+endpos)/2)<delta_o && !isChangingEnd && !isChangingStart){
                    isChangingOffset = true;
                }
                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                isChangingStart = false;
                isChangingEnd = false;
                isChangingOffset = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isChangingStart){
                    handleStartMovement(view,event);
                }
                else if (isChangingEnd){
                    handleEndMovement(view,event);
                }
                else if (isChangingOffset){
                    handleOffsetMovement(view,event);
                }
                break;
            default:
                 break;
        }
        view.invalidate();
        return true;
    }
    public void handleOffsetMovement(GraphProgressBar view,MotionEvent event){
        float x = event.getX();
        boolean direction = x-(startpos+endpos)/2 > 0;
        if ((endpos>=w)
                && direction||
                (startpos <=0 && !direction))
            return;


        int m = (startpos+endpos);

        int d = (int) ((x-(double)m/2)*(unitPerPx));

        view.handleOffsetMovement(d,direction);
    }


    public void handleStartMovement(GraphProgressBar view, MotionEvent event){
        float x = event.getX();
        boolean direction = x-endpos > 0;

        if ((startpos<=0 && direction) || (endpos>=w-borderWidth
                && direction) ||
                (Math.abs(endpos-startpos) < minOffsetPx && direction)) return;

        //convert to pb units
        startpos = (int)event.getX();
        int moveToProgress =  (int) (((double)(event.getX())/(w))*100);
        view.handleStartMovement(moveToProgress);
    }

    public void handleEndMovement(GraphProgressBar view, MotionEvent event){
        float x = event.getX();
        boolean direction = x-endpos > 0;
        if ((endpos>=w-borderWidth && direction) || (endpos<=borderWidth  && !direction) ||
                (Math.abs(endpos-startpos) < minOffsetPx && !direction)) return;


        int moveToProgress =  (int)(((x-endpos)*unitPerPx));
        endpos= (int)event.getX();
        view.handleEndMovement(moveToProgress);
    }

    public int getProgressStartPx(int progressStart) {
        return (int)(((double)(progressStart))*pxPerUnit);
    }

    public int getProgressEndPx(int progressEnd) {
        //TODO ???
        double k1 = ((double)(w)/100);
        //double k1 = pxPerUnit;
        //Log.d("TEST ", "MSG: " + pxPerUnit + " " + k1);
        return ((int)((((((double)(progressEnd))))*k1))) - borderWidth;
    }
}