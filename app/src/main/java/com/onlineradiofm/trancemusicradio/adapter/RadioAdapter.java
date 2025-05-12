/*
 * Copyright (c) 2018. Radio Polska - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://radiopolska.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onlineradiofm.trancemusicradio.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.view.recyclerlib.touchhelp.OnStartDragListener;
import com.onlineradiofm.trancemusicradio.ypylibs.view.recyclerlib.touchhelp.YPYTouchHelperAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.view.recyclerlib.touchhelp.YPYTouchHelperViewHolder;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.ADMOB_TEST_DEVICE;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.IS_SMALL_NATIVE;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 4/20/18.
 */
public class RadioAdapter extends YPYRecyclerViewAdapter<RadioModel> implements YPYTouchHelperAdapter {

    private OnRadioListener onRadioListener;
    private boolean isRecordedFiles;
    private OnStartDragListener dragStartListener;
    private OnItemMoveListener onItemMoveListener;
    private OnFavUploadListener onFavUploadListener;
    private final boolean isFromFavorite;
    private final NativeTemplateStyle styles;
    private final RoundedCornersTransformation cornersTransformation;
    private int defaultRes;

    public RadioAdapter(Context mContext, ArrayList<RadioModel> listObjects) {
        this(mContext, listObjects, null, false);
    }

    public RadioAdapter(Context mContext, ArrayList<RadioModel> listObjects, View mHeader) {
        this(mContext, listObjects, mHeader, false);
    }

    public RadioAdapter(Context mContext, ArrayList<RadioModel> listObjects, View mHeader, boolean isFromFavorite) {
        super(mContext, listObjects, mHeader);
        this.isFromFavorite = isFromFavorite;
        ColorDrawable backgroundColor = new ColorDrawable(mContext.getResources().getColor(R.color.white));
        this.styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(backgroundColor).build();
        int floatDimen = mContext.getResources().getDimensionPixelOffset(R.dimen.corner_radius);
        this.cornersTransformation = new RoundedCornersTransformation(floatDimen, 0);
        this.defaultRes = R.drawable.ic_live_radio_default;
    }

    public void setRecordedFiles(boolean recordedFiles) {
        isRecordedFiles = recordedFiles;
        if (isRecordedFiles) {
            defaultRes = R.drawable.ic_record_default;
        }
    }


    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        RadioHolder mHolder = (RadioHolder) holder;
        final RadioModel radioModel = mListModels.get(position);
        mHolder.mTvName.setText(radioModel.getName());

        String tag = radioModel.getTags();
        if (TextUtils.isEmpty(tag) && !TextUtils.isEmpty(radioModel.getBitRate())) {
            tag = String.format(mContext.getString(R.string.format_bitrate), radioModel.getBitRate());
        }
        mHolder.mTvDes.setText(tag);
        mHolder.mBtnFavorite.setLiked(radioModel.isFavorite());

        GlideImageLoader.displayImage(mContext, mHolder.mImgRadio, radioModel.getArtWork(), cornersTransformation, defaultRes);

