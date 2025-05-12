package com.onlineradiofm.trancemusicradio.ypylibs.remoteconfig;

import android.app.Activity;

import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


public class FirebaseRemote {

    private static final long DEFAULT_TIME_OUT_IN_SECONDS = 10;

    private final long cacheExpiration;
    private final long timeOut;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private final Activity mActivity;

    public FirebaseRemote(Activity mActivity, long cacheExpiration, int resConfigDefault) {
        this(mActivity, cacheExpiration, DEFAULT_TIME_OUT_IN_SECONDS, resConfigDefault);
    }

    private FirebaseRemote(Activity mActivity, long cacheExpiration, long timeOut, int resConfigDefault) {
        this.mActivity = mActivity;
        this.timeOut = timeOut;
        this.cacheExpiration = cacheExpiration;
        setUpFireBaseConfig(resConfigDefault);
    }

    private void setUpFireBaseConfig(int resConfigDefault) {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaultsAsync(resConfigDefault);
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(timeOut)
                .setMinimumFetchIntervalInSeconds(cacheExpiration).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
    }

    public void fetchDataFromFireBase(final IYPYCallback mCallback, final IYPYCallback mErrorCallback) {
        try {
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(mActivity, task -> {
                YPYLog.e(IRadioConstants.TAG, "===>fetchAndActivate=" + task.isSuccessful());
                if (task.isSuccessful()) {
                    if (mCallback != null) {
                        mCallback.onAction();
                    }
                }
                else {
                    if (mErrorCallback != null) {
                        mErrorCallback.onAction();
                    }
                }

            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getFirebaseBooleanConfig(String key) {
        try {
            if (mFirebaseRemoteConfig != null) {
                return mFirebaseRemoteConfig.getBoolean(key);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getFirebaseLongConfig(String key) {
        try {
            if (mFirebaseRemoteConfig != null) {
                return mFirebaseRemoteConfig.getLong(key);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    public String getFirebaseStringConfig(String key) {
        try {
            if (mFirebaseRemoteConfig != null) {
                return mFirebaseRemoteConfig.getString(key);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
