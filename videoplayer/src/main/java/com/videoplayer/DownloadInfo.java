package com.videoplayer;

public class DownloadInfo {
    private int status = 0 ;
    private int progress = 0 ;

    public DownloadInfo(int status, int progress) {
        this.status = status;
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
