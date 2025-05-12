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

package com.onlineradiofm.trancemusicradio.ypylibs.executor;

import android.os.Process;

import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */
public class YPYExecutorSupplier {
    public static final String TAG =YPYExecutorSupplier.class.getSimpleName();
    /*
    * Number of cores to decide the number of threads
    */
    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /*
    * thread pool com.makingmixes.app.executor for background tasks
    */
    private ThreadPoolExecutor mForBackgroundTasks;
    /*
    * thread pool com.makingmixes.app.executor for light weight background tasks
    */
    private ThreadPoolExecutor mForLightWeightBackgroundTasks;
    /*
    * thread pool com.makingmixes.app.executor for main thread tasks
    */
    private MainThreadExecutor mMainThreadExecutor;
    /*
    * an instance of YPYExecutorSupplier
    */
    private static YPYExecutorSupplier sInstance;
    private PriorityThreadFactory mBgThreadPiority;

    /*
    * returns the instance of YPYExecutorSupplier
    */
    public static YPYExecutorSupplier getInstance() {
        if (sInstance == null) {
            synchronized (YPYExecutorSupplier.class) {
                sInstance = new YPYExecutorSupplier();
            }
        }
        return sInstance;
    }

    /*
    * constructor for  YPYExecutorSupplier
    */
    private YPYExecutorSupplier() {
        YPYLog.d(TAG,"===================>number core="+NUMBER_OF_CORES);
        // setting the thread factory
        mBgThreadPiority = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        // setting the thread pool com.makingmixes.app.executor for mForBackgroundTasks;
        mForBackgroundTasks = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                mBgThreadPiority
        );

        // setting the thread pool com.makingmixes.app.executor for mForLightWeightBackgroundTasks;
        mForLightWeightBackgroundTasks = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                mBgThreadPiority
        );

        // setting the thread pool com.makingmixes.app.executor for mMainThreadExecutor;
        mMainThreadExecutor = new MainThreadExecutor();
    }

    /*
    * returns the thread pool com.makingmixes.app.executor for background task
    */

    public ThreadPoolExecutor forBackgroundTasks() {
        return mForBackgroundTasks;
    }

    /*
    * returns the thread pool com.makingmixes.app.executor for light weight background task
    */
    public ThreadPoolExecutor forLightWeightBackgroundTasks() {
        return mForLightWeightBackgroundTasks;
    }

    /*
    * returns the thread pool com.makingmixes.app.executor for main thread task
    */
    public Executor forMainThreadTasks() {
        return mMainThreadExecutor;
    }

    public void onDestroy() {
        try {
            if (mBgThreadPiority != null) {
                mBgThreadPiority.onDestroy();
                mBgThreadPiority=null;
            }
            if (mMainThreadExecutor != null) {
                mMainThreadExecutor.onDestroy();
                mMainThreadExecutor=null;
            }
            mForLightWeightBackgroundTasks=null;
            mForBackgroundTasks=null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        sInstance=null;

    }
}
