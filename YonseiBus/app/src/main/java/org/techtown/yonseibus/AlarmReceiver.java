package org.techtown.yonseibus;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;



public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context,MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int receiveData = intent.getIntExtra("hourMinBus",0);
        int hour = receiveData/10000;
        int minute = (receiveData%10000)/100;
        int busNum = receiveData%100;
        String receiveString = String.valueOf(busNum) + "번 " + String.valueOf(hour) + ":" + String.valueOf(minute);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, receiveData, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"default");

        //OREO API 26 이상에서는 채널 필요
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setSmallIcon(R.drawable.ic_launcher_background);//mipmap 사용시 Oreo 이상에서 시스템 UI 에러
            String channelName = "매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH;// 소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName,importance);
            channel.setDescription(description);

            if(notificationManager != null){
                //노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }
        else{
            builder.setSmallIcon(R.mipmap.ic_launcher);//Oreo 이하에서 mipmap 사용하지 않으면 x
        }

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("(Time to watch some cool stuff!)")
                .setContentTitle(receiveString)
                .setContentText(receiveString)
                .setContentInfo("INFO")
                .setContentIntent(pendingIntent);

        if(notificationManager != null){
            //노티피케이션 동작시킴
            notificationManager.notify(1234, builder.build());



        }
    }
}
