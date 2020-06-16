package com.perseverance.phando.splash;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TrilokiNath on 19-09-2017.
 */

public class AppInfo {
    @SerializedName("app_type")
    private String appType;

    @SerializedName("app_version")
    private String currentVersion;

    @SerializedName("force_update")
    private String forceUpdate;

    @Override
    public String toString() {
        return "AppInfo{" +
                "appType='" + appType + '\'' +
                ", currentVersion='" + currentVersion + '\'' +
                ", forceUpdate='" + forceUpdate + '\'' +
                '}';
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public int getCurrentVersion() {
        try {
            return Integer.parseInt(currentVersion);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public boolean isForceUpdate() {
        return "1".equals(forceUpdate);
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
