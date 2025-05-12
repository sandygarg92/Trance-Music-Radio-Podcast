package com.onlineradiofm.trancemusicradio.ypylibs.ads;

import android.app.Activity;
import android.os.Handler;
import android.view.ViewGroup;

import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;




public abstract class YPYAdvertisement {

    public static final long DEFAULT_TIME_OUT_LOAD_ADS = 15000;

    public Activity mContext;

    protected String testId;
    protected String bannerId;
    protected String mediumId;
    protected String interstitialId;

    public Handler mHandlerAds = new Handler();
    public long timeOutLoadAds;
    protected boolean isDestroy;
    String rewardId;

    YPYAdvertisement(Activity mContext,
                     String bannerId, String interstitialId, String testId) {
        this(mContext, bannerId, interstitialId, testId, DEFAULT_TIME_OUT_LOAD_ADS);
    }

    YPYAdvertisement(Activity mContext, String bannerId,
                     String interstitialId, String testId, long timeOutLoadAds) {
        this.mContext = mContext;
        this.timeOutLoadAds = timeOutLoadAds;
        this.bannerId = bannerId;
        this.interstitialId = interstitialId;
        this.testId = testId;
    }

    public abstract void setUpAdBanner(ViewGroup mLayoutAds, boolean isAllowShowAds);
    public abstract void setUpMediumBanner(ViewGroup mLayoutAds, boolean isAllowShowAds);
    public abstract void showInterstitialAd(boolean isAllowShowAds,IYPYCallback mCallback);
    public abstract void showLoopInterstitialAd(IYPYCallback mCallback);
    public abstract void setUpLoopInterstitial();

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }
    public abstract void setUpRewardAd(IYPYRewardAdsListener callback);
    public abstract void showReward(IYPYRewardAdsListener callback);
    public abstract boolean isRewardLoaded();

    public void onDestroy() {
        isDestroy = true;
        mHandlerAds.removeCallbacksAndMessages(null);
    }

}
