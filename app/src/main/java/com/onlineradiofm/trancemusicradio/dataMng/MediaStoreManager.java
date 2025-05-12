package com.onlineradiofm.trancemusicradio.dataMng;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.ANDROID9_DIR_DOWNLOADED;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.ANDROID9_DIR_RECORDS;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.ANDROID_10_DIR_PUBLIC_DOWNLOAD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.ANDROID_10_DIR_PUBLIC_RECORD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.DIR_CACHE;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.FORMAT_SAVED;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.TYPE_MY_DOWNLOAD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.TYPE_TAB_FAVORITE;

public class MediaStoreManager {

    @NonNull
    public static ResultModel<RadioModel> getRadiosRecorded(@NonNull Context context) {
        ResultModel<RadioModel> result = new ResultModel<>(ResultModel.STATUS_OK);
        ArrayList<RadioModel> listItems = getListRecordedRadios(context);
        result.setListModels(listItems);
        return result;
    }

    @NonNull
    public static ResultModel<RadioModel> getEpisodesDownloaded(@NonNull Context context) {
        ResultModel<RadioModel> result = new ResultModel<>(ResultModel.STATUS_OK);
        ArrayList<RadioModel> listItems = getListEpisodesDownloaded(context);
        int sizeGallery = listItems != null ? listItems.size() : 0;
        if (listItems != null) {
            ArrayList<RadioModel> listTemps = (ArrayList<RadioModel>) TotalDataManager.getInstance(context).getListData(TYPE_MY_DOWNLOAD);
            if (listTemps != null && listTemps.size() > 0) {
                if (sizeGallery <= 0) {
                    listItems.addAll((ArrayList<RadioModel>) listTemps.clone());
                }
                else {
                    for (RadioModel item : listTemps) {
                        if (!listItems.contains(item)) {
                            listItems.add(item);
                        }
                    }
                }
            }
            TotalDataManager.getInstance(context).updateFavoriteForList(listItems, TYPE_TAB_FAVORITE);
        }
        result.setListModels(listItems);
        return result;
    }

    @Nullable
    public static Uri getUriOfTrackAndroid(@NonNull Context context, @NonNull RadioModel radio) {
        if (IOUtils.isAndroid10()) {
            return getUriOfTrackAndroid10(context, radio);
        }
        return getUriOfTrackAndroidLower10(context, radio);
    }


