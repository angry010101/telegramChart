package com.yakymovych.simon.telegramchart.Utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yakymovych.simon.telegramchart.Model.ChartData;
import com.yakymovych.simon.telegramchart.Model.local.Plot;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphGenerator {
    public List<Plot> plots = new ArrayList<Plot>();
    private double f(long x,int p){
        return Math.random()*10+10*p;
    }


    public void add(Plot p){
        plots.add(p);
    }
    public ChartData fromJson(Context context){
        String myJson=loadJSONFromAsset(context);
        Type listType = new TypeToken<List<ChartData>>(){}.getType();
        List<ChartData> chartData = new Gson().fromJson(myJson, listType);
        Log.d("FROMJSON","CHART DATA SIZE: " + chartData.size());
        return chartData.get(0);
    }
    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("chart_data.json");
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

    public String generateColor(){
        Random randomGenerator = new Random();
        int low = 0;
        int high = 16777215;
        int value = randomGenerator.nextInt(high-low) + low;
        String hex = Integer.toHexString(value);
        Log.d("GRAPHGENERATOR","COLOR: " + hex);
        return hex;
    }
    public void generate(int p1){
        Plot p = new Plot();
        List<Long> x = new ArrayList<Long>();
        List<Double> y = new ArrayList<>();
        for (long i =0;i<100;i++){
            x.add(i);
            y.add(f(i,p1));
        }
        p.x = x;
        p.y = y;
        p.color = generateColor();
        plots.add(p);
    }
}
