package com.transferone.transferone.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.transferone.transferone.service.AlarmReceiver;
import java.util.Calendar;
import java.util.TimeZone;
import static android.content.Context.ALARM_SERVICE;

/**
 * Created by killandy on 2017/3/23.
 */

public class AlarmUtils {


    /**
     * 打开提醒
     *
     * @param context
     */

    public static void startRemind(Context context){
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        //获取当前毫秒值
        long systemTime = System.currentTimeMillis();

        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        mCalendar.set(Calendar.HOUR_OF_DAY, 21);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        long selectTime = mCalendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        //AlarmReceiver.class为广播接受者
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //得到AlarmManager实例
        AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), (1000 * 60 * 60 * 24), pi);
    }


    /**
     * 关闭提醒
     */
    public static void stopRemind(Context context){

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0,
                intent, 0);
        AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
    }
}