        if (isFromFavorite) {
            mHolder.mImgUpload.setVisibility(!radioModel.isPodCast() && !radioModel.isUploaded() ? View.VISIBLE : View.GONE);
            mHolder.mLayoutViewCount.setVisibility(View.GONE);
            mHolder.mImgSort.setOnTouchListener((v, event) -> {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    if (dragStartListener != null) {
                        dragStartListener.onStartDrag(holder);
                    }
                }
                return false;
            });
            mHolder.mImgUpload.setOnClickListener(v -> {
                if (onFavUploadListener != null) {
                    onFavUploadListener.onUploadFav(radioModel);
                }
            });
        }
        else {
            mHolder.mLayoutViewCount.setVisibility(radioModel.getViewCount() > 0 ? View.VISIBLE : View.GONE);
            mHolder.mTvNumberView.setText(radioModel.getStrViewCount());
        }

        if (!isRecordedFiles) {
            mHolder.mBtnFavorite.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    if (onRadioListener != null) {
                        onRadioListener.onFavorite(radioModel, true);
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    if (onRadioListener != null) {
                        onRadioListener.onFavorite(radioModel, false);
                    }
                }
            });
        }
        mHolder.mImgMenu.setOnClickListener(view -> {
            if (onRadioListener != null) {
                onRadioListener.onViewMenu(view, radioModel, isRecordedFiles);
            }
        });

        mHolder.mLayoutRoot.setOnClickListener(view -> {
            if (listener != null) {
                listener.onViewDetail(radioModel);
            }
        });

    }

    public void setOnRadioListener(OnRadioListener onRadioListener) {
        this.onRadioListener = onRadioListener;
    }

    public void setDragStartListener(OnStartDragListener dragStartListener) {
        this.dragStartListener = dragStartListener;
    }

    public void setOnItemMoveListener(OnItemMoveListener onMenuListener) {
        this.onItemMoveListener = onMenuListener;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void updateDarkMode(@NonNull RecyclerView.ViewHolder mHolder) {
        super.updateDarkMode(mHolder);
        RadioHolder mRadioHolder = (RadioHolder) mHolder;
        mRadioHolder.mTvName.setTextColor(mDarkTextMainColor);
        mRadioHolder.mTvDes.setTextColor(mDarkTextSecondColor);
        if (isFromFavorite) {
            mRadioHolder.mImgSort.setTextColor(mDarkTextSecondColor);
        }
        mRadioHolder.mImgMenu.setTextColor(mDarkTextSecondColor);
        if (mRadioHolder.mCardView != null) {
            mRadioHolder.mCardView.setCardBackgroundColor(mDarkBgCardColor);
        }
        if (mRadioHolder.mLayoutRoot != null) {
            mRadioHolder.mLayoutRoot.setBackgroundColor(mDarkBgCardColor);
        }
        if (mRadioHolder.mLayoutBottomGrid != null) {
            mRadioHolder.mLayoutBottomGrid.setBackgroundColor(mDarkBgCardColor);
        }
        if (mRadioHolder.mDivider != null) {
            mRadioHolder.mDivider.setBackgroundColor(mDarkDividerColor);
        }
        mRadioHolder.mBtnFavorite.setLikeDrawableRes(R.drawable.ic_heart_dark_mode_36dp);
        mRadioHolder.mBtnFavorite.setUnlikeDrawableRes(R.drawable.ic_heart_outline_white_36dp);
        mRadioHolder.mBtnFavorite.setCircleStartColorInt(mDarkAccentColor);
        mRadioHolder.mBtnFavorite.setCircleStartColorInt(mDarkAccentColor);
        mRadioHolder.mBtnFavorite.setExplodingDotColorsInt(mDarkAccentColor, mDarkAccentColor);
        mRadioHolder.mTvNumberView.setTextColor(mDarkAccentColor);
        mRadioHolder.mIconView.setTextColor(mDarkAccentColor);
        mRadioHolder.mImgUpload.setSupportImageTintList(ColorStateList.valueOf(mDarkTextSecondColor));

    }

    public interface OnItemMoveListener {
        void onItemMoved(int fromPosition, int toPosition);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        try {
            int relFrom = fromPosition;
            int relTo = toPosition;
            if (isHasHeader) {
                if (relFrom == 0 || relTo == 0) {
                    return false;
                }
                relFrom = fromPosition - 1;
                relTo = toPosition - 1;
            }
            if (mListModels != null && mListModels.size() > 0) {
                Collections.swap(mListModels, relFrom, relTo);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onItemMoved(int fromPosition, int toPosition) {
        try {
            if (mListModels != null && mListModels.size() > 0) {
                if (onItemMoveListener != null) {
                    onItemMoveListener.onItemMoved(fromPosition, toPosition);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public boolean hasItemHeader() {
        return isHasHeader;
    }

    public interface OnRadioListener {
        void onFavorite(RadioModel model, boolean isFavorite);

        void onViewMenu(View mView, RadioModel model, boolean isRecord);
    }

    public interface OnFavUploadListener {
        void onUploadFav(RadioModel model);
    }

    public void setOnFavUploadListener(OnFavUploadListener onFavUploadListener) {
        this.onFavUploadListener = onFavUploadListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(R.layout.item_flat_list_radio, v, false);
        return new RadioHolder(mView);
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
    public View createAdmobNativeAdsView(@NonNull AbstractModel appModel, @NonNull ViewGroup mRootLayoutAds, @NonNull NativeAd unifiedNativeAd) {
        if (IS_SMALL_NATIVE) {
            View adView = mInflater.inflate(R.layout.item_native_small_template, mRootLayoutAds, false);
            TemplateView template = adView.findViewById(R.id.my_native_ads);
            template.setStyles(styles);
            template.setNativeAd(unifiedNativeAd);

            appModel.setNativeAdView(adView);
            return adView;
        }
        return super.createAdmobNativeAdsView(appModel, mRootLayoutAds, unifiedNativeAd);
    }


    public class RadioHolder extends ViewNormalHolder implements YPYTouchHelperViewHolder {

        public TextView mTvName;
        public TextView mTvDes;
        public TextView mTvNumberView;
        public ImageView mImgRadio;
        public LinearLayout mLayoutViewCount;
        public View mLayoutRoot;
        public CardView mCardView;
        public View mLayoutBottomGrid;
        public View mDivider;
        public LikeButton mBtnFavorite;
        public AppCompatTextView mImgMenu;
        public AppCompatImageView mImgUpload;
        public AppCompatTextView mIconView;
        public AppCompatTextView mImgSort;

        RadioHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void onFindView(View convertView) {
            mTvName = convertView.findViewById(R.id.tv_name);
            mTvDes = convertView.findViewById(R.id.tv_des);
            mTvNumberView = convertView.findViewById(R.id.tv_number_view);
            mImgRadio = convertView.findViewById(R.id.img_radio);
            mLayoutViewCount = convertView.findViewById(R.id.layout_view_count);
            mLayoutRoot = convertView.findViewById(R.id.layout_root);
            mCardView = convertView.findViewById(R.id.card_view);
            mDivider = convertView.findViewById(R.id.list_divider);
            mBtnFavorite = convertView.findViewById(R.id.btn_favourite);
            mImgMenu = convertView.findViewById(R.id.img_menu);
            mImgUpload = convertView.findViewById(R.id.img_upload);
            mIconView = convertView.findViewById(R.id.icon_view);
            mImgSort = convertView.findViewById(R.id.img_sort);

            mTvName.setSelected(true);
            if (isRecordedFiles) {
                mBtnFavorite.setVisibility(View.GONE);
                mLayoutViewCount.setVisibility(View.GONE);
            }
            mImgMenu.setVisibility(View.VISIBLE);
            mImgSort.setVisibility(isFromFavorite ? View.VISIBLE : View.GONE);
            mImgUpload.setVisibility(isFromFavorite ? View.VISIBLE : View.GONE);

        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
        }

        @Override
        public void onUpdateUIWhenSupportRTL() {
            mTvName.setGravity(Gravity.END);
            mTvDes.setGravity(Gravity.END);
        }
    }


}
