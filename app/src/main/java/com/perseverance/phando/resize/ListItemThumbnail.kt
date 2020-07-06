package com.perseverance.phando.resize

import com.perseverance.phando.Session

class ListItemThumbnail(imageOrientation: Int? = 0) : ThumbnailResizer {
    val WIDTH_SCALE_FACTOR = 0.40
    var HEIGHT_SCALE_FACTOR = 0.56

    init {
        imageOrientation?.let {
            if (it == 1) {
                HEIGHT_SCALE_FACTOR=1.33
            }
        }
    }

    private var screenWidth: Int = Session.instance.resources.displayMetrics.widthPixels

    override fun getHeight(): Int {
        return (getWidth() * HEIGHT_SCALE_FACTOR).toInt()
    }

    override fun getWidth(): Int {
        return (screenWidth * WIDTH_SCALE_FACTOR).toInt()
    }

}