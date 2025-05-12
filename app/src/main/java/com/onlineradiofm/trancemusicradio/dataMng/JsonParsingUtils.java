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

import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
public class JsonParsingUtils implements IRadioConstants {

    static <T> ResultModel<T> getResultModel(Reader in, Type mDatas){
        if (in == null) {
            return null;
        }
        try {
            Gson mGson = new GsonBuilder().create();
            return mGson.fromJson(in,mDatas);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    static String parsingImageSongFromItunes(String data) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject mJsonObject = new JSONObject(data);
                if (mJsonObject.opt("results") != null) {
                    JSONArray mJsonArray = mJsonObject.getJSONArray("results");
                    if (mJsonArray.length() > 0) {
                        int size = mJsonArray.length();
                        for (int i = 0; i < size; i++) {
                            JSONObject mJsArray = mJsonArray.getJSONObject(i);
                            if (mJsArray.opt("artworkUrl100") != null) {
                                String img=mJsArray.getString("artworkUrl100");
                                if(!TextUtils.isEmpty(img)){
                                    img=img.replace("100x100","600x600");
                                    return img;
                                }
                            }
                            else if (mJsArray.opt("artworkUrl60") != null) {
                                return mJsArray.getString("artworkUrl60");
                            }
                            else if (mJsArray.opt("artworkUrl30") != null) {
                                return mJsArray.getString("artworkUrl30");
                            }
                        }
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



}
