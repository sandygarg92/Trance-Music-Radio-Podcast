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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://jamit.com
 * Created by radiopolska on 4/20/18.
 */
public class EpisodeAdapter extends YPYRecyclerViewAdapter<RadioModel> {

    private final RoundedCornersTransformation cornersTransformation;
    private OnEpisodeListener onEpisodeListener;

    public EpisodeAdapter(Context mContext, ArrayList<RadioModel> listObjects, View mHeader,RoundedCornersTransformation cornersTransformation) {
        super(mContext, listObjects, mHeader);
        this.cornersTransformation = cornersTransformation;
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        EpisodeHolder mHolder = (EpisodeHolder) holder;
        final RadioModel radioModel = mListModels.get(position);
        mHolder.mTvName.setText(radioModel.getName());
        String artist = radioModel.getArtist();
        mHolder.mTvDes.setText(!TextUtils.isEmpty(artist) ? artist : radioModel.getTags());

        String img = radioModel.getArtWork();
        GlideImageLoader.displayImage(mContext, mHolder.mImgEpisode, img, cornersTransformation, R.drawable.ic_podcast_default);

        mHolder.mBtnFavorite.setLiked(radioModel.isFavorite());
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



        mHolder.mLayoutRoot.setOnClickListener(view -> {
            if (listener != null) {
                listener.onViewDetail(radioModel);
            }
        });
        mHolder.mImgMenu.setOnClickListener(view -> {
            if (onMenuListener != null) {
                onMenuListener.onShowMenu(view,radioModel);
            }
        });

    }

    public void setOnEpisodeListener(OnEpisodeListener onEpisodeListener) {
        this.onEpisodeListener = onEpisodeListener;
    }

    public interface OnEpisodeListener {
        void onFavorite(RadioModel model, boolean isFavorite);
    }



    public interface OnMenuListener {
        void onShowMenu(View mView, RadioModel model);
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(R.layout.item_flat_list_episode, v, false);
        return new EpisodeHolder(mView);
    }

    @Override
    public void updateDarkMode(@NonNull RecyclerView.ViewHolder mHolder) {
        super.updateDarkMode(mHolder);
        EpisodeHolder mRadioHolder = (EpisodeHolder) mHolder;
        mRadioHolder.mTvName.setTextColor(mDarkTextMainColor);
        mRadioHolder.mTvDes.setTextColor(mDarkTextSecondColor);
        mRadioHolder.mImgMenu.setTextColor(mDarkTextSecondColor);

        mRadioHolder.mDivider.setBackgroundColor(mDarkDividerColor);
        mRadioHolder.mLayoutRoot.setBackgroundColor(mDarkBgFlatListColor);

        mRadioHolder.mBtnFavorite.setLikeDrawableRes(R.drawable.ic_heart_dark_mode_36dp);
        mRadioHolder.mBtnFavorite.setUnlikeDrawableRes(R.drawable.ic_heart_outline_white_36dp);
        mRadioHolder.mBtnFavorite.setCircleStartColorInt(mDarkAccentColor);
        mRadioHolder.mBtnFavorite.setCircleStartColorInt(mDarkAccentColor);
        mRadioHolder.mBtnFavorite.setExplodingDotColorsInt(mDarkAccentColor, mDarkAccentColor);

    }

    public class EpisodeHolder extends ViewNormalHolder {

        public TextView mTvName;
        public TextView mTvDes;
        public ImageView mImgEpisode;
        public View mLayoutRoot;
        public LikeButton mBtnFavorite;
        public View mDivider;
        public AppCompatTextView mImgMenu;

        EpisodeHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void onFindView(View convertView) {
            mTvName = convertView.findViewById(R.id.tv_name);
            mTvDes = convertView.findViewById(R.id.tv_des);
            mImgEpisode = convertView.findViewById(R.id.img_episode);
            mLayoutRoot = convertView.findViewById(R.id.layout_root);
            mBtnFavorite = convertView.findViewById(R.id.btn_favourite);
            mDivider = convertView.findViewById(R.id.divider);
            mImgMenu = convertView.findViewById(R.id.img_menu);
            mTvName.setSelected(true);
        }

        @Override
        public void onUpdateUIWhenSupportRTL() {
            mTvName.setGravity(Gravity.END);
            mTvDes.setGravity(Gravity.END);
        }
    }
}
