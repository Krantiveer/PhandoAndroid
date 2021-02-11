package com.perseverance.phando;


import android.util.Log;

import com.perseverance.phando.factory.FeatureConfigInterface;
import com.perseverance.phando.utils.MyLog;

/**
 * Created by TrilokiNath on 13-01-2020.
 */

public class FeatureConfigClass implements FeatureConfigInterface {

    @Override
    public String getBaseUrl() {
        if (Constants.BUILD_PROD){
            return Constants.BASE_URL_PROD;
        }else {
            return Constants.BASE_URL_STAGE;
        }
    }


    @Override
    public String getBaseAPIUrl() {
        MyLog.e("Constants.BUILD_PROD - ", ""+Constants.BUILD_PROD);
        if (Constants.BUILD_PROD){
            return Constants.BASE_URL_PROD+"api/";
        }else {
            return Constants.BASE_URL_STAGE+"api/";
        }
    }


    @Override
    public String getGATrackerId() {
        return Constants.GA_TRACKER_ID;
    }
}
