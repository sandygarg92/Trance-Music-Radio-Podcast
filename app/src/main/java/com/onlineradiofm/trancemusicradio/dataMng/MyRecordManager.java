package com.onlineradiofm.trancemusicradio.dataMng;

import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.ANDROID_10_DIR_PUBLIC_RECORD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.DATE_PATTERN;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.FORMAT_SAVED;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.LIST_STORAGE_PERMISSIONS;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.LIST_STORAGE_PERMISSIONS_13;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.LIST_STORAGE_PERMISSIONS_14;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.RECORD_TEMP_FILE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.fragment.FragmentTabLibrary;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.io.File;
import java.util.Date;

public class MyRecordManager {

    @NonNull
    private final MainActivity context;

    @NonNull
    private final FragmentTabLibrary fragmentLibraries;

    @NonNull
    private final TotalDataManager totalMng;

    public MyRecordManager(@NonNull MainActivity context,
                           @NonNull FragmentTabLibrary fragmentLibraries) {
        this.context = context;
        this.fragmentLibraries = fragmentLibraries;
        this.totalMng = TotalDataManager.getInstance(context);
    }

    @SuppressLint("NewApi")
    public void startSaveFile(IYPYCallback callback) {
        context.showProgressDialog();
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            Uri uri;
            if (IOUtils.isAndroid14())
                uri = saveFileTemp(LIST_STORAGE_PERMISSIONS_14);
            else if (IOUtils.isAndroid13())
                uri = saveFileTemp(LIST_STORAGE_PERMISSIONS_13);
            else
                uri=saveFileTemp(LIST_STORAGE_PERMISSIONS);
            YPYLog.e("DCM", "=======>startSaveFile=" + uri);
            context.runOnUiThread(() -> {
                context.dismissProgressDialog();
                context.showToast(uri != null ? R.string.info_save_file_success : R.string.info_save_file_error);
                if (uri != null) {
                    if (fragmentLibraries.isLoadingData()) {
                        fragmentLibraries.setLoadingData(false);
                        if (callback != null) {
                            callback.onAction();
                        }
                    }
                }
            });
        });
    }


    @SuppressLint("NewApi")
    public void deleteFileTemp(String[] permissions) {
        try {
            if (ApplicationUtils.isGrantAllPermission(context, permissions)) {
                File mDirFile = totalMng.getDirDownloadTemp(context);
                if (mDirFile != null) {
                    File mTempRecordFile = new File(mDirFile, RECORD_TEMP_FILE);
                    if (mTempRecordFile.isFile()) {
                        mTempRecordFile.delete();
                    }
                }
            }
            else {
                Log.d("hello","");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("NewApi")
    private Uri saveFileTemp(String[] permissions) {
        try {
            if (ApplicationUtils.isGrantAllPermission(context, permissions)) {
                File mRecordTemp = totalMng.getDirDownloadTemp(context);
                File mRecordDir = totalMng.getNewRecorded(context);
                if (mRecordDir != null && mRecordTemp != null) {
                    File tmpFile = new File(mRecordTemp, RECORD_TEMP_FILE);
                    if (tmpFile.exists() && tmpFile.isFile()) {
                        return saveFileToGallery(tmpFile);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private Uri saveFileToGallery(@NonNull File tmpFile) {
        try {
            Date mDate = new Date();
            String mStrDate = DateFormat.format(DATE_PATTERN, mDate.getTime()).toString();
            String fileName = String.format(context.getString(R.string.format_recorded_file), mStrDate) + FORMAT_SAVED;
            ContentValues values = getContentValues(fileName);
            Uri uri = null;
            if (IOUtils.isAndroid10()) {
                uri = MediaStoreManager.insertFileToGalleryAndroid10(context, values, tmpFile.getAbsolutePath());
                //delete the cache file
                tmpFile.delete();
            }
            else {
                //if we save to media store we need to change file name to save information
                File parent = totalMng.getNewRecorded(context);
                if (parent != null && parent.exists()) {
                    File mediaStoreFile = new File(parent, fileName);
                    boolean isRenamed = tmpFile.renameTo(mediaStoreFile);
                    YPYLog.e("DCM", "=======>saveFileToGallery rename =" + isRenamed);
                    if (isRenamed) {
                        values.put(MediaStore.Audio.Media.DATA, mediaStoreFile.getAbsolutePath());
                        uri = MediaStoreManager.insertFileToGallery(context, values, mediaStoreFile.getAbsolutePath());
                    }
                }
            }
            return uri;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ContentValues getContentValues(@NonNull String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        if (IOUtils.isAndroid10()) {
            String relativePath = Environment.DIRECTORY_MUSIC + "/" + ANDROID_10_DIR_PUBLIC_RECORD;
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath);
        }
        else {
            values.put(MediaStore.Audio.Media.ALBUM, ANDROID_10_DIR_PUBLIC_RECORD);
        }
        return values;
    }


}
