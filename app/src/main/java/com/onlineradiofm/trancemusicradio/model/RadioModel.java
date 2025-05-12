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

package com.onlineradiofm.trancemusicradio.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.dataMng.MediaStoreManager;
import com.onlineradiofm.trancemusicradio.dataMng.TotalDataManager;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.db.entity.RMRadioEntity;
import com.onlineradiofm.trancemusicradio.ypylibs.music.model.YPYMusicModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.DownloadUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.StringUtils;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.DOWNLOAD_SEPARATOR;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.FORMAT_SAVED;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.LIST_STORAGE_PERMISSIONS;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.PREFIX_CONTENT;

public class RadioModel extends YPYMusicModel {

    @SerializedName("bitrate")
    private String bitRate;

    @SerializedName("tags")
    private String tags;

    @SerializedName("type_radio")
    private String typeRadio;

    @SerializedName("source_radio")
    private String sourceRadio;

    @SerializedName("link_radio")
    private String linkRadio;

    @SerializedName("user_agent_radio")
    private String userAgentRadio;

    @SerializedName("url_facebook")
    private String urlFacebook;

    @SerializedName("url_twitter")
    private String urlTwitter;

    @SerializedName("url_instagram")
    private String urlInstagram;

    @SerializedName("url_website")
    private String urlWebsite;

    @SerializedName("views")
    private long viewCount;

    @SerializedName("is_podcast")
    private boolean isPodCast;

    @SerializedName("is_fav")
    private int isFav;

    @SerializedName("is_uploaded")
    private boolean isUploaded;

    @SerializedName("is_my_radio")
    private boolean isMyRadio;

    private transient String song;
    private transient String artist;
    private transient String strViewCount;
    private transient long date;
    private transient String songImg;

    private transient boolean isCalculateCount;
    private transient String mediaPath;

    public RadioModel(long id, String name, String image) {
        super(id, name, image);
    }

    public RadioModel(String name, String path) {
        super(0, name, null);
        this.path = path;
    }

    public RadioModel(boolean isShowAds) {
        super(isShowAds);
    }

    public String getBitRate() {
        if (!TextUtils.isEmpty(bitRate) && TextUtils.isDigitsOnly(bitRate)) {
            return bitRate + " Kb";
        }
        return bitRate;
    }

    public String getTags() {
        return tags;
    }

    public String getLinkRadio() {
        return linkRadio;
    }

    @Override
    public boolean isOfflineModel() {
        return isSourceOther() || super.isOfflineModel();
    }

    public boolean isOfflineFile() {
        return path != null && !TextUtils.isEmpty(path);
    }

