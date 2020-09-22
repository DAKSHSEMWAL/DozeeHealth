package com.motishare.dozeecodeforhealth.core;


import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;

import com.motishare.dozeecodeforhealth.interfaces.Constants;

public class DozeeHealth extends Application implements Constants, LifecycleObserver {
    /*private FirebaseAnalytics mFirebaseAnalytics;*/
    @Override
    public void onCreate() {
        super.onCreate();
        /*mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);*/
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();
        Log.e("apphash",appSignatureHelper.getAppSignatures().toString());
    }

}