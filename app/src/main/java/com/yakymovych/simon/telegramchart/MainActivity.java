package com.yakymovych.simon.telegramchart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
    TextView tv;
    LineChart lc;
    int plot_length;
    boolean theme=false;
    CheckBoxCreator chbCreator;
    GraphGenerator graphGenerator = new GraphGenerator();
    GraphProgressBar progressbar;
    XLabelsView xLabelsView;
    Plot p1;
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

    private void changeTheme() {
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra(THEME_TAG,!this.theme);
        startActivity(i);
        finish();
    }

    CompoundButton.OnCheckedChangeListener chbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String tag = (String) (buttonView).getTag();
            Log.d("TAG:",tag);
            if (isChecked){
                //graphGenerator.generate(0,18);
                //lc.setPlots(graphGenerator.plots);
                lc.setVisiblePlot(tag,true);
                progressbar.setVisiblePlot(tag,true);
                lc.startAnimShow(tag);
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
Chart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        if (b!=null){
            this.theme = b.getBoolean(THEME_TAG);
        }
        if (this.theme) setTheme(R.style.AppThemeDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout ll = this.findViewById(R.id.layout);
        xLabelsView = this.findViewById(R.id.xLabelsView);
        lc = this.findViewById(R.id.chart);
        progressbar = this.findViewById(R.id.graphProgressBar);

        ChartData chartData = graphGenerator.fromJson(this).get(0);
        Chart c = Chart.fromСhartData(chartData);
        this.chart = c;



        chbCreator =  new CheckBoxCreator(this,ll);
        chbCreator.setData(c.names);
        chbCreator.generate(chbListener);

        xLabelsView.setDates(c.columns.get("x"));
        this.progressbar.setPlots(c);
        plot_length =  c.getAxisLength();

//        plot_length = graphGenerator.plots.get(0).x.size();
    //    this.progressbar.setPlots(c);
        //TODO
        //plot_length = c.columns.get().size();

        lc.setLineChartListener(new LineChart.LineChartListener() {
            @Override
            public void onDidInit() {
                didInit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    void didInit() {
        lc.setPlots(this.chart);
        lc.setStartAndEnd(0,plot_length);
    }


}
