package com.perseverance.phando.AdsUtil;

import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.perseverance.phando.BuildConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by trilokinathyadav on 07-11-2017.
 */

public class AdConfig {
//    public static final String FB_AD_1 = "144081699640724_144082426307318";
    public static final String FB_AD_1 = "";
    private static final List<String> GOOGLE_TEST_DEVICES = new ArrayList<String>() {
        {
//            add("7F0BD7C082CBCF75DFD4B64A5C56A5E2");
        }
    };

    private static final List<String> FACEBOOK_TEST_DEVICES = new ArrayList<String>() {
        {
//            add("c0b8cce3-a5e7-4338-8314-635284d309f6");
//            add("a6eb5114-ba61-4cbd-874d-745c98ab2c8c");
        }
    };

    public static AdRequest getAdRequest() {

        AdRequest.Builder builder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            for (String testDevices : GOOGLE_TEST_DEVICES) {
                builder.addTestDevice(testDevices);
            }
        }

        return builder.build();
    }

    public static void setRequestConfiguration() {

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder()
                        .setTestDeviceIds(GOOGLE_TEST_DEVICES)
                        .build());
    }

    public static void setFacebookAdSetting() {
        if (BuildConfig.DEBUG) {
            List<AdSettings.TestAdType> adTypes = new ArrayList<>();
            adTypes.add(AdSettings.TestAdType.IMG_16_9_APP_INSTALL);
            adTypes.add(AdSettings.TestAdType.IMG_16_9_LINK);
            adTypes.add(AdSettings.TestAdType.VIDEO_HD_16_9_46S_APP_INSTALL);
            adTypes.add(AdSettings.TestAdType.VIDEO_HD_16_9_46S_LINK);
            adTypes.add(AdSettings.TestAdType.VIDEO_HD_16_9_15S_APP_INSTALL);
            adTypes.add(AdSettings.TestAdType.VIDEO_HD_16_9_15S_LINK);

            adTypes.add(AdSettings.TestAdType.VIDEO_HD_9_16_39S_APP_INSTALL);
            adTypes.add(AdSettings.TestAdType.VIDEO_HD_16_9_15S_LINK);
            adTypes.add(AdSettings.TestAdType.VIDEO_HD_9_16_39S_LINK);
            adTypes.add(AdSettings.TestAdType.CAROUSEL_IMG_SQUARE_APP_INSTALL);
            adTypes.add(AdSettings.TestAdType.CAROUSEL_IMG_SQUARE_LINK);

            AdSettings.addTestDevices(FACEBOOK_TEST_DEVICES);
            AdSettings.setTestAdType(adTypes.get((int) ((Math.random() * (((adTypes.size() - 1) - 0) + 1)) + 0)));
        }
    }
}
