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

package com.onlineradiofm.trancemusicradio.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.PodCastAdapter;
import com.onlineradiofm.trancemusicradio.itunes.model.PodCastModel;
import com.onlineradiofm.trancemusicradio.itunes.model.SearchResultModel;
import com.onlineradiofm.trancemusicradio.itunes.webservice.ITunesNetUtils;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.activity.YPYFragmentActivity;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.StringUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.util.ArrayList;

import androidx.annotation.NonNull;


import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.onlineradiofm.trancemusicradio.itunes.constants.IITunesConstants.ITUNES_ENTITY_PODCAST;
import static com.onlineradiofm.trancemusicradio.itunes.constants.IITunesConstants.ITUNES_MEDIA_TYPE_PODCAST;


public class FragmentDetailListPod extends XRadioListFragment<PodCastModel> {

    private int mTypeUI;
    private String mKeyword;
    private RoundedCornersTransformation cornersTransformation;

    @Override
    public YPYRecyclerViewAdapter<PodCastModel> createAdapter(ArrayList<PodCastModel> listObjects) {
        PodCastAdapter podCastAdapter = new PodCastAdapter(mContext,listObjects, cornersTransformation);
        podCastAdapter.setListener(podCastModel -> mContext.goToPodCastModel(podCastModel));
        return podCastAdapter;
    }

    @Override
    public ResultModel<PodCastModel> getListModelFromServer(int offset, int limit) {
        ResultModel<PodCastModel> mResultModel;
        if(ApplicationUtils.isOnline(mContext) && !TextUtils.isEmpty(mKeyword)){
            String country = XRadioSettingManager.getUserToken(mContext);
            String keyword = StringUtils.urlEncodeString(mKeyword);
            SearchResultModel searchResultModel = ITunesNetUtils.getITunesSearchResultModel(keyword, ITUNES_MEDIA_TYPE_PODCAST,
                    ITUNES_ENTITY_PODCAST, mNumberItemPerPage);
            YPYLog.e("DCM","=========>searchResultModel="+(searchResultModel));
            if(searchResultModel!=null){
                ArrayList<PodCastModel> mListPodCast = searchResultModel.getListPodcasts();
                YPYLog.e("DCM","=========>mListPodCast="+(mListPodCast!=null?mListPodCast.size():0));
                if(mListPodCast == null){
                    mListPodCast = new ArrayList<>();
                }
                mResultModel = new ResultModel<>(200, "");
                mResultModel.setListModels(mListPodCast);
                return mResultModel;
            }
        }

        return null;
    }

    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_CARD_LIST);
        int floatDimen = mContext.getResources().getDimensionPixelOffset(R.dimen.corner_radius);
        this.cornersTransformation = new RoundedCornersTransformation(floatDimen, 0);
    }

    @Override
    public void onExtractData(Bundle args) {
        super.onExtractData(args);
        if (args != null) {
            mKeyword = args.getString(KEY_SEARCH);
            if(mSavedInstanceState!=null){
                if(getActivity()!=null){
                    ((YPYFragmentActivity)getActivity()).setActionBarTitle(mNameScreen);
                }
            }
        }
    }

    public void startSearch(String keyword){
        try{
            if (!TextUtils.isEmpty(keyword) && mContext != null) {
                this.mKeyword = keyword;
                setLoadingData(false);
                startLoadData();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!TextUtils.isEmpty(mKeyword)){
            outState.putString(KEY_SEARCH,mKeyword);
        }
    }

    @Override
    ArrayList<PodCastModel> doOnNextWithListModel(ArrayList<PodCastModel> listModels, boolean isLoadMore) {
        return addNativeAdsToListModel(listModels,isLoadMore);
    }

    @Override
    PodCastModel createNativeAdsModel() {
        return new PodCastModel(true);
    }
}
