
package ru.vampirus.bcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences(
                    "ru.vampirus.bcontrol_preferences", Context.MODE_PRIVATE);
            if (true == prefs.getBoolean("startup_preference", false)) {
                Intent startServiceIntent = new Intent(context, LocalService.class);
                context.startService(startServiceIntent);
            }
        }
    }

}
