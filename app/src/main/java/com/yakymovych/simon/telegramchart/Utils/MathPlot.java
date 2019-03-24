package com.yakymovych.simon.telegramchart.Utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.Model.local.Plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MathPlot {
    public final int offsetTop;
    public static Long inf = Long.MAX_VALUE;
    public final int offsetBottom;
    private final boolean drawDates;
    public float e = 0.0001F ;
    public double xmax,xmin;
    public int start,end;
    private double ymax,ymin;
    //private List<Plot> plots = new ArrayList<>();
    public int w,h;
    private volatile double yMaxLimit;
    private volatile double yMinLimit;

    Paint alphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Set<String> visiblePlots = new HashSet<>();
    private Chart chart;
    private int alphaValue = 100;
    private View view;
    private List<Double> chartDates;

    public void setVisiblePlots(Set<String> visiblePlots) {
        this.visiblePlots = visiblePlots;
    }


    ValueAnimator alphaAnimatorShow;

    ValueAnimator.AnimatorUpdateListener ll = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            beginAnimation(animation);
        }
    };

    private void beginAnimation(ValueAnimator animation) {
        this.alphaPaint.setAlpha((Integer) animation.getAnimatedValue());
        if (view!=null) view.invalidate();
    }


    public void setyMaxLimit(double yMaxLimit) {
        this.yMaxLimit = yMaxLimit;
    }


    public void  setView(View v){
        this.view = v;
    }

    public void setPlots(Chart chart) {
        if (yMaxLimit ==0) yMaxLimit = ymax;
        if (yMinLimit ==0) yMinLimit= ymin;
        //this.plots = plots;
        this.chart = chart;
        List<Double> xs = chart.columns.get("x");
        this.chartDates =  new ArrayList<>();
        for (int i=0;i<chart.getAxisLength();i+= visibleDates){
            chartDates.add(xs.get(i));
        }
        //this.calcMaxGlobalY();
    }

    int paddingDates;
    public MathPlot(int w, int h, int offsetTop,int offsetBotton,boolean drawDates,int paddingDates){
        this.w = w;
        this.offsetTop = offsetTop;
        this.offsetBottom = offsetBotton;
        this.h = h-offsetTop-offsetBotton-paddingDates;
        this.drawDates = drawDates;
        this.paddingDates = paddingDates;
        alphaPaint.setTextSize(20);

    }

    public void setAlphaPaintColor(int c) {
        this.alphaPaint.setColor(c);
    }

    void calcMaxGlobalX(){
        xmax = 0;
            List<Double> ys = chart.columns.get("x");
            xmax = Math.max(xmax,Collections.max(ys.subList(start,end)));

    }
    void calcMinGlobalX(){
        xmin = inf;
        List<Double> xs = chart.columns.get("x");
        xmin = Math.min(xmin,Collections.min(xs.subList(start,end)));
    }

    void calcMaxGlobalY(){
        ymax = 0;

        for (String i : visiblePlots){
            List<Double> ys = chart.columns.get(i);
            //TODO
            ymax = Math.max(ymax,Collections.max(ys.subList(start,end)));
        }
    }

    void calcMinGlobalY(){
        ymin = inf;
        for (String i : visiblePlots){
            List<Double> ys = chart.columns.get(i);
            ymin = Math.min(ymin,Collections.min(ys.subList(start,end)));
        }
    }
    Double calcMaxLocalY(List<Double> p){
        return Collections.max(p.subList(start,end));
    }

    Double calcMinLocalY(List<Double> p){
        return Collections.min(p.subList(start,end));
    }

    public void setStart(int start) {
        int dstart = this.start - start;
        this.start = start;
        calcGlobals();
        if (dstart > 0){
       //     startAnimShow();
        }
        else {
         //   startAnimHide();
        }
    }

    public void setEnd(int end) {
        int dend = this.end - end;
        this.end = end;
        calcGlobals();
        if (dend<0){
        //    startAnimShow();
        }
        else {
        //    startAnimHide();
        }
    }



    public void setStartAndEnd(int start,int end){
        this.start = start;
        this.end = end;
        //this.calcGlobals();
    }

    public void calcGlobals(){
        calcMaxGlobalX();
        calcMaxGlobalY();
        calcMinGlobalX();
        calcMinGlobalY();


    }



    int y_size;

    List<List<Double>> y_charts = new ArrayList<>();
    List<String> colors = new ArrayList<>();
    float[][] combined;
    List<Double> xs;

    private void prepare(){
        colors.clear();
        y_charts.clear();
        for (String i : visiblePlots){
            List<Double> p = chart.columns.get(i);
            y_charts.add(p);
            colors.add(chart.colors.get(i));
        }
        y_size = y_charts.size();
    }
    double kx;
    public void calculateCharts(){
        //List<List<Double>> y_charts = new ArrayList<>();
        //colors = new ArrayList<>();
        prepare();

        int g;
        if (y_size == 0) return;

        kx = ((double)(w))/(xmax-xmin);
        double ky = ((double)(h)/(yMaxLimit-yMinLimit));
        xs = chart.columns.get("x");
        long x = (long)((xs.get(start) -xmin)*kx);

        float[] yl = new float[y_size];
        combined = new float[y_size][xs.size()*4];

        for (int yi = 0;yi<y_size;yi++){
            yl[yi] = (float)(h-((y_charts.get(yi).get(start)-yMinLimit))*ky) + offsetTop;
        }
        double xi;
        for (int i=start+1,z=0;i<end;i++,z+=4){

            for (g =0;g<y_size;g++) {
                combined[g][z] = x;
            }
            xi = xs.get(i);
            x = (long)((xi-xmin)*kx);
            for (g =0;g<y_size;g++) {
                combined[g][z+2] = x;
                combined[g][z+1] = yl[g];
                combined[g][z+3] = (float)(h-((y_charts.get(g).get(i)-yMinLimit))*ky) + offsetTop;;
                yl[g] = combined[g][z+3];
            }
        }

    }

    public void drawCharts(Canvas canvas, Paint paint) {
        //calculateCharts();
        for (int g =1;g<y_size+1;g++) {
            paint.setColor(Color.parseColor(colors.get(g-1)));
            canvas.drawLines(combined[g-1], paint);

        }
        if (drawDates)
            this.drawValues(canvas);
    }

    private int visibleDates = 5;
    private void drawValues(Canvas canvas) {


        double v = ((double)(end-start)/visibleDates);
        for (double k=start; k<end;k+=v){

                double dlta = Math.abs(Math.round(k/visibleDates)-k/visibleDates);
                Double val  = chartDates.get((int) Math.round(k/visibleDates));
                int x = (int)((val-xmin)*kx);
                alphaPaint.setAlpha((100-(int)(dlta*100*2)));
                    canvas.drawText(GraphGenerator.getStringDate(val.longValue()),x,this.h+offsetBottom+offsetTop,alphaPaint);

        }
    }

    public Set<String> getVisiblePlots() {
        return visiblePlots;
    }

    public float getYMax() {
        return (float)this.ymax;
    }

    public float getYMin() {
        return (float)this.ymin;
    }


    public void setHeight(Float h) {
        ymax = h;
    }

    public void setyMinLimit(double ymn) {
        this.yMinLimit = ymn;
    }

    public void setLimits() {
        yMinLimit = ymin;
        yMaxLimit = ymax;
    }

    public void rescale(double ymina, double ymaxa) {
        yMinLimit = ymina;
        yMaxLimit = ymaxa;
    }

    public double getyMaxLimit() {
        return yMaxLimit;
    }

    public double getyMinLimit() {
        return yMinLimit;
    }

    public void rescaleMin(double ymina) {
        yMinLimit = ymina;
    }
    public void rescaleMax(double ymaxa) {
        yMaxLimit = ymaxa;
    }

}
