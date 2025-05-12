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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/25/17.
 */

public class ResultModel<T>{

    public static final int STATUS_OK=200;
    public static final int STATUS_DOWNLOADING=201;
    public static final int STATUS_ERROR=203;

    @SerializedName("status")
    private int status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("datas")
    private ArrayList<T> listModels;

    private transient int percentage;
    public ResultModel(int status) {
        this.status = status;
    }

    public ResultModel(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResultModel() {
    }

    public String getMsg() {
        return msg;
    }

    public boolean isResultOk(){
        return status==200;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<T> getListModels() {
        return listModels;
    }

    public void setListModels(ArrayList<T> listModels) {
        this.listModels = listModels;
    }

    @Nullable
    public T firstModel(){
        return listModels!=null && listModels.size()>0? listModels.get(0):null;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public boolean isDownloadingFile(){
        return status==STATUS_DOWNLOADING;
    }
}
