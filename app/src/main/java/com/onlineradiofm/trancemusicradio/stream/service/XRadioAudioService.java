package com.onlineradiofm.trancemusicradio.stream.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.stream.notification.XRadioMediaNotification;
import com.onlineradiofm.trancemusicradio.stream.playback.XRadioPlayBackControl;
import com.onlineradiofm.trancemusicradio.ypylibs.music.constant.IYPYStreamConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.music.control.IYPYPlaybackInfoListener;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.music.mediaplayer.YPYMediaPlayer;
import com.onlineradiofm.trancemusicradio.ypylibs.music.model.YPYMusicModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;


/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2019-06-06.
 */
public class XRadioAudioService extends MediaBrowserServiceCompat implements IYPYStreamConstants {

    private static final String TAG = XRadioAudioService.class.getSimpleName();

    private final Handler mHandlerSleep = new Handler();
    private final Handler mSeekHandler = new Handler();
    private final Handler mHandlerPlayCount = new Handler();

    private XRadioPlayBackControl mPlaybackControl;
    private MediaSessionCompat mMediaSessionCompat;

    private NotificationManager mNotificationManager;
    private int mMinuteCount;

    private XRadioMediaNotification mMediaNotification;

    @Nullable
    private WifiManager.WifiLock mWifiLock;

