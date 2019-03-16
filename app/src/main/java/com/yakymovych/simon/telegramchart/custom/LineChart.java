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
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.ArrayList;
import java.util.List;

public class LineChart extends View {
    public List<Integer> x,px,prx;
    public List<Double> y,py,pry;
    private int start,end;
    private MathPlot mp;
    public List<Plot> plots;
    private int dividersCount = 6;
    private int y_threshold = 5;
    private final int y_stats_offset=100;
    private boolean drawToTop = false;
    private final double y_real_threshold = 1.85;
    private final int intersection_radius = 10;
    private int stats_x_position = 0;
    private double stats_y_intersection = 0;
    private int width;
    private int height;
    LineChartListener lineChartListener;
    private int topMargin = 8;

    public void setLineChartListener(LineChartListener lineChartListener) {
        this.lineChartListener = lineChartListener;
    }

    public void setPlots(List<Plot> plots) {
        this.plots = plots;
        mp.setPlots(plots);
        this.invalidate();
    }

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

    public void setStartAndEnd(int start,int end) {
        this.start = start;
        this.end = end;
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
//        for (Plot p : plots){
//            prx = x.subList(start,end);
//            pry = y.subList(start,end);
//        }
//        mp.setPlots(plots);
        mp.setStartAndEnd(start,end);
        this.invalidate();
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


    private void initSizes(){
        this.width = this.getWidth();
        this.height = this.getHeight();
        mp = new MathPlot(width,height,topMargin);
        if (lineChartListener != null){
            lineChartListener.onDidInit();
        }
        this.invalidate();
    }

    private void init(){
        x = new ArrayList<>();

        y = new ArrayList<>();

        start=0;
        end=x.size();


        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSizes();
            }
        });

    }



    private boolean isFingerDown = false;
    private int stats_x,stats_y=100;
    private int stats_w=250,stats_h=160;
    private int stats_offset=20;

    private final int stats_draw_left_threshold = 50;
    private int stats_draw_right_threshold;

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
                stats_draw_right_threshold = this.getWidth()-stats_w-50;
                Log.d("VIEW: ","WIDTH" + x + " " + stats_draw_right_threshold + " " + stats_draw_left_threshold);
                if (isFingerDown && x < stats_draw_right_threshold && x > stats_draw_left_threshold ){
                    y_threshold = (int)(((ymax-ymin)/2)*(y_real_threshold));
                    int w = this.getWidth();
                    double t = ((double)((end-start))/this.getWidth());

                    int nearest = mp.findNearestFor(plots.get(0),(int)(x*t));

                    stats_x = (int) (nearest*w/(end-start-1));
                    stats_x_position = nearest;
                    stats_y = y_stats_offset;
                    drawToTop = false;


                    //TODO pry is obsolete there is no calculations
                    //but buggy behavior
                    //statictics doesn't include y posittion

                    double y_intersection = this.plots.get(0).y.get(nearest);

                    Log.d("VIEW: ","HAPPENED: " + y_intersection + " max: " + ymax);

                    stats_y_intersection = y_intersection;

                    Log.d("VIEW: ","HAPPENED: " + stats_y_intersection);
//                    stats_y_intersection = (int) (y_intersection);

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


        y_threshold = (int)((ymax-ymin)/2);
        //FIX IT
        mp.setPlots(plots);
        mp.setStartAndEnd(start,end);
        mp.drawCharts(canvas,paint);

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

        drawIntersection(canvas,paint);
    }

    private void drawIntersection(Canvas canvas, Paint paint) {
        canvas.drawCircle(stats_x, (int)(this.getHeight()-(stats_y_intersection-ymin)*((double)this.getHeight())/(ymax-ymin)),intersection_radius,paint);
    }

    private void drawXDividers(Canvas canvas, Paint paint) {
        int h = this.getHeight();
        double t = (double)((ymax-ymin))/dividersCount;
        for (int i=0,k=dividersCount;i<h || k>0;i+= h/dividersCount,k--){
            canvas.drawLine(0,i,this.getWidth(),i,paint);
            canvas.drawText(""+(t*(k+1)),0,i,paint);
        }
    }

    private void drawXAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,this.getHeight(),this.getWidth(),this.getHeight(),paint);
    }

    private void drawYAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,0,0,this.getHeight(),paint);
    }

    public interface LineChartListener{
        void onDidInit();
    }
}
