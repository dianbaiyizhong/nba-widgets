package com.nntk.nba.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

public class UpdateHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent != null && intent.getAction() != null && context != null) {
                if ((intent.getAction().equalsIgnoreCase(Intent.ACTION_MY_PACKAGE_REPLACED)) || (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction()) && (intent.getData().getSchemeSpecificPart().equals(context.getPackageName())))) {
                    Logger.i("UpdateHandler...");
                    WidgetNotification.scheduleWidgetUpdate(context, NbaMapWidget.class);
                    WidgetNotification.scheduleWidgetUpdate(context, ScoreBoardWidget.class);

                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}