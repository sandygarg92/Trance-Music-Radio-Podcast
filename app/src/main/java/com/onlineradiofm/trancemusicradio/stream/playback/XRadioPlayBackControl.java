package com.onlineradiofm.trancemusicradio.stream.playback;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.dataMng.RetroRadioNetUtils;
import com.onlineradiofm.trancemusicradio.dataMng.TotalDataManager;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.music.constant.IYPYStreamConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.music.control.IYPYPlaybackInfoListener;
import com.onlineradiofm.trancemusicradio.ypylibs.music.control.YPYPlaybackControl;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.music.mediaplayer.YPYMediaPlayer;
import com.onlineradiofm.trancemusicradio.ypylibs.music.model.YPYMusicModel;
import com.onlineradiofm.trancemusicradio.ypylibs.reactive.YPYRXModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.DownloadUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ImageProcessingUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.StringUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.io.File;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okio.BufferedSink;
import okio.Okio;


import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.DELTA_TIME_CHECK_VIEW;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.MAXIMUM_DELTA_RECORD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.MINIMUM_DELTA_RECORD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.PREFIX_CONTENT;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.RATE_EFFECT;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.RECORD_TEMP_FILE;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.TYPE_COUNT_VIEW;


/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2019-06-06.
 */

public class XRadioPlayBackControl extends YPYPlaybackControl implements IYPYStreamConstants {

    private final Context mContext;

    private YPYMediaPlayer ypyMediaPlayer;
    private MediaPlayer mMusicPlayer;

    private final IYPYPlaybackInfoListener mPlaybackInfoListener;
    private final MediaSessionCompat mMediaSessionCompat;

    private int mCurrentState;

    //record params
    private boolean isAllowRecordingFile;
    private File mTempRecordFile;
    private BufferedSink sink;
    private boolean isCreatedRecordFile;
    private long pivotTimeRecord;

    private final Handler mSkipHandler = new Handler();
    private long pivotTimer;
    private long pivotRadioId;
    private final YPYRXModel mYPYRXModel;

    private boolean isPlayCompleted;

    public XRadioPlayBackControl(@NonNull Context context, MediaSessionCompat mMediaSessionCompat,
                                 IYPYPlaybackInfoListener listener) {
        super(context);
        this.mContext = context.getApplicationContext();
        this.mPlaybackInfoListener = listener;
        this.mMediaSessionCompat = mMediaSessionCompat;
        this.mYPYRXModel = new YPYRXModel(Schedulers.io(), AndroidSchedulers.mainThread());

    }

    /**
     * Once the {@link MediaPlayer} is released, it can't be used again, and another one has to be
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */

