package com.yakymovych.simon.telegramchart.Utils.ViewPort;

import android.util.Log;
import android.view.MotionEvent;

import com.yakymovych.simon.telegramchart.custom.ProgressBar.GraphProgressBar;

public class ProgressBarViewPort extends BaseViewPortUtils {
    int startpos,endpos;
    boolean isChangingStart = false;
    boolean isChangingEnd = false;
    boolean isChangingOffset = false;
    int w,h;


    public void setStartpos(int startpos) {
        this.startpos = this.getProgressStartPx(startpos);
    }

    public void setEndpos(int endpos) {
        this.endpos = this.getProgressEndPx(endpos);
    }

    int delta = 25;
    int delta_o=50;

    int borderWidth = 16;

    public ProgressBarViewPort(int width, int height,int progressLeft,int progressRight) {
        super();
        w = width;
        h = height;

        startpos = getProgressStartPx(progressLeft);
        endpos = getProgressEndPx(progressRight);

    }


    public synchronized boolean onTouchEvent(GraphProgressBar view, MotionEvent event) {
        int x = (int) event.getX();

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
                    handleStartMovement(view,event);
                }
                else if (isChangingEnd){
                    handleEndMovement(view,event);
                }
                else if (isChangingOffset){
                    view.handleOffsetMovement(event);
                }
                break;
            default:
                 break;
        }
        view.invalidate();
        return true;
    }


    int minOffsetElems = 80;


    public void handleStartMovement(GraphProgressBar view, MotionEvent event){
        float x = event.getX();
        boolean direction = x-endpos > 0;
        //crunch
        if ((startpos<=0 && direction) || (endpos>=w-borderWidth
                && direction) ||
                (Math.abs(endpos-startpos) < minOffsetElems && direction)) return;

        //convert to pb units
        startpos = (int)event.getX();
        int moveToProgress =  (int) (((double)(event.getX())/(w))*100);
        view.handleStartMovement(moveToProgress);
    }

    public void handleEndMovement(GraphProgressBar view, MotionEvent event){
        float x = event.getX();
        boolean direction = x-endpos > 0;
        if ((endpos>=w-borderWidth && direction) || (endpos<=borderWidth  && !direction) ||
                (Math.abs(endpos-startpos) < minOffsetElems && !direction)) return;



        int moveToProgress =  (int)(((x-endpos)*100.0/w));
        endpos= (int)event.getX();
        view.handleEndMovement(moveToProgress);
    }

    public int getProgressStartPx(int progressStart) {
        return (int)((((double)(progressStart))/100)*w);
    } // this.getWidth()

    public int getProgressEndPx(int progressEnd) {
        return ((int)((((((double)(progressEnd))/100))*w))) - borderWidth;
    }
}