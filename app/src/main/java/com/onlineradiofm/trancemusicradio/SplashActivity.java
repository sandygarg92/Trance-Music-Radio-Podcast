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

import static com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils.isSupportRTL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.dataMng.MemberShipManager;
import com.onlineradiofm.trancemusicradio.dataMng.TotalDataManager;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.databinding.ActivitySplashBinding;
import com.onlineradiofm.trancemusicradio.databinding.DialogTermOfConditionBinding;
import com.onlineradiofm.trancemusicradio.db.DatabaseManager;
import com.onlineradiofm.trancemusicradio.gdpr.GDPRManager;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.activity.YPYFragmentActivity;
import com.onlineradiofm.trancemusicradio.ypylibs.activity.YPYSplashActivity;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.AdMobAdvertisement;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.AppOpenAdsManager;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.YPYAdvertisement;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.music.model.YPYMusicModel;
import com.onlineradiofm.trancemusicradio.ypylibs.remoteconfig.FirebaseRemote;
import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends YPYSplashActivity<ActivitySplashBinding> implements IRadioConstants, PurchasesUpdatedListener {

    private TotalDataManager mTotalMng;
    private boolean isAllowShowAdsWhenAskingTerm = true;
    private final Handler mHandler = new Handler();
    private MemberShipManager memberShipManager;
    private boolean isLoadedDataFromFB;
    private FirebaseRemote mFirebaseConfig;
    protected AppOpenAdsManager appOpenAdsManager;

    @Override
    protected ActivitySplashBinding getViewBinding() {
        return ActivitySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpOverlayBackground(true);
        YPYLog.setDebug(DEBUG);

        mTotalMng = TotalDataManager.getInstance(getApplicationContext());

        updateThemeColor(XRadioSettingManager.isDarkMode(this));

        mFirebaseConfig = new FirebaseRemote(this, FIREBASE_CACHE_EXPIRATION, R.xml.remote_config_defaults);

        //reset sleep mode
        XRadioSettingManager.setSleepMode(this, 0);
        memberShipManager = new MemberShipManager(this, this);
        memberShipManager.setMemberManagerListener((isSuccess, error) -> {
            YPYLog.e(IRadioConstants.TAG, "==========>onFinishingCheckIAP errorCode=" + error + "==>isSuccess=" + isSuccess);
            startLoadFromFirebase();
        });

        initOpenAds();
    }

    public void initOpenAds() {
        String adType = getString(R.string.ad_type);
        if (adType.equalsIgnoreCase(AdMobAdvertisement.ADMOB_ADS)) {
            YPYLog.e(IRadioConstants.TAG, "=====>init open ads");
            this.appOpenAdsManager = new AppOpenAdsManager(this, getString(R.string.ad_open_id));
            this.appOpenAdsManager.fetchOpenAdsInApp();
        }
    }

    public void updateThemeColor(boolean isDark) {
        setLightStatusBar(!isDark);
        //TODO DARK MODE
        int bgColor = ContextCompat.getColor(this, isDark ? R.color.dark_color_background : R.color.light_color_background);
        viewBinding.layoutBg.setBackgroundColor(bgColor);

        int textColor = ContextCompat.getColor(this, isDark ? R.color.dark_text_main_color : R.color.light_text_main_color);
        viewBinding.tvLogoTop.setTextColor(textColor);

        int textSecondColor = ContextCompat.getColor(this, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);
        viewBinding.tvLoading.setTextColor(textSecondColor);

        //    int splashProgress = ContextCompat.getColor(this, isDark ? R.color.dark_progressbar_splash : R.color.light_progressbar_splash);
        //    viewBinding.progressBar1.setIndicatorColor(splashProgress);

        int textAccentColor = ContextCompat.getColor(this, isDark ? R.color.dark_color_accent : R.color.grey_80);
        viewBinding.tvBottomSlogan.setTextColor(textAccentColor);


    }

    @Override
    public void onInitData() {
        viewBinding.animationView.setVisibility(View.VISIBLE);
        showDialogTerm(() -> YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            boolean checkIAP = memberShipManager != null && memberShipManager.checkIAP();
            if (!checkIAP) {
                runOnUiThread(this::startLoadFromFirebase);
            }
        }));
    }

    private void onLoadLocalCache() {
        runOnUiThread(this::onStartCreateAds);
        if (isGrantAllPermission(getListPermissionNeedGrant())) {
            mTotalMng.readAllCache();
            //TODO save last played song
            long lastPlayedId = XRadioSettingManager.getLastPlayedRadioId(this);
            boolean isMyLastedPlay = XRadioSettingManager.getLastPlayedMyRadio(this);
            if (lastPlayedId > 0) {
                ResultModel<RadioModel> result = null;
                if (isMyLastedPlay) {
                    result = DatabaseManager.getInstance(this).getRadioWithId(this, lastPlayedId);
                }
                else {
                    if (ApplicationUtils.isOnline(this)) {
                        result = XRadioNetUtils.getLastPlayedRadios(this);
                    }
                }
                if (result != null && result.firstModel() != null) {
                    mTotalMng.setListCacheData(TYPE_LAST_PLAYED_RADIO, result.getListModels());
                }
                ArrayList<RadioModel> listRadios = (ArrayList<RadioModel>) mTotalMng.getListData(TYPE_LAST_PLAYED_RADIO);
                if (!YPYStreamManager.getInstance().isHavingList()) {
                    YPYStreamManager.getInstance().setListTrackModels((ArrayList<? extends YPYMusicModel>) listRadios.clone());
                }
            }
        }
        runOnUiThread(this::checkGDPR);
    }

    @Override
    public File getDirectoryCached() {
        return mTotalMng.getDirectoryCached(getApplicationContext());
    }

    @Override
    public String[] getListPermissionNeedGrant() {
        if (IOUtils.isAndroid14())
            return LIST_STORAGE_PERMISSIONS_14;
        else if (IOUtils.isAndroid13())
            return LIST_STORAGE_PERMISSIONS_13;
        return LIST_STORAGE_PERMISSIONS;
    }

    @Override
    public YPYAdvertisement createAds() {
        if (!MemberShipManager.isIAPremiumMember(this)) {
            String adType = getString(R.string.ad_type);
            String bannerId = getString(R.string.ad_banner_id);
            String interstitialId = getString(R.string.ad_interstitial_id);


            {
                String appId = getString(R.string.admob_app_id);
                AdMobAdvertisement mAdmob = new AdMobAdvertisement(this, bannerId, interstitialId, ADMOB_TEST_DEVICE);
                if (!TextUtils.isEmpty(appId) && (!TextUtils.isEmpty(bannerId) || !TextUtils.isEmpty(interstitialId))) {
                    GDPRManager.getInstance().init(appId, ADMOB_TEST_DEVICE);
                }
                return mAdmob;
            }
        }
        return null;
    }

    public void showDialogTerm(IYPYCallback mCallback) {
        if (!XRadioSettingManager.getAgreeTerm(this)) {
            try {
                DialogTermOfConditionBinding viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_term_of_condition, null, false);
                String format = getString(R.string.format_term_and_conditional);
                String msg = String.format(format, getString(R.string.app_name), URL_TERM_OF_USE, URL_PRIVACY_GOOGLE);
                Spanned result;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    result = Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY);
                }
                else {
                    result = Html.fromHtml(msg);
                }
                viewBinding.tvTermInfo.setText(result);
                viewBinding.tvTermInfo.setMovementMethod(LinkMovementMethod.getInstance());
                MaterialDialog.Builder mBuilder = createBasicDialogBuilder(R.string.title_term_of_use, R.string.title_agree, R.string.title_no);
                mBuilder.canceledOnTouchOutside(false);
                mBuilder.titleGravity(GravityEnum.CENTER);
                mBuilder.customView(viewBinding.getRoot(), true);
                boolean b = isSupportRTL();
                if (b) {
                    viewBinding.tvTermInfo.setGravity(Gravity.END);
                }
                mBuilder.onPositive((dialog, which) -> {
                    XRadioSettingManager.setAgreeTerm(this, true);
                    isAllowShowAdsWhenAskingTerm = false;
                    if (mCallback != null) {
                        mCallback.onAction();
                    }
                });
                mBuilder.onNegative((dialog, which) -> {
                    onDestroyData();
                    finish();
                });
                mBuilder.keyListener((dialogInterface, i, keyEvent) -> i == KeyEvent.KEYCODE_BACK);
                mBuilder.show();
            }
            catch (Exception e) {
                e.printStackTrace();
                XRadioSettingManager.setAgreeTerm(this, true);
            }
            return;
        }
        if (mCallback != null) {
            mCallback.onAction();
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        if (memberShipManager != null) {
            memberShipManager.onDestroy();
        }
    }

    private void checkGDPR() {
        if (!MemberShipManager.isIAPremiumMember(this)
                || isGrantAllPermission(getListPermissionNeedGrant())) {
            GDPRManager.getInstance().startCheck(this, mHandler, () -> goToMainActivity(isAllowShowAdsWhenAskingTerm));
        }
        else {
            goToMainActivity(isAllowShowAdsWhenAskingTerm);
        }
    }

    public void goToMainActivity(boolean isShowAds) {
        CountDownTimer countDownTimer =
                new CountDownTimer(3 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        // ✅ Check premium status
                        if (MemberShipManager.isIAPremiumMember(SplashActivity.this)) {
                            Log.d("Ads not For Pre-Users", "User is premium, skipping ads.");
                            goToRealActivity(); // Skip app open ads for premium users
                        } else {
                            // Show the app open ad.
                            appOpenAdsManager.showAdIfAvailable(() -> goToRealActivity());
                        }
                    }
                };
        countDownTimer.start();
    }

    private void goToRealActivity() {
        try{
      //      viewBinding.progressBar1.hide();
            viewBinding.animationView.setVisibility(View.INVISIBLE);
            Class<? extends YPYFragmentActivity> clazz = !isGrantAllPermission(getListPermissionNeedGrant()) ? GrantPermissionActivity.class : MainActivity.class;
            Intent mIntent = new Intent(this, clazz);
            startActivity(mIntent);
            finish();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        Log.e("DCM", "========>onPurchasesUpdated=" + billingResult.getResponseCode());
    }

    private boolean isGrantAllPermission(String[] permission) {
        if (XRadioSettingManager.getSkipNow(this)) {
            return true;
        }
        return ApplicationUtils.isGrantAllPermission(this, permission);
    }
    private void startLoadFromFirebase() {
        mFirebaseConfig.fetchDataFromFireBase(() -> {
                    mHandler.removeCallbacksAndMessages(null);
                    parseDataFromFirebase();
                },
                () -> {
                    mHandler.removeCallbacksAndMessages(null);
                    parseDataFromFirebase();
                });
        mHandler.postDelayed(this::parseDataFromFirebase, TIME_OUT_MAX_TIME_WAIT);
    }

    private void parseDataFromFirebase() {
        try {
            if (!isLoadedDataFromFB) {
                isLoadedDataFromFB = true;
                long freqOpenAds = mFirebaseConfig.getFirebaseLongConfig(KEY_OPEN_ADS_FREQ);
              //  long freqCta = mFirebaseConfig.getFirebaseLongConfig(KEY_IAM_FREQ);


                mTotalMng.setIamOpenAdFreq(freqOpenAds);
              //  mTotalMng.setIamCtaFreq(freqCta);

                YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(this::onLoadLocalCache);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


}

