package com.yakymovych.simon.telegramchart.custom.ProgressBar;

import android.util.Log;
import android.view.MotionEvent;


public class ProgressBarViewPort {
    int startpos;
    int endpos;
    boolean isChangingStart = false;
    boolean isChangingEnd = false;
    boolean isChangingOffset = false;
    int w,h;
    int minOffsetPx = 80;
    private final double unitPerPx;
    private final double pxPerUnit;
    GraphProgressBar view;
    int progressMax;

    public void setStartpos(int startpos) {
        this.startpos = this.getProgressStartPx(startpos);
    }

    public void setEndpos(int endpos) {
        this.endpos = this.getProgressEndPx(endpos);
    }

    public void setMinOffsetPx(int minOffsetElements) {
        this.minOffsetPx = (int) (minOffsetElements * ((double)endpos-startpos)/w);
    }

    int delta = 40;
    int delta_o=60;
    int borderWidth = 16;

    public ProgressBarViewPort(GraphProgressBar view,int width, int height,int progressLeft,int progressRight,int progressMax, int minOffsetElements) {
        super();
        w = width;
        h = height;
        this.progressMax = progressMax;
        this.setStartpos(progressLeft);
        this.setEndpos(progressRight);
        setMinOffsetPx(minOffsetElements);
        unitPerPx = (double)(progressMax)/w;
        pxPerUnit = ((double)(w)/progressMax);
//
//        this.delta = w/6;
//        this.delta_o = w/5;

        this.view = view;
    }


    public boolean onTouchEvent(final MotionEvent event) {
        Log.d("PROGRESSBAR","TOUCH EVENT");
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
                this.onStopChanging();
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
                 break;
        }
        view.invalidate();
        return true;
    }

    private void onStopChanging() {
        view.handleStopChanging();
    }

    public void handleOffsetMovement(MotionEvent event){
        float x = event.getX();
        boolean direction = x-(startpos+endpos)/2 > 0;
        if ((endpos>=w-borderWidth)
                && direction||
                (startpos <=0 && !direction))
            return;

        int m = (startpos+endpos);
        int d = (int) ((x-(double)m/2)*(unitPerPx));
        view.handleOffsetMovement(d,direction);
    }


    public void handleStartMovement(MotionEvent event){
        float x = event.getX();
        boolean direction = x-endpos > 0;

        if ((startpos<=0 && direction) || (endpos>=w-borderWidth
                && direction) ||
                (Math.abs(endpos-startpos) < minOffsetPx && direction)) return;

        //convert to pb units
        startpos = (int)event.getX();
        int moveToProgress =  (int) (((double)(event.getX())/(w))*progressMax);
        view.handleStartMovement(moveToProgress);
    }

    public void handleEndMovement(MotionEvent event){
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
        double k1 = ((double)(w)/progressMax);
        Log.d("ACTIONDOWN","K1: " + k1 + " " + w + " " + progressMax);
        //double k1 = pxPerUnit;
        //Log.d("TEST ", "MSG: " + pxPerUnit + " " + k1);
        return ((int)((((((double)(progressEnd))))*k1))) - borderWidth;
    }
}