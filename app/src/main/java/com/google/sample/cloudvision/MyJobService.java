package com.google.sample.cloudvision;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.io.File;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MyJobService extends JobService {
    private static final String TAG = com.google.sample.cloudvision.MyJobService.class.getSimpleName();
    private JobParameters jobParameters;
    private int days;
    private File downloads;
    private File screenshots;
    private Handler handler;
    final JobService service = this;
    private final String CHANNEL_ID = "APP";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onStartJob(JobParameters params) {
        jobParameters = params;
        days = params.getExtras().getInt("days");
        Log.d(TAG, "onStartJob");
        trigger();
        deleteAfterDays();
        return true;
    }

    private void trigger() {
        Intent yesIntent = new Intent(NotificationReceiver.ACTION_YES);
        Intent noIntent = new Intent(NotificationReceiver.ACTION_NO);
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast
                (service, 0, yesIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast
                (service, 1, noIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (service, CHANNEL_ID)
                .setContentTitle("사진 정기 삭제 알림")
                .setContentText(days + "일이 지났는데 사진을 삭제할건가요?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(R.mipmap.ic_launcher, "네", yesPendingIntent)
                .addAction(R.mipmap.ic_launcher, "아니오", noPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(NotificationReceiver.NOTIFICATION_ID, builder.build());
        deleteAfterDays();
    }

    public void deleteAfterDays() {
        final int dayToMillis = 24*60*60*1000;
        handler = new Handler();
        Log.i(TAG, "add delete handler after " + days + " days");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                trigger();
            }
        }, days*dayToMillis);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: Job cancelled before completion");
        handler.removeCallbacksAndMessages(null);
        return true;
    }


}
