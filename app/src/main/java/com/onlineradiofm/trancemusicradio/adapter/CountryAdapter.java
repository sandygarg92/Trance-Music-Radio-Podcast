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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.model.CountryModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;

import java.util.ArrayList;

/**
 * @author:YPY Global
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: bl911vn@gmail.com
 * @Website: http://ypyglobal.com
 * Created by dotrungbao on 4/20/18.
 */
public class CountryAdapter extends YPYRecyclerViewAdapter<CountryModel> {

    public CountryAdapter(Context mContext, ArrayList<CountryModel> listObjects) {
        super(mContext, listObjects);
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        CountryHolder mHolder = (CountryHolder) holder;
        final CountryModel countryModel = mListModels.get(position);
        mHolder.mTvName.setText(countryModel.getName());

        GlideImageLoader.displayImage(mContext, mHolder.mImgBg, countryModel.getArtWork(), R.drawable.ic_live_radio_default);
        mHolder.rootLayout.setOnClickListener(view -> {
            if (listener != null) {
                listener.onViewDetail(countryModel);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(R.layout.item_podcast, v, false);
        return new CountryHolder(mView);
    }

    @Override
    public void updateDarkMode(@NonNull RecyclerView.ViewHolder holder) {
        super.updateDarkMode(holder);
        CountryHolder mHolder = (CountryHolder) holder;
        mHolder.rootLayout.setCardBackgroundColor(mDarkBgCardColor);
        // mHolder.mImgOverLay.setImageResource(R.drawable.bg_dark_header_podcast);
    }


    public class CountryHolder extends ViewNormalHolder {

        public TextView mTvName;
        public ImageView mImgBg;
        public CardView rootLayout;
        // public AppCompatImageView mImgOverLay;

        public CountryHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void onFindView(View convertView) {
            mTvName = convertView.findViewById(R.id.tv_name);
            mImgBg = convertView.findViewById(R.id.img_genre);
            rootLayout = convertView.findViewById(R.id.layoutRoot);
            //  mImgOverLay = convertView.findViewById(R.id.img_overlay);
        }

        @Override
        public void onUpdateUIWhenSupportRTL() {
        }
    }
}
