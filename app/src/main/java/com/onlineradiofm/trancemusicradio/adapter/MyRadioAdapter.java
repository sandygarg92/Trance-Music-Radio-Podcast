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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://jamit.com
 * Created by radiopolska on 4/20/18.
 */
public class MyRadioAdapter extends YPYRecyclerViewAdapter<RadioModel> {

    public MyRadioAdapter(Context mContext, ArrayList<RadioModel> listObjects) {
        super(mContext, listObjects);
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyRadioHolder mHolder = (MyRadioHolder) holder;
        final RadioModel radioModel = mListModels.get(position);
        mHolder.mTvName.setText(radioModel.getName());
        mHolder.mImgRadio.setImageResource(radioModel.isLive() ? R.drawable.ic_live_radio_default : R.drawable.ic_mp3_default);
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

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(R.layout.item_my_radio, v, false);
        return new MyRadioHolder(mView);
    }

    @Override
    public void updateDarkMode(@NonNull RecyclerView.ViewHolder mHolder) {
        super.updateDarkMode(mHolder);
        MyRadioHolder mRadioHolder = (MyRadioHolder) mHolder;
        mRadioHolder.mTvName.setTextColor(mDarkTextMainColor);
        mRadioHolder.mImgMenu.setTextColor(mDarkTextSecondColor);

        mRadioHolder.mDivider.setBackgroundColor(mDarkDividerColor);
        mRadioHolder.mLayoutRoot.setBackgroundColor(mDarkBgFlatListColor);
    }

    public class MyRadioHolder extends ViewNormalHolder {

        public TextView mTvName;
        public View mLayoutRoot;
        public View mDivider;
        public AppCompatTextView mImgMenu;
        public AppCompatImageView mImgRadio;

        MyRadioHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void onFindView(View convertView) {
            mTvName = convertView.findViewById(R.id.tv_name);
            mLayoutRoot = convertView.findViewById(R.id.layout_root);
            mDivider = convertView.findViewById(R.id.divider);
            mImgMenu = convertView.findViewById(R.id.img_menu);
            mImgRadio = convertView.findViewById(R.id.img_radio);
            mTvName.setSelected(true);
        }

        @Override
        public void onUpdateUIWhenSupportRTL() {
            mTvName.setGravity(Gravity.END);
        }
    }
}
