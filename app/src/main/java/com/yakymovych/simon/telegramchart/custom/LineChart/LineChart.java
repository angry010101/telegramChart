package com.yakymovych.simon.telegramchart.custom.LineChart;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
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

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LineChart extends View {
    private int start,end;
    private MathPlot mp;
    //private List<Plot> plots;


    LineChartDrawManager drawManager;
    LineChartViewPort viewPort;
    private Set<String> visiblePlots = new HashSet<>();
    private Chart chart;

    public void setVisiblePlots(Set<String> visiblePlots) {
        this.visiblePlots = visiblePlots;
        mp.setVisiblePlots(visiblePlots);
    }

    LineChartListener lineChartListener;
    private int topMargin = 20;
    int bottomMargin = 20;
    ValueAnimator newGraphAnimator;
    ValueAnimator heightAnimator;

    public void startAnimShow(String pos){
        List<Double> p1 = chart.columns.get(pos);
        this.setVisiblePlot(pos,true);
        PropertyValuesHolder pvhX=null;
        PropertyValuesHolder pvhY=null;
        float lcmaxy = Collections.max(p1.subList(mp.start,mp.end)).floatValue();

        if (lcmaxy > mp.getYMax())
            pvhX = PropertyValuesHolder.ofFloat("TRANSLATION_YMAX",mp.getYMax(), lcmaxy);
        float lcminy = Collections.min(p1.subList(mp.start,mp.end)).floatValue();
        if (lcminy < mp.getYMin())
            pvhY = PropertyValuesHolder.ofFloat("TRANSLATION_YMIN",mp.getYMin(),lcminy);


        if (pvhX != null){
            if (pvhY !=null){
                newGraphAnimator = ValueAnimator.ofPropertyValuesHolder(pvhX,pvhY);
            }
            else {
                newGraphAnimator = ValueAnimator.ofPropertyValuesHolder(pvhX);
            }
        }
        else {
            if (pvhY !=null){
                newGraphAnimator = ValueAnimator.ofPropertyValuesHolder(pvhY);
            }
            else {
                this.invalidate();
                return;
            }
        }

        newGraphAnimator.setDuration(1000);
        newGraphAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                beginAnimation(animation);
            }
        });
        newGraphAnimator.start();
    }

    public void setVisiblePlot(String pos, boolean b) {
        if (b) visiblePlots.add(pos);
        else visiblePlots.remove(pos);

        mp.setVisiblePlots(visiblePlots);
    }

    public void setLineChartListener(LineChartListener lineChartListener) {
        this.lineChartListener = lineChartListener;
    }


    public void setPlots(Chart c) {
        this.chart = c;
        //this.plots = plots;
        mp.setPlots(c);
        //this.setVisiblePlot(,true);
        this.invalidate();
    }


    public void setStart(int start) {
        this.start = start;
        removeStartEnd();
        this.invalidate();
    }

    public void setStartAndEnd(int start,int end) {
        this.start = start;
        this.end = end;
        removeStartEnd();
    }

    public void setEnd(int end) {
        this.end = end;
        removeStartEnd();
    }


    private void removeStartEnd(){
        int start = this.start;
        int end = this.end;
        mp.setStartAndEnd(start,end);
        viewPort.setStartAndEnd(start,end);
        this.animateHeight();
    }

    private void animateHeight() {
        if (heightAnimator != null && heightAnimator.isRunning()  ){
            //heightAnimator.pause();
            //return;
           // Log.d("HEIGHT","PAUSED" );
        }
        else {

        }
  //      float lcmaxy = mp.getYMax();
   //     float lcminy = mp.getYMin();
        float lcmaxy = (float) mp.getyMaxLimit();
        float lcminy = (float) mp.getyMinLimit();

        mp.calcGlobals();
        PropertyValuesHolder pvhX = null;
        PropertyValuesHolder pvhY = null;
        if (Math.abs(mp.getYMax() - lcmaxy)>mp.e){
            pvhX = PropertyValuesHolder.ofFloat("TRANSLATION_YMAX", lcmaxy,mp.getYMax());
        }
        if (Math.abs(mp.getYMin() - lcminy)>mp.e){
            pvhY = PropertyValuesHolder.ofFloat("TRANSLATION_YMIN", lcminy,mp.getYMin());
        }
        Log.d("HEIGHT","STARTED" + mp.getYMax() + " " + lcmaxy);



        if (pvhX != null){
            if (pvhY !=null){
                heightAnimator = ValueAnimator.ofPropertyValuesHolder(pvhX,pvhY);
            }
            else {
                heightAnimator= ValueAnimator.ofPropertyValuesHolder(pvhX);
            }
        }
        else {
            if (pvhY !=null){
                heightAnimator = ValueAnimator.ofPropertyValuesHolder(pvhY);
            }
            else {
                this.invalidate();
                return;
            }
        }

        heightAnimator.setDuration(500);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float ymaxscale = (Float) animation.getAnimatedValue("TRANSLATION_YMAX");
                Float yminscale = (Float) animation.getAnimatedValue("TRANSLATION_YMIN");
                Log.d("HEIGHT ANIMATION","UPDATE: " + ymaxscale + " " + yminscale);
                if (ymaxscale != null){
                    if (yminscale !=null){
                        mp.rescale(yminscale,ymaxscale);
                    }
                    else {
                        mp.rescaleMax(ymaxscale);
                    }
                }
                else {
                    if (yminscale != null){
                        mp.rescaleMin(yminscale);
                    }
                }
                startRescaling();
            }
        });
        //if (pvhX != null || pvhY != null )
            heightAnimator.start();
    }
    public void startRescaling(){
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
        int width = this.getWidth();
        int height = this.getHeight();
        mp = new MathPlot(width,height,topMargin,bottomMargin);
        viewPort = new LineChartViewPort(this,width,height);
        drawManager = new LineChartDrawManager(mp,width,height);
        if (lineChartListener != null){
            lineChartListener.onDidInit();
        }
        this.invalidate();
    }

    private void init(){
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSizes();
            }
        });

    }

    private void beginAnimation(ValueAnimator animation) {
        Float ymx = (Float) animation.getAnimatedValue("TRANSLATION_YMAX");
        Float ymn = (Float) animation.getAnimatedValue("TRANSLATION_YMIN");
        if (ymx!=null)
            this.mp.setyMaxLimit(ymx);
        if (ymn!=null)
            this.mp.setyMinLimit(ymn);
        this.invalidate();
    }


    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        return viewPort.onTouchEvent(event);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        drawManager.draw(canvas,viewPort.isFingerDown);
    }


    public void startAnimHide(String pos) {
        List<Double> p1 = chart.columns.get(pos);
        setVisiblePlot(pos,false);
        mp.calcGlobals();
        PropertyValuesHolder pvhX=null;
        PropertyValuesHolder pvhY=null;
        float lcmaxy = Collections.max(p1.subList(mp.start,mp.end)).floatValue();

        if (lcmaxy > mp.getYMax())
            pvhX = PropertyValuesHolder.ofFloat("TRANSLATION_YMAX", lcmaxy,mp.getYMax());

        float lcminy = Collections.min(p1.subList(mp.start,mp.end)).floatValue();
        if (lcminy < mp.getYMin())
            pvhY = PropertyValuesHolder.ofFloat("TRANSLATION_YMIN",lcminy,mp.getYMin());


        if (pvhX != null){
            if (pvhY !=null){
                newGraphAnimator = ValueAnimator.ofPropertyValuesHolder(pvhX,pvhY);
            }
            else {
                newGraphAnimator = ValueAnimator.ofPropertyValuesHolder(pvhX);
            }
        }
        else {
            if (pvhY !=null){
                newGraphAnimator = ValueAnimator.ofPropertyValuesHolder(pvhY);
            }
            else {
                this.invalidate();
                return;
            }
        }

        newGraphAnimator.setDuration(1000);
        newGraphAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                beginAnimation(animation);
            }
        });
        newGraphAnimator.start();
    }



    private double stats_y_intersection = 0;

    private int stats_x,stats_y=100;
    private int stats_offset=20;

    private int y_threshold = 5;

    private final int y_stats_offset=100;
    private boolean drawToTop = false;

    public void handleMove(long x,int w) {

        /*int nearest = viewPort.findNearestFor(plots.get(0),x);
        //stats_x = (int) (nearest*w/(end-start-1));
        //stats_x_position = nearest;

        drawManager.setStatsX((nearest*w/(end-start-1)));

        stats_y = y_stats_offset;
        drawToTop = false;

        Log.d("VIEW: ","NEAREST: " + nearest);
        double y_intersection = this.plots.get(0).y.get(nearest);

        float ymax = mp.getYMax();
        float ymin = mp.getYMin();
        Log.d("VIEW: ","HAPPENED: " + y_intersection + " max: " + ymax);

        stats_y_intersection = y_intersection;


        Log.d("VIEW: ","HAPPENED: " + stats_y_intersection);
        if (y_intersection>(y_threshold+ymin) ){
            stats_y = (int)(y_threshold+ymin)+y_stats_offset;
            drawToTop = true;
        }*/
        this.invalidate();
    }

    public interface LineChartListener{
        void onDidInit();
    }
}
