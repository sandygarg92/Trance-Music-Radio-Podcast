/*
 * Copyright (c) 2017. Radio Polska - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://radiopolska.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.onlineradiofm.trancemusicradio.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.databinding.FragmentRecyclerviewBinding;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.AdMobAdvertisement;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.fragment.YPYFragment;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;
import com.onlineradiofm.trancemusicradio.ypylibs.view.YPYRecyclerView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/25/17.
 */

public abstract class XRadioListFragment<T> extends YPYFragment<FragmentRecyclerviewBinding> implements IRadioConstants
        , YPYRecyclerView.OnDBRecyclerViewListener {

    protected MainActivity mContext;
    ArrayList<T> mListModels;

    int mType = -1;
    private boolean isDestroy;
    YPYRecyclerViewAdapter<T> mAdapter;

    private boolean isAllowLoadMore;
    private boolean isAllowShowHeader;
    private boolean isAllowRefresh = true;
    private boolean isTab;

    int mNumberItemPerPage = NUMBER_ITEM_PER_PAGE;
    private int mMaxPage = MAX_PAGE;
    private boolean isAllowReadCache;
    private boolean isOfflineData;
    private boolean isGetFromCacheWhenNoData;

    @NonNull
    @Override
    protected FragmentRecyclerviewBinding getViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentRecyclerviewBinding.inflate(inflater, container, false);
    }

    @Override
    public void findView() {
        mContext = (MainActivity) requireActivity();
        viewBinding.swipeRefresh.setOnRefreshListener(this::onRefreshData);
        viewBinding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.light_color_accent));
        viewBinding.swipeRefresh.setEnabled(isAllowRefresh);

        setUpUI();
        updateDarkMode(XRadioSettingManager.isDarkMode(mContext));

        if (isAllowLoadMore) {
            viewBinding.recyclerView.setOnDBListViewListener(this);
        }

        if (!isTab || isFirstInTab()) {
            startLoadData();
        }

    }

    void onRefreshData() {
        if (mContext != null) {
            if (viewBinding.progressBar1.getVisibility() == View.VISIBLE) {
                viewBinding.swipeRefresh.setRefreshing(false);
                return;
            }
            if (isAllowLoadMore && viewBinding.loadingFooter.getRoot().getVisibility() == View.VISIBLE) {
                viewBinding.swipeRefresh.setRefreshing(false);
                return;
            }
            onReceiveData(true, false);
        }

    }


    @Override
    public void hideFooterView() {
        viewBinding.loadingFooter.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void showFooterView() {
        viewBinding.loadingFooter.getRoot().setVisibility(View.VISIBLE);
    }


    @Override
    public void startLoadData() {
        super.startLoadData();
        if (mContext != null && !isLoadingData()) {
            setLoadingData(true);
            onReceiveData(false, true);
        }
    }

    private void onReceiveData(boolean isNeedRefresh, boolean isNeedHideRecycler) {
        if (isNeedRefresh) {
            viewBinding.recyclerView.onResetData(false);
        }
        if (isNeedHideRecycler) {
            viewBinding.recyclerView.setVisibility(View.GONE);
            showLoading(true);
        }
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            ArrayList<T> mListModels = null;
            ResultModel<T> resultModel = null;
            boolean isNeedCheckOnline = false;
            if (isOfflineData || (!isNeedRefresh && isAllowReadCache && mType > 0 && !ApplicationUtils.isOnline(mContext))) {
                mListModels = getDataFromCache();
            }
            if (!isOfflineData && (mListModels == null || isNeedRefresh)) {
                isNeedCheckOnline = true;
                resultModel = getListModelFromServer(0, mNumberItemPerPage);
                if (resultModel != null && resultModel.isResultOk()) {
                    if (isAllowReadCache && mType > 0) {
                        mContext.mTotalMng.setListCacheData(mType, resultModel.getListModels());
                        mListModels = (ArrayList<T>) mContext.mTotalMng.getListData(mType);
                    }
                    if (mListModels == null || mListModels.size() == 0) {
                        mListModels = resultModel.getListModels();
                    }
                }
                else {
                    if (isGetFromCacheWhenNoData) {
                        mListModels = getDataFromCache();
                    }
                }
            }
            ArrayList<T> mListNewModels = doOnNextWithListModel(mListModels, false);
            ResultModel<T> finalResultModel = resultModel;
            boolean finalIsNeedCheckOnline = isNeedCheckOnline;

            mContext.runOnUiThread(() -> {
                try {
                    if (isDestroy) return;
                    showLoading(false);
                    viewBinding.swipeRefresh.setRefreshing(false);
                    if (finalIsNeedCheckOnline && (finalResultModel == null || !finalResultModel.isResultOk())) {
                        if (isGetFromCacheWhenNoData) {
                            setUpInfo(mListNewModels);
                            return;
                        }
                        String msg = finalResultModel != null ? finalResultModel.getMsg() : null;
                        if (!TextUtils.isEmpty(msg)) {
                            showResult(msg);
                            return;
                        }
                        int msgId = !ApplicationUtils.isOnline(mContext) ? R.string.info_lose_internet : R.string.info_server_error;
                        showResult(msgId);
                        return;
                    }
                    setUpInfo(mListNewModels);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            });
        });
    }

    @Override
    public void onLoadNextModel() {
        if (!ApplicationUtils.isOnline(mContext)) {
            hideFooterView();
            viewBinding.swipeRefresh.setRefreshing(false);
            mContext.showToast(R.string.info_lose_internet);
            viewBinding.recyclerView.setStartAddingPage(false);
            return;
        }
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            int originalSize = mListModels != null ? mListModels.size() : 0;
            if (mListModels != null) {
                for (T model : mListModels) {
                    if (model instanceof AbstractModel) {
                        if (((AbstractModel) model).isShowAds()) {
                            originalSize--;
                        }
                    }
                }
            }
            ResultModel<T> resultModel = getListModelFromServer(originalSize, mNumberItemPerPage);
            ArrayList<T> listLoadMores = (resultModel != null && resultModel.isResultOk()) ? resultModel.getListModels() : null;
            final int sizeLoaded = listLoadMores != null ? listLoadMores.size() : 0;
            listLoadMores = doOnNextWithListModel(listLoadMores, true);

            final boolean isLoadOkNumberItem = sizeLoaded >= mNumberItemPerPage;
            ArrayList<T> finalListLoadMores = listLoadMores;

            int finalOriginalSize = originalSize;
            mContext.runOnUiThread(() -> {
                try {
                    if (isDestroy) return;
                    hideFooterView();
                    boolean isAllowLoadPage = isLoadOkNumberItem && viewBinding.recyclerView.getCurrentPage() < mMaxPage;
                    YPYLog.e(TAG, "=========>isLoadOkNumberItem=" + isLoadOkNumberItem + "==>isAllowLoadPage=" + isAllowLoadPage);
                    viewBinding.recyclerView.setAllowAddPage(isAllowLoadPage);
                    if (isAllowLoadPage) {
                        int page = viewBinding.recyclerView.getCurrentPage() + 1;
                        viewBinding.recyclerView.setCurrentPage(page);
                    }

                    if (sizeLoaded > 0) {
                        mListModels.addAll(finalListLoadMores);
                        if (mAdapter != null) {
                            mAdapter.notifyItemRangeChanged(finalOriginalSize, sizeLoaded);
                        }
                        mContext.mTotalMng.saveListCacheModelInThread(mType);
                    }
                    viewBinding.recyclerView.setStartAddingPage(false);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            });
        });
    }

    private void setUpInfo(ArrayList<T> listObjects) {
        if (isDestroy) return;
        this.viewBinding.recyclerView.setAdapter(null);
        if (!isOfflineData) {
            destroyNativeAds();
            if (this.mListModels != null) {
                this.mListModels.clear();
                this.mListModels = null;
            }
        }
        this.mListModels = listObjects;
        int size = mListModels != null ? mListModels.size() : 0;
        if (size > 0 || (isAllowShowHeader && mListModels != null)) {
            viewBinding.recyclerView.setVisibility(View.VISIBLE);
            mAdapter = createAdapter(listObjects);
            if (mAdapter != null) {
                viewBinding.recyclerView.setAdapter(mAdapter);
            }
            if (isAllowLoadMore) {
                boolean b = checkAllowLoadMore(size);
                viewBinding.recyclerView.setAllowAddPage(b);
                if (b) {
                    int page = viewBinding.recyclerView.getCurrentPage() + 1;
                    viewBinding.recyclerView.setCurrentPage(page);
                }
            }

        }
        if (!isAllowShowHeader) {
            updateInfo();
        }
    }

    private ArrayList<T> getDataFromCache() {
        ArrayList<T> mListModels = (ArrayList<T>) mContext.mTotalMng.getListData(mType);
        if (mListModels == null) {
            mContext.mTotalMng.readCacheData(mType);
            mListModels = (ArrayList<T>) mContext.mTotalMng.getListData(mType);
        }
        return mListModels;
    }

    public abstract YPYRecyclerViewAdapter<T> createAdapter(ArrayList<T> listObjects);

    public abstract ResultModel<T> getListModelFromServer(int offset, int limit);

    public abstract void setUpUI();


    private boolean checkAllowLoadMore(int sizeLoaded) {
        int page = (int) Math.floor((float) sizeLoaded / (float) mNumberItemPerPage);
        return page < mMaxPage && sizeLoaded >= mNumberItemPerPage;
    }

    @Override
    public void onDestroy() {
        isDestroy = true;
        try {
            if (!isOfflineData) {
                destroyNativeAds();
                viewBinding.swipeRefresh.setRefreshing(false);
                viewBinding.swipeRefresh.setEnabled(false);
                viewBinding.recyclerView.setAdapter(null);
                if (mListModels != null) {
                    mListModels.clear();
                    mListModels = null;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void updateInfo() {
        boolean b = mListModels != null && mListModels.size() > 0;
        viewBinding.tvNoResult.setVisibility(b ? View.GONE : View.VISIBLE);
        if (!b) {
            viewBinding.tvNoResult.setText(R.string.title_no_data);
        }
    }

    protected void showLoading(boolean b) {
        viewBinding.progressBar1.setVisibility(b ? View.VISIBLE : View.GONE);
        if (b) {
            viewBinding.recyclerView.setVisibility(View.GONE);
            viewBinding.layoutTop.setVisibility(View.GONE);
            viewBinding.tvNoResult.setVisibility(View.GONE);
        }
    }

    private void showResult(int resId) {
        if (mContext != null) {
            showResult(mContext.getString(resId));
        }
    }

    private void showResult(String msg) {
        viewBinding.tvNoResult.setText(msg);
        if (mAdapter == null) {
            viewBinding.tvNoResult.setVisibility(View.VISIBLE);
        }
        else {
            viewBinding.tvNoResult.setVisibility(View.GONE);
            mContext.showToast(msg);
        }
    }

    @Override
    public void onExtractData(Bundle args) {
        super.onExtractData(args);
        if (args != null) {
            mType = args.getInt(KEY_TYPE_FRAGMENT, -1);
            isAllowLoadMore = args.getBoolean(KEY_ALLOW_MORE, false);
            isAllowReadCache = args.getBoolean(KEY_ALLOW_READ_CACHE, false);
            isTab = args.getBoolean(KEY_IS_TAB, false);
            isAllowRefresh = args.getBoolean(KEY_ALLOW_REFRESH, true);
            isAllowShowHeader = args.getBoolean(KEY_ALLOW_SHOW_HEADER, false);
            mNumberItemPerPage = args.getInt(KEY_NUMBER_ITEM_PER_PAGE, NUMBER_ITEM_PER_PAGE);
            mMaxPage = args.getInt(KEY_MAX_PAGE, MAX_PAGE);
            isOfflineData = args.getBoolean(KEY_OFFLINE_DATA, false);
            isGetFromCacheWhenNoData = args.getBoolean(KEY_READ_CACHE_WHEN_NO_DATA, false);

        }
    }

    @Override
    public void notifyData() {
        super.notifyData();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (!isAllowShowHeader) {
                updateInfo();
            }
        }
    }


    @Override
    public void notifyData(int pos) {
        super.notifyData(pos);
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(pos);
        }
    }

    public void notifyFavorite(long trackId, boolean isFav) {
        if (mContext != null && mListModels != null && mListModels.size() > 0) {
            int index = mContext.mTotalMng.updateFavoriteForId((ArrayList<RadioModel>) mListModels, trackId, isFav);
            if (index >= 0) {
                mContext.runOnUiThread(() -> notifyData(index));
            }
        }
    }
    void setUpUIRecyclerView(int mTypeUI) {
        setUpUIRecyclerView(mTypeUI,2);
    }

    void setUpUIRecyclerView(int mTypeUI, int numColumn) {
        try {
            int dialogMargin = getResources().getDimensionPixelOffset(R.dimen.dialog_margin);
            int smallMargin = getResources().getDimensionPixelOffset(R.dimen.small_margin);

            if (mTypeUI == UI_FLAT_LIST || mTypeUI == UI_CARD_LIST) {
                mContext.setUpRecyclerViewAsListView(viewBinding.recyclerView, mTypeUI == UI_CARD_LIST ? mContext.getSupportDrawable(R.drawable.alpha_divider_small_verti) : null);
            }
            else if (mTypeUI == UI_CARD_GRID || mTypeUI == UI_FLAT_GRID) {
                Drawable mDrawableVer = null;
                try {
                    if (mTypeUI == UI_FLAT_GRID) {
                        mDrawableVer = mContext.getSupportDrawable(R.drawable.alpha_divider_large_verti);
                    }
                    else {
                        mDrawableVer = mContext.getSupportDrawable(R.drawable.alpha_divider_small_verti);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                mContext.setUpRecyclerViewAsGridView(viewBinding.recyclerView, numColumn, mDrawableVer, null);
                GridLayoutManager layoutManager = (GridLayoutManager) viewBinding.recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            RecyclerView.Adapter<?> mAdapter = viewBinding.recyclerView.getAdapter();
                            if (mAdapter != null) {
                                if (mAdapter.getItemViewType(position) == YPYRecyclerViewAdapter.TYPE_HEADER_VIEW) {
                                    return numColumn;
                                }
                            }
                            return 1;
                        }
                    });
                }

            }
            else if (mTypeUI == UI_MAGIC_GRID) {
                mContext.setUpRecyclerViewAsStaggered(viewBinding.recyclerView, 2, mContext.getSupportDrawable(R.drawable.alpha_divider_small_verti), null);
            }
            if (mTypeUI != UI_FLAT_LIST) {
                viewBinding.recyclerView.setPadding(smallMargin, dialogMargin, smallMargin, 0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean isCheckBack() {
        if (!isOfflineData && viewBinding.loadingFooter.getRoot().getVisibility() == View.VISIBLE) {
            return true;
        }
        if (viewBinding.progressBar1.getVisibility() == View.VISIBLE) {
            return true;
        }
        if (isRecyclerScrolling()) {
            return true;
        }
        return super.isCheckBack();
    }

    private boolean isRecyclerScrolling() {
        try {
            return viewBinding.recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TYPE_FRAGMENT, mType);
        outState.putBoolean(KEY_ALLOW_MORE, isAllowLoadMore);
        outState.putBoolean(KEY_ALLOW_READ_CACHE, isAllowReadCache);
        outState.putBoolean(KEY_IS_TAB, isTab);
        outState.putBoolean(KEY_ALLOW_REFRESH, isAllowRefresh);
        outState.putBoolean(KEY_ALLOW_SHOW_HEADER, isAllowShowHeader);
        outState.putInt(KEY_NUMBER_ITEM_PER_PAGE, NUMBER_ITEM_PER_PAGE);
        outState.putInt(KEY_MAX_PAGE, MAX_PAGE);
        outState.putBoolean(KEY_OFFLINE_DATA, isOfflineData);
        outState.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, isGetFromCacheWhenNoData);

    }

    @Override
    public void updateDarkMode(boolean isDark) {
        super.updateDarkMode(isDark);
        try {
            if (mContext != null) {
                int colorProgress = ContextCompat.getColor(mContext, isDark ? R.color.dark_progressbar_color : R.color.light_progressbar_color);
                int colorRefresh = ContextCompat.getColor(mContext, isDark ? R.color.dark_color_accent : R.color.light_color_accent);
                int bgLoadMore = ContextCompat.getColor(mContext, isDark ? R.color.dark_load_more_bg_color : R.color.light_load_more_bg_color);
                int textLoadMore = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_main_color : R.color.light_text_main_color);

                viewBinding.swipeRefresh.setColorSchemeColors(colorRefresh);
                viewBinding.progressBar1.setProgressColor(colorProgress);
                viewBinding.loadingFooter.progressBarLoadingMore.setProgressColor(colorProgress);
                viewBinding.loadingFooter.layoutRootLoadingMore.setBackgroundColor(bgLoadMore);
                viewBinding.loadingFooter.tvMessage.setTextColor(textLoadMore);
                viewBinding.tvNoResult.setTextColor(textLoadMore);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    ArrayList<T> doOnNextWithListModel(ArrayList<T> listModels, boolean isLoadMore) {
        return listModels;
    }

    private void destroyNativeAds() {
        if (mListModels != null && mListModels.size() > 0) {
            for (Object mObject : mListModels) {
                if (mObject instanceof AbstractModel) {
                    ((AbstractModel) mObject).onDestroyAds();
                }
            }
        }

    }

    ArrayList<T> addNativeAdsToListModel(ArrayList<T> mListTracks, boolean isLoadMore) {
        try {
            if (mListTracks != null && mListTracks.size() > 0 && mContext != null && !mContext.isPremiumMember()) {
                boolean isAdmobAds = mContext.mAdvertisement instanceof AdMobAdvertisement;
                int currentSize = isLoadMore && mListModels != null ? mListModels.size() : 0;
                int size = mListTracks.size();
                if (SHOW_ADS && SHOW_NATIVE_ADS && isAdmobAds && ApplicationUtils.isOnline(mContext) && NATIVE_FREQ > 0) {
                    ArrayList<T> mListNewTracks = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        int currentIndex = currentSize + i;
                        if (currentIndex > 0 && currentIndex % NATIVE_FREQ == 0) {
                            T mObject = createNativeAdsModel();
                            mListNewTracks.add(mObject);
                        }
                        mListNewTracks.add(mListTracks.get(i));
                    }
                    return mListNewTracks;
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mListTracks;

    }

    T createNativeAdsModel() {
        return null;
    }


    public void deleteModel(long id) {
        try {
            if (isDestroy) return;
            if (mAdapter != null && mListModels != null && mListModels.size() > 0 && id != 0) {
                Iterator<T> mIterator = mListModels.iterator();
                while (mIterator.hasNext()) {
                    T mModel1 = mIterator.next();
                    if (mModel1 instanceof AbstractModel) {
                        if (((AbstractModel) mModel1).getId() == id) {
                            mIterator.remove();
                            mContext.runOnUiThread(this::notifyData);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void setUpRecyclerViewAsHorizontalView(RecyclerView mRecyclerView) {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutMngList = new LinearLayoutManager(mContext);
        mLayoutMngList.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutMngList);
    }
}
