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

package com.onlineradiofm.trancemusicradio.ypylibs.cache;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/19/17.
 */

public class YPYSaveModel {
    private int id;
    private Type saveType;
    private String fileName;
    private ArrayList<? extends Object> listSavedData;
    private int maximumObject;

    public YPYSaveModel(int id, Type saveType, String fileName) {
        this.id = id;
        this.saveType = saveType;
        this.fileName=fileName;
    }
    public YPYSaveModel(int id) {
        this(id,null,null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getSaveType() {
        return saveType;
    }

    public void setSaveType(Type saveType) {
        this.saveType = saveType;
    }

    public ArrayList<? extends Object> getListSavedData() {
        return listSavedData;
    }

    public void setListSavedData(ArrayList<? extends Object> listSavedData) {
        this.listSavedData = listSavedData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void onDestroy(){
        try{
            if(listSavedData!=null){
                listSavedData.clear();
                listSavedData=null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public int getMaximumObject() {
        return maximumObject;
    }

    public void setMaximumObject(int maximumObject) {
        this.maximumObject = maximumObject;
    }
}
