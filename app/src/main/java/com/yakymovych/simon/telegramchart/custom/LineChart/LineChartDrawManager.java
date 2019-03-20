package com.yakymovych.simon.telegramchart.custom.LineChart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.text.SimpleDateFormat;

public class LineChartDrawManager {


    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect rect = new Rect();
    MathPlot mp;
    int w,h;
    int dividersCount=5;
    int statsX, statsY;
    int statsW, statsH;

    int dateCount = 5;

    //remove some
    int y_stats_offset = 20;
    int y_threshold=5;
    int stats_y_intersection = 20;
    int intersection_radius = 10;

    public void setStatsX(int statsX) {
        this.statsX = statsX;
    }

    public void setStatsY(int statsY) {
        this.statsY = statsY;
    }


    public LineChartDrawManager(MathPlot mp, int w, int h){
        rect.left = 0;
        this.w = w;
        this.h = h;
        rect.right = w;
        rect.top = 0;
        rect.bottom = h;
        this.mp = mp;
        paint.setColor(Color.MAGENTA);
        paint.setTextSize(30);
    }

    public void draw(Canvas canvas,boolean drawStats){
        mp.drawCharts(canvas,paint);
        //this.drawXAsis(canvas,paint);
        this.drawXDividers(canvas,paint);
        if (drawStats){
            this.drawStats(canvas,paint);
        }
    }


    private void drawXAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,h,w,h,paint);
    }

    private void drawYAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,0,0,h,paint);
    }




    private void drawXDividers(Canvas canvas, Paint paint) {
        double t = ((mp.getyMaxLimit()-mp.getyMinLimit()))/dividersCount;
        for (int i=h/dividersCount,k=dividersCount;i<h || k>0;i+= h/dividersCount,k--){
            canvas.drawLine(0,i,w,i,paint);
            canvas.drawText(""+(int)Math.round(t*(k)),0,i,paint);
        }

    }

    private void drawStats(Canvas canvas, Paint paint) {
        //COULD BE BETTER
        canvas.drawRect(new Rect(statsX, statsY, statsX + statsW, statsY + statsH),paint);
        if (true){
            canvas.drawLine(statsX,h, statsX, statsY + statsH,paint);
        }
        else {

            canvas.drawLine(statsX,(int)(y_threshold+mp.getYMin())+ statsH +y_stats_offset,
                    statsX,0,paint);
        }

        drawIntersection(canvas,paint);
    }

    private void drawIntersection(Canvas canvas, Paint paint) {
        canvas.drawCircle(statsX,
                (int)(h-(stats_y_intersection-mp.getYMin())*((double)h)/(mp.getYMax()-mp.getYMin())),
                intersection_radius,paint);
    }



}
