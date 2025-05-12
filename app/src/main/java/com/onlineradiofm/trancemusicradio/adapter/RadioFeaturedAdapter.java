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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 4/20/18.
 */
public class RadioFeaturedAdapter extends YPYRecyclerViewAdapter<RadioModel> {

    public RadioFeaturedAdapter(Context mContext, ArrayList<RadioModel> listObjects) {
        super(mContext, listObjects);
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RadioModel radioModel = mListModels.get(position);
        RadioFeatureHolder mHolder = (RadioFeatureHolder) holder;
        mHolder.mTvName.setText(radioModel.getName());

        mHolder.mBtnFavorite.setLiked(radioModel.isFavorite());

        String artwork = radioModel.getArtWork();
        GlideImageLoader.displayImage(mContext, mHolder.mImgView, artwork, R.drawable.ic_live_radio_default);

        mHolder.mImgView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onViewDetail(radioModel);
            }
        });

        mHolder.mBtnFavorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (onFavoriteListener != null) {
                    onFavoriteListener.onFavorite(radioModel, true);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (onFavoriteListener != null) {
                    onFavoriteListener.onFavorite(radioModel, false);
                }
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(R.layout.item_radio_featured, v, false);
        return new RadioFeatureHolder(mView);
    }

    @Override
    public void updateDarkMode(@NonNull RecyclerView.ViewHolder mHolder) {
        super.updateDarkMode(mHolder);
        RadioFeatureHolder mRadioHolder = (RadioFeatureHolder) mHolder;
        mRadioHolder.mTvName.setTextColor(mDarkTextMainColor);
        mRadioHolder.mLayoutRoot.setCardBackgroundColor(mDarkBgCardColor);
        mRadioHolder.mImgOverLay.setImageResource(R.drawable.bg_dark_header_podcast);

        mRadioHolder.mBtnFavorite.setLikeDrawableRes(R.drawable.ic_heart_dark_mode_36dp);
        mRadioHolder.mBtnFavorite.setUnlikeDrawableRes(R.drawable.ic_heart_outline_white_36dp);
        mRadioHolder.mBtnFavorite.setCircleEndColorRes(R.color.dark_color_accent);
        mRadioHolder.mBtnFavorite.setCircleStartColorRes(R.color.dark_color_accent);
        mRadioHolder.mBtnFavorite.setExplodingDotColorsRes(R.color.dark_color_accent, R.color.dark_color_accent);
    }

    public class RadioFeatureHolder extends ViewNormalHolder {

        public TextView mTvName;
        public AppCompatImageView mImgView;
        public AppCompatImageView mImgOverLay;
        public LikeButton mBtnFavorite;
        public CardView mLayoutRoot;

        RadioFeatureHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void onFindView(View convertView) {
            mTvName = convertView.findViewById(R.id.tv_name);
            mImgView = convertView.findViewById(R.id.img_view);
            mBtnFavorite = convertView.findViewById(R.id.btn_favourite);
            mLayoutRoot = convertView.findViewById(R.id.layout_root);
            mImgOverLay = convertView.findViewById(R.id.img_overlay);
        }

        @Override
        public void onUpdateUIWhenSupportRTL() {
        }
    }

}
