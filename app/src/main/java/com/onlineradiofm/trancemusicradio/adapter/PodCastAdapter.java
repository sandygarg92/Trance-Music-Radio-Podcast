/*
 * Copyright (c) 2018. YPY Global - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://ypyglobal.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onlineradiofm.trancemusicradio.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.itunes.model.PodCastModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.ADMOB_TEST_DEVICE;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.IS_SMALL_NATIVE;

/**
 * @author:YPY Global
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: bl911vn@gmail.com
 * @Website: http://ypyglobal.com
 * Created by dotrungbao on 4/20/18.
 */
public class PodCastAdapter extends YPYRecyclerViewAdapter<PodCastModel> {

    private final NativeTemplateStyle styles;
    private final String titleUnknown;
    private final RoundedCornersTransformation cornersTransformation;

    public PodCastAdapter(Context mContext, ArrayList<PodCastModel> listObjects, RoundedCornersTransformation cornersTransformation) {
        super(mContext, listObjects);
        ColorDrawable backgroundColor = new ColorDrawable(mContext.getResources().getColor(R.color.white));
        this.styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(backgroundColor).build();
        this.titleUnknown = mContext.getString(R.string.title_unknown);
        this.cornersTransformation = cornersTransformation;
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        PodCastHolder mHolder = (PodCastHolder) holder;
        final PodCastModel podCastModel = mListModels.get(position);
        mHolder.mTvName.setText(podCastModel.getName());

        String artistName = podCastModel.getArtistName();
        mHolder.mTvDes.setText(!TextUtils.isEmpty(artistName) ? artistName : titleUnknown);

        String img = podCastModel.getArtWork();
        GlideImageLoader.displayImage(mContext, mHolder.mImgPodCast, img, cornersTransformation, R.drawable.ic_podcast_default);

        mHolder.mLayoutRoot.setOnClickListener(view -> {
            if (listener != null) {
                listener.onViewDetail(podCastModel);
            }
        });

    }

    @Override
    public void updateDarkMode(@NonNull RecyclerView.ViewHolder holder) {
        super.updateDarkMode(holder);
        PodCastHolder mHolder = (PodCastHolder) holder;
        mHolder.mTvName.setTextColor(mDarkTextMainColor);
        mHolder.mTvDes.setTextColor(mDarkTextSecondColor);
        mHolder.mImgChevron.setTextColor(mDarkTextSecondColor);
        mHolder.mLayoutRoot.setBackgroundColor(mDarkBgFlatListColor);
        mHolder.mDivider.setBackgroundColor(mDarkDividerColor);
        mHolder.mRippleLayout.setRippleColor(mDarkRippleColor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(R.layout.item_flat_list_podcast, v, false);
        return new PodCastHolder(mView);
    }

    @Override
    public String getNativeAdId() {
        return mContext.getString(R.string.ad_native_id);
    }

    @Override
    public String getNativeAdsTestId() {
        return ADMOB_TEST_DEVICE;
    }

    @Override
    public View createAdmobNativeAdsView(@NonNull AbstractModel appModel, @NonNull ViewGroup mRootLayoutAds, @NonNull NativeAd nativeAd) {
        if (IS_SMALL_NATIVE) {
            View adView = mInflater.inflate(R.layout.item_native_small_template, mRootLayoutAds, false);
            TemplateView template = adView.findViewById(R.id.my_native_ads);
            template.setStyles(styles);
            template.setNativeAd(nativeAd);

            appModel.setNativeAdView(adView);
            return adView;
        }
        return super.createAdmobNativeAdsView(appModel, mRootLayoutAds, nativeAd);
    }


    public class PodCastHolder extends ViewNormalHolder {

        public TextView mTvName;
        public TextView mTvDes;
        public View mDivider;
        public AppCompatImageView mImgPodCast;
        public AppCompatTextView mImgChevron;
        public MaterialRippleLayout mRippleLayout;
        public View mLayoutRoot;

        PodCastHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void onFindView(View convertView) {
            mTvName = convertView.findViewById(R.id.tv_name);
            mTvDes = convertView.findViewById(R.id.tv_des);
            mImgPodCast = convertView.findViewById(R.id.img_podcast);
            mImgChevron = convertView.findViewById(R.id.img_chevron);
            mLayoutRoot = convertView.findViewById(R.id.layout_root);
            mDivider = convertView.findViewById(R.id.divider);
            mRippleLayout = convertView.findViewById(R.id.layout_ripple_podcast);
            mTvName.setSelected(true);
        }

        @Override
        public void onUpdateUIWhenSupportRTL() {
            mTvName.setGravity(Gravity.END);
            mTvDes.setGravity(Gravity.END);
            mImgChevron.setText(Html.fromHtml(mContext.getString(R.string.icon_chevron_left)));
        }
    }
}
