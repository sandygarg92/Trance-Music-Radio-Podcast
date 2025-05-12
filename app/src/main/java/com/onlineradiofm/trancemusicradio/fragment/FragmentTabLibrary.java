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

package com.onlineradiofm.trancemusicradio.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.RadioAdapter;
import com.onlineradiofm.trancemusicradio.dataMng.MediaStoreManager;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderLibraryBinding;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.music.constant.IYPYStreamConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.io.File;
import java.util.ArrayList;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 4/20/18.
 */
public class FragmentTabLibrary extends XRadioListFragment<RadioModel> implements IYPYStreamConstants {

    private ItemHeaderLibraryBinding mHeaderViewBinding;

    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        RadioAdapter mRadioAdapter = new RadioAdapter(mContext, listObjects, mHeaderViewBinding.getRoot());
        mRadioAdapter.setRecordedFiles(true);
        mRadioAdapter.setOnRadioListener(new RadioAdapter.OnRadioListener() {
            @Override
            public void onFavorite(RadioModel model, boolean isFavorite) {
            }

            @Override
            public void onViewMenu(View mView, RadioModel model, boolean isRecord) {
                showPopUpMenu(mView, model);
            }
        });
        mRadioAdapter.setListener(mObject -> mContext.startPlayingList(mObject, listObjects));
        return mRadioAdapter;
    }

    @SuppressLint("NewApi")
    @Override
    public ResultModel<RadioModel> getListModelFromServer(int offset, int limit) {
        if (IOUtils.isAndroid14() && ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS_14))
            return MediaStoreManager.getRadiosRecorded(mContext);
        else if (IOUtils.isAndroid13() && ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS_13))
            return MediaStoreManager.getRadiosRecorded(mContext);
        else if (ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS)) {
            return MediaStoreManager.getRadiosRecorded(mContext);
        }
        ResultModel<RadioModel> result = new ResultModel<>(ResultModel.STATUS_OK);
        result.setListModels(new ArrayList<>());
        return result;
    }


    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_FLAT_LIST);
        int smallMargin = getResources().getDimensionPixelOffset(R.dimen.small_margin);
        viewBinding.recyclerView.setPadding(0, smallMargin, 0, 0);
        setUpHeader();
    }

    private void setUpHeader() {
        mHeaderViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_header_library,
                viewBinding.recyclerView, false);
        mHeaderViewBinding.layoutDownloadPodcast.setOnClickListener(v -> mContext.goToDownloadedPodCast());
        mHeaderViewBinding.layoutMyRadio.setOnClickListener(v -> mContext.goToMyRadios());
        mHeaderViewBinding.layoutAddRadio.setOnClickListener(v -> mContext.goToAddOrEditStation(null));

        updateHeaderColor(XRadioSettingManager.isDarkMode(mContext));
        if (ApplicationUtils.isSupportRTL()) {
            mHeaderViewBinding.imgChevron.setText(Html.fromHtml(mContext.getString(R.string.icon_chevron_left)));
        }
    }

    @Override
    public void updateDarkMode(boolean isDark) {
        super.updateDarkMode(isDark);
        if (mHeaderViewBinding != null) {
            updateHeaderColor(isDark);
        }

    }

    private void updateHeaderColor(boolean isDark) {
        int darkCard = ContextCompat.getColor(mContext, isDark ? R.color.dark_card_background : R.color.light_card_background);
        int textColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_main_color : R.color.light_list_color_main_text);
        int secondColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_second_color : R.color.light_list_color_secondary_text);
        int bgColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_card_background : R.color.light_list_bg_color);

        mHeaderViewBinding.layoutDownloadPodcast.setBackgroundColor(bgColor);
        mHeaderViewBinding.tvDownloadedPodcast.setTextColor(textColor);
        mHeaderViewBinding.downloadCardView.setCardBackgroundColor(darkCard);

        mHeaderViewBinding.layoutAddRadio.setBackgroundColor(bgColor);
        mHeaderViewBinding.tvAddRadio.setTextColor(textColor);
        mHeaderViewBinding.addRadioCardView.setCardBackgroundColor(darkCard);

        mHeaderViewBinding.layoutMyRadio.setBackgroundColor(bgColor);
        mHeaderViewBinding.tvMyRadio.setTextColor(textColor);
        mHeaderViewBinding.myRadioCardView.setCardBackgroundColor(darkCard);

        mHeaderViewBinding.tvRecord.setTextColor(textColor);
        mHeaderViewBinding.imgChevron.setTextColor(secondColor);
        mHeaderViewBinding.imgRadioChevron.setTextColor(secondColor);
    }

    private void showPopUpMenu(View mView, @NonNull RadioModel model) {
        try {
            if (mContext != null) {
                boolean isDark = XRadioSettingManager.isDarkMode(mContext);
                Context wrapper = new ContextThemeWrapper(mContext, isDark ? R.style.AppThemeDarkFull : R.style.AppThemeLightFull);

                PopupMenu popupMenu = new PopupMenu(wrapper, mView);
                popupMenu.getMenuInflater().inflate(R.menu.menu_record_files, popupMenu.getMenu());
                popupMenu.getMenu().findItem(R.id.action_delete).setVisible(!IOUtils.isAndroid10());
//                popupMenu.getMenu().findItem(R.id.action_rename).setVisible(false);
                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_delete) {
                        mContext.showFullDialog(R.string.title_confirm, mContext.getString(R.string.info_delete_file), R.string.title_delete
                                , R.string.title_cancel, () -> deleteFile(model));
                    }
//                    else if (itemId == R.id.action_rename) {
//                        showDialogRename(model);
//                    }
                    else if (itemId == R.id.action_share) {
                        mContext.shareRadioModel(model);
                    }
                    return true;
                });
                popupMenu.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteFile(@NonNull RadioModel model) {
        if (mContext != null) {
            mContext.showProgressDialog();
            RadioModel mCurrentRadio = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
            String playPath = mCurrentRadio != null ? mCurrentRadio.getPath() : null;
            YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
                String path = model.getPath();
                final boolean isPlayPath = playPath != null && playPath.equalsIgnoreCase(path);
                try {
                    if (!TextUtils.isEmpty(path) && path.startsWith(PREFIX_CONTENT)) {
                        MediaStoreManager.deleteUri(mContext, Uri.parse(path));
                    }
                    String mediaPath = model.getMediaPath();
                    if (!TextUtils.isEmpty(mediaPath)) {
                        File mFile = new File(mediaPath);
                        if (mFile.exists() && mFile.isFile()) {
                            YPYLog.e("DCM", "=====>delete file=" + mediaPath);
                            mFile.delete();
                        }
                    }
                    mListModels.remove(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mContext.runOnUiThread(() -> {
                    mContext.dismissProgressDialog();
                    YPYStreamManager.getInstance().removeMusicModel(path);
                    notifyData();
                    if (isPlayPath) {
                        mContext.startMusicService(ACTION_NEXT);
                    }
                });

            });

        }
    }

