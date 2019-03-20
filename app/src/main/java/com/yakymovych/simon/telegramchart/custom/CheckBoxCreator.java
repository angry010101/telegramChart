package com.yakymovych.simon.telegramchart.custom;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.yakymovych.simon.telegramchart.MainActivity;

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

    public CheckBoxCreator(Context context,LinearLayout ll) {
        this.context = context;
        this.ll = ll;
    }

    public void setData(Map<String,String> names){
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
        this.chbListener = chbListener;
        for (int i = 0;i<names.size();i++){
            CheckBox ch = createCheckBox(names.get(i), tags.get(i));
        }

    }


    private CheckBox createCheckBox(String text,String tag){
        CheckBox ch = new CheckBox(context);
        ch.setText(text);
        ch.setTag(tag);
        ch.setOnCheckedChangeListener(chbListener);
        ll.addView(ch);
        return ch;
    }

}
