package com.yakymovych.simon.telegramchart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.Model.ChartData;
import com.yakymovych.simon.telegramchart.Utils.GraphGenerator;
import com.yakymovych.simon.telegramchart.custom.CheckBoxCreator;
import com.yakymovych.simon.telegramchart.custom.LineChart.LineChart;
import com.yakymovych.simon.telegramchart.custom.ProgressBar.GraphProgressBar;
import com.yakymovych.simon.telegramchart.custom.XLabelsView;

public class MainFragment extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    int backColor;
    Chart chart;

    int plot_length;

    CheckBoxCreator chbCreator;
    GraphGenerator graphGenerator = new GraphGenerator();
    GraphProgressBar progressbar;
    XLabelsView xLabelsView;
    LineChart lc;

    static MainFragment newInstance(int page) {
        MainFragment pageFragment = new MainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment, null);


        LinearLayout ll = view.findViewById(R.id.layout);
        xLabelsView = view.findViewById(R.id.xLabelsView);
        lc = view.findViewById(R.id.chart);
        progressbar = view.findViewById(R.id.graphProgressBar);

        ChartData chartData = graphGenerator.fromJson(getContext()).get(pageNumber);
        Chart c = Chart.fromСhartData(chartData);
        this.chart = c;



        chbCreator =  new CheckBoxCreator(getContext(),ll);
        chbCreator.setData(c);
        chbCreator.generate(chbListener);

        xLabelsView.setDates(c.columns.get("x"));
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
                xLabelsView.moveTo(p1,p2);
            }

            @Override
            public void onEndProgressChanged(View v, int p1, int p2) {
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
        return view;
    }


    void didInit() {
        lc.setPlots(this.chart);
        lc.setStartAndEnd(0,plot_length);
    }
}
