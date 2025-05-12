/*
 * Copyright (c) 2018. YPY Global - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://ypyglobal.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onlineradiofm.trancemusicradio.fragment;

import static com.onlineradiofm.trancemusicradio.itunes.constants.IITunesConstants.ITUNES_ENTITY_PODCAST;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.adapter.EpisodeAdapter;
import com.onlineradiofm.trancemusicradio.databinding.ItemHeaderDetailPodcastBinding;
import com.onlineradiofm.trancemusicradio.itunes.model.PodCastModel;
import com.onlineradiofm.trancemusicradio.itunes.model.SearchResultModel;
import com.onlineradiofm.trancemusicradio.itunes.model.rss.RssChannelModel;
import com.onlineradiofm.trancemusicradio.itunes.model.rss.RssFeedModel;
import com.onlineradiofm.trancemusicradio.itunes.model.rss.RssItemModel;
import com.onlineradiofm.trancemusicradio.itunes.webservice.ITunesNetUtils;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ShareActionUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.StringUtils;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * @author:YPY Global
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: bl911vn@gmail.com
 * @Website: http://ypyglobal.com
 * Created by dotrungbao on 4/20/18.
 */
public class FragmentDetailPodCast extends XRadioListFragment<RadioModel> {

    private PodCastModel podCastModel;
    private ItemHeaderDetailPodcastBinding headerViewBinding;
    private RoundedCornersTransformation cornersTransformation;


    @Override
    public YPYRecyclerViewAdapter<RadioModel> createAdapter(ArrayList<RadioModel> listObjects) {
        //update podcast information
        updateBanner(listObjects.size());
        EpisodeAdapter episodeAdapter = new EpisodeAdapter(mContext, listObjects, headerViewBinding.getRoot(), cornersTransformation);
        episodeAdapter.setListener(episodeModel -> mContext.startPlayingList(episodeModel, listObjects));
        episodeAdapter.setOnMenuListener((mView, model) -> mContext.showPopUpMenu(mView, model));
        episodeAdapter.setOnEpisodeListener((model, isFavorite) -> mContext.updateFavorite(model, mType, isFavorite));
        episodeAdapter.setOnFavoriteListener((model, isFavorite) -> mContext.updateFavorite(model, mType, isFavorite));
        return episodeAdapter;
    }

    private void updateBanner(int numEpisodes) {
        if (podCastModel != null) {
            String des = podCastModel.getDescription();
            if (!TextUtils.isEmpty(des)) {
                headerViewBinding.tvHeaderDes.setText(Html.fromHtml(des).toString().trim());
            }
            headerViewBinding.tvHeaderNumberTrack.setText(StringUtils.getFormatSocial(mContext, numEpisodes, R.string.format_episode, R.string.format_episodes));
            String link = podCastModel.getLink();
            headerViewBinding.layoutRippleInfo.setVisibility(!TextUtils.isEmpty(link) ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(link)) {
                if (!link.startsWith("http")) {
                    link = "http://" + link;
                }
                String finalLink = link;
                headerViewBinding.btnInfo.setOnClickListener(v -> ShareActionUtils.goToUrl(mContext, finalLink));
            }
        }
    }