    @Override
    public String getLinkToPlay(@NonNull Context mContext) {
        try {
            if (isPodCast) {
                //TODO return uri from media store
                boolean isGranted = ApplicationUtils.isGrantAllPermission(mContext, LIST_STORAGE_PERMISSIONS);
                if(isGranted){
                    Uri uri = MediaStoreManager.getUriOfTrackAndroid(mContext,this);
                    if (uri != null) return uri.toString();
                }
                String offlineFile = TotalDataManager.getInstance(mContext).getFileDownloaded(mContext, this);
                if (!TextUtils.isEmpty(offlineFile)) {
                    return offlineFile;
                }
            }
            if (isOfflineFile()) {
                if (path.startsWith(PREFIX_CONTENT)) return path;
                File mFile = new File(path);
                if (mFile.exists() && mFile.isFile()) {
                    return path;
                }
            }
            if (!TextUtils.isEmpty(linkRadio)) {
                if (linkRadio.toLowerCase().contains(".m3u8")) {
                    return linkRadio;
                }
                else if (linkRadio.toLowerCase().contains(".pls")) {
                    if (linkRadio.contains("listen.pls?")) {
                        return linkRadio.substring(0, linkRadio.indexOf("listen.pls?"));
                    }
                    else {
                        String data = null;
                        if (ApplicationUtils.isOnline(mContext)) {
                            data = DownloadUtils.downloadString(linkRadio);
                        }
                        if (data != null && !TextUtils.isEmpty(data)) {
                            String[] datas = data.split("\\n");
                            if (datas.length > 0) {
                                for (String mStr : datas) {
                                    if (mStr.contains("File")) {
                                        String[] urls = mStr.split("=+");
                                        if (urls.length >= 2) {
                                            return urls[1];
                                        }
                                    }
                                }
                                return data;
                            }
                        }
                    }
                }
                else if (linkRadio.toLowerCase().contains(".m3u")) {
                    String data = null;
                    if (ApplicationUtils.isOnline(mContext)) {
                        data = DownloadUtils.downloadString(linkRadio);
                    }
                    if (!TextUtils.isEmpty(data)) {
                        return data;
                    }
                    else {
                        return linkRadio.replace(".m3u", "");
                    }
                }
                if (isUseYPYPlayer()) {
                    return linkRadio + "_Other";
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return linkRadio;
    }


    private void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setTypeRadio(String typeRadio) {
        this.typeRadio = typeRadio;
    }

    public void setSourceRadio(String sourceRadio) {
        this.sourceRadio = sourceRadio;
    }

    public void setLinkRadio(String linkRadio) {
        this.linkRadio = linkRadio;
    }

    private void setUserAgentRadio(String userAgentRadio) {
        this.userAgentRadio = userAgentRadio;
    }

    private void setUrlFacebook(String urlFacebook) {
        this.urlFacebook = urlFacebook;
    }

    private void setUrlTwitter(String urlTwitter) {
        this.urlTwitter = urlTwitter;
    }

    private void setUrlWebsite(String urlWebsite) {
        this.urlWebsite = urlWebsite;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public RadioModel cloneObject() {
        RadioModel model = new RadioModel(id, name, image);
        model.setFavorite(isFavorite);
        model.setBitRate(bitRate);
        model.setLinkRadio(linkRadio);
        model.setTypeRadio(typeRadio);
        model.setSourceRadio(sourceRadio);
        model.setTags(tags);
        model.setUrlFacebook(urlFacebook);
        model.setUrlTwitter(urlTwitter);
        model.setUrlWebsite(urlWebsite);
        model.setUploaded(isUploaded);
        model.setUrlInstagram(urlInstagram);
        model.setUserAgentRadio(userAgentRadio);
        model.setIsPodCast(isPodCast);
        model.setPath(path);
        model.setMyRadio(isMyRadio);
        return model;
    }

    public boolean isMyRadio() {
        return isMyRadio;
    }

    public void setMyRadio(boolean myRadio) {
        isMyRadio = myRadio;
    }

    @Override
    public String getArtWork() {
        if (!TextUtils.isEmpty(image) && !image.startsWith("http")) {
            image = XRadioNetUtils.URL_HOST + XRadioNetUtils.FOLDER_RADIOS + image;
        }
        return super.getImage();
    }

    @Override
    public String getSong() {
        return name;
    }

    @Override
    public String getArtist() {
        StringBuilder mStringBuilder = new StringBuilder();
        if (song != null && !TextUtils.isEmpty(song)) {
            mStringBuilder.append(song);
        }
        if (artist != null && !TextUtils.isEmpty(artist)) {
            if (mStringBuilder.length() > 0) {
                mStringBuilder.append(" - ");
            }
            mStringBuilder.append(artist);
        }
        if (mStringBuilder.length() == 0) {
            mStringBuilder.append(tags);
        }
        return mStringBuilder.toString();
    }

    public void setSong(String song) {
        this.song = song;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    @Override
    public boolean isLive() {
        if (isOfflineModel() || isPodCast) return false;
        return !isSourceOther();
    }

    private boolean isSourceOther() {
        return !TextUtils.isEmpty(sourceRadio) && sourceRadio.equalsIgnoreCase("Other");
    }

    @Override
    public String getShareStr() {
        StringBuilder mStringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(name)) {
            mStringBuilder.append(name).append("\n");
        }
        if (isMyRadio) {
            mStringBuilder.append(linkRadio).append("\n");
        }
        return mStringBuilder.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RadioModel) {
            RadioModel model = (RadioModel) obj;
            return id != 0 && id == model.getId() && isMyRadio == model.isMyRadio();
        }
        return false;
    }

    public String getMetaData() {
        if (!TextUtils.isEmpty(song)) {
            StringBuilder mStringBuilder = new StringBuilder();
            mStringBuilder.append(song);
            if (!TextUtils.isEmpty(artist)) {
                mStringBuilder.append(" - ");
                mStringBuilder.append(artist);
            }
            return mStringBuilder.toString();

        }
        return null;
    }

    private void setUrlInstagram(String urlInstagram) {
        this.urlInstagram = urlInstagram;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isCalculateCount() {
        if (isPodCast) return true;
        return isCalculateCount;
    }

    public void setCalculateCount(boolean calculateCount) {
        isCalculateCount = calculateCount;
    }

    public long getViewCount() {
        return viewCount;
    }

    public String getStrViewCount() {
        if (TextUtils.isEmpty(strViewCount)) {
            strViewCount = StringUtils.formatNumberSocial(viewCount);
        }
        return strViewCount;
    }

    @Override
    public boolean isFavorite() {
        return super.isFavorite() || isFav > 0;
    }

    @Override
    public int getResImgDefault() {
        return R.drawable.ic_light_play_default;
    }

    public String getSongImg() {
        if (!TextUtils.isEmpty(songImg)) {
            return songImg;
        }
        return getArtWork();
    }

    public void setSongImg(String songImg) {
        this.songImg = songImg;
    }

    public void setIsPodCast(boolean isPodCast) {
        this.isPodCast = isPodCast;
    }

    public boolean isPodCast() {
        return isPodCast;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }


    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    @Override
    public boolean isUseYPYPlayer() {
        if (isPodCast || isOfflineModel()) {
            return false;
        }
        return super.isUseYPYPlayer();
    }

    public MediaInfo getMediaInfo(@NonNull Context mContext) {
        String urlStream = getLinkToPlay(mContext);
        if (!TextUtils.isEmpty(urlStream)) {
            MediaMetadata radioMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);
            radioMetadata.putString(MediaMetadata.KEY_TITLE, name);
            if (!TextUtils.isEmpty(tags)) {
                radioMetadata.putString(MediaMetadata.KEY_SUBTITLE, tags);
                radioMetadata.putString(MediaMetadata.KEY_ARTIST, tags);
            }
            else {
                radioMetadata.putString(MediaMetadata.KEY_SUBTITLE, mContext.getString(R.string.app_name));
                radioMetadata.putString(MediaMetadata.KEY_ARTIST, mContext.getString(R.string.app_name));
            }
            String img = getArtWork();
            if (!TextUtils.isEmpty(img) && img.startsWith("http")) {
                radioMetadata.addImage(new WebImage(Uri.parse(img)));
                radioMetadata.addImage(new WebImage(Uri.parse(img)));
            }
            else {
                radioMetadata.addImage(new WebImage(Uri.parse(IRadioConstants.URL_IMAGE_DEFAULT_FOR_CHROME_CAST)));
                radioMetadata.addImage(new WebImage(Uri.parse(IRadioConstants.URL_IMAGE_DEFAULT_FOR_CHROME_CAST)));
            }
            boolean isNeedAppend = true;
            int lastIndex = urlStream.lastIndexOf("/");
            if (lastIndex >= 0) {
                String subStr = urlStream.substring(lastIndex + 1);
                if (subStr.equals(";") || subStr.length() > 0) {
                    isNeedAppend = false;
                }
            }
            if (isNeedAppend && !urlStream.toLowerCase().contains(".mp3") && !urlStream.toLowerCase().contains(".aac")
                    && !urlStream.toLowerCase().contains("/stream")
                    && sourceRadio != null && !sourceRadio.equalsIgnoreCase("other")) {
                if (urlStream.endsWith("/")) {
                    urlStream = urlStream + ";";
                }
                else {
                    urlStream = urlStream + "/;";
                }
            }
            MediaInfo.Builder builder = new MediaInfo.Builder(urlStream)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(radioMetadata);

            if (urlStream.toLowerCase().contains(".m3u8")) {
                builder.setContentType("application/x-mpegurl");
            }
            else {
                if (!TextUtils.isEmpty(typeRadio)) {
                    if (typeRadio.equalsIgnoreCase("MP3")) {
                        builder.setContentType("audio/mp3");
                    }
                    else if (typeRadio.equalsIgnoreCase("AAC")) {
                        builder.setContentType("audio/aac");
                    }
                }
            }
            return builder.build();
        }

        return null;
    }

    public boolean canDownload() {
        return isPodCast;
    }

    public String getNameFileDownload() {
        if (linkRadio != null && !TextUtils.isEmpty(linkRadio)) {
            return ApplicationUtils.getMd5Hash(linkRadio) + FORMAT_SAVED;
        }
        return "";
    }

    public String getMediaStoreNameFile() {
        String displayName = getDisplayNameInMediaStore();
        return !TextUtils.isEmpty(displayName) ? displayName + FORMAT_SAVED : "";
    }

    public String getDisplayNameInMediaStore() {
        StringBuilder mStrBuilder = new StringBuilder();
        mStrBuilder.append(id);
        mStrBuilder.append(DOWNLOAD_SEPARATOR);
        mStrBuilder.append(StringUtils.urlEncodeString(name));

        String artist = getArtist();
        if (!TextUtils.isEmpty(artist)) {
            mStrBuilder.append(DOWNLOAD_SEPARATOR);
            mStrBuilder.append(StringUtils.urlEncodeString(artist));
        }

        String image = getFileNameImage();
        if (image != null && !TextUtils.isEmpty(image)) {
            mStrBuilder.append(DOWNLOAD_SEPARATOR);
            mStrBuilder.append(StringUtils.urlEncodeString(image));
        }

        return mStrBuilder.toString();
    }

    public String getPrefixMediaStore() {
        return id + DOWNLOAD_SEPARATOR;
    }

    private String getFileNameImage() {
        String image = this.getImage();
        if (image != null && !TextUtils.isEmpty(image)) {
            if (image.startsWith("http")) {
                int lastIndex = image.lastIndexOf("/");
                if (lastIndex >= 0) {
                    image = image.substring(lastIndex + 1);
                }
            }
        }
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.sourceRadio);
        dest.writeString(this.linkRadio);
        dest.writeByte(this.isMyRadio ? (byte) 1 : (byte) 0);
    }

    protected RadioModel(Parcel in) {
        super(in);
        this.sourceRadio = in.readString();
        this.linkRadio = in.readString();
        this.isMyRadio = in.readByte() != 0;
    }

    public static final Creator<RadioModel> CREATOR = new Creator<>() {
        @Override
        public RadioModel createFromParcel(Parcel source) {
            return new RadioModel(source);
        }

        @Override
        public RadioModel[] newArray(int size) {
            return new RadioModel[size];
        }
    };

    public RMRadioEntity createRadioEntity() {
        RMRadioEntity entity = new RMRadioEntity(name, linkRadio, isSourceOther() ? 1 : 0);
        entity.id = id;
        return entity;
    }

    @Nullable
    public static RadioModel createEpisodeFromMediaStore(@NonNull Uri uri, @NonNull String displayName) {
        try {
            String data = displayName.replace(FORMAT_SAVED, "");
            String[] listData = data.split(DOWNLOAD_SEPARATOR);
            int size = listData.length;
            if (size >= 2) {
                long id = Long.parseLong(listData[0]);
                String name = StringUtils.urlDecodeString(listData[1]);
                String nextData = size >= 3 ? StringUtils.urlDecodeString(listData[2]) : "";
                String image = size >= 4 ? listData[3] : "";
                String artist = "";
                if (nextData.toLowerCase(Locale.ROOT).endsWith(".png") || nextData.toLowerCase(Locale.ROOT).endsWith(".jpg")) {
                    image = nextData;
                }
                else {
                    artist = nextData;
                }
                if(!TextUtils.isEmpty(image) && !image.startsWith("http")){
                    image = XRadioNetUtils.URL_HOST + XRadioNetUtils.FOLDER_PODCASTS + image;
                }
                RadioModel radio = new RadioModel(id, name, image);
                radio.setTags(artist);
                radio.setArtist(artist);
                radio.setIsPodCast(true);
                radio.setPath(uri.toString());
                return radio;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }
}
