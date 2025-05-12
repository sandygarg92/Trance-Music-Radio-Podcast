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

package com.onlineradiofm.trancemusicradio.dataMng;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.model.GenreModel;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.cache.YPYCacheDataModel;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */

public class TotalDataManager implements IRadioConstants {

    private static TotalDataManager totalDataManager;
    private YPYCacheDataModel mYPYCacheModel;
    private String iamTitle;
    private String iamMsg;
    private String iamImage;
    private String iamCtaBtn;
    private String iamCtaType;
    private String iamCtaValue;
    private long iamCtaFreq;
    private long iamOpenAdFreq;
    private boolean isShowingInApp;

    public static TotalDataManager getInstance(Context mContext) {
        if (totalDataManager == null) {
            totalDataManager = new TotalDataManager(mContext);
        }
        return totalDataManager;
    }

    private TotalDataManager(Context mContext) {
        mYPYCacheModel = new YPYCacheDataModel(() -> getDirectoryTemp(mContext));
        mYPYCacheModel.addSaveMode(TYPE_TAB_LIVE, new TypeToken<ArrayList<RadioModel>>() {
        }.getType());
        mYPYCacheModel.addSaveMode(TYPE_TAB_SEARCH, new TypeToken<ArrayList<GenreModel>>() {
        }.getType());
        mYPYCacheModel.addSaveMode(TYPE_TAB_FAVORITE, new TypeToken<ArrayList<RadioModel>>() {
        }.getType());
        mYPYCacheModel.addSaveMode(TYPE_USER_FAV_RADIOS, new TypeToken<ArrayList<RadioModel>>() {
        }.getType());
        mYPYCacheModel.addSaveMode(TYPE_MY_DOWNLOAD, new TypeToken<ArrayList<RadioModel>>() {
        }.getType());
        mYPYCacheModel.addSaveMode(TYPE_LAST_PLAYED_RADIO, new TypeToken<ArrayList<RadioModel>>() {
        }.getType());
    }

    public ArrayList<?> getListData(int id) {
        if (mYPYCacheModel != null) {
            if (id == TYPE_TAB_FAVORITE) {
                ArrayList<RadioModel> mListFav = (ArrayList<RadioModel>) mYPYCacheModel.getListCacheData(TYPE_TAB_FAVORITE);
                if (mListFav != null && mListFav.size() > 0) {
                    for (RadioModel model : mListFav) {
                        model.setFavorite(true);
                    }
                }
            }
            return mYPYCacheModel.getListCacheData(id);
        }
        return null;
    }

    public void readAllCache() {
        if (mYPYCacheModel != null) {
            mYPYCacheModel.readAllCache();
            ArrayList<RadioModel> mListFav = (ArrayList<RadioModel>) getListData(TYPE_TAB_FAVORITE);
            if (mListFav != null && mListFav.size() > 0) {
                for (RadioModel model : mListFav) {
                    model.setFavorite(true);
                }
            }
        }
    }

    public void onDestroy() {
        if (mYPYCacheModel != null) {
            mYPYCacheModel.onDestroy();
            mYPYCacheModel = null;
        }
        totalDataManager = null;
    }

    public void readCacheData(int id) {
        if (mYPYCacheModel != null) {
            mYPYCacheModel.readCacheData(id);
            if (id == TYPE_TAB_LIVE) {
                mYPYCacheModel.readCacheData(TYPE_TAB_FAVORITE);
                updateFavoriteForList((ArrayList<RadioModel>) getListData(TYPE_TAB_LIVE), TYPE_TAB_FAVORITE);
            }
        }
    }

    public void setListCacheData(int id, ArrayList<?> mListDatas) {
        if (mYPYCacheModel != null) {
            mYPYCacheModel.setListCacheData(id, mListDatas);
        }
    }

    public void saveListCacheModel(int id) {
        if (mYPYCacheModel != null) {
            mYPYCacheModel.saveCacheData(id);
        }
    }

