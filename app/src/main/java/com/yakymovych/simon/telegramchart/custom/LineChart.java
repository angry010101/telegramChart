package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class LineChart extends View {
    public List<Integer> x,px,prx;
    public List<Double> y,py,pry;
    private int start,end;
    private int dividersCount = 6;
    private int y_threshold = 5;
    private final int y_stats_offset=100;
    private boolean drawToTop = false;
    private final double y_real_threshold = 1.85;

    public void setDividersCount(int dividersCount) {
        this.dividersCount = dividersCount;
        this.invalidate();
    }

    public void setStart(int start) {
        this.start = start;

        Log.d("STARTING: ","STARTED: " + this.start);
        removeStartEnd();

        this.invalidate();
    }

    public void setEnd(int end) {
        this.end = end;

        removeStartEnd();
        this.invalidate();
    }


    private void removeStartEnd(){
        int start = this.start;
        int end = this.end;
        prx = x.subList(start,end);
        pry = y.subList(start,end);

        //        prx = new ArrayList<>(x);
//        pry = new ArrayList<>(y);
//        Log.d("SIZE: ",prx.size() + "");
//        for (int i =0;i<start-1;i++){
//            prx.remove(0);
//            pry.remove(0);
//        }
//        for (int i =prx.size()-1;i>end;i--){
//            prx.remove(i);
//            pry.remove(i);
//        }
    }
    public LineChart(Context context) {
        super(context);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private double f(int x){
        return Math.random()*10+10;
    }
    private void init(){
        x = new ArrayList<Integer>();

        y = new ArrayList<>();
        for (int i =0;i<100;i++){
            x.add(i);
            y.add(f(i));
        }
        prx = new ArrayList<>(x);
        pry = new ArrayList<>(y);
        start=0;
        end=x.size();



        xmin = Collections.min(prx);
        xmax = Collections.max(prx);
        ymin = Collections.min(pry);
        ymax = Collections.max(pry);
    }



    private boolean isFingerDown = false;
    private int stats_x,stats_y=100;
    private int stats_w=250,stats_h=160;
    private int stats_offset=20;
    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        Log.d("VIEW: ","TOUCH");
        int x = (int) event.getX();
        int y = (int) event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isFingerDown = true;
                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                isFingerDown = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFingerDown){
                    y_threshold = (int)(((ymax-ymin)/2)*(y_real_threshold));
                    Log.d("SEARCH: ","" + x + " \n"+ this.x);
                    int w = this.getWidth();
                    double t = ((double)((end-start))/this.getWidth());
                    int nearest = findNearestFor((int)(x*t));
                    stats_x = (int) (nearest*w/(end-start-1));
                    stats_y = y_stats_offset;
                    drawToTop = false;

                    double y_intersection = this.pry.get(nearest);
                    //Log.d("VIEW: ", " " + this.prx.get(nearest) +" "+this.pry.get(nearest) + " " + y_threshold);
                    if (y_intersection>(y_threshold+ymin) ){
                        stats_y = (int)(y_threshold+ymin)+stats_h+y_stats_offset;
                        drawToTop = true;
                    }
                    this.invalidate();
                }
                break;
            default:
                Log.d("VIEW: ","HAPPENED");
        }
        invalidate();
        return true;
        //return super.onTouchEvent(event);
    }

    private int findNearestFor(int x) {
        //List<Integer> prcx = this.x.subList(start,end);
        Log.d("VIEW: ","START: " +start + " END: " +end+ "CURR: " + this.x.indexOf(x));
        return this.x.indexOf(x); //+start;
    }

    int xmin,xmax;
    double ymin,ymax;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.MAGENTA);
        paint.setTextSize(30);
        Rect rect = new Rect();
        rect.left = 0;
        rect.right = getWidth();
        rect.top = 0;
        rect.bottom = getHeight();



        px = new ArrayList<Integer>();
        py = new ArrayList<>();



        xmin = Collections.min(prx);
        xmax = Collections.max(prx);
        ymin = Collections.min(pry);
        ymax = Collections.max(pry);

        y_threshold = (int)((ymax-ymin)/2);
        int h = this.getHeight();
        double kx = (double)(this.getWidth())/(xmax-xmin);
        double ky = (double)(this.getHeight())/(ymax-ymin);
        for (int xi : prx){
            px.add((int)((xi-xmin)*kx));
        }
        for (Double yi : pry){

            py.add((h-((yi-ymin))*ky));
        }
        for (int i=0;i<prx.size()-1;i++ ){
            canvas.drawLine(px.get(i),py.get(i).intValue(),px.get(i+1),py.get(i+1).intValue(),paint);
            //canvas.drawText("x: " + x.get(i) + " y: " + y.get(i),15+ px.get(i),py.get(i),paint);
        }

        this.drawXAsis(canvas,paint);
        this.drawYAsis(canvas,paint);
        this.drawXDividers(canvas,paint);
        if (isFingerDown){
            this.drawStats(canvas,paint);
        }
        //canvas.drawRect(rect, paint);
    }

    private void drawStats(Canvas canvas, Paint paint) {
        //COULD BE BETTER
        canvas.drawRect(new Rect(stats_x,stats_y,stats_x+stats_w,stats_y+stats_h),paint);
        if (!drawToTop){
            canvas.drawLine(stats_x,this.getHeight(),stats_x,stats_y+stats_h,paint);
        }
        else {

            canvas.drawLine(stats_x,(int)(y_threshold+ymin)+stats_h+y_stats_offset,
                    stats_x,0,paint);
        }
    }

    private void drawXDividers(Canvas canvas, Paint paint) {
        int h = this.getHeight();
        double t = (double)((ymax-ymin))/dividersCount;
        for (int i=0,k=dividersCount;i<h || k>0;i+= h/dividersCount,k--){
            canvas.drawLine(0,i,this.getWidth(),i,paint);
            canvas.drawText(""+(t*k),0,i,paint);
        }
    }

    private void drawXAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,this.getHeight(),this.getWidth(),this.getHeight(),paint);
    }

    private void drawYAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,0,0,this.getHeight(),paint);
    }
}
