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
    GraphGenerator graphGenerator = new GraphGenerator();
    GraphProgressBar progressbar,sbend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChartData chartData = loadJson();

        Log.d("MAIN","" + chartData.columns.get(0));
        List<Object> l = chartData.columns.get(0);
        List<Long> x =  (List<Long>)(Object)l.subList(1,l.size());

        Log.d("MAIN","CASTED: " + x);


        //chartData
        lc = this.findViewById(R.id.chart);
        Plot p = new Plot();
        p.x = lc.x;
        p.y = lc.y;
        p.prx = lc.prx;
        p.pry = lc.pry;
        p.start = lc.start;
        p.end = lc.end;

        graphGenerator.generate(10);
        graphGenerator.generate(100);


        ArrayList<Plot> pl = new ArrayList<>();
        pl.add(p);

        progressbar = this.findViewById(R.id.graphProgressBar);
        this.progressbar.setPlots(graphGenerator.plots);

        progressbar.setProgressChangedListener(new GraphProgressBar.ProgressChangedListener() {
            @Override
            public void onStartProgressChanged(View v, int p1, int p2, int offset) {
                lc.setStart((int)(((double)(p1)/100) * (lc.x.size()-1)));
            }

            @Override
            public void onEndProgressChanged(View v, int p1, int p2, int offset) {
                lc.setEnd((int)(((double)(p2)/100) * lc.x.size()));
            }

            @Override
            public void onOffsetProgressChanged(View v, int p1, int p2, int offset) {
                try{
                    lc.setStart((int)(((double)(p1)/100) * (lc.x.size()-1)));
                    lc.setEnd((int)(((double)(p2)/100) * lc.x.size()));
                }
                catch (Exception e ){
                    Log.d("ERROR","FATAL ERROR");
                }
            }
        });
//        progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.d("MAINACTIVITY:","CHANGED: " + progress);
//                lc.setStart((int)(((double)(progress)/100) * (lc.x.size()-1)));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//
//        sbend.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.d("MAINACTIVITY:","CHANGED: " + progress);
//                lc.setEnd((int)(((double)(progress)/100) * lc.x.size()));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//


    }


    public ChartData loadJson(){
        String myJson=loadJSONFromAsset();
        Type listType = new TypeToken<List<ChartData>>(){}.getType();
        List<ChartData> chartData = new Gson().fromJson(myJson, listType);
        return chartData.get(0);
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("chart_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
