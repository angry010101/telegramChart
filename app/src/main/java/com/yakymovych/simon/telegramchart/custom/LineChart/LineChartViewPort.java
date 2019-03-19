package com.yakymovych.simon.telegramchart.custom.LineChart;

import android.util.Log;
import android.view.MotionEvent;

import com.yakymovych.simon.telegramchart.Model.local.Plot;

import java.util.List;

public class LineChartViewPort {
    int w,h;
    int start,end;

    private int stats_w=250,stats_h=160;
    LineChart view;
    private int stats_x,stats_y=100;
    private int stats_offset=20;
    private int y_threshold = 5;

    private final double y_real_threshold = 1.85;

    private final int intersection_radius = 10;
    private int stats_x_position = 0;
    private double stats_y_intersection = 0;


    private final int y_stats_offset=100;
    private boolean drawToTop = false;

    int xmin,xmax;
    double ymin,ymax;
    private double unitPerPx;

    LineChartViewPort(LineChart view,int w,int h){
        this.w = w;
        this.h = h;
        this.view = view;
    }

    public void setStart(int start) {
        this.start = start;
        unitPerPx = ((double)((end-start))/w);
    }


    public void setEnd(int end) {
        this.end = end;
        unitPerPx = ((double)((end-start))/w);
    }

    public void setStartAndEnd(int start,int end){
        this.start = start;
        this.end = end;
        unitPerPx = ((double)((end-start))/w);
    }

    public boolean isFingerDown = false;
    private final int stats_draw_left_threshold = 50;
    private int stats_draw_right_threshold;


    public int findNearestFor(Plot p, long x) {
        List<Long> prcx = p.x.subList(start,end);
        Log.d("SEARCH", "prcx " + start + "END: " + end);
        Log.d("SEARCH", "prcx " + prcx.toString() + " \nx = " + x + " zero: " + prcx.get(0));
        return prcx.indexOf(x+prcx.get(0));
    }

    public synchronized boolean onTouchEvent(MotionEvent event) {
        Log.d("VIEW: ","TOUCH");
        long x = (long) event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isFingerDown = true;
                view.invalidate();
                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                isFingerDown = false;
                view.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                stats_draw_right_threshold = w-stats_w-50;
                Log.d("VIEW: ","WIDTH" + x + " " + stats_draw_right_threshold + " " + stats_draw_left_threshold);
                if (isFingerDown && x < stats_draw_right_threshold && x > stats_draw_left_threshold ){
                    //y_threshold = (int)(((ymax-ymin)/2)*(y_real_threshold));
                    double t = ((double)((end-start))/w);
                    view.handleMove((long)(x*t),w);

                }
                break;
            default:
                Log.d("VIEW: ","HAPPENED");
        }
        return true;
    }
}