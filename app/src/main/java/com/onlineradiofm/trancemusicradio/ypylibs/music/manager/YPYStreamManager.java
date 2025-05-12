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

package com.onlineradiofm.trancemusicradio.ypylibs.music.manager;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.ypylibs.music.mediaplayer.YPYMediaPlayer;
import com.onlineradiofm.trancemusicradio.ypylibs.music.model.YPYMusicModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/19/17.
 */

public class YPYStreamManager {

    private static YPYStreamManager musicManager;
    private ArrayList<? extends YPYMusicModel> listTrackModels;

    private int currentIndex = -1;
    private YPYMusicModel currentModel;
    private boolean isLoading;
    private YPYMediaPlayer ypyMediaPlayer;
    private MediaPlayer nativeMediaPlayer;
    private YPYMediaPlayer.StreamInfo streamInfo;
    private boolean isRecordingFile;
    private boolean isOfflineList;

    private Equalizer equalizer;
    private BassBoost bassBoost;
    private Virtualizer virtualizer;

    public static YPYStreamManager getInstance() {
        if (musicManager == null) {
            musicManager = new YPYStreamManager();
        }
        return musicManager;
    }

    private YPYStreamManager() {

    }

    public void onDestroy() {
        if (listTrackModels != null) {
            listTrackModels.clear();
            listTrackModels = null;
        }
        isRecordingFile = false;
        isOfflineList = false;
        currentIndex = -1;
        currentModel = null;
        musicManager = null;
    }

    public ArrayList<? extends YPYMusicModel> getListTrackModels() {
        return listTrackModels;
    }

    public boolean setCurrentModel(YPYMusicModel trackModel) {
        if (listTrackModels != null && listTrackModels.size() > 0) {
            for (YPYMusicModel model : listTrackModels) {
                if (model.equals(trackModel)) {
                    currentModel = model;
                    currentIndex = listTrackModels.indexOf(model);
                    return true;
                }
            }
        }
        return false;
    }

