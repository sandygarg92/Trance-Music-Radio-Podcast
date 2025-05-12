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

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.CountryAdapter;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.databinding.ItemFormSearchPodcastBinding;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderSearchBinding;
import com.onlineradiofm.trancemusicradio.model.CountryModel;
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
public class FragmentTabPodcast extends XRadioListFragment<CountryModel> {

    private ItemHeaderSearchBinding mHeaderViewBinding;
    private ItemFormSearchPodcastBinding mSearchViewBinding;
    private ArrayList<CountryModel> listCurrentCountries;
    private ArrayList<CountryModel> listNewCountries;

    @Override
    public YPYRecyclerViewAdapter<CountryModel> createAdapter(ArrayList<CountryModel> listObjects) {
        viewBinding.layoutTop.setVisibility(View.VISIBLE);
        setUpCountries();
        CountryAdapter countryAdapter = new CountryAdapter(mContext, listCurrentCountries);
        countryAdapter.setListener(data -> mContext.goToCountry(data));
        return countryAdapter;
    }


    @Override
    public void setUpUI() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        int numColumn = orientation == ORIENTATION_LANDSCAPE ? 2 : 3;
        setUpUIRecyclerView(UI_FLAT_GRID,numColumn);

        int smallMargin = getResources().getDimensionPixelOffset(R.dimen.small_margin);
        viewBinding.recyclerView.setPadding(smallMargin, 0, smallMargin, 0);

        boolean isDark = XRadioSettingManager.isDarkMode(mContext);
        setUpSearchHeader(isDark);
        setUpHeader(isDark);
    }

    @Override
    public ResultModel<CountryModel> getListModelFromServer(int offset, int limit) {
        if (ApplicationUtils.isOnline(mContext)) {
            ResultModel<CountryModel> resultCountries = XRadioNetUtils.getListCountryModel();
            if (resultCountries != null && resultCountries.isResultOk()) {
                listNewCountries = resultCountries.getListModels();
            }
            return XRadioNetUtils.getListCountryModel();
        }
        return null;
    }



    private void setUpSearchHeader(boolean isDark) {
        mSearchViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_form_search_podcast, viewBinding.layoutTop, false);
        if (isDark) {
            int colorText = ContextCompat.getColor(mContext, R.color.dark_text_main_color);
            int colorHintText = ContextCompat.getColor(mContext, R.color.dark_text_second_color);
            mSearchViewBinding.tvSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setHintTextColor(colorHintText);
            mSearchViewBinding.layoutEdSearch.setBackgroundResource(R.drawable.bg_dark_edit_search);
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
                    mContext.goToSearchPod(keyword);
                }
                return true;
            }
            return false;
        });
    }

    private void setUpHeader(boolean isDark) {
        mHeaderViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_header_search, viewBinding.recyclerView, false);
        if (isDark) {
            int colorText = ContextCompat.getColor(mContext, R.color.dark_text_main_color);
            mHeaderViewBinding.tvCountry.setTextColor(colorText);
            mHeaderViewBinding.tvGenre.setTextColor(colorText);
        }
        setUpRecyclerViewAsHorizontalView(mHeaderViewBinding.recyclerView);
    }

    @Override
    public void updateDarkMode(boolean isDark) {
        super.updateDarkMode(isDark);

        int colorText = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_main_color : R.color.light_text_main_color);
        int colorHintText = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);

        if (mHeaderViewBinding != null) {
            mHeaderViewBinding.tvCountry.setTextColor(colorText);
            mHeaderViewBinding.tvGenre.setTextColor(colorText);
        }
        if (mSearchViewBinding != null) {
            mSearchViewBinding.tvSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setTextColor(colorText);
            mSearchViewBinding.edSearch.setHintTextColor(colorHintText);
            ImageViewCompat.setImageTintList(mSearchViewBinding.imgSearch, ContextCompat.getColorStateList(mContext, isDark ? R.color.dark_text_hint_color : R.color.light_text_hint_color));

            mSearchViewBinding.layoutEdSearch.setBackgroundResource(isDark ? R.drawable.bg_dark_edit_search : R.drawable.bg_light_edit_search);
        }

    }

    private void setUpCountries() {

        mHeaderViewBinding.recyclerView.setAdapter(null);
        if (listCurrentCountries != null) {
            listCurrentCountries.clear();
            listCurrentCountries = null;
        }
        this.listCurrentCountries = listNewCountries;
        int size = listCurrentCountries != null ? listCurrentCountries.size() : 0;
        mHeaderViewBinding.layoutCountries.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        if (size > 0) {
            CountryAdapter countryAdapter = new CountryAdapter(mContext, listCurrentCountries);
            countryAdapter.setListener(data -> mContext.goToCountry(data));
            mHeaderViewBinding.recyclerView.setAdapter(countryAdapter);
        }
    }

    @Override
    public void notifyData(int pos) {
        super.notifyData(mHeaderViewBinding != null ? pos + 2 : pos);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listCurrentCountries != null) {
            listCurrentCountries.clear();
            listCurrentCountries = null;
        }
    }
}

