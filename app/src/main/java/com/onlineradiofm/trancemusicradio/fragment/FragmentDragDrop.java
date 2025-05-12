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

import static com.onlineradiofm.trancemusicradio.RadioFragmentActivity.REQUEST_PERMISSION_RECORD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.databinding.FragmentDragDropBinding;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.fragment.YPYFragment;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;
import com.onlineradiofm.trancemusicradio.ypylibs.music.constant.IYPYStreamConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.music.mediaplayer.YPYMediaPlayer;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.StringUtils;
import com.wicaodian.widget.IndicatorSeekBar;
import com.wicaodian.widget.OnSeekChangeListener;
import com.wicaodian.widget.SeekParams;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class FragmentDragDrop extends YPYFragment<FragmentDragDropBinding> implements IRadioConstants, IYPYStreamConstants, View.OnClickListener {

    private MainActivity mContext;
    private long timeRecord;
    private final Handler mHandler = new Handler();
    private AudioManager mAudioManager;

    private int resDefault = R.drawable.ic_light_play_default;
    private RoundedCornersTransformation cornersTransformation;
    private ColorStateList downloadedStateList;
    private ColorStateList downloadLightStateList;
    private ColorStateList downloadDarkStateList;

    @NonNull
    @Override
    protected FragmentDragDropBinding getViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentDragDropBinding.inflate(inflater, container, false);
    }

    @Override
    public void findView() {
        this.mContext = (MainActivity) requireActivity();
        int floatDimen = mContext.getResources().getDimensionPixelOffset(R.dimen.size_img_tiny);
        this.cornersTransformation = new RoundedCornersTransformation(floatDimen, 0);

        downloadedStateList = ContextCompat.getColorStateList(mContext, R.color.checked_download);
        downloadLightStateList = ContextCompat.getColorStateList(mContext, R.color.light_play_color_text);
        downloadDarkStateList = ContextCompat.getColorStateList(mContext, R.color.dark_play_color_text);

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        viewBinding.layoutSeekbarPodcast.seekBarPodcast.setOnSeekChangeListener(new OnSeekChangeListener() {

            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                if (YPYStreamManager.getInstance().isPrepareDone()) {
                    RadioModel currentModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                    if (currentModel != null) {
                        int currentPos = (int) (viewBinding.layoutSeekbarPodcast.seekBarPodcast.getProgressFloat() * currentModel.getDuration() / 100f);
                        mContext.startMusicService(ACTION_UPDATE_POS, currentPos);
                    }
                }
            }
        });
        viewBinding.layoutSeekbarVolume.seekBarVolume.setOnSeekChangeListener(new OnSeekChangeListener() {
            private boolean isFromUser;

            @Override
            public void onSeeking(SeekParams seekParams) {
                isFromUser = seekParams.fromUser;
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                if (isFromUser) {
                    isFromUser = false;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
                }
            }
        });
        updateVolume();
        updateInfo();

        viewBinding.layoutTotalControl.btnFavorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                RadioModel model = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                mContext.updateFavorite(model, 0, true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                RadioModel model = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                mContext.updateFavorite(model, 0, false);
            }
        });
        updateDarkMode(XRadioSettingManager.isDarkMode(mContext));
        setUpClick();
        updateStatePlay();

        boolean isSupportRTL = ApplicationUtils.isSupportRTL();
        if (isSupportRTL) {
            onUpdateUIWhenSupportRTL();
        }

    }

    private void updateStatePlay() {
        boolean isLoading = YPYStreamManager.getInstance().isLoading();
        showLoading(isLoading);
        if (!isLoading) {
            updateStatusPlayer(YPYStreamManager.getInstance().isPlaying());
            YPYMediaPlayer.StreamInfo mStrInfo = YPYStreamManager.getInstance().getStreamInfo();
            updateImage(mStrInfo != null ? mStrInfo.imgUrl : null);
        }
    }

    private void setUpClick() {
        viewBinding.btnClose.setOnClickListener(this);
        viewBinding.btnMenu.setOnClickListener(this);
        viewBinding.layoutTotalControl.btnNext.setOnClickListener(this);
        viewBinding.layoutTotalControl.btnPrev.setOnClickListener(this);
        viewBinding.layoutTotalControl.btnRecord.setOnClickListener(this);
        viewBinding.layoutTotalControl.btnReplay.setOnClickListener(this);
        viewBinding.layoutTotalControl.btnForward.setOnClickListener(this);
        viewBinding.layoutTotalControl.fbPlay.setOnClickListener(this);
        viewBinding.layoutTotalControl.btnDownload.setOnClickListener(this);
        viewBinding.layoutInfoPlay.btnEqualizer.setOnClickListener(this);
        viewBinding.layoutInfoPlay.btnSleep.setOnClickListener(this);

    }

    public void showLoading(boolean isShow) {
        try {
            if (mContext != null) {
                RadioModel currentModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                if (currentModel != null && (currentModel.isPodCast() || currentModel.isOfflineModel())) {
                    viewBinding.layoutSeekbarPodcast.seekBarPodcast.setProgress(0);
                    viewBinding.layoutSeekbarPodcast.tvCurrentTime.setText(mContext.getString(R.string.title_empty_duration));
                    viewBinding.layoutSeekbarPodcast.tvDuration.setText(mContext.getString(R.string.title_empty_duration));
                    viewBinding.layoutSeekbarPodcast.getRoot().setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
                }
                if (isShow) {
                    viewBinding.layoutTotalControl.layoutControl.setVisibility(View.INVISIBLE);
                    // viewBinding.layoutTotalControl.playProgressBar.setVisibility(View.VISIBLE);
                    // viewBinding.layoutTotalControl.playProgressBar.show();
                } else {
                    // if (viewBinding.layoutTotalControl.playProgressBar.getVisibility() == View.VISIBLE) {
                    //    viewBinding.layoutTotalControl.playProgressBar.hide();
                    //    viewBinding.layoutTotalControl.playProgressBar.setVisibility(View.INVISIBLE);
                    // }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateStatusPlayer(boolean isPlaying) {
        if (mContext != null) {
            viewBinding.layoutTotalControl.layoutControl.setVisibility(View.VISIBLE);
            viewBinding.layoutTotalControl.fbPlay.setImageResource(isPlaying ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_arrow_white_36dp);
        }
    }

    public void updateInfoWhenComplete() {
        try {
            if (mContext != null) {
                RadioModel mRadioModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                if (mRadioModel != null) {
                    this.viewBinding.layoutInfoPlay.tvDragSinger.setText(R.string.info_radio_ended_title);
                    this.viewBinding.layoutInfoPlay.tvDragSinger.setText(ApplicationUtils.isOnline(mContext) ? R.string.info_radio_ended_sub : R.string.info_connection_lost);
                    this.updateFullTimer();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateInfo() {
        try {
            RadioModel mRadioModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
            if (mContext != null && mRadioModel != null) {
                boolean isShowSeekBar = mRadioModel.isPodCast() || mRadioModel.isOfflineModel();
                boolean isFavDisable = mRadioModel.isOfflineFile() && !mRadioModel.isPodCast();

                this.viewBinding.layoutTotalControl.layoutRecord.setVisibility(mRadioModel.isOfflineModel() ? View.INVISIBLE : View.VISIBLE);
                this.viewBinding.layoutTotalControl.layoutReplay.setVisibility(isShowSeekBar ? View.VISIBLE : View.INVISIBLE);
                this.viewBinding.layoutTotalControl.layoutForward.setVisibility(isShowSeekBar ? View.VISIBLE : View.INVISIBLE);
                this.viewBinding.layoutRippleMenu.setVisibility(isFavDisable ? View.INVISIBLE : View.VISIBLE);

                this.viewBinding.layoutSeekbarPodcast.getRoot().setVisibility(isShowSeekBar ? View.VISIBLE : View.GONE);
                this.viewBinding.layoutSeekbarVolume.getRoot().setVisibility(isShowSeekBar ? View.GONE : View.VISIBLE);

                this.viewBinding.layoutSeekbarPodcast.tvCurrentTime.setText(R.string.title_empty_duration);
                this.viewBinding.layoutSeekbarPodcast.tvDuration.setText(R.string.title_empty_duration);

                YPYMediaPlayer.StreamInfo mStreamInfo = YPYStreamManager.getInstance().getStreamInfo();
                String title = mStreamInfo != null && !TextUtils.isEmpty(mStreamInfo.title) ? mStreamInfo.title : mRadioModel.getName();
                String singer = mStreamInfo != null && !TextUtils.isEmpty(mStreamInfo.artist) ? mStreamInfo.artist : mRadioModel.getTags();

                this.viewBinding.stationName.setText(mRadioModel.getName());

                this.viewBinding.layoutInfoPlay.tvDragSong.setText(title);
                this.viewBinding.layoutInfoPlay.tvDragSong.setSelected(true);

                this.viewBinding.layoutInfoPlay.tvDragSinger.setSelected(true);
                this.viewBinding.layoutInfoPlay.tvDragSinger.setText(singer);

                viewBinding.layoutTotalControl.btnFavorite.setVisibility(isFavDisable ? View.INVISIBLE : View.VISIBLE);
                viewBinding.layoutTotalControl.btnFavorite.setLiked(mRadioModel.isFavorite());
                updateCurrentDownload();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentDownload() {
        try {
            RadioModel mRadioModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
            if (mRadioModel == null || viewBinding == null || mContext == null) {
                return;
            }
            viewBinding.layoutTotalControl.layoutDownload.setVisibility(mRadioModel.canDownload() ? View.VISIBLE : View.GONE);
            if (mRadioModel.canDownload()) {
                boolean isFileDownloaded = mContext.mTotalMng.isFileDownloaded(mContext, mRadioModel);
                viewBinding.layoutTotalControl.btnDownload.setImageResource(isFileDownloaded ? R.drawable.ic_download_checked_36dp : R.drawable.ic_download_36dp);
                viewBinding.layoutTotalControl.layoutDownload.setEnabled(!isFileDownloaded);
                if (isFileDownloaded) {
                    ImageViewCompat.setImageTintList(viewBinding.layoutTotalControl.btnDownload, downloadedStateList);
                } else {
                    boolean isDark = XRadioSettingManager.isDarkMode(mContext);
                    ImageViewCompat.setImageTintList(viewBinding.layoutTotalControl.btnDownload, isDark ? downloadDarkStateList : downloadLightStateList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void updateVolume() {
        try {
            AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (mgr != null) {
                int values = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                viewBinding.layoutSeekbarVolume.seekBarVolume.setMax(maxVolume);
                viewBinding.layoutSeekbarVolume.seekBarVolume.setProgress(values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeVolume(int count) {
        try {
            int values = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            values = values + count;
            if (values >= maxVolume) {
                values = maxVolume;
            }
            if (values < 0) {
                values = 0;
            }
            viewBinding.layoutSeekbarVolume.seekBarVolume.setProgress(values);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, values, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_close) {
            mContext.collapseListenMusic();
        } else if (id == R.id.btn_download) {
            RadioModel downloadModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
            if (downloadModel != null) {
                if (!mContext.isPremiumMember()) {
                    mContext.askWatchAddOrSubcriptionPopUp(mContext, downloadModel);
                } else  {
                mContext.downloadManager.startDownloadFile(downloadModel);
                }
            }
        } else if (id == R.id.btnMenu) {
            RadioModel downloadModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
            if (downloadModel != null) {
                mContext.showPopUpMenu(viewBinding.btnMenu, downloadModel);
            }
        } else if (id == R.id.btnEqualizer) {
            mContext.goToEqualizer();
        } else if (id == R.id.btnSleep) {
            mContext.showDialogSleepMode();
        } else if (id == R.id.btn_next) {
            if (mContext.checkActionBeforePlay(true)) {
                return;
            }
            mContext.startMusicService(ACTION_NEXT);
        } else if (id == R.id.btn_prev) {
            if (mContext.checkActionBeforePlay(true)) {
                return;
            }
            mContext.startMusicService(ACTION_PREVIOUS);
        } else if (id == R.id.btn_record) {
            if (mContext.checkActionBeforePlay(false)) {
                return;
            }
            boolean isRecord = YPYStreamManager.getInstance().isRecordingFile();
            if (!isRecord) {
                if (IOUtils.isAndroid14()) {
                    // request write access using MediaStore.createWriteRequest() (for Android 14+)
                    mContext.requestMediaStoreWriteAccess();
                    if (!ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS_14))
                    {
                        mContext.startGrantSDCardPermission(REQUEST_PERMISSION_RECORD);
                        return;
                    }
                } else if (IOUtils.isAndroid13()) {
                    // request write access using MediaStore.createWriteRequest() (for Android 13+)
                    mContext.requestMediaStoreWriteAccess();
                    if (!ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS_13))
                        {
                            mContext.startGrantSDCardPermission(REQUEST_PERMISSION_RECORD);
                            return;
                        }
                    } else if (!ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS)) {
                        mContext.startGrantSDCardPermission(REQUEST_PERMISSION_RECORD);
                        return;
                    }
                    mContext.startMusicService(ACTION_RECORD_START);
                } else {
                    mContext.startMusicService(ACTION_RECORD_STOP);
                }
            } else if (id == R.id.btn_replay) {
                if (mContext.isAllCheckNetWorkOff && !ApplicationUtils.isOnline(mContext)) {
                    mContext.showToast(R.string.info_connect_to_play);
                    return;
                }
                mContext.startMusicService(ACTION_UPDATE_FAST, -1);
            } else if (id == R.id.btn_forward) {
                if (mContext.isAllCheckNetWorkOff && !ApplicationUtils.isOnline(mContext)) {
                    mContext.showToast(R.string.info_connect_to_play);
                    return;
                }
                mContext.startMusicService(ACTION_UPDATE_FAST, 1);
            } else if (id == R.id.fb_play) {
                if (mContext.checkActionBeforePlay(false)) {
                    return;
                }
                boolean isPrepareDone = YPYStreamManager.getInstance().isPrepareDone();
                if (!isPrepareDone) {
                    mContext.startMusicService(ACTION_PLAY);
                    return;
                }
                mContext.startMusicService(ACTION_TOGGLE_PLAYBACK);
            }
        }

        public void updateImage (String url){
            GlideImageLoader.displayImage(mContext, viewBinding.imgPlaySong, url, cornersTransformation, resDefault);
        }

        public void notifyFavorite ( long trackId, boolean isFav){
            try {
                if (mContext != null) {
                    RadioModel mRadioModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                    if (mRadioModel != null) {
                        if (mRadioModel.getId() == trackId) {
                            mRadioModel.setFavorite(isFav);
                            viewBinding.layoutTotalControl.btnFavorite.setLiked(isFav);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void updateSleepMode ( long value){
            viewBinding.tvSleepTimer.setVisibility(value > 0 ? View.VISIBLE : View.INVISIBLE);
            viewBinding.tvSleepTimer.setText(value > 0 ? StringUtils.getStringTimer(value) : getString(R.string.empty_duration));
        }

        private void onUpdateUIWhenSupportRTL () {
            viewBinding.layoutTotalControl.btnNext.setImageResource(R.drawable.ic_skip_previous_white_36dp);
            viewBinding.layoutTotalControl.btnPrev.setImageResource(R.drawable.ic_skip_next_white_36dp);
            viewBinding.layoutSeekbarVolume.seekBarVolume.setScaleX(-1f);
            viewBinding.layoutSeekbarPodcast.seekBarPodcast.setScaleX(-1f);
            viewBinding.layoutSeekbarVolume.imgVolumeMax.setScaleX(-1f);
            viewBinding.layoutSeekbarVolume.imgVolumeOff.setScaleX(-1f);
        }

        public void updateStateRecord ( boolean isRecord){
            try {
                this.viewBinding.tvInfoRecord.setVisibility(isRecord ? View.VISIBLE : View.INVISIBLE);
                this.viewBinding.layoutTotalControl.btnRecord.setImageResource(isRecord ? R.drawable.ic_stop_record_white_36dp : R.drawable.ic_record_white_36dp);
                if (isRecord) {
                    timeRecord = 0;
                    mHandler.removeCallbacksAndMessages(null);
                    startTimeRecord();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void startTimeRecord () {
            try {
                if (YPYStreamManager.getInstance().isRecordingFile()) {
                    this.timeRecord = timeRecord + 1000;
                    String format = mContext.getString(R.string.format_recording_files);
                    this.viewBinding.tvInfoRecord.setText(String.format(format, StringUtils.getStringTimer(timeRecord)));
                    this.mHandler.postDelayed(this::startTimeRecord, 1000);
                } else {
                    this.timeRecord = 0;
                    this.mHandler.removeCallbacksAndMessages(null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void updateTimer ( long current){
            try {
                RadioModel model = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                if (model != null && current > 0 && model.getDuration() > 0 && (model.isPodCast() || model.isOfflineModel())) {
                    this.viewBinding.layoutSeekbarPodcast.tvCurrentTime.setText(StringUtils.getStringTimer(current));
                    this.viewBinding.layoutSeekbarPodcast.tvDuration.setText(StringUtils.getStringTimer(model.getDuration()));
                    int percent = (int) (((float) current / (float) model.getDuration()) * 100f);
                    viewBinding.layoutSeekbarPodcast.seekBarPodcast.setProgress(percent);
                    viewBinding.layoutSeekbarPodcast.seekBarPodcast.invalidate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void updateFullTimer () {
            try {
                RadioModel model = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                if (model != null && model.getDuration() > 0 && (model.isPodCast() || model.isOfflineModel())) {
                    this.viewBinding.layoutSeekbarPodcast.tvCurrentTime.setText(StringUtils.getStringTimer(model.getDuration()));
                    this.viewBinding.layoutSeekbarPodcast.seekBarPodcast.setProgress(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void updateDarkMode ( boolean isDark){
            if (this.viewBinding != null && mContext != null) {
                int colorText = ContextCompat.getColor(mContext, isDark ? R.color.dark_play_color_text : R.color.light_play_color_text);
                int colorSecondText = ContextCompat.getColor(mContext, isDark ? R.color.dark_play_color_secondary_text : R.color.light_play_color_secondary_text);
                int colorAccent = ContextCompat.getColor(mContext, isDark ? R.color.dark_play_accent_color : R.color.light_play_accent_color);
                int bgLayer3Color = ContextCompat.getColor(mContext, isDark ? R.color.dark_color_background : R.color.light_color_background);

                ColorStateList btTintList = ContextCompat.getColorStateList(mContext, isDark ? R.color.dark_play_color_text : R.color.light_play_color_text);
                ColorStateList fbTintList = ContextCompat.getColorStateList(mContext, isDark ? R.color.dark_play_accent_color : R.color.light_play_accent_color);

                viewBinding.layoutBg.imgBgLayer1.setBackgroundColor(isDark ? getResources().getColor(R.color.dark_color_accent) : getResources().getColor(R.color.light_color_accent));
                viewBinding.layoutBg.imgBgLayer2.setBackgroundResource(isDark ? R.drawable.bg_dark_playing : R.drawable.bg_light_playing);
                viewBinding.layoutBg.imgBgLayer3.setBackgroundColor(bgLayer3Color);

                viewBinding.layoutSeekbarPodcast.tvCurrentTime.setTextColor(colorSecondText);
                viewBinding.layoutSeekbarPodcast.tvDuration.setTextColor(colorSecondText);
                viewBinding.tvInfoRecord.setTextColor(colorSecondText);

                ImageViewCompat.setImageTintList(viewBinding.layoutInfoPlay.btnEqualizer, btTintList);
                ImageViewCompat.setImageTintList(viewBinding.layoutInfoPlay.btnSleep, btTintList);

                ImageViewCompat.setImageTintList(viewBinding.layoutTotalControl.btnNext, btTintList);
                ImageViewCompat.setImageTintList(viewBinding.layoutTotalControl.btnPrev, btTintList);
                ImageViewCompat.setImageTintList(viewBinding.layoutTotalControl.btnReplay, btTintList);
                ImageViewCompat.setImageTintList(viewBinding.layoutTotalControl.btnForward, btTintList);

                viewBinding.layoutInfoPlay.tvDragSong.setTextColor(colorText);
                viewBinding.layoutInfoPlay.tvDragSinger.setTextColor(colorSecondText);

                ImageViewCompat.setImageTintList(viewBinding.layoutSeekbarVolume.imgVolumeMax, btTintList);
                ImageViewCompat.setImageTintList(viewBinding.layoutSeekbarVolume.imgVolumeOff, btTintList);

                viewBinding.layoutSeekbarPodcast.seekBarPodcast.thumbColor(colorAccent);
                // viewBinding.layoutSeekbarPodcast.seekBarPodcast.progressColor(colorSecondText, colorAccent);

                viewBinding.layoutSeekbarVolume.seekBarVolume.thumbColor(colorAccent);
                // viewBinding.layoutSeekbarVolume.seekBarVolume.progressColor(colorSecondText, colorAccent);

                viewBinding.layoutTotalControl.btnFavorite.setUnlikeDrawableRes(isDark ? R.drawable.ic_heart_outline_white_36dp : R.drawable.ic_heart_outline_black_36dp);
                viewBinding.layoutTotalControl.btnFavorite.setLikeDrawableRes(isDark ? R.drawable.ic_heart_dark_mode_36dp : R.drawable.ic_heart_pink_36dp);
                viewBinding.layoutTotalControl.fbPlay.setBackgroundTintList(fbTintList);

                // viewBinding.layoutTotalControl.playProgressBar.setIndicatorColor(colorAccent);
                resDefault = isDark ? R.drawable.ic_dark_play_default : R.drawable.ic_light_play_default;
                YPYMediaPlayer.StreamInfo mStrInfo = YPYStreamManager.getInstance().getStreamInfo();
                updateImage(mStrInfo != null ? mStrInfo.imgUrl : null);
                updateCurrentDownload();
            }
        }

    }
