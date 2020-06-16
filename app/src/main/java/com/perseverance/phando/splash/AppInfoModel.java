package com.perseverance.phando.splash;

import com.perseverance.phando.retrofit.SuperModel;

/**
 * Created by TrilokiNath on 19-09-2017.
 */

public class AppInfoModel extends SuperModel {
    private AppInfo appInfo;

    public AppInfoModel(AppInfo appInfo, Throwable throwable) {
        super(throwable);
        this.appInfo = appInfo;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }
}
