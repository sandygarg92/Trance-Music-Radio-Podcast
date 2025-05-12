package com.onlineradiofm.trancemusicradio.itunes.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-01-16.
 */
public class TopITunesModel {
    @SerializedName("feed")
    private FeedModel feedModel;

    public TopITunesModel(FeedModel feedModel) {
        this.feedModel = feedModel;
    }

    public FeedModel getFeedModel() {
        return feedModel;
    }
}
