package com.yakymovych.simon.telegramchart.Utils;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.Model.local.Plot;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<Long> prx = new ArrayList<>();
        List<Double> pry =new ArrayList<>();
        this.calcGlobals();
        Log.d("MATHPLOT","XMIN : " + xmin + " xmax: " + xmax);
        double lmin_x = xmin;
        double lmax_x = xmax;

        Double lmin_y = calcMinLocalY(p);
        Double lmax_y = calcMaxLocalY(p);

        double kx = ((double)(w))/(lmax_x-lmin_x);
        double ky = ((double)(h)/(yMaxLimit -yMinLimit));


        Log.d("MATHPLOT","ky: " + ky + " yMaxLimit: " + yMaxLimit + " " + start + " " + end);
        for (int i=start;i<end;i++){
            double xi = chart.columns.get("x").get(i);
            double yi = p.get(i);
            prx.add((long)((xi-lmin_x)*kx));
            pry.add((h-((yi-yMinLimit))*ky) + offsetTop);
        }

        Log.d("MATHPLOT","PLOTTING " + prx.toString());
        for (int i=0;i<prx.size()-1;i++ ){
            canvas.drawLine(prx.get(i),pry.get(i).intValue(),
                    prx.get(i+1),pry.get(i+1).intValue(),paint);
            //canvas.drawText("x: " + x.get(i) + " y: " + y.get(i),15+ px.get(i),py.get(i),paint);
        }
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
        int i,k;
        List<List<Double>> y_charts = new ArrayList<>();
        List<String> colors = new ArrayList<>();

        for (String s : visiblePlots){
            List<Double> p = chart.columns.get(s);
            y_charts.add(p);
            colors.add(chart.colors.get(s));
        }

        int y_size = y_charts.size();
        if (y_size == 0) return;
        double lmin_x = xmin;
        double lmax_x = xmax;

        double kx = ((double)(w))/(lmax_x-lmin_x);
        double ky = ((double)(h)/(yMaxLimit -yMinLimit));

        List<Double> xs = chart.columns.get("x");
        float[][] points = new float[y_size+1][(end-start)*2];

        long x;
        float y;
        double xi;
        double yi;
        ArrayList<Path> paths = new ArrayList<>();
        for (i =0;i<y_size;i++){
            Path p = new Path();

            p.setFillType(Path.FillType.WINDING);
            float x1 = (float) (double) xs.get(0);
            float y1 = (float) (double) y_charts.get(i).get(0);

            x = (long)((x1-lmin_x)*kx);
            y = (float)(h-((y1-yMinLimit))*ky) + offsetTop;
            p.reset();
            p.moveTo(x,y);
            paths.add(p);
        }
        for (i=start+1;i<end;i++){
            xi = xs.get(i);
            x = (long)((xi-lmin_x)*kx);
            for (int g =0;g<y_size;g++){
                yi = y_charts.get(g).get(i);
                y = (float)(h-((yi-yMinLimit))*ky) + offsetTop;
                paths.get(g).lineTo(x,y);
            }
        }



//        for (int i=start+1,k=0;i<end;i++,k+=2){
//            points[0][k] = x;
//            xi = xs.get(i);
//            x = (long)((xi-lmin_x)*kx);
//            points[0][k+1] = x;
//
//            for (int g =0;g<y_charts.size();g++){
//                points[g+1][k] = yl[g];
//                yi = y_charts.get(g).get(i);
//                y[g] = (float)(h-((yi-yMinLimit))*ky) + offsetTop;
//                points[g+1][k+1] = y[g];
//                yl[g] = y[g];
//            }
//        }

        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        for (int g =1;g<y_charts.size()+1;g++) {
            Path p = paths.get(g-1);
            paint.setColor(Color.parseColor(colors.get(g-1)));
            canvas.drawPath(p,paint);
//            canvas.drawLines(arr, paint);
        }
    }

    public static float[] combine(float[] a, float[] b){
        int length = a.length + b.length;
        float[] result = new float[length];
        for (int i=0;i<a.length;i++){
            result[2*i] = a[i];
            result[2*i+1] = b[i];
        }
        return result;
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
