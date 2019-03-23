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

import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.ArrayList;
import java.util.List;

public class LineChartDrawManager {


    public double pxPerUnit;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect rect = new Rect();
    MathPlot mp;
    int w,h;
    int dividersCount=4;
    int statsX, statsY;
    int statsXoffsetLeft=20;
    int statsW = 160, statsH = 200;
    float statsBorderWidth=3;

    int chartBackground ;
    float stats_radius = 20;
    int dateCount = 5;

    int textPaddingLeft = 22;
    int textPaddingTop = 10;
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
    Paint paintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    int textColor;

    public LineChartDrawManager(MathPlot mp, int w, int h,int paintColor,int chartBackground,int colorChartBorder,int defaultTextColor){
        this.chartBackground = chartBackground;
        intersectionPaint.setColor(Color.WHITE);
        intersectionPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        intersectionPaint.setAntiAlias(true);

        this.textColor = defaultTextColor;
        paintText.setTextSize(24);
        paintText.setAntiAlias(true);
        this.paintBorder.setStrokeWidth(3);
        this.paintBorder.setStyle(Paint.Style.STROKE);
        this.paintBorder.setColor(colorChartBorder);
        intersectionPaint.setColor(Color.WHITE);
        intersectionPaint.setAntiAlias(true);
        rect.left = 0;
        this.w = w;
        this.h = h;
        rect.right = w;
        rect.top = 0;
        rect.bottom = h;
        this.mp = mp;

        statsW = w/5;
        statsH = h/4;
        paint.setColor(paintColor);
        paint.setTextSize(30);
    }

    public void draw(Canvas canvas, boolean drawStats, double[] ys,int[] ys_real_data,  ArrayList<String> ysColors,Long currentX,List<String> ysLabels){
        mp.drawCharts(canvas,graphPaint);
        this.drawXDividers(canvas,paint);
        if (drawStats && ys != null){
            this.drawIntersection(canvas,graphPaint, ys,ysColors);
            this.drawStats(canvas,paint,currentX,ys,ys_real_data,ysColors,ysLabels);
        }
    }


    private void drawXAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,h,w,h,paint);
    }

    private void drawYAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,0,0,h,paint);
    }




    private void drawXDividers(Canvas canvas, Paint paint) {
        int  hc =(int)(h-mp.offsetTop-mp.offsetBottom)/(dividersCount);
        double t = ((mp.getyMaxLimit()-mp.getyMinLimit()))/(dividersCount);
        double ymin = mp.getyMinLimit();
        for (int i=0,k=dividersCount;k>0;i+= hc,k--){
            canvas.drawLine(0,i+mp.offsetBottom,w,i+mp.offsetBottom,paint);
            canvas.drawText(""+(int)Math.round(t*(k)+ymin),0,i+mp.offsetBottom,paint);
        }

    }

    private void drawStats(Canvas canvas, Paint paint, Long currentX, double[] ys, int[] ys_real_data, ArrayList<String> ysColors, List<String> ysLabels) {
        //COULD BE BETTER
        int statsX = this.statsX-statsXoffsetLeft;
        int minStatsW = 180;

        double ymax = mp.getYMax();

        statsW = minStatsW + (int)(Math.log10(ymax) + 1)*6*ys.length;
        if (ys.length>2) statsW += (ys.length-2)*40;
        statsY=80;
        RectF rect = new RectF(statsX, statsY,statsX + statsW, statsY + statsH);
        int lastColor = paint.getColor();
        paint.setColor(chartBackground);
        //canvas.drawRect(new Rect(statsX, statsY, statsX + statsW, statsY + statsH),paint);
        canvas.drawRoundRect(rect,stats_radius,stats_radius,paint);
        canvas.drawRoundRect(rect,stats_radius,stats_radius,paintBorder);
        paint.setColor(lastColor);
        paintText.setColor(textColor);


        if (ys.length !=0){
            canvas.drawText(GraphGenerator.getStringDateWithDay(currentX),statsX+textPaddingLeft,paint.getTextSize()+statsY+textPaddingTop,paintText);
            int textOffset = (statsW-2*textPaddingLeft)/ys.length;
            for (int i=0;i<ys.length;i++){
                paintText.setColor(Color.parseColor(ysColors.get(i)));
                double y = ys_real_data[i];
                canvas.drawText(GraphGenerator.formatDecimal(y),statsX+textPaddingLeft+i*textOffset,statsY+statsH-paint.getTextSize()-3*textPaddingTop,paintText);
                canvas.drawText(ysLabels.get(i),statsX+textPaddingLeft+i*textOffset,statsY+statsH-3*textPaddingTop,paintText);
            }
        }

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
