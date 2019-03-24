package com.yakymovych.simon.telegramchart.custom.ProgressBar;

import android.view.MotionEvent;


class ProgressBarViewPort {
    private int startpos;
    private int endpos;
    private boolean isChangingStart = false;
    private boolean isChangingEnd = false;
    private boolean isChangingOffset = false;
    private final int w;
    private final int h;
    private int minOffsetPx = 80;
    private final double unitPerPx;
    private final double pxPerUnit;
    private final GraphProgressBar view;
    private final int progressMax;

    public void setStartpos(int startpos) {
        this.startpos = this.getProgressStartPx(startpos);
    }

    public void setEndpos(int endpos) {
        this.endpos = this.getProgressEndPx(endpos);
    }

    private void setMinOffsetPx(int minOffsetElements) {
        this.minOffsetPx = (int) (minOffsetElements * ((double)endpos-startpos)/w);
    }

    private final int delta = 30;
    private final int delta_o=40;
    private final int borderWidth = 16;

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
        int x = (int) event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(x-startpos)<delta){
                    isChangingStart = true;
                    view.handleStartChanging();
                }
                else if (Math.abs(x-endpos)<delta){
                    isChangingEnd = true;
                    view.handleStartChanging();
                }
                else
                if (Math.abs(x-(startpos+endpos)/2)<delta_o && !isChangingEnd && !isChangingStart){
                    isChangingOffset = true;
                    view.handleStartChanging();
                }
                break;
            case MotionEvent.ACTION_UP: // отпускание
            //case MotionEvent.ACTION_CANCEL:
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

    private void handleOffsetMovement(MotionEvent event){
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


    private void handleStartMovement(MotionEvent event){
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

    private void handleEndMovement(MotionEvent event){
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
        //double k1 = pxPerUnit;
        //Log.d("TEST ", "MSG: " + pxPerUnit + " " + k1);
        return ((int)((((((double)(progressEnd))))*k1))) - borderWidth;
    }
}