package com.chinesequiz;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;

import static com.chinesequiz.Util.LOG_OUT;

public class SetNotificationActivity extends AppCompatActivity {
    private NotificationManager notificationManager;
    private TimePicker notificationTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notification);
        configureDesign();
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Alarm");

        notificationTime = (TimePicker)findViewById(R.id.notificationTimePicker);
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Typeface fontFace = Typeface.createFromAsset(getAssets(), "JosefinSans-Regular.ttf");
        Button setNotificationBtn = (Button) findViewById(R.id.setNotificationBtn);
        setNotificationBtn.setTypeface(fontFace);

        Button signOutBt = (Button) findViewById(R.id.button);
        signOutBt.setTypeface(fontFace);

        setNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(SetNotificationActivity.this);
                notification.setSmallIcon(R.drawable.ic_launcher_background);
                notification.setContentTitle("Chinese Quiz");
                notification.setContentText("Chinese Quiz");
                notification.setContentText("Make sure you study for the chinese quiz");
                notification.setPriority(NotificationCompat.PRIORITY_HIGH);
                notification.setCategory(NotificationCompat.CATEGORY_ALARM);

                int hour = notificationTime.getCurrentHour();
                int minute = notificationTime.getCurrentMinute();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);
                Date notificationDate = calendar.getTime();

                PendingIntent contentIntent = PendingIntent.getActivity(SetNotificationActivity.this, 0,
                        new Intent(SetNotificationActivity.this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(contentIntent);
                notification.setAutoCancel(true);
                scheduleNotification(notification.build(), notificationDate.getTime());

                Toast.makeText(SetNotificationActivity.this,"Alarm is created!",Toast.LENGTH_SHORT).show();
            }
        });

        signOutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void scheduleNotification(Notification notification, long eventStartTime) {
        Intent notificationIntent = new Intent(SetNotificationActivity.this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = eventStartTime;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    public void logout() {
        //-----Remove locally saved email
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SetNotificationActivity.this);
        pref.edit().putString("Email", "");
        pref.edit().putString("Password", "");

        FirebaseAuth.getInstance().signOut();
        finish();
        MainActivity.handler.sendEmptyMessage(LOG_OUT);
        //startActivity(new Intent(SetNotificationActivity.this, LoginActivity.class));
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
