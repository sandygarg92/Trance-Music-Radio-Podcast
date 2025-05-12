package com.onlineradiofm.trancemusicradio.ypylibs.ads;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.util.Collections;
import java.util.Map;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: https://polskieradio.app
 * Created by radiopolska on 2/22/18.
 */

public class AdMobAdvertisement extends YPYAdvertisement {

    private AdView adView;
    private InterstitialAd loopInterstitialAd;
    private AdView adMediumView;
    private RewardedAd mRewardedAds;
    private boolean isRewardPlayComplete;
    private boolean isInitialized;
    private boolean isTimeOutInterstitial;
    public static final String ADMOB_ADS = "admob";

    public AdMobAdvertisement(Activity mContext, String bannerId, String interstitialId, String testId) {
        super(mContext, bannerId, interstitialId, testId);
    }

    public void initAds(IYPYCallback mCallback) {
        if (!TextUtils.isEmpty(testId)) {
            RequestConfiguration.Builder mRequestBuilder = new RequestConfiguration.Builder();
            if (testId != null) {
                mRequestBuilder.setTestDeviceIds(Collections.singletonList(testId));
            }
            MobileAds.setRequestConfiguration(mRequestBuilder.build());
        }
        if (ApplicationUtils.isOnline(mContext) && !isInitialized) {
            MobileAds.initialize(mContext, initializationStatus -> {
                try {
                    Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                    for (String adapterClass : statusMap.keySet()) {
                        AdapterStatus status = statusMap.get(adapterClass);
                        Log.e("DCM", "adapter =" + adapterClass + "==>status=" + status);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                isInitialized = true;
                if (mCallback != null) {
                    mCallback.onAction();
                }
            });
            return;
        }
        if (mCallback != null) {
            mCallback.onAction();
        }

    }

    @Override
    public void setUpAdBanner(ViewGroup mLayoutAds, boolean isAllowShowAds) {
        if (isAllowShowAds && ApplicationUtils.isOnline(mContext)
                && mLayoutAds != null && mLayoutAds.getChildCount() == 0 && !TextUtils.isEmpty(bannerId)) {
            if (adView != null) {
                adView.destroy();
            }
            adView = new AdView(mContext);
            adView.setAdUnitId(bannerId);

            AdSize mAdSize = getAdSize();
            YPYLog.e("DCM", "=========>setUpAdBanner=" + mAdSize);
            adView.setAdSize((mAdSize != null && mAdSize != AdSize.INVALID) ? mAdSize : AdSize.BANNER);

            mLayoutAds.addView(adView);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mLayoutAds.setVisibility(View.VISIBLE);

                }
            });
            AdRequest adRequest = buildAdRequest();
            if (adRequest != null) {
                adView.loadAd(adRequest);
            }
            mLayoutAds.setVisibility(View.GONE);
            return;
        }
        if (mLayoutAds != null && mLayoutAds.getChildCount() == 0) {
            mLayoutAds.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUpMediumBanner(ViewGroup mLayoutAds, boolean isAllowShowAds) {
        if (isAllowShowAds && ApplicationUtils.isOnline(mContext) && mLayoutAds != null
                && mLayoutAds.getChildCount() == 0 && !TextUtils.isEmpty(mediumId)) {
            if (adMediumView != null) {
                adMediumView.destroy();
            }
            adMediumView = new AdView(mContext);
            adMediumView.setAdUnitId(mediumId);
            adMediumView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            mLayoutAds.addView(adMediumView);
            adMediumView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mLayoutAds.setVisibility(View.VISIBLE);

                }
            });
            AdRequest adRequest = buildAdRequest();
            if (adRequest != null) {
                adMediumView.loadAd(adRequest);
            }
            mLayoutAds.setVisibility(View.GONE);
            return;
        }
        if (mLayoutAds != null && mLayoutAds.getChildCount() == 0) {
            mLayoutAds.setVisibility(View.GONE);
        }
    }

    @Override
    public void showInterstitialAd(boolean isAllowShowAds, IYPYCallback mCallback) {
        if (ApplicationUtils.isOnline(mContext) && !TextUtils.isEmpty(interstitialId) && isAllowShowAds) {
            AdRequest adRequest = buildAdRequest();
            if (adRequest != null) {
                InterstitialAd.load(mContext, interstitialId, adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        try {
                            mHandlerAds.removeCallbacksAndMessages(null);
                            if(!isTimeOutInterstitial){
                                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        if (mCallback != null) {
                                            mCallback.onAction();
                                        }
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        YPYLog.e("DCM", "========>onAdFailedToShowFullScreenContent=" + adError);
                                        if (mCallback != null) {
                                            mCallback.onAction();
                                        }
                                    }
                                });
                                interstitialAd.show(mContext);
                            }

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        mHandlerAds.removeCallbacksAndMessages(null);
                        YPYLog.e("DCM", "========>onAdFailedToLoad=" + loadAdError);
                        if (!isTimeOutInterstitial) {
                            if (mCallback != null) {
                                mCallback.onAction();
                            }
                        }
                    }
                });
                mHandlerAds.postDelayed(() -> {
                    isTimeOutInterstitial = true;
                    if (mCallback != null) {
                        mCallback.onAction();
                    }
                }, timeOutLoadAds);
                return;
            }
        }
        if (mCallback != null) {
            mCallback.onAction();
        }
    }

    @Override
    public void showLoopInterstitialAd(IYPYCallback mCallback) {
        if (this.loopInterstitialAd != null) {
            this.loopInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    AdMobAdvertisement.this.loopInterstitialAd = null;
                    if (mCallback != null) {
                        mCallback.onAction();
                    }
                    if (!isDestroy) {
                        setUpLoopInterstitial();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    YPYLog.e("DCM", "========>showLoopInterstitialAd onAdFailedToShowFullScreenContent=" + adError);
                    AdMobAdvertisement.this.loopInterstitialAd = null;
                    if (mCallback != null) {
                        mCallback.onAction();
                    }
                }
            });
            this.loopInterstitialAd.show(mContext);
            return;
        }
        if (mCallback != null) {
            mCallback.onAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (adView != null) {
                adView.destroy();
            }
            if (adMediumView != null) {
                adMediumView.destroy();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setUpLoopInterstitial() {
        try {
            if (ApplicationUtils.isOnline(mContext) && loopInterstitialAd == null && !TextUtils.isEmpty(interstitialId)) {
                AdRequest adRequest = buildAdRequest();
                if (adRequest != null) {
                    InterstitialAd.load(mContext, interstitialId, adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            AdMobAdvertisement.this.loopInterstitialAd = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            YPYLog.e("DCM", "========>setUpLoopInterstitial onAdFailedToLoad=" + loadAdError);
                        }
                    });
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setUpRewardAd(IYPYRewardAdsListener callback) {
        try {
            if (ApplicationUtils.isOnline(mContext) && !TextUtils.isEmpty(rewardId)) {
                AdRequest mAdRequest = buildAdRequest();
                if (mAdRequest != null) {
                    RewardedAd.load(mContext, rewardId, mAdRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.e("DCM", "======>onRewardedVideoAdFailedToLoad=" + loadAdError);
                            if (callback != null) {
                                callback.onErrorLoadedRewardAds();
                            }
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            super.onAdLoaded(rewardedAd);
                            mRewardedAds = rewardedAd;
                            if (callback != null) {
                                callback.onRewardedVideoLoaded();
                            }
                        }
                    });
                    isRewardPlayComplete = false;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showReward(IYPYRewardAdsListener callback) {
        try {
            if (isRewardLoaded()) {
                mRewardedAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        YPYLog.e("DCM", "======>onRewardedAdFailedToShow=" + adError);
                        mRewardedAds = null;
                        if (callback != null) {
                            callback.onErrorShowRewardAds();
                        }
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        YPYLog.e("DCM", "======>onAdDismissedFullScreenContent");
                        if (callback != null) {
                            callback.onRewardedVideoClosed(isRewardPlayComplete);
                        }
                        mRewardedAds = null;
                        setUpRewardAd(callback);
                    }
                });
                mRewardedAds.show(mContext, rewardItem -> {
                    YPYLog.e("DCM", "======>onUserEarnedReward");
                    isRewardPlayComplete = true;
                    if (callback != null) {
                        callback.onReceiveRewardAds();
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRewardLoaded() {
        return mRewardedAds != null;
    }

    private AdSize getAdSize() {
        try {
            // Step 2 - Determine the screen width (less decorations) to use for the ad width.
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;

            int adWidth = (int) (widthPixels / density);
            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mContext, adWidth);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AdRequest buildAdRequest() {
        try {
            //TODO GDPR
            AdRequest.Builder mBuilder = new AdRequest.Builder();
            return mBuilder.build();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
