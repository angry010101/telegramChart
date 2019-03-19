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
    Context context;
    LinearLayout ll;
    CompoundButton.OnCheckedChangeListener chbListener;

    public CheckBoxCreator(Context context,LinearLayout ll) {
        this.context = context;
        this.ll = ll;
    }

    public void setData(Map<String,String> names){
        List<String> n = new ArrayList<>();
        Map<String, String> map = names;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            n.add(entry.getKey() + "/" + entry.getValue());
        }
        this.names = n;
    }

    public void generate(CompoundButton.OnCheckedChangeListener chbListener){
        this.chbListener = chbListener;
        Iterator<String> i = names.iterator();
        while (i.hasNext()){
            CheckBox ch = createCheckBox(i.next());
        }

    }


    private CheckBox createCheckBox(String text){
        CheckBox ch = new CheckBox(context);
        ch.setText(text);
        ch.setTag(text);
        ch.setOnCheckedChangeListener(chbListener);
        ll.addView(ch);
        return ch;
    }

}
