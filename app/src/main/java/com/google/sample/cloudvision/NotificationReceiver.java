package com.google.sample.cloudvision;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_YES =
            "com.google.sample.cloudvision.ACTION_YES";
    public static final String ACTION_NO =
            "com.google.sample.cloudvision.ACTION_NO";
    public static final String TAG = NotificationReceiver.class.getSimpleName();
    public static final int NOTIFICATION_ID = 123;

    public NotificationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = null;
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i(TAG, "Action: " + intent.getAction());
        if (intent.getAction().equals(ACTION_YES)) {
            i = new Intent(context, DeleteActivity.class);
        } else if (intent.getAction().equals(ACTION_NO)) {
            i = new Intent(context, mainDeleteActivity.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationManager.cancel(NOTIFICATION_ID);
        context.startActivity(i);
    }
}


