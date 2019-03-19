package com.yakymovych.simon.telegramchart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.Model.ChartData;
import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.custom.CheckBoxCreator;
import com.yakymovych.simon.telegramchart.custom.ProgressBar.GraphProgressBar;
import com.yakymovych.simon.telegramchart.custom.LineChart.LineChart;
import com.yakymovych.simon.telegramchart.custom.XLabelsView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    LineChart lc;
    int plot_length;

    CheckBoxCreator chbCreator;
    GraphGenerator graphGenerator = new GraphGenerator();
    GraphProgressBar progressbar;
    XLabelsView xLabelsView;
    Plot p1;



    CompoundButton.OnCheckedChangeListener chbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //graphGenerator.generate(0,18);
                //lc.setPlots(graphGenerator.plots);
                lc.startAnimShow(1);
                Set<Integer> vp = new HashSet<>();
                vp.add(0);
                vp.add(1);
                progressbar.setVisiblePlots(vp);
                progressbar.invalidate();
            }
            else {
                lc.startAnimHide(1);
                Set<Integer> vp = new HashSet<>();
                vp.add(0);
                progressbar.setVisiblePlots(vp);
                progressbar.invalidate();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout ll = this.findViewById(R.id.layout);
        xLabelsView = this.findViewById(R.id.xLabelsView);
        lc = this.findViewById(R.id.chart);
        progressbar = this.findViewById(R.id.graphProgressBar);

        ChartData chartData = graphGenerator.fromJson(this).get(0);
        Chart c = Chart.fromСhartData(chartData);


        //chartData

        //graphGenerator.generate(10, 2);

        //graphGenerator.generate(11);
        graphGenerator.generate(12,1);
        graphGenerator.generate(13,1);


        chbCreator =  new CheckBoxCreator(this,ll);
        chbCreator.setData(c.names);
        chbCreator.generate(chbListener);


        this.progressbar.setPlots(graphGenerator.plots);
        plot_length = graphGenerator.plots.get(0).x.size();
//        plot_length = graphGenerator.plots.get(0).x.size();
    //    this.progressbar.setPlots(c);
        //TODO
        //plot_length = c.columns.get().size();


        Log.d("MAIN","PLOT LENGTH:" + plot_length);
        Log.d("MAIN","PLOT COUNT:" + graphGenerator.plots.size());
        lc.setLineChartListener(new LineChart.LineChartListener() {
            @Override
            public void onDidInit() {
                lc.setPlots(graphGenerator.plots);
                lc.setStartAndEnd(0,plot_length);
            }
        });

        progressbar.setProgressChangedListener(new GraphProgressBar.ProgressChangedListener() {
            @Override
            public void onStartProgressChanged(View v, int p1, int p2) {
                Log.d("MAIN","PROGRESS START CHANGED: " + p1 + " " + p2 );
                lc.setStart((int)(((double)(p1)/progressbar.progressMax) * plot_length));
                xLabelsView.moveTo(p1,p2);
            }

            @Override
            public void onEndProgressChanged(View v, int p1, int p2) {
                Log.d("MAIN","PROGRESS END CHANGED: " + p1 + " " + p2 );
                lc.setEnd((int)(((double)(p2)/progressbar.progressMax) * plot_length));
                xLabelsView.moveTo(p1,p2);
            }

            @Override
            public void onOffsetProgressChanged(View v, int p1, int p2) {
                lc.setStartAndEnd((int)(((double)(p1)/progressbar.progressMax) * (plot_length-1)),
                            (int)(((double)(p2)/progressbar.progressMax) * plot_length));
                xLabelsView.moveTo(p1,p2);
            }
        });

    }


}
