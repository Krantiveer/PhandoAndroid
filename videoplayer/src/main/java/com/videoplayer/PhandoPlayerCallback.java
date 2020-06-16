package com.videoplayer;

public interface PhandoPlayerCallback {

    void  updateSettingButton(boolean enable);
    void  onConrolVisibilityChange(int visibility);
    void  onPlayerEvent(PlayerTrackingEvent playerTrackingEvent);
    void  onPlayerProgress(long time);
    void  onDownloadStateChanged();

}
