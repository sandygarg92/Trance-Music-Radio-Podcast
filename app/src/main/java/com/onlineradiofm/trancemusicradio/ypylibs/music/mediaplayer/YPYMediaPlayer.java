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

package com.onlineradiofm.trancemusicradio.ypylibs.music.mediaplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.PowerManager;
import android.text.TextUtils;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.music.shoutcast.ShoutcastDataSourceFactory;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.OkHttpClient;


/**
 * @author:radiopolska
 * @Skype: 
 * @Mobile : 
 * @Email: baodt@hanet.com
 * @Website: http://hanet.com/
 * @Project: cyberfm
 * Created by radiopolska on 5/21/17.
 */

public class YPYMediaPlayer {

    private static final long ONE_MINUTE = 60000L;
    private static final int WAKE_LOCK_TIMEOUT = 60;

    private final Context mContext;

    private OnStreamListener onStreamListener;

    private boolean isPrepared;
    private ExoPlayer mAudioPlayer;
    private final Call.Factory factory = (Call.Factory) new OkHttpClient.Builder().build();

    @Nullable
    private PowerManager.WakeLock wakeLock = null;

    public YPYMediaPlayer(Context mContext) {
        this.mContext = mContext;
    }

    public void release() {
        isPrepared = false;
        if (mAudioPlayer != null) {
            mAudioPlayer.release();
            stayAwake(false);
            mAudioPlayer = null;
        }

    }

