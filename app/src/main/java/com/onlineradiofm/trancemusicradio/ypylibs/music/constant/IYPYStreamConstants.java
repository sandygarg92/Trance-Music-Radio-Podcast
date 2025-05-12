
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

package com.onlineradiofm.trancemusicradio.ypylibs.music.constant;


public interface IYPYStreamConstants {

    String KEY_VALUE = "value";
    String KEY_ACTION = "KEY_ACTION";

    String ACTION_BROADCAST_PLAYER = ".action.ACTION_BROADCAST_PLAYER";
    String ACTION_BUFFERING = ".action.ACTION_BUFFERING";
    String ACTION_LOADING = ".action.ACTION_LOADING";
    String ACTION_PLAY = ".action.ACTION_PLAY";
    String ACTION_STOP = ".action.ACTION_STOP";
    String ACTION_NEXT = ".action.ACTION_NEXT";
    String ACTION_PREVIOUS = ".action.ACTION_PREVIOUS";
    String ACTION_PAUSE = ".action.ACTION_PAUSE";
    String ACTION_TOGGLE_PLAYBACK = ".action.ACTION_TOGGLE_PLAYBACK";
    String ACTION_DIMINISH_LOADING = ".action.ACTION_DIMINISH_LOADING";
    String ACTION_ERROR = ".action.ACTION_ERROR";
    String ACTION_UPDATE_SLEEP_MODE = ".action.ACTION_UPDATE_SLEEP_MODE";
    String ACTION_UPDATE_POS = ".action.UPDATE_POS";
    String ACTION_UPDATE_COVER_ART = ".action.ACTION_UPDATE_COVER_ART";
    String ACTION_UPDATE_INFO = ".action.ACTION_UPDATE_INFO";
    String ACTION_CONNECTION_LOST = ".action.ACTION_CONNECTION_LOST";
    String ACTION_COMPLETE = ".action.ACTION_COMPLETE";
    String ACTION_RESET_INFO = ".action.ACTION_RESET_INFO";
    String ACTION_UPDATE_FAST_SEEK = ".action.ACTION_UPDATE_FAST_SEEK";
    String ACTION_UPDATE_NOTIFICATION = ".action.ACTION_UPDATE_NOTIFICATION";
    String ACTION_UPDATE_FAST = ".action.ACTION_UPDATE_FAST";
    long  DELTA_SEEK = 15000; // 15 seconds

    int NOTIFICATION_ID = 503;
    int ONE_MINUTE = 60000;
    int DESIRE_IMAGE_SIZE = 200;

    String TAG_MEDIA = "RadioAudioService";

    String ACTION_RECORD_START = ".action.ACTION_RECORD_START";
    String ACTION_RECORD_STOP = ".action.ACTION_RECORD_STOP";
    String ACTION_RECORD_ERROR_UNKNOWN = ".action.ACTION_RECORD_ERROR_UNKNOWN";
    String ACTION_RECORD_ERROR_SD = ".action.ACTION_RECORD_ERROR_SD";
    String ACTION_RECORD_ERROR_SHORT_TIME = ".action.ACTION_RECORD_ERROR_SHORT_TIME";
    String ACTION_RECORD_FINISH = ".action.ACTION_RECORD_FINISH";
    String ACTION_RECORD_MAXIMUM = ".action.ACTION_RECORD_MAXIMUM";




}
