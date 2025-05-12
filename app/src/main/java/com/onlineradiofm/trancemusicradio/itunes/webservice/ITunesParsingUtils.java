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

package com.onlineradiofm.trancemusicradio.itunes.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.Reader;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */


class ITunesParsingUtils {


    static <T> T getModel(Reader in, Class<T> classOfT){
        try {
            if (in != null) {
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(in,classOfT);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static <T> T getXMLModel(Reader in, Class<T> classOfT){
        Persister persister = new Persister();
        try {
            return persister.read(classOfT, in);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
