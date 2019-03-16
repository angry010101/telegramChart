package com.yakymovych.simon.telegramchart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yakymovych.simon.telegramchart.Model.ChartData;
import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.custom.GraphProgressBar;
import com.yakymovych.simon.telegramchart.custom.LineChart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    LineChart lc;
    int plot_length;
    GraphGenerator graphGenerator = new GraphGenerator();
    GraphProgressBar progressbar,sbend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChartData chartData = graphGenerator.fromJson(this);

        Log.d("MAIN","" + chartData.columns.get(0));
        List<Object> l = chartData.columns.get(0);
        List<Long> x =  (List<Long>)(Object)l.subList(1,l.size());
        Plot p = new Plot();
        p.x = x ;
        //p.y =
        //p.x = x;

        //chartData
        lc = this.findViewById(R.id.chart);

        graphGenerator.generate(10);
        graphGenerator.generate(10);


        progressbar = this.findViewById(R.id.graphProgressBar);
        this.progressbar.setPlots(graphGenerator.plots);
        plot_length = graphGenerator.plots.get(0).x.size();
        lc.setLineChartListener(new LineChart.LineChartListener() {
            @Override
            public void onDidInit() {
                lc.setStartAndEnd(0,plot_length);
                lc.setPlots(graphGenerator.plots);
            }
        });

        progressbar.setProgressChangedListener(new GraphProgressBar.ProgressChangedListener() {
            @Override
            public void onStartProgressChanged(View v, int p1, int p2, int offset) {
                lc.setStart((int)(((double)(p1)/100) * plot_length));
            }

            @Override
            public void onEndProgressChanged(View v, int p1, int p2, int offset) {
                lc.setEnd((int)(((double)(p2)/100) * plot_length));
            }

            @Override
            public void onOffsetProgressChanged(View v, int p1, int p2, int offset) {
                try{
                    lc.setStartAndEnd((int)(((double)(p1)/100) * (plot_length-1)),
                            (int)(((double)(p2)/100) * plot_length));
                }
                catch (Exception e ){
                    Log.d("ERROR","FATAL ERROR");
                }
            }
        });

    }


}
