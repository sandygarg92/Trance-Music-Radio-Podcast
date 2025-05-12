/*
 * Copyright (c) 2017. Radio Polska - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://radiopolska.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.onlineradiofm.trancemusicradio.dataMng;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.model.CountryModel;
import com.onlineradiofm.trancemusicradio.model.GenreModel;
import com.onlineradiofm.trancemusicradio.model.PodCastModel;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.model.TopRadioModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.DownloadUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.StringUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */

public class XRadioNetUtils implements IRadioConstants {

    public static final String URL_HOST = "https://appsoup.net/trance-app/";
    public static final String API_KEY = "eHJhZAAvcGVyZZVjdGFwAA1";

    private static final String FORMAT_API_END_POINT = FOLDER_API + "api.php?method=%1$s";
    private static final String METHOD_GET_GENRES = "getGenres";
    private static final String METHOD_GET_RADIOS = "getRadios";
    private static final String METHOD_GET_COUNTRIES = "getCountries";
    private static final String METHOD_TRENDING_RADIOS = "getTrendingRadios";
    private static final String METHOD_TOP_RADIOS = "getTopRadios";
    private static final String METHOD_FAV_RADIOS = "getFavRadios";
    private static final String METHOD_GET_PODCASTS = "getPodcasts";

    private static final String KEY_API = "&api_key=";
    private static final String KEY_QUERY = "&q=";
    private static final String KEY_TYPE = "&type=";
    private static final String KEY_GENRE_ID = "&genre_id=";
    private static final String KEY_OFFSET = "&offset=";
    private static final String KEY_LIMIT = "&limit=";
    private static final String KEY_USER_ID = "&user_id=";
    private static final String KEY_TOKEN = "&token=";
    private static final String KEY_IS_FEATURE = "&is_feature=1";
    private static final String KEY_COUNTY_ID = "&country_id=";
    private static final String KEY_RADIO_ID = "&radio_id=";
    private static final String KEY_GENRE_ID2 = "&genre_id=";

    public static final String FOLDER_GENRES = "/uploads/genres/";
    public static final String FOLDER_PODCASTS = "/uploads/podcasts/";
    public static final String FOLDER_RADIOS = "/uploads/radios/";
    public static final String FOLDER_THEMES = "/uploads/themes/";
    public static final String FOLDER_COUNTRIES = "/uploads/countries/";

    private static final String FORMAT_ITUNES = "https://itunes.apple.com/search?term=%1$s&entity=song&limit=1";


