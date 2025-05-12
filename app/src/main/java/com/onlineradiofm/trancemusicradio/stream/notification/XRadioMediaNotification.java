package com.onlineradiofm.trancemusicradio.stream.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.stream.service.XRadioAudioService;
import com.onlineradiofm.trancemusicradio.ypylibs.music.constant.IYPYStreamConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.music.control.YPYPlaybackControl;
import com.onlineradiofm.trancemusicradio.ypylibs.music.model.YPYMusicModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2019-06-06.
 */
public class XRadioMediaNotification implements IYPYStreamConstants {

    private static final String ANDROID8_CHANNEL_ONE_NAME = "PodcastX_Channel";
    private static final int REQUEST_CODE = 501;
    private static final String TAG = XRadioMediaNotification.class.getSimpleName();

    private final XRadioAudioService mService;

    private final NotificationCompat.Action mPlayAction;
    private final NotificationCompat.Action mPauseAction;
    private final NotificationCompat.Action mSkipNextAction;
    private final NotificationCompat.Action mPreviousAction;
    private final NotificationCompat.Action mCloseAction;

    private final NotificationManager mNotificationManager;
    private final YPYPlaybackControl mPlaybackAdapter;

    public XRadioMediaNotification(@NonNull XRadioAudioService service, @NonNull YPYPlaybackControl mPlayback) {
        this.mService = service;
        this.mPlaybackAdapter = mPlayback;

        this.mNotificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mPlayAction = new NotificationCompat.Action(R.drawable.ic_play_arrow_white_36dp, "Play",
                buildMediaButtonPendingIntent(mService, PlaybackStateCompat.ACTION_PLAY));

        this.mPauseAction = new NotificationCompat.Action(R.drawable.ic_pause_white_36dp, "Pause",
                buildMediaButtonPendingIntent(mService, PlaybackStateCompat.ACTION_PAUSE));

        this.mSkipNextAction = new NotificationCompat.Action(R.drawable.ic_skip_next_white_36dp, "SkipNext",
                buildMediaButtonPendingIntent(mService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        this.mCloseAction = new NotificationCompat.Action(R.drawable.ic_close_white_36dp, "Close",
                buildMediaButtonPendingIntent(mService, PlaybackStateCompat.ACTION_STOP));

        this.mPreviousAction = new NotificationCompat.Action(R.drawable.ic_skip_previous_white_36dp, "SkipPrevious",
                buildMediaButtonPendingIntent(mService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }


    public Notification getNotification(@Nullable PlaybackStateCompat state, @Nullable MediaSessionCompat.Token token) {
        NotificationCompat.Builder builder = buildNotification(state, token);
        return builder.build();
    }

    private NotificationCompat.Builder buildNotification(@Nullable PlaybackStateCompat state, @Nullable MediaSessionCompat.Token token) {
        String packageName = mService.getPackageName();

        String mChannelId = packageName + ".N2";
        createChannel(mChannelId);

        YPYMusicModel mCurrentTrack = mPlaybackAdapter.getCurrentMedia();

        String contentTitle = mCurrentTrack != null && !TextUtils.isEmpty(mCurrentTrack.getName())
                ? mCurrentTrack.getName() : mService.getString(R.string.app_name);

        String contentText = mCurrentTrack != null && !TextUtils.isEmpty(mCurrentTrack.getArtist())
                ? mCurrentTrack.getArtist() : mService.getString(R.string.title_unknown);

        PendingIntent mStopIntent = buildMediaButtonPendingIntent(mService, PlaybackStateCompat.ACTION_STOP);

        androidx.media.app.NotificationCompat.MediaStyle mediaStyle = new androidx.media.app.NotificationCompat.MediaStyle();
        mediaStyle.setShowActionsInCompactView(0, 1, 2, 3);
        mediaStyle.setCancelButtonIntent(mStopIntent);

        if (token != null) {
            mediaStyle.setMediaSession(token);
        }

        mediaStyle.setShowCancelButton(true);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mService, mChannelId);
        mBuilder.setStyle(mediaStyle);
        mBuilder.setAutoCancel(false);
        mBuilder.setDeleteIntent(mStopIntent);
        mBuilder.setContentTitle(contentTitle);
        mBuilder.setContentText(contentText);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setSmallIcon(R.drawable.ic_notification_24dp);
        int mColorNoti = mPlaybackAdapter.getColorNotification();
        if (mColorNoti != 0) {
            mBuilder.setColor(mColorNoti);
        }
        else {
            mBuilder.setColor(mService.getResources().getColor(R.color.color_noti_background));
        }
        mBuilder.setColorized(true);
        mBuilder.setShowWhen(true);

        Bitmap mBitmapTrack = mPlaybackAdapter.getBitmapTrack();
        try {
            if (mBitmapTrack != null && !mBitmapTrack.isRecycled()) {
                mBuilder.setLargeIcon(mBitmapTrack);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        boolean isPlaying = state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING;
        if (state != null) {
            // If  previous action is enabled.
            if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
                mBuilder.addAction(mPreviousAction);
            }
            mBuilder.addAction(isPlaying ? mPauseAction : mPlayAction);
            // If next action is enabled.
            if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
                mBuilder.addAction(mSkipNextAction);
            }
            // If next action is enabled.
            if ((state.getActions() & PlaybackStateCompat.ACTION_STOP) != 0) {
                mBuilder.addAction(mCloseAction);
            }
        }
        else {
            mBuilder.addAction(mPreviousAction);
            mBuilder.addAction(mPlayAction);
            mBuilder.addAction(mSkipNextAction);
            mBuilder.addAction(mCloseAction);
        }
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setOngoing(isPlaying);
        mBuilder.setContentIntent(createContentIntent());

        return mBuilder;

    }


    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mService, MainActivity.class);
        openUI.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flag = IOUtils.isAndroid12() ? (PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT) : PendingIntent.FLAG_UPDATE_CURRENT;
        return PendingIntent.getActivity(mService.getApplicationContext(), REQUEST_CODE, openUI, flag);
    }

    private void createChannel(String channelId) {
        if (IOUtils.isAndroid80()) {
            try {
                if (mNotificationManager.getNotificationChannel(channelId) == null) {
                    String CHANNEL_ONE_NAME = mService.getPackageName() + ANDROID8_CHANNEL_ONE_NAME;
                    NotificationChannel notificationChannel = new NotificationChannel(channelId,
                            CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_LOW);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.setShowBadge(true);
                    mNotificationManager.createNotificationChannel(notificationChannel);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static PendingIntent buildMediaButtonPendingIntent(Context context,
                                                              @PlaybackStateCompat.MediaKeyAction long action) {
        ComponentName mbrComponent = getMediaButtonReceiverComponent(context);
        if (mbrComponent == null) {
            Log.w(TAG, "A unique media button receiver could not be found in the given context, so "
                    + "couldn't build a pending intent.");
            return null;
        }
        return buildMediaButtonPendingIntent(context, mbrComponent, action);
    }

    //TODO STUPID FUCK For android 12
    public static PendingIntent buildMediaButtonPendingIntent(Context context,
                                                              ComponentName mbrComponent, @PlaybackStateCompat.MediaKeyAction long action) {
        if (mbrComponent == null) {
            Log.w(TAG, "The component name of media button receiver should be provided.");
            return null;
        }
        int keyCode = PlaybackStateCompat.toKeyCode(action);
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            Log.w(TAG,
                    "Cannot build a media button pending intent with the given action: " + action);
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(mbrComponent);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
        return PendingIntent.getBroadcast(context, keyCode, intent, IOUtils.isAndroid12() ? PendingIntent.FLAG_IMMUTABLE : 0);
    }

    //TODO STUPID FUCK For android 12
    public static ComponentName getMediaButtonReceiverComponent(Context context) {
        Intent queryIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        queryIntent.setPackage(context.getPackageName());
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryBroadcastReceivers(queryIntent, 0);
        if (resolveInfos.size() == 1) {
            ResolveInfo resolveInfo = resolveInfos.get(0);
            return new ComponentName(resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name);
        }
        else if (resolveInfos.size() > 1) {
            Log.w(TAG, "More than one BroadcastReceiver that handles "
                    + Intent.ACTION_MEDIA_BUTTON + " was found, returning null.");
        }
        return null;
    }
}
