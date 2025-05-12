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

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.RadioAdapter;
import com.onlineradiofm.trancemusicradio.adapter.RadioFeaturedAdapter;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchBinding;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderFeatureBinding;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderRadioTitleBinding;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.model.TopRadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;

import java.util.ArrayList;


public class FragmentTabLive extends XRadioListFragment<RadioModel> {

    private ItemHeaderFeatureBinding mHeaderViewBinding;
    private TopRadioModel mTopRadioModel;
    private ItemFormSearchBinding mSearchViewBinding;

    private RadioFeaturedAdapter mAdapterEditor;
    private RadioFeaturedAdapter mAdapterNewRelease;

    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        viewBinding.layoutTop.setVisibility(View.VISIBLE);
        mAdapterNewRelease = null;
        mAdapterEditor = null;

        if (mTopRadioModel != null) {
            mHeaderViewBinding.layoutHeader.removeAllViews();
            boolean isDark = XRadioSettingManager.isDarkMode(mContext);
              mAdapterEditor = addItemHeader(mTopRadioModel.getListEditorChoices(), isDark);
            //  mAdapterNewRelease = addItemHeader(mTopRadioModel.getListNewReleases(), isDark);
        }
        RadioAdapter mRadioAdapter = new RadioAdapter(mContext, listObjects, mTopRadioModel != null ? mHeaderViewBinding.getRoot() : null);
        mRadioAdapter.setListener(mObject -> mContext.startPlayingList(mObject, listObjects));
        mRadioAdapter.setOnRadioListener(new RadioAdapter.OnRadioListener() {
            @Override
            public void onFavorite(RadioModel model, boolean isFavorite) {
                mContext.updateFavorite(model, mType, isFavorite);
            }

            @Override
            public void onViewMenu(View mView, RadioModel model, boolean isRecord) {
                mContext.showPopUpMenu(mView, model);
            }
        });
        return mRadioAdapter;
    }


    @Override
    public ResultModel<RadioModel> getListModelFromServer(int offset, int limit) {
        ResultModel<RadioModel> mResultModel = null;
        if (ApplicationUtils.isOnline(mContext)) {
            if (offset == 0) {
                ResultModel<TopRadioModel> mResultTopModel = XRadioNetUtils.getListHeaderTopModels(mContext, TOP_ITEM_PER_PAGE);
                if (mResultTopModel != null && mResultTopModel.isResultOk()) {
                    mTopRadioModel = mResultTopModel.firstModel();
                    if (mTopRadioModel != null) {
                        if (mTopRadioModel.getListEditorChoices() != null) {
                            mContext.mTotalMng.updateFavoriteForList(mTopRadioModel.getListEditorChoices().getListModels(), TYPE_TAB_FAVORITE);
                        }
                        if (mTopRadioModel.getListNewReleases() != null) {
                            mContext.mTotalMng.updateFavoriteForList(mTopRadioModel.getListNewReleases().getListModels(), TYPE_TAB_FAVORITE);
                        }
                    }
                }
            }
            mResultModel = XRadioNetUtils.getListTopRadios(mContext, offset, limit);
        }
        if (mResultModel != null && mResultModel.isResultOk()) {
            mContext.mTotalMng.updateFavoriteForList(mResultModel.getListModels(), TYPE_TAB_FAVORITE);
        }
        return mResultModel;
    }

    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_CARD_LIST);
        boolean isDark = XRadioSettingManager.isDarkMode(mContext);
        setUpHeader();
        setUpSearchHeader(isDark);
    }

    private void setUpHeader() {
        mHeaderViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_header_feature, viewBinding.recyclerView, false);
    }

    @Override
    public void notifyData(int pos) {
        super.notifyData(mHeaderViewBinding != null ? pos + 1 : pos);
    }

    private RadioFeaturedAdapter addItemHeader(@NonNull ResultModel<RadioModel> resultModel, boolean isDark) {
        if (resultModel.firstModel() == null) {
            return null;
        }
        ItemHeaderRadioTitleBinding mHeaderViewBinding = addTitleHeader(resultModel.getMsg(), isDark);
        mHeaderViewBinding.tvSeeMore.setOnClickListener(view -> mContext.goToShowMoreTopModel(resultModel.getMsg(), resultModel.getStatus()));
        setUpRecyclerViewAsHorizontalView(mHeaderViewBinding.recyclerView);

        RadioFeaturedAdapter mAdapter = new RadioFeaturedAdapter(mContext, resultModel.getListModels());
        mHeaderViewBinding.recyclerView.setAdapter(mAdapter);
        mAdapter.setListener(mObject -> mContext.startPlayingList(mObject, resultModel.getListModels()));
        mAdapter.setOnFavoriteListener((model, isFavorite) -> mContext.updateFavorite(model, TYPE_TAB_FAVORITE, isFavorite));
        return mAdapter;

    }

    private ItemHeaderRadioTitleBinding addTitleHeader(String msg, boolean isDark) {
        ItemHeaderRadioTitleBinding viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_header_radio_title, mHeaderViewBinding.layoutHeader, false);
        viewBinding.tvTitle.setText(msg);

        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mHeaderViewBinding.layoutHeader.addView(viewBinding.getRoot(), mLayoutParams);

        viewBinding.tvSeeMore.setVisibility(View.VISIBLE);
        if (isDark) {
            viewBinding.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.dark_text_main_color));
            viewBinding.tvSeeMore.setTextColor(ContextCompat.getColor(mContext, R.color.light_color_accent));
        }
        return viewBinding;
    }


    @Override
    public void updateDarkMode(boolean isDark) {
        super.updateDarkMode(isDark);

        int colorText = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_main_color : R.color.light_text_main_color);
        int colorHintText = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);

        if (mHeaderViewBinding != null) {
            //   mHeaderViewBinding.tvCountry.setTextColor(colorText);
            //   mHeaderViewBinding.tvGenre.setTextColor(colorText);
        }
        if (mSearchViewBinding != null) {
            mSearchViewBinding.tvSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setHintTextColor(colorHintText);
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgSearch, ContextCompat.getColorStateList(mContext, isDark ? R.color.dark_text_hint_color
                    : R.color.light_text_hint_color));

            mSearchViewBinding.layoutEdSearch.setBackgroundResource(isDark ? R.drawable.bg_dark_edit_search : R.drawable.bg_light_edit_search);
        }

    }

    private void setUpSearchHeader(boolean isDark) {
        mSearchViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_form_search, viewBinding.layoutTop, false);
        if (isDark) {
            int colorText = ContextCompat.getColor(mContext, R.color.dark_text_main_color);
            int colorHintText = ContextCompat.getColor(mContext, R.color.dark_text_second_color);
            mSearchViewBinding.tvSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setHintTextColor(colorHintText);
            //   mSearchViewBinding.layoutEdSearch.setBackgroundResource(R.drawable.bg_dark_edit_search);
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgSearch, ContextCompat.getColorStateList(mContext, R.color.dark_text_second_color));

        }
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewBinding.layoutTop.addView(mSearchViewBinding.getRoot(), mLayoutParams);
        mSearchViewBinding.edSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                ApplicationUtils.hiddenVirtualKeyboard(mContext,mSearchViewBinding.edSearch);
                String keyword = mSearchViewBinding.edSearch.getText() != null ? mSearchViewBinding.edSearch.getText().toString() : "";
                if(!TextUtils.isEmpty(keyword)){
                    mSearchViewBinding.edSearch.setText("");
                    mContext.goToSearch(keyword);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void notifyFavorite(long trackId, boolean isFav) {
        super.notifyFavorite(trackId, isFav);
        try {
            if (mContext != null && mTopRadioModel != null) {
                ResultModel<RadioModel> resultModelNew = mTopRadioModel.getListNewReleases();
                if (mAdapterNewRelease != null && resultModelNew != null && resultModelNew.firstModel() != null) {
                    int index = mContext.mTotalMng.updateFavoriteForId(resultModelNew.getListModels(), trackId, isFav);
                    if (index >= 0) {
                        mContext.runOnUiThread(() -> mAdapterNewRelease.notifyItemChanged(index));
                    }
                }
                ResultModel<RadioModel> resultEditor = mTopRadioModel.getListEditorChoices();
                if (mAdapterEditor != null && resultEditor != null && resultEditor.firstModel() != null) {
                    int index = mContext.mTotalMng.updateFavoriteForId(resultEditor.getListModels(), trackId, isFav);
                    if (index >= 0) {
                        mContext.runOnUiThread(() -> mAdapterEditor.notifyItemChanged(index));
                    }
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    ArrayList<RadioModel> doOnNextWithListModel(ArrayList<RadioModel> listModels, boolean isLoadMore) {
        return addNativeAdsToListModel(listModels, isLoadMore);
    }

    @Override
    RadioModel createNativeAdsModel() {
        return new RadioModel(true);
    }
}
