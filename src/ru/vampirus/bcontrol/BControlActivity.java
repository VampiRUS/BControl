
package ru.vampirus.bcontrol;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main Activity
 * 
 * @author vampirus
 */
public class BControlActivity extends Activity {
    private Button mButton1;
    private Button mButton2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mButton1 = (Button) findViewById(R.id.start);
        mButton1.setOnClickListener(mStartListener);
        mButton2 = (Button) findViewById(R.id.stop);
        mButton2.setOnClickListener(mStopListener);

        Button preference = (Button) findViewById(R.id.preference);
        preference.setOnClickListener(mPreferenceListener);

    }

    public void onResume() {
        super.onResume();
        if (false == isMyServiceRunning()) {
            mButton2.setEnabled(false);
        } else {
            mButton1.setEnabled(false);
        }

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        if ("" == prefs.getString("ssid_preference", "")) {
            TextView text = (TextView) findViewById(R.id.hello);
            text.setText(R.string.set_access_point_name);
        } else {
            TextView text = (TextView) findViewById(R.id.hello);
            text.setText(R.string.hello);
        }
    }

    private OnClickListener mStartListener = new OnClickListener() {
        /**
         * Start service
         */
        public void onClick(View v)
        {
            if (null != startService(new Intent(BControlActivity.this,
                    LocalService.class))) {
                mButton1.setEnabled(false);
                mButton2.setEnabled(true);
            }
        }
    };

    private OnClickListener mStopListener = new OnClickListener() {
        /**
         * Stop service
         */
        public void onClick(View v)
        {
            if (true == stopService(new Intent(BControlActivity.this,
                    LocalService.class))) {
                mButton2.setEnabled(false);
                mButton1.setEnabled(true);
            }
        }
    };

    private OnClickListener mPreferenceListener = new OnClickListener() {
        /**
         * Run preferences activity
         */
        public void onClick(View v)
        {
            Intent settingsActivity = new Intent(getBaseContext(),
                    Preferences.class);
            startActivity(settingsActivity);
        }
    };

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("ru.vampirus.bcontrol.LocalService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
