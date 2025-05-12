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

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.GenreFeaturedAdapter;
import com.onlineradiofm.trancemusicradio.adapter.RadioAdapter;
import com.onlineradiofm.trancemusicradio.adapter.RadioFeaturedAdapter;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchStationBinding;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderFeatureBinding;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderGenreTitleBinding;
import com.onlineradiofm.trancemusicradio.model.GenreModel;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.model.TopRadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;

import java.util.ArrayList;

/**
 * @author:YPY Global
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: bl911vn@gmail.com
 * @Website: http://ypyglobal.com
 * Created by dotrungbao on 4/20/18.
 */
public class FragmentTabSearch extends XRadioListFragment<RadioModel> {

    private ItemHeaderFeatureBinding mHeaderViewBinding;
    private TopRadioModel mTopRadioModel;
    private ItemFormSearchStationBinding mSearchViewBinding;

    private RadioFeaturedAdapter mAdapterEditor;
    private RadioFeaturedAdapter mAdapterNewRelease;

    private com.onlineradiofm.trancemusicradio.adapter.GenreFeaturedAdapter mAdapterGenre;

    private ArrayList<GenreModel> mListGenreModels;

    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        viewBinding.layoutTop.setVisibility(View.VISIBLE);
        mAdapterNewRelease = null;
        mAdapterEditor = null;

        if (mTopRadioModel != null) {
            mHeaderViewBinding.layoutHeader.removeAllViews();
            boolean isDark = XRadioSettingManager.isDarkMode(mContext);
            mAdapterGenre = addItemHeader(isDark);
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
            ResultModel<GenreModel> resultGenres =  XRadioNetUtils.getListGenreModel();;
            if (resultGenres != null && resultGenres.isResultOk()) {
                mListGenreModels = resultGenres.getListModels();
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

    private GenreFeaturedAdapter addItemHeader(boolean isDark) {
        ItemHeaderGenreTitleBinding viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_header_genre_title, mHeaderViewBinding.layoutHeader, false);
        mHeaderViewBinding.layoutHeader.addView(viewBinding.getRoot());
        if (isDark) {
            viewBinding.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.dark_text_main_color));
        }
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        viewBinding.recyclerView.setLayoutManager(layoutManager);

        int size = mListGenreModels != null ? mListGenreModels.size() : 0;
        if (size > 0) {
            GenreFeaturedAdapter genreFeaturedAdapter = new GenreFeaturedAdapter(mContext, mListGenreModels);
            genreFeaturedAdapter.setListener(data -> mContext.goToGenreModel(data));
            viewBinding.recyclerView.setAdapter(genreFeaturedAdapter);
            return genreFeaturedAdapter;
        }
        return null;
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
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgSearch, ContextCompat.getColorStateList(mContext, isDark ? R.color.dark_text_hint_color : R.color.light_text_hint_color));
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgAdd, ContextCompat.getColorStateList(mContext, isDark ? R.color.dark_text_hint_color : R.color.light_text_hint_color));

            mSearchViewBinding.layoutEdSearch.setBackgroundResource(isDark ? R.drawable.bg_dark_edit_search : R.drawable.bg_light_edit_search);
        }

    }

    private void setUpSearchHeader(boolean isDark) {
        mSearchViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_form_search_station, viewBinding.layoutTop, false);
        if (isDark) {
            int colorText = ContextCompat.getColor(mContext, R.color.dark_text_main_color);
            int colorHintText = ContextCompat.getColor(mContext, R.color.dark_text_second_color);
            mSearchViewBinding.tvSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setHintTextColor(colorHintText);
            //   mSearchViewBinding.layoutEdSearch.setBackgroundResource(R.drawable.bg_dark_edit_search);
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgSearch, ContextCompat.getColorStateList(mContext, R.color.dark_text_second_color));
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgAdd, ContextCompat.getColorStateList(mContext, R.color.dark_text_second_color));
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
        mSearchViewBinding.imgAdd.setOnClickListener(v -> {
            mContext.goToAddOrEditStation(null);
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
