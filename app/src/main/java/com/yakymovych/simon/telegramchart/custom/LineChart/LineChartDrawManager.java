package com.yakymovych.simon.telegramchart.custom.LineChart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LineChartDrawManager {


    private final double pxPerUnit;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect rect = new Rect();
    private final MathPlot mp;
    private final int w;
    private final int h;
    private final int dividersCount=5;
    int statsX;
    private int statsY;
    private int statsXBox;
    private final int statsXoffsetLeft=20;
    private int statsW;
    private final int statsH;
    float statsBorderWidth=3;

    private final int chartBackground ;
    private final float stats_radius = 20;
    int dateCount = 5;

    private final int textPaddingLeft = 22;
    private final int textPaddingTop = 10;
    //remove some
    int y_stats_offset = 20;
    int y_threshold=5;
    int stats_y_intersection = 20;
    private final int intersection_radius = 6;
    private final Paint intersectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint intersectionGraphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint chartBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public void setStatsX(int statsX) {
        this.statsX = statsX;
    }

    public void setStatsY(int statsY) {
        this.statsY = statsY;
    }
    private final Paint paintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int textColor;

    public LineChartDrawManager(MathPlot mp, int w, int h,int paintColor,int chartBackground,int colorChartBorder,int defaultTextColor){
        this.chartBackground = chartBackground;
        intersectionPaint.setColor(Color.WHITE);
        intersectionPaint.setAntiAlias(true);
        intersectionPaint.setStrokeWidth(2);




        intersectionGraphPaint.setStyle(Paint.Style.STROKE);


        int stroke_width = 4;
        intersectionGraphPaint.setStrokeWidth(stroke_width);


        chartBackgroundPaint.setColor(chartBackground);
        this.textColor = defaultTextColor;
        paintText.setTextSize(24);
        paintText.setAntiAlias(true);
        this.paintBorder.setStrokeWidth(3);
        this.paintBorder.setStyle(Paint.Style.STROKE);
        this.paintBorder.setColor(colorChartBorder);
        rect.left = 0;
        this.w = w;
        this.h = h;
        rect.right = w;
        rect.top = 0;
        rect.bottom = h;
        this.mp = mp;

        statsW = w/5;
        statsH = h/4;

        this.pxPerUnit = (double)(w)/(mp.getYMax()-mp.getYMin());
        paint.setColor(paintColor);
        paint.setTextSize(30);
    }

    public void draw(Canvas canvas, boolean drawStats, double[] ys,int[] ys_real_data,  ArrayList<String> ysColors,Long currentX,List<String> ysLabels){
        if (mp.getVisiblePlots() == null || mp.getVisiblePlots().size() == 0)return;
        //mp.calcGlobals();
        mp.drawCharts(canvas,graphPaint);
        this.drawXDividers(canvas,paint);
        this.drawXAsis(canvas,paint);
        if (drawStats && ys != null){
            int statsX = this.statsX-statsXoffsetLeft;
            int minStatsW = 200;

            statsXBox= statsX;
            double ymax = mp.getYMax();

            statsW = minStatsW + (int)(Math.log10(ymax) + 1)*6*ys.length;
            if (ys.length>2) statsW += (ys.length-2)*40;
            //20 - threshold right
            if (statsXBox+statsW > w){
                statsXBox = w-statsW;
            }

            if (ys.length == 0 || ysColors.size() == 0 || ys.length > ysColors.size())return;
            this.drawIntersection(canvas, ys,ysColors);
            this.drawStats(canvas,paint,currentX,ys,ys_real_data,ysColors,ysLabels);
        }
    }


    private void drawXAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,h,w,h,paint);
        //calcOffset();
        //canvas.drawText(String.valueOf((int)(mp.getYMin()-mp.offsetBottom/ky)),0,h-mp.offsetBottom,paint);
    }

    private void drawYAsis(Canvas canvas,Paint paint) {
        canvas.drawLine(0,0,0,h,paint);
    }


    private double ky;
    private int offsetUnits;
    private int offsetTopPx;
    private void calcOffset(){
        ky = ((double)(h)/(mp.getyMaxLimit()-mp.getyMinLimit()));
        offsetUnits = (int)((mp.getYMax()-mp.getYMin())/6);  //25
        offsetTopPx = (int) (offsetUnits*ky);
    }

    private void drawXDividers(Canvas canvas, Paint paint) {
        calcOffset();
        int  hc =(int)(h-mp.offsetTop-mp.offsetBottom-offsetUnits*ky)/(dividersCount);
        double t = ((mp.getyMaxLimit()-mp.getyMinLimit()-offsetUnits))/(dividersCount);

        double ymin = mp.getyMinLimit();
        for (int i=0,k=dividersCount;k>0;i+= hc,k--){
            canvas.drawLine(0,i+mp.offsetBottom+offsetTopPx,w,i+mp.offsetBottom+offsetTopPx,paint);
            canvas.drawText(""+(int)Math.round(t*(k)+ymin),0,i+offsetTopPx,paint);
        }

    }

    private void drawStats(Canvas canvas, Paint paint, Long currentX, double[] ys, int[] ys_real_data, ArrayList<String> ysColors, List<String> ysLabels) {
        //COULD BE BETTER


        statsY=80;
        RectF rect = new RectF(statsXBox, statsY,statsXBox + statsW, statsY + statsH);
        //canvas.drawRect(new Rect(statsX, statsY, statsX + statsW, statsY + statsH),paint);
        canvas.drawRoundRect(rect,stats_radius,stats_radius,chartBackgroundPaint);
        canvas.drawRoundRect(rect,stats_radius,stats_radius,paintBorder);
        paintText.setColor(textColor);


        if (ys.length !=0){
            canvas.drawText(GraphGenerator.getStringDateWithDay(currentX),statsXBox+textPaddingLeft,paint.getTextSize()+statsY+textPaddingTop,paintText);
            int textOffset = (statsW-2*textPaddingLeft)/ys.length;
            for (int i=0;i<ys.length;i++){
                paintText.setColor(Color.parseColor(ysColors.get(i)));
                double y = ys_real_data[i];
                canvas.drawText(GraphGenerator.formatDecimal(y),statsXBox+textPaddingLeft+i*textOffset,statsY+statsH-paint.getTextSize()-3*textPaddingTop,paintText);
                canvas.drawText(ysLabels.get(i),statsXBox+textPaddingLeft+i*textOffset,statsY+statsH-3*textPaddingTop,paintText);
            }
        }

    }

    private void drawIntersection(Canvas canvas, double[] ys, ArrayList<String> ysColors) {
        int stroke_width = 4;

        int lh = statsY+statsH;
        double[] ys_sorted = new double[ys.length];
        System.arraycopy( ys, 0, ys_sorted, 0, ys.length );
        for (int i=0;i<ys_sorted.length;i++){
            ys_sorted[i] = mp.h-ys_sorted[i]+mp.offsetTop;
        }
        Arrays.sort(ys_sorted);
        for (int i =0;i<ys.length;i++){
            double y = ys[i];

            intersectionGraphPaint.setColor(Color.parseColor(ysColors.get(i)));
            int ytodraw = mp.h-(int)y+mp.offsetTop;
            canvas.drawCircle(statsX,
                    ytodraw,
                    intersection_radius,intersectionGraphPaint);

            int ytodrawline =  (int) ys_sorted[i];
            if (lh<ytodrawline-intersection_radius){
                canvas.drawLine(statsX,lh,statsX,ytodrawline-intersection_radius,intersectionPaint);
            }

            lh = (ytodrawline+intersection_radius+stroke_width/2);
        }
        if (lh < this.h)
            canvas.drawLine(statsX,lh,
                statsX, this.h,intersectionPaint);

    }



}
