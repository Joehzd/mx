package com.mx.mxs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Animator.AnimatorListener {
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.activity_splish);
        mImageView = (ImageView) findViewById(R.id.image);
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.fade_in);
        animatorSet.setTarget(mImageView);
        animatorSet.start();
        animatorSet.addListener(this);
        checkNetwork(this);
        checkPermission();
        
    }
    public void checkPermission(){
        int result=getApplicationContext().checkCallingOrSelfPermission("android.permission.SYSTEM_ALERT_WINDOW");
        
        if (result!=PackageManager.PERMISSION_GRANTED){
            toastShow(getString(R.string.permission_text));
        }
    }
    public void checkNetwork(Context context){
        if (context!=null){
            ConnectivityManager managerCompat= (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=managerCompat.getActiveNetworkInfo();
            if (networkInfo!=null){
                boolean isConnect=networkInfo.isAvailable();
                if (!isConnect){
                    toastShow(getString(R.string.no_network));
                }
                
            }else {
                toastShow(getString(R.string.network_err));
            }
           
        }
    }

    private void toastShow(String s) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, null);
        TextView textView= (TextView) layout.findViewById(R.id.toast_text);
        Toast toast= new Toast(MainActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        textView.setText(s);
        toast.setGravity(Gravity.TOP,0,0);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Intent i=new Intent(MainActivity.this,ShowActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onAnimationCancel(Animator animation) {
        
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
    
}
