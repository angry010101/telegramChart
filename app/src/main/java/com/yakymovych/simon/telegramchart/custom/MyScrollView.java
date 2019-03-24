package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView implements GestureDetector.OnGestureListener {
    private static final float SWIPE_MIN_DISTANCE = 40;
    private static final float SWIPE_MIN_DISTANCE_Y = 250;
    private static final float SWIPE_THRESHOLD_VELOCITY = 200;
    boolean locked=false;
    private GestureDetectorCompat mDetector;
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    void init(){

        mDetector = new GestureDetectorCompat(this.getContext(),
                this);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (locked) return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        this.mDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

//
//            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MIN_DISTANCE_Y
//                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                this.setLocked(false);
//            }
//            // right to left swipe
//            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE
//                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                this.setLocked(true);
//            }
        if (e1 == null || e2 == null ) return  false;

            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MIN_DISTANCE_Y || Math.abs(e1.getX() - e2.getX())>SWIPE_MIN_DISTANCE)
            if (Math.abs(e1.getY() - e2.getY()) > Math.abs(e1.getX() - e2.getX() )) {
                this.setLocked(false);
            }
            else {
                this.setLocked(true);
            }
        return false;
    }
}