    public static ResultModel<GenreModel> getListGenreModel() {
        try {
            String url = URL_HOST + String.format(FORMAT_API_END_POINT, METHOD_GET_GENRES) +
                    KEY_API + API_KEY;
            YPYLog.e(TAG, "==========>getListGenreModel=" + url);
            Reader mInputStream = DownloadUtils.downloadReader(url);
            Type mTypeToken = new TypeToken<ResultModel<GenreModel>>() {
            }.getType();
            return JsonParsingUtils.getResultModel(mInputStream, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ResultModel<CountryModel> getListCountryModel() {
        try {
            String url = URL_HOST + String.format(FORMAT_API_END_POINT, METHOD_GET_COUNTRIES) +
                    KEY_API + API_KEY;
            YPYLog.e(TAG, "====>getListCountryModel=" + url);
            Reader mInputStream = DownloadUtils.downloadReader(url);
            Type mTypeToken = new TypeToken<ResultModel<CountryModel>>() {
            }.getType();
            return JsonParsingUtils.getResultModel(mInputStream, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultModel<RadioModel> getListRadioModel(Context mContext, long countryId, long genreId, int offset, int limit) {
        return getListRadioModel(mContext, countryId, genreId, null, offset, limit, false);
    }

    public static ResultModel<RadioModel> searchRadioModel(Context mContext, String query, int offset, int limit) {
        return getListRadioModel(mContext, -1, -1, query, offset, limit, false);
    }

    public static ResultModel<RadioModel> getListRadioModel(Context mContext, long countryId, int offset, int limit) {
        return getListRadioModel(mContext, countryId, -1, null, offset, limit, false);
    }

    public static ResultModel<RadioModel> getFavRadios(Context mContext, int offset, int limit) {
        try {
            StringBuilder mStringBuilder = new StringBuilder(URL_HOST);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT, METHOD_FAV_RADIOS));
            mStringBuilder.append(KEY_API).append(API_KEY);
            if (limit > 0) {
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            mStringBuilder.append(KEY_OFFSET).append(offset);
            mStringBuilder.append(KEY_USER_ID).append(XRadioSettingManager.getUserId(mContext));
            mStringBuilder.append(KEY_TOKEN).append(XRadioSettingManager.getUserToken(mContext));
            String url = mStringBuilder.toString();
            YPYLog.e(TAG, "==========>getListHeaderTopModels=" + url);
            Type mTypeToken = new TypeToken<ResultModel<RadioModel>>() {
            }.getType();
            return getListDataFromServer(url, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultModel<TopRadioModel> getListHeaderTopModels(Context mContext, int limit) {
        try {
            StringBuilder mStringBuilder = new StringBuilder(URL_HOST);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT, METHOD_TOP_RADIOS));
            mStringBuilder.append(KEY_API).append(API_KEY);
            if (limit > 0) {
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            mStringBuilder.append(KEY_OFFSET).append(0);
            if (XRadioSettingManager.isSignedIn(mContext)) {
                mStringBuilder.append(KEY_USER_ID).append(XRadioSettingManager.getUserId(mContext));
                mStringBuilder.append(KEY_TOKEN).append(XRadioSettingManager.getUserToken(mContext));
            }
            String url = mStringBuilder.toString();
            YPYLog.e(TAG, "==========>getListHeaderTopModels=" + url);
            Type mTypeToken = new TypeToken<ResultModel<TopRadioModel>>() {
            }.getType();
            return getListDataFromServer(url, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ResultModel<RadioModel> getListTopRadios(Context mContext, int offset, int limit) {
        try {
            StringBuilder mStringBuilder = new StringBuilder(URL_HOST);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT, METHOD_GET_RADIOS));
            mStringBuilder.append(KEY_API).append(API_KEY).append(KEY_GENRE_ID2);
            if (offset >= 0) {
                mStringBuilder.append(KEY_OFFSET).append(offset);
            }
            if (limit > 0) {
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            if (XRadioSettingManager.isSignedIn(mContext)) {
                mStringBuilder.append(KEY_USER_ID).append(XRadioSettingManager.getUserId(mContext));
                mStringBuilder.append(KEY_TOKEN).append(XRadioSettingManager.getUserToken(mContext));
            }
            String url = mStringBuilder.toString();
            YPYLog.e(TAG, "==========>getListTrendingRadios=" + url);
            Type mTypeToken = new TypeToken<ResultModel<RadioModel>>() {
            }.getType();
            return getListDataFromServer(url, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultModel<RadioModel> getListTopRadioModels(Context mContext, String type, int offset, int limit) {
        try {
            StringBuilder mStringBuilder = new StringBuilder(URL_HOST);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT, METHOD_TOP_RADIOS));
            mStringBuilder.append(KEY_API).append(API_KEY);
            mStringBuilder.append(KEY_TYPE).append(type);
            if (offset >= 0) {
                mStringBuilder.append(KEY_OFFSET).append(offset);
            }
            if (limit > 0) {
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            if (XRadioSettingManager.isSignedIn(mContext)) {
                mStringBuilder.append(KEY_USER_ID).append(XRadioSettingManager.getUserId(mContext));
                mStringBuilder.append(KEY_TOKEN).append(XRadioSettingManager.getUserToken(mContext));
            }
            String url = mStringBuilder.toString();
            YPYLog.e(TAG, "==========>getListHeaderTopModels=" + url);
            Type mTypeToken = new TypeToken<ResultModel<RadioModel>>() {
            }.getType();
            return getListDataFromServer(url, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultModel<RadioModel> getListTrendingRadios(Context mContext, int offset, int limit) {
        try {
            StringBuilder mStringBuilder = new StringBuilder(URL_HOST);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT, METHOD_TRENDING_RADIOS));
            mStringBuilder.append(KEY_API).append(API_KEY);
            if (offset >= 0) {
                mStringBuilder.append(KEY_OFFSET).append(offset);
            }
            if (limit > 0) {
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            if (XRadioSettingManager.isSignedIn(mContext)) {
                mStringBuilder.append(KEY_USER_ID).append(XRadioSettingManager.getUserId(mContext));
                mStringBuilder.append(KEY_TOKEN).append(XRadioSettingManager.getUserToken(mContext));
            }
            String url = mStringBuilder.toString();
            YPYLog.e(TAG, "==========>getListTrendingRadios=" + url);
            Type mTypeToken = new TypeToken<ResultModel<RadioModel>>() {
            }.getType();
            return getListDataFromServer(url, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultModel<PodCastModel> getPodcasts(int offset, int limit, boolean isFeature, String query) {
        try {
            StringBuilder mStringBuilder = new StringBuilder(URL_HOST);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT, METHOD_GET_PODCASTS));
            mStringBuilder.append(KEY_API).append(API_KEY);
            if (offset >= 0) {
                mStringBuilder.append(KEY_OFFSET).append(offset);
            }
            if (limit > 0) {
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            if (isFeature) {
                mStringBuilder.append(KEY_IS_FEATURE);
            }
            if (!TextUtils.isEmpty(query)) {
                mStringBuilder.append(KEY_QUERY).append(StringUtils.urlEncodeString(query));
            }
            String url = mStringBuilder.toString();
            YPYLog.e(TAG, "==========>getPodcasts=" + url);
            Type mTypeToken = new TypeToken<ResultModel<PodCastModel>>() {
            }.getType();
            return getListDataFromServer(url, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultModel<RadioModel> getLastPlayedRadios(Context mContext) {
        try {
            long radioId = XRadioSettingManager.getLastPlayedRadioId(mContext);
            String url = URL_HOST + String.format(FORMAT_API_END_POINT, METHOD_GET_RADIOS) +
                    KEY_API + API_KEY +
                    KEY_RADIO_ID + radioId;
            YPYLog.e(TAG, "==========>getLastPlayedRadios=" + url);
            Type mTypeToken = new TypeToken<ResultModel<RadioModel>>() {
            }.getType();
            return getListDataFromServer(url, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static ResultModel<RadioModel> getListRadioModel(Context mContext, long countryId, long genreId, String query, int offset, int limit, boolean isFeature) {
        try {
            StringBuilder mStringBuilder = new StringBuilder(URL_HOST);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT, METHOD_GET_RADIOS));
            mStringBuilder.append(KEY_API).append(API_KEY);
            if (offset >= 0) {
                mStringBuilder.append(KEY_OFFSET).append(offset);
            }
            if (limit > 0) {
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            if (genreId > 0) {
                mStringBuilder.append(KEY_GENRE_ID).append(genreId);
            }
            if (countryId > 0) {
                mStringBuilder.append(KEY_COUNTY_ID).append(countryId);
            }
            if (isFeature) {
                mStringBuilder.append(KEY_IS_FEATURE);
            }
            if (!TextUtils.isEmpty(query)) {
                mStringBuilder.append(KEY_QUERY).append(StringUtils.urlEncodeString(query));
            }
            if (XRadioSettingManager.isSignedIn(mContext)) {
                mStringBuilder.append(KEY_USER_ID).append(XRadioSettingManager.getUserId(mContext));
                mStringBuilder.append(KEY_TOKEN).append(XRadioSettingManager.getUserToken(mContext));
            }
            String url = mStringBuilder.toString();
            YPYLog.e(TAG, "==========>getListRadioModel=" + url);
            Type mTypeToken = new TypeToken<ResultModel<RadioModel>>() {
            }.getType();
            return getListDataFromServer(url, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> ResultModel<T> getListDataFromServer(String url, Type mTypeToken) {
        try {
            Reader mInputStream = DownloadUtils.downloadReader(url);
            return JsonParsingUtils.getResultModel(mInputStream, mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImageOfSong(String title, String artist) {
        try {
            StringBuilder mStringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(artist)) {
                mStringBuilder.append(artist);
                mStringBuilder.append("-");
            }
            if (!TextUtils.isEmpty(title)) {
                mStringBuilder.append(title);
            }
            String data = mStringBuilder.toString().replaceAll("\\s+", "-");

            String ituneURL = String.format(FORMAT_ITUNES, StringUtils.urlEncodeString(data));
            String mImg = JsonParsingUtils.parsingImageSongFromItunes(DownloadUtils.downloadString(ituneURL));
            Log.e("DCM", "======>itunes IMAGE=" + mImg + "==>url=" + ituneURL);
            if (!TextUtils.isEmpty(mImg)) {
                return mImg;
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