    public void setVolume(float volume) {
        try {
            if (mAudioPlayer != null) {
                mAudioPlayer.setVolume(volume);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnStreamListener(OnStreamListener onStreamListener) {
        this.onStreamListener = onStreamListener;
    }


    public void setDataSource(String url) {
        if (!TextUtils.isEmpty(url)) {
            String mUrlStream = url;
            /*DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);*/

            DefaultTrackSelector trackSelector = new DefaultTrackSelector(mContext);

            mAudioPlayer = new ExoPlayer.Builder(mContext)
                    .setTrackSelector(trackSelector)
                    .build();
            mAudioPlayer.addListener(new Player.Listener() {

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                        if (onStreamListener != null) {
                            onStreamListener.onComplete();
                        }
                    }
                    else if (playbackState == Player.STATE_READY) {
                        if (onStreamListener != null && !isPrepared) {
                            isPrepared = true;
                            onStreamListener.onPrepare();
                        }
                    }
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    if (onStreamListener != null) {
                        onStreamListener.onError();
                    }
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                        if (onStreamListener != null) {
                            onStreamListener.onComplete();
                        }
                    } else if (playbackState == Player.STATE_READY) {
                        if (onStreamListener != null && !isPrepared) {
                            isPrepared = true;
                            onStreamListener.onPrepare();
                        }
                    }
                }

            });
            DataSource.Factory dataSourceFactory;
            MediaSource mediaSource;

            if (mUrlStream.endsWith("_Other")) {
                mUrlStream = mUrlStream.replace("_Other", "");
            }
            if(!mUrlStream.startsWith("http")){
                dataSourceFactory = new DefaultDataSourceFactory(mContext, getUserAgent(mContext));
            }
            else{
                dataSourceFactory = new ShoutcastDataSourceFactory(factory,
                        getUserAgent(mContext), null, data -> {
                    try {
                        String artist = data.getArtist();
                        String song = data.getSong();
                        String show = data.getShow();
                        StreamInfo mStreamInfo = new StreamInfo();
                        mStreamInfo.title = !TextUtils.isEmpty(song) ? song : "";
                        if (TextUtils.isEmpty(mStreamInfo.title)) {
                            mStreamInfo.title = !TextUtils.isEmpty(show) ? show : "";
                        }
                        mStreamInfo.artist = !TextUtils.isEmpty(artist) ? artist : "";
                        YPYExecutorSupplier.getInstance().forMainThreadTasks().execute(() -> {
                            if (onStreamListener != null) {
                                onStreamListener.onUpdateMetaData(mStreamInfo);
                            }
                        });

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                ((ShoutcastDataSourceFactory) dataSourceFactory).setYpyDataSourceListener((buffer, offset, length) -> {
                    if(onStreamListener!=null){
                        onStreamListener.onDataSourceBytesRead(buffer,offset,length);
                    }
                });

            }
            if (mUrlStream.endsWith(".m3u8") || mUrlStream.endsWith(".M3U8")) {
                mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(mUrlStream));
            }
            else {
                MediaItem mediaItem = MediaItem.fromUri(mUrlStream);
                mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mediaItem);
            }
            mAudioPlayer.prepare(mediaSource);
            start();
            return;

        }
        if (onStreamListener != null) {
            onStreamListener.onError();
        }
    }


    public void start() {
        try {
            if (mAudioPlayer != null) {
                mAudioPlayer.setPlayWhenReady(true);
                stayAwake(true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pause() {
        try {
            if (mAudioPlayer != null) {
                mAudioPlayer.setPlayWhenReady(false);
                stayAwake(false);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        try {
            if (mAudioPlayer != null) {
                mAudioPlayer.stop();
                stayAwake(false);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isPlaying() {
        try {
            boolean isReady = mAudioPlayer != null && mAudioPlayer.getPlayWhenReady();
            if (isReady) {
                return mAudioPlayer.getPlaybackState() == Player.STATE_READY;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void seekTo(long currentPos){
        try{
            if(mAudioPlayer!=null){
                mAudioPlayer.seekTo(currentPos);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public long getDuration(){
        try{
            if(mAudioPlayer!=null){
                return mAudioPlayer.getDuration();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public long getCurrentPosition(){
        try{
            if(mAudioPlayer!=null){
                return mAudioPlayer.getCurrentPosition();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }


    private String getUserAgent(Context mContext) {
        return Util.getUserAgent(mContext, getClass().getSimpleName());
    }

    /**
     * This function has the MediaPlayer access the low-level power manager
     * service to control the device's power usage while playing is occurring.
     * The parameter is a combination of {@link android.os.PowerManager} wake flags.
     * Use of this method requires {@link android.Manifest.permission#WAKE_LOCK}
     * permission.
     * By default, no attempt is made to keep the device awake during playback.
     *
     * @param context the Context to use
     * @param mode    the power/wake mode to set
     * @see android.os.PowerManager
     */
    public void setWakeMode(Context context, int mode) {
        try{
            boolean wasHeld = false;
            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wasHeld = true;
                    wakeLock.release();
                }
                wakeLock = null;
            }
            //Acquires the wakelock if we have permissions to
            if (context.getPackageManager().checkPermission(Manifest.permission.WAKE_LOCK, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (pm != null) {
                    wakeLock = pm.newWakeLock(mode | PowerManager.ON_AFTER_RELEASE, YPYMediaPlayer.class.getName());
                    wakeLock.setReferenceCounted(false);
                } else {
                    YPYLog.e("DCM", "Unable to acquire WAKE_LOCK due to a null power manager");
                }
            }
            else {
                YPYLog.w("DCM", "Unable to acquire WAKE_LOCK due to missing manifest permission");
            }
            stayAwake(wasHeld);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Used with playback state changes to correctly acquire and
     * release the wakelock if the user has enabled it with {@link #setWakeMode(Context, int)}.
     * If the {@link #wakeLock} is null then no action will be performed.
     *
     * @param awake True if the wakelock should be acquired
     */
    private void stayAwake(boolean awake) {
        try{
            if (wakeLock == null) {
                return;
            }
            if (awake && !wakeLock.isHeld()) {
                wakeLock.acquire(WAKE_LOCK_TIMEOUT*ONE_MINUTE);
            }
            else if (!awake && wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public int getAudioSession() {
        try {
            if (mAudioPlayer != null) {
                return mAudioPlayer.getAudioSessionId();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public interface OnStreamListener {
        void onPrepare();
        void onError();
        void onComplete();
        void onUpdateMetaData(StreamInfo info);
        void onDataSourceBytesRead(byte[] buffer, int offset, int length);
    }

    public static class StreamInfo {
        public String title;
        public String artist;
        public String imgUrl;
    }


}
