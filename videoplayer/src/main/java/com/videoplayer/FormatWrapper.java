package com.videoplayer;

import com.google.android.exoplayer2.Format;

public class FormatWrapper {
    private int originalIndex;
    private Format format;

    public FormatWrapper(int originalIndex, Format format) {
        this.originalIndex = originalIndex;
        this.format = format;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(int originalIndex) {
        this.originalIndex = originalIndex;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }
}
