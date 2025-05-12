package com.onlineradiofm.trancemusicradio.itunes.model;

import com.onlineradiofm.trancemusicradio.model.PodCastModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-01-16.
 */
public class FeedModel {

    @SerializedName("results")
    private ArrayList<PodCastModel> listPodcast;

    public FeedModel(ArrayList<PodCastModel> listTracks) {
        this.listPodcast = listTracks;
    }

    public ArrayList<PodCastModel> getListPodcast() {
        return listPodcast;
    }
}
