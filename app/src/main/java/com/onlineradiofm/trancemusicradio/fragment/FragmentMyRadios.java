package com.onlineradiofm.trancemusicradio.fragment;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.MyRadioAdapter;
import com.onlineradiofm.trancemusicradio.db.DatabaseManager;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-02-10.
 */
public class FragmentMyRadios extends XRadioListFragment<RadioModel> {

    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        MyRadioAdapter mRadioAdapter = new MyRadioAdapter(mContext, listObjects);
        mRadioAdapter.setListener(mObject -> mContext.startPlayingList(mObject, listObjects));
        mRadioAdapter.setOnMenuListener(this::showPopUpMenu);
        return mRadioAdapter;
    }

    @Override
    public ResultModel<RadioModel> getListModelFromServer(int offset, int limit) {
        return DatabaseManager.getInstance(mContext).getAllRadios(mContext);
    }

    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_FLAT_LIST);
    }

    private void showPopUpMenu(@NonNull View mView, @NonNull RadioModel model) {
        try {
            boolean isDark = XRadioSettingManager.isDarkMode(mContext);
            Context wrapper = new ContextThemeWrapper(mContext, isDark ? R.style.AppThemeDarkFull : R.style.AppThemeLightFull);
            PopupMenu popupMenu = new PopupMenu(wrapper, mView);
            popupMenu.getMenuInflater().inflate(R.menu.menu_my_radio, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_remove) {
                    mContext.showFullDialog(R.string.title_confirm, mContext.getString(R.string.info_confirm_delete_my_radio), R.string.title_remove
                            , R.string.title_cancel, () -> startDeleteRadio(model));
                }
                else if (itemId == R.id.action_edit) {
                    mContext.goToAddOrEditStation(model);
                }
                else if (itemId == R.id.action_share) {
                    mContext.shareRadioModel(model);
                }
                return true;
            });
            popupMenu.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startDeleteRadio(@NonNull RadioModel radio) {
        mContext.showProgressDialog();
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            DatabaseManager.getInstance(mContext).deleteRadio(radio.getId());
            long lastId = XRadioSettingManager.getLastPlayedRadioId(mContext);
            boolean isLastPlayedMyRadio = XRadioSettingManager.getLastPlayedMyRadio(mContext);
            if (isLastPlayedMyRadio && radio.getId() == lastId) {
                XRadioSettingManager.setLastPlayedMyRadio(mContext, false);
                XRadioSettingManager.setLastPlayedRadioId(mContext, 0);
            }
            mContext.runOnUiThread(() -> {
                mContext.dismissProgressDialog();
                mContext.showToast(R.string.info_update_radio_success);
                setLoadingData(false);
                startLoadData();
            });
        });


    }


}
