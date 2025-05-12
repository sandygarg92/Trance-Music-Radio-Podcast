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

package com.onlineradiofm.trancemusicradio.ypylibs.utils;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */
public class StringUtils {

    public static String urlEncodeString(String data) {
        if (data != null && !data.equals("")) {
            try {
                return URLEncoder.encode(data, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static String formatNumberSocial(long number) {
        String strComment = String.valueOf(number);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(3);
        DecimalFormat df = (DecimalFormat) nf;
        if (number >= 1000 && number < 1000000) {
            strComment = df.format(number);
        }
        else if (number >= 1000000 && number < 1000000000) {
            //strComment= number / 1000 +"M";
            strComment = df.format(number);
        }
        else if (number >= 1000000000) {
            strComment = number / 1000 + "B";
        }
        return strComment.replaceAll(",+", ".");
    }

    public static String urlDecodeString(String data) {
        if (data != null && !data.equals("")) {
            try {
                return URLDecoder.decode(data, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static String getFormatSocial(Context mContext, long number, int resMin, int resMax) {
        try {
            if (number > 1) {
                return String.format(mContext.getString(resMax), number);
            }
            else {
                return String.format(mContext.getString(resMin), number);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(number);

    }

    public static String getStringTimer(long millis) {
        try {
            long second = (millis / 1000) % 60;
            long minute = (millis / (1000 * 60)) % 60;
            long hour = (millis / (1000 * 60 * 60)) % 24;
            String time;
            if (hour > 0) {
                time = String.format("%02d:%02d:%02d", hour, minute, second);
                return time;
            }
            else {
                time = String.format("%02d:%02d", minute, second);
            }
            return time;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String getShortTitle(String title, int maxLen) {
        try {
            int count = title != null ? title.length() : 0;
            String strTitle = title;
            if (count >= maxLen) {
                strTitle = title.substring(0, maxLen) + "...";
            }
            return strTitle;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return title;

    }

    public static boolean isNumber(String data) {
        return data.matches("[+-]?\\d*(\\.\\d+)?");
    }

}
