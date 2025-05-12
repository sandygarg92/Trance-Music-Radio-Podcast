package com.onlineradiofm.trancemusicradio.fragment;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.databinding.FragmentAddRadioBinding;
import com.onlineradiofm.trancemusicradio.db.DatabaseManager;
import com.onlineradiofm.trancemusicradio.db.entity.RMRadioEntity;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.fragment.YPYFragment;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 4/20/18.
 */

public class FragmentAddRadio extends YPYFragment<FragmentAddRadioBinding> implements IRadioConstants {

    protected MainActivity mContext;

    private RadioModel radioModel;

    @NonNull
    @Override
    protected FragmentAddRadioBinding getViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentAddRadioBinding.inflate(inflater);
    }

    @Override
    public void findView() {
        mContext = (MainActivity) requireActivity();
        viewBinding.btnAdd.setOnClickListener(view -> startSaveRadio());
        updateDarkMode(XRadioSettingManager.isDarkMode(mContext));
        if (radioModel != null && radioModel.isMyRadio()) {
            viewBinding.edRadioName.setText(radioModel.getName());
            viewBinding.edRadioLink.setText(radioModel.getLinkRadio());
            viewBinding.cbLiveRadio.setChecked(radioModel.isLive());
            viewBinding.cbMp3.setChecked(!radioModel.isLive());
            viewBinding.tvAdd.setText(R.string.title_update);
            viewBinding.imgAdd.setImageResource(R.drawable.ic_baseline_edit_24);
        }
    }

    @Override
    public void onExtractData(Bundle savedInstance) {
        super.onExtractData(savedInstance);
        this.radioModel = savedInstance.getParcelable(KEY_MODEL);
    }

    @Override
    public void updateDarkMode(boolean isDark) {
        super.updateDarkMode(isDark);
        int textColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_main_color : R.color.light_text_main_color);
        int secondColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);
        int accentColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_color_accent : R.color.light_color_accent);
        Drawable bgEdit = ContextCompat.getDrawable(mContext, isDark ? R.drawable.bg_dark_edit_search : R.drawable.bg_light_edit_search);
        viewBinding.edRadioLink.setTextColor(textColor);
        viewBinding.edRadioLink.setHintTextColor(secondColor);
        viewBinding.edRadioLink.setBackgroundDrawable(bgEdit);

        viewBinding.edRadioName.setTextColor(textColor);
        viewBinding.edRadioName.setHintTextColor(secondColor);
        viewBinding.edRadioName.setBackgroundDrawable(bgEdit);

        ColorStateList colorStateList = new ColorStateList(
                new int[][]
                        {
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                new int[]
                        {
                                secondColor, // disabled
                                accentColor   // enabled
                        }
        );
        viewBinding.cbLiveRadio.setButtonTintList(colorStateList);
        viewBinding.cbMp3.setButtonTintList(colorStateList);

        viewBinding.cbLiveRadio.setTextColor(textColor);
        viewBinding.cbMp3.setTextColor(textColor);

    }

    private void startSaveRadio() {
        ApplicationUtils.hiddenVirtualKeyboard(mContext, viewBinding.edRadioLink);
        ApplicationUtils.hiddenVirtualKeyboard(mContext, viewBinding.edRadioName);
        String name = viewBinding.edRadioName.getText() != null ? viewBinding.edRadioName.getText().toString().trim() : "";
        String formatEmpty = mContext.getString(R.string.format_empty_name);
        if (TextUtils.isEmpty(name)) {
            mContext.showToast(String.format(formatEmpty, getString(R.string.title_radio_name)));
            return;
        }
        String linkRadio = viewBinding.edRadioLink.getText() != null ? viewBinding.edRadioLink.getText().toString().trim() : "";
        if (TextUtils.isEmpty(linkRadio)) {
            mContext.showToast(String.format(formatEmpty, getString(R.string.title_radio_link)));
            return;
        }
        if (!linkRadio.startsWith("http")) {
            mContext.showToast(R.string.info_invalid_link);
            return;
        }
        int isMp3 = viewBinding.cbMp3.isChecked() ? 1 : 0;
        mContext.showProgressDialog();
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            long index;
            if (radioModel != null) {
                RMRadioEntity entity = radioModel.createRadioEntity();
                entity.isMp3 = isMp3;
                entity.name = name;
                entity.linkTrack = linkRadio;
                index = DatabaseManager.getInstance(mContext).updateRadio(entity);
            }
            else {
                RMRadioEntity entity = new RMRadioEntity(name, linkRadio, isMp3);
                index = DatabaseManager.getInstance(mContext).insertRadio(entity);
            }
            mContext.runOnUiThread(() -> {
                mContext.dismissProgressDialog();
                if (index > 0) {
                    reloadData();
                }
                if (radioModel != null) {
                    mContext.showToast(index > 0 ? R.string.info_update_radio_success : R.string.info_update_radio_error);
                    return;
                }
                viewBinding.edRadioName.setText("");
                viewBinding.edRadioLink.setText("");
                mContext.showToast(index > 0 ? R.string.info_add_radio_success : R.string.info_add_radio_error);
            });
        });


    }

    private void reloadData() {
        FragmentMyRadios fragmentMyRadios = (FragmentMyRadios) mContext.getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_MY_RADIO);
        if (fragmentMyRadios != null) {
            fragmentMyRadios.setLoadingData(false);
            fragmentMyRadios.startLoadData();
        }
    }
}
