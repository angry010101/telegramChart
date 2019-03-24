package com.yakymovych.simon.telegramchart.Utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yakymovych.simon.telegramchart.Model.ChartData;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphGenerator {
    private double f(long x,int p){
        return (Math.random()*10)*((Math.random()-0.5) /(Math.abs(Math.random()-0.5)) )+10*p;
    }


    private static final DecimalFormat formatter = new DecimalFormat("0.00");

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
        return new Gson().fromJson(myJson, listType);
    }

    private String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("chart_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
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

    private static final DateFormat dateFormat = new SimpleDateFormat("MMM dd");
    private static final DateFormat statsdateFormat = new SimpleDateFormat("E, MMM dd");
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

}
