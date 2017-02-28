package com.mx.mxs.Utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mx.mxs.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by hz121 on 2016/3/3.
 */
public class WindowUtils {
    private static final String LOG_TAG = "初始化窗口";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    private static Boolean isShown = false;
    private static String responseText;
    private static String md5;
    private static TextView popupWindowView;
    private static String search = "";

    /**
     * 显示弹出框
     */
    public static void showPopupWindow(final Context context, String text) {
        search = text;
        if (isShown) {
            Log.i(LOG_TAG, "已经展示");
            return;
        }
        isShown = true;
        Log.i(LOG_TAG, "显示悬浮窗");
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context);
        final LayoutParams params = new LayoutParams();
        // 错误类型,显示优先级很高
        params.type = LayoutParams.TYPE_SYSTEM_ERROR;
        //params.type = LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置flag
        params.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        // 设置这个弹出框的透明遮罩
        params.format = PixelFormat.TRANSLUCENT;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP;
        params.windowAnimations = android.R.style.Animation_Toast;

        if (!"".equals(responseText)) {

            mWindowManager.addView(mView, params);
            httpRequest();
        }
        Log.i(LOG_TAG, "添加view");

    }

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        if (isShown && null != mView) {
            Log.i(LOG_TAG, "隐藏 PopupWindow");
            mWindowManager.removeView(mView);
            isShown = false;
        }

    }

    /**
     * 点击窗口外部区域可消除
     */
    private static View setUpView(final Context context) {
        Log.i(LOG_TAG, "设置View");
        View view = LayoutInflater.from(context).inflate(R.layout.view_layout, null);
        popupWindowView = (TextView) view.findViewById(R.id.content);// 非透明的内容区域
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = new Rect();
                popupWindowView.getGlobalVisibleRect(rect);
                if (!rect.contains(x, y)) {
                    WindowUtils.hidePopupWindow();
                }
                return false;
            }
        });
        return view;

    }

    /**
     * MD5加密
     */

    public static String md5Encode(String inStr) throws Exception {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    /**
     * 使用volley框架请求数据
     */
    public static void httpRequest() {
        final String UTF8 = "utf-8";
        //申请者开发者id，实际使用时请修改成开发者自己的appid
        final String appId = "20151115000005503";
        //申请成功后的证书token，实际使用时请修改成开发者自己的token
        final String token = "923DUxNVY4dWH1KafzZM";
        String to = "";
        final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";
        //随机数，用于生成md5值
        final Random random = new Random();
        int salt = random.nextInt(10000);
        StringBuilder md5String = new StringBuilder();
        md5String.append(appId).append(search).append(salt).append(token);

        try {
            md5 = md5Encode(md5String.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (search.matches("^[a-zA-Z]*")) {
            to = "zh";
        } else {
            to = "en";
        }
        String request = url + "?" + "q=" + search + "&" + "from=" + "auto" + "&"
                + "to=" + to + "&" + "appid="
                + appId + "&" + "salt=" + salt + "&" + "sign=" + md5;
        System.out.println(request);
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(request, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("成功", response.toString());
                        JSONArray array = null;

                        try {
                            array = (JSONArray) response.get("trans_result");
                            JSONObject dst = (JSONObject) array.get(0);
                            String text = dst.getString("dst");
                            responseText = URLDecoder.decode(text, UTF8);
                            popupWindowView.setText(responseText);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            responseText = "Json解析错误";
                            popupWindowView.setText(responseText);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("错误", error.getMessage(), error);
                responseText = "连接错误";
                popupWindowView.setText(responseText);
            }
        });
        mQueue.add(jsonObjectRequest);
    }
}