package com.onlineradiofm.trancemusicradio.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.google.gson.annotations.SerializedName;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-01-16.
 */
public class PodCastModel extends AbstractModel implements Parcelable {

    @SerializedName("link_rss")
    private String feedUrl;

    @SerializedName("short_des")
    private String shortDes;

    @SerializedName("des")
    private String description;

    @SerializedName("link_info")
    private String linkInfo;

    public PodCastModel(boolean isShowAds) {
        super(isShowAds);
    }

    public PodCastModel(long id, String name, String image) {
        super(id, name, image);
    }

    @Override
    public String getArtWork() {
        if (!TextUtils.isEmpty(image) && !image.startsWith("http")) {
            image = XRadioNetUtils.URL_HOST + XRadioNetUtils.FOLDER_PODCASTS + image;
        }
        return super.getImage();
    }

    public String getShortDes() {
        return shortDes;
    }

    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(String linkInfo) {
        this.linkInfo = linkInfo;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.feedUrl);
        dest.writeString(this.linkInfo);
        dest.writeString(this.description);
        dest.writeString(this.shortDes);
    }

    private PodCastModel(Parcel in) {
        super(in);
        this.feedUrl = in.readString();
        this.linkInfo = in.readString();
        this.description = in.readString();
        this.shortDes = in.readString();
    }

    public static final Parcelable.Creator<PodCastModel> CREATOR = new Parcelable.Creator<PodCastModel>() {
        @Override
        public PodCastModel createFromParcel(Parcel source) {
            return new PodCastModel(source);
        }

        @Override
        public PodCastModel[] newArray(int size) {
            return new PodCastModel[size];
        }
    };
}

