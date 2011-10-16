
package ru.vampirus.bcontrol;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocalService extends Service {
    private WifiManager mMainWifi;
    private IntentFilter mIntentFilter;
    private WifiReceiver mWifiReceiver;
    private Timer mTimer;
    private boolean mOfline = true;
    private boolean mIdle = false;
    private String mOldPoint = "";
    private int mOldBrightnes = 255;

    private final static String TAG = "BControlService";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        mMainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiReceiver();
        mIntentFilter = new IntentFilter();
        mTimer = new Timer();
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mWifiReceiver, mIntentFilter);
        Log.d(TAG, "local service started");
        Toast.makeText(this, R.string.service_started, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        unregisterReceiver(mWifiReceiver);
        Log.d(TAG, "local service stopped");
        Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_SHORT).show();
    }

    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "In WifiReceiver: Broadcast Received = " + action);
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                handleNetworkStateChanged((NetworkInfo) intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO));
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN));
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                Log.d(TAG, "onRecive: task shedule");
                mIdle = false;
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG, "TimerTask.run: task shedule");
                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());
                        if (false == mIdle && true == mOfline
                                && mOldPoint.equalsIgnoreCase(
                                        prefs.getString("ssid_preference", ""))) {
                            int brightnes = Settings.System.getInt(getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS, -1);
                            if (255 != brightnes) {
                                Log.d(TAG, "TimerTask.run: brightness on");
                                mOldBrightnes = brightnes;
                                Settings.System.putInt(getContentResolver(),
                                        Settings.System.SCREEN_BRIGHTNESS, 255);
                            }
                        }

                    }
                },
                        // bug
                        // http://code.google.com/p/android/issues/detail?id=2096
                        Integer.parseInt(prefs.getString("delay_ms", "2000")));

            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                mIdle = true;
            }
        }
        /**
         * responds to changes in the state of wifi
         * @param info
         */
        private void handleNetworkStateChanged(NetworkInfo info) {
            Log.d(TAG, "WifiReceiver.handleNetworkStateChanged: NetworkInfo: "
                    + info);
            switch (info.getState()) {
                case CONNECTED:
                    WifiInfo wifiInfo = mMainWifi.getConnectionInfo();
                    if (wifiInfo.getSSID() == null || wifiInfo.getBSSID() == null) {
                        Log.d(TAG,
                                "handleNetworkStateChanged: Got connected event" +
                                        " but SSID or BSSID are null. SSID: "
                                        + wifiInfo.getSSID()
                                        + ", BSSID: "
                                        + wifiInfo.getBSSID() + ", ignoring event");
                        return;
                    }
                    mOldPoint = wifiInfo.getSSID();
                    mOfline = false;
                    Log.d(TAG, "handleNetworkStateChanged: ssid:" + wifiInfo.getSSID() + ",bssid:"
                            + wifiInfo.getBSSID());
                    break;

                case DISCONNECTED:
                    Log.d(TAG, "offline");
                    mOfline = true;
                    break;
            }
        }

        /**
         * responds to the on / off wifi
         * @param wifiState
         */
        private void handleWifiStateChanged(int wifiState) {
            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                Log.d(TAG, "handleWifiStateChanged: disabled");
                mOfline = true;
            } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                Log.d(TAG, "handleWifiStateChanged: enabled");
            }
        }

    }

}
