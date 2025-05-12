/*
 * Copyright (c) 2017. Radio Polska - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://radiopolska.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.onlineradiofm.trancemusicradio.ypylibs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.util.ArrayList;


/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */

public abstract class YPYRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER_VIEW = -1;
    public static final int TYPE_NATIVE_ADS = -2;

    protected int mDarkTextMainColor;
    protected int mDarkTextSecondColor;
    protected int mDarkAccentColor;
    protected int mDarkBgCardColor;
    protected int mDarkDividerColor;
    protected int mDarkBgFlatListColor;
    protected int mDarkRippleColor;

    protected LayoutInflater mInflater;
    private final View mHeaderView;

    public Context mContext;
    protected ArrayList<T> mListModels;
    protected boolean isHasHeader;

    public OnItemClickListener<T> listener;
    protected boolean isDarkMode;

    protected OnMenuListener<T> onMenuListener;
    protected OnFavoriteListener<T> onFavoriteListener;

    private final boolean isSupportRTL;

    public YPYRecyclerViewAdapter(Context mContext, ArrayList<T> listObjects) {
        this(mContext, listObjects, null);

    }

    public YPYRecyclerViewAdapter(Context mContext, ArrayList<T> listObjects, View mHeaderView) {
        this.mContext = mContext;
        this.mListModels = listObjects;
        this.isHasHeader = mHeaderView != null;
        this.mHeaderView = mHeaderView;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mDarkTextMainColor = mContext.getResources().getColor(R.color.dark_list_color_main_text);
        this.mDarkTextSecondColor = mContext.getResources().getColor(R.color.dark_list_color_secondary_text);
        this.mDarkDividerColor = mContext.getResources().getColor(R.color.dark_list_color_divider);
        this.mDarkBgFlatListColor = mContext.getResources().getColor(R.color.dark_list_bg_color);
        this.mDarkRippleColor = mContext.getResources().getColor(R.color.dark_ripple_button_color);

        this.mDarkBgCardColor = mContext.getResources().getColor(R.color.dark_card_background);
        this.mDarkAccentColor = mContext.getResources().getColor(R.color.dark_color_accent);
        this.isDarkMode = XRadioSettingManager.isDarkMode(mContext);

        this.isSupportRTL = ApplicationUtils.isSupportRTL();
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T mItem = null;
        int pos = position;
        int viewType = getItemViewType(position);
        if (isHasHeader) {
            if (viewType != TYPE_HEADER_VIEW) {
                pos = position - 1;
                mItem = mListModels.get(pos);
            }
        }
        else {
            mItem = mListModels.get(position);
        }
        if (viewType != TYPE_HEADER_VIEW) {
            if (mItem instanceof AbstractModel && ((AbstractModel) mItem).isShowAds()) {
                bindNativeAdsHolder((AbstractModel) mItem, (ViewNativeHolder) holder);
            }
            else {
                onBindNormalViewHolder(holder, pos);
            }
        }
    }

    public abstract void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position);

    public abstract RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType);


    @Override
    public int getItemCount() {
        int size = mListModels != null ? mListModels.size() : 0;
        if (isHasHeader) {
            return size + 1;
        }
        else {
            return size;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isHasHeader && position == 0) {
            return TYPE_HEADER_VIEW;
        }
        T mItem = null;
        if (position > 0 && isHasHeader) {
            mItem = mListModels.get(position - 1);
        }
        else {
            if (!isHasHeader) {
                mItem = mListModels.get(position);
            }
        }
        if (mItem instanceof AbstractModel && ((AbstractModel) mItem).isShowAds()) {
            return TYPE_NATIVE_ADS;
        }
        return super.getItemViewType(position);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup v, int viewType) {
        RecyclerView.ViewHolder mHolder;
        if (viewType == TYPE_HEADER_VIEW) {
            mHolder = new ViewHeaderHolder(mHeaderView);
        }
        else if (viewType == TYPE_NATIVE_ADS) {
            View mNativeAds = mInflater.inflate(getResLayoutIdNativeAds(), v, false);
            mHolder = new ViewNativeHolder(mNativeAds);
        }
        else {
            mHolder = onCreateNormalViewHolder(v, viewType);
            if (isDarkMode) {
                updateDarkMode(mHolder);
            }
        }
        return mHolder;
    }

    private void bindNativeAdsHolder(AbstractModel appModel, ViewNativeHolder mHolder) {
        mHolder.mRootLayoutAds.removeAllViews();
        if (!appModel.isRequestAd()) {
            bindAdmobAdsHolder(appModel, mHolder);
        }
        try {
            View mAdview = appModel.getNativeAdView();
            if (mAdview != null && mAdview.getParent() != null) {
                ((ViewGroup) mAdview.getParent()).removeAllViews();
            }
            if (mAdview != null) {
                mHolder.mRootLayoutAds.addView(mAdview);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class ViewHeaderHolder extends RecyclerView.ViewHolder {
        ViewHeaderHolder(View convertView) {
            super(convertView);
        }
    }


    public ArrayList<T> getListObjects() {
        return mListModels;
    }

    public interface OnItemClickListener<T> {
        void onViewDetail(T mObject);
    }

    public void setListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }


    public void updateDarkMode(@NonNull RecyclerView.ViewHolder mHolder) {

    }

    @LayoutRes
    public int getResLayoutIdNativeAds() {
        return R.layout.item_native_ads;
    }

    public String getNativeAdId() {
        return null;
    }

    public String getNativeAdsTestId() {
        return null;
    }


    public boolean showNativeAdsDivider() {
        return true;
    }

    public View createAdmobNativeAdsView(@NonNull AbstractModel appModel, @NonNull ViewGroup mRootLayoutAds, @NonNull NativeAd nativeAd) {
        try {
            NativeAdView adView = (NativeAdView) mInflater.inflate(R.layout.item_native_medium_template, mRootLayoutAds, false);
            appModel.setNativeAdView(adView);
            populateUnifiedNativeAdView(nativeAd, adView);
            View mDivider = adView.findViewById(R.id.divider_native_ads);
            mDivider.setVisibility(showNativeAdsDivider() ? View.VISIBLE : View.INVISIBLE);
            return adView;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void bindAdmobAdsHolder(@NonNull AbstractModel adModel, @NonNull ViewNativeHolder mHolder) {
        AdLoader.Builder builder = new AdLoader.Builder(mContext, getNativeAdId());
        builder.forNativeAd(nativeAd -> {
            try {
                View adView = createAdmobNativeAdsView(adModel, mHolder.mRootLayoutAds, nativeAd);
                if (adView != null) {
                    mHolder.mRootLayoutAds.removeAllViews();
                    mHolder.mRootLayoutAds.addView(adView);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        com.google.android.gms.ads.nativead.NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                YPYLog.e("DCM", "=====>native ads banner error=" + loadAdError);
            }
        }).build();
        adModel.setAdLoader(adLoader);
//        AdRequest mAdRequest = AdMobAdvertisement.buildAdRequest();
//        if (mAdRequest != null) {
//            adLoader.loadAd(mAdRequest);
//        }
    }

    /**
     * Populates a {@link com.google.android.gms.ads.nativead.NativeAdView} object with data from a given
     * {@link com.google.android.gms.ads.nativead.NativeAdView}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
     */
    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        try {
            // Set the media view. Media content will be automatically populated in the media view once
            // adView.setNativeAd() is called.
            MediaView mediaView = adView.findViewById(R.id.ad_media);
            adView.setMediaView(mediaView);

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            // The headline is guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            }
            else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            }
            else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            }
            else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            }
            else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }

            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            }
            else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            }
            else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            }
            else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad. The SDK will populate the adView's MediaView
            // with the media content from this native ad.
            adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            VideoController vc = nativeAd.getMediaContent().getVideoController();

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {
                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class ViewNativeHolder extends RecyclerView.ViewHolder {
        ViewGroup mRootLayoutAds;

        ViewNativeHolder(View convertView) {
            super(convertView);
            mRootLayoutAds = convertView.findViewById(R.id.layout_ad_root);
        }
    }

    public abstract class ViewNormalHolder extends RecyclerView.ViewHolder {

        public ViewNormalHolder(View convertView) {
            super(convertView);
            onFindView(convertView);
            if (isSupportRTL) {
                onUpdateUIWhenSupportRTL();
            }
        }

        public abstract void onFindView(View convertView);

        public void onUpdateUIWhenSupportRTL() {

        }
    }

    public interface OnMenuListener<T> {
        void onShowMenu(View mView, T model);
    }

    public interface OnFavoriteListener<T> {
        void onFavorite(T model, boolean isFavorite);
    }

    public void setOnMenuListener(OnMenuListener<T> onMenuListener) {
        this.onMenuListener = onMenuListener;
    }

    public void setOnFavoriteListener(OnFavoriteListener<T> onFavoriteListener) {
        this.onFavoriteListener = onFavoriteListener;
    }
}
