package com.onlineradiofm.trancemusicradio.itunes.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.google.gson.annotations.SerializedName;

/**
 * @author:YPY Global
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: bl911vn@gmail.com
 * @Website: http://ypyglobal.com
 * Created by dotrungbao on 2020-01-16.
 */
public class PodCastModel extends AbstractModel implements Parcelable {

    @SerializedName(value = "trackId")
    private long trackId;

    @SerializedName(value = "trackName")
    private String trackName;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("artworkUrl100")
    private String artwork;

    @SerializedName("releaseDate")
    private String releaseDate;

    @SerializedName("feedUrl")
    private String feedUrl;

    @SerializedName("trackCount")
    private long trackCount;

    @SerializedName("description")
    private String description;

    @SerializedName("link")
    private String link;


    public PodCastModel(boolean isShowAds) {
        super(isShowAds);
    }

    public PodCastModel(long id, String name, String image) {
        super(id,name,image);
    }

    @Override
    public long getId() {
        if(trackId>0){
            return trackId;
        }
        return super.getId();
    }

    @Override
    public String getName() {
        if(!TextUtils.isEmpty(trackName)){
            return trackName;
        }
        return super.getName();
    }

    public long getTrackCount() {
        return trackCount;
    }

    @Override
    public String getArtWork() {
        if(!TextUtils.isEmpty(artwork) && artwork.contains("100x100")){
            artwork = artwork.replace("100x100", "600x600");
            return artwork;
        }
        return artwork;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getArtistName() {
        return artistName;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
        dest.writeLong(this.trackId);
        dest.writeString(this.trackName);
        dest.writeString(this.artistName);
        dest.writeString(this.artwork);
        dest.writeString(this.releaseDate);
        dest.writeString(this.feedUrl);
        dest.writeLong(this.trackCount);
        dest.writeString(this.description);
        dest.writeString(this.link);
    }

    private PodCastModel(Parcel in) {
        super(in);
        this.trackId = in.readLong();
        this.trackName = in.readString();
        this.artistName = in.readString();
        this.artwork = in.readString();
        this.releaseDate = in.readString();
        this.feedUrl = in.readString();
        this.trackCount = in.readLong();
        this.description = in.readString();
        this.link = in.readString();
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

