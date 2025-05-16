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

import static com.onlineradiofm.trancemusicradio.ShowUrlActivity.KEY_HEADER;
import static com.onlineradiofm.trancemusicradio.ShowUrlActivity.KEY_SHOW_URL;
import static com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils.isSupportRTL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.gms.tasks.Task;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.dataMng.MemberShipManager;
import com.onlineradiofm.trancemusicradio.dataMng.RetroRadioNetUtils;
import com.onlineradiofm.trancemusicradio.dataMng.TotalDataManager;
import com.onlineradiofm.trancemusicradio.databinding.DialogSleepTimeBinding;
import com.onlineradiofm.trancemusicradio.databinding.DialogStoragePermissionBinding;
import com.onlineradiofm.trancemusicradio.equalizer.EqualizerActivity;
import com.onlineradiofm.trancemusicradio.fragment.XRadioListFragment;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.stream.service.XRadioAudioService;
import com.onlineradiofm.trancemusicradio.ypylibs.activity.YPYFragmentActivity;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.AdMobAdvertisement;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.AppOpenAdsManager;
import com.onlineradiofm.trancemusicradio.ypylibs.ads.YPYAdvertisement;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.googlecast.YPYCastManager;
import com.onlineradiofm.trancemusicradio.ypylibs.googlecast.queue.YPYQueueDataProvider;
import com.onlineradiofm.trancemusicradio.ypylibs.googlecast.queue.YPYQueueUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.music.constant.IYPYStreamConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYResultModelCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ShareActionUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;
import com.onlineradiofm.trancemusicradio.ypylibs.view.CircularProgressBar;
import com.triggertrap.seekarc.SeekArc;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/19/17.
 */

public abstract class RadioFragmentActivity<T extends ViewBinding> extends YPYFragmentActivity implements IRadioConstants, IYPYStreamConstants {

    public static final int REQUEST_PERMISSION_RECORD = 1001;
    public static final int REQUEST_PERMISSION_DOWNLOAD = 1002;

    public TotalDataManager mTotalMng;

    public boolean isPausing;
    public Bundle mSavedInstance;

    private boolean isStartCheckRecord;

    private ApplicationBroadcast mApplicationBroadcast;
    public YPYCastManager mYPYCastManager;
    protected T viewBinding;

    protected boolean isLoadAgainOpenAds;
    protected AppOpenAdsManager appOpenAdsManager;
    private long countOpenAds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onDoBeforeSetView();
        super.onCreate(savedInstanceState);

        //init view binding
        viewBinding = getViewBinding();
        View view = viewBinding.getRoot();
        setContentView(view);

        this.mTotalMng = TotalDataManager.getInstance(getApplicationContext());
        this.mSavedInstance = savedInstanceState;

        createArrayFragment();
        onRestoreFragment(savedInstanceState);

