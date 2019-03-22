package com.yakymovych.simon.telegramchart.custom.LineChart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.ArrayList;

public class LineChartDrawManager {


    public double pxPerUnit;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect rect = new Rect();
    MathPlot mp;
    int w,h;
    int dividersCount=5;
    int statsX, statsY;
    int statsXoffsetLeft=20;
    int statsW = 220, statsH = 160;

    float stats_radius = 20;
    int dateCount = 5;

    //remove some
    int y_stats_offset = 20;
    int y_threshold=5;
    int stats_y_intersection = 20;
    int intersection_radius = 10;
    Paint intersectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void setStatsX(int statsX) {
        this.statsX = statsX;
    }

    public void setStatsY(int statsY) {
        this.statsY = statsY;
    }


    public LineChartDrawManager(MathPlot mp, int w, int h,int paintColor){
        intersectionPaint.setColor(Color.WHITE);
        intersectionPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        intersectionPaint.setAntiAlias(true);
        rect.left = 0;
        this.w = w;
        this.h = h;
        rect.right = w;
        rect.top = 0;
        rect.bottom = h;
        this.mp = mp;
        paint.setColor(paintColor);
        paint.setTextSize(30);
    }

    public void draw(Canvas canvas, boolean drawStats, double[] ys, ArrayList<String> ysColors){
        mp.drawCharts(canvas,graphPaint);
        this.drawXDividers(canvas,paint);
        if (drawStats && ys != null){
            this.drawIntersection(canvas,graphPaint, ys,ysColors);
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
        int statsX = this.statsX-statsXoffsetLeft;
        statsY=80;
        RectF rect = new RectF(statsX, statsY,statsX + statsW, statsY + statsH);

        //canvas.drawRect(new Rect(statsX, statsY, statsX + statsW, statsY + statsH),paint);
        canvas.drawRoundRect(rect,stats_radius,stats_radius,paint);
        if (true){
            canvas.drawLine(statsX+statsXoffsetLeft,h,
                    statsX+statsXoffsetLeft, statsY + statsH,paint);
        }
        else {

            canvas.drawLine(statsX+statsXoffsetLeft,(int)(y_threshold+mp.getYMin())+ statsH +y_stats_offset,
                    statsX+statsXoffsetLeft,0,paint);
        }
    }

    private void drawIntersection(Canvas canvas, Paint paint, double[] ys, ArrayList<String> ysColors) {
        for (int i =0;i<ys.length;i++){
            double y = ys[i];
            paint.setColor(Color.parseColor(ysColors.get(i)));
            Log.d("INTERSECTION","Y: " + y);
            int ytodraw = mp.h-(int)y+mp.offsetTop;
            canvas.drawCircle(statsX,
                    ytodraw,
                    intersection_radius,paint);
//            Path smallPath = new Path();
//            smallPath.addCircle(statsX,ytodraw,intersection_radius/2,Path.Direction.CW);
//            canvas.clipPath(smallPath);
            canvas.drawCircle(statsX,ytodraw,intersection_radius/2,intersectionPaint);
        }
    }



}
