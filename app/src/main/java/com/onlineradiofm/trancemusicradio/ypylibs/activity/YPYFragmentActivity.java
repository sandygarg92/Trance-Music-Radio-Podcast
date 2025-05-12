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

package com.onlineradiofm.trancemusicradio.ypylibs.activity;

import static com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils.isSupportRTL;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.YPYAdvertisement;
import com.onlineradiofm.trancemusicradio.ypylibs.fragment.IYPYFragmentConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.fragment.YPYFragment;
import com.onlineradiofm.trancemusicradio.ypylibs.listener.IYPYSearchViewInterface;
import com.onlineradiofm.trancemusicradio.ypylibs.reactive.YPYRXModel;
import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ResolutionUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.view.DividerItemDecoration;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */

public class YPYFragmentActivity extends AppCompatActivity implements IYPYFragmentConstants {

    public static final String TAG = YPYFragmentActivity.class.getSimpleName();

    private static final String KEY_FRAGMENT_TAGS = "key_fragment_tags";

    //fuck support appcombat
    public static final String VECTOR_DRAWABLE_CLAZZ_NAME = "android.graphics.drawable.VectorDrawable";
    public static final int[] CHECKED_STATE_SET = new int[]{android.R.attr.state_checked};
    public static final int[] EMPTY_STATE_SET = new int[0];

    public Dialog mProgressDialog;

    private int screenWidth;
    private int screenHeight;

    public ArrayList<Fragment> mListFragments;

    private boolean isAllowPressMoreToExit;
    private int countToExit;
    private long pivotTime;

    private ConnectionChangeReceiver mNetworkBroadcast;
    private INetworkListener mNetworkListener;

    public ViewGroup mLayoutAds;
    public SearchView searchView;

    public Drawable mBackDrawable;
    public int mContentActionColor;
    public int mIconColor;

    public YPYAdvertisement mAdvertisement;
    public YPYRXModel mYPYRXModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFormat(PixelFormat.RGBA_8888);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        this.createProgressDialog();

        this.mContentActionColor = getResources().getColor(R.color.light_action_bar_text_color);
        this.mIconColor = getResources().getColor(R.color.light_icon_color);

        int idBack = isSupportRTL() ? R.drawable.ic_arrow_next_white_24dp : R.drawable.ic_arrow_back_white_24dp;
        this.mBackDrawable = ContextCompat.getDrawable(this, idBack);
        if (this.mBackDrawable != null) {
            this.mBackDrawable.setColorFilter(mContentActionColor, PorterDuff.Mode.SRC_ATOP);
        }

        int[] mRes = ResolutionUtils.getDeviceResolution(this);
        if (mRes != null && mRes.length == 2) {
            screenWidth = mRes[0];
            screenHeight = mRes[1];
        }
        mYPYRXModel = new YPYRXModel(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    public void setLightStatusBar(boolean isUseLightStatusBar) {
        try {
            if (IOUtils.isMarshmallow()) {
                View decor = getWindow().getDecorView();
                if (isUseLightStatusBar) {
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                else {
                    decor.setSystemUiVisibility(0);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void processRightToLeft() {
        try {
            boolean b = isSupportRTL();
            if (b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }
                onUpdateUIWhenSupportRTL();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUpdateUIWhenSupportRTL() {

    }

    public void onStartCreateAds() {
        mAdvertisement = createAds();
    }

    public YPYAdvertisement createAds() {
        return null;
    }


    public void setUpOverlayBackground(boolean useBg) {
        if (IOUtils.isLollipop()) {
            if (useBg) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    public void setUpImageViewBaseOnColor(View mView, int color, @DrawableRes int idDrawable, boolean isReset) {
        Drawable mDrawable = ContextCompat.getDrawable(this, idDrawable);
        if (mDrawable == null) {
            return;
        }
        if (isReset) {
            mDrawable.clearColorFilter();
        }
        else {
            mDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        if (mView instanceof Button) {
            mView.setBackground(mDrawable);
        }
        else if (mView instanceof ImageView) {
            ((ImageView) mView).setImageDrawable(mDrawable);
        }
    }

    public void setUpBottomBanner(int resId, boolean isAllowShowAds) {
        setUpAdBanner(findViewById(resId), isAllowShowAds);
    }


    public void setUpAdBanner(ViewGroup mLayoutAds, boolean isAllowShowAds) {
        this.mLayoutAds = mLayoutAds;
        if (mAdvertisement != null) {
            mAdvertisement.setUpAdBanner(mLayoutAds, isAllowShowAds);
        }
        else {
            hideAds();
        }
    }


    public void showInterstitialAd(boolean isAllowShowAds, final IYPYCallback mCallback) {
//        if (mAdvertisement != null) {
//            mAdvertisement.showInterstitialAd(isAllowShowAds, mCallback);
//            return;
//        }
        if (mCallback != null) {
            mCallback.onAction();
        }
    }


    public void hideAds() {
        if (mLayoutAds != null) {
            mLayoutAds.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mYPYRXModel != null) {
            mYPYRXModel.onDestroy();
        }
        if (mAdvertisement != null) {
            mAdvertisement.onDestroy();
        }
        if (mNetworkBroadcast != null) {
            unregisterReceiver(mNetworkBroadcast);
            mNetworkBroadcast = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchView != null && !searchView.isIconified()) {
                hiddenKeyBoardForSearchView();
                return true;
            }
            boolean b = backToHome();
            if (b) {
                return true;
            }
            if (!isAllowPressMoreToExit) {
                showQuitDialog();
            }
            else {
                pressMoreToExitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean backToHome() {
        return isFragmentCheckBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return backToHome();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setIsAllowPressMoreToExit(boolean isAllowPressMoreToExit) {
        this.isAllowPressMoreToExit = isAllowPressMoreToExit;
    }

    private void pressMoreToExitApp() {
        if (countToExit >= 1) {
            long delaTime = System.currentTimeMillis() - pivotTime;
            if (delaTime <= 2000) {
                onDestroyData();
                finish();
                return;
            }
            else {
                countToExit = 0;
            }
        }
        pivotTime = System.currentTimeMillis();
        showToast(R.string.info_press_again_to_exit);
        countToExit++;
    }

    public MaterialDialog createFullDialog(int iconId, int mTitleId, int mYesId, int mNoId, String messageId, final IYPYCallback mCallback, final IYPYCallback mNeCallback) {
        MaterialDialog.Builder mBuilder = createBasicDialogBuilder(mTitleId, mYesId, mNoId);
        mBuilder.title(mTitleId);
        if (iconId != -1) {
            mBuilder.iconRes(iconId);
        }
        mBuilder.content(messageId);
        mBuilder.autoDismiss(true);
        mBuilder.onPositive((dialog, which) -> {
            if (mCallback != null) {
                mCallback.onAction();
            }
        });
        mBuilder.onNegative((dialog, which) -> {
            if (mNeCallback != null) {
                mNeCallback.onAction();
            }
        });
        return mBuilder.build();
    }


    public void showFullDialog(int titleId, String message, int idPositive, int idNegative, final IYPYCallback mDBCallback) {
        createFullDialog(-1, titleId, idPositive, idNegative, message, mDBCallback, null).show();
    }

    public void showFullDialog(int titleId, String message, int idPositive, int idNegative, final IYPYCallback mDBCallback, final IYPYCallback mNegative) {
        createFullDialog(-1, titleId, idPositive, idNegative, message, mDBCallback, mNegative).show();
    }

    public void showQuitDialog() {
        int mNoId = R.string.title_no;
        int mTitleId = R.string.title_confirm;
        int mYesId = R.string.title_yes;
        int iconId = com.triggertrap.seekarc.R.drawable.ic_launcher;
        int messageId = R.string.info_close_app;

        createFullDialog(iconId, mTitleId, mYesId, mNoId, getString(messageId), () -> {
            onDestroyData();
            finish();
        }, null).show();

    }

    private void createProgressDialog() {
        this.mProgressDialog = new Dialog(this);
        this.mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mProgressDialog.setContentView(R.layout.item_progress_bar);
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
    }

    public void showProgressDialog() {
        showProgressDialog(R.string.info_loading);
    }

    public void showProgressDialog(int messageId) {
        showProgressDialog(getString(messageId));
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog != null) {
            TextView mTvMessage = mProgressDialog.findViewById(R.id.tv_message);
            mTvMessage.setText(message);
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public void dismissProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showToastWithLongTime(int resId) {
        showToastWithLongTime(getString(resId));
    }

    public void showToastWithLongTime(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public void onDestroyData() {

    }

    public void createArrayFragment() {
        mListFragments = new ArrayList<>();
    }

    public void addFragment(Fragment mFragment) {
        if (mFragment != null && mListFragments != null) {
            mListFragments.add(mFragment);
        }
    }

    public boolean backStack() {
        if (mListFragments != null && mListFragments.size() > 0) {
            int count = mListFragments.size();
            Fragment mFragment = mListFragments.remove(count - 1);
            if (mFragment != null) {
                if (mFragment instanceof YPYFragment) {
                    ((YPYFragment<?>) mFragment).backToHome(this);
                    return true;
                }
            }
        }
        return false;
    }

    public void goToFragment(String tag, int idContainer, String fragmentName, Bundle mBundle) {
        String currentTag = getCurrentFragmentTag();
        if (TextUtils.isEmpty(currentTag)) {
            goToFragment(tag, idContainer, fragmentName, 0, mBundle);
        }
        else {
            goToFragment(tag, idContainer, fragmentName, currentTag, mBundle);
        }
    }


    public void goToFragment(String tag, int idContainer, String fragmentName, String parentTag, Bundle mBundle) {
        goToFragment(tag, idContainer, fragmentName, 0, parentTag, mBundle);
    }

    public void goToFragment(String tag, int idContainer, String fragmentName, int parentId, Bundle mBundle) {
        goToFragment(tag, idContainer, fragmentName, parentId, null, mBundle);
    }

    public void goToFragment(String tag, int idContainer, String fragmentName, int parentId, String parentTag, Bundle mBundle) {
        try {
            if (!TextUtils.isEmpty(tag) && getSupportFragmentManager().findFragmentByTag(tag) != null) {
                return;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (mBundle != null) {
                if (parentId != 0) {
                    mBundle.putInt(KEY_ID_FRAGMENT, parentId);
                }
                if (!TextUtils.isEmpty(parentTag)) {
                    mBundle.putString(KEY_NAME_FRAGMENT, parentTag);
                }
            }

            Fragment mFragment = getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), fragmentName);
            mFragment.setArguments(mBundle);
            addFragment(mFragment);

            transaction.add(idContainer, mFragment, tag);
            if (parentId != 0) {
                Fragment mFragmentParent = getSupportFragmentManager().findFragmentById(parentId);
                if (mFragmentParent != null) {
                    transaction.hide(mFragmentParent);
                }
            }
            if (!TextUtils.isEmpty(parentTag)) {
                Fragment mFragmentParent = getSupportFragmentManager().findFragmentByTag(parentTag);
                if (mFragmentParent != null) {
                    transaction.hide(mFragmentParent);
                }
            }
            transaction.commitAllowingStateLoss();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onRestoreFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null && mListFragments != null) {
            ArrayList<String> mListTag = savedInstanceState.getStringArrayList(KEY_FRAGMENT_TAGS);
            if (mListTag != null && mListTag.size() > 0) {
                for (String mTag : mListTag) {
                    mListFragments.add(getSupportFragmentManager().findFragmentByTag(mTag));
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListFragments != null && mListFragments.size() > 0) {
            ArrayList<String> mListTagFragment = new ArrayList<>();
            for (Fragment mFragment : mListFragments) {
                mListTagFragment.add(mFragment.getTag());
            }
            outState.putStringArrayList(KEY_FRAGMENT_TAGS, mListTagFragment);
        }
    }


    public void registerNetworkBroadcastReceiver(INetworkListener networkListener) {
        if (mNetworkBroadcast != null) {
            return;
        }
        mNetworkBroadcast = new ConnectionChangeReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkBroadcast, mIntentFilter);
        mNetworkListener = networkListener;
    }

    private class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mNetworkListener != null) {
                mNetworkListener.onNetworkState(ApplicationUtils.isOnline(YPYFragmentActivity.this));
            }
        }
    }

    public interface INetworkListener {
        void onNetworkState(boolean isNetworkOn);
    }

    public void setActionBarTitle(String title) {
        ActionBar mAb = getSupportActionBar();
        if (mAb != null) {
            mAb.setTitle(title);
        }
    }

    public void setActionBarTitle(int titleId) {
        setActionBarTitle(getResources().getString(titleId));
    }

    public void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            if (mBackDrawable != null) {
                getSupportActionBar().setHomeAsUpIndicator(mBackDrawable);
            }
        }
    }


    public void setUpCustomizeActionBar(int actionBarBgColor, int actionBarContentColor, boolean isNeedShowBack) {
        Toolbar mToolbar = findViewById(R.id.my_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (actionBarBgColor != 0) {
                setColorForActionBar(actionBarBgColor);
            }
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_more_vert_white_24dp);
            int colorIcon = mContentActionColor;
            if (actionBarContentColor != 0) {
                colorIcon = actionBarContentColor;
            }
            mToolbar.setTitleTextColor(colorIcon);
            if (drawable != null) {
                drawable.setColorFilter(colorIcon, PorterDuff.Mode.SRC_ATOP);
            }
            mToolbar.setOverflowIcon(drawable);


            if (mBackDrawable != null) {
                this.mBackDrawable.setColorFilter(colorIcon, PorterDuff.Mode.SRC_ATOP);
            }

            if (isNeedShowBack) {
                showBackButton();
            }
        }
    }


    public void removeElevationActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setElevation(0);
        }
    }

    public void setColorForActionBar(int color) {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setBackgroundDrawable(new ColorDrawable(color));
        }
    }

    public void updateSearchUI() {
        if (searchView == null) return;
        ImageView searchBtn = searchView.findViewById(androidx.appcompat.R.id.search_button);
        setUpImageViewBaseOnColor(searchBtn, mContentActionColor, R.drawable.ic_search_white_24dp, false);

        ImageView closeBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        setUpImageViewBaseOnColor(closeBtn, mContentActionColor, R.drawable.ic_close_white_24dp, false);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(mContentActionColor);
        searchEditText.setHintTextColor(mContentActionColor);

        try {
            ImageView searchSubmit = searchView.findViewById(androidx.appcompat.R.id.search_go_btn);
            if (searchSubmit != null) {
                searchSubmit.setColorFilter(mContentActionColor, PorterDuff.Mode.SRC_ATOP);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSetupForSearchView(Menu menu, int idSearch, final IYPYSearchViewInterface mListener) {
        searchView = (SearchView) menu.findItem(idSearch).getActionView();
        searchView.clearFocus();
        updateSearchUI();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) {
                hiddenKeyBoardForSearchView();
                if (mListener != null) {
                    mListener.onProcessSearchData(keyword);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String keyword) {
                if (mListener != null) {
                    mListener.onStartSuggestion(keyword);
                }
                return true;
            }
        });
        searchView.setOnSearchClickListener(v -> {
            searchView.onActionViewExpanded();
            if (mListener != null) {
                mListener.onClickSearchView();
            }
        });
        searchView.setOnCloseListener(() -> {
            if (mListener != null) {
                mListener.onCloseSearchView();
            }
            return true;
        });
        searchView.setQueryHint(getString(R.string.title_search));
        searchView.setSubmitButtonEnabled(true);
    }

    public void hiddenKeyBoardForSearchView() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
            ApplicationUtils.hiddenVirtualKeyboard(this, searchView);
        }
    }


    @Override
    public void onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            hiddenKeyBoardForSearchView();
        }
        else {
            super.onBackPressed();
        }

    }

    public String getCurrentFragmentTag() {
        Fragment mFragment = getCurrentFragment();
        if (mFragment != null) {
            return mFragment.getTag();
        }
        return null;
    }

    public Fragment getCurrentFragment() {
        if (mListFragments != null && mListFragments.size() > 0) {
            return mListFragments.get(mListFragments.size() - 1);
        }
        return null;
    }


    public void setUpRecyclerViewAsListView(RecyclerView mRecyclerView, Drawable mDivider) {
        if (mDivider != null) {
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, mDivider));
        }
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutMngList = new LinearLayoutManager(this);
        mLayoutMngList.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutMngList);
    }

    public void setUpRecyclerViewAsListView(RecyclerView mRecyclerView) {
        setUpRecyclerViewAsListView(mRecyclerView, null);
    }

    public void setUpRecyclerViewAsGridView(RecyclerView mRecyclerView, int numberColumn, Drawable mDividerVer, Drawable mDividerHori) {
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberColumn);
        mRecyclerView.setLayoutManager(layoutManager);
        if (mDividerVer != null) {
            DividerItemDecoration mItemDecorVerti = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, mDividerVer);
            mRecyclerView.addItemDecoration(mItemDecorVerti);
        }
        if (mDividerHori != null) {
            DividerItemDecoration mHoriItem = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST, mDividerHori);
            mRecyclerView.addItemDecoration(mHoriItem);
        }

    }

    public void setUpRecyclerViewAsStaggered(RecyclerView mRecyclerView, int numberColumn, Drawable mDividerVer, Drawable mDividerHori) {
        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        try {
            int size = mRecyclerView.getItemDecorationCount();
            if (size > 0) {
                for (int i = size - 1; i >= 0; i--) {
                    mRecyclerView.removeItemDecorationAt(i);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (mDividerVer != null) {
            DividerItemDecoration mItemDecorVerti = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, mDividerVer);
            mRecyclerView.addItemDecoration(mItemDecorVerti);
        }
        if (mDividerHori != null) {
            DividerItemDecoration mHoriItem = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST, mDividerHori);
            mRecyclerView.addItemDecoration(mHoriItem);
        }

    }

    public boolean isFragmentCheckBack() {
        if (mListFragments != null && mListFragments.size() > 0) {
            for (Fragment mFragment : mListFragments) {
                if (mFragment instanceof YPYFragment) {
                    boolean isBack = ((YPYFragment) mFragment).isCheckBack();
                    if (isBack) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Drawable getSupportDrawable(int resId) {
        try {
            if (resId != 0) {
                final Drawable d = AppCompatResources.getDrawable(this, resId);
                if (d != null) {
                    if (Build.VERSION.SDK_INT == 21
                            && VECTOR_DRAWABLE_CLAZZ_NAME.equals(d.getClass().getName())) {
                        fixVectorDrawableTinting(d);
                    }
                    return d;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private void fixVectorDrawableTinting(final Drawable drawable) {
        try {
            final int[] originalState = drawable.getState();
            if (originalState.length == 0) {
                // The drawable doesn't have a state, so set it to be checked
                drawable.setState(CHECKED_STATE_SET);
            }
            else {
                // Else the drawable does have a state, so clear it
                drawable.setState(EMPTY_STATE_SET);
            }
            // Now set the original state
            drawable.setState(originalState);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public MaterialDialog.Builder createBasicDialogBuilder(int titleId, int resPositive, int resNegative) {
        boolean isDark = XRadioSettingManager.isDarkMode(this);
        MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(this);
        mBuilder.title(titleId);
        if (isDark) {
            mBuilder.backgroundColorRes(R.color.dark_card_background);
            mBuilder.titleColorRes(R.color.dark_color_accent);
            mBuilder.positiveColorRes(R.color.dark_color_accent);
            mBuilder.contentColorRes(R.color.dark_text_main_color);
            mBuilder.negativeColor(getResources().getColor(R.color.dark_text_second_color));
        }
        else {
            mBuilder.backgroundColor(getResources().getColor(R.color.dialog_bg_color));
            mBuilder.titleColor(getResources().getColor(R.color.dialog_color_text));
            mBuilder.contentColor(getResources().getColor(R.color.dialog_color_text));
            mBuilder.positiveColor(getResources().getColor(R.color.dialog_color_accent));
            mBuilder.negativeColor(getResources().getColor(R.color.dialog_color_secondary_text));
        }
        if (resPositive != 0) {
            mBuilder.positiveText(resPositive);
        }
        if (resNegative != 0) {
            mBuilder.negativeText(resNegative);
        }

        mBuilder.autoDismiss(true);
        return mBuilder;
    }

}
