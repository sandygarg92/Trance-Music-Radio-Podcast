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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.model.GenreModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;

import java.util.ArrayList;


/**
 * @author: Itou
 * @Skype:
 * @Mobile:
 * @Email:
 * @Website:
 * Created by itou on 8/4/23.
 */
public class GenreFeaturedAdapter extends YPYRecyclerViewAdapter<GenreModel> {

    public GenreFeaturedAdapter(Context mContext, ArrayList<GenreModel> listObjects) {
        super(mContext, listObjects);
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        final GenreModel genreModel = mListModels.get(position);
        GenreFeatureHolder mHolder = (GenreFeatureHolder) holder;
        mHolder.mTvName.setText(genreModel.getName());
        GlideImageLoader.displayImage(mContext, mHolder.mImgBg, genreModel.getArtWork(), R.drawable.ic_live_radio_default);
        mHolder.rootLayout.setOnClickListener(view -> {
            if (listener != null) {
                listener.onViewDetail(genreModel);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(R.layout.item_genre_featured, v, false);
        return new GenreFeatureHolder(mView);
    }

    @Override
    public void updateDarkMode(@NonNull RecyclerView.ViewHolder holder) {
        super.updateDarkMode(holder);
    }

    public class GenreFeatureHolder extends ViewNormalHolder {

        public TextView mTvName;
        public ImageView mImgBg;
        public CardView rootLayout;

        GenreFeatureHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void onFindView(View convertView) {
            mTvName = convertView.findViewById(R.id.tv_name);
            mImgBg = convertView.findViewById(R.id.img_overlay);
            rootLayout = convertView.findViewById(R.id.layout_root);
        }

        @Override
        public void onUpdateUIWhenSupportRTL() {
        }
    }

}
