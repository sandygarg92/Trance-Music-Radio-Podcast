package com.onlineradiofm.trancemusicradio.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.onlineradiofm.trancemusicradio.adapter.RadioAdapter;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-02-10.
 */
public class FragmentTopRadios extends XRadioListFragment<RadioModel> {

    private String mTypeTop;

    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        RadioAdapter mRadioAdapter = new RadioAdapter(mContext, listObjects);
        mRadioAdapter.setListener(mObject -> mContext.startPlayingList(mObject, listObjects));
        mRadioAdapter.setOnRadioListener(new RadioAdapter.OnRadioListener() {
            @Override
            public void onFavorite(RadioModel model, boolean isFavorite) {
                mContext.updateFavorite(model, mType, isFavorite);
            }
            @Override
            public void onViewMenu(View mView, RadioModel model, boolean isRecord) {
                mContext.showPopUpMenu(mView, model);
            }
        });
        return mRadioAdapter;
    }

    @Override
    public ResultModel<RadioModel> getListModelFromServer(int offset, int limit) {
        if (ApplicationUtils.isOnline(mContext) && !TextUtils.isEmpty(mTypeTop)) {
            return XRadioNetUtils.getListTopRadioModels(mContext, mTypeTop, offset, limit);
        }
        return null;
    }

    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_FLAT_LIST);
    }

    @Override
    public void onExtractData(Bundle args) {
        super.onExtractData(args);
        if (args != null) {
            mTypeTop = args.getString(KEY_TYPE_TOP, "");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TYPE_TOP, mTypeTop);
    }

    @Override
    ArrayList<RadioModel> doOnNextWithListModel(ArrayList<RadioModel> listModels, boolean isLoadMore) {
        return addNativeAdsToListModel(listModels, isLoadMore);
    }

    @Override
    RadioModel createNativeAdsModel() {
        return new RadioModel(true);
    }
}
