package com.chinesequiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        configureDesign();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 1500);
    }

    private void configureDesign() {
        Typeface fontFace = Typeface.createFromAsset(getAssets(), "JosefinSans-Regular.ttf");
        TextView titleTv = findViewById(R.id.textView);
        titleTv.setTypeface(fontFace);
    }
}
