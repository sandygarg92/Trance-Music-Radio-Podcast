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

package com.onlineradiofm.trancemusicradio.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.model.UserModel;

import androidx.annotation.NonNull;


public class XRadioSettingManager implements IXRadioSettingConstants {


    private static final String NAME_SHARPREFS = "app_prefs";

    private static void saveSetting(Context mContext, String mKey, String mValue) {
        try {
            if (mContext != null) {
                SharedPreferences mSharedPreferences = mContext.getSharedPreferences(NAME_SHARPREFS, Context.MODE_PRIVATE);
                if (mSharedPreferences != null) {
                    Editor editor = mSharedPreferences.edit();
                    editor.putString(mKey, mValue);
                    editor.apply();
                }
            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getSetting(Context mContext, String mKey, String mDefValue) {
        try {
            if (mContext != null) {
                SharedPreferences mSharedPreferences = mContext.getSharedPreferences(NAME_SHARPREFS, Context.MODE_PRIVATE);
                if (mSharedPreferences != null) {
                    return mSharedPreferences.getString(mKey, mDefValue);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mDefValue;

    }

    public static void setLastPlayedMyRadio(Context mContext, boolean mValue) {
        saveSetting(mContext, KEY_LAST_MY_RADIO, String.valueOf(mValue));
    }

    public static boolean getLastPlayedMyRadio(Context mContext) {
        return Boolean.parseBoolean(getSetting(mContext, KEY_LAST_MY_RADIO, "false"));
    }

    public static void setLastPlayedRadioId(Context mContext, long mValue) {
        saveSetting(mContext, KEY_LAST_RADIO_ID, String.valueOf(mValue));
    }

    public static long getLastPlayedRadioId(Context mContext) {
        return Long.parseLong(getSetting(mContext, KEY_LAST_RADIO_ID, "0"));
    }

    public static void setThemId(Context mContext, long mValue) {
        saveSetting(mContext, KEY_THEMES_ID, String.valueOf(mValue));
    }

    public static long getThemId(Context mContext) {
        return Long.parseLong(getSetting(mContext, KEY_THEMES_ID, "0"));
    }

    public static boolean isDarkMode(Context mContext) {
        return getThemId(mContext) == IRadioConstants.DARK_MODE_THEME_ID;
    }

    public static int getSleepMode(Context mContext) {
        return Integer.parseInt(getSetting(mContext, KEY_TIME_SLEEP, "0"));
    }

    public static void setSleepMode(Context mContext, int mValue) {
        saveSetting(mContext, KEY_TIME_SLEEP, String.valueOf(mValue));
    }

    public static void setAgreeTerm(Context mContext, boolean mValue) {
        saveSetting(mContext, KEY_AGREE_TERM, String.valueOf(mValue));
    }

    public static boolean getAgreeTerm(Context mContext) {
        return Boolean.parseBoolean(getSetting(mContext, KEY_AGREE_TERM, "false"));
    }

    public static void setIdMember(Context mContext, int mValue) {
        saveSetting(mContext, KEY_MEMBER_ID, String.valueOf(mValue));
    }

    public static int getIdMember(Context mContext) {
        return Integer.parseInt(getSetting(mContext, KEY_MEMBER_ID, "0"));
    }

    public static void setSkipNow(Context mContext, boolean mValue) {
        saveSetting(mContext, KEY_SKIP_NOW, String.valueOf(mValue));
    }

    public static boolean getSkipNow(Context mContext) {
        return Boolean.parseBoolean(getSetting(mContext, KEY_SKIP_NOW, "false"));
    }

    public static long getUserId(Context mContext) {
        return Long.parseLong(getSetting(mContext, KEY_USER_ID, "0"));
    }

    public static String getUserToken(Context mContext) {
        return getSetting(mContext, KEY_USER_TOKEN, "");
    }

    public static String getUserEmail(Context mContext) {
        return getSetting(mContext, KEY_USER_EMAIL, "");
    }

    public static String getUserAvatar(Context mContext) {
        return getSetting(mContext, KEY_USER_AVATAR, "");
    }

    public static String getDisplayName(Context mContext, boolean isGetEmail) {
        String userName = getSetting(mContext, KEY_USER_NAME, "");
        if (TextUtils.isEmpty(userName)) {
            userName = isGetEmail ? getSetting(mContext, KEY_USER_EMAIL, "") : mContext.getString(R.string.title_unknown_name);
        }
        return userName;
    }

    public static void logOut(@NonNull Context mContext) {
        try {
            saveSetting(mContext, KEY_USER_ID, String.valueOf(0));
            saveSetting(mContext, KEY_USER_EMAIL, "");
            saveSetting(mContext, KEY_USER_TOKEN, "");
            saveSetting(mContext, KEY_USER_NAME, "");
            saveSetting(mContext, KEY_USER_AVATAR, "");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveUserModel(@NonNull Context mContext, @NonNull UserModel userModel) {
        try {
            saveSetting(mContext, KEY_USER_ID, String.valueOf(userModel.getId()));
            saveSetting(mContext, KEY_USER_EMAIL, userModel.getEmail());
            saveSetting(mContext, KEY_USER_TOKEN, userModel.getUserToken());

            if (!TextUtils.isEmpty(userModel.getName())) {
                saveSetting(mContext, KEY_USER_NAME, userModel.getName());
            }
            if (!TextUtils.isEmpty(userModel.getImage())) {
                saveSetting(mContext, KEY_USER_AVATAR, userModel.getImage());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSignedIn(@NonNull Context mContext) {
        try {
            String userToken = getUserToken(mContext);
            long userId = getUserId(mContext);
            return !TextUtils.isEmpty(userToken) && userId > 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static boolean getEqualizer(Context mContext) {
        return Boolean.parseBoolean(getSetting(mContext, KEY_EQUALIZER_ON, "false"));
    }

    public static void setEqualizer(Context mContext, boolean mValue) {
        saveSetting(mContext, KEY_EQUALIZER_ON, String.valueOf(mValue));
    }

    public static String getEqualizerPreset(Context mContext) {
        return getSetting(mContext, KEY_EQUALIZER_PRESET, "0");
    }

    public static void setEqualizerPreset(Context mContext, String mValue) {
        saveSetting(mContext, KEY_EQUALIZER_PRESET, mValue);
    }

    public static String getEqualizerParams(Context mContext) {
        return getSetting(mContext, KEY_EQUALIZER_PARAMS, "");
    }

    public static void setEqualizerParams(Context mContext, String mValue) {
        saveSetting(mContext, KEY_EQUALIZER_PARAMS, mValue);
    }

    public static short getBassBoost(Context mContext) {
        return Short.parseShort(getSetting(mContext, KEY_BASSBOOST, "0"));
    }

    public static void setBassBoost(Context mContext, short mValue) {
        saveSetting(mContext, KEY_BASSBOOST, String.valueOf(mValue));
    }

    public static short getVirtualizer(Context mContext) {
        return Short.parseShort(getSetting(mContext, KEY_VIRTUALIZER, "0"));
    }

    public static void setVirtualizer(Context mContext, short mValue) {
        saveSetting(mContext, KEY_VIRTUALIZER, String.valueOf(mValue));
    }


    public static void setDontAskAgainDownload(Context mContext, boolean mValue) {
        saveSetting(mContext, KEY_DONT_ASK_AGAIN_DOWNLOAD, String.valueOf(mValue));
    }

    public static boolean getDontAskAgainDownload(Context mContext) {
        return Boolean.parseBoolean(getSetting(mContext, KEY_DONT_ASK_AGAIN_DOWNLOAD, "false"));
    }

}
