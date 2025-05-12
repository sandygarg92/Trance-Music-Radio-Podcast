package com.onlineradiofm.trancemusicradio.itunes.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author:YPY Global
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: bl911vn@gmail.com
 * @Website: http://ypyglobal.com
 * Created by dotrungbao on 2020-01-16.
 */
public class SearchResultModel {

    @SerializedName("resultCount")
    private int resultCount;

    @SerializedName("results")
    private ArrayList<PodCastModel> listPodcasts;

    public SearchResultModel(int resultCount, ArrayList<PodCastModel> listTracks) {
        this.resultCount = resultCount;
        this.listPodcasts = listTracks;
    }

    public int getResultCount() {
        return resultCount;
    }

    public ArrayList<PodCastModel> getListPodcasts() {
        return listPodcasts;
    }
}
