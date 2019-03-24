package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.yakymovych.simon.telegramchart.MainActivity;
import com.yakymovych.simon.telegramchart.Model.Chart;
import com.yakymovych.simon.telegramchart.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CheckBoxCreator {
    List<String> names;
    List<String> tags;
    Context context;
    LinearLayout ll;
    CompoundButton.OnCheckedChangeListener chbListener;
    private Chart chart;

    public CheckBoxCreator(Context context,LinearLayout ll) {
        this.context = context;
        this.ll = ll;
    }

    public void setData(Chart chart){
        this.chart = chart;
        Map<String,String> names = chart.names;
        List<String> n = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        Map<String, String> map = names;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            n.add(entry.getValue());
            tags.add(entry.getKey());
        }
        this.names = n;
        this.tags = tags;
    }

    public void generate(CompoundButton.OnCheckedChangeListener chbListener){
        Log.d("CHECKBOXCREATOR","GENERATING");
        this.chbListener = chbListener;
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        int color = typedValue.data;
        theme.resolveAttribute(R.attr.chartDividersColor, typedValue, true);

        int dividercolor = typedValue.data;

        removeViews();

        //int dividerHeight = (int) (context.getResources().getDisplayMetrics().density * 1); // 1dp to pixels
        int dividerHeight = (int) 1; // 1dp to pixels

        createCheckBox(names.get(0), tags.get(0), color);
        for (int i = 1;i<names.size();i++){
            //add divider
            View v = new View(context);
            LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight);
            lp.setMarginStart(68);

            v.setLayoutParams(lp);
            v.setBackgroundColor(dividercolor);

            ll.addView(v);
            countDividers++;
            createCheckBox(names.get(i), tags.get(i), color);


        }

    }

    int count=0;
    int countDividers = 0;
    private CheckBox createCheckBox(String text,String tag,int textcolor){
        String color = this.chart.colors.get(tag);
        AppCompatCheckBox ch = new AppCompatCheckBox(new ContextThemeWrapper(this.context, R.style.checkbox), null, 0);
        ch.setText(text);
        ch.setTag(tag);
        ch.setOnCheckedChangeListener(chbListener);
        ch.setTextColor(textcolor);
        ch.setButtonTintList(ColorStateList.valueOf(Color.parseColor(color)));
        count++;
        ll.addView(ch);
        return ch;
    }

    public void removeViews(){
        ll.removeViews(ll.getChildCount()-count-countDividers,count+countDividers);
        count =0;
        countDividers = 0;
    }
}
