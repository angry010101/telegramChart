package com.yakymovych.simon.telegramchart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.yakymovych.simon.telegramchart.Model.ChartData;
import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.custom.GraphProgressBar;
import com.yakymovych.simon.telegramchart.custom.LineChart;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    LineChart lc;
    int plot_length;
    CheckBox joined,left;
    GraphGenerator graphGenerator = new GraphGenerator();
    GraphProgressBar progressbar,sbend;
    Plot p1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChartData chartData = graphGenerator.fromJson(this);

        joined = this.findViewById(R.id.joined);
        left = this.findViewById(R.id.left);

        joined.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    graphGenerator.generate(0,8);
                    lc.setPlots(graphGenerator.plots);
                    lc.startAnimShow(1);
                }
                else {
                    lc.startAnimHide(1);
                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Log.d("MAIN","" + chartData.columns.get(0));
        List<Object> l = chartData.columns.get(0);
        List<Object> l1 = chartData.columns.get(1);
        List<Object> l2 = chartData.columns.get(2);


        List<Double> x_d =  (List<Double>)(Object)l.subList(1,l.size());
        List<Double> y0_d =  (List<Double>)(Object)l1.subList(1,l.size());
        List<Double> y1_d =  (List<Double>)(Object)l2.subList(1,l.size());

        List<Long> x = new ArrayList<>();

        for (double item : x_d) {
            x.add((Double.valueOf(item).longValue()));
        }


        Plot p = new Plot();
        p.x =  x;
        p.y = y0_d;
        p.color = graphGenerator.generateColor();
        p1 = new Plot();
        p1.y = y1_d;
        p1.color = graphGenerator.generateColor();
        //p.x = x;

        //chartData
        lc = this.findViewById(R.id.chart);
        lc = this.findViewById(R.id.chart);

        //graphGenerator.add(p);

        //graphGenerator.add(p1);
        graphGenerator.generate(10, 2);

        //graphGenerator.generate(11);
        //graphGenerator.generate(12);
        //graphGenerator.generate(13);


        progressbar = this.findViewById(R.id.graphProgressBar);
        this.progressbar.setPlots(graphGenerator.plots);
//        plot_length = graphGenerator.plots.get(0).x.size();
        plot_length = graphGenerator.plots.get(0).x.size();
        Log.d("MAIN","PLOT LENGTH:" + plot_length);
        Log.d("MAIN","PLOT COUNT:" + graphGenerator.plots.size());
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