    private void initializeMediaPlayer(boolean isUseYPYPlayer) {
        try {
            if (ypyMediaPlayer != null || mMusicPlayer != null) return;

            if (isUseYPYPlayer) {
                ypyMediaPlayer = new YPYMediaPlayer(mContext);
                // Make sure the media player will acquire a wake-lock while
                // playing. If we don't do that, the CPU might go to sleep while the
                // song is playing, causing playback to stop.
                ypyMediaPlayer.setWakeMode(mContext.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                ypyMediaPlayer.setOnStreamListener(new YPYMediaPlayer.OnStreamListener() {
                    @Override
                    public void onPrepare() {
                        YPYStreamManager.getInstance().setLoading(false);
                        setNewState(PlaybackStateCompat.STATE_PLAYING);
                        initAudioEffect();

                        if (mCurrentMusicModel instanceof RadioModel) {
                            boolean isAllowCount = !mCurrentMusicModel.isOfflineModel() && !((RadioModel) mCurrentMusicModel).isCalculateCount();
                            if (isAllowCount) {
                                onStartingViewCount(mCurrentMusicModel.getId());
                            }
                        }
                        if (mCurrentMusicModel.isLive()) {
                            XRadioSettingManager.setLastPlayedMyRadio(mContext, ((RadioModel) mCurrentMusicModel).isMyRadio());
                            XRadioSettingManager.setLastPlayedRadioId(mContext, mCurrentMusicModel.getId());
                        }

                        play();
                        if (mPlaybackInfoListener != null) {
                            mPlaybackInfoListener.onPrepareDone();
                        }
                    }

                    @Override
                    public void onError() {
                        Log.e("TAG","initializeMediaPlayer error");
                        /*YPYStreamManager.getInstance().setLoading(false);
                        setNewState(PlaybackStateCompat.STATE_ERROR);*/
                        playDefaultPuttedUrl();
                    }

                    @Override
                    public void onComplete() {
                        boolean isPodCast = (mCurrentMusicModel instanceof RadioModel) && ((RadioModel) mCurrentMusicModel).isPodCast();
                        YPYLog.e("DCM", "======>isPodCast=" + isPodCast);
                        if (isPodCast) {
                            YPYMusicModel mYPYMusicModel = YPYStreamManager.getInstance().nextPlay(mContext, true);
                            if (mYPYMusicModel != null) {
                                playMusicModel(mYPYMusicModel);
                            }
                            else {
                                stop();
                            }
                        }
                        else {
                            isPlayCompleted = true;
                            pause();
                            sendMusicBroadcast(ACTION_COMPLETE);
                        }

                    }

                    @Override
                    public void onUpdateMetaData(YPYMediaPlayer.StreamInfo info) {
                        YPYLog.e("DCM", "======>get StreamInfo info=" + info);
                        updateStreamInfo(info);
                    }

                    @Override
                    public void onDataSourceBytesRead(byte[] buffer, int offset, int length) {
                        startCheckRecord(buffer, offset, length);
                        long delta = System.currentTimeMillis() - pivotTimeRecord;
                        if (delta >= MAXIMUM_DELTA_RECORD * ONE_MINUTE) {
                            checkStopRecord(ACTION_RECORD_MAXIMUM);
                        }
                    }
                });
                YPYStreamManager.getInstance().setYpyMediaPlayer(ypyMediaPlayer);
            }
            else {
                mMusicPlayer = new MediaPlayer();
                mMusicPlayer.setWakeMode(mContext.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mMusicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMusicPlayer.setOnPreparedListener(mediaPlayer -> {
                    YPYStreamManager.getInstance().setLoading(false);
                    setNewState(PlaybackStateCompat.STATE_PLAYING);
                    initAudioEffect();
                    if (mCurrentMusicModel instanceof RadioModel) {
                        boolean isAllowCount = !mCurrentMusicModel.isOfflineModel() && !((RadioModel) mCurrentMusicModel).isCalculateCount();
                        YPYLog.e("DCM", "======>mMusicPlayer isAllowCount=" + isAllowCount);
                        if (isAllowCount) {
                            onStartingViewCount(mCurrentMusicModel.getId());
                        }
                    }
                    if (mCurrentMusicModel.isLive()) {
                        XRadioSettingManager.setLastPlayedMyRadio(mContext, ((RadioModel) mCurrentMusicModel).isMyRadio());
                        XRadioSettingManager.setLastPlayedRadioId(mContext, mCurrentMusicModel.getId());
                    }
                    play();
                    if (mPlaybackInfoListener != null) {
                        mPlaybackInfoListener.onPrepareDone();
                    }
                });
                mMusicPlayer.setOnCompletionListener(mediaPlayer -> {
                    boolean isPodCast = (mCurrentMusicModel instanceof RadioModel) && ((RadioModel) mCurrentMusicModel).isPodCast();
                    YPYLog.e("DCM", "======>isPodCast=" + isPodCast);
                    if (isPodCast) {
                        YPYMusicModel mYPYMusicModel = YPYStreamManager.getInstance().nextPlay(mContext, true);
                        if (mYPYMusicModel != null) {
                            playMusicModel(mYPYMusicModel);
                        }
                        else {
                            stop();
                        }
                    }
                    else {
                        isPlayCompleted = true;
                        pause();
                        sendMusicBroadcast(ACTION_COMPLETE);
                    }

                });
                mMusicPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
                    YPYStreamManager.getInstance().setLoading(false);
                    setNewState(PlaybackStateCompat.STATE_ERROR);
                    return true;
                });
                YPYStreamManager.getInstance().setNativeMediaPlayer(mMusicPlayer);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void playDefaultPuttedUrl() {
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            final String uriStream = "https://appsoup.net/defmp3/trance.mp3"; // musicModel.getLinkToPlay(mContext);
            RadioModel musicModel = (RadioModel) getCurrentMedia();

            mBitmapTrack = startGetBitmap(musicModel.getArtWork());
            YPYExecutorSupplier.getInstance().forMainThreadTasks().execute(() -> {
                if (!TextUtils.isEmpty(uriStream)) {
                    setUpMediaForStream(uriStream, musicModel.isUseYPYPlayer()/*false*/);
                    updateMetaData();
                }
                else {
                    stop();
                }
                isStartLoading = false;
            });

        });

    }

    private void sendMusicBroadcast(String action) {
        try {
            Intent mIntent = new Intent(mContext.getPackageName() + ACTION_BROADCAST_PLAYER);
            mIntent.putExtra(KEY_ACTION, action);
            mContext.sendBroadcast(mIntent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void playMusicModel(@NonNull YPYMusicModel musicModel) {
        try {
            if (!isStartLoading) {
                isStartLoading = true;
                this.mCurrentMusicModel = musicModel;
                //release old player
                releaseMediaPlayer();
                if (mMediaSessionCompat != null) {
                    mediaMetadataCompat = buildMetaDataCompat(null);
                    mMediaSessionCompat.setMetadata(mediaMetadataCompat);
                }
                if (mMediaSessionCompat != null && !mMediaSessionCompat.isActive()) {
                    mMediaSessionCompat.setActive(true);
                }
                setNewState(PlaybackStateCompat.STATE_CONNECTING);
                YPYStreamManager.getInstance().setLoading(true);

                this.isPlayCompleted = false;

                YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
                    final String uriStream = musicModel.getLinkToPlay(mContext);
                    YPYLog.e("DCM", "======>playMusicModel uriStream=" + uriStream);
                    mBitmapTrack = startGetBitmap(musicModel.getArtWork());
                    YPYExecutorSupplier.getInstance().forMainThreadTasks().execute(() -> {
                        if (!TextUtils.isEmpty(uriStream)) {
                            setUpMediaForStream(uriStream, musicModel.isUseYPYPlayer());
                            updateMetaData();
                        }
                        else {
                            stop();
                        }
                        isStartLoading = false;
                    });

                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSkipToNext() {
        YPYMusicModel mMusicModel = YPYStreamManager.getInstance().nextPlay(mContext, false);
        if (mMusicModel != null) {
            playMusicModel(mMusicModel);
        }
        else {
            stop();
        }

    }

    @Override
    public void onSkipToPrevious() {
        YPYMusicModel mMusicModel = YPYStreamManager.getInstance().prevPlay(mContext);
        if (mMusicModel != null) {
            playMusicModel(mMusicModel);
        }
        else {
            stop();
        }
    }

    @Override
    public void onFastForward() {
    }

    @Override
    public void onRewind() {

    }

    private void setUpMediaForStream(String uriStream, boolean isUseYPYPlayer) {
        initializeMediaPlayer(isUseYPYPlayer);
        try {
            if (!TextUtils.isEmpty(uriStream)) {
                if (isUseYPYPlayer && ypyMediaPlayer != null) {
                    setNewState(PlaybackStateCompat.STATE_BUFFERING);
                    ypyMediaPlayer.setDataSource(uriStream);
                    return;
                }
                if (!isUseYPYPlayer && mMusicPlayer != null) {
                    setNewState(PlaybackStateCompat.STATE_BUFFERING);
                    if (uriStream.startsWith(PREFIX_CONTENT)) {
                        mMusicPlayer.setDataSource(mContext, Uri.parse(uriStream));
                    }
                    else {
                        mMusicPlayer.setDataSource(uriStream);
                    }
                    mMusicPlayer.prepareAsync();
                    return;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        setNewState(PlaybackStateCompat.STATE_ERROR);
        onStop();
    }


    @Override
    public boolean isPlaying() {
        if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
            return true;
        }
        return ypyMediaPlayer != null && ypyMediaPlayer.isPlaying();
    }

    @Override
    protected void onPlay() {
        if (YPYStreamManager.getInstance().isLoading()) {
            return;
        }
        if (mCurrentState == PlaybackStateCompat.STATE_PAUSED
                && mCurrentMusicModel != null && mCurrentMusicModel.isLive()) {
            playMusicModel(mCurrentMusicModel);
            return;
        }
        try {
            if (mMusicPlayer != null && !mMusicPlayer.isPlaying()) {
                if (!mMediaSessionCompat.isActive()) {
                    mMediaSessionCompat.setActive(true);
                }
                mMusicPlayer.start();
            }
            if (ypyMediaPlayer != null && !ypyMediaPlayer.isPlaying()) {
                if (!mMediaSessionCompat.isActive()) {
                    mMediaSessionCompat.setActive(true);
                }
                ypyMediaPlayer.start();
            }
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }
        catch (Exception e) {
            e.printStackTrace();
            onStop();
        }

    }

    //TODO fake for streaming live radio
    @Override
    protected void onPause() {
        if (YPYStreamManager.getInstance().isLoading()) {
            return;
        }
        try {
            if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
                if (mCurrentMusicModel.isLive()) {
                    mMusicPlayer.stop();
                }
                else {
                    mMusicPlayer.pause();
                }
            }
            if (ypyMediaPlayer != null && ypyMediaPlayer.isPlaying()) {
                if (mCurrentMusicModel.isLive()) {
                    checkStopRecord(ACTION_RECORD_FINISH);
                    ypyMediaPlayer.stop();
                }
                else {
                    ypyMediaPlayer.pause();
                }
            }
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
        catch (Exception e) {
            e.printStackTrace();
            onStop();
        }
    }

    @Override
    protected void onStop() {
        if (mYPYRXModel != null) {
            mYPYRXModel.onDestroy();
        }
        if (YPYStreamManager.getInstance().isRecordingFile()) {
            checkStopRecord(ACTION_RECORD_FINISH);
        }
        if (mMediaSessionCompat != null) {
            mMediaSessionCompat.setActive(false);
        }
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        releaseMediaPlayer();

    }

    @Override
    public void seekTo(long position) {
        if (YPYStreamManager.getInstance().isLoading()) {
            return;
        }
        try {
            if (mMusicPlayer != null && position > 0 && position <= getDuration()) {
                mMusicPlayer.seekTo((int) position);
                setNewState(mCurrentState);
                return;
            }
            if (ypyMediaPlayer != null && position > 0 && position <= getDuration()) {
                ypyMediaPlayer.seekTo((int) position);
                setNewState(mCurrentState);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            onStop();
        }

    }

    @Override
    public void setVolume(float volume) {
        if (mMusicPlayer != null) {
            mMusicPlayer.setVolume(volume, volume);
            return;
        }
        if (ypyMediaPlayer != null) {
            ypyMediaPlayer.setVolume(volume);
        }
    }

    @Override
    public void togglePlay() {
        if (YPYStreamManager.getInstance().isLoading()) {
            return;
        }
        if (this.isPlayCompleted) {
            playMusicModel(mCurrentMusicModel);
            return;
        }
        if (mMusicPlayer != null) {
            if (!mMusicPlayer.isPlaying()) {
                play();
            }
            else {
                pause();
            }
            return;
        }
        if (ypyMediaPlayer != null) {
            if (!ypyMediaPlayer.isPlaying()) {
                play();
            }
            else {
                pause();
            }
        }
    }

    @Override
    public long getDuration() {
        if (mMusicPlayer != null) {
            return mMusicPlayer.getDuration();
        }
        if (ypyMediaPlayer != null) {
            return ypyMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public long getCurrentPosition() {
        if (mMusicPlayer != null) {
            return mMusicPlayer.getCurrentPosition();
        }
        if (ypyMediaPlayer != null) {
            return ypyMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    @Override
    protected MediaMetadataCompat buildMetaDataCompat(Bitmap mBitmapTrack) {
        try {
            int currentIndex = YPYStreamManager.getInstance().getCurrentIndex();
            int numberTrack = YPYStreamManager.getInstance().getNumberTrack();
            if (mCurrentMusicModel != null && numberTrack > 0 && currentIndex >= 0) {
                MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
                try {
                    if (mBitmapTrack != null && !mBitmapTrack.isRecycled()) {
                        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, mBitmapTrack);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                String artWork = ((RadioModel) mCurrentMusicModel).getSongImg();
                if (!TextUtils.isEmpty(artWork) && artWork.startsWith("http")) {
                    metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, artWork);
                }
                String title = mCurrentMusicModel.getSong();
                String artistName = mCurrentMusicModel.getArtist();
                if (TextUtils.isEmpty(artistName)) {
                    artistName = mContext.getString(R.string.title_unknown);
                }
                metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title);
                metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName);
                metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, currentIndex);
                metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, numberTrack);

                return metadataBuilder.build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void releaseMediaPlayer() {
        onResetViewCount();
        onDestroyBitmap();
        if (mMusicPlayer != null) {
            mMusicPlayer.release();
            mMusicPlayer = null;
        }
        if (ypyMediaPlayer != null) {
            ypyMediaPlayer.release();
            ypyMediaPlayer = null;
        }
        YPYStreamManager.getInstance().resetMedia();
    }

    // This is the main reducer for the player state machine.
    private void setNewState(@PlaybackStateCompat.State int newPlayerState) {
        try {
            mCurrentState = newPlayerState;
            final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
            long action = getAvailableActions();
            stateBuilder.setActions(action);
            stateBuilder.setState(mCurrentState, 0, 1.0f, SystemClock.elapsedRealtime());
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */
    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;

        switch (mCurrentState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
            case PlaybackStateCompat.STATE_CONNECTING:
            case PlaybackStateCompat.STATE_BUFFERING:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    private void onDestroyBitmap() {
        try {
            if (mBitmapTrack != null) {
                mBitmapTrack.recycle();
                mBitmapTrack = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mColorNotification = 0;
    }

    private void updateStreamInfo(YPYMediaPlayer.StreamInfo info) {
        RadioModel mCurrentTrack = (RadioModel) getCurrentMedia();
        String title = info != null && !TextUtils.isEmpty(info.title) ? info.title : null;
        String artist = info != null && !TextUtils.isEmpty(info.artist) ? info.artist : null;
        YPYStreamManager.getInstance().setStreamInfo(info);

        if (mCurrentTrack != null) {
            mCurrentTrack.setSong(title);
            mCurrentTrack.setArtist(artist);
        }

        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onUpdateMetadata(info);
        }

        updateMetaData();
        startFindImage(info);
    }

    private void updateMetaData() {
        try {
            mediaMetadataCompat = buildMetaDataCompat(mBitmapTrack);
            if (mMediaSessionCompat != null && mMediaSessionCompat.isActive()) {
                mMediaSessionCompat.setMetadata(mediaMetadataCompat);
            }
            setNewState(mCurrentState);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startFindImage(@Nullable YPYMediaPlayer.StreamInfo mStreamInfo) {
        try {
            YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
                String img = getImageFromMetadata(mStreamInfo);
                RadioModel mCurrentModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
                if (mCurrentModel != null) {
                    mCurrentModel.setSongImg(img);
                    if (mPlaybackInfoListener != null) {
                        mPlaybackInfoListener.onUpdateArtwork(img);
                    }
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getImageFromMetadata(@Nullable YPYMediaPlayer.StreamInfo mStreamInfo) {
        if (ApplicationUtils.isOnline(mContext) && mStreamInfo != null) {
            String url = XRadioNetUtils.getImageOfSong(mStreamInfo.title, mStreamInfo.artist);
            mStreamInfo.imgUrl = !TextUtils.isEmpty(url) ? url : "";
            return mStreamInfo.imgUrl;
        }
        return "";
    }

    private Bitmap startGetBitmap(String uri) {
        Bitmap mBitmap = null;
        try {
            if (!TextUtils.isEmpty(uri)) {
                InputStream mInputStream = null;
                if (!uri.startsWith("http")) {
                    if (uri.startsWith("file:///android_asset/")) {
                        uri = uri.replace("file:///android_asset/", "");
                        YPYLog.e("DCM", "========>startGetBitmap=" + uri);
                        mInputStream = mContext.getAssets().open(uri);
                    }
                    if (mInputStream != null) {
                        mBitmap = ImageProcessingUtils.decodePortraitBitmap(mInputStream, 300, 300);
                    }
                }
                else {
                    try {
                        mInputStream = DownloadUtils.downloadInputStream(uri);
                        if (mInputStream != null) {
                            mBitmap = ImageProcessingUtils.decodePortraitBitmap(mInputStream, 300, 300);
                            mInputStream.close();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;

    }

    private synchronized void startCheckRecord(byte[] buffer, int offset, int length) {
        if (isAllowRecordingFile) {
            if (buffer != null && length != -1) {
                try {
                    if (!isCreatedRecordFile) {
                        isCreatedRecordFile = true;
                        File mDirFile = TotalDataManager.getInstance(mContext).getDirDownloadTemp(mContext);
                        if (mDirFile != null) {
                            File mOldFile = new File(mDirFile, RECORD_TEMP_FILE);
                            if (mOldFile.exists() && mOldFile.isFile()) {
                                mOldFile.delete();
                            }
                            mTempRecordFile = new File(mDirFile, RECORD_TEMP_FILE);
                            YPYLog.e("DCM", "====>startCheckRecord mTempRecordFile=" + mOldFile.getAbsolutePath());
                            sink = Okio.buffer(Okio.sink(mOldFile));
                            sink.write(buffer, offset, length);

                            pivotTimeRecord = System.currentTimeMillis();
                            sendMusicBroadcast(ACTION_RECORD_START);
                        }
                        else {
                            stopRecord(ACTION_RECORD_ERROR_SD);
                        }
                    }
                    else {
                        if (sink != null) {
                            sink.write(buffer, offset, length);
                        }
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                    stopRecord(ACTION_RECORD_ERROR_UNKNOWN);
                }
            }
        }
    }

    public void onStartRecord() {
        if (YPYStreamManager.getInstance().isPlaying()) {
            if (mCurrentMusicModel != null && !mCurrentMusicModel.isOfflineModel()) {
                if (!isAllowRecordingFile) {
                    stopRecord(null);
                    isAllowRecordingFile = true;
                    YPYStreamManager.getInstance().setRecordingFile(true);
                }
            }

        }
    }

    public void checkStopRecord(String action) {
        if (isAllowRecordingFile) {
            long delta = System.currentTimeMillis() - pivotTimeRecord;
            Log.e("DCM", "====>checkStopRecord=" + delta);
            if (delta < MINIMUM_DELTA_RECORD) {
                stopRecord(ACTION_RECORD_ERROR_SHORT_TIME);
                try {
                    if (mTempRecordFile != null && mTempRecordFile.isFile()) {
                        mTempRecordFile.delete();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                stopRecord(action);
            }
        }
    }

    private synchronized void stopRecord(String action) {
        YPYStreamManager.getInstance().setRecordingFile(false);
        isAllowRecordingFile = false;
        isCreatedRecordFile = false;
        pivotTimeRecord = 0;
        if (mCurrentMusicModel != null && mCurrentMusicModel.isOfflineModel()) {
            return;
        }
        try {
            if (sink != null) {
                sink.close();
                sink = null;
            }
        }
        catch (Exception e1) {
            e1.printStackTrace();
            sink = null;
        }
        YPYLog.e("DCM", "========>stopRecord action=" + action);
        if (!TextUtils.isEmpty(action)) {
            sendMusicBroadcast(action);
        }

    }

    private void onResetViewCount() {
        this.mSkipHandler.removeCallbacksAndMessages(null);
        this.pivotTimer = 0;
        this.pivotRadioId = 0;
        if (mCurrentMusicModel != null && (mCurrentMusicModel instanceof RadioModel)) {
            ((RadioModel) mCurrentMusicModel).setCalculateCount(false);
        }
    }

    private void onStartingViewCount(long radioId) {
        try {
            if (mCurrentMusicModel != null && (mCurrentMusicModel instanceof RadioModel)) {
                ((RadioModel) mCurrentMusicModel).setCalculateCount(true);
            }
            this.pivotTimer = System.currentTimeMillis();
            this.pivotRadioId = radioId;
            this.mSkipHandler.removeCallbacksAndMessages(null);
            this.mSkipHandler.postDelayed(() -> checkUpdateViewCount(this.pivotRadioId), DELTA_TIME_CHECK_VIEW);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkUpdateViewCount(long id) {
        try {
            this.mSkipHandler.removeCallbacksAndMessages(null);
            long deltaView = System.currentTimeMillis() - pivotTimer;
            updateViewCount(id, deltaView >= DELTA_TIME_CHECK_VIEW ? 1 : -1);
            onResetViewCount();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateViewCount(long id, int value) {
        try {
            if (ApplicationUtils.isOnline(mContext)) {
                Observable<ResultModel<AbstractModel>> viewCount = RetroRadioNetUtils.updateCount(mContext, id, TYPE_COUNT_VIEW, value);
                mYPYRXModel.addObservableToObserver(viewCount, new DisposableObserver<>() {
                    @Override
                    public void onNext(@NonNull ResultModel<AbstractModel> mResultModel) {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkUpdateSkipCount() {
        try {
            if (mCurrentMusicModel instanceof RadioModel) {
                if (((RadioModel) mCurrentMusicModel).isCalculateCount() && pivotRadioId > 0 && pivotTimer > 0) {
                    long deltaView = System.currentTimeMillis() - pivotTimer;
                    YPYLog.e("DCM", "======>update skip count pivotRadioId=" + pivotRadioId + "=>deltaView=" + deltaView);
                    if (deltaView < DELTA_TIME_CHECK_VIEW) {
                        checkUpdateViewCount(pivotRadioId);
                    }
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initAudioEffect() {
        boolean b = XRadioSettingManager.getEqualizer(mContext);
        try {
            int audioSession = YPYStreamManager.getInstance().getAudioSession();
            if (audioSession == 0) return;
            Equalizer mEqualizer = new Equalizer(0, audioSession);
            mEqualizer.setEnabled(b);
            setUpParams(mEqualizer);
            YPYStreamManager.getInstance().setEqualizer(mEqualizer);

            setUpBassBoostVir();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setUpBassBoostVir() {
        try {
            int audioSession = YPYStreamManager.getInstance().getAudioSession();
            if (audioSession == 0) return;
            boolean b = XRadioSettingManager.getEqualizer(mContext);
            BassBoost mBassBoost = new BassBoost(0, audioSession);
            Virtualizer mVirtualizer = new Virtualizer(0, audioSession);
            if (mBassBoost.getStrengthSupported() && mVirtualizer.getStrengthSupported()) {
                short bass = XRadioSettingManager.getBassBoost(mContext);
                short vir = XRadioSettingManager.getVirtualizer(mContext);
                mBassBoost.setEnabled(b);
                mVirtualizer.setEnabled(b);
                mBassBoost.setStrength((short) (bass * RATE_EFFECT));
                mVirtualizer.setStrength((short) (vir * RATE_EFFECT));
                YPYStreamManager.getInstance().setBassBoost(mBassBoost);
                YPYStreamManager.getInstance().setVirtualizer(mVirtualizer);
            }
            else {
                try {
                    mBassBoost.release();
                    mVirtualizer.release();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setUpParams(Equalizer mEqualizer) {
        try {
            if (mEqualizer != null) {
                String presetStr = XRadioSettingManager.getEqualizerPreset(mContext);
                if (!TextUtils.isEmpty(presetStr)) {
                    if (StringUtils.isNumber(presetStr)) {
                        short preset = Short.parseShort(presetStr);
                        short numberPreset = mEqualizer.getNumberOfPresets();
                        if (numberPreset > 0) {
                            if (preset < numberPreset - 1 && preset >= 0) {
                                try {
                                    mEqualizer.usePreset(preset);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                        }
                    }
                }
                setUpEqualizerCustom(mEqualizer);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setUpEqualizerCustom(Equalizer mEqualizer) {
        try {
            if (mEqualizer != null) {
                String params = XRadioSettingManager.getEqualizerParams(mContext);
                if (!TextUtils.isEmpty(params)) {
                    String[] mEqualizerParams = params.split(":");
                    if (mEqualizerParams.length > 0) {
                        int size = mEqualizerParams.length;
                        for (int i = 0; i < size; i++) {
                            mEqualizer.setBandLevel((short) i, Short.parseShort(mEqualizerParams[i]));
                        }
                        short numberPreset = mEqualizer.getNumberOfPresets();
                        XRadioSettingManager.setEqualizerPreset(mContext, String.valueOf(numberPreset));
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


}
