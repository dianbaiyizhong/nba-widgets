package com.nntk.nba.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

public class BootHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction() != null && context != null) {
                if (intent.getAction().equalsIgnoreCase( Intent.ACTION_BOOT_COMPLETED)) {
                    Logger.i("BootHandler...");
                    WidgetNotification.scheduleWidgetUpdate(context,NbaMapWidget.class);
                    WidgetNotification.scheduleWidgetUpdate(context, ScoreBoardWidget.class);

                    return;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
