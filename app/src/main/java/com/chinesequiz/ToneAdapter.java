package com.chinesequiz;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToneAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<String> toneArr;
    Typeface fontFace;

    @Override
    public int getCount() {
        return toneArr.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View vi;

        Object object = toneArr.get(position);
        if(object instanceof HashMap) {

            HashMap<String, Object> toneObj = (HashMap<String, Object>) object;
            //-----If type is "0" regard it as title else if "1" regard it as tone
            if(toneObj.get("type").equals("0")) {
                vi = inflater.inflate(R.layout.item_tone_title, parent, false);
                TextView nameTv = vi.findViewById(R.id.textView4);
                nameTv.setTypeface(fontFace);
                nameTv.setText((String)toneObj.get("name"));
            }
            else {
                vi = inflater.inflate(R.layout.item_tone, parent, false);
                TextView nameTv = vi.findViewById(R.id.textView2);
                nameTv.setTypeface(fontFace);
                nameTv.setText((String)toneObj.get("name"));
            }
        }
        else {
            vi = inflater.inflate(R.layout.item_tone, parent, false);
            TextView nameTv = vi.findViewById(R.id.textView2);
            nameTv.setTypeface(fontFace);
            nameTv.setText((String)object);
        }

        return vi;
    }

    public ToneAdapter(Activity activity, Map<String, Object> subLessonObj) {
        this.activity = activity;
        toneArr = (ArrayList<String>) subLessonObj.get("tones");
        fontFace = Typeface.createFromAsset(activity.getAssets(), "JosefinSans-Regular.ttf");
    }
}
