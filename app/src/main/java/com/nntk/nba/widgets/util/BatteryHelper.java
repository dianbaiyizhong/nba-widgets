package com.nntk.nba.widgets.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryHelper {

    public static int getBatteryLevel(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryLevel = 0;
        if (intent != null) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            batteryLevel = (int) (batteryLevel / (float) batteryScale * 100);
        }
        return batteryLevel;
    }
}