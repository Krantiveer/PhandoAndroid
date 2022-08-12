package com.perseverance.phando.AdsUtil;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
//import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.perseverance.phando.db.AppDatabase;
import com.perseverance.phando.utils.MyLog;

import java.util.List;

public class BannerAdLayout extends LinearLayout implements LifecycleObserver {
    int adListIndex = 0;
    List<AdModel> bannerList = null;
//    PublisherAdView adView = null;
    com.facebook.ads.AdView facebookAdView = null;

    public BannerAdLayout(Context context) {
        super(context);
        init(context);
    }

    public BannerAdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerAdLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scanForActivity(context).getLifecycle().addObserver(this);
    }

    public void loadAds(String screenId) {
//        if (BuildConfig.DEBUG && screenId == null) {
//            AdModel adModel = new AdModel();
//            adModel.setPublisherId("/6499/example/banner");
//            loadAdmob(adModel);
//            return;
//        }

        bannerList = AppDatabase.Companion.getInstance(this.getContext()).adModelDao().loadAllByIds(screenId);
        MyLog.e("banner", screenId + " - " + bannerList.toString());
        if (bannerList != null && bannerList.size() > 0) {
            loadBanner();
        }
    }

    private void loadBanner() {
        MyLog.i("banner", adListIndex + "-loadBanner()- size-" + bannerList.size());
        if (adListIndex < bannerList.size()) {
            MyLog.i("banner", adListIndex + "-loadBanner(inside if)- size-" + bannerList.size());
            AdModel banner = bannerList.get(adListIndex);
            if (banner.getPartnerName().equalsIgnoreCase("admob")) {
                MyLog.i("banner", "Loading Admob");
                loadAdmob(banner);
            } else if (banner.getPartnerName().equalsIgnoreCase("facebook")) {
                MyLog.i("banner", "Loading facebook");
                loadFacebook(banner);
            }
        }
    }

    private void loadAdmob(final AdModel banner1) {
//        adView = new PublisherAdView(this.getContext());
        //AdSize customAdSize = new AdSize(250, 250);
        // adView.setAdSizes(AdSize.SMART_BANNER);
//        adView.setAdSizes(AdSize.BANNER);
//        adView.setAdUnitId(banner1.getPublisherId());
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                BannerAdLayout.this.removeAllViews();
//                BannerAdLayout.this.addView(adView);
//                MyLog.i("banner", "Admob loaded  " + banner1.getPublisherId());
//
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                MyLog.i("banner", adListIndex + ">banner admob failed " + banner1.getPublisherId() + " " + errorCode);
//
//                adListIndex++;
//                loadBanner();
//            }
//
//
//        });
//        AdConfig.setRequestConfiguration();
        // Create an ad request.
//        PublisherAdRequest.Builder publisherAdRequestBuilder = new PublisherAdRequest.Builder();
        // Add the PublisherAdView to the view hierarchy.
        // BannerAdLayout.this.removeAllViews();
        // BannerAdLayout.this.addView(adView);

//        adView.loadAd(publisherAdRequestBuilder.build());
    }

    private void loadFacebook(final AdModel banner1) {

        facebookAdView = new com.facebook.ads.AdView(this.getContext(), banner1.getPublisherId(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);

        //commented it currently as this file isn't being used as of now

        //        facebookAdView.setAdListener(new com.facebook.ads.AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                MyLog.i("banner", adListIndex + ">banner facebook failed " + banner1.getPublisherId() + " " + adError.getErrorCode());
//                adListIndex++;
//                loadBanner();
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                MyLog.i("banner", "banner Facebook loaded " + banner1.getPublisherId());
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//
//            }
//        });

        BannerAdLayout.this.removeAllViews();
        BannerAdLayout.this.addView(facebookAdView);
        AdConfig.setFacebookAdSetting();
        facebookAdView.loadAd();

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
//        if (adView != null) {
//            adView.resume();
//        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
//        if (adView != null) {
//            adView.pause();
//        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
//        if (adView != null) {
//            adView.destroy();
//        }
        if (facebookAdView != null) {
            facebookAdView.destroy();
        }
    }

    private AppCompatActivity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof AppCompatActivity)
            return (AppCompatActivity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }
}
