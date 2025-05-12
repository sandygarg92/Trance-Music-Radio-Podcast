package com.onlineradiofm.trancemusicradio.ypylibs.music.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.DrawableRes;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2019-06-06.
 */
public class YPYMusicModel extends AbstractModel implements Parcelable {

    private transient long duration;

    @SerializedName("path")
    protected String path;

    public YPYMusicModel(long id, String name, String image) {
        super(id, name, image);
    }

    public YPYMusicModel(boolean isShowAds) {
        super(isShowAds);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getLinkToPlay(Context mContext){
        return null;
    }

    public String getArtist() {
        return null;
    }

    public String getSong() {
        return null;
    }

    public boolean isUseYPYPlayer(){
        return true;
    }

    public boolean isLive(){
        return false;
    }

    @DrawableRes
    public int getResImgDefault(){
        return 0;
    }

    public boolean isOfflineModel(){
        return !TextUtils.isEmpty(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeString(this.path);
    }

    protected YPYMusicModel(Parcel in) {
        super(in);
        this.path = in.readString();
    }

    public static final Creator<YPYMusicModel> CREATOR = new Creator<>() {
        @Override
        public YPYMusicModel createFromParcel(Parcel source) {
            return new YPYMusicModel(source);
        }

        @Override
        public YPYMusicModel[] newArray(int size) {
            return new YPYMusicModel[size];
        }
    };


}
