package com.yakymovych.simon.telegramchart.Utils;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.Model.local.Plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MathPlot {
    private final int offsetTop;
    public static Long inf = Long.MAX_VALUE;
    private final int offsetBottom;
    public float e = 0.0001F ;
    private double xmax,xmin;
    public int start,end;
    private double ymax,ymin;
    //private List<Plot> plots = new ArrayList<>();
    private int w,h;
    private double yMaxLimit;
    private double yMinLimit;

    private Set<String> visiblePlots = new HashSet<>();
    private Chart chart;

    public void setVisiblePlots(Set<String> visiblePlots) {
        this.visiblePlots = visiblePlots;
    }

    public void setyMaxLimit(double yMaxLimit) {
        Log.d("MATHPLOT","set y limit: " + yMaxLimit);
        this.yMaxLimit = yMaxLimit;
    }
//
//    public int findNearestFor(Plot p, long x) {
//        List<Long> prcx = p.x.subList(start,end);
//        Log.d("SEARCH", "prcx " + prcx.toString() + " \nx = " + x + " zero: " + prcx.get(0));
//        return prcx.indexOf(x+prcx.get(0));
//    }
//
//
//    public void setPlots(List<Plot> plots) {
//        if (yMaxLimit ==0) yMaxLimit = ymax;
//        if (yMinLimit ==0) yMinLimit= ymin;
//        this.plots = plots;
//        //this.calcMaxGlobalY();
//    }


    public void setPlots(Chart chart) {
        if (yMaxLimit ==0) yMaxLimit = ymax;
        if (yMinLimit ==0) yMinLimit= ymin;
        //this.plots = plots;
        this.chart = chart;
        //this.calcMaxGlobalY();
    }

    public MathPlot(int w, int h, int offsetTop,int offsetBotton){
        this.w = w;
        this.offsetTop = offsetTop;
        this.offsetBottom = offsetBotton;
        this.h = h-offsetTop-offsetBotton;
    }
    void calcMaxGlobalX(){
        xmax = 0;
            List<Double> ys = chart.columns.get("x");
            xmax = Math.max(xmax,Collections.max(ys.subList(start,end)));
        Log.d("CALCMAX","MAXIMUM: " + xmax);

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
        this.start = start;
        calcGlobals();
    }

    public void setEnd(int end) {
        this.end = end;
        calcGlobals();
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



    public void drawChart(List<Double> p, Canvas canvas, Paint paint){
        double lmin_x = xmin;
        double lmax_x = xmax;

        double kx = ((double)(w))/(lmax_x-lmin_x);
        double ky = ((double)(h)/(yMaxLimit -yMinLimit));

        List<Double> xs = chart.columns.get("x");
        float[] points = new float[(end-start)*4];


        long x = (long)((xs.get(0) -lmin_x)*kx);
        float y = (float)(h-((p.get(0)-yMinLimit))*ky) + offsetTop;
        double xi;
        double yi;

        for (int i=start,k=0;i<end;i++,k+=4){
            points[k] = x;
            points[k+1] = y;

            xi = xs.get(i);
            yi = p.get(i);

            x = (long)((xi-lmin_x)*kx);
            y = (float)(h-((yi-yMinLimit))*ky) + offsetTop;

            points[k+2] = x;
            points[k+3] = y;
        }
        canvas.drawLines(points,paint);
    }
//
//    public void drawCharts(Canvas canvas, Paint paint) {
//        //calcGlobals();
//        Log.d("VIEW","DRAWING CHARTS" + visiblePlots );
//        for (String i : visiblePlots){
//            List<Double> p = chart.columns.get(i);
//            Log.d("DRAWING","CHART " + i + " " + p);
//            String color = chart.colors.get(i);
//            paint.setColor(Color.parseColor(color));
//            drawChart(p,canvas,paint);
//        }
//    }


    public void drawCharts(Canvas canvas, Paint paint) {
        List<List<Double>> y_charts = new ArrayList<>();
        List<String> colors = new ArrayList<>();

        for (String i : visiblePlots){
            List<Double> p = chart.columns.get(i);
            y_charts.add(p);
            colors.add(chart.colors.get(i));
        }
        int y_size = y_charts.size();
        if (y_size == 0) return;
        double lmin_x = xmin,lmax_x = xmax;

        double kx = ((double)(w))/(lmax_x-lmin_x);
        double ky = ((double)(h)/(yMaxLimit-yMinLimit));

        List<Double> xs = chart.columns.get("x");

        long x = (long)((xs.get(start) -lmin_x)*kx);

        float[] yl = new float[y_size];
        float[][] combined = new float[y_size][xs.size()*4];

        for (int yi = 0;yi<y_size;yi++){
            yl[yi] = (float)(h-((y_charts.get(yi).get(start)-yMinLimit))*ky) + offsetTop;
        }
        double xi;
        int g;
        for (int i=start+1,k=0,z=0;i<end;i++,k+=2,z+=4){

            for (g =0;g<y_size;g++) {
                combined[g][z] = x;
            }

            xi = xs.get(i);
            x = (long)((xi-lmin_x)*kx);

            for (g =0;g<y_size;g++) {
                combined[g][z+2] = x;
                combined[g][z+1] = yl[g];
                combined[g][z+3] = (float)(h-((y_charts.get(g).get(i)-yMinLimit))*ky) + offsetTop;;
                yl[g] = combined[g][z+3];
            }
        }

        for (g =1;g<y_size+1;g++) {
            paint.setColor(Color.parseColor(colors.get(g-1)));
            canvas.drawLines(combined[g-1], paint);
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
