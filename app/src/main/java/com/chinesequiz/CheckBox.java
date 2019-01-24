package com.chinesequiz;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

//-----Customized checkbox for schedule page
public class CheckBox extends AppCompatCheckBox {
    private OnCheckedChangeListener mListener;
    public CheckBox(final Context context) {
        super(context);
    }

    public CheckBox(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBox(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnCheckedChangeListener(final OnCheckedChangeListener listener) {
        mListener = listener;
        super.setOnCheckedChangeListener(listener);
    }

    public void setChecked(final boolean checked, final boolean alsoNotify) {
        //-----If alsoNotify value is false, checkbox - onCheckedChangeListener function is not called when checkbox is checked
        if (!alsoNotify) {
            super.setOnCheckedChangeListener(null);
            super.setChecked(checked);
            super.setOnCheckedChangeListener(mListener);
            return;
        }
        super.setChecked(checked);
    }
}