    private final MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPlay() {
            super.onPlay();
            if (mPlaybackControl != null) {
                mPlaybackControl.play();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mPlaybackControl != null) {
                mPlaybackControl.pause();
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mPlaybackControl != null) {
                mPlaybackControl.stop();
            }

        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            if (mPlaybackControl != null) {
                mPlaybackControl.seekTo(pos);
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            if (mPlaybackControl != null) {
                mPlaybackControl.onSkipToNext();
            }

        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            if (mPlaybackControl != null) {
                mPlaybackControl.onSkipToPrevious();
            }
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            if (mPlaybackControl != null) {
                mPlaybackControl.onFastForward();
            }
        }

        @Override
        public void onRewind() {
            super.onRewind();
            if (mPlaybackControl != null) {
                mPlaybackControl.onRewind();
            }
        }

        @Override
        public boolean onMediaButtonEvent(@NonNull Intent mediaButtonEvent) {
            try {
                String action = mediaButtonEvent.getAction();
                if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
                    final KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (event.getKeyCode()) {
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                                if (mPlaybackControl != null) {
                                    mPlaybackControl.play();
                                }
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                                if (mPlaybackControl != null) {
                                    mPlaybackControl.pause();
                                }
                                break;
                            case KeyEvent.KEYCODE_MEDIA_STOP:
                                if (mPlaybackControl != null) {
                                    mPlaybackControl.stop();
                                }
                                break;
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return super.onMediaButtonEvent(mediaButtonEvent);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        this.mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        this.initMediaSession();
        this.setUpWifiLock();
    }

    private void setUpWifiLock() {
        try {
            WifiManager mWifiManager = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE));
            if (mWifiManager != null) {
                int lockType = IOUtils.isAndroid80() ? WifiManager.WIFI_MODE_FULL_HIGH_PERF : WifiManager.WIFI_MODE_FULL;
                this.mWifiLock = mWifiManager.createWifiLock(lockType, TAG);
                // If we are streaming from the internet, we want to hold a
                // Wifi lock, which prevents the Wifi radio from going to
                // sleep while the song is playing.
                if (mWifiLock != null && !mWifiLock.isHeld()) {
                    mWifiLock.acquire();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initMediaSession() {
        try {
            if (mMediaSessionCompat == null) {
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, IOUtils.isAndroid12() ? PendingIntent.FLAG_IMMUTABLE : 0);
                ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
                mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), TAG_MEDIA, mediaButtonReceiver, pendingIntent);
                mMediaSessionCompat.setCallback(mMediaSessionCallback);

                mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
                setSessionToken(mMediaSessionCompat.getSessionToken());
                mediaButtonIntent.setClass(this, MediaButtonReceiver.class);

                mPlaybackControl = new XRadioPlayBackControl(this, mMediaSessionCompat, new MediaPlayerListener());
                mMediaNotification = new XRadioMediaNotification(this, mPlaybackControl);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }


    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        try {
            String action = intent.getAction();
            if (action != null && !TextUtils.isEmpty(action)) {
                if (mMediaSessionCompat != null && action.equalsIgnoreCase(Intent.ACTION_MEDIA_BUTTON)) {
                    setUpNotification();
                    MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
                    return super.onStartCommand(intent, flags, startId);
                }
                String packageName = getPackageName();
                if (action.equalsIgnoreCase(packageName + ACTION_TOGGLE_PLAYBACK)) {
                    if (mPlaybackControl != null) {
                        mPlaybackControl.togglePlay();
                    }
                }
                else if (action.equalsIgnoreCase(packageName + ACTION_PLAY)) {
                    setUpNotification();
                    startSleepMode();
                    if (mPlaybackControl != null) {
                        mPlaybackControl.checkUpdateSkipCount();
                        mPlaybackControl.playMusicModel(YPYStreamManager.getInstance().getCurrentModel());
                    }
                }
                else if (action.equalsIgnoreCase(packageName + ACTION_STOP)) {
                    setUpNotification();
                    startSleepMode();
                    if (mPlaybackControl != null) {
                        mPlaybackControl.stop();
                    }
                }
                else if (action.equalsIgnoreCase(packageName + ACTION_NEXT)) {
                    setUpNotification();
                    if (mPlaybackControl != null) {
                        mPlaybackControl.checkUpdateSkipCount();
                        mPlaybackControl.onSkipToNext();
                    }
                }
                else if (action.equalsIgnoreCase(packageName + ACTION_PREVIOUS)) {
                    setUpNotification();
                    if (mPlaybackControl != null) {
                        mPlaybackControl.checkUpdateSkipCount();
                        mPlaybackControl.onSkipToPrevious();
                    }
                }
                else if (action.equals(packageName + ACTION_UPDATE_POS)) {
                    int mCurrentPos = intent.getIntExtra(KEY_VALUE, -1);
                    if (mPlaybackControl != null) {
                        mPlaybackControl.seekTo(mCurrentPos);
                    }
                }
                else if (action.equals(packageName + ACTION_UPDATE_FAST)) {
                    int sign = intent.getIntExtra(KEY_VALUE, 0);
                    seekTo15Seconds(sign);
                }
                else if (action.equals(packageName + ACTION_UPDATE_FAST_SEEK)) {
                    int sign = intent.getIntExtra(KEY_VALUE, 0);
                    if (mPlaybackControl != null && sign != 0) {
                        if (sign > 0) {
                            mPlaybackControl.onFastForward();
                        }
                        else {
                            mPlaybackControl.onRewind();
                        }
                    }
                }
                else if (action.equals(packageName + ACTION_UPDATE_SLEEP_MODE)) {
                    startSleepMode();
                }
                else if (action.equals(packageName + ACTION_CONNECTION_LOST)) {
                    if (mPlaybackControl != null && mPlaybackControl.isPlaying()) {
                        mPlaybackControl.togglePlay();
                        sendMusicBroadcast(ACTION_CONNECTION_LOST);
                        mPlaybackControl.checkStopRecord(ACTION_RECORD_FINISH);
                    }
                }
                else if (action.equals(packageName + ACTION_RECORD_START)) {
                    if (mPlaybackControl != null) {
                        mPlaybackControl.onStartRecord();
                    }
                }
                else if (action.equals(packageName + ACTION_RECORD_STOP)) {
                    if (mPlaybackControl != null) {
                        mPlaybackControl.checkStopRecord(ACTION_RECORD_FINISH);
                    }
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    private void sendMusicBroadcast(String action) {
        sendMusicBroadcast(action, -1);
    }

    private void sendMusicBroadcast(String action, long value1) {
        Intent mIntent = new Intent(getPackageName() + ACTION_BROADCAST_PLAYER);
        mIntent.putExtra(KEY_ACTION, action);
        if (value1 != -1) {
            mIntent.putExtra(KEY_VALUE, value1);
        }
        sendBroadcast(mIntent);
    }

    private void sendMusicBroadcast(String action, String value) {
        try {
            Intent mIntent = new Intent(getPackageName() + ACTION_BROADCAST_PLAYER);
            mIntent.putExtra(KEY_ACTION, action);
            mIntent.putExtra(KEY_VALUE, value);
            sendBroadcast(mIntent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startUpdatePosition() {
        mSeekHandler.postDelayed(() -> {
            YPYMusicModel mCurrentTrack = mPlaybackControl != null ? mPlaybackControl.getCurrentMedia() : null;
            try {
                if (mCurrentTrack != null) {
                    long duration = mPlaybackControl.getDuration();
                    if (duration > 0) {
                        if (mCurrentTrack.getDuration() == 0) {
                            mCurrentTrack.setDuration(duration);
                        }
                        long current = mPlaybackControl.getCurrentPosition();
                        sendMusicBroadcast(ACTION_UPDATE_POS, current);
                        if (current < duration) {
                            startUpdatePosition();
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }, 1000);

    }


    private void startSleepMode() {
        try {
            int minute = XRadioSettingManager.getSleepMode(this);
            mHandlerSleep.removeCallbacksAndMessages(null);
            if (minute > 0) {
                this.mMinuteCount = minute * ONE_MINUTE;
                startCountSleep();
            }
            else {
                sendMusicBroadcast(ACTION_UPDATE_SLEEP_MODE, 0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void startCountSleep() {
        try {
            if (mMinuteCount > 0) {
                mHandlerSleep.postDelayed(() -> {
                    mMinuteCount = mMinuteCount - 1000;
                    sendMusicBroadcast(ACTION_UPDATE_SLEEP_MODE, mMinuteCount);
                    if (mMinuteCount <= 0) {
                        if (mPlaybackControl != null) {
                            mPlaybackControl.stop();
                        }
                    }
                    else {
                        startCountSleep();
                    }
                }, 1000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class MediaPlayerListener implements IYPYPlaybackInfoListener {


        MediaPlayerListener() {

        }

        @Override
        public void onPlaybackStateChange(@NonNull PlaybackStateCompat state) {
            try {
                // Report the state to the MediaSession.
                mMediaSessionCompat.setPlaybackState(state);
                switch (state.getState()) {
                    case PlaybackStateCompat.STATE_PLAYING:
                        startMusicServiceAgain(state);
                        sendMusicBroadcast(ACTION_PLAY);
                        startUpdatePosition();
                        break;
                    case PlaybackStateCompat.STATE_CONNECTING:
                        mHandlerPlayCount.removeCallbacksAndMessages(null);
                        mSeekHandler.removeCallbacksAndMessages(null);
                        startMusicServiceAgain(state);
                        sendMusicBroadcast(ACTION_LOADING);
                        break;
                    case PlaybackStateCompat.STATE_BUFFERING:
                        startMusicServiceAgain(state);
                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                        updateNotificationForPause(state);
                        sendMusicBroadcast(ACTION_PAUSE);
                        break;
                    case PlaybackStateCompat.STATE_STOPPED:
                        onActionStop(state);
                        sendMusicBroadcast(ACTION_STOP);
                        break;
                    case PlaybackStateCompat.STATE_ERROR:
                        onActionStop(state);
                        sendMusicBroadcast(ACTION_DIMINISH_LOADING);
                        sendMusicBroadcast(ACTION_ERROR);
                        break;
                    case PlaybackStateCompat.STATE_FAST_FORWARDING:
                        break;
                    case PlaybackStateCompat.STATE_NONE:
                        break;
                    case PlaybackStateCompat.STATE_REWINDING:
                        break;
                    case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                        break;
                    case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                        break;
                    case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
                        break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpdateMetadata(YPYMediaPlayer.StreamInfo info) {
            sendMusicBroadcast(info != null ? ACTION_UPDATE_INFO : ACTION_RESET_INFO);
        }

        @Override
        public void onUpdateArtwork(String img) {
            sendMusicBroadcast(ACTION_UPDATE_COVER_ART, !TextUtils.isEmpty(img) ? img : "");
        }


        @Override
        public void onPrepareDone() {
            sendMusicBroadcast(ACTION_DIMINISH_LOADING);
        }

    }

    private void startMusicServiceAgain(PlaybackStateCompat state) {
        try {
            if (getSessionToken() != null) {
                Notification notification = mMediaNotification.getNotification(state, getSessionToken());
                startForeground(NOTIFICATION_ID, notification);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateNotificationForPause(PlaybackStateCompat state) {
        try {
            if (getSessionToken() != null) {
                mSeekHandler.removeCallbacksAndMessages(null);
                //stopForeground(false);
                Notification notification = mMediaNotification.getNotification(state, getSessionToken());
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void releaseWifiLock() {
        try {
            // we can also release the Wifi lock, if we're holding it
            if (mWifiLock != null && mWifiLock.isHeld()) {
                mWifiLock.release();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onActionStop(PlaybackStateCompat state) {
        try {
            this.releaseWifiLock();
            setUpNotification(state, getSessionToken());

            mHandlerPlayCount.removeCallbacksAndMessages(null);
            mHandlerSleep.removeCallbacksAndMessages(null);
            mSeekHandler.removeCallbacksAndMessages(null);
            stopForeground(true);
            stopSelf();
            YPYStreamManager.getInstance().onDestroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void seekTo15Seconds(int sign) {
        try {
            if (YPYStreamManager.getInstance().isPrepareDone() && sign != 0 && mPlaybackControl != null) {
                long currentPos = mPlaybackControl.getCurrentPosition();
                long duration = mPlaybackControl.getDuration();

                currentPos = currentPos + sign * DELTA_SEEK;
                if (currentPos <= 0) {
                    currentPos = 0;
                }
                if (currentPos > duration) {
                    currentPos = duration - 1000;
                }
                YPYLog.e("DCM", "===>seek to=" + currentPos + "==>duration=" + duration);
                mPlaybackControl.seekTo(currentPos);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setUpNotification() {
        setUpNotification(null, null);
    }

    private void setUpNotification(@Nullable PlaybackStateCompat state, @Nullable MediaSessionCompat.Token mToken) {
        try {
            //TODO DOBAO stupid code to fix android 9.0 because it called Action Stop which started foreground service
            if (IOUtils.isAndroid80()) {
                Notification notification = mMediaNotification.getNotification(state, mToken);
                startForeground(NOTIFICATION_ID, notification);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        onActionStop(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.releaseWifiLock();
        if (mPlaybackControl != null) {
            mPlaybackControl.stop();
        }
        if (mMediaSessionCompat != null) {
            mMediaSessionCompat.release();
            mMediaSessionCompat = null;
        }

    }

}
