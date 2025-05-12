package com.onlineradiofm.trancemusicradio.dataMng;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;

import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.databinding.ItemDownloadProcessBinding;
import com.onlineradiofm.trancemusicradio.fragment.FragmentDownloads;
import com.onlineradiofm.trancemusicradio.fragment.FragmentDragDrop;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.MimeUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.ANDROID_10_DIR_PUBLIC_DOWNLOAD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.PREFIX_CONTENT;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.TAG_FRAGMENT_DOWNLOAD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.TYPE_MY_DOWNLOAD;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.TYPE_TAB_FAVORITE;
import static com.onlineradiofm.trancemusicradio.dataMng.MediaStoreManager.getUriOfTrackAndroid;
import static com.onlineradiofm.trancemusicradio.ypylibs.music.constant.IYPYStreamConstants.ACTION_NEXT;

public class MyDownloadManager {

    @NonNull
    private final MainActivity context;

    @NonNull
    private final FragmentDragDrop fragmentDragDrop;

    private AppCompatDialog mDownloadProgressDialog;
    private Disposable mDownloadDisposable;

    public MyDownloadManager(@NonNull MainActivity context, @NonNull FragmentDragDrop fragmentDragDrop) {
        this.context = context;
        this.fragmentDragDrop = fragmentDragDrop;
    }

    public void startDownloadFile(@NonNull RadioModel model) {
        try {
            if (mDownloadProgressDialog != null && mDownloadProgressDialog.isShowing()) {
                return;
            }
            File mDownloadRoot = TotalDataManager.getInstance(context).getNewDirDownloaded(context);
            if (mDownloadRoot != null) {
                String newNameFile = model.getNameFileDownload();
                File destinationFile = new File(mDownloadRoot, newNameFile);
                if (destinationFile.exists() && destinationFile.isFile()) {
                    destinationFile.delete();
                }
                ItemDownloadProcessBinding viewBinding = DataBindingUtil.inflate(context.getLayoutInflater(), R.layout.item_download_process, null, false);
                mDownloadProgressDialog = new AppCompatDialog(context);
                mDownloadProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDownloadProgressDialog.setContentView(viewBinding.getRoot());
                mDownloadProgressDialog.setCancelable(false);
                mDownloadProgressDialog.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);

                boolean isDark = XRadioSettingManager.isDarkMode(context);
                int colorAccent = ContextCompat.getColor(context, isDark ? R.color.dark_color_accent : R.color.light_color_accent);
                int dialogBgColor = ContextCompat.getColor(context, isDark ? R.color.dark_card_background : R.color.dialog_bg_color);
                int textColor = ContextCompat.getColor(context, isDark ? R.color.dark_text_main_color : R.color.dialog_color_text);
                int textHintColor = ContextCompat.getColor(context, isDark ? R.color.dark_text_hint_color : R.color.dialog_color_hint_text);

                viewBinding.tvTitleDownload.setTextColor(textColor);
                viewBinding.tvPercentage.setTextColor(textColor);
                viewBinding.layoutDownloadBg.setBackgroundColor(dialogBgColor);
                viewBinding.circleView.setBackgroundColor(textHintColor);
                viewBinding.circleView.setProgressColor(colorAccent);

                viewBinding.btnCancel.setOnClickListener(view -> cancelDownload(model));
                viewBinding.circleView.setProgress(0f);
                viewBinding.tvPercentage.setText(R.string.title_loading);

                mDownloadProgressDialog.show();

                YPYLog.e("DCM", "==========>link download=" + model.getLinkRadio());
                Observable<ResultModel<File>> mObserver = RetroRadioNetUtils.downloadFile(model.getLinkRadio(),
                        mDownloadRoot.getAbsolutePath()).doOnNext(fileResultModel -> saveFile(fileResultModel, model, newNameFile));
                mDownloadDisposable = context.mYPYRXModel.addObservableToObserver(mObserver, new DisposableObserver<>() {
                    @Override
                    public void onNext(@NonNull ResultModel<File> fileResultModel) {
                        if (fileResultModel.isDownloadingFile()) {
                            String percent = fileResultModel.getPercentage() + "%";
                            viewBinding.circleView.setProgress(fileResultModel.getPercentage());
                            viewBinding.tvPercentage.setText(percent);
                            viewBinding.tvPercentage.setTextColor(colorAccent);
                        }
                        else if (fileResultModel.isResultOk()) {
                            dismissProgressDownload();
                            context.showToast(R.string.info_saved_song_success);
                            context.updateInfoOfPlayingTrack();
                            FragmentDownloads mFragmentDownload = (FragmentDownloads) context.getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_DOWNLOAD);
                            if (mFragmentDownload != null) {
                                mFragmentDownload.notifyData();
                            }
                            if (YPYStreamManager.getInstance().isHavingList()) {
                                fragmentDragDrop.updateCurrentDownload();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        dismissProgressDownload();
                        context.showToast(R.string.info_download_error);
                    }

                    @Override
                    public void onComplete() {
                        dismissProgressDownload();
                    }
                });

            }
            else {
                context.showToast(R.string.info_sdcard_error);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile(@NonNull ResultModel<File> fileResultModel, @NonNull RadioModel model, @NonNull String newNameFile) {
        if (fileResultModel.isResultOk()) {
            if (context.checkStoragePermissions()) {
                File file = fileResultModel.firstModel();
                if (file != null && file.exists() && file.isFile()) {
                    Uri uri = saveFileToGallery(model, file);
                    YPYLog.e("DCM", "=========>saveFileToGallery=" + uri);
                }
            }
            else {
                RadioModel model1 = model.cloneObject();
                if (model1 != null) {
                    model.setPath(newNameFile);
                    model1.setPath(newNameFile);
                }
                TotalDataManager.getInstance(context).addModelToCache(TYPE_MY_DOWNLOAD, model1);
            }
        }
    }

    public void moveToMediaStore(@NonNull RadioModel radio, @Nullable FragmentDownloads fragmentDownload) {
        if (context.checkStoragePermissions()) {
            String path = TotalDataManager.getInstance(context).getFileDownloaded(context, radio);
            if (path != null && !TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    context.showProgressDialog();
                    YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
                        Uri uri = null;
                        if (IOUtils.isAndroid10()) {
                            uri = saveFileToGallery(radio, file);
                        }
                        else {
                            File newFileMove = moveFileToDownloadedAndroid9(radio, file);
                            if (newFileMove != null) {
                                uri = saveFileToGallery(radio, newFileMove);
                            }
                        }
                        YPYLog.e("DCM", "=========>moveToMediaStore=" + uri);
                        if (uri != null) {
                            TotalDataManager.getInstance(context).removeModelToCache(TYPE_MY_DOWNLOAD, radio);
                        }
                        Uri finalUri = uri;
                        context.runOnUiThread(() -> {
                            context.dismissProgressDialog();
                            context.showToast(finalUri != null ? R.string.info_move_file_success : R.string.info_move_file_error);
                            if (fragmentDownload != null) {
                                fragmentDownload.setLoadingData(false);
                                fragmentDownload.startLoadData();
                            }
                        });
                    });
                    return;
                }
            }
        }
        context.showToast(R.string.info_move_file_error);
    }

