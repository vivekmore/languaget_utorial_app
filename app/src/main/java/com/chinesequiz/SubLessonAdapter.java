package com.chinesequiz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static com.chinesequiz.Util.ACTIVITY_FINISHED;
import static com.chinesequiz.Util.PAGE_SCROLLED;

public class SubLessonAdapter extends PagerAdapter implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {
    private Context mContext;
    ArrayList<Map<String, Object>> subLessonArr;
    private MediaPlayer player;
    public static Handler handler;
    ArrayList<ImageView> playIvArr;
    ImageView swipeIv;
    @Override
    public int getCount() {
        return subLessonArr.size() + 1;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout;
        if(position == 0) {
            layout = (ViewGroup) inflater.inflate(R.layout.page_pdf, collection, false);
            configurePdf(layout);
        }
        else {
            layout = (ViewGroup) inflater.inflate(R.layout.page_sub_lesson, collection, false);
            configureSubLesson(layout, position);
        }

        collection.addView(layout);
        return layout;
    }

    private void configurePdf(ViewGroup layout) {
        int lessonIndex = ((Long) LessonActivity.lessonObj.get("index")).intValue();
        PDFView pdfView = layout.findViewById(R.id.pdfView);
        pdfView.fromAsset(String.format("lesson%d.pdf", lessonIndex))
                .defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(mContext))
                .spacing(10) // in dp
                .onPageError(this)
                .load();


        swipeIv = layout.findViewById(R.id.imageView5);
        swipeIv.setVisibility(View.INVISIBLE);
    }

    private void configureSubLesson(ViewGroup layout, int position) {
        final Map<String, Object> subLessonObj = subLessonArr.get(position - 1);

        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "JosefinSans-Regular.ttf");
        TextView nameTv = layout.findViewById(R.id.textView);
        nameTv.setTypeface(fontFace);
        nameTv.setText((String)subLessonObj.get("name"));

        ConstraintLayout playCl = layout.findViewById(R.id.play_cl);
        final ImageView playIv = layout.findViewById(R.id.imageView4);

        boolean playIvAdded = false;
        for(int i = 0; i < playIvArr.size(); i++) {
            ImageView addedPlayIv = playIvArr.get(i);
            if(playIv.equals(addedPlayIv)) {
                playIvAdded = true;
                break;
            }
        }
        if(!playIvAdded) {
            playIvArr.add(playIv);
        }

        playCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPlaying = (boolean) subLessonObj.get("playing");
                if(isPlaying) {
                    stopPlay(subLessonObj, playIv);
                }
                else {
                    startPlay(subLessonObj, playIv);
                }
            }
        });

        ListView toneLv = layout.findViewById(R.id.tone_lv);
        ToneAdapter adapter = new ToneAdapter((Activity)mContext, subLessonObj);
        toneLv.setAdapter(adapter);
    }

    private void stopPlay(Map<String, Object> subLessonObj, ImageView playIv) {
        subLessonObj.put("playing", false);

        playIv.setImageResource(R.drawable.play);
        player.stop();
    }

    private void startPlay(final Map<String, Object> subLessonObj, final ImageView playIv) {
        //-----Play sub lesson sound
        subLessonObj.put("playing", true);

        playIv.setImageResource(R.drawable.stop);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay(subLessonObj, playIv);
            }
        });

        new Thread() {
            public void run() {
                try {
                    player.setDataSource((String)subLessonObj.get("sound_url"));
                    player.prepare();
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player.start();
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public SubLessonAdapter(Context context) {
        mContext = context;
        playIvArr = new ArrayList<>();
        getSubLessons();

        handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ACTIVITY_FINISHED) {
                    if(player != null) {
                        player.stop();
                    }
                }
                else if(msg.what == PAGE_SCROLLED) {
                    for(int i = 0; i < subLessonArr.size(); i++) {
                        Map<String, Object> subLessonObj = subLessonArr.get(i);
                        subLessonObj.put("playing", false);
                    }

                    if(player != null) {
                        player.stop();
                    }

                    for(int i = 0; i < playIvArr.size(); i++) {
                        ImageView playIv = playIvArr.get(i);
                        playIv.setImageResource(R.drawable.play);
                    }
                }
            }
        };
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        if(page == pageCount - 1) {
            Log.d("PDF VIEW", "PDF SCROLLED TO END");
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    private void getSubLessons() {
        //-----Get sub lessons of the lesson
        subLessonArr = new ArrayList<>();
        Util.showProgressDialog("Loading..", mContext);
        String lessonId = (String) LessonActivity.lessonObj.get("id");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference subLessonRef = (CollectionReference) db.collection("sub_lesson");
        subLessonRef.whereEqualTo("lesson_id", lessonId).orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Util.hideProgressDialog();
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> subLessonObj = document.getData();
                        subLessonObj.put("playing", false);
                        subLessonArr.add(subLessonObj);
                    }
                    notifyDataSetChanged();

                    /*CollectionReference userScheduleRef = (CollectionReference) db.collection("user_schedule");
                    userScheduleRef.whereEqualTo("user_id", lessonId)*/

                    //-----Show swipe animation image if there are additional sub lessons
                    if(subLessonArr.size() != 0) {
                        swipeIv.setVisibility(View.VISIBLE);
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(1200); //You can manage the blinking time with this parameter
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(5);
                        swipeIv.startAnimation(anim);

                        anim.setAnimationListener(new Animation.AnimationListener(){
                            @Override
                            public void onAnimationStart(Animation arg0) {
                            }
                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                            }
                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                swipeIv.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
                else {
                    Log.d("Sublesson Error", "Error", task.getException());
                }
            }
        });
    }
}