    public void saveListCacheModelInThread(int id) {
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            saveListCacheModel(id);
        });

    }

    public void addModelToCache(int type, Object object) {
        if (mYPYCacheModel != null) {
            mYPYCacheModel.addModelInCache(type, 0, object);
        }
    }

    public boolean removeModelToCache(int type, Object object) {
        if (mYPYCacheModel != null) {
            return mYPYCacheModel.removeModelInCache(type, object);
        }
        return false;
    }

    public int updateFavoriteForId(ArrayList<RadioModel> listObject, long id, boolean isFavorite) {
        if (listObject != null && listObject.size() > 0) {
            for (AbstractModel baseModel : listObject) {
                if (baseModel.getId() == id) {
                    baseModel.setFavorite(isFavorite);
                    baseModel.setFavorite(isFavorite);
                    return listObject.indexOf(baseModel);
                }
            }
        }
        return -1;
    }


    public void updateFavoriteForList(ArrayList<RadioModel> listObject, int type) {
        ArrayList<RadioModel> listFavoriteObject = (ArrayList<RadioModel>) getListData(type);
        if (listObject != null && listObject.size() > 0) {
            for (RadioModel radioModel : listObject) {
                radioModel.setFavorite(isInFavoriteList(listFavoriteObject, radioModel.getId()));
            }
        }
    }

    private boolean isInFavoriteList(ArrayList<RadioModel> radioModels, long id) {
        try {
            if (radioModels != null && radioModels.size() > 0) {
                for (RadioModel trackModel : radioModels) {
                    if (trackModel.getId() == id) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isListEqual(ArrayList<? extends AbstractModel> mList1, ArrayList<? extends AbstractModel> mList2) {
        try {
            int size1 = mList1 != null ? mList1.size() : 0;
            int size2 = mList2 != null ? mList2.size() : 0;
            if (size2 > 0 && size1 == size2) {
                for (int i = 0; i < size1; i++) {
                    AbstractModel model1 = mList1.get(i);
                    AbstractModel model2 = mList2.get(i);
                    if (!model1.equals(model2)) {
                        return false;
                    }
                }
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public File getNewRecorded(Context mContext) {
        boolean isSkip = XRadioSettingManager.getSkipNow(mContext);
        if (isSkip && !IOUtils.isAndroid10()) {
            boolean isGranted = ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS);
            if (isGranted) {
                File mRoot = new File(Environment.getExternalStorageDirectory(), DIR_CACHE);
                if (!mRoot.exists()) {
                    mRoot.mkdirs();
                }
                return checkOrCreateFolder(mRoot, ANDROID9_DIR_RECORDS);
            }
        }
        return getOldDirRecorded(mContext);
    }

    public File getOldDirRecorded(Context mContext) {
        File mRoot = getDirectoryCached(mContext);
        return checkOrCreateFolder(mRoot, ANDROID9_DIR_RECORDS);
    }

    public File getNewDirDownloaded(Context mContext) {
        boolean isSkip = XRadioSettingManager.getSkipNow(mContext);
        if (isSkip && !IOUtils.isAndroid10()) {
            boolean isGranted = ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS);
            if (isGranted) {
                File mRoot = new File(Environment.getExternalStorageDirectory(), DIR_CACHE);
                if (!mRoot.exists()) {
                    mRoot.mkdirs();
                }
                return checkOrCreateFolder(mRoot, ANDROID9_DIR_DOWNLOADED);
            }
        }
        return getOldDirDownloaded(mContext);
    }

    public File getOldDirDownloaded(Context mContext) {
        File mRoot = getDirectoryCached(mContext);
        return checkOrCreateFolder(mRoot, ANDROID9_DIR_DOWNLOADED);
    }

    public boolean isFileDownloaded(@NonNull Context mContext, @NonNull RadioModel model) {
        return !TextUtils.isEmpty(getFileDownloaded(mContext, model));
    }

    public File getDownloadFileNoCheck(@NonNull Context mContext, @NonNull RadioModel model) {
        try {
            File oldRootDir = getOldDirDownloaded(mContext);
            if (oldRootDir != null) {
                String newNameFile;
                if (model.isOfflineFile()) {
                    newNameFile = model.getPath();
                }
                else {
                    newNameFile = model.getNameFileDownload();
                }
                File mOldFile = new File(oldRootDir, newNameFile);
                if (mOldFile.exists() && mOldFile.isFile()) {
                    return mOldFile;
                }
                File newRoot = getNewDirDownloaded(mContext);
                if (newRoot != null) {
                    File newFile = new File(newRoot, newNameFile);
                    if (newFile.exists() && newFile.isFile()) {
                        return newFile;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFileDownloaded(@NonNull Context mContext, @NonNull RadioModel model) {
        File desFile = getDownloadFileNoCheck(mContext, model);
        if (desFile != null && desFile.exists() && desFile.isFile()) {
            ArrayList<RadioModel> listDownloads = (ArrayList<RadioModel>) getListData(TYPE_MY_DOWNLOAD);
            if (listDownloads != null && listDownloads.size() > 0) {
                for (RadioModel model1 : listDownloads) {
                    if (model1.equals(model)) {
                        return desFile.getAbsolutePath();
                    }
                }
            }
        }
        return null;
    }

    public File getDirDownloadTemp(Context mContext) {
        File mRoot = getDirectoryCached(mContext);
        return checkOrCreateFolder(mRoot, DIR_TEMP);
    }

    private File getDirectoryTemp(Context mContext) {
        File mRoot = getDirectoryCached(mContext);
        return checkOrCreateFolder(mRoot, DIR_TEMP);
    }

    public File getDirectoryCached(Context mContext) {
        try {
            boolean isSkip = XRadioSettingManager.getSkipNow(mContext);
            if (!isSkip && !IOUtils.isAndroid10() && ApplicationUtils.hasSDcard()) {
                boolean isGranted = ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS);
                if (isGranted) {
                    return checkOrCreateFolder(Environment.getExternalStorageDirectory(), DIR_CACHE);
                }
            }
            File mFile = mContext.getExternalCacheDir();
            if (mFile == null) {
                mFile = mContext.getCacheDir();
            }
            return mFile;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File checkOrCreateFolder(@NonNull File mRoot, @NonNull String nameFolder) {
        try {
            final File mFile = new File(mRoot, nameFolder);
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
            return mFile;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getIamTitle() {
        return iamTitle;
    }

    public void setIamTitle(String iamTitle) {
        this.iamTitle = iamTitle;
    }

    public String getIamDesc() {
        return iamMsg;
    }

    public void setIamDesc(String iamMsg) {
        this.iamMsg = iamMsg;
    }

    public String getIamImage() {
        return iamImage;
    }

    public void setIamImage(String iamImage) {
        this.iamImage = iamImage;
    }

    public String getIamCtaBtn() {
        return iamCtaBtn;
    }

    public void setIamCtaBtn(String iamCtaBtn) {
        this.iamCtaBtn = iamCtaBtn;
    }

    public String getIamCtaType() {
        return iamCtaType;
    }

    public void setIamCtaType(String iamCtaType) {
        this.iamCtaType = iamCtaType;
    }

    public String getIamCtaValue() {
        return iamCtaValue;
    }

    public void setIamCtaValue(String iamCtaValue) {
        this.iamCtaValue = iamCtaValue;
    }

    public long getIamCtaFreq() {
        return iamCtaFreq;
    }

    public void setIamCtaFreq(long iamCtaFreq) {
        this.iamCtaFreq = iamCtaFreq;
    }

    public long getIamOpenAdFreq() {
        return iamOpenAdFreq;
    }

    public void setIamOpenAdFreq(long iamOpenAdFreq) {
        this.iamOpenAdFreq = iamOpenAdFreq;
    }

    public boolean isShowingInApp() {
        return isShowingInApp;
    }

    public void setShowingInApp(boolean showingInApp) {
        isShowingInApp = showingInApp;
    }
}
