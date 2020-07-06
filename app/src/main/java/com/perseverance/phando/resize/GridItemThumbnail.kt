package com.perseverance.phando.resize

import com.perseverance.phando.Session

class GridItemThumbnail (imageOrientation: Int?=0): ThumbnailResizer {
    var WIDTH_SCALE_FACTOR = 0.45
    var HEIGHT_SCALE_FACTOR = 0.56
    init {
        imageOrientation?.let {
            if (it == 1) {
                WIDTH_SCALE_FACTOR =0.45
                HEIGHT_SCALE_FACTOR=1.33
            }
        }
    }

    private var screenWidth :Int = Session.instance.resources.displayMetrics.widthPixels

    override fun getHeight(): Int {
        return (getWidth() * HEIGHT_SCALE_FACTOR).toInt()
    }

    override fun getWidth(): Int {
        return (screenWidth * WIDTH_SCALE_FACTOR).toInt()
    }

}