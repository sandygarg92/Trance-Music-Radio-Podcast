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

package com.onlineradiofm.trancemusicradio;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import com.behavior.model.FixAppBarLayoutBehavior;
import com.behavior.model.YPYBottomSheetBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.onlineradiofm.trancemusicradio.dataMng.MyDownloadManager;
import com.onlineradiofm.trancemusicradio.dataMng.MyRecordManager;
import com.onlineradiofm.trancemusicradio.databinding.ActivityMainBinding;
import com.onlineradiofm.trancemusicradio.db.DatabaseManager;
import com.onlineradiofm.trancemusicradio.fragment.FragmentAddRadio;
import com.onlineradiofm.trancemusicradio.fragment.FragmentCloudFavorite;
import com.onlineradiofm.trancemusicradio.fragment.FragmentDetailList;
import com.onlineradiofm.trancemusicradio.fragment.FragmentDetailListPod;
import com.onlineradiofm.trancemusicradio.fragment.FragmentDetailPodCast;
import com.onlineradiofm.trancemusicradio.fragment.FragmentDownloads;
import com.onlineradiofm.trancemusicradio.fragment.FragmentDragDrop;
import com.onlineradiofm.trancemusicradio.fragment.FragmentMyRadios;
import com.onlineradiofm.trancemusicradio.fragment.FragmentProfile;
import com.onlineradiofm.trancemusicradio.fragment.FragmentTabFavorite;
import com.onlineradiofm.trancemusicradio.fragment.FragmentTabLibrary;
import com.onlineradiofm.trancemusicradio.fragment.FragmentTabLive;
import com.onlineradiofm.trancemusicradio.fragment.FragmentTabPodcast;
import com.onlineradiofm.trancemusicradio.fragment.FragmentTabSearch;
import com.onlineradiofm.trancemusicradio.fragment.FragmentTopRadios;
import com.onlineradiofm.trancemusicradio.itunes.model.PodCastModel;
import com.onlineradiofm.trancemusicradio.model.CountryModel;
import com.onlineradiofm.trancemusicradio.model.GenreModel;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.IYPYRewardAdsListener;
import com.onlineradiofm.trancemusicradio.ypylibs.fragment.YPYFragment;
import com.onlineradiofm.trancemusicradio.ypylibs.fragment.YPYFragmentAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.music.mediaplayer.YPYMediaPlayer;
import com.onlineradiofm.trancemusicradio.ypylibs.music.model.YPYMusicModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.StringUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.util.ArrayList;

public class MainActivity extends RadioFragmentActivity<ActivityMainBinding> implements View.OnClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener, IYPYRewardAdsListener {

    private static final String KEY_TOP_INDEX = "view_pager_index";

    private Drawable logoDrawable;

    private int mStartHeight;

    private ArrayList<Fragment> mListHomeFragments;
    private FragmentTabLive mFragmentTabLive;
    private FragmentTabFavorite mFragmentTabFavorite;
    private FragmentTabLibrary mFragmentLibraries;
    private FragmentTabPodcast mFragmentTabPodcast;
    private FragmentTabSearch mFragmentTabSearch;

    private YPYBottomSheetBehavior<RelativeLayout> mBottomSheetBehavior;

    private FragmentDragDrop mFragmentDragDrop;
    private int countInterstitial;

    public boolean isAllCheckNetWorkOff;
    private int mCurrentIndex = 0;

    private Menu mMenu;
    private boolean isNotSetUp;
    private boolean isFirstTime;
    private int resDefaultPlayer = R.drawable.ic_small_light_play_default;

    private RadioModel downloadModel;
    public MyDownloadManager downloadManager;
    public MyRecordManager recordManager;

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onDoWhenDone() {
        if (mSavedInstance != null) {
            mCurrentIndex = mSavedInstance.getInt(KEY_TOP_INDEX, 0);
        }
        logoDrawable = ContextCompat.getDrawable(this, R.drawable.ic_action_bar_logo);
        viewBinding.viewPager.setPagingEnabled(false);
        super.onDoWhenDone();

        ((CoordinatorLayout.LayoutParams) viewBinding.appBar.getLayoutParams()).setBehavior(new FixAppBarLayoutBehavior());
        setIsAllowPressMoreToExit(true);

        mFragmentDragDrop = (FragmentDragDrop) getSupportFragmentManager().findFragmentById(R.id.fragment_drag_drop);
        findViewById(R.id.img_touch).setOnTouchListener((v, event) -> true);

        viewBinding.layoutTotalDragDrop.btnSmallNext.setOnClickListener(this);
        viewBinding.layoutTotalDragDrop.btnSmallPrev.setOnClickListener(this);
        viewBinding.layoutTotalDragDrop.btnSmallPlay.setOnClickListener(this);
        viewBinding.bottomNavMenu.setOnNavigationItemSelectedListener(this);

        setUpTab();
        showAppRate();

        this.downloadManager = new MyDownloadManager(this, mFragmentDragDrop);
        this.recordManager = new MyRecordManager(this, mFragmentLibraries);

        registerApplicationBroadcastReceiver(MainActivity.this);
        setUpGoogleCast();

        //TODO WHEN SAVED INSTANCE !=NULL
        if (mListFragments != null && mListFragments.size() > 0) {
            showHideLayoutContainer(true);
            YPYFragment<?> mFragment = (YPYFragment<?>) mListFragments.get(mListFragments.size() - 1);
            if (!TextUtils.isEmpty(mFragment.getScreenName())) {
                setActionBarTitle(mFragment.getScreenName());
            }
        }
    }

    //This one to fix stupid problem about pivot height
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        try {
            if (!isNotSetUp) {
                isNotSetUp = true;
                setUpBottomPlayer();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpBottomPlayer() {
        setUpDragDropLayout();
        boolean isHaving = isHavingListStream();
        showLayoutListenMusic(isHaving);
        if (isHaving) {
            boolean isPlay = YPYStreamManager.getInstance().isPlaying();
            boolean isLoad = YPYStreamManager.getInstance().isLoading();
            showLoading(isLoad);
            updateStatePlayer(isPlay);
            updateInfoOfPlayingTrack();
            YPYMediaPlayer.StreamInfo mStrInfo = YPYStreamManager.getInstance().getStreamInfo();
            processUpdateImage(mStrInfo != null ? mStrInfo.imgUrl : null);
        }
    }

    private void setUpDragDropLayout() {
        findViewById(R.id.img_fake_touch).setOnTouchListener((v, event) -> true);
        mStartHeight = getResources().getDimensionPixelOffset(R.dimen.size_img_big);
        viewBinding.layoutTotalDragDrop.layoutSmallControl.setOnClickListener(view -> expandLayoutListenMusic());

        mBottomSheetBehavior = (YPYBottomSheetBehavior<RelativeLayout>) BottomSheetBehavior.from(viewBinding.layoutTotalDragDrop.getRoot());
        mBottomSheetBehavior.setPeekHeight(mStartHeight);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            boolean isHidden;
            float mSlideOffset;

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                try {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        showHeaderMusicPlayer(true);
                        enableDragForBottomSheet(ALLOW_DRAG_DROP_WHEN_EXPAND);
                        updatePlayingBackground(true);
                    }
                    else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        isHidden = false;
                        enableDragForBottomSheet(true);
                        showHeaderMusicPlayer(false);
                        updatePlayingBackground(false);

                        boolean isCastConnect = mYPYCastManager != null && mYPYCastManager.isCastConnected();
                        if (!isHavingListStream() && !isCastConnect) {
                            showLayoutListenMusic(false);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                try {
                    if (mSlideOffset > 0 && slideOffset > mSlideOffset && !isHidden) {
                        showAppBar(false);
                        isHidden = true;
                    }
                    mSlideOffset = slideOffset;
                    viewBinding.layoutTotalDragDrop.layoutSmallControl.setVisibility(View.VISIBLE);
                    viewBinding.layoutTotalDragDrop.dragDropContainer.setVisibility(View.VISIBLE);
                    viewBinding.layoutTotalDragDrop.layoutSmallControl.setAlpha(1f - slideOffset);
                    viewBinding.layoutTotalDragDrop.dragDropContainer.setAlpha(slideOffset);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        showLayoutListenMusic(false);
    }


    private void showHeaderMusicPlayer(boolean b) {
        viewBinding.layoutTotalDragDrop.layoutSmallControl.setVisibility(!b ? View.VISIBLE : View.GONE);
        viewBinding.layoutTotalDragDrop.dragDropContainer.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    public void showAppBar(boolean b) {
        viewBinding.appBar.setExpanded(b);
    }

    private void showLayoutListenMusic(boolean b) {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && !b) {
            return;
        }
        viewBinding.layoutTotalDragDrop.getRoot().setVisibility(b ? View.VISIBLE : View.GONE);
        viewBinding.viewPager.setPadding(0, 0, 0, b ? mStartHeight : 0);
        viewBinding.container.setPadding(0, 0, 0, b ? mStartHeight : 0);
        if (!b) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void expandLayoutListenMusic() {
        if (mBottomSheetBehavior == null) return;
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateVolume();
            }
            enableDragForBottomSheet(ALLOW_DRAG_DROP_WHEN_EXPAND);
            updatePlayingBackground(true);
        }
    }

    public boolean collapseListenMusic() {
        if (mBottomSheetBehavior == null) return false;
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            enableDragForBottomSheet(ALLOW_DRAG_DROP_WHEN_EXPAND);
            updatePlayingBackground(false);
            return true;
        }
        return false;
    }

    private void updatePlayingBackground(boolean isShow) {
        boolean isDark = XRadioSettingManager.isDarkMode(this);
        boolean isHasChildFR = mListFragments != null && mListFragments.size() > 0;
        viewBinding.bottomNavMenu.setVisibility(!isHasChildFR && !isShow ? View.VISIBLE : View.GONE);
        viewBinding.appBar.setVisibility(isShow ? View.GONE : View.VISIBLE);
        showAppBar(!isShow);
        if (isShow) {
            int colorPlayAccent = ContextCompat.getColor(this, isDark ? R.color.dark_play_accent_color : R.color.light_play_accent_color);
            viewBinding.layoutBg.setBackgroundColor(colorPlayAccent);
        }
        else {
            int normalBg = ContextCompat.getColor(this, isDark ? R.color.dark_color_background : R.color.light_color_background);
            viewBinding.layoutBg.setBackgroundColor(normalBg);
        }
    }

    public void enableDragForBottomSheet(boolean b) {
        mBottomSheetBehavior.setAllowUserDragging(b);
    }

    public void updateDarkModeForFragment() {
        try {
            boolean isDark = XRadioSettingManager.isDarkMode(this);
            if (mListHomeFragments != null && mListHomeFragments.size() > 0) {
                for (Fragment mFragment : mListHomeFragments) {
                    ((YPYFragment<?>) mFragment).updateDarkMode(isDark);
                    if (viewBinding.viewPager.getCurrentItem() == mListHomeFragments.indexOf(mFragment)) {
                        ((YPYFragment<?>) mFragment).startLoadData();
                    }
                }
            }
            if (mListFragments != null && mListFragments.size() > 0) {
                for (Fragment mFragment : mListFragments) {
                    ((YPYFragment<?>) mFragment).updateDarkMode(isDark);
                    ((YPYFragment<?>) mFragment).startLoadData();
                }
                Fragment mCurrentFragment = getCurrentFragment();
                if (mCurrentFragment instanceof YPYFragment) {
                    setActionBarTitle(((YPYFragment<?>) mCurrentFragment).getScreenName());
                }
            }
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateDarkMode(isDark);
            }
            if (isHavingListStream()) {
                startMusicService(ACTION_UPDATE_NOTIFICATION);
                YPYMediaPlayer.StreamInfo mStrInfo = YPYStreamManager.getInstance().getStreamInfo();
                processUpdateImage(mStrInfo != null ? mStrInfo.imgUrl : null);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateThemeColor(boolean isDark) {
        super.updateThemeColor(isDark);

        //TODO DARK MODE
        int bgColor = ContextCompat.getColor(this, isDark ? R.color.dark_color_background : R.color.light_color_background);
        viewBinding.layoutBg.setBackgroundColor(bgColor);

        int bgPagerColor = ContextCompat.getColor(this, isDark ? R.color.dark_pager_color_background : R.color.light_pager_color_background);
        viewBinding.viewPager.setBackgroundColor(bgPagerColor);
        viewBinding.container.setBackgroundColor(bgPagerColor);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked}, // enabled
                new int[]{-android.R.attr.state_checked} // disabled
        };
        int[] colors = new int[]{
                ContextCompat.getColor(this, isDark ? R.color.dark_bottom_nav_text_focus_color : R.color.light_bottom_nav_text_focus_color),
                ContextCompat.getColor(this, isDark ? R.color.dark_bottom_nav_text_normal_color : R.color.light_bottom_nav_text_normal_color)
        };
        ColorStateList btItemTintColor = new ColorStateList(states, colors);
        viewBinding.bottomNavMenu.setItemTextColor(btItemTintColor);
        viewBinding.bottomNavMenu.setItemIconTintList(btItemTintColor);
        viewBinding.bottomNavMenu.setItemRippleColor(ContextCompat.getColorStateList(this,
                isDark ? R.color.dark_bottom_nav_ripple_color : R.color.light_bottom_nav_ripple_color));
        viewBinding.bottomNavMenu.setBackgroundColor(ContextCompat.getColor(this, isDark ? R.color.dark_bottom_nav_background_color
                : R.color.light_bottom_nav_background_color));
        viewBinding.bottomNavMenu.setItemRippleColor(ContextCompat.getColorStateList(this, isDark ? R.color.dark_bottom_nav_ripple_color
                : R.color.light_bottom_nav_ripple_color));

        int bgPlayer = ContextCompat.getColor(this, isDark ? R.color.dark_bottom_player_bg_color : R.color.light_bottom_player_bg_color);
        viewBinding.layoutTotalDragDrop.layoutSmallControl.setBackgroundColor(bgPlayer);

        ColorStateList btTintList = ContextCompat.getColorStateList(this, isDark ? R.color.dark_play_color_text : R.color.light_play_color_text);
        ImageViewCompat.setImageTintList(viewBinding.layoutTotalDragDrop.btnSmallPlay, btTintList);
        ImageViewCompat.setImageTintList(viewBinding.layoutTotalDragDrop.btnSmallNext, btTintList);
        ImageViewCompat.setImageTintList(viewBinding.layoutTotalDragDrop.btnSmallPrev, btTintList);

        int processColor = ContextCompat.getColor(this, isDark ? R.color.dark_color_accent : R.color.light_color_accent);
        viewBinding.layoutTotalDragDrop.imgStatusLoading.setProgressColor(processColor);

        int playerColor = ContextCompat.getColor(this, isDark ? R.color.dark_play_color_text : R.color.light_play_color_text);
        int playerSecondColor = ContextCompat.getColor(this, isDark ? R.color.dark_play_color_secondary_text : R.color.light_play_color_secondary_text);
        viewBinding.layoutTotalDragDrop.tvRadioName.setTextColor(playerColor);
        viewBinding.layoutTotalDragDrop.tvInfo.setTextColor(playerSecondColor);

        resDefaultPlayer = isDark ? R.drawable.ic_small_dark_play_default : R.drawable.ic_small_light_play_default;
        setUpActionBar(isDark);
    }

    private void setUpActionBar(boolean isDark) {
        int actionBarColor = ContextCompat.getColor(this, !isDark ? R.color.light_action_bar_background : R.color.dark_action_bar_background);
        int actionBarTextColor = ContextCompat.getColor(this, !isDark ? R.color.light_action_bar_text_color : R.color.dark_action_bar_text_color);

        setUpCustomizeActionBar(actionBarColor, actionBarTextColor, false);
        viewBinding.myToolbar.toolBarTitle.setTextColor(actionBarTextColor);

        setActionBarTitle(R.string.title_home_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            if (!isFirstTime) {
                isFirstTime = true;
                lockMenuAndShowBackButton(false);
            }
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        super.setActionBarTitle("");
        viewBinding.myToolbar.toolBarTitle.setText(title);
    }

    private void setUpTab() {
        ArrayList<Fragment> mListHomeFragments = buildListFragments();
        ((YPYFragment<?>) mListHomeFragments.get(mCurrentIndex)).setFirstInTab(true);
        YPYFragmentAdapter mTabAdapters = new YPYFragmentAdapter(getSupportFragmentManager(), mListHomeFragments, viewBinding.viewPager);
        viewBinding.viewPager.setAdapter(mTabAdapters);
        viewBinding.viewPager.setOffscreenPageLimit(mListHomeFragments.size());
        this.mListHomeFragments = mListHomeFragments;
        this.viewBinding.viewPager.setCurrentItem(mCurrentIndex);
    }

    private ArrayList<Fragment> buildListFragments() {
        ArrayList<Fragment> mListHomeFragments = new ArrayList<>();

        Bundle mBundle1 = new Bundle();
        mBundle1.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_LIVE);
        mBundle1.putBoolean(KEY_IS_TAB, true);
        mBundle1.putBoolean(KEY_ALLOW_READ_CACHE, true);
        mBundle1.putBoolean(KEY_ALLOW_MORE, true);
        mBundle1.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, true);
        mFragmentTabLive = (FragmentTabLive) getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), FragmentTabLive.class.getName());
        mFragmentTabLive.setArguments(mBundle1);
        mFragmentTabLive.setFirstInTab(true);
        mListHomeFragments.add(mFragmentTabLive);

        Bundle mBundle2 = new Bundle();
        mBundle2.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_PODCAST);
        mBundle2.putBoolean(KEY_IS_TAB, true);
        mBundle2.putBoolean(KEY_ALLOW_READ_CACHE, true);
        mBundle2.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, true);
        mFragmentTabPodcast = (FragmentTabPodcast) getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), FragmentTabPodcast.class.getName());
        mFragmentTabPodcast.setArguments(mBundle2);
        mListHomeFragments.add(mFragmentTabPodcast);

        Bundle mBundle3 = new Bundle();
        mBundle3.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_FAVORITE);
        mBundle3.putBoolean(KEY_IS_TAB, true);
        mBundle3.putBoolean(KEY_OFFLINE_DATA, true);
        mBundle3.putBoolean(KEY_ALLOW_REFRESH, false);
        mBundle3.putBoolean(KEY_ALLOW_SHOW_HEADER, true);
        mFragmentTabFavorite = (FragmentTabFavorite) getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), FragmentTabFavorite.class.getName());
        mFragmentTabFavorite.setArguments(mBundle3);
        mListHomeFragments.add(mFragmentTabFavorite);

        Bundle mBundle4 = new Bundle();
        mBundle4.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_LIVE);
        mBundle4.putBoolean(KEY_IS_TAB, true);
        mBundle4.putBoolean(KEY_ALLOW_READ_CACHE, true);
        mBundle4.putBoolean(KEY_ALLOW_MORE, true);
        mBundle4.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, true);
        mFragmentTabSearch = (FragmentTabSearch) getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), FragmentTabSearch.class.getName());
        mFragmentTabSearch .setArguments(mBundle4);
        mFragmentTabSearch .setFirstInTab(true);
        mListHomeFragments.add(mFragmentTabSearch);

//        Bundle mBundle5 = new Bundle();
//        mBundle5.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_LIBRARIES);
//        mBundle5.putBoolean(KEY_IS_TAB, true);
//        mBundle5.putBoolean(KEY_ALLOW_SHOW_HEADER, true);
//        mFragmentTagsList = (FragmentTagsList) getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), FragmentTagsList.class.getName());
//        mFragmentTagsList.setArguments(mBundle5);
//        mListHomeFragments.add(mFragmentTagsList);

//        Bundle mBundle5 = new Bundle();
//        mBundle5.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_LIVE);
//        mBundle5.putBoolean(KEY_IS_TAB, true);
//        mBundle5.putBoolean(KEY_ALLOW_READ_CACHE, true);
//        mBundle5.putBoolean(KEY_ALLOW_MORE, true);
//        mBundle5.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, true);
//        mFragmentTagsList = (FragmentTagsList) getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), FragmentTagsList.class.getName());
//        mFragmentTagsList.setArguments(mBundle5);
//        mFragmentTagsList.setFirstInTab(true);
//        mListHomeFragments.add(mFragmentTagsList);

        Bundle mBundle5 = new Bundle();
        mBundle5.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_LIBRARIES);
        mBundle5.putBoolean(KEY_IS_TAB, true);
        mBundle5.putBoolean(KEY_ALLOW_SHOW_HEADER, true);
        mFragmentLibraries = (FragmentTabLibrary) getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), FragmentTabLibrary.class.getName());
        mFragmentLibraries.setArguments(mBundle5);
        mListHomeFragments.add(mFragmentLibraries);


        return mListHomeFragments;
    }

    @Override
    public void onDestroyData() {
        if (isHavingListStream()) {
            startMusicService(ACTION_STOP);
        }
        else {
            YPYStreamManager.getInstance().onDestroy();
        }
        super.onDestroyData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            this.mMenu = menu;
            boolean isDark = XRadioSettingManager.isDarkMode(this);
            mMenu.findItem(R.id.action_themes).setIcon(isDark ? R.drawable.ic_day_mode_24dp : R.drawable.ic_dark_mode_24dp);
            mMenu.findItem(R.id.action_themes).setTitle(isDark ? R.string.title_light_mode : R.string.title_dark_mode);
            mMenu.findItem(R.id.action_premium).setIcon(isDark ? R.drawable.ic_premium_dark : R.drawable.ic_premium_light);
            if (mYPYCastManager != null) {
                mYPYCastManager.setUpMediaRoutMenuItem(mMenu, R.id.action_route_menu_item);

            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    public void goToSearch(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            showAppBar(true);
            hiddenKeyBoardForSearchView();
            FragmentDetailList mFragmentSearch = (FragmentDetailList) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_DETAIL_SEARCH);
            if (mFragmentSearch != null) {
                mFragmentSearch.startSearch(keyword);
            }
            else {
                backStack();
                setActionBarTitle(R.string.title_search);
                showHideLayoutContainer(true);
                Bundle mBundle = new Bundle();
                mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_SEARCH);
                mBundle.putBoolean(KEY_ALLOW_MORE, true);
                mBundle.putString(KEY_SEARCH, keyword);
                mBundle.putBoolean(KEY_ALLOW_READ_CACHE, false);
                mBundle.putBoolean(KEY_ALLOW_REFRESH, false);
                mBundle.putString(KEY_NAME_SCREEN, getString(R.string.title_search));
                goToFragment(TAG_FRAGMENT_DETAIL_SEARCH, R.id.container, FragmentDetailList.class.getName(), 0, mBundle);
            }
        }
    }


    public ResultModel<RadioModel> mResultModelone;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mListFragments != null && mListFragments.size() > 0) {
                return backToHome();
            }
            goToProfileTab();
            return true;
        }
        else if (item.getItemId() == R.id.action_themes) {
            startSwitchTheme();
            return true;
        }
        else if (item.getItemId() == R.id.action_premium) {
            goToPremium();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void startSwitchTheme() {
        boolean isDarkMode = XRadioSettingManager.isDarkMode(this);
        long newThemeId = isDarkMode ? LIGHT_MODE_THEME_ID : DARK_MODE_THEME_ID;
        XRadioSettingManager.setThemId(this, newThemeId);

        updateThemeColor(!isDarkMode);
        updateDarkModeForFragment();

        if (mMenu != null) {
            mMenu.findItem(R.id.action_themes).setIcon(isDarkMode ? R.drawable.ic_dark_mode_24dp : R.drawable.ic_day_mode_24dp);
            mMenu.findItem(R.id.action_themes).setTitle(isDarkMode ? R.string.title_dark_mode : R.string.title_light_mode);
            mMenu.findItem(R.id.action_premium).setIcon(isDarkMode ? R.drawable.ic_premium_dark : R.drawable.ic_premium_light);
        }
        String msg = String.format(getString(R.string.format_update_success), getString(!isDarkMode ? R.string.title_dark_mode : R.string.title_light_mode));
        showToast(msg);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                if (mFragmentDragDrop != null) {
                    mFragmentDragDrop.changeVolume(1);
                }
                return true;
            }

        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                if (mFragmentDragDrop != null) {
                    mFragmentDragDrop.changeVolume(-1);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean backToHome() {
        boolean isFromParent = super.backToHome();
        boolean isHasProfile = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_PROFILE) != null;
        if (collapseListenMusic() || (!isHasProfile && isFromParent)) {
            return true;
        }
        boolean isAddTag = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_ADD_RADIO) != null;
        if (backStack()) {
            if (mListFragments != null && mListFragments.size() > 0) {
                if (mMenu != null && isAddTag) {
                    mMenu.findItem(R.id.action_premium).setVisible(true);
                }
                return true;
            }
            if (mMenu != null) {
                mMenu.findItem(R.id.action_premium).setVisible(true);
            }
            showHideLayoutContainer(false);
            return true;
        }
        if (YPYStreamManager.getInstance().isRecordingFile()) {
            showToast(R.string.info_recording_file);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFragmentCheckBack() {
        try {
            if (mListHomeFragments != null && mListHomeFragments.size() > 0) {
                for (Fragment mFragment : mListHomeFragments) {
                    if (mFragment instanceof YPYFragment) {
                        boolean isBack = ((YPYFragment<?>) mFragment).isCheckBack();
                        if (isBack) {
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return super.isFragmentCheckBack();
    }

    public void showHideLayoutContainer(boolean isShow) {
        viewBinding.container.setVisibility(isShow ? View.VISIBLE : View.GONE);
        viewBinding.bottomNavMenu.setVisibility(isShow ? View.GONE : View.VISIBLE);
        viewBinding.viewPager.setVisibility(isShow ? View.GONE : View.VISIBLE);
        lockMenuAndShowBackButton(isShow);
        if (isShow) {
            viewBinding.appBar.setExpanded(true);
        }
        else {
            setActionBarTitle(R.string.title_home_screen);
        }

    }

    public void lockMenuAndShowBackButton(boolean isLock) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(isLock ? mBackDrawable : logoDrawable);
        }
    }

    @Override
    public void notifyFavorite(long id, boolean isFav) {
        super.notifyFavorite(id, isFav);
        if (mFragmentTabLive != null) {
            mFragmentTabLive.notifyFavorite(id, isFav);
        }
        runOnUiThread(() -> {
            if (mFragmentTabFavorite != null) {
                mFragmentTabFavorite.notifyData();
            }
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.notifyFavorite(id, isFav);
            }
        });

    }

    public void goToCountry(CountryModel model) {
        if (model != null) {
            setActionBarTitle(model.getName());
            showHideLayoutContainer(true);
            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_DETAIL_GENREPOD);
            mBundle.putBoolean(KEY_ALLOW_MORE, false);
            mBundle.putInt(KEY_NUMBER_ITEM_PER_PAGE, LIMIT_ITUNES_NORMAL);
            mBundle.putBoolean(KEY_ALLOW_READ_CACHE, false);
            mBundle.putBoolean(KEY_ALLOW_REFRESH, true);
            mBundle.putString(KEY_NAME_SCREEN, model.getName());

            mBundle.putString(KEY_SEARCH, model.getName());

            String tag = getCurrentFragmentTag();
            if (TextUtils.isEmpty(tag)) {
                goToFragment(TAG_FRAGMENT_DETAIL_COUNTRY, R.id.container, FragmentDetailListPod.class.getName(), 0, mBundle);
            } else {
                goToFragment(TAG_FRAGMENT_DETAIL_COUNTRY, R.id.container, FragmentDetailListPod.class.getName(), tag, mBundle);
            }
        }
    }

    public void goToSearchPod(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            showAppBar(true);
            hiddenKeyBoardForSearchView();
            FragmentDetailList mFragmentSearch = (FragmentDetailList) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_DETAIL_SEARCH);
            if (mFragmentSearch != null) {
                mFragmentSearch.startSearch(keyword);
            } else {
                backStack();
                setActionBarTitle(R.string.title_search);
                showHideLayoutContainer(true);
                Bundle mBundle = new Bundle();
                mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_SEARCH);
                mBundle.putBoolean(KEY_ALLOW_MORE, true);
                mBundle.putString(KEY_SEARCH, keyword);
                mBundle.putBoolean(KEY_ALLOW_READ_CACHE, false);
                mBundle.putBoolean(KEY_ALLOW_REFRESH, false);
                mBundle.putString(KEY_NAME_SCREEN, getString(R.string.title_search));
                goToFragment(TAG_FRAGMENT_DETAIL_SEARCH, R.id.container, FragmentDetailListPod.class.getName(), 0, mBundle);
            }
        }
    }


    public void goToPodCastModel(PodCastModel model) {
        if (model != null) {
            String title = StringUtils.getShortTitle(model.getName(), MAX_LENGTH_TITLE - 10);
            setActionBarTitle(title);
            showHideLayoutContainer(true);

            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_DETAIL_PODCAST);
            mBundle.putBoolean(KEY_ALLOW_MORE, false);
            mBundle.putInt(KEY_NUMBER_ITEM_PER_PAGE, LIMIT_ITUNES_NORMAL);
            mBundle.putBoolean(KEY_ALLOW_READ_CACHE, false);
            mBundle.putBoolean(KEY_ALLOW_REFRESH, true);
            mBundle.putString(KEY_NAME_SCREEN, title);
            mBundle.putParcelable(KEY_MODEL, model);
            goToFragment(TAG_FRAGMENT_DETAIL_PODCAST, R.id.container, FragmentDetailPodCast.class.getName(), mBundle);
        }
    }

    public void goToAddOrEditStation(@Nullable RadioModel model) {
        if (mMenu != null) {
            mMenu.findItem(R.id.action_premium).setVisible(false);
        }
        String title = getString(model != null ? R.string.title_edit_radio : R.string.title_add_radio);
        setActionBarTitle(title);
        showHideLayoutContainer(true);
        Bundle mBundle = new Bundle();
        mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_ADD_RADIO);
        mBundle.putString(KEY_NAME_SCREEN, title);
        mBundle.putParcelable(KEY_MODEL, model);
        goToFragment(TAG_FRAGMENT_ADD_RADIO, R.id.container, FragmentAddRadio.class.getName(), mBundle);
    }

    public void openTabsScreen(String title,String fragment_name){
        setActionBarTitle(title);
        showHideLayoutContainer(true);
        Bundle mBundle = new Bundle();
        mBundle.putString(KEY_NAME_SCREEN, title);
        goToFragment(TAG_FRAGMENT_ADD_RADIO, R.id.container, fragment_name, mBundle);
    }

    public void goToCloudFav() {
        if (!XRadioSettingManager.isSignedIn(this)) {
            goToLogin();
            return;
        }
        String shortTitle = StringUtils.getShortTitle(getString(R.string.title_cloud_favorite), MAX_LENGTH_TITLE - 10);
        setActionBarTitle(shortTitle);
        showHideLayoutContainer(true);
        Bundle mBundle = new Bundle();
        mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_USER_FAV_RADIOS);
        mBundle.putBoolean(KEY_ALLOW_MORE, true);
        mBundle.putBoolean(KEY_ALLOW_READ_CACHE, true);
        mBundle.putBoolean(KEY_ALLOW_REFRESH, true);
        mBundle.putString(KEY_NAME_SCREEN, shortTitle);
        goToFragment(TAG_FRAGMENT_USER_FAV, R.id.container, FragmentCloudFavorite.class.getName(), mBundle);
    }

    public void goToDownloadedPodCast() {
        boolean isDontAsk = XRadioSettingManager.getDontAskAgainDownload(this);
        boolean isGranted = this.checkStoragePermissions();
        YPYLog.e("DCM", "======>isGranted=" + isGranted + "===>isDontAsk=" + isDontAsk);
        if (!isDontAsk && !isGranted) {
            this.showPermissionDownloadDialog();
            return;
        }
        int type = isGranted ? TYPE_PODCAST_DOWNLOADED : TYPE_MY_DOWNLOAD;
        setActionBarTitle(R.string.title_downloaded_podcast);
        showHideLayoutContainer(true);
        Bundle mBundle = new Bundle();
        mBundle.putInt(KEY_TYPE_FRAGMENT, type);
        mBundle.putBoolean(KEY_ALLOW_REFRESH, isGranted);
        mBundle.putBoolean(KEY_OFFLINE_DATA, !isGranted);
        mBundle.putString(KEY_NAME_SCREEN, getString(R.string.title_downloaded_podcast));
        goToFragment(TAG_FRAGMENT_DOWNLOAD, R.id.container, FragmentDownloads.class.getName(), mBundle);
    }

    public void goToMyRadios() {
        setActionBarTitle(R.string.title_my_radio);
        showHideLayoutContainer(true);
        Bundle mBundle = new Bundle();
        mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_MY_RADIO);
        mBundle.putString(KEY_NAME_SCREEN, getString(R.string.title_my_radio));
        goToFragment(TAG_FRAGMENT_MY_RADIO, R.id.container, FragmentMyRadios.class.getName(), mBundle);
    }

    public void goToProfileTab() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_PROFILE) != null) {
            return;
        }
        setActionBarTitle(R.string.title_tab_profile);
        showHideLayoutContainer(true);
        Bundle mBundle = new Bundle();
        mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_SETTING);
        mBundle.putString(KEY_NAME_SCREEN, getString(R.string.title_tab_profile));
        goToFragment(TAG_FRAGMENT_PROFILE, R.id.container, FragmentProfile.class.getName(), mBundle);
    }


    public void goToGenreModel(GenreModel model) {
        if (model != null) {
            setActionBarTitle(model.getName());
            showHideLayoutContainer(true);
            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_DETAIL_GENRE);
            mBundle.putBoolean(KEY_ALLOW_MORE, true);
            mBundle.putBoolean(KEY_ALLOW_READ_CACHE, false);
            mBundle.putString(KEY_NAME_SCREEN, model.getName());
            mBundle.putBoolean(KEY_ALLOW_REFRESH, true);
            mBundle.putLong(KEY_GENRE_ID, model.getId());
            goToFragment(TAG_FRAGMENT_DETAIL_GENRE, R.id.container, FragmentDetailList.class.getName(), mBundle);
        }
    }

    public void goToShowMoreTopModel(String nameScreen, int status) {
        if (status < 0) {
            setActionBarTitle(nameScreen);
            showHideLayoutContainer(true);
            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_DETAIL_TOP);
            mBundle.putBoolean(KEY_ALLOW_MORE, true);
            mBundle.putBoolean(KEY_ALLOW_READ_CACHE, false);
            mBundle.putString(KEY_NAME_SCREEN, nameScreen);
            mBundle.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, true);
            mBundle.putBoolean(KEY_ALLOW_REFRESH, true);
            mBundle.putString(KEY_TYPE_TOP, status == ID_EDITOR_CHOICE ? TYPE_EDITOR_CHOICE : TYPE_NEW_RELEASE);
            goToFragment(TAG_FRAGMENT_DETAIL_TOP_MODEL, R.id.container, FragmentTopRadios.class.getName(), mBundle);
        }
    }

    public void startPlayingList(RadioModel model, ArrayList<RadioModel> listRadioModels) {
        if (!ApplicationUtils.isOnline(this) && !model.isOfflineModel()) {
            if (isAllCheckNetWorkOff) {
                showToast(R.string.info_connect_to_play);
                return;
            }
            if (YPYStreamManager.getInstance().isPrepareDone()) {
                startMusicService(ACTION_STOP);
            }
            showToast(R.string.info_connect_to_play);
            return;
        }
        if (YPYStreamManager.getInstance().isRecordingFile()) {
            showToast(R.string.info_recording_file);
            return;
        }
        RadioModel currentRadio = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
        if (currentRadio != null && currentRadio.equals(model)) {
            boolean isPrepareDone = YPYStreamManager.getInstance().isPrepareDone();
            if (!isPrepareDone) {
                startMusicService(ACTION_PLAY);
                return;
            }
            return;
        }
        countInterstitial++;
        showModeInterstitial(countInterstitial, INTERSTITIAL_FREQUENCY, () -> playRadio(model, listRadioModels));
    }

    private void playRadio(RadioModel model, ArrayList<RadioModel> listRadioModels) {
        if (mYPYCastManager != null && mYPYCastManager.isCastConnected()) {
            if (YPYStreamManager.getInstance().isPrepareDone()) {
                startMusicService(ACTION_STOP);
            }
            processCastRadio(model, ACTION_PLAY_NOW);
            return;
        }
        updateInfoOfPlayingTrack(model);
        String url = model != null ? model.getArtWork() : null;
        if (mFragmentDragDrop != null) {
            mFragmentDragDrop.updateImage(url);
        }
        if (listRadioModels != null && listRadioModels.size() > 0) {
            ArrayList<RadioModel> mListPlaying = (ArrayList<RadioModel>) YPYStreamManager.getInstance().getListTrackModels();
            if (mListPlaying == null || !mTotalMng.isListEqual(mListPlaying, listRadioModels)) {
                ArrayList<RadioModel> mListDatas = (ArrayList<RadioModel>) listRadioModels.clone();
                removeNativeAdsModel(mListDatas);
                YPYStreamManager.getInstance().setListTrackModels(mListDatas);

            }
            startPlayRadio(model);
        }
    }

    public void startPlayRadio(RadioModel trackModel) {
        try {
            viewBinding.layoutTotalDragDrop.btnSmallPlay.setImageResource(R.drawable.ic_play_arrow_white_36dp);
            boolean b = YPYStreamManager.getInstance().setCurrentModel(trackModel);
            if (b) {
                startMusicService(ACTION_PLAY);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            viewBinding.layoutTotalDragDrop.btnSmallPlay.setImageResource(R.drawable.ic_play_arrow_white_36dp);
            startMusicService(ACTION_STOP);
        }

    }

    public void updateInfoOfPlayingTrack() {
        RadioModel radioModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
        updateInfoOfPlayingTrack(radioModel);
    }

    private void updateInfoOfPlayingTrack(RadioModel ringtoneModel) {
        try {
            if (ringtoneModel != null) {
                showLayoutListenMusic(true);
                viewBinding.layoutTotalDragDrop.tvRadioName.setText(Html.fromHtml(ringtoneModel.getName()));
                String artist = ringtoneModel.getMetaData();
                if (TextUtils.isEmpty(artist)) {
                    String tag = ringtoneModel.getTags();
                    artist = !TextUtils.isEmpty(tag) ? tag : getString(R.string.title_unknown);
                }
                viewBinding.layoutTotalDragDrop.tvInfo.setText(artist);
                viewBinding.layoutTotalDragDrop.tvInfo.setSelected(true);

                String imgSong = ringtoneModel.getArtWork();
                GlideImageLoader.displayImage(this, viewBinding.layoutTotalDragDrop.imgSong, imgSong, resDefaultPlayer);
                if (mFragmentDragDrop != null) {
                    mFragmentDragDrop.updateInfo();
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("SuspiciousIndentation")
    @Override
    public void processBroadcast(String actionPlay, long value) {
        if(!(actionPlay.equalsIgnoreCase(".action.UPDATE_POS")))
        Log.e("TAG processBroadcast","actionPlay="+actionPlay);

        if (actionPlay.equalsIgnoreCase(ACTION_LOADING)) {
            showLoading(true);
            updateInfoOfPlayingTrack();
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.showLoading(true);
                mFragmentDragDrop.updateInfo();
                RadioModel model = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                mFragmentDragDrop.updateImage(model != null ? model.getArtWork() : null);
            }
        }
        if (actionPlay.equalsIgnoreCase(ACTION_DIMINISH_LOADING)) {
            showLoading(false);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.showLoading(false);
            }
        }
        if (actionPlay.equalsIgnoreCase(ACTION_RESET_INFO)) {
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateInfo();
                mFragmentDragDrop.updateImage(null);
            }
        }
        if (actionPlay.equalsIgnoreCase(ACTION_COMPLETE)) {
            updateStatePlayer(false);
            viewBinding.layoutTotalDragDrop.tvInfo.setText(R.string.info_radio_ended_title);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateInfoWhenComplete();
            }
        }
        if (actionPlay.equalsIgnoreCase(ACTION_CONNECTION_LOST)) {
            updateStatePlayer(false);
            viewBinding.layoutTotalDragDrop.tvInfo.setText(R.string.info_connection_lost);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateInfoWhenComplete();
            }
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_PAUSE)) {
            updateStatePlayer(false);
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_PLAY)) {
            updateStatePlayer(true);
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_STOP) || actionPlay.equalsIgnoreCase(ACTION_ERROR)) {
            updateStatePlayer(false);
            showLayoutListenMusic(false);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateSleepMode(0);
                mFragmentDragDrop.updateStatusPlayer(false);
            }
            collapseListenMusic();
            if (actionPlay.equalsIgnoreCase(ACTION_ERROR)) {
                int resId = ApplicationUtils.isOnline(this) ? R.string.info_play_error : R.string.info_connect_to_play;
                showToast(resId);
                startMusicService(ACTION_STOP);
            }
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_UPDATE_INFO)) {
            updateInfoOfPlayingTrack();
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_UPDATE_SLEEP_MODE)) {
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateSleepMode(value);
            }
        }
        else if (actionPlay.contains("ACTION_RECORD_")) {
            processActionRecord(actionPlay);
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_UPDATE_POS)) {
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateTimer(value);
            }
        }

    }

    private void processActionRecord(String action) {
        if (action.equalsIgnoreCase(ACTION_RECORD_START)) {
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateStateRecord(true);
            }
            return;
        }
        else if (action.equalsIgnoreCase(ACTION_RECORD_FINISH)) {
            showDialogSaveFile();
        }
        else if (action.equalsIgnoreCase(ACTION_RECORD_ERROR_SD)) {
            showToast(R.string.info_record_error_sdcard);
        }
        else if (action.equalsIgnoreCase(ACTION_RECORD_ERROR_SHORT_TIME)) {
            showToast(R.string.info_record_error_short);
        }
        else if (action.equalsIgnoreCase(ACTION_RECORD_ERROR_UNKNOWN)) {
            showToast(R.string.info_record_error_unknown);
        }
        else if (action.equalsIgnoreCase(ACTION_RECORD_MAXIMUM)) {
            showToast(String.format(getString(R.string.format_recording_maximum), String.valueOf(MAXIMUM_DELTA_RECORD)));
            showDialogSaveFile();

        }

        if (mFragmentDragDrop != null) {
            mFragmentDragDrop.updateStateRecord(false);
        }
    }

    @SuppressLint("NewApi")
    private void showDialogSaveFile() {
        if (recordManager == null) return;
        showFullDialog(R.string.title_confirm,
                getString(R.string.info_saved_file),
                R.string.title_save,
                R.string.title_cancel, () -> recordManager.startSaveFile(() -> {
                    if (viewBinding.viewPager.getCurrentItem() == mListHomeFragments.indexOf(mFragmentLibraries)) {
                        mFragmentLibraries.startLoadData();
                    }
                }),
                () -> {
            if (IOUtils.isAndroid14())
                recordManager.deleteFileTemp(LIST_STORAGE_PERMISSIONS_14);
            else if (IOUtils.isAndroid13())
                recordManager.deleteFileTemp(LIST_STORAGE_PERMISSIONS_13);
            else
                recordManager.deleteFileTemp(LIST_STORAGE_PERMISSIONS);
        });
    }

    @Override
    public void onDoWhenNetworkOn() {
        super.onDoWhenNetworkOn();
        if (isHavingListStream()) {
            if (isAllCheckNetWorkOff) {
                isAllCheckNetWorkOff = false;
                startMusicService(ACTION_TOGGLE_PLAYBACK);
            }
        }

    }

    @Override
    public void onLoadAds() {
        super.onLoadAds();
        if (mAdvertisement != null) {
            mAdvertisement.setUpRewardAd(this);
            initOpenAds();
        }
    }

    @Override
    public void onDoWhenNetworkOff() {
        super.onDoWhenNetworkOff();
        if (isHavingListStream() && YPYStreamManager.getInstance().isPlaying()) {
            isAllCheckNetWorkOff = true;
            startMusicService(ACTION_CONNECTION_LOST);
        }
    }

    @Override
    public void processUpdateImage(String imgSong) {
        try {
            if (TextUtils.isEmpty(imgSong)) {
                RadioModel radioModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                imgSong = radioModel.getArtWork();
            }
            GlideImageLoader.displayImage(this, viewBinding.layoutTotalDragDrop.imgSong, imgSong, resDefaultPlayer);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateImage(imgSong);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoading(boolean b) {
        viewBinding.layoutTotalDragDrop.layoutBtAction.setVisibility(!b ? View.VISIBLE : View.INVISIBLE);
        viewBinding.layoutTotalDragDrop.imgStatusLoading.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public void updateStatePlayer(boolean isPlaying) {
        int playId = isPlaying ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_arrow_white_36dp;
        viewBinding.layoutTotalDragDrop.btnSmallPlay.setImageResource(playId);
        if (mFragmentDragDrop != null) {
            mFragmentDragDrop.updateStatusPlayer(isPlaying);
        }

    }

    @Override
    protected void onDestroy() {
        DatabaseManager.getInstance(this).onDestroy();
        this.viewBinding.viewPager.setAdapter(null);
        if (this.mListHomeFragments != null) {
            this.mListHomeFragments.clear();
            this.mListHomeFragments = null;
        }
        super.onDestroy();
    }

    public boolean checkActionBeforePlay(boolean isNeedCheckRecord) {
        YPYMusicModel mCurrentRadio = YPYStreamManager.getInstance().getCurrentModel();
        boolean isOffline = mCurrentRadio != null && mCurrentRadio.isOfflineModel();
        if (isAllCheckNetWorkOff && !ApplicationUtils.isOnline(this) && !isOffline) {
            showToast(R.string.info_connect_to_play);
            return true;
        }
        if (isNeedCheckRecord && YPYStreamManager.getInstance().isRecordingFile()) {
            showToast(R.string.info_recording_file);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (checkActionBeforePlay(view.getId() != R.id.btn_small_play)) {
            return;
        }
        int id = view.getId();
        if (id == R.id.btn_small_next) {
            startMusicService(ACTION_NEXT);
        }
        else if (id == R.id.btn_small_prev) {
            startMusicService(ACTION_PREVIOUS);
        }
        else if (id == R.id.btn_small_play) {
            boolean isPrepareDone = YPYStreamManager.getInstance().isPrepareDone();
            if (!isPrepareDone) {
                startMusicService(ACTION_PLAY);
                return;
            }
            startMusicService(ACTION_TOGGLE_PLAYBACK);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewBinding.viewPager.getCurrentItem() >= 0) {
            outState.putInt(KEY_TOP_INDEX, viewBinding.viewPager.getCurrentItem());
        }
    }

    @Override
    public void onUpdateUIWhenSupportRTL() {
        super.onUpdateUIWhenSupportRTL();
        viewBinding.layoutTotalDragDrop.tvRadioName.setGravity(Gravity.END);
        viewBinding.layoutTotalDragDrop.tvInfo.setGravity(Gravity.END);
        viewBinding.layoutTotalDragDrop.btnSmallNext.setImageResource(R.drawable.ic_skip_previous_white_36dp);
        viewBinding.layoutTotalDragDrop.btnSmallPrev.setImageResource(R.drawable.ic_skip_next_white_36dp);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_tab_live) {
            selectedTab(mListHomeFragments.indexOf(mFragmentTabLive));
        } else if (itemId == R.id.action_tab_favorite) {
            selectedTab(mListHomeFragments.indexOf(mFragmentTabFavorite));
        } else if (itemId == R.id.action_tab_search) {
            selectedTab(mListHomeFragments.indexOf(mFragmentTabSearch));
        } else if (itemId == R.id.action_tab_library) {
            selectedTab(mListHomeFragments.indexOf(mFragmentLibraries));
        } else if (itemId == R.id.action_tab_podcast) {
            selectedTab(mListHomeFragments.indexOf(mFragmentTabPodcast));
        }
        return true;
    }

    private void selectedTab(int pos) {
        hiddenKeyBoardForSearchView();
        viewBinding.appBar.setExpanded(true);
        viewBinding.viewPager.setCurrentItem(pos);
        ((YPYFragment<?>) mListHomeFragments.get(pos)).startLoadData();
    }

    public void resetLoadWhenSignOut() {
        showAppBar(true);
        if (mFragmentTabLive != null) {
            mFragmentTabLive.setLoadingData(false);
            if (mListHomeFragments.indexOf(mFragmentTabLive) == viewBinding.viewPager.getCurrentItem()) {
                mFragmentTabLive.startLoadData();
            }
        }
        if (mFragmentTabFavorite != null) {
            mFragmentTabFavorite.setLoadingData(false);
            if (mListHomeFragments.indexOf(mFragmentTabFavorite) == viewBinding.viewPager.getCurrentItem()) {
                mFragmentTabFavorite.startLoadData();
            }
        }
    }

    @Override
    public void updateWhenCastConnect(boolean isCastConnect) {
        super.updateWhenCastConnect(isCastConnect);
        if (YPYStreamManager.getInstance().isHavingList() && isCastConnect) {
            startMusicService(ACTION_STOP);
        }
    }


    @Override
    public void onRewardedVideoLoaded() {
    }

    @Override
    public void onRewardedVideoClosed(boolean isRewardCompleted) {
        if (isRewardCompleted) {
            if (downloadModel != null) {
                this.downloadManager.startDownloadFile(downloadModel);
            }
        }
        else {
            showToast(R.string.info_rewards_ads_error);
        }
    }


    @Override
    public void onReceiveRewardAds() {
        if (downloadModel != null) {
            showToast(R.string.info_rewards_ads_success);
        }
    }

    @Override
    public void onErrorLoadedRewardAds() {
    }

    @Override
    public void onErrorShowRewardAds() {
        if (downloadModel != null) {
            showModeInterstitial(1, 1, () -> this.downloadManager.startDownloadFile(downloadModel));
        }
    }

    public void startDownloadFile(@NonNull RadioModel model) {
        if (!ApplicationUtils.isOnline(this)) {
            showToastWithLongTime(R.string.info_lose_internet);
            return;
        }
        this.downloadModel = model;
        boolean isDontAsk = XRadioSettingManager.getDontAskAgainDownload(this);
        boolean isGranted = this.checkStoragePermissions();
        if (!isDontAsk && !isGranted) {
            this.showPermissionDownloadDialog();
            return;
        }
        Log.d("Inside on startDownloadFile Ads => " + mAdvertisement.isRewardLoaded(), String.valueOf(mAdvertisement != null));
        if (mAdvertisement != null && mAdvertisement.isRewardLoaded()) {
            this.mAdvertisement.showReward(this);
        }
        else {
            showModeInterstitial(1, 1, () -> this.downloadManager.startDownloadFile(model));
        }

    }

    public void showPopUpMenu(@NonNull View mView, @NonNull RadioModel model) {
        try {
            boolean isDark = XRadioSettingManager.isDarkMode(this);
            Context wrapper = new ContextThemeWrapper(this, isDark ? R.style.AppThemeDarkFull : R.style.AppThemeLightFull);
            PopupMenu popupMenu = new PopupMenu(wrapper, mView);
            popupMenu.getMenuInflater().inflate(R.menu.menu_radios, popupMenu.getMenu());
            boolean canDownload = model.canDownload();
            popupMenu.getMenu().findItem(R.id.action_report).setVisible(!canDownload);
            if (canDownload) {
                boolean isDownloaded = downloadManager.isDownloaded(model);
                popupMenu.getMenu().findItem(R.id.action_download).setVisible(!isDownloaded);
                if (isDownloaded) {
                    boolean isMediaStore = model.getPath() != null && model.getPath().startsWith(PREFIX_CONTENT);
                    popupMenu.getMenu().findItem(R.id.action_delete).setVisible(!isMediaStore || !IOUtils.isAndroid10());
                    popupMenu.getMenu().findItem(R.id.action_move_gallery).setVisible(!isMediaStore && checkStoragePermissions());
                }
                else {
                    popupMenu.getMenu().findItem(R.id.action_delete).setVisible(false);
                }
            }
            else {
                popupMenu.getMenu().findItem(R.id.action_download).setVisible(false);
                popupMenu.getMenu().findItem(R.id.action_delete).setVisible(false);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_delete) {
                    FragmentDownloads mFragmentDownload = (FragmentDownloads) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_DOWNLOAD);
                    showFullDialog(R.string.title_confirm, getString(R.string.info_remove_file), R.string.title_remove
                            , R.string.title_cancel, () -> downloadManager.deleteEpisode(model, mFragmentDownload));
                }
                else if (itemId == R.id.action_report) {
                    showDialogReport(model);
                }
                else if (itemId == R.id.action_share) {
                    shareRadioModel(model);
                }
                else if (itemId == R.id.action_download) {
                    Log.e("TAG MainActivity","R.id.action_download");
                    if (!isPremiumMember()) {
                         Log.e("TAG MainActivity","isPremiumMember false R.id.action_download");
                        askWatchAddOrSubcriptionPopUp(this, model);
                    } else  {
                         Log.e("TAG MainActivity","isPremiumMember true R.id.action_download");
                        downloadManager.startDownloadFile(model);
                }
                }
                else if (itemId == R.id.action_move_gallery) {
                    FragmentDownloads mFragmentDownload = (FragmentDownloads) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_DOWNLOAD);
                    downloadManager.moveToMediaStore(model, mFragmentDownload);
                }
                return true;
            });
            popupMenu.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void askWatchAddOrSubcriptionPopUp(Context context, RadioModel model) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Premium");
            builder.setMessage("This is a Premium feature, Watch this Ad and then you can download it " +
                    "or you can buy the Premium version for unlimited access.");

            builder.setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent mIntent = new Intent(MainActivity.this, UpgradePremiumActivity.class);
                    mIntent.putExtra("from","download");
                    startActivity(mIntent);
                   /// finish();
                }
            });

            builder.setNegativeButton("Watch Ad", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startDownloadFile(model);
                }
            });

            builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_DOWNLOAD) {
            if (ApplicationUtils.isGrantAllPermission(grantResults)) {
                if (downloadModel != null) {
                    startDownloadFile(downloadModel);
                }
                else {
                    goToDownloadedPodCast();
                }
            }
            else {
                showToast(R.string.info_permission_denied);
            }
        }
    }
}