    @Override
    public ResultModel<RadioModel> getListModelFromServer(int offset, int limit) {
        ResultModel<RadioModel> mResultModel;
        if (ApplicationUtils.isOnline(mContext) && podCastModel != null) {
            String feedUrl = podCastModel.getFeedUrl();
            if (TextUtils.isEmpty(feedUrl)) {
                SearchResultModel searchResultModel = ITunesNetUtils.lookUpModel(podCastModel.getId(), ITUNES_ENTITY_PODCAST);
                if (searchResultModel != null) {
                    ArrayList<PodCastModel> mListPodcast = searchResultModel.getListPodcasts();
                    PodCastModel mLookUpPodcast = mListPodcast != null && mListPodcast.size() > 0 ? mListPodcast.get(0) : null;
                    if (mLookUpPodcast != null) {
                        podCastModel.setFeedUrl(mLookUpPodcast.getFeedUrl());
                        feedUrl = mLookUpPodcast.getFeedUrl();
                    }
                }
            }
            if (!TextUtils.isEmpty(feedUrl)) {
                RssFeedModel mFeedModel = ITunesNetUtils.getRssFeedModel(feedUrl);
                RssChannelModel rssChannelModel = mFeedModel != null ? mFeedModel.getChannel() : null;
                if (rssChannelModel != null) {
                    podCastModel.setDescription(rssChannelModel.getDescription());
                    podCastModel.setLink(rssChannelModel.getLink());
                    ArrayList<RssItemModel> mListItems = rssChannelModel.getItemModels();
                    if (mListItems != null && mListItems.size() > 0) {
                        ArrayList<RadioModel> mListEpisodeModels = new ArrayList<>();
                        String channel = rssChannelModel.getTitle();
                        String author = rssChannelModel.getITunesAuthor();
                        String img = rssChannelModel.getImage();
                        for (RssItemModel itemModel : mListItems) {
                            RadioModel model = itemModel.convertToRadioModel(channel, author, img);
                            if (model != null) {
                                mListEpisodeModels.add(model);
                            }
                        }
                        mContext.mTotalMng.updateFavoriteForList(mListEpisodeModels, TYPE_TAB_FAVORITE);
                        mResultModel = new ResultModel<>(200, "");
                        mResultModel.setListModels(mListEpisodeModels);
                        return mResultModel;
                    }
                }
                else {
                    mResultModel = new ResultModel<>(203, "");
                    mResultModel.setMsg(mContext.getString(R.string.info_podcast_offline));
                    return mResultModel;
                }
            }
        }
        return null;
    }

    @Override
    public void setUpUI() {
        setUpUIRecyclerView(UI_FLAT_LIST);
        viewBinding.recyclerView.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelOffset(R.dimen.dialog_margin));

        int floatDimen = mContext.getResources().getDimensionPixelOffset(R.dimen.corner_radius);
        this.cornersTransformation = new RoundedCornersTransformation(floatDimen, 0);
        setUpHeader();
    }

    private void setUpHeader() {
        BlurTransformation mBlurTransform = new BlurTransformation();
        headerViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_header_detail_podcast, viewBinding.recyclerView, false);
        String artwork = podCastModel != null ? podCastModel.getArtWork() : null;
        GlideImageLoader.displayImage(mContext, headerViewBinding.imgHeaderPodcast, artwork, cornersTransformation, R.drawable.ic_podcast_default);
        GlideImageLoader.displayImage(mContext, headerViewBinding.imgHeaderBg, artwork, mBlurTransform, R.drawable.bg_nav_header);
        headerViewBinding.tvHeaderName.setText(podCastModel != null ? podCastModel.getName() : null);

        String tag = podCastModel != null ? podCastModel.getArtistName() : null;
        if (TextUtils.isEmpty(tag)) {
            tag = mContext.getString(R.string.app_name);
        }
        headerViewBinding.tvHeaderSubName.setText(tag);
        headerViewBinding.btnPlayAll.setOnClickListener(v -> {
            if (mListModels != null && mListModels.size() > 0) {
                mContext.startPlayingList(mListModels.get(0), mListModels);
            }
        });
        boolean isDark = XRadioSettingManager.isDarkMode(mContext);
        headerViewBinding.btnPlayAll.setBackgroundResource(isDark
                ? R.drawable.btn_dark_rounded_play_all : R.drawable.btn_light_rounded_play_all);
        headerViewBinding.layoutRoot.setBackgroundResource(isDark ? R.drawable.bg_dark_header_podcast : R.drawable.bg_light_header_podcast);
    }

    @Override
    public void onExtractData(Bundle args) {
        super.onExtractData(args);
        if (args != null) {
            podCastModel = args.getParcelable(KEY_MODEL);
        }
    }

    @Override
    public void updateDarkMode(boolean isDark) {
        super.updateDarkMode(isDark);
        if (headerViewBinding != null) {
            headerViewBinding.btnPlayAll.setBackgroundResource(isDark ? R.drawable.btn_dark_rounded_play_all : R.drawable.btn_light_rounded_play_all);
            headerViewBinding.layoutRoot.setBackgroundResource(isDark ? R.drawable.bg_dark_header_podcast : R.drawable.bg_light_header_podcast);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (podCastModel != null) {
            outState.putParcelable(KEY_MODEL, podCastModel);
        }
    }

    @Override
    public void notifyData(int pos) {
        super.notifyData(pos + 1);
    }
}
