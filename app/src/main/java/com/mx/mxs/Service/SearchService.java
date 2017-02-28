package com.mx.mxs.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mx.mxs.R;
import com.mx.mxs.Utils.WindowUtils;

public class SearchService extends Service {
    private static final String LOG_TAG = "输出";
    private ClipboardManager clipboardManager;

     static boolean isUnbind=true;

    public SearchService() {
    }

    private MyBinder binder = new MyBinder();


    public class MyBinder extends Binder {
        
        public void setIsUnbind(boolean is) {
            SearchService.isUnbind = is;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                String text = "";
                if (clipboardManager.hasPrimaryClip()) {
                    if ((clipboardManager.getPrimaryClip().getItemAt(0).getText()) instanceof String) {
                        text = (String) clipboardManager.getPrimaryClip().getItemAt(0).getText();
                    } else {
                        // 如果直接使用String,复制短信输入框SpannableString会错误
                        return;
                    }
                } else {
                    return;
                }
                if (text != null&& isUnbind) {
                    //要执行2次才不为空，此处有多个监听
                    WindowUtils.showPopupWindow(getApplicationContext(), text);
                }
                Log.i(LOG_TAG, "已经展示" + text);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

}
