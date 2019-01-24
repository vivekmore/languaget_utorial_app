package com.chinesequiz;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Map;

import static com.chinesequiz.Util.ACTIVITY_FINISHED;
import static com.chinesequiz.Util.PAGE_SCROLLED;

//-----Activity to show lesson details. Pdf & tones
public class LessonActivity extends AppCompatActivity {
    int scrollingPage = 0;
    int curPage = 0;
    public static Map<String, Object> lessonObj;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        configureDesign();
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle(String.format("Lesson %s", (String)lessonObj.get("name")));

        ViewPager viewPager = findViewById(R.id.viewPager);
        SubLessonAdapter adapter = new SubLessonAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                scrollingPage = i;
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if(i == ViewPager.SCROLL_STATE_IDLE) {
                    if(curPage != scrollingPage) {
                        Log.d("VIEW PAGER", "PAGE SCROLLED");
                        SubLessonAdapter.handler.sendEmptyMessage(PAGE_SCROLLED);
                        curPage = scrollingPage;
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubLessonAdapter.handler.sendEmptyMessage(ACTIVITY_FINISHED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
