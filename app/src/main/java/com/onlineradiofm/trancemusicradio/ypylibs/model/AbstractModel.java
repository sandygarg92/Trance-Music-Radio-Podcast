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

package com.onlineradiofm.trancemusicradio.ypylibs.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.gson.annotations.SerializedName;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/27/17.
 */

public class AbstractModel implements Parcelable {

    @SerializedName("id")
    protected long id;

    @SerializedName("name")
    protected String name;

    @SerializedName("img")
    protected String image;

    protected transient boolean isFavorite;

    private transient boolean isShowAds;
    private transient View nativeAdView;
    private transient AdLoader adLoader;

    public AbstractModel(boolean isShowAds) {
        this.isShowAds = isShowAds;
    }

    public AbstractModel(long id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getArtWork() {
        return image;
    }

    public String getTypeName() {
        return null;
    }

    public String getShareStr() {
        return null;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public AbstractModel cloneObject() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractModel) {
            AbstractModel model = (AbstractModel) obj;
            return id != 0 && id == model.getId();
        }
        return false;
    }

    public View getNativeAdView() {
        return nativeAdView;
    }

    public void setNativeAdView(View nativeAdView) {
        this.nativeAdView = nativeAdView;
    }

    public void onDestroyAds() {
        try {
            if (isShowAds) {
                if (nativeAdView instanceof NativeAdView) {
                    ((NativeAdView) nativeAdView).destroy();
                }
                if (nativeAdView instanceof TemplateView) {
                    ((TemplateView) nativeAdView).destroyNativeAd();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isShowAds() {
        return isShowAds;
    }

    public boolean isRequestAd() {
        return adLoader != null;
    }

    public void setAdLoader(AdLoader adLoader) {
        this.adLoader = adLoader;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.image);
    }

    protected AbstractModel(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.image = in.readString();
    }

    public static final Creator<AbstractModel> CREATOR = new Creator<>() {
        @Override
        public AbstractModel createFromParcel(Parcel source) {
            return new AbstractModel(source);
        }

        @Override
        public AbstractModel[] newArray(int size) {
            return new AbstractModel[size];
        }
    };

}
