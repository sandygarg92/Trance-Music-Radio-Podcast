package com.onlineradiofm.trancemusicradio.ypylibs.googlecast;

import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.onlineradiofm.trancemusicradio.ypylibs.activity.YPYFragmentActivity;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 11/10/18.
 */
public class YPYCastManager {

    private final YPYFragmentActivity mContext;
    private final String mCastInfo;
    private final int mOverlayColorId;
    private final CastContext mCastContext;
    private final CastStateListener mCastStateListener;
    private CastSession mCastSession;
    private IntroductoryOverlay mIntroductoryOverlay;
    private final SessionManagerListener<CastSession> mSessionManagerListener;

    private final Handler mHandlerOverlay;
    private MenuItem mMediaRouteMenuItem;

    public YPYCastManager(@NonNull YPYFragmentActivity mContext, @NonNull String castIntro, @ColorRes int resColorId, @NonNull SessionManagerListener<CastSession> mSessionMng) {
        this.mContext = mContext;
        this.mCastInfo = castIntro;
        this.mOverlayColorId = resColorId;
        this.mCastContext = CastContext.getSharedInstance(mContext);
        this.mSessionManagerListener = mSessionMng;
        this.mHandlerOverlay = new Handler();
        this.mCastStateListener = newState -> {
            if (newState != CastState.NO_DEVICES_AVAILABLE) {
                showIntroductoryOverlay();
            }
        };
    }

    public YPYCastManager(@NonNull YPYFragmentActivity mContext, @NonNull String castIntro, @ColorRes int resColorId) {
        this.mContext = mContext;
        this.mCastInfo = castIntro;
        this.mOverlayColorId = resColorId;
        this.mCastContext = CastContext.getSharedInstance(mContext);
        this.mSessionManagerListener = new MySessionManagerListener();
        this.mHandlerOverlay = new Handler();
        this.mCastStateListener = newState -> {
            if (newState != CastState.NO_DEVICES_AVAILABLE) {
                showIntroductoryOverlay();
            }
        };
    }

    public void setUpMediaRoutMenuItem(@NonNull Menu menu, @IdRes int itemId) {
        try {
            this.mMediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(mContext, menu, itemId);
            this.showIntroductoryOverlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MenuItem getMediaRouteMenuItem() {
        return mMediaRouteMenuItem;
    }

    public void onCastResume() {
        try {
            mCastContext.addCastStateListener(mCastStateListener);
            mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
            if (mCastSession == null) {
                mCastSession = CastContext.getSharedInstance(mContext).getSessionManager()
                        .getCurrentCastSession();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onCastPause() {
        try {
            mCastContext.removeCastStateListener(mCastStateListener);
            mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CastSession getCastSession() {
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(mContext).getSessionManager()
                    .getCurrentCastSession();
        }
        return mCastSession;
    }

    public boolean isCastConnected() {
        return mCastSession != null && mCastSession.isConnected();
    }

    public CastDevice getCastDevice() {
        return mCastSession != null ? mCastSession.getCastDevice() : null;
    }

    public String getCastDeviceName() {
        try {
            CastDevice mCastDevice = getCastDevice();
            if (mCastDevice != null) {
                String name = mCastDevice.getFriendlyName();
                if (TextUtils.isEmpty(name)) {
                    name = mCastDevice.getModelName();
                    if (TextUtils.isEmpty(name)) {
                        name = mCastDevice.getDeviceId();
                    }
                }
                return name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showIntroductoryOverlay() {
        try {
            if (mIntroductoryOverlay != null) {
                mIntroductoryOverlay.remove();
            }
            if (mMediaRouteMenuItem != null && mMediaRouteMenuItem.isVisible()) {
                mHandlerOverlay.removeCallbacksAndMessages(null);
                mHandlerOverlay.post(() -> {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(mContext, mMediaRouteMenuItem)
                            .setTitleText(mCastInfo)
                            .setOverlayColor(mOverlayColorId)
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    () -> mIntroductoryOverlay = null)
                            .build();
                    mIntroductoryOverlay.show();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onCastDestroy() {
        mHandlerOverlay.removeCallbacksAndMessages(null);
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
            mIntroductoryOverlay = null;
        }
    }


    private class MySessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionEnded(CastSession session, int error) {
            if (session == mCastSession) {
                mCastSession = null;
            }
            mContext.invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            mCastSession = session;
            mContext.invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            mCastSession = session;
            mContext.invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarting(CastSession session) {
        }

        @Override
        public void onSessionStartFailed(CastSession session, int error) {

        }

        @Override
        public void onSessionEnding(CastSession session) {
        }

        @Override
        public void onSessionResuming(CastSession session, String sessionId) {
        }

        @Override
        public void onSessionResumeFailed(CastSession session, int error) {

        }

        @Override
        public void onSessionSuspended(CastSession session, int reason) {
        }

    }

    public RemoteMediaClient getRemoteMediaClient() {
        try {
            if (mCastContext != null) {
                CastSession castSession = mCastContext.getSessionManager().getCurrentCastSession();
                return isCastConnected() ? castSession.getRemoteMediaClient() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
