package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.MathPlot;

import java.util.List;

public class LineChartView extends SurfaceView implements SurfaceHolder.Callback{
    Paint paint =new Paint(Paint.ANTI_ALIAS_FLAG);
    LineChartSurfaceThread thread = null;
    SurfaceHolder surfaceHolder;
    volatile boolean running = false;

    public List<Plot> plots;
    private MathPlot mp;

    private int start,end;

    int xmin,xmax;
    double ymin,ymax;

    private int y_threshold = 5;
    private final int y_stats_offset=100;
    private boolean drawToTop = false;
    private final double y_real_threshold = 1.85;

    private int width;
    private int height;
    LineChart.LineChartListener lineChartListener;
    private int topMargin = 20;

    public void setPlots(List<Plot> plots) {
        this.plots = plots;
        mp.setPlots(plots);
        this.invalidate();
    }

    public void setLineChartListener(LineChart.LineChartListener lineChartListener) {
        this.lineChartListener = lineChartListener;
    }

    public LineChartView(Context context) {
        super(context);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void initSizes(){
        this.width = this.getWidth();
        this.height = this.getHeight();
        mp = new MathPlot(width,height,topMargin);
        if (lineChartListener != null){
            lineChartListener.onDidInit();
        }
        this.invalidate();
    }

    private void init(){
        surfaceHolder = getHolder();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSizes();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Canvas canvas = surfaceHolder.lockCanvas();
        doDraw(canvas);
        surfaceHolder.unlockCanvasAndPost(canvas);
        return true;
    }

    public void doDraw(Canvas canvas) {
        Rect rect = new Rect();
        rect.left = 0;
        rect.right = getWidth();
        rect.top = 0;
        rect.bottom = getHeight();


        y_threshold = (int)((ymax-ymin)/2);
        //FIX IT
        mp.setPlots(plots);
        mp.setStartAndEnd(start,end);
        mp.drawCharts(canvas,paint);

//        this.drawXAsis(canvas,paint);
//        this.drawYAsis(canvas,paint);
//        this.drawXDividers(canvas,paint);
//        if (isFingerDown){
//            this.drawStats(canvas,paint);
//        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                               int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new LineChartSurfaceThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void setStartAndEnd(int start, int end) {
        this.start = start;
        this.end = end;
        mp.setStartAndEnd(start,end);
        this.invalidate();
    }
}
