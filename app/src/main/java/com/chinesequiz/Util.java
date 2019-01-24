package com.chinesequiz;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Doug on 11/15/2018.
 */

public class Util {
    public static final int ACTIVITY_FINISHED = 1000;
    public static final int LOG_OUT = 1001;
    public static final int PAGE_SCROLLED = 1002;

    public static Dialog progressDlg = null;
    public static Toast toast = null;

    //-----Show load dialog
    public static void showProgressDialog(String titleStr, Context context)  {
        Typeface fontFace = Typeface.createFromAsset(context.getAssets(), "JosefinSans-Regular.ttf");

        progressDlg = new Dialog(context);
        progressDlg.requestWindowFeature(((Activity)context).getWindow().FEATURE_NO_TITLE);
        progressDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDlg.setContentView(R.layout.dialog_loading);
        progressDlg.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        progressDlg.getWindow().setGravity(Gravity.CENTER);

        TextView titleTv = (TextView)progressDlg.findViewById(R.id.textView39);
        titleTv.setText(titleStr);
        titleTv.setTypeface(fontFace);

        progressDlg.show();
    }

    //-----Hide load dialog
    public static void hideProgressDialog()  {
        if(progressDlg != null) {
            progressDlg.dismiss();
        }
    }

    //-----Show toast
    public static void showToast (String toastStr, Context context) {
        if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
            toast = Toast.makeText(context, toastStr, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

}
