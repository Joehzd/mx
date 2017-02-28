package com.mx.mxs;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.mx.mxs.Service.SearchService;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hz121 on 2016/1/27.
 */
public class MainFragment extends PreferenceFragment {
    Intent intent;
    private SearchService.MyBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            binder= (SearchService.MyBinder) service;
            binder.setIsUnbind(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            System.out.println("失败");
        }
    };
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting);
        intent = new Intent(getActivity(), SearchService.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean canStart = sharedPreferences.getBoolean("KEY_ENABLE", true);
        if (canStart) {
            getActivity().bindService(intent, connection, Service.BIND_AUTO_CREATE);
        }

        //开关
        findPreference("KEY_ENABLE").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean have = isServiceWork(getActivity(), "com.mx.mxs.Service.SearchService");
                if (!(Boolean) newValue && have) {
                    binder.setIsUnbind(false);
                    getActivity().unbindService(connection);
                   
                } else {
                    getActivity().bindService(intent, connection, Service.BIND_AUTO_CREATE);
                   
                    
                }
                return true;
            }
        });
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(11000);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
