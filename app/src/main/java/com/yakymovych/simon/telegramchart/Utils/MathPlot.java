package com.yakymovych.simon.telegramchart.Utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.yakymovych.simon.telegramchart.Model.local.Plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MathPlot {
    private final int offsetTop;
    public static Long inf = Long.MAX_VALUE;
    private long xmax,xmin;
    public int start,end;
    private double ymax,ymin;
    private List<Plot> plots = new ArrayList<>();
    private int w,h;
    private double yMaxLimit;
    private double yMinLimit;

    private Set<Integer> visiblePlots = new HashSet<Integer>();

    public void setVisiblePlots(Set<Integer> visiblePlots) {
        this.visiblePlots = visiblePlots;
    }

    public void setyMaxLimit(double yMaxLimit) {
        Log.d("MATHPLOT","set y limit: " + yMaxLimit);
        this.yMaxLimit = yMaxLimit;
    }

    public int findNearestFor(Plot p, int x) {
        //List<Integer> prcx = this.x.subList(start,end);
        return p.x.indexOf(x);
    }
    public void setPlots(List<Plot> plots) {
        if (yMaxLimit ==0) yMaxLimit = ymax;
        if (yMinLimit ==0) yMinLimit= ymin;
        this.plots = plots;
        //this.calcMaxGlobalY();
    }

    public MathPlot(int w, int h, int offsetTop){
        this.w = w;
        this.offsetTop = offsetTop;
        this.h = h-2*offsetTop;
    }
    void calcMaxGlobalX(){
        xmax = 0;
        if (plots.isEmpty()) return;
        for (int i : visiblePlots){
            Plot p = plots.get(i);
            xmax = Math.max(xmax,Collections.max(p.x.subList(start,end)));
        }
    }
    void calcMinGlobalX(){
        xmin = inf;
        if (plots.isEmpty()) return;
        for (int i : visiblePlots){
            Plot p = plots.get(i);
            xmin = Math.min(xmin,Collections.min(p.x.subList(start,end)));
        }
    }

    void calcMaxGlobalY(){
        ymax = 0;
        if (plots.isEmpty()) return;
        for (int i : visiblePlots){
            Plot p = plots.get(i);
            ymax = Math.max(ymax,Collections.max(p.y.subList(start,end)));
        }
    }

    void calcMinGlobalY(){
        ymin = inf;
        if (plots.isEmpty()) return;
        for (int i : visiblePlots){
            Plot p = plots.get(i);
            ymin = Math.min(ymin,Collections.min(p.y.subList(start,end)));
        }
    }

    Long calcMaxLocalX(Plot p){
        return Collections.max(p.x.subList(start,end));
    }

    Long calcMinLocalX(Plot p){
        return Collections.min(p.x.subList(start,end));
    }

    Double calcMaxLocalY(Plot p){
        return Collections.max(p.y.subList(start,end));
    }

    Double calcMinLocalY(Plot p){
        return Collections.min(p.y.subList(start,end));
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



    public void drawChart(Plot p, Canvas canvas, Paint paint){
        List<Integer> prx = new ArrayList<>();
        List<Double> pry =new ArrayList<>();
        long lmin_x = calcMinLocalX(p);
        long lmax_x = calcMaxLocalX(p);

        Double lmin_y = calcMinLocalY(p);
        Double lmax_y = calcMaxLocalY(p);

        double kx = ((double)(w))/(lmax_x-lmin_x);
        double ky = ((double)(h)/(yMaxLimit -yMinLimit));


        Log.d("MATHPLOT","ky: " + ky + " yMaxLimit: " + yMaxLimit);
        for (int i=start;i<end;i++){
            long xi = p.x.get(i);
            double yi = p.y.get(i);
            prx.add((int)((xi-lmin_x)*kx));
            pry.add((h-((yi-yMinLimit))*ky) + offsetTop);
        }

        for (int i=0;i<prx.size()-1;i++ ){
            canvas.drawLine(prx.get(i),pry.get(i).intValue(),
                    prx.get(i+1),pry.get(i+1).intValue(),paint);
            //canvas.drawText("x: " + x.get(i) + " y: " + y.get(i),15+ px.get(i),py.get(i),paint);
        }
    }

    public void drawCharts(Canvas canvas, Paint paint) {
        calcGlobals();

        for (int i : visiblePlots){
            Plot p = plots.get(i);
            paint.setColor(Color.parseColor("#"+p.color));
            drawChart(p,canvas,paint);
        }
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
}
