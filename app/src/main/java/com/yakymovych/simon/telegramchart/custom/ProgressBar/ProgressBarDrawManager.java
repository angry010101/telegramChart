package com.yakymovych.simon.telegramchart.custom.ProgressBar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.List;
import java.util.Set;

public class ProgressBarDrawManager {
    MathPlot mp;
    int width,height;


    Paint bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint grayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int borderWidth=16;
    private int sliderTopBorder=4;

    public ProgressBarDrawManager(MathPlot mp, int width, int height){
        this.mp= mp;
        this.width = width;
        this.height = height;
        init();
    }
    private void init(){
        paint.setColor(Color.GREEN);
        paint.setTextSize(30);

        grayPaint.setColor(Color.GRAY);
        grayPaint.setAlpha(60);
        //grayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        grayPaint.setAntiAlias(true);

        bluePaint.setColor(Color.BLUE);
        bluePaint.setAlpha(40);
        //grayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        bluePaint.setAntiAlias(true);


    }


    private void drawSlider(Canvas canvas,int p,int e) {
//        int p = viewPort.getProgressStartPx(progress);
//        int e = viewPort.getProgressEndPx(progressEnd);
        canvas.drawRect(0,0,p,height,grayPaint);
        canvas.drawRect(e+borderWidth,0,width,height,grayPaint);
        canvas.drawRect(p+borderWidth,0,e,sliderTopBorder,bluePaint);
        canvas.drawRect(p+borderWidth,height-sliderTopBorder,e,height,bluePaint);
        canvas.drawRect(p,0,p+borderWidth,height,bluePaint);
        canvas.drawRect(e,0,e+borderWidth,height,bluePaint);
    }


    public void draw(Canvas canvas, List<Plot> plots,Set<Integer> visiblePlots,int p,int e) {
        mp.setPlots(plots);
        mp.setStartAndEnd(0,plots.get(0).x.size());

        mp.setyMaxLimit(mp.getYMax());
        mp.setyMinLimit(mp.getYMin());

        mp.setVisiblePlots(visiblePlots);

        mp.calcGlobals();

        mp.drawCharts(canvas,paint);
        this.drawSlider(canvas,p,e);
    }

}
