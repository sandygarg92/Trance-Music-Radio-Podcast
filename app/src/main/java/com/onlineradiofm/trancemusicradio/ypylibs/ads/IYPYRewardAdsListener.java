package com.onlineradiofm.trancemusicradio.ypylibs.ads;

/**
 * @author:YPY Global
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: bl911vn@gmail.com
 * @Website: http://ypyglobal.com
 * Created by dotrungbao on 2019-09-10.
 */
public interface IYPYRewardAdsListener {
    void onRewardedVideoLoaded();
    void onRewardedVideoClosed(boolean isRewardCompleted);
    void onReceiveRewardAds();
    void onErrorLoadedRewardAds();
    void onErrorShowRewardAds();
}
