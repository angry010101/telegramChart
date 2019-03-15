package com.yakymovych.simon.telegramchart.Utils;

import android.util.Log;

import com.yakymovych.simon.telegramchart.Model.local.Plot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphGenerator {
    public List<Plot> plots = new ArrayList<Plot>();
    private double f(int x,int p){
        return Math.random()*10+10*p;
    }


    private String generateColor(){
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
        List<Integer> x = new ArrayList<Integer>();
        List<Double> y = new ArrayList<>();
        for (int i =0;i<100;i++){
            x.add(i);
            y.add(f(i,p1));
        }
        p.x = x;
        p.y = y;
        p.end=x.size();
        p.color = generateColor();
        plots.add(p);
    }
}
