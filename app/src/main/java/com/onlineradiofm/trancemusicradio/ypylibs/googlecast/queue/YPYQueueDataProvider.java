/*
 * Copyright (C) 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onlineradiofm.trancemusicradio.ypylibs.googlecast.queue;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 11/10/18.
 */

public class YPYQueueDataProvider {

    private static final int INVALID_INDEX = -1;

    private final Context mAppContext;
    private final List<MediaQueueItem> mQueue = new CopyOnWriteArrayList<>();

    private static YPYQueueDataProvider mInstance;

    // Locks modification to the remove queue.
    private final Object mLock = new Object();

    private int mRepeatMode;
    private boolean mShuffle;
    private MediaQueueItem mCurrentIem;
    private OnQueueDataChangedListener mListener;
    private boolean mDetachedQueue = true;

    private MyRemoteMediaClientListener mRemoteListener;
    private MediaQueueItem mUpcomingItem;

    private YPYQueueDataProvider(Context context) {
        mAppContext = context.getApplicationContext();
        mRepeatMode = MediaStatus.REPEAT_MODE_REPEAT_OFF;

        mShuffle = false;
        mCurrentIem = null;
        MySessionManagerListener mSessionMngListener = new MySessionManagerListener();
        CastContext.getSharedInstance(mAppContext).getSessionManager().addSessionManagerListener(mSessionMngListener, CastSession.class);
        syncWithRemoteQueue();
    }


    public boolean isQueueDetached() {
        return mDetachedQueue;
    }

    public int getPositionByItemId(int itemId) {
        try {
            if (mQueue.isEmpty()) {
                return INVALID_INDEX;
            }
            for (int i = 0; i < mQueue.size(); i++) {
                if (mQueue.get(i).getItemId() == itemId) {
                    return i;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return INVALID_INDEX;
    }

    public static synchronized YPYQueueDataProvider getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new YPYQueueDataProvider(context);
        }
        return mInstance;
    }

    public void removeFromQueue(int position) {
        try {
            synchronized (mLock) {
                RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
                if (remoteMediaClient == null) {
                    return;
                }
                remoteMediaClient.queueRemoveItem(mQueue.get(position).getItemId(), null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void unregisterMediaCallback() {
        try {
            RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
            if (remoteMediaClient != null && mRemoteListener != null) {
                remoteMediaClient.unregisterCallback(mRemoteListener);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mRemoteListener = null;

    }

    public void removeAll() {
        try {
            synchronized (mLock) {
                if (mQueue.isEmpty()) {
                    return;
                }
                RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
                if (remoteMediaClient == null) {
                    return;
                }
                remoteMediaClient.stop();
                mQueue.clear();
                if (mListener != null) {
                    mListener.onQueueDataChanged();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    public int getCount() {
        return mQueue.size();
    }

    public MediaQueueItem getItem(int position) {
        return position < mQueue.size() && position >= 0 ? mQueue.get(position) : null;
    }

    public void clearQueue() {
        try {
            mQueue.clear();
            mDetachedQueue = true;
            mCurrentIem = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getRepeatMode() {
        return mRepeatMode;
    }

    public boolean isShuffleOn() {
        return mShuffle;
    }

    public MediaQueueItem getCurrentItem() {
        return mCurrentIem;
    }

    public int getCurrentItemId() {
        return mCurrentIem != null ? mCurrentIem.getItemId() : INVALID_INDEX;
    }

    public MediaQueueItem getUpcomingItem() {
        return mUpcomingItem;
    }


    public void setOnQueueDataChangedListener(OnQueueDataChangedListener listener) {
        mListener = listener;
    }

    public List<MediaQueueItem> getItems() {
        return mQueue;
    }

    /**
     * Listener notifies the data of the queue has changed.
     */
    public interface OnQueueDataChangedListener {
        void onQueueDataChanged();
    }

    private void syncWithRemoteQueue() {
        try {
            RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
            if (remoteMediaClient != null) {
                if (mRemoteListener == null) {
                    mRemoteListener = new MyRemoteMediaClientListener();
                    remoteMediaClient.registerCallback(mRemoteListener);
                }
                MediaStatus mediaStatus = remoteMediaClient.getMediaStatus();
                if (mediaStatus != null) {
                    List<MediaQueueItem> items = mediaStatus.getQueueItems();
                    if (items != null && !items.isEmpty()) {
                        mQueue.clear();
                        mQueue.addAll(items);
                        mRepeatMode = mediaStatus.getQueueRepeatMode();
                        mCurrentIem = mediaStatus.getQueueItemById(mediaStatus.getCurrentItemId());
                        mDetachedQueue = false;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class MySessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            syncWithRemoteQueue();
        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            syncWithRemoteQueue();
        }

        @Override
        public void onSessionEnded(CastSession session, int error) {
            unregisterMediaCallback();
            try {
                clearQueue();
                if (mListener != null) {
                    mListener.onQueueDataChanged();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
            unregisterMediaCallback();
        }
    }

    private class MyRemoteMediaClientListener extends RemoteMediaClient.Callback {

        @Override
        public void onPreloadStatusUpdated() {
            try {
                RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
                if (remoteMediaClient == null) {
                    return;
                }
                MediaStatus mediaStatus = remoteMediaClient.getMediaStatus();
                if (mediaStatus == null) {
                    return;
                }
                mUpcomingItem = mediaStatus.getQueueItemById(mediaStatus.getPreloadedItemId());
                Log.d("DCM", "onRemoteMediaPreloadStatusUpdated() with item=" + mUpcomingItem);
                if (mListener != null) {
                    mListener.onQueueDataChanged();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onQueueStatusUpdated() {
            updateMediaQueue();
            if (mListener != null) {
                mListener.onQueueDataChanged();
            }
        }

        @Override
        public void onStatusUpdated() {
            updateMediaQueue();
            if (mListener != null) {
                mListener.onQueueDataChanged();
            }
        }

        private void updateMediaQueue() {
            try {
                RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
                MediaStatus mediaStatus;
                List<MediaQueueItem> queueItems = null;
                if (remoteMediaClient != null) {
                    mediaStatus = remoteMediaClient.getMediaStatus();
                    if (mediaStatus != null) {
                        queueItems = mediaStatus.getQueueItems();
                        mRepeatMode = mediaStatus.getQueueRepeatMode();
                        mCurrentIem = mediaStatus.getQueueItemById(mediaStatus.getCurrentItemId());
                    }
                }
                mQueue.clear();
                if (queueItems != null) {
                    Log.e("DCM", "Queue is updated with a list of size: " + queueItems.size());
                    if (queueItems.size() > 0) {
                        mQueue.addAll(queueItems);
                        mDetachedQueue = false;
                    }
                    else {
                        mDetachedQueue = true;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private RemoteMediaClient getRemoteMediaClient() {
        try {
            CastSession castSession = CastContext.getSharedInstance(mAppContext).getSessionManager().getCurrentCastSession();
            if (castSession != null && castSession.isConnected()) {
                return castSession.getRemoteMediaClient();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
