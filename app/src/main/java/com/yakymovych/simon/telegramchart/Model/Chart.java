package com.yakymovych.simon.telegramchart.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chart {
    public Map<String,String> colors;
    public Map<String,List<Double>> columns;
    public Map<String,String> names;
    private Map<String,String> types;

    public int getAxisLength(){
        return columns.get("x").size();
    }

    public static Chart fromСhartData(ChartData chartData) {
        Chart c = new Chart();
        c.colors = chartData.colors;
        c.names = chartData.names;
        c.types = chartData.types;
        c.columns = new HashMap<>();

        List<Object> l1 = chartData.columns.get(1);
        List<Object> l2 = chartData.columns.get(2);


        List<Object> l ;
        List<Double> x_d ;
        String label ;
        for (int i =0;i<chartData.columns.size();i++){
            l = chartData.columns.get(i);
            label = (String)l.get(0);
            x_d = (List<Double>)(Object)l.subList(1,l.size());
            c.columns.put(label,x_d);
        }
        return c;
    }


}
