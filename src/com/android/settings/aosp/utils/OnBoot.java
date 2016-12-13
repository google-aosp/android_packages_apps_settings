package com.android.settings.aosp.util;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.List;
import com.android.settings.util.CMDProcessor;

public class OnBoot extends BroadcastReceiver {

    Context settingsContext = null;
    private static final String TAG = "AOSP_onboot";
    Boolean mSetupRunning = false;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++) {
            if(procInfos.get(i).processName.equals("com.google.android.setupwizard")) {
               mSetupRunning = true;
            }
        }
        if(!mSetupRunning) {
             SharedPreferences sharedpreferences = context.getSharedPreferences("dt2w_pref", Context.MODE_PRIVATE);
             String isDt2wOn = sharedpreferences.getString("dt2w", null);
             if (isDt2wOn != null) {
                 if (isDt2wOn.equals("true")) {
                     CMDProcessor.runShellCommand("echo 1 > /sys/android_touch/doubletap2wake");
                 } else if (isDt2wOn.equals("false")) {
                     CMDProcessor.runShellCommand("echo 0 > /sys/android_touch/doubletap2wake");
                 }
             } else {
                 if (CMDProcessor.runShellCommand("cat sys/android_touch/doubletap2wake").getStdout().contains("1")) {
                     setDt2wEnabled("true");
                 } else {
                     setDt2wEnabled("false");
                 }
             }
        }
    }
    private void setDt2wEnabled(String status) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("dt2w_pref", Context.MODE_PRIVATE).edit();
        editor.putString("dt2w", status);
        editor.apply();
    }
}
