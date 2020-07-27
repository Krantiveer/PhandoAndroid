package com.sandesh.epaper.AdsUtil

import android.text.TextUtils
import com.facebook.ads.*
import com.perseverance.phando.AdsUtil.AdConfig
import com.perseverance.phando.AdsUtil.BannerType
import com.perseverance.phando.Session
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.utils.MyLog

class MyNativeAdManager private constructor(): NativeAdListener {
    companion object {
        private var myNativeAdManager: MyNativeAdManager? = null
        fun getInstance(): MyNativeAdManager? {
            if (myNativeAdManager == null)
                myNativeAdManager = MyNativeAdManager()
            return myNativeAdManager
        }
    }

    private var AD_REQUEST_COUNT: Int = 3
    private var adId: String? = null
    private var nativeAdsManager: NativeAdsManager? = null
    private var impressionCounter = 0
    private var isLoadAdsInprogress: Boolean = false
    private var adErrorCode = -1


    init {
       // val requestCount = FirebaseRemoteConfigUtil.getDetailFor(FirebaseRemoteConfigConstant.NATIVE_AD_REQUEST_COUNT_KEY)
        val requestCount = "3"
        if (!requestCount.isEmpty()){
            AD_REQUEST_COUNT=requestCount.toInt()
        }
        adId = AdConfig.FB_AD_1

        try {
            val nativeAdList = AppDatabase.getInstance(Session.instance)?.adModelDao()?.loadAllByIds(BannerType.NATIVE_AD)
            if (nativeAdList != null && nativeAdList.size > 0) {
                val adModel = nativeAdList[0]
                if (adModel != null && !TextUtils.isEmpty(adModel.publisherId)) {
                    adId = adModel.publisherId
                }
            }
        } catch (e: Exception) {
        }


        if (BuildConfig.DEBUG) {
            adId = "YOUR_PLACEMENT_ID"
        }
        if (!TextUtils.isEmpty(adId)) {
            initNativeAdManager()
        }
        MyLog.e("initializing MyNativeAdManager : $AD_REQUEST_COUNT/${getPlacementID(adId)}")
    }

    private fun initNativeAdManager() {

        nativeAdsManager = NativeAdsManager(Session.instance, adId, AD_REQUEST_COUNT!!)
        AdConfig.setFacebookAdSetting()
        nativeAdsManager!!.setListener(object : NativeAdsManager.Listener {
            override fun onAdsLoaded() {
                MyLog.e("NativeAdsManager_Loaded")
                //resetErrorCode()
                isLoadAdsInprogress = false

            }

            override fun onAdError(adError: AdError) {
                adErrorCode = adError.errorCode
                MyLog.e(adError.errorCode.toString() + "/" + adError.errorMessage + " id :" + getPlacementID(adId))
                isLoadAdsInprogress = false

            }


        })
        callLoadAd()
    }

    fun nextNativeAd(): NativeAd? {

        if (nativeAdsManager == null) {
            MyLog.e(" initNativeAdManager is null calling initNativeAdManager()")
            initNativeAdManager()
            return null
        }
        /*check for first time if impressionCounter ==0 */
        if (impressionCounter == 0 && canReloadAd()) {
            val nativeAd = nativeAdsManager!!.nextNativeAd()
            nativeAd?.setAdListener(this)
            if (nativeAd == null) {
                callLoadAd()
            }

            return nativeAd
        }

        val nativeAd = nativeAdsManager!!.nextNativeAd()
        if (impressionCounter > 0 && impressionCounter % AD_REQUEST_COUNT!! == 0 && canReloadAd()) {

            callLoadAd()
        }
        nativeAd?.setAdListener(this)
        return nativeAd
    }

    private fun callLoadAd() {

        //resetErrorCode()
        MyLog.e("Impression counter " + impressionCounter)
        isLoadAdsInprogress = true
        nativeAdsManager!!.loadAds()
        MyLog.e("NativeAdsManager_loadAds")
    }

    private fun resetErrorCode() {
        adErrorCode = -1
    }

    //return true if Ad loading is not in-progress and adErrorCode!=AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE
    private fun canReloadAd(): Boolean {
        if (isLoadAdsInprogress) {
            MyLog.e("isLoadAdsInprogress " + isLoadAdsInprogress)
            return false
        }
        /*if (adErrorCode == AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE) {
            MyLog.info("adErrorCode==AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE " + (adErrorCode == AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE))
            return false
        }*/
        return true
    }

    override fun onMediaDownloaded(p0: Ad?) {

    }

    override fun onError(ad: Ad, adError: AdError) {

    }

    override fun onAdLoaded(ad: Ad) {

    }

    override fun onAdClicked(ad: Ad) {

    }

    override fun onLoggingImpression(ad: Ad) {
        impressionCounter++
        MyLog.e("onLoggingImpression : " + impressionCounter)
    }

    fun getPlacementID(id: String?): String? {
        if (id?.contains("_") != true) {
            return "id not found"

        }
        return id?.split("_")?.get(1)
    }
}
