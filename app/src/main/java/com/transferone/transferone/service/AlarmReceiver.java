package com.transferone.transferone.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;

import com.transferone.transferone.Activity.MainActivity;
import com.transferone.transferone.R;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by killandy on 2017/7／3.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp=context.getSharedPreferences("translateone",MODE_PRIVATE);
        if(sp.getBoolean("today",false)){

        }else{
            NotificationManager manger = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            //为了版本兼容  选择V7包下的NotificationCompat进行构造
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            //Ticker是状态栏显示的提示
            builder.setTicker("译一");
            //第一行内容  通常作为通知栏标题
            builder.setContentTitle("每日翻译");
            //第二行内容 通常是通知正文
            builder.setContentText("点击前往翻译");
            builder.setAutoCancel(true);
            //系统状态栏显示的小图标
            builder.setSmallIcon(R.mipmap.logo);
            Intent i = new Intent(context,MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context,1,i,0);
            //点击跳转的intent
            builder.setContentIntent(pIntent);
            //通知默认的声音 震动 呼吸灯
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
            Notification notification = builder.build();
            manger.notify(1,notification);
        }

    }
}
