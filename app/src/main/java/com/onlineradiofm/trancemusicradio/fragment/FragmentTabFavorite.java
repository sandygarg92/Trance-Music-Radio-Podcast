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

package com.onlineradiofm.trancemusicradio.fragment;

import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.RadioAdapter;
import com.onlineradiofm.trancemusicradio.dataMng.RetroRadioNetUtils;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderCloudFavoriteBinding;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.view.recyclerlib.touchhelp.YPYTouchHelperCallback;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 4/20/18.
 */
public class FragmentTabFavorite extends XRadioListFragment<RadioModel> {

    private ItemTouchHelper mItemTouchHelper;
    private ItemHeaderCloudFavoriteBinding mHeaderViewBinding;

    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        RadioAdapter mRadioAdapter = new RadioAdapter(mContext, listObjects, mHeaderViewBinding.getRoot(), true);
        mRadioAdapter.setListener(mObject -> mContext.startPlayingList(mObject, listObjects));
        mRadioAdapter.setOnMenuListener((fromPosition, toPosition) -> mContext.mTotalMng.saveListCacheModelInThread(mType));
        mRadioAdapter.setOnFavUploadListener(this::uploadRadioToCloud);
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
        mRadioAdapter.setDragStartListener(viewHolder -> {
            if (mItemTouchHelper != null) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        });
        YPYTouchHelperCallback callback = new YPYTouchHelperCallback(mRadioAdapter);
        //TODO disable swipe to delete
        callback.setAllowSwipe(false);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(viewBinding.recyclerView);
        return mRadioAdapter;
    }

    @Override
    public ResultModel<RadioModel> getListModelFromServer(int offset, int limit) {
        return null;
    }

    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_FLAT_LIST);
        int smallMargin = getResources().getDimensionPixelOffset(R.dimen.small_margin);
        viewBinding.recyclerView.setPadding(0, smallMargin, 0, 0);
        setUpHeader();

    }

    private void setUpHeader() {
        mHeaderViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_header_cloud_favorite,
                viewBinding.recyclerView, false);
        boolean isSignedIn = XRadioSettingManager.isSignedIn(mContext);
        mHeaderViewBinding.tvCloudInfo.setText(isSignedIn ? R.string.info_see_favorite_cloud : R.string.info_save_favorite_cloud);
        mHeaderViewBinding.layoutCloudFav.setOnClickListener(v -> mContext.goToCloudFav());

        updateHeaderColor(XRadioSettingManager.isDarkMode(mContext));
        if(ApplicationUtils.isSupportRTL()){
            mHeaderViewBinding.imgChevron.setText(Html.fromHtml(mContext.getString(R.string.icon_chevron_left)));
        }
    }

    @Override
    public void updateDarkMode(boolean isDark) {
        super.updateDarkMode(isDark);
        if (mHeaderViewBinding != null) {
            updateHeaderColor(isDark);
        }

    }

    private void updateHeaderColor(boolean isDark) {
        int darkCard = ContextCompat.getColor(mContext, isDark ? R.color.dark_card_background : R.color.light_card_background);
        int textColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_main_color : R.color.light_list_color_main_text);
        int secondColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_second_color : R.color.light_list_color_secondary_text);
        int bgColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_card_background : R.color.light_list_bg_color);

        mHeaderViewBinding.layoutCloudFav.setBackgroundColor(bgColor);
        mHeaderViewBinding.tvCloudName.setTextColor(textColor);
        mHeaderViewBinding.tvCloudInfo.setTextColor(secondColor);
        mHeaderViewBinding.tvCloudInfo.setSelected(true);
        mHeaderViewBinding.favCardView.setCardBackgroundColor(darkCard);

        mHeaderViewBinding.imgChevron.setTextColor(secondColor);
        mHeaderViewBinding.tvLocalFav.setTextColor(textColor);
    }

    @Override
    public void notifyData() {
        super.notifyData();
        if (mAdapter == null) {
            onRefreshData();
        }
    }

    @Override
    public void notifyData(int pos) {
        super.notifyData(pos + 1);
    }

    private void uploadRadioToCloud(@NonNull RadioModel radioModel) {
        if (mContext != null) {
            if (!XRadioSettingManager.isSignedIn(mContext)) {
                mContext.goToLogin();
                return;
            }
            Observable<ResultModel<AbstractModel>> favObservable = RetroRadioNetUtils.updateCount(mContext, radioModel.getId(), TYPE_COUNT_FAV, 1);
            mContext.addObservableToObserverWithCheckAll(favObservable, resultModel -> {
                if (resultModel != null && resultModel.isResultOk()) {
                    mContext.showToast(R.string.info_upload_fv_done);
                    radioModel.setUploaded(true);
                    notifyData();
                    mContext.mTotalMng.saveListCacheModelInThread(TYPE_TAB_FAVORITE);
                    return;
                }
                mContext.checkUserResultError(resultModel);
            }, null);
        }
    }
}