    private void dismissProgressDownload() {
        try {
            if (mDownloadProgressDialog != null) {
                mDownloadProgressDialog.dismiss();
                mDownloadProgressDialog = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelDownload(@NonNull RadioModel model) {
        try {
            mDownloadProgressDialog.dismiss();
            if (mDownloadDisposable != null) {
                mDownloadDisposable.dispose();
                mDownloadDisposable = null;
            }
            File mFile = TotalDataManager.getInstance(context).getDownloadFileNoCheck(context, model);
            if (mFile != null) {
                mFile.delete();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri saveFileToGallery(@NonNull RadioModel radio, @NonNull File file) {
        try {
            ContentValues values = getContentValues(radio, file.getAbsolutePath());
            Uri uri = null;
            if (IOUtils.isAndroid10()) {
                uri = MediaStoreManager.insertFileToGalleryAndroid10(context,values, file.getAbsolutePath());
                //delete the cache file
                file.delete();
            }
            else {
                //if we save to media store we need to change file name to save information
                File parent = file.getParentFile();
                if (parent != null && parent.exists()) {
                    File mediaStoreFile = new File(parent, radio.getMediaStoreNameFile());
                    boolean isRenamed = file.renameTo(mediaStoreFile);
                    YPYLog.e("DCM", "=======>saveFileToGallery rename =" + isRenamed);
                    if (isRenamed) {
                        values.put(MediaStore.Audio.Media.DATA, mediaStoreFile.getAbsolutePath());
                        uri = MediaStoreManager.insertFileToGallery(context,values, mediaStoreFile.getAbsolutePath());
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


    private ContentValues getContentValues(@NonNull RadioModel radio, @NonNull String path) {
        ContentValues values = new ContentValues();
        String artist = !TextUtils.isEmpty(radio.getArtist()) ? radio.getArtist() : context.getString(R.string.app_name);
        String displayMediaStore = radio.getDisplayNameInMediaStore();
        YPYLog.e("DCM", "==========>nameInGallery =" + displayMediaStore + "====>artist=" + artist);

        String mime = getMimeType(path);
        values.put(MediaStore.Audio.Media.MIME_TYPE, mime);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, displayMediaStore);
        values.put(MediaStore.Audio.Media.TITLE, radio.getName());
        values.put(MediaStore.Audio.Media.ARTIST, artist);

        if (IOUtils.isAndroid10()) {
            String relativePath = Environment.DIRECTORY_MUSIC + "/" + ANDROID_10_DIR_PUBLIC_DOWNLOAD;
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath);
        }
        else {
            values.put(MediaStore.Audio.Media.ALBUM, ANDROID_10_DIR_PUBLIC_DOWNLOAD);
        }
        return values;
    }

    private String getMimeType(@NonNull String tmpPath) {
        String[] data = tmpPath.split("\\.+");
        String mime = null;
        if (data.length >= 2) {
            String extension = data[data.length - 1];
            mime = MimeUtils.guessMimeTypeFromExtension(extension);
        }
        if (TextUtils.isEmpty((mime))) {
            mime = "audio/mpeg";
        }
        return mime;
    }

    public boolean isDownloaded(@NonNull RadioModel radio) {
        if (context.checkStoragePermissions()) {
            String path = radio.getPath();
            if (path != null && path.startsWith(PREFIX_CONTENT)) return true;
            Uri uri = getUriOfTrackAndroid(context, radio);
            YPYLog.e("DCM", "======>uri downloaded=" + uri);
            if (uri != null) {
                radio.setPath(uri.toString());
                return true;
            }
        }
        return TotalDataManager.getInstance(context).isFileDownloaded(context, radio);
    }

    public void deleteEpisode(@NonNull RadioModel model, @Nullable FragmentDownloads fragmentDownloads) {
        context.showProgressDialog();
        RadioModel episodeModel = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
        String playPath = episodeModel != null ? episodeModel.getPath() : null;
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            String path = model.getPath();
            final boolean isPlayPath = playPath != null && playPath.equalsIgnoreCase(path);
            int deleteUriIndex = -1;
            boolean isUpdateFav = false;
            if (path.startsWith(PREFIX_CONTENT)) {
                deleteUriIndex = MediaStoreManager.deleteUri(context, Uri.parse(path));
                deleteEpisodesFile(model, deleteUriIndex);
                if (model.getLinkRadio() == null || TextUtils.isEmpty(model.getLinkRadio())) {
                    context.mTotalMng.removeModelToCache(TYPE_TAB_FAVORITE, model);
                    isUpdateFav = true;
                }
            }
            else {
                deleteEpisodesFile(model, deleteUriIndex);
                context.mTotalMng.removeModelToCache(TYPE_MY_DOWNLOAD, model);
            }
            int finalDeleteUriIndex = deleteUriIndex;
            boolean finalIsUpdateFav = isUpdateFav;
            context.runOnUiThread(() -> {
                context.dismissProgressDialog();
                YPYStreamManager.getInstance().removeOfflineModel(path);
                model.setPath(null);
                if (fragmentDownloads != null) {
                    if (finalDeleteUriIndex >= 0) {
                        fragmentDownloads.setLoadingData(false);
                        fragmentDownloads.startLoadData();
                    }
                    else {
                        fragmentDownloads.notifyData();
                    }
                }
                if (finalIsUpdateFav) {
                    context.notifyFavorite(model.getId(), false);
                }
                if (isPlayPath) {
                    context.startMusicService(ACTION_NEXT);
                }
                context.showToast(R.string.info_delete_success);
                if (YPYStreamManager.getInstance().isHavingList()) {
                    fragmentDragDrop.updateCurrentDownload();
                }
            });

        });
    }

    private void deleteEpisodesFile(@NonNull RadioModel model, int deleteUriIndex) {
        try {
            if (deleteUriIndex >= 0 && !IOUtils.isAndroid10()) {
                File parent = TotalDataManager.getInstance(context).getNewDirDownloaded(context);
                if (parent != null) {
                    File mediaStoreFile = new File(parent, model.getMediaStoreNameFile());
                    YPYLog.e("DCM", "=======>mediaStoreFile=" + mediaStoreFile.getAbsolutePath());
                    if (mediaStoreFile.exists() && mediaStoreFile.isFile()) {
                        mediaStoreFile.delete();
                        return;
                    }
                }

            }
            String mDownloadedPath = TotalDataManager.getInstance(context).getFileDownloaded(context, model);
            if (!TextUtils.isEmpty(mDownloadedPath)) {
                File mFile = new File(mDownloadedPath);
                if (mFile.exists()) {
                    mFile.delete();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private File moveFileToDownloadedAndroid9(@NonNull RadioModel model, @NonNull File currentPath) {
        try {
            File parent = TotalDataManager.getInstance(context).getNewDirDownloaded(context);
            if (parent != null) {
                File mediaStoreFile = new File(parent, model.getMediaStoreNameFile());
                YPYLog.e("DCM", "=======>mediaStoreFile=" + mediaStoreFile.getAbsolutePath());
                YPYLog.e("DCM", "=======>currentPath=" + currentPath);
                boolean isRenamed = currentPath.renameTo(mediaStoreFile);
                YPYLog.e("DCM", "=======>moveFileToDownloadedAndroid9 rename =" + isRenamed);
                if (isRenamed) {
                    return mediaStoreFile;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
