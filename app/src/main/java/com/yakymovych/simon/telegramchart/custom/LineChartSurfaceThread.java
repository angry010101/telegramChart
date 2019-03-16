package com.yakymovych.simon.telegramchart.custom;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class LineChartSurfaceThread extends Thread{

    private SurfaceHolder myThreadSurfaceHolder;
    private LineChartView myThreadSurfaceView;
    private boolean myThreadRun = false;

    public LineChartSurfaceThread(SurfaceHolder surfaceHolder,
                           LineChartView surfaceView) {
        myThreadSurfaceHolder = surfaceHolder;
        myThreadSurfaceView = surfaceView;
    }

    public void setRunning(boolean b) {
        myThreadRun = b;
    }

    @Override
    public void run() {
        // super.run();
        while (myThreadRun) {
            Canvas c = null;
            try {
                c = myThreadSurfaceHolder.lockCanvas(null);
                synchronized (myThreadSurfaceHolder) {
                    myThreadSurfaceView.doDraw(c);
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    myThreadSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}
