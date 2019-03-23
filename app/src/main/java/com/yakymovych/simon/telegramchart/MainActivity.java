package com.yakymovych.simon.telegramchart;

import android.content.Intent;
import android.support.v4.view.ViewPager;
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
    static final int PAGE_COUNT = 10;

    ViewPager pager;
    PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        if (b!=null){
            this.theme = b.getBoolean(THEME_TAG);
        }
        if (this.theme) setTheme(R.style.AppThemeDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pager = findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

//        plot_length = graphGenerator.plots.get(0).x.size();
    //    this.progressbar.setPlots(c);
        //TODO
        //plot_length = c.columns.get().size();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }



}
