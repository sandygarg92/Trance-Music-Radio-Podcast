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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */
public class ApplicationUtils {

    public static boolean isSupportRTL() {
        try {
            return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static boolean isOnline(Context mContext) {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (IOUtils.isAndroid10()) {
                    NetworkCapabilities mCapabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                    if (mCapabilities != null) {
                        return mCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                    }
                }
                else {
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isGrantAllPermission(Context mContext, String[] permission) {
        if (permission != null && permission.length > 0) {
            for (String mStr : permission) {
                if (ContextCompat.checkSelfPermission(mContext, mStr) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isGrantAllPermission(int[] grantResults) {
        if (grantResults != null && grantResults.length > 0) {
            for (int grantResult : grantResults) {
                YPYLog.e("DCM", "======>grantResult=" + grantResult);
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean hasSDcard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getVersionName(Context mContext) {
        PackageInfo pinfo;
        try {
            pinfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return pinfo.versionName;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void hiddenVirtualKeyboard(Context mContext, View myEditText) {
        try {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GradientDrawable.Orientation getOrientation(int gradOrientation) {
        GradientDrawable.Orientation orientation;
        if (gradOrientation == 0) {
            orientation = GradientDrawable.Orientation.LEFT_RIGHT;
        }
        else if (gradOrientation == 180) {
            orientation = GradientDrawable.Orientation.RIGHT_LEFT;
        }
        else if (gradOrientation == 270) {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        }
        else if (gradOrientation == 90) {
            orientation = GradientDrawable.Orientation.BOTTOM_TOP;
        }
        else if (gradOrientation == 315) {
            orientation = GradientDrawable.Orientation.TL_BR;
        }
        else if (gradOrientation == 225) {
            orientation = GradientDrawable.Orientation.TR_BL;
        }
        else if (gradOrientation == 45) {
            orientation = GradientDrawable.Orientation.BL_TR;
        }
        else if (gradOrientation == 135) {
            orientation = GradientDrawable.Orientation.BR_TL;
        }
        else {
            orientation = GradientDrawable.Orientation.TL_BR;
        }
        return orientation;
    }

    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            StringBuilder md5 = new StringBuilder(number.toString(16));
            while (md5.length() < 32) {
                md5.insert(0, "0");

            }
            return md5.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