        onDoWhenDone();
        processRightToLeft();

    }

    public void setUpGoogleCast() {
        //TODO SETUP CAST MANAGER
        mYPYCastManager = new YPYCastManager(this, getString(R.string.info_intro_cast), R.color.intro_cast_bg_color);
    }


    protected void onDoBeforeSetView() {
        setUpOverlayBackground(true);
    }


    @Override
    public YPYAdvertisement createAds() {
        if (!isPremiumMember()) {
            String adType = getString(R.string.ad_type);
            String bannerId = getString(R.string.ad_banner_id);
            String interstitialId = getString(R.string.ad_interstitial_id);
            String rewardId = getString(R.string.ad_reward_id);

            {
                AdMobAdvertisement mAdmob = new AdMobAdvertisement(this, bannerId, interstitialId, ADMOB_TEST_DEVICE);
                mAdmob.setRewardId(rewardId);
                return mAdmob;
            }

        }
        return null;
    }

    public void initOpenAds() {
//        String adType = getString(R.string.ad_type);
//        if (adType.equalsIgnoreCase(AdMobAdvertisement.ADMOB_ADS)) {
//            YPYLog.e(IRadioConstants.TAG, "=====>init open ads");
//            this.appOpenAdsManager = new AppOpenAdsManager(this, getString(R.string.ad_open_id));
//            this.appOpenAdsManager.fetchOpenAdsInApp();
//            this.appOpenAdsManager.showAdIfAvailable(() -> appOpenAdsManager.fetchOpenAdsInApp());
//        }
    }


    protected abstract T getViewBinding();

    public void updateThemeColor(boolean isDark) {
        setLightStatusBar(!isDark);
        if (mProgressDialog != null) {
            TextView mTvMessage = mProgressDialog.findViewById(R.id.tv_message);
            RelativeLayout mRootDialog = mProgressDialog.findViewById(R.id.layout_root_dialog);
            CircularProgressBar mProgressBar = mProgressDialog.findViewById(R.id.progressBar1);
            int colorText = ContextCompat.getColor(this, isDark ? R.color.dark_text_main_color : R.color.light_text_main_color);
            int bgColor = ContextCompat.getColor(this, isDark ? R.color.dark_color_background : R.color.light_color_background);
            int progressColor = ContextCompat.getColor(this, isDark ? R.color.dark_progressbar_color : R.color.light_progressbar_color);
            mRootDialog.setBackgroundColor(bgColor);
            mProgressBar.setProgressColor(progressColor);
            mTvMessage.setTextColor(colorText);
        }
    }

    public void setUpLayoutBanner() {
        setUpBottomBanner(R.id.layout_ads, SHOW_ADS);
    }


    @Override
    public void onDestroyData() {
        super.onDestroyData();
        mTotalMng.onDestroy();
    }

    public void showAppRate() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(taskLaunch -> {
                });
            }
        });

    }

    @Override
    protected void onPause() {
        if (mYPYCastManager != null) {
            mYPYCastManager.onCastPause();
        }
        super.onPause();
        isPausing = true;
    }

    @Override
    protected void onResume() {
        if (mYPYCastManager != null) {
            mYPYCastManager.onCastResume();
            boolean b = mYPYCastManager.isCastConnected();
            updateWhenCastConnect(b);
        }
        super.onResume();
        if (isPausing) {
            isPausing = false;
            onDoWhenResume();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mYPYCastManager != null) {
            boolean b = mYPYCastManager.isCastConnected();
            updateWhenCastConnect(b);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    public void updateWhenCastConnect(boolean isCastConnect) {

    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        try {
            CastContext mCastContext = CastContext.getSharedInstance(this);
            return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event)
                    || super.dispatchKeyEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.dispatchKeyEvent(event);
    }

    public void onDoWhenResume() {
//        try {
//            YPYLog.e(IRadioConstants.TAG, "========>isLoadAgainOpenAds=" + isLoadAgainOpenAds + "===>appOpenAdsManager=" + appOpenAdsManager);
//            if (isLoadAgainOpenAds && appOpenAdsManager != null) {
//                this.isLoadAgainOpenAds = false;
//                appOpenAdsManager.fetchOpenAdsInApp();
//                return;
//            }
//            countOpenAds++;
//            long freqClick = mTotalMng.getIamOpenAdFreq();
//            YPYLog.e(IRadioConstants.TAG, "========>countOpenAds=" + countOpenAds + "===>freqClick=" + freqClick);
//            if (freqClick > 0 && countOpenAds > 0 && countOpenAds % freqClick == 0 && appOpenAdsManager != null) {
//                appOpenAdsManager.showAdIfAvailable(() -> appOpenAdsManager.fetchOpenAdsInApp());
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public void onDoWhenDone() {
        onStartCreateAds();
        updateThemeColor(XRadioSettingManager.isDarkMode(this));
        if (ApplicationUtils.isOnline(this)) {
            onDoWhenNetworkOn();
        }
        registerNetworkBroadcastReceiver(isNetworkOn -> {
            if (isNetworkOn) {
                onDoWhenNetworkOn();
            } else {
                onDoWhenNetworkOff();
            }
        });
    }

    public void onDoWhenNetworkOn() {
        //TODO make this one for open bidding Android
        if (mAdvertisement != null && mAdvertisement instanceof AdMobAdvertisement) {
            ((AdMobAdvertisement) mAdvertisement).initAds(this::onLoadAds);
            return;
        }
        onLoadAds();
    }

    public void onLoadAds() {
        setUpLayoutBanner();
        if (mAdvertisement != null) {
            mAdvertisement.setUpLoopInterstitial();
        }
    }

    public void onDoWhenNetworkOff() {
    }

    @Override
    protected void onDestroy() {
        if (mYPYCastManager != null) {
            mYPYCastManager.onCastDestroy();
            mYPYCastManager = null;
        }
        if (appOpenAdsManager != null) {
            appOpenAdsManager.onDestroy();
        }
        super.onDestroy();
        if (mApplicationBroadcast != null) {
            unregisterReceiver(mApplicationBroadcast);
            mApplicationBroadcast = null;
        }

    }

    public void goToUrl(String name, String url) {
        Intent mIntent = new Intent(this, ShowUrlActivity.class);
        mIntent.putExtra(KEY_HEADER, name);
        mIntent.putExtra(KEY_SHOW_URL, url);
        startActivity(mIntent);
    }


    public void updateFavorite(@NonNull RadioModel model, int type, boolean isFav) {
        boolean isSignedIn = XRadioSettingManager.isSignedIn(this);
        boolean isPodCast = model.isPodCast();
        if (type == TYPE_TAB_FAVORITE && !isPodCast && !isFav && model.isUploaded()) {
            updateToLocalDevice(model, false, false);
            Observable<ResultModel<AbstractModel>> favObservable = RetroRadioNetUtils.updateCount(this, model.getId(), TYPE_COUNT_FAV, -1);
            addObservableToObserverInBackground(favObservable, resultModel -> {
                if (!checkUserResultError(resultModel) && isSignedIn) {
                    showToast(R.string.info_remove_all_fav_successfully);
                }
            }, null);
            return;
        }
        if (!isPodCast && ApplicationUtils.isOnline(this)) {
            int value = isFav ? 1 : -1;
            Observable<ResultModel<AbstractModel>> favObservable = RetroRadioNetUtils.updateCount(this, model.getId(), TYPE_COUNT_FAV, value);
            if (isSignedIn) {
                showProgressDialog();
            }
            addObservableToObserverInBackground(favObservable, resultModel -> {
                dismissProgressDialog();
                if (resultModel != null) {
                    model.setUploaded(isSignedIn && resultModel.isResultOk());
                    if (checkUserResultError(resultModel)) {
                        return;
                    }
                }
                updateToLocalDevice(model, isFav, isSignedIn);
            }, () -> {
                dismissProgressDialog();
                model.setUploaded(false);
                updateToLocalDevice(model, isFav, false);
            });
        } else {
            updateToLocalDevice(model, isFav, false);
        }
    }

    public boolean checkUserResultError(ResultModel<?> resultModel) {
        try {
            if (resultModel != null) {
                int status = resultModel.getStatus();
                if (status == STATUS_BANNED_ACCOUNT || status == STATUS_INVALID_ACCOUNT) {

                    //sign out google account
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                    GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                    mGoogleSignInClient.signOut();

                    //show Toast
                    int msgId = status == STATUS_BANNED_ACCOUNT ? R.string.info_banned_account : R.string.info_invalid_account;
                    XRadioSettingManager.logOut(this);
                    showToast(msgId);
                    goToLogin();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateToLocalDevice(@NonNull RadioModel model, boolean isFav, boolean isUploaded) {
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            if (!isFav) {
                boolean b = mTotalMng.removeModelToCache(TYPE_TAB_FAVORITE, model);
                if (b) {
                    model.setFavorite(false);
                    notifyFavorite(model.getId(), false);
                }
            } else {
                RadioModel mObject = model.cloneObject();
                if (mObject != null) {
                    mObject.setFavorite(true);
                    mTotalMng.addModelToCache(TYPE_TAB_FAVORITE, mObject);
                    model.setFavorite(true);
                    notifyFavorite(model.getId(), true);
                }
            }
            if (isUploaded) {
                runOnUiThread(() -> showToast(isFav ? R.string.info_added_all_fav_successfully : R.string.info_remove_all_fav_successfully));
            }
        });

    }

    public void notifyFavorite(long id, boolean isFav) {
        if (mListFragments != null && mListFragments.size() > 0) {
            for (Fragment mFragment : mListFragments) {
                if (mFragment instanceof XRadioListFragment) {
                    ((XRadioListFragment<?>) mFragment).notifyFavorite(id, isFav);
                }
            }
        }
    }


    public void shareContent(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            String app = String.format(getString(R.string.info_content_share), getString(R.string.app_name), String.format(URL_FORMAT_LINK_APP, getPackageName()));
            ShareActionUtils.shareInfo(this, msg + "\n" + app);
        }
    }

    public void shareRadioModel(@NonNull RadioModel radioModel) {
        String path = radioModel.getPath();
        if (!TextUtils.isEmpty(path) && !radioModel.isPodCast()) {
            shareFile(path);
            return;
        }
        String msg = radioModel.getShareStr();
        shareContent(msg);
    }

    public void shareFile(String path) {
        try {
            Uri uri = null;
            if (!path.startsWith(PREFIX_CONTENT)) {
                File mFile = new File(path);
                if (mFile.exists() && mFile.isFile()) {
                    if (!IOUtils.isLollipop()) {
                        uri = Uri.fromFile(mFile);
                    } else {
                        uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", mFile);
                    }
                }
            } else {
                uri = Uri.parse(path);
            }
            if (uri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("*/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.title_share)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startMusicService(String action) {
        try {
            Intent mIntent1 = new Intent(this, XRadioAudioService.class);
            mIntent1.setAction(getPackageName() + action);
            startService(mIntent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startMusicService(String action, int value) {
        try {
            Intent mIntent1 = new Intent(this, XRadioAudioService.class);
            mIntent1.setAction(getPackageName() + action);
            if (value != 0) {
                mIntent1.putExtra(KEY_VALUE, value);
            }
            startService(mIntent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialogSleepMode() {
        boolean isDark = XRadioSettingManager.isDarkMode(this);
        DialogSleepTimeBinding viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_sleep_time, null, false);
        int timer = XRadioSettingManager.getSleepMode(this);
        String timerStr = timer > 0 ? getString(R.string.format_minutes) + timer : getString(R.string.title_off);
        viewBinding.tvInfo.setText(timerStr);

        viewBinding.seekSleep.setProgressColor(getResources().getColor(isDark ? R.color.dark_color_accent : R.color.light_color_accent));
        viewBinding.seekSleep.setArcColor(getResources().getColor(isDark ? R.color.dark_text_second_color : R.color.dialog_color_secondary_text));
        viewBinding.seekSleep.setMax((MAX_SLEEP_MODE - MIN_SLEEP_MODE) / STEP_SLEEP_MODE + 1);
        viewBinding.seekSleep.setProgressWidth(getResources().getDimensionPixelOffset(R.dimen.tiny_margin));
        viewBinding.seekSleep.setProgress(XRadioSettingManager.getSleepMode(this) / STEP_SLEEP_MODE);
        viewBinding.seekSleep.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                int timer = progress * STEP_SLEEP_MODE;
                XRadioSettingManager.setSleepMode(RadioFragmentActivity.this, timer);
                String timerStr = timer > 0 ? getString(R.string.format_minutes) + timer : getString(R.string.title_off);
                viewBinding.tvInfo.setText(timerStr);
                if (YPYStreamManager.getInstance().isPrepareDone()) {
                    startMusicService(ACTION_UPDATE_SLEEP_MODE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });
        MaterialDialog.Builder mBuilder = createBasicDialogBuilder(R.string.title_sleep_mode, R.string.title_done, 0);
        if (isDark) {
            viewBinding.tvInfo.setTextColor(getResources().getColor(R.color.dark_text_main_color));
        }
        mBuilder.customView(viewBinding.getRoot(), false);
        final MaterialDialog mDialog = mBuilder.build();
        mDialog.show();
    }

    public boolean isHavingListStream() {
        return YPYStreamManager.getInstance().isHavingList();
    }

    public void goToPremium() {
        if (isHavingListStream()) {
            startMusicService(ACTION_STOP);
        }
        Intent mIntent = new Intent(this, UpgradePremiumActivity.class);
        startActivity(mIntent);
        finish();
    }

    public boolean isPremiumMember() {
        return MemberShipManager.isIAPremiumMember(this);
    }

    void removeNativeAdsModel(ArrayList<RadioModel> mListTracks) {
        try {
            if (mListTracks != null && mListTracks.size() > 0) {
                Iterator<RadioModel> mIterator = mListTracks.iterator();
                while (mIterator.hasNext()) {
                    AbstractModel model = mIterator.next();
                    if (model.isShowAds()) {
                        mIterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void registerApplicationBroadcastReceiver(Context mContext) {
        if (mApplicationBroadcast != null) {
            return;
        }
        mApplicationBroadcast = new ApplicationBroadcast();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(getPackageName() + ACTION_BROADCAST_PLAYER);
        if (IOUtils.isAndroid13()) {
            // Android 13+ requires RECEIVER_EXPORTED or RECEIVER_NOT_EXPORTED
            mContext.registerReceiver(mApplicationBroadcast, mIntentFilter, Context.RECEIVER_EXPORTED);
        } else {
            // Older Android versions
            mContext.registerReceiver(mApplicationBroadcast, mIntentFilter);
        }
    }


    private class ApplicationBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent != null) {
                    String action = intent.getAction();
                    if (action != null && !TextUtils.isEmpty(action)) {
                        String packageName = getPackageName();
                        if (action.equals(packageName + ACTION_BROADCAST_PLAYER)) {
                            String actionPlay = intent.getStringExtra(KEY_ACTION);
                            if (actionPlay != null && !TextUtils.isEmpty(actionPlay)) {
                                if (actionPlay.equalsIgnoreCase(ACTION_UPDATE_COVER_ART)) {
                                    String value = intent.getStringExtra(KEY_VALUE);
                                    processUpdateImage(value);

                                } else {
                                    long value = intent.getLongExtra(KEY_VALUE, -1);
                                    processBroadcast(actionPlay, value);
                                }

                            }
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void processUpdateImage(String value) {

    }

    public void processBroadcast(String actionPlay, long value) {

    }

    public <V> void addObservableToObserver(Observable<ResultModel<V>> mObservable, IYPYResultModelCallback<V> mCallback) {
        addObservableToObserver(mObservable, null, mCallback);
    }

    public <V> void addObservableToObserver(Observable<ResultModel<V>> mObservable, IYPYCallback mCallbackSuccess, IYPYResultModelCallback<V> mModelCallbackSuccess) {
        if (mYPYRXModel == null) return;
        showProgressDialog(R.string.info_loading);
        mYPYRXModel.addObservableToObserver(mObservable, new DisposableObserver<ResultModel<V>>() {
            @Override
            public void onNext(@NonNull ResultModel<V> mResultObject) {
                checkResult(mResultObject, mCallbackSuccess, mModelCallbackSuccess);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                showToast(R.string.info_server_error);
                dismissProgressDialog();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public <V> void checkResult(ResultModel<V> resultModel, final IYPYCallback mCallback, final IYPYResultModelCallback<V> mCallbackModel) {
        try {
            dismissProgressDialog();
            if (resultModel == null) {
                showToast(R.string.info_server_error);
                return;
            }
            if (resultModel.isResultOk()) {
                if (mCallback != null) {
                    mCallback.onAction();
                } else {
                    if (mCallbackModel != null) {
                        mCallbackModel.onAction(resultModel);
                    }
                }
                return;
            }
            checkShowErrorMessage(resultModel, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <V> void addObservableToObserverWithCheckAll(Observable<ResultModel<V>> mObservable, IYPYResultModelCallback<V> mModelCallback, IYPYCallback mCallbackError) {
        if (mYPYRXModel == null) return;
        showProgressDialog(R.string.info_loading);
        mYPYRXModel.addObservableToObserver(mObservable, new DisposableObserver<ResultModel<V>>() {
            @Override
            public void onNext(@NonNull ResultModel<V> mResultObject) {
                dismissProgressDialog();
                if (mModelCallback != null) {
                    mModelCallback.onAction(mResultObject);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                showToast(R.string.info_server_error);
                dismissProgressDialog();
                if (mCallbackError != null) {
                    mCallbackError.onAction();
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void checkShowErrorMessage(ResultModel<?> resultModel, boolean isNeedReset, IYPYCallback mResetCallback) {
        try {
            int status = resultModel.getStatus();
            int resId;
            boolean isCheckReset = false;
            if (status == STATUS_BANNED_ACCOUNT) {
                resId = R.string.info_banned_account;
                isCheckReset = true;
            } else if (status == STATUS_INVALID_ACCOUNT) {
                resId = R.string.info_invalid_account;
                isCheckReset = true;
            } else if (status == STATUS_WRONG_USER_PASS) {
                resId = R.string.info_wrong_user_pass;
            } else {
                resId = R.string.info_invalid_param;
            }
            showToast(resId);
            YPYLog.e("DCM", "====>isNeedReset=" + isNeedReset + "==>isCheckReset=" + isCheckReset);
            if (isNeedReset && isCheckReset) {
                XRadioSettingManager.logOut(this);
                if (mResetCallback != null) {
                    mResetCallback.onAction();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <V> void addObservableToObserverInBackground(Observable<ResultModel<V>> mObservable, final IYPYResultModelCallback<V> mCallback, IYPYCallback mCallbackError) {
        if (mYPYRXModel == null) return;
        mYPYRXModel.addObservableToObserver(mObservable, new DisposableObserver<ResultModel<V>>() {
            @Override
            public void onNext(@NonNull ResultModel<V> mResultObject) {
                if (mCallback != null) {
                    mCallback.onAction(mResultObject);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                showToast(R.string.info_server_error);
                if (mCallbackError != null) {
                    mCallbackError.onAction();
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void goToLogin() {
        Intent mIntent = new Intent(this, SignInActivity.class);
        startActivity(mIntent);
        finish();
    }

    public void processCastRadio(RadioModel model, int id) {
        try {
            if (ApplicationUtils.isOnline(this)) {
                YPYQueueDataProvider.getInstance(this).removeAll();
                showProgressDialog();
                YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
                    MediaInfo mediaInfo = model.getMediaInfo(this);
                    runOnUiThread(() -> {
                        try {
                            dismissProgressDialog();
                            if (mediaInfo == null) {
                                showToast(R.string.info_error_queue);
                                return;
                            }
                            MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo)
                                    .setAutoplay(true)
                                    .setPreloadTime(PRELOAD_TIME_S)
                                    .build();
                            processMediaItemQueue(id, queueItem);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                });
            } else {
                showToast(R.string.info_connection_lost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processMediaItemQueue(int id, MediaQueueItem queueItem) {
        try {
            YPYQueueDataProvider provider = YPYQueueDataProvider.getInstance(this);
            final RemoteMediaClient remoteMediaClient = mYPYCastManager.getRemoteMediaClient();
            if (provider.isQueueDetached() && provider.getCount() > 0) {
                if (id == ACTION_PLAY_NOW || id == ACTION_ADD_QUEUE) {
                    MediaQueueItem[] items = YPYQueueUtils.rebuildQueueAndAppend(provider.getItems(), queueItem);
                    remoteMediaClient.queueLoad(items, provider.getCount(), MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
                }
            } else {
                MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};
                if (provider.getCount() == 0) {
                    remoteMediaClient.queueLoad(newItemArray, 0, MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
                } else {
                    int currentId = provider.getCurrentItemId();
                    if (id == ACTION_PLAY_NOW) {
                        remoteMediaClient.queueInsertAndPlayItem(queueItem, currentId, null);
                    } else if (id == ACTION_PLAY_NEXT) {
                        int currentPosition = provider.getPositionByItemId(currentId);
                        if (currentPosition == provider.getCount() - 1) {
                            remoteMediaClient.queueAppendItem(queueItem, null);
                        } else {
                            int nextItemId = provider.getItem(currentPosition + 1).getItemId();
                            remoteMediaClient.queueInsertItems(newItemArray, nextItemId, null);
                        }
                        showToast(R.string.info_item_added_to_play_next);

                    } else if (id == ACTION_ADD_QUEUE) {
                        remoteMediaClient.queueAppendItem(queueItem, null);
                        showToast(R.string.info_item_added_to_queue);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showDialogReport(@NonNull RadioModel radioModel) {
        MaterialDialog.Builder mDialogBuilder = createBasicDialogBuilder(R.string.title_report_radio, R.string.title_cancel, 0);
        mDialogBuilder.items(R.array.array_reports);
        mDialogBuilder.autoDismiss(true);
        mDialogBuilder.itemsCallback((dialog, itemView, position, text) -> {
            dialog.dismiss();
            if (position == 0) {
                startReportRadiosToAdmin(radioModel);
            } else {
                startReportRadiosViaEmail(radioModel);
            }
        });
        mDialogBuilder.show();
    }

    private void startReportRadiosViaEmail(@NonNull RadioModel radioModel) {
        String subject = getString(R.string.title_report_radio) + " - " + getString(R.string.app_name);
        String msg = String.format(getString(R.string.format_report), radioModel.getName());
        ShareActionUtils.shareViaEmail(this, YOUR_CONTACT_EMAIL, subject, msg);
    }


    private void startReportRadiosToAdmin(@NonNull RadioModel radioModel) {
        if (!XRadioSettingManager.isSignedIn(this)) {
            goToLogin();
            return;
        }
        if (!ApplicationUtils.isOnline(this)) {
            showToast(R.string.info_lose_internet);
            return;
        }
        Observable<ResultModel<AbstractModel>> reportObservable = RetroRadioNetUtils.updateReport(this, radioModel.getId());
        addObservableToObserver(reportObservable, resultModel -> {
            boolean isSuccess = resultModel != null && resultModel.isResultOk();
            showToast(isSuccess ? R.string.info_report_success : R.string.info_report_error);
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            if (ApplicationUtils.isOnline(this) && isHavingListStream()) {
                startMusicService(ACTION_NEXT);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            if (ApplicationUtils.isOnline(this) && isHavingListStream()) {
                startMusicService(ACTION_PREVIOUS);
                return true;
            }

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (ApplicationUtils.isOnline(this) && isHavingListStream()) {
                if (YPYStreamManager.getInstance().isPlaying()) {
                    startMusicService(ACTION_TOGGLE_PLAYBACK);
                    return true;
                }
            }

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (ApplicationUtils.isOnline(this) && isHavingListStream()) {
                if (YPYStreamManager.getInstance().isPrepareDone() &&
                        !YPYStreamManager.getInstance().isPlaying()) {
                    startMusicService(ACTION_TOGGLE_PLAYBACK);
                    return true;
                }
            }

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            if (ApplicationUtils.isOnline(this) && isHavingListStream()) {
                startMusicService(ACTION_TOGGLE_PLAYBACK);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public void goToEqualizer() {
        Intent mIntent = new Intent(this, EqualizerActivity.class);
        startActivity(mIntent);
    }

    public void showModeInterstitial(int countInterstitial, long freqAds, IYPYCallback mCallback) {
        if (SHOW_ADS && freqAds > 0) {
            if (mAdvertisement != null && countInterstitial % freqAds == 2) {
                mAdvertisement.showLoopInterstitialAd(mCallback);
                return;
            }
        }
        if (mCallback != null) {
            mCallback.onAction();
        }
    }

    public void showPermissionDownloadDialog() {
        boolean isDark = XRadioSettingManager.isDarkMode(this);
        int color = ContextCompat.getColor(this, isDark ? R.color.dark_text_main_color : R.color.dialog_color_text);

        DialogStoragePermissionBinding viewBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.dialog_storage_permission, null, false);
        viewBinding.tvInfo.setTextColor(color);
        viewBinding.cbDontAsk.setTextColor(color);

        MaterialDialog.Builder mBuilder = createBasicDialogBuilder(R.string.title_confirm, R.string.title_agree, R.string.title_cancel);
        mBuilder.canceledOnTouchOutside(false);
        mBuilder.titleGravity(GravityEnum.CENTER);
        mBuilder.customView(viewBinding.getRoot(), true);
        if (isSupportRTL()) {
            viewBinding.tvInfo.setGravity(Gravity.END);
        }
        mBuilder.onPositive((dialog, which) -> {
            XRadioSettingManager.setDontAskAgainDownload(this, viewBinding.cbDontAsk.isChecked());
            startGrantSDCardPermission(REQUEST_PERMISSION_DOWNLOAD);
        });
        mBuilder.onNegative((dialog, which) -> XRadioSettingManager.setDontAskAgainDownload(this, viewBinding.cbDontAsk.isChecked()));
        mBuilder.keyListener((dialogInterface, i, keyEvent) -> i == KeyEvent.KEYCODE_BACK);
        mBuilder.show();
    }

    @SuppressLint("NewApi")
    public boolean checkStoragePermissions() {
        if (IOUtils.isAndroid14())
            return ApplicationUtils.isGrantAllPermission(this, LIST_STORAGE_PERMISSIONS_14);
        if (IOUtils.isAndroid13())
            return ApplicationUtils.isGrantAllPermission(this, LIST_STORAGE_PERMISSIONS_13);
        return ApplicationUtils.isGrantAllPermission(this, LIST_STORAGE_PERMISSIONS);
    }


    @SuppressLint("NewApi")
    public void startGrantSDCardPermission(int requestCode) {
        if (IOUtils.isAndroid14() && !ApplicationUtils.isGrantAllPermission(this, LIST_STORAGE_PERMISSIONS_14)) {
            this.isStartCheckRecord = requestCode == REQUEST_PERMISSION_RECORD;
            ActivityCompat.requestPermissions(this, LIST_STORAGE_PERMISSIONS_14, requestCode);
        } else if (IOUtils.isAndroid13() && !ApplicationUtils.isGrantAllPermission(this, LIST_STORAGE_PERMISSIONS_13)) {
            this.isStartCheckRecord = requestCode == REQUEST_PERMISSION_RECORD;
            ActivityCompat.requestPermissions(this, LIST_STORAGE_PERMISSIONS_13, requestCode);
        } else if (IOUtils.isMarshmallow() && !ApplicationUtils.isGrantAllPermission(this, LIST_STORAGE_PERMISSIONS)) {
            this.isStartCheckRecord = requestCode == REQUEST_PERMISSION_RECORD;
            ActivityCompat.requestPermissions(this, LIST_STORAGE_PERMISSIONS, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_RECORD) {
            if (ApplicationUtils.isGrantAllPermission(grantResults)) {
                if (isStartCheckRecord) {
                    isStartCheckRecord = false;
                    startMusicService(ACTION_RECORD_START);
                }
            } else {
                showToast(R.string.info_permission_denied);
                isStartCheckRecord = false;
            }
        }
    }


}