    public void onItemMoved(int fromPosition, int toPosition) {
        try {
            if (listTrackModels != null && listTrackModels.size() > 0) {
                Collections.swap(listTrackModels, fromPosition, toPosition);
                currentIndex = currentModel != null ? listTrackModels.indexOf(currentModel) : 0;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListTrackModels(ArrayList<? extends YPYMusicModel> listModels) {
        if (this.listTrackModels != null) {
            this.listTrackModels.clear();
            this.listTrackModels = null;
        }
        this.currentIndex = -1;
        this.currentModel = null;
        this.isRecordingFile = false;
        this.listTrackModels = listModels;
        int size = listModels != null ? listModels.size() : 0;
        if (size > 0) {
            currentIndex = 0;
            currentModel = listModels.get(currentIndex);
            isOfflineList = true;
            for (YPYMusicModel model : listModels) {
                if (!model.isOfflineModel()) {
                    isOfflineList = false;
                    break;
                }
            }
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getNumberTrack() {
        return listTrackModels != null ? listTrackModels.size() : 0;
    }

    public YPYMusicModel getCurrentModel() {
        return currentModel;
    }

    public YPYMusicModel nextPlay(Context mContext, boolean isComplete) {
        return changeAction(mContext, 1, isComplete);
    }

    public YPYMusicModel prevPlay(Context mContext) {
        return changeAction(mContext, -1, false);
    }

    private YPYMusicModel changeAction(Context mContext, int count, boolean isComplete) {
        int size = listTrackModels != null ? listTrackModels.size() : 0;
        if (size > 0) {
            currentIndex = currentIndex + count;
            if (currentIndex >= size) {
                currentIndex = 0;
            }
            else if (currentIndex < 0) {
                currentIndex = size - 1;
            }
            currentModel = listTrackModels.get(currentIndex);
            return currentModel;
        }
        return null;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isPlaying() {
        try {
            if (nativeMediaPlayer != null && nativeMediaPlayer.isPlaying()) {
                return true;
            }
            return ypyMediaPlayer != null && ypyMediaPlayer.isPlaying();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPrepareDone() {
        try {
            int sessionId = 0;
            if (nativeMediaPlayer != null) {
                sessionId = nativeMediaPlayer.getAudioSessionId();
            }
            if (sessionId != 0) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ypyMediaPlayer != null;
    }

    public boolean isEndOfList() {
        int size = listTrackModels != null ? listTrackModels.size() : 0;
        if (size > 0) {
            return isOfflineList && currentIndex == size - 1;
        }
        return false;
    }

    public void setYpyMediaPlayer(YPYMediaPlayer ypyMediaPlayer) {
        this.ypyMediaPlayer = ypyMediaPlayer;
    }

    public void setNativeMediaPlayer(MediaPlayer nativeMediaPlayer) {
        this.nativeMediaPlayer = nativeMediaPlayer;
    }

    public void resetMedia() {
        this.releaseEffect();
        this.nativeMediaPlayer = null;
        this.ypyMediaPlayer = null;
        this.streamInfo = null;
    }

    public void releaseEffect(){
        try {
            if(equalizer!=null){
                equalizer.release();
                equalizer=null;
            }
            if(bassBoost!=null){
                bassBoost.release();
                bassBoost=null;
            }
            if(virtualizer!=null){
                virtualizer.release();
                virtualizer=null;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHavingList() {
        return listTrackModels != null && listTrackModels.size() > 0;
    }

    public YPYMediaPlayer.StreamInfo getStreamInfo() {
        return streamInfo;
    }

    public void setStreamInfo(YPYMediaPlayer.StreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    public boolean isRecordingFile() {
        return isRecordingFile;
    }

    public void setRecordingFile(boolean recordingFile) {
        isRecordingFile = recordingFile;
    }

    public void removeMusicModel(String path){
        try{
            if(listTrackModels!=null && listTrackModels.size()>0 && !TextUtils.isEmpty(path)){
                Iterator<? extends YPYMusicModel> mIterator = listTrackModels.iterator();
                while (mIterator.hasNext()){
                    YPYMusicModel model= mIterator.next();
                    if(model.isOfflineModel() && model.getPath().equalsIgnoreCase(path)){
                        mIterator.remove();
                        currentIndex--;
                        if(currentIndex<0){
                            currentIndex=0;
                        }
                        break;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public void updateMusicModel(long id, String name, String path){
        try{
            if(listTrackModels!=null && listTrackModels.size()>0 && !TextUtils.isEmpty(path)){
                for (YPYMusicModel model : listTrackModels) {
                    if (model.isOfflineModel() && model.getId() == id) {
                        model.setName(name);
                        model.setPath(path);
                        break;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public void removeOfflineModel(String path){
        try{
            if(listTrackModels !=null && listTrackModels.size()>0 && !TextUtils.isEmpty(path)){
                Iterator<? extends YPYMusicModel> mIterator = listTrackModels.iterator();
                while (mIterator.hasNext()){
                    YPYMusicModel model= mIterator.next();
                    if(model.isOfflineModel() && model.getPath().equalsIgnoreCase(path)){
                        mIterator.remove();
                        currentIndex--;
                        if(currentIndex<0){
                            currentIndex=0;
                        }
                        break;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public int getAudioSession(){
        try{
            if (ypyMediaPlayer != null) {
                return ypyMediaPlayer.getAudioSession();
            }
            if(nativeMediaPlayer!=null){
                return nativeMediaPlayer.getAudioSessionId();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public Object getMediaPlayer() {
        if (ypyMediaPlayer != null) {
            return ypyMediaPlayer;
        }
        return nativeMediaPlayer;
    }

    public Equalizer getEqualizer() {
        return equalizer;
    }

    public void setEqualizer(Equalizer equalizer) {
        this.equalizer = equalizer;
    }

    public BassBoost getBassBoost() {
        return bassBoost;
    }

    public void setBassBoost(BassBoost bassBoost) {
        this.bassBoost = bassBoost;
    }

    public Virtualizer getVirtualizer() {
        return virtualizer;
    }

    public void setVirtualizer(Virtualizer virtualizer) {
        this.virtualizer = virtualizer;
    }

}
