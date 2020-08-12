package com.perseverance.phando.resize

import com.perseverance.phando.Session

class ListItemCircularThumbnail() : ThumbnailResizer {
    val WIDTH_SCALE_FACTOR = 0.25
    val HEIGHT_SCALE_FACTOR = 1.00

    private var screenWidth: Int = Session.instance.resources.displayMetrics.widthPixels

    override fun getHeight(): Int {
        return (getWidth() * HEIGHT_SCALE_FACTOR).toInt()
    }

    override fun getWidth(): Int {
        return (screenWidth * WIDTH_SCALE_FACTOR).toInt()
    }

}