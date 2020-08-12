package com.perseverance.phando.AdsUtil;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.perseverance.phando.R;

import java.util.ArrayList;
import java.util.List;


public class FBAdViewHolder extends RecyclerView.ViewHolder {
    //todo
    private final NativeAdLayout nativeAdContainer = null;
    private Context context;

    public FBAdViewHolder(View itemView) {
        super(itemView);
        // nativeAdContainer = itemView.findViewById(R.id.adContainer);
    }

    public void setAdData(Context context, final NativeAd nativeAd) {
        this.context = context;
        if (nativeAd == null || !nativeAd.isAdLoaded()) {
            // Utils.sendException(context,"FBAdViewHolder > setAdData() : nativeAd == null || !nativeAd.isAdLoaded()");
            return;
        }
        try {
            nativeAdContainer.removeAllViews();
            View adView = LayoutInflater.from(context).inflate(R.layout.tuple_ad_widget_item_new, nativeAdContainer, false);
            nativeAdContainer.addView(adView);

            // Add the AdOptionsView
            LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
            AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdContainer);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);

            // Create native UI using the ad metadata.
            AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
            MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
            TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
            TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
            TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
            TextView nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

            // Set the Text.
            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

            // Set the Text.
            nativeAdTitle.setText(nativeAd.getAdvertiserName());
            if (TextUtils.isEmpty(nativeAd.getAdBodyText())) {
                nativeAdBody.setVisibility(View.GONE);
            } else {
                nativeAdBody.setText(nativeAd.getAdBodyText());
                nativeAdBody.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(nativeAd.getAdSocialContext())) {
                nativeAdSocialContext.setVisibility(View.GONE);
            } else {
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdSocialContext.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(nativeAd.getSponsoredTranslation())) {
                sponsoredLabel.setText("Ad");
            } else {
                sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

            }


            // Create a list of clickable views
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);

            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(
                    adView,
                    nativeAdMedia,
                    nativeAdIcon,
                    clickableViews);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
