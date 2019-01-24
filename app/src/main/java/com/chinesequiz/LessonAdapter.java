package com.chinesequiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LessonAdapter extends BaseAdapter {
    Dialog dialog;
    Activity activity;
    ArrayList<Map<String, Object>> lessonArr;
    ArrayList<Map<String, Object>> userScheduleArr;
    ArrayList<Map<String, Object>> scheduleArr;

    @Override
    public int getCount() {
        return lessonArr.size();
    }

    @Override
    public Object getItem(int position) {
        return lessonArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View vi = inflater.inflate(R.layout.item_lesson, parent, false);
        configureItem(vi, position);
        return vi;
    }

    private void configureItem(View view, int position) {
        Map<String, Object> lessonObj = lessonArr.get(position);
        Typeface fontFace = Typeface.createFromAsset(activity.getAssets(), "JosefinSans-Regular.ttf");
        TextView nameTv = view.findViewById(R.id.textView2);
        nameTv.setTypeface(fontFace);
        nameTv.setText((String)lessonObj.get("name"));
    }

    public LessonAdapter(Activity activity) {
        this.activity = activity;
        getLessons();
    }

    private void getLessons() {
        //-----Get lessons from database
        lessonArr = new ArrayList<>();
        Util.showProgressDialog("Loading..", activity);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference lessonRef = (CollectionReference) db.collection("lesson");
        lessonRef.orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Util.hideProgressDialog();
                    if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        Log.d("FIREBASE LESSON DATA", document.getId() + "->" + document.getData());
                        Map<String, Object> lessonObj = document.getData();
                        lessonObj.put("id", document.getId());
                        lessonArr.add(lessonObj);
                    }
                    getUserLastSchedule();
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void getUserLastSchedule() {
        //-----Get user last lesson that he checked
        userScheduleArr = new ArrayList<>();
        Util.showProgressDialog("Loading..", activity);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final CollectionReference userScheduleRef = (CollectionReference) db.collection("user_schedule");
        userScheduleRef.whereEqualTo("user_id", userId).orderBy("schedule_id").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Util.hideProgressDialog();
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> userScheduleObj = document.getData();
                        userScheduleObj.put("id", document.getId());
                        userScheduleArr.add(userScheduleObj);
                    }
                }

                int userLastScheduleId;
                int prevUserScheduleId = 0;
                for(int i = 0; i < userScheduleArr.size(); i++) {
                    Map<String, Object> userScheduleObj = userScheduleArr.get(i);
                    userLastScheduleId = ((Long)userScheduleObj.get("schedule_id")).intValue();
                    if(userLastScheduleId != prevUserScheduleId + 1) {
                        break;
                    }

                    prevUserScheduleId = userLastScheduleId;
                }

                if(prevUserScheduleId != 75) {
                    getCurSchedule(prevUserScheduleId);
                }
                Log.d("User Last Schedule Id", String.format("%d", prevUserScheduleId));
            }
        });
    }

    private void getCurSchedule(final int lastScheduleId) {
        //-----Get user today lesson schedule based on last schedule Id
        Util.showProgressDialog("Loading..", activity);
        scheduleArr = new ArrayList<>();
        final ArrayList<Map<String, Object>> tempArr = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference scheduleRef = (CollectionReference) db.collection("schedule");
        scheduleRef.orderBy("week_number").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Util.hideProgressDialog();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> scheduleObj = document.getData();
                        scheduleObj.put("id", document.getId());
                        tempArr.add(scheduleObj);
                    }

                    int scheduleIndex = 0;
                    int curScheduleIndex = 0;
                    for(int i = 0; i < tempArr.size(); i++) {
                        Map<String, Object> tempObj = tempArr.get(i);
                        ArrayList<Map<String, Object>> weekObj = (ArrayList<Map<String, Object>>) tempObj.get("week_schedules");
                        for(int j = 0; j < weekObj.size(); j++) {
                            Map<String, Object> scheduleObj = weekObj.get(j);
                            scheduleArr.add(scheduleObj);
                            scheduleIndex ++;

                            if(((Long) scheduleObj.get("schedule_id")).intValue() == lastScheduleId) {
                                curScheduleIndex = scheduleIndex;
                            }
                        }
                    }


                    Map<String, Object> curScheduleObj = scheduleArr.get(curScheduleIndex);
                    String curScheduleName = (String) curScheduleObj.get("name");
                    String[] separated = curScheduleName.split(",");
                    int firstLesson = Integer.parseInt(separated[0]);
                    String secondLessonStr = separated[1];
                    secondLessonStr = secondLessonStr.substring(1);
                    int secondLesson = Integer.parseInt(secondLessonStr);
                    showTodayLessonDialog(firstLesson, secondLesson);
                }
            }
        });
    }


    private void showTodayLessonDialog(final int firstLesson, int secondLesson) {
        //-----Show today schedule dialog
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(activity.getWindow().FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_reminder);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        String firstLessonStr = String.format("lesson %d", firstLesson);
        String secondLessonStr = String.format("lesson %d", secondLesson);

        Typeface fontFace = Typeface.createFromAsset(activity.getAssets(), "JosefinSans-Regular.ttf");

        TextView titleTv = dialog.findViewById(R.id.textView10);
        titleTv.setTypeface(fontFace);

        Spanned contentText = Html.fromHtml(String.format("Your lessons for today are lesson %d and lesson %d", firstLesson, secondLesson));
        TextView contentTv = dialog.findViewById(R.id.textView19);
        Spannable spannable = (Spannable)contentText;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.colorPrimary)), 27, 27 + firstLessonStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.colorPrimary)), 32 + firstLessonStr.length(), 32 + firstLessonStr.length() + secondLessonStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentTv.setTypeface(fontFace);
        contentTv.setText(spannable);

        Button okBt = (Button)dialog.findViewById(R.id.okBtn);
        Button dismissBt = (Button)dialog.findViewById(R.id.button2);
        okBt.setTypeface(fontFace);
        dismissBt.setTypeface(fontFace);

        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                LessonActivity.lessonObj = lessonArr.get(firstLesson - 1);
                activity.startActivity(new Intent(activity, LessonActivity.class));
            }
        });

        dismissBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
