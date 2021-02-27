package com.example.boundservice24112020;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyBoundService extends Service {

    int mCount = 0;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    String CHANNEL_ID = "MY_CHANNEL";
    OnDataChange mOnDataChange;
    boolean mIsRunning = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalIBinder();
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = createNotification(mCount);
        startForeground(1 , mBuilder.build());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsRunning){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsRunning = true;
                    mCount++;
                    if (mOnDataChange != null){
                        mOnDataChange.changeCount(mCount);
                    }
                    mNotificationManager.notify(1 , createNotification(mCount).build());
                    new Handler().postDelayed(this , 1000);
                }
            },1000);
        }

        return START_STICKY;
    }
    private NotificationCompat.Builder createNotification(int count){
        Intent intent = new Intent(this , MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                123,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder notify = new NotificationCompat.Builder(MyBoundService.this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setShowWhen(true)
                .setContentTitle("Xử lý tiến trình")
                .setContentText("Đang xử lý " + count)
                .addAction(R.mipmap.ic_launcher , "Open App" ,pendingIntent)
                .setPriority(2);

//                .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(0 , 1).setMediaSession())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        return notify;

    }

    class LocalIBinder extends Binder {
        MyBoundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyBoundService.this;
        }
    }

    public void setOnDataChange(OnDataChange onDataChange){
        mOnDataChange = onDataChange;
    }
}
