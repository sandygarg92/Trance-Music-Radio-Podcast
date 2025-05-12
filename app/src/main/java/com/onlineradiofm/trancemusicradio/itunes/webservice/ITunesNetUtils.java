/*
 * Copyright (c) 2017. YPY Global - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://ypyglobal.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.onlineradiofm.trancemusicradio.itunes.webservice;

import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.itunes.constants.IITunesConstants;
import com.onlineradiofm.trancemusicradio.itunes.model.SearchResultModel;
import com.onlineradiofm.trancemusicradio.itunes.model.TopITunesModel;
import com.onlineradiofm.trancemusicradio.itunes.model.rss.RssFeedModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.DownloadUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.io.Reader;

/**
 * @author:YPY Global
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: bl911vn@gmail.com
 * @Website: www.ypyglobal.com
 * @Date:Oct 20, 2017
 */

public class ITunesNetUtils implements IITunesConstants {

    public static TopITunesModel getRssITunesModel(String country, String mediaType, String feedType, int limit){
        try{
            String urlRss = String.format(FORMAT_URL_ITUNES_TOP_CHART,country,mediaType,feedType,limit);
            YPYLog.e("DCM","==========>urlRss="+urlRss);
            return getJsonModelFromServer(urlRss, TopITunesModel.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static SearchResultModel getITunesSearchResultModel(String term, String mediaType, String entity, int limit){
        try{
            StringBuilder mStringBuilder =new StringBuilder(FORMAT_URL_ITUNES_SEARCH);
            if(!TextUtils.isEmpty(term)){
                mStringBuilder.append(PARAM_TERM).append(term);
            }
            if(!TextUtils.isEmpty(mediaType)){
                mStringBuilder.append(PARAM_MEDIA).append(mediaType);
            }
            if(!TextUtils.isEmpty(entity)){
                mStringBuilder.append(PARAM_ENTITY).append(entity);
            }
            if(limit>0){
                mStringBuilder.append(PARAM_LIMIT).append(limit);
            }
            String url=mStringBuilder.toString();
            YPYLog.e("DCM","==========>getITunesSearchResultModel="+url);
            return getJsonModelFromServer(url,SearchResultModel.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static SearchResultModel lookUpModel(long id,String entity){
        try{
            StringBuilder mStringBuilder =new StringBuilder(FORMAT_URL_ITUNES_LOOKUP);
            if(id>0){
                mStringBuilder.append(PARAM_ID).append(id);
            }
            if(!TextUtils.isEmpty(entity)){
                mStringBuilder.append(PARAM_ENTITY).append(entity);
            }
            String url=mStringBuilder.toString();
            YPYLog.e("DCM","==========>lookUpModel="+url);
            return getJsonModelFromServer(url,SearchResultModel.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static RssFeedModel getRssFeedModel(String urlFeed){
        return getXMLModelFromServer(urlFeed,RssFeedModel.class);
    }

    private static <T> T getJsonModelFromServer(String url, Class<T> classOfT){
        try {
            Reader mInputStream = DownloadUtils.downloadReader(url);
            return ITunesParsingUtils.getModel(mInputStream,classOfT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T getXMLModelFromServer(String url, Class<T> classOfT){
        try {
            Reader mInputStream = DownloadUtils.downloadReader(url);
            return ITunesParsingUtils.getXMLModel(mInputStream,classOfT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
