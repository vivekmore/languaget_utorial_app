package com.chinesequiz;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Map;

import static com.chinesequiz.Util.LOG_OUT;

//-----Main activity to show all lessons
public class MainActivity extends AppCompatActivity {
    public static Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureDesign();

        handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == LOG_OUT) {
                    finish();
                }
            }
        };
    }

    private void configureDesign() {
        Typeface fontFace = Typeface.createFromAsset(getAssets(), "JosefinSans-Regular.ttf");
        TextView titleTv = findViewById(R.id.textView3);
        titleTv.setTypeface(fontFace);

        GridView lessonGv = findViewById(R.id.lesson_grid);
        final LessonAdapter adapter = new LessonAdapter(this);
        lessonGv.setAdapter(adapter);

        lessonGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LessonActivity.lessonObj = (Map<String, Object>) adapter.getItem(position);
                startActivity(new Intent(MainActivity.this, LessonActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_alarm:
                startActivity(new Intent(MainActivity.this, SetNotificationActivity.class));
                break;
            case R.id.menu_calendar:
                startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
                break;
            case R.id.menu_help:
                startActivity(new Intent(MainActivity.this, TutorialActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
