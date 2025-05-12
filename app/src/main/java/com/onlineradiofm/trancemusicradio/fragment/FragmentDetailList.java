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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;

import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.RadioAdapter;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchBinding;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
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
public class FragmentDetailList extends XRadioListFragment<RadioModel> {

    private long mGenreId;
    private String mKeyword;
    private long mCountryId;
    private ItemFormSearchBinding mSearchViewBinding;
    private boolean isFirstTimeShowSearch, isCountry;

    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        if (mType == TYPE_SEARCH) {
            viewBinding.layoutTop.setVisibility(View.VISIBLE);
        }
        RadioAdapter mRadioAdapter = new RadioAdapter(mContext, listObjects);
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
        if (!isCountry) {
            ResultModel<RadioModel> mResultModel = null;
            if (mType == TYPE_DETAIL_GENRE) {
                mResultModel = XRadioNetUtils.getListRadioModel(mContext, mCountryId, mGenreId, offset, limit);
            } else if (mType == TYPE_SEARCH) {
                mResultModel = XRadioNetUtils.searchRadioModel(mContext, mKeyword, offset, limit);
            } else if (mType == TYPE_DETAIL_COUNTRY) {
                mResultModel = XRadioNetUtils.getListRadioModel(mContext, mCountryId, offset, limit);
            }
            if (mResultModel != null && mResultModel.isResultOk()) {
                mContext.mTotalMng.updateFavoriteForList(mResultModel.getListModels(), TYPE_TAB_FAVORITE);
            }

            return mResultModel;
        }

        return ((MainActivity) getActivity()).mResultModelone;
    }

    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_FLAT_LIST);
        if (mType == TYPE_SEARCH) {
            setUpSearchHeader(XRadioSettingManager.isDarkMode(mContext));
        }
    }

    @Override
    public void onExtractData(Bundle args) {
        super.onExtractData(args);
        if (args != null) {
            mGenreId = args.getLong(KEY_GENRE_ID, -1);
            mCountryId = args.getLong(KEY_COUNTRY_ID, -1);
            if (mType == TYPE_SEARCH) {
                mKeyword = args.getString(KEY_SEARCH);
            }
            isCountry = args.getBoolean("isCountry", false);
        }
    }

    public void startSearch(String keyword) {
        try {
            if (!TextUtils.isEmpty(keyword) && mContext != null) {
                this.mKeyword = keyword;
                setLoadingData(false);
                startLoadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_GENRE_ID, mGenreId);
        outState.putLong(KEY_COUNTRY_ID, mCountryId);
        if (!TextUtils.isEmpty(mKeyword)) {
            outState.putString(KEY_SEARCH, mKeyword);
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

    private void setUpSearchHeader(boolean isDark) {
        mSearchViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_form_search, viewBinding.layoutTop, false);
        if (isDark) {
            int colorText = ContextCompat.getColor(mContext, R.color.dark_text_main_color);
            int colorHintText = ContextCompat.getColor(mContext, R.color.dark_text_second_color);
            mSearchViewBinding.tvSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setHintTextColor(colorHintText);
            mSearchViewBinding.layoutEdSearch.setBackgroundResource(R.drawable.bg_dark_edit_search);
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgSearch, ContextCompat.getColorStateList(mContext, R.color.dark_text_second_color));
        }
        mSearchViewBinding.tvSearch.setVisibility(View.GONE);

        int dialogMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.dialog_margin);
        viewBinding.layoutTop.setPadding(0, dialogMargin, 0, dialogMargin);

        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewBinding.layoutTop.addView(mSearchViewBinding.getRoot(), mLayoutParams);
        mSearchViewBinding.edSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ApplicationUtils.hiddenVirtualKeyboard(mContext, mSearchViewBinding.edSearch);
                String keyword = mSearchViewBinding.edSearch.getText() != null ? mSearchViewBinding.edSearch.getText().toString() : "";
                if (!TextUtils.isEmpty(keyword)) {
                    mSearchViewBinding.edSearch.setText("");
                    startSearch(keyword);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void updateDarkMode(boolean isDark) {
        super.updateDarkMode(isDark);
        if (mSearchViewBinding != null) {
            int colorText = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_main_color : R.color.light_text_main_color);
            int colorHintText = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);

            mSearchViewBinding.tvSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setHintTextColor(colorHintText);
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgSearch, ContextCompat.getColorStateList(mContext, isDark ? R.color.dark_text_hint_color
                    : R.color.light_text_hint_color));

            mSearchViewBinding.layoutEdSearch.setBackgroundResource(isDark ? R.drawable.bg_dark_edit_search : R.drawable.bg_light_edit_search);
        }
    }

    @Override
    protected void showLoading(boolean b) {
        super.showLoading(b);
        if (mType == TYPE_SEARCH && b) {
            if (!isFirstTimeShowSearch) {
                isFirstTimeShowSearch = true;
                viewBinding.layoutTop.setVisibility(View.GONE);
            } else {
                viewBinding.layoutTop.setVisibility(View.VISIBLE);
            }
        }

    }
}
