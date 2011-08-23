package ru.vampirus;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocalService extends Service{
	private WifiManager mainWifi;
	private IntentFilter mIntentFilter;
	private WifiReceiver mWifiReceiver;

	
	private final static String TAG = "BControlService";



	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onCreate() {
        // Display a notification about us starting.  We put an icon in the status bar.
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiReceiver();
        mIntentFilter = new IntentFilter();
        //mIntentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver,mIntentFilter);
        //mainWifi.startScan();
        Log.e(TAG, "local service started");
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }
	
	@Override
    public void onDestroy() {
        // Tell the user we stopped.
		unregisterReceiver(mWifiReceiver);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }
	
    class WifiReceiver extends BroadcastReceiver {

	        public void onReceive(Context c, Intent intent) {
	        	final String action = intent.getAction();
	            Log.d(TAG, "In WifiReceiver: Broadcast Received = " + action);
	            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
	                handleNetworkStateChanged(
	                        (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO));
	            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
	                handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
	                        WifiManager.WIFI_STATE_UNKNOWN));
	            }
	        }
	        
	        private void handleNetworkStateChanged(NetworkInfo info) {
	        	Log.d(TAG,"WifiReceiver.handleNetworkStateChanged: NetworkInfo: "
	                        + info);
	        	Log.d(TAG,"WifiReceiver.handleNetworkStateChanged: connection stat: "
                        + info.getState());
	            switch (info.getState()) {
	                case CONNECTED:
	                    WifiInfo wifiInfo = mainWifi.getConnectionInfo();
	                    if (wifiInfo.getSSID() == null || wifiInfo.getBSSID() == null) {
	                    	Log.d(TAG,"handleNetworkStateChanged: Got connected event but SSID or BSSID are null. SSID: "
	                                + wifiInfo.getSSID()
	                                + ", BSSID: "
	                                + wifiInfo.getBSSID() + ", ignoring event");
	                        return;
	                    }
	                    //onConnected(wifiInfo.getSSID(), wifiInfo.getBSSID());
	                    Log.d(TAG,"handleNetworkStateChanged: ssid:"+wifiInfo.getSSID()+",bssid:"+wifiInfo.getBSSID());
	                    break;

	                case DISCONNECTED:
	                    //onDisconnected();
	                    break;
	            }
	        }

	        private void handleWifiStateChanged(int wifiState) {
	            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
	                //quit();
	            } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
	                //onEnabled();
	            }
	        }
	        
	    }


}
