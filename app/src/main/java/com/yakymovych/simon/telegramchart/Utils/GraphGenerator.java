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
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GraphGenerator {
    public List<Plot> plots = new ArrayList<Plot>();
    private double f(long x,int p){
        return (Math.random()*10)*((Math.random()-0.5) /(Math.abs(Math.random()-0.5)) )+10*p;
    }


    static DecimalFormat formatter = new DecimalFormat("0.00");
    public void add(Plot p){
        plots.add(p);
    }

    static {
        formatter.setRoundingMode(RoundingMode.FLOOR);
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);
//        formatter.setMaximumIntegerDigits(4);
//        formatter.setMinimumIntegerDigits(0);
    }


    public List<ChartData> fromJson(Context context){
        String myJson=loadJSONFromAsset(context);
        Type listType = new TypeToken<List<ChartData>>(){}.getType();
        List<ChartData> chartData = new Gson().fromJson(myJson, listType);
        return chartData;
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

    public static String generateColor(){
            // create random object - reuse this as often as possible
            Random random = new Random();

            // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
            int nextInt = random.nextInt(0xaaaaaa + 1);

            // format it as hexadecimal string (with hashtag and leading zeros)
            return String.format("%06x", nextInt);
    }

    static DateFormat dateFormat = new SimpleDateFormat("MMM dd");
    static DateFormat statsdateFormat = new SimpleDateFormat("E, MMM dd");
    public static String getStringDateWithDay(long x){
        return statsdateFormat.format(x);
    }


    public static String getStringDate(long x){
        return dateFormat.format(x);
    }

    public static List<String> getStringDates(List<Long> x ){
        ArrayList<String> d = new ArrayList<>();
        for (int i=0;i<x.size();i++){
            d.add(getStringDate(x.get(i)));
        }
        return d;
    }

    public static String formatDecimal(Double d){
        return formatter.format(d);
    }

    public void generate(int p1, int i1){
        Plot p = new Plot();
        List<Long> x = new ArrayList<Long>();
        List<Double> y = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("MMM dd");


        for (long i =0;i<112;i++){
            x.add(i);
            dates.add(dateFormat.format(new Date()));
            y.add(i1 * f(i,p1));
        }
        p.x = x;
        p.y = y;
        p.dates = dates;
        p.color = generateColor();
        plots.add(p);
    }
}
