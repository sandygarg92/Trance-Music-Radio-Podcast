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

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.EpisodeAdapter;
import com.onlineradiofm.trancemusicradio.dataMng.MediaStoreManager;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://jamit.com
 * Created by radiopolska on 4/20/18.
 */
public class FragmentDownloads extends XRadioListFragment<RadioModel> {

    private RoundedCornersTransformation cornersTransformation;

    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        EpisodeAdapter adapter = new EpisodeAdapter(mContext, listObjects, null, cornersTransformation);
        adapter.setListener(episodeModel -> mContext.startPlayingList(episodeModel, listObjects));
        adapter.setOnMenuListener((mView, model) -> mContext.showPopUpMenu(mView, model));
        adapter.setOnFavoriteListener((model, isFavorite) -> mContext.updateFavorite(model, mType, isFavorite));
        return adapter;
    }

    @Override
    public ResultModel<RadioModel> getListModelFromServer(int offset, int limit) {
        if (mContext.checkStoragePermissions()) {
            return MediaStoreManager.getEpisodesDownloaded(mContext);
        }
        return null;
    }

    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_FLAT_LIST);
        viewBinding.recyclerView.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelOffset(R.dimen.dialog_margin));

        int floatDimen = mContext.getResources().getDimensionPixelOffset(R.dimen.corner_radius);
        this.cornersTransformation = new RoundedCornersTransformation(floatDimen, 0);
    }

    @Override
    public void notifyData() {
        super.notifyData();
        if (mAdapter == null) {
            onRefreshData();
        }
    }

}
