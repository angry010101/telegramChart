package com.yakymovych.simon.telegramchart;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.Model.ChartData;
import com.yakymovych.simon.telegramchart.Model.local.Plot;
import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.custom.CheckBoxCreator;
import com.yakymovych.simon.telegramchart.custom.MyScrollView;
import com.yakymovych.simon.telegramchart.custom.ProgressBar.GraphProgressBar;
import com.yakymovych.simon.telegramchart.custom.LineChart.LineChart;
import com.yakymovych.simon.telegramchart.custom.XLabelsView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
    TextView tv;
    LineChart lc;
    int plot_length;
    boolean theme=false;



    Chart chart;
    CheckBoxCreator chbCreator;
    GraphGenerator graphGenerator = new GraphGenerator();
    GraphProgressBar progressbar;

    private final String THEME_TAG = "theme_tag";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_theme:
                changeTheme();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    private void changeTheme() {
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra(THEME_TAG,!this.theme);
        startActivity(i);
        finish();
    }
    static final int PAGE_COUNT = 10;
    List<ChartData> chartData;
    LinearLayout ll;


    MyScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        if (b!=null){
            this.theme = b.getBoolean(THEME_TAG);
        }
        if (this.theme) setTheme(R.style.AppThemeDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll = this.findViewById(R.id.layout);
        scrollView = this.findViewById(R.id.scrollv);

        lc = this.findViewById(R.id.chart);

        progressbar = this.findViewById(R.id.graphProgressBar);
        progressbar.setScrollView(scrollView);
        lc.setScrollView(scrollView);
        List<Integer> data = new ArrayList<>();
        chartData = graphGenerator.fromJson(this);
        for (int i=0;i<chartData.size();i++){
            data.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, data);
        Chart c = Chart.fromСhartData(chartData.get(0));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        this.chart = c;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                setChart(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        chbCreator =  new CheckBoxCreator(this,ll);
        chbCreator.setData(c);
        //chbCreator.generate(chbListener);

        this.progressbar.setPlots(c);
        plot_length =  c.getAxisLength();
        lc.setLineChartListener(new LineChart.LineChartListener() {
            @Override
            public void onDidInit() {
                didInit();
            }
        });

        progressbar.setProgressChangedListener(new GraphProgressBar.ProgressChangedListener() {
            @Override
            public void onStartProgressChanged(View v, int p1, int p2) {
                lc.setStart((int)(((double)(p1)/progressbar.progressMax) * plot_length));

                lc.mp.calculateCharts();
                lc.invalidate();
            }

            @Override
            public void onEndProgressChanged(View v, int p1, int p2) {
                lc.setEnd((int)(((double)(p2)/progressbar.progressMax) * plot_length));
                lc.mp.calculateCharts();
                lc.invalidate();
            }

            @Override
            public void onOffsetProgressChanged(View v, int p1, int p2) {
                lc.setStartAndEnd((int)(((double)(p1)/progressbar.progressMax) * (plot_length-1)),
                        (int)(((double)(p2)/progressbar.progressMax) * plot_length));

                lc.mp.calculateCharts();
                lc.invalidate();
            }

            @Override
            public void onStopChanging(View v, int p1, int p2) {
                lc.animateHeight();
            }
        });

//        plot_length = graphGenerator.plots.get(0).x.size();
    //    this.progressbar.setPlots(c);
        //TODO
        //plot_length = c.columns.get().size();



    }

    CompoundButton.OnCheckedChangeListener chbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String tag = (String) (buttonView).getTag();
            if (isChecked){
                //graphGenerator.generate(0,18);
                //lc.setPlots(graphGenerator.plots);
                lc.setVisiblePlot(tag,true);

                progressbar.setVisiblePlot(tag,true);
                if (lc.getVisiblePlots().size() == 1) {
                    lc.showPlots();
                }
                else {
                    lc.startAnimShow(tag);
                }
                progressbar.invalidate();
            }
            else {
                lc.startAnimHide(tag);
                lc.setVisiblePlot(tag,false);
                progressbar.setVisiblePlot(tag,false);
                progressbar.invalidate();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    public void setChart(int pos) {
        this.chart = Chart.fromСhartData(chartData.get(pos));

        chbCreator.setData(this.chart);
        chbCreator.generate(chbListener);
        lc.setPlots(this.chart);

        lc.setStartAndEnd(0,plot_length);
        lc.setVisiblePlots(new HashSet<String>());
        this.progressbar.setPlots(this.chart);
        progressbar.setVisiblePlots(new HashSet<String>());

        progressbar.setStartAndEnd(0,100);
        progressbar.mp.calculateCharts();
        progressbar.invalidate();
        plot_length =  this.chart.getAxisLength();

    }

    void didInit() {
        lc.setPlots(this.chart);
        lc.setStartAndEnd(0,plot_length);

    }



}
