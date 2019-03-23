package com.yakymovych.simon.telegramchart.custom.LineChart;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.R;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LineChart extends View {
    private int start,end;
    public MathPlot mp;
    //private List<Plot> plots;
    int chartBackground;

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

    int chartBorder;

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
        mp.setStart(start);
        viewPort.setStart(start);
        this.animateHeight();
    }

    public void setStartAndEnd(int start,int end) {
        this.start = start;
        this.end = end;
        removeStartEnd();

    }

    public void setEnd(int end) {
        this.end = end;

        mp.setEnd(end);
        viewPort.setEnd(end);
        this.animateHeight();
    }


    private void removeStartEnd(){
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
        init(context,null);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    int textColor;
    private void initSizes(int color){
        int width = this.getWidth();
        int height = this.getHeight();
        mp = new MathPlot(width,height,topMargin,bottomMargin,true);
        mp.setView(this);
        viewPort = new LineChartViewPort(this,width,height);
        drawManager = new LineChartDrawManager(mp,width,height,color,chartBackground,chartBorder,textColor);
        if (lineChartListener != null){
            lineChartListener.onDidInit();
        }
        this.invalidate();
    }

    private void init(Context context,AttributeSet attrs){

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                context.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary,
                        android.R.attr.textColorSecondary,
                        R.attr.chartStatsBackground,
                        R.attr.chartStatsBorder});

        textColor = arr.getColor(0, -1);
        final int primaryColor = arr.getColor(1, -1);
        chartBackground = arr.getColor(2, -1);
        chartBorder = arr.getColor(3, -1);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSizes(primaryColor);
            }
        });

    }

    public Set<String> getVisiblePlots() {
        return visiblePlots;
    }

    public void showPlots(){
        if (newGraphAnimator != null && newGraphAnimator.isRunning()) {
            newGraphAnimator.pause();
        }
        if (heightAnimator != null && heightAnimator.isRunning()) {
            heightAnimator.pause();
        }

        mp.calcGlobals();
        mp.setyMaxLimit(mp.getYMax());
        mp.setyMinLimit(mp.getYMin());
        this.invalidate();
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
        drawManager.draw(canvas,viewPort.isFingerDown,ys,ys_real_data,ysColors,currentX,ysLabels);
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

    double[] ys;
    Long currentX;
    ArrayList<String> ysColors;
    ArrayList<String> ysLabels;
    int[] ys_real_data;
    public void handleMove(long pos,long x_px, int w) {
        ys = new double[visiblePlots.size()];
        ys_real_data = new int[visiblePlots.size()];
        int xpos = (int) pos;//viewPort.findNearestFor(this.chart.columns.get("x"),pos);
        //drawManager.statsX = this.chart.columns.get("x").get((int)pos+start).intValue();
        Double xs = this.chart.columns.get("x").get((int) (pos+start));
        currentX = xs.longValue();
        Double xsStart = this.chart.columns.get("x").get((int) (start));
        Double xsEnd = this.chart.columns.get("x").get((int) (end-1));
        int i=0;
        double pxPerUnitY = (double)mp.h/(mp.getyMaxLimit()-mp.getyMinLimit());

        ysColors = new ArrayList<>();
        ysLabels = new ArrayList<>();
       // for (Map.Entry<String, List<Double>> entry : this.chart.columns.entrySet()) {
        for (String plot : visiblePlots) {
            List<Double> ys1 = chart.columns.get(plot);
            String key = plot;
            ysColors.add(i, this.chart.colors.get(key));
            ysLabels.add(i,this.chart.names.get(key));
            ys[i] = (ys1.get((int) (pos+start))-mp.getyMinLimit())*pxPerUnitY;
            ys_real_data[i++] = ys1.get((int) (pos+start)).intValue();
        }
        double pxPerUnit = (double)w/(xsEnd-xsStart);
        drawManager.statsX =(int)((xs-xsStart)*pxPerUnit);
        this.invalidate();
    }

    public interface LineChartListener{
        void onDidInit();
    }
}
