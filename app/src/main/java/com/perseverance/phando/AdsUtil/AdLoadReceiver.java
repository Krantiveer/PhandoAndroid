package com.perseverance.phando.AdsUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by trilokinathyadav on 1/10/2018.
 */

public abstract class AdLoadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        onAdLoaded();
    }
    public abstract void onAdLoaded();
}