//    private void showDialogRename(RadioModel model) {
//        if (mContext != null) {
//            boolean isDark = XRadioSettingManager.isDarkMode(mContext);
//
//            View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_text, null);
//            final EditText mEdPlaylistName = mView.findViewById(R.id.ed_name);
//            mEdPlaylistName.setTextColor(mContext.getResources().getColor(isDark ? R.color.dark_text_main_color : R.color.dialog_color_text));
//            mEdPlaylistName.setHintTextColor(mContext.getResources().getColor(isDark ? R.color.dark_text_second_color : R.color.dialog_color_secondary_text));
//            mView.findViewById(R.id.divider).setBackgroundColor(mContext.getResources().getColor(isDark ? R.color.dark_color_accent : R.color.dialog_color_accent));
//            if (ApplicationUtils.isSupportRTL()) {
//                mEdPlaylistName.setGravity(Gravity.BOTTOM | Gravity.END);
//            }
//            mEdPlaylistName.setText(model.getName());
//            MaterialDialog.Builder mBuilder = mContext.createBasicDialogBuilder(R.string.title_rename,
//                    R.string.title_save, R.string.title_cancel);
//            mBuilder.customView(mView, false);
//            mBuilder.onPositive((dialog, which) -> {
//                ApplicationUtils.hiddenVirtualKeyboard(mContext, mEdPlaylistName);
//                String mNewName = mEdPlaylistName.getText().toString();
//                checkName(model, mNewName);
//            });
//
//            final MaterialDialog mDialog = mBuilder.build();
//            mEdPlaylistName.setOnEditorActionListener((textView, actionId, keyEvent) -> {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    String mNewName = mEdPlaylistName.getText().toString();
//                    checkName(model, mNewName);
//                    mDialog.dismiss();
//                    return true;
//                }
//                return false;
//            });
//            if (mDialog.getWindow() != null) {
//                mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                mDialog.show();
//                mEdPlaylistName.requestFocus();
//            }
//        }
//
//    }
//
//    private void checkName(@NonNull RadioModel model, String newName) {
//        if (TextUtils.isEmpty(newName)) {
//            mContext.showToast(R.string.info_name_empty);
//            return;
//        }
//        if (model.getName().equals(newName)) {
//            return;
//        }
//        if (mListModels != null && mListModels.size() > 0) {
//            for (RadioModel model1 : mListModels) {
//                if (model1.getName().equals(newName) && model1.getId() != model.getId()) {
//                    mContext.showToast(R.string.info_name_existed);
//                    return;
//                }
//            }
//        }
//        mContext.showProgressDialog();
//        RadioModel mCurrentRadio = (RadioModel) YPYStreamManager.getInstance().getCurrentModel();
//        String playPath = mCurrentRadio != null ? mCurrentRadio.getPath() : null;
//        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
//            String path = model.getPath();
//            final boolean isPlayPath = playPath != null && playPath.equalsIgnoreCase(path);
//            try {
//                if (!TextUtils.isEmpty(path)) {
//                    File mFile = new File(path);
//                    if (mFile.exists() && mFile.isFile()) {
//                        File mDestFile = new File(mContext.mTotalMng.getOldDirRecorded(mContext), newName + FORMAT_SAVED);
//                        boolean b = mFile.renameTo(mDestFile);
//                        if (b) {
//                            model.setPath(mDestFile.getAbsolutePath());
//                            model.setName(newName);
//                            YPYStreamManager.getInstance().updateMusicModel(model.getId(), model.getName(), model.getPath());
//                        }
//                    }
//                }
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//            mContext.runOnUiThread(() -> {
//                mContext.dismissProgressDialog();
//                notifyData();
//                if (isPlayPath) {
//                    mContext.startMusicService(ACTION_NEXT);
//                }
//            });
//
//        });
//
//
//    }
}
