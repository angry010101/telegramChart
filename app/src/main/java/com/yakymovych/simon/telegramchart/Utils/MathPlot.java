package com.yakymovych.simon.telegramchart.Utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.yakymovych.simon.telegramchart.Model.local.Plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MathPlot {
    private final int offsetTop;
    private int inf = 999999999;
    private int xmax,xmin;
    private double ymax,ymin;
    private List<Plot> plots = new ArrayList<>();
    private int w,h;


    public MathPlot(int w, int h,int offsetTop){
        this.w = w;
        this.offsetTop = offsetTop;
        this.h = h-2*offsetTop;
    }
    void calcMaxGlobalX(){
        xmax = 0;
        for (Plot p : plots){
            xmax = Math.max(xmax,Collections.max(p.x));
        }
    }
    void calcMinGlobalX(){
        xmin = inf;
        for (Plot p : plots){
            xmin = Math.min(xmin,Collections.min(p.x));
        }
    }

    void calcMaxGlobalY(){
        ymax = 0;
        for (Plot p : plots){
            ymax = Math.max(ymax,Collections.max(p.y));
        }
    }

    void calcMinGlobalY(){
        ymin = inf;
        for (Plot p : plots){
            ymin = Math.min(ymin,Collections.min(p.y));
        }
    }

    Integer calcMaxLocalX(Plot p){
        return Collections.max(p.x);
    }

    Integer calcMinLocalX(Plot p){
        return Collections.min(p.x);
    }

    Double calcMaxLocalY(Plot p){
        return Collections.max(p.y);
    }

    Double calcMinLocalY(Plot p){
        return Collections.min(p.y);
    }


    void calcGlobals(){
        calcMaxGlobalX();
        calcMaxGlobalY();
        calcMinGlobalX();
        calcMinGlobalY();
    }

    public void drawChart(Plot p, Canvas canvas, Paint paint){
        List<Integer> prx = new ArrayList<>();
        List<Double> pry =new ArrayList<>();
        int lmin_x = calcMinLocalX(p);
        int lmax_x = calcMaxLocalX(p);

        Double lmin_y = calcMinLocalY(p);
        Double lmax_y = calcMaxLocalY(p);

        double kx = ((double)(w))/(lmax_x-lmin_x);
        double ky = ((double)(h))/(lmax_y-lmin_y);

        Log.d("MATHPLOT","PRX SIZE : " + prx.size());
        for (int i=0;i<p.x.size();i++){
            int xi = p.x.get(i);
            double yi = p.y.get(i);
            prx.add((int)((xi-lmin_x)*kx));
            pry.add((h-((yi-lmin_y))*ky) + offsetTop);
        }

        for (int i=0;i<prx.size()-1;i++ ){
            canvas.drawLine(prx.get(i),pry.get(i).intValue(),
                    prx.get(i+1),pry.get(i+1).intValue(),paint);
            //canvas.drawText("x: " + x.get(i) + " y: " + y.get(i),15+ px.get(i),py.get(i),paint);
        }
    }

    public void drawCharts(List<Plot> plots, Canvas canvas, Paint paint) {
        for (Plot p : plots){
            drawChart(p,canvas,paint);
        }
    }
}