    @Nullable
    private static Uri getUriOfTrackAndroid10(@NonNull Context context, @NonNull RadioModel radio) {
        Cursor cursor = null;
        try {
            if (!IOUtils.isAndroid10()) return null;
            String[] filePathColumn = {MediaStore.MediaColumns._ID};
            String relativePath = Environment.DIRECTORY_MUSIC + "/" + ANDROID_10_DIR_PUBLIC_DOWNLOAD;

            String formatId = radio.getPrefixMediaStore() + "%";
            String selectionArgs = MediaStore.Audio.Media.RELATIVE_PATH + " like '%" + relativePath + "%' ";
            selectionArgs = "(" + selectionArgs + " and " + MediaStore.Audio.Media.DISPLAY_NAME + " like '" + formatId + "')";
            YPYLog.e("DCM", "======>android 10 selectionArgs=" + selectionArgs);

            Uri uriExternal = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            cursor = context.getContentResolver().query(uriExternal, filePathColumn, selectionArgs, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIdIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                long id = cursor.getLong(columnIdIndex);
                YPYLog.e("DCM", "====>getUriOfTrackAndroid10 idStr=" + id);
                return ContentUris.withAppendedId(uriExternal, id);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeCursor(cursor);
        }
        return null;
    }

    @Nullable
    private static Uri getUriOfTrackAndroidLower10(@NonNull Context context, @NonNull RadioModel radio) {
        Cursor cursor = null;
        try {
            if (IOUtils.isAndroid10()) return null;
            File rootFolder = TotalDataManager.getInstance(context).getNewDirDownloaded(context);
            if (rootFolder != null) {
                String[] filePathColumn = {MediaStore.MediaColumns._ID};

                String formatId = "%" + DIR_CACHE + "/" + ANDROID9_DIR_DOWNLOADED + "/" + radio.getPrefixMediaStore() + "%";
                String selectionArgs = MediaStore.Audio.Media.DATA + " like '" + formatId + "'";
                YPYLog.e("DCM", "======>android lower 10 selectionArgs=" + selectionArgs);

                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, filePathColumn, selectionArgs, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIdIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                    long id = cursor.getLong(columnIdIndex);
                    YPYLog.e("DCM", "====>idStr=" + id);
                    return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeCursor(cursor);
        }
        return null;
    }

    @Nullable
    private static ArrayList<RadioModel> getListRecordedRadios(@NonNull Context context) {
        Cursor cursor = null;
        try {
            Uri uriDownload;
            String selectionArgs;
            String[] filePathColumn;
            String des = context.getString(R.string.title_recorded_files);
            if (IOUtils.isAndroid10()) {
                String relativePath = Environment.DIRECTORY_MUSIC + "/" + ANDROID_10_DIR_PUBLIC_RECORD;
                selectionArgs = MediaStore.Audio.Media.RELATIVE_PATH + " like '%" + relativePath + "%' ";
                uriDownload = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                filePathColumn = new String[]{MediaStore.MediaColumns._ID, MediaStore.Audio.AudioColumns.DISPLAY_NAME};
            }
            else {
                String formatId = "%" + DIR_CACHE + "/" + ANDROID9_DIR_RECORDS + "/%";
                selectionArgs = MediaStore.Audio.Media.DATA + " like '" + formatId + "'";
                uriDownload = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                filePathColumn = new String[]{MediaStore.MediaColumns._ID, MediaStore.Audio.AudioColumns.DISPLAY_NAME, MediaStore.Audio.Media.DATA};
            }
            String order = MediaStore.MediaColumns._ID + " DESC";
            cursor = context.getContentResolver().query(uriDownload, filePathColumn, selectionArgs, null, order);

            ArrayList<RadioModel> list = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                int columnIdIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                int columnDisplayIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME);
                int columnDataIndex = -1;
                if (!IOUtils.isAndroid10()) {
                    columnDataIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                }
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(columnIdIndex);
                    String displayStr = cursor.getString(columnDisplayIndex);
                    Uri uri = ContentUris.withAppendedId(uriDownload, id);
                    RadioModel radio = new RadioModel(displayStr.replace(FORMAT_SAVED, ""), uri.toString());
                    radio.setId(id);
                    radio.setTags(des);
                    if (columnDataIndex > -1) {
                        radio.setMediaPath(cursor.getString(columnDataIndex));
                    }
                    list.add(radio);
                }
            }
            return list;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeCursor(cursor);
        }
        return null;
    }

    @Nullable
    private static ArrayList<RadioModel> getListEpisodesDownloaded(@NonNull Context context) {
        Cursor cursor = null;
        try {
            Uri uriDownload;
            String selectionArgs;
            String[] filePathColumn = {MediaStore.MediaColumns._ID, MediaStore.Audio.AudioColumns.DISPLAY_NAME};
            if (IOUtils.isAndroid10()) {
                String relativePath = Environment.DIRECTORY_MUSIC + "/" + ANDROID_10_DIR_PUBLIC_DOWNLOAD;
                selectionArgs = MediaStore.Audio.Media.RELATIVE_PATH + " like '%" + relativePath + "%' ";
                uriDownload = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
            else {
                String formatId = "%" + DIR_CACHE + "/" + ANDROID9_DIR_DOWNLOADED + "/%";
                selectionArgs = MediaStore.Audio.Media.DATA + " like '" + formatId + "'";
                uriDownload = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            String order = MediaStore.MediaColumns._ID + " DESC";
            cursor = context.getContentResolver().query(uriDownload, filePathColumn, selectionArgs, null, order);
            ArrayList<RadioModel> list = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                int columnIdIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                int columnDisplayIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(columnIdIndex);
                    String displayStr = cursor.getString(columnDisplayIndex);
                    Uri uri = ContentUris.withAppendedId(uriDownload, id);
                    RadioModel radio = RadioModel.createEpisodeFromMediaStore(uri, displayStr);
                    if (radio != null) {
                        list.add(radio);
                    }
                }
            }
            return list;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeCursor(cursor);
        }
        return null;
    }

    public static int deleteUri(@NonNull Context context, @NonNull Uri uri) {
        try {
            return context.getContentResolver().delete(uri, null, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void closeCursor(@Nullable Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static Uri insertFileToGallery(@NonNull Context context, @NonNull ContentValues values, @NonNull String tmpPath) {
        try {
            if (IOUtils.isAndroid10()) return null;
            return context.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(tmpPath), values);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Uri insertFileToGalleryAndroid10(@NonNull Context context, @NonNull ContentValues dataValues, @NonNull String tmpPath) {
        try {
            if (!IOUtils.isAndroid10()) return null;
            //put pending = 1 to starting copy file
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.IS_PENDING, 1);
            Uri collections = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri mUri = context.getContentResolver().insert(collections, values);
            if (mUri != null) {
                File mFile = new File(tmpPath);
                if (mFile.exists() && mFile.isFile()) {
                    OutputStream mOutputStream = context.getContentResolver().openOutputStream(mUri);
                    if (mOutputStream != null) {
                        InputStream mInputStream = new FileInputStream(mFile);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = mInputStream.read(buf)) > 0) {
                            mOutputStream.write(buf, 0, len);
                        }
                        mInputStream.close();
                        mOutputStream.close();

                        //update again is pending
                        values.putAll(dataValues);
                        context.getContentResolver().update(mUri, values, null, null);

                        values.clear();
                        values.put(MediaStore.Audio.Media.IS_PENDING, 0);
                        context.getContentResolver().update(mUri, values, null, null);
                        return mUri;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

