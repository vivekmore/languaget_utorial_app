package com.chinesequiz;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScheduleAdapter extends RecyclerView.Adapter {
    Activity activity;
    ArrayList<Map<String, Object>> scheduleArr;
    ArrayList<Map<String, Object>> userScheduleArr;
    String userId;
    FirebaseFirestore db;

    public static final int VIEW_TYPE_SCHEDULE_TITLE = 1;
    public static final int VIEW_TYPE_SCHEDULE = 2;

    private class ScheduleTitleHolder extends RecyclerView.ViewHolder {
        ScheduleTitleHolder(View itemView) {
            super(itemView);
            TextView weekTv;
            TextView mTv, tTv, wTv, thTv, fTv;
            Typeface fontFace = Typeface.createFromAsset(activity.getAssets(), "JosefinSans-Regular.ttf");
            weekTv = itemView.findViewById(R.id.textView1);
            mTv = itemView.findViewById(R.id.textView2);
            tTv = itemView.findViewById(R.id.textView3);
            wTv = itemView.findViewById(R.id.textView4);
            thTv = itemView.findViewById(R.id.textView5);
            fTv = itemView.findViewById(R.id.textView6);
            weekTv.setTypeface(fontFace);
            mTv.setTypeface(fontFace);
            tTv.setTypeface(fontFace);
            wTv.setTypeface(fontFace);
            mTv.setTypeface(fontFace);
            thTv.setTypeface(fontFace);
            fTv.setTypeface(fontFace);
        }
    }

    private class ScheduleHolder extends RecyclerView.ViewHolder {
        TextView weekTv;
        CheckBox mCb, tCb, wCb, thCb, fCb;
        TextView mTv, tTv, wTv, thTv, fTv;

        ScheduleHolder(View itemView) {
            super(itemView);
            Typeface fontFace = Typeface.createFromAsset(activity.getAssets(), "JosefinSans-Regular.ttf");
            weekTv = itemView.findViewById(R.id.week_tv);
            mCb = itemView.findViewById(R.id.checkBox1);
            tCb = itemView.findViewById(R.id.checkBox2);
            wCb = itemView.findViewById(R.id.checkBox3);
            thCb = itemView.findViewById(R.id.checkBox4);
            fCb = itemView.findViewById(R.id.checkBox5);
            mTv = itemView.findViewById(R.id.textView1);
            tTv = itemView.findViewById(R.id.textView2);
            wTv = itemView.findViewById(R.id.textView3);
            thTv = itemView.findViewById(R.id.textView4);
            fTv = itemView.findViewById(R.id.textView5);
            weekTv.setTypeface(fontFace);
            mCb.setTypeface(fontFace);
            tCb.setTypeface(fontFace);
            wCb.setTypeface(fontFace);
            thCb.setTypeface(fontFace);
            fCb.setTypeface(fontFace);
            mTv.setTypeface(fontFace);
            tTv.setTypeface(fontFace);
            wTv.setTypeface(fontFace);
            mTv.setTypeface(fontFace);
            thTv.setTypeface(fontFace);
            fTv.setTypeface(fontFace);
        }

        void bind(Map<String, Object> scheduleObj) {
            ArrayList<Map<String, Object>> weekScheduleArr = (ArrayList<Map<String, Object>>) scheduleObj.get("week_schedules");
            int week = ((Long)scheduleObj.get("week_number")).intValue();
            weekTv.setText(String.format("%d", week));

            final Map<String, Object> mScheduleObj = weekScheduleArr.get(0);
            mTv.setText((String)mScheduleObj.get("name"));
            final int mScheduleId = ((Long)mScheduleObj.get("schedule_id")).intValue();
            final String mUserScheduleId = scheduleChecked(mScheduleId);
            if(mUserScheduleId.equals("")) {
                mCb.setChecked(false, false);
            }
            else {
                mCb.setChecked(true, false);
            }
            mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateSchedule(mUserScheduleId, mScheduleId, isChecked);
                }
            });

            Map<String, Object> tScheduleObj = weekScheduleArr.get(1);
            tTv.setText((String)tScheduleObj.get("name"));
            final int tScheduleId = ((Long)tScheduleObj.get("schedule_id")).intValue();
            final String tUserScheduleId = scheduleChecked(tScheduleId);
            if(tUserScheduleId.equals("")) {
                tCb.setChecked(false, false);
            }
            else {
                tCb.setChecked(true, false);
            }
            tCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateSchedule(tUserScheduleId, tScheduleId, isChecked);
                }
            });

            Map<String, Object> wScheduleObj = weekScheduleArr.get(2);
            wTv.setText((String)wScheduleObj.get("name"));
            final int wScheduleId = ((Long)wScheduleObj.get("schedule_id")).intValue();
            final String wUserScheduleId = scheduleChecked(wScheduleId);
            if(wUserScheduleId.equals("")) {
                wCb.setChecked(false, false);
            }
            else {
                wCb.setChecked(true, false);
            }
            wCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateSchedule(wUserScheduleId, wScheduleId, isChecked);
                }
            });

            Map<String, Object> thScheduleObj = weekScheduleArr.get(3);
            thTv.setText((String)thScheduleObj.get("name"));
            final int thScheduleId = ((Long)thScheduleObj.get("schedule_id")).intValue();
            final String thUserScheduleId = scheduleChecked(thScheduleId);
            if(thUserScheduleId.equals("")) {
                thCb.setChecked(false, false);
            }
            else {
                thCb.setChecked(true, false);
            }
            thCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateSchedule(thUserScheduleId, thScheduleId, isChecked);
                }
            });

            Map<String, Object> fScheduleObj = weekScheduleArr.get(4);
            fTv.setText((String)fScheduleObj.get("name"));
            final int fScheduleId = ((Long)fScheduleObj.get("schedule_id")).intValue();
            final String fUserScheduleId = scheduleChecked(fScheduleId);
            if(fUserScheduleId.equals("")) {
                fCb.setChecked(false, false);
            }
            else {
                fCb.setChecked(true, false);
            }
            fCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateSchedule(fUserScheduleId, fScheduleId, isChecked);
                }
            });
        }
    }

    private void updateSchedule(String documentId, int scheduleId, boolean isChecked) {
        //-----Check and uncheck schedule
        if(isChecked) {
            Map<String, Object> scheduleObj = new HashMap<>();
            scheduleObj.put("user_id", userId);
            scheduleObj.put("schedule_id", scheduleId);
            scheduleObj.put("date", new Date());

            Util.showProgressDialog("Adding..", activity);
            db.collection("user_schedule")
                    .add(scheduleObj)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Util.hideProgressDialog();
                            getUserSchedules();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Util.hideProgressDialog();
                        }
                    });
        }
        else {
            Util.showProgressDialog("Removing..", activity);
            db.collection("user_schedule").document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Util.hideProgressDialog();
                            getUserSchedules();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Util.hideProgressDialog();
                        }
                    });
        }
    }

    private String scheduleChecked(int scheduleId) {
        for(int i = 0; i < userScheduleArr.size(); i++) {
            Map<String, Object> userScheduleObj = userScheduleArr.get(i);
            if(((Long)userScheduleObj.get("schedule_id")).intValue() == scheduleId) {
                return (String) userScheduleObj.get("id");
            }
        }

        return "";
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (i == VIEW_TYPE_SCHEDULE_TITLE) {
            view = inflater.inflate(R.layout.item_schedule_title, viewGroup, false);
            return new ScheduleTitleHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_schedule, viewGroup, false);
            return new ScheduleHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_SCHEDULE:
                Map<String, Object> scheduleObj = scheduleArr.get(i - 1);
                ((ScheduleHolder) viewHolder).bind(scheduleObj);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleArr.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_TYPE_SCHEDULE_TITLE;
        }
        else {
            return VIEW_TYPE_SCHEDULE;
        }
    }

    public ScheduleAdapter(Activity activity) {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.activity = activity;
        getSchedules();
    }

    private void getSchedules() {
        //-----Get app schedules
        userScheduleArr = new ArrayList<>();
        scheduleArr = new ArrayList<>();
        Util.showProgressDialog("Loading..", activity);
        db = FirebaseFirestore.getInstance();
        CollectionReference scheduleRef = (CollectionReference) db.collection("schedule");
        scheduleRef.orderBy("week_number").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Util.hideProgressDialog();
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> lessonObj = document.getData();
                        lessonObj.put("id", document.getId());
                        scheduleArr.add(lessonObj);
                    }
                    notifyDataSetChanged();
                    getUserSchedules();
                }
            }
        });
    }

    private void getUserSchedules() {
        //-----Get user checked schedules
        userScheduleArr = new ArrayList<>();
        Util.showProgressDialog("Loading user schedule..", activity);
        db = FirebaseFirestore.getInstance();
        CollectionReference userScheduleRef = (CollectionReference) db.collection("user_schedule");
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
                    notifyDataSetChanged();
                }
            }
        });
    }
}
