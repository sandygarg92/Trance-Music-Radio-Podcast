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

package com.onlineradiofm.trancemusicradio.ypylibs.activity;

import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.LIST_STORAGE_PERMISSIONS_13;
import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.LIST_STORAGE_PERMISSIONS_14;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.IOUtils;

import java.io.File;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/19/17.
 */

@SuppressLint("CustomSplashScreen")
public abstract class YPYSplashActivity<T extends ViewBinding> extends YPYFragmentActivity {

    public static final String TAG = YPYSplashActivity.class.getSimpleName();
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1000;
    public static final int REQUEST_PERMISSION_CODE = 1001;

    private boolean isCheckGoogle;
    public boolean isNeedCheckGoogleService = true;
    protected T viewBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init view binding
        viewBinding = getViewBinding();
        View view = viewBinding.getRoot();
        setContentView(view);

        processRightToLeft();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isCheckGoogle && isNeedCheckGoogleService) {
            isCheckGoogle = true;
            checkGooglePlayService();
        }
    }

    private void checkGooglePlayService() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        try {
            int result = googleAPI.isGooglePlayServicesAvailable(this);
            if (result == ConnectionResult.SUCCESS) {
                startCheck();
            } else {
                if (googleAPI.isUserResolvableError(result)) {
                    isCheckGoogle = false;
                    googleAPI.showErrorDialogFragment(this, result, REQUEST_GOOGLE_PLAY_SERVICES);
                } else {
                    showToast(googleAPI.getErrorString(result));
                    backToHome();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startCheck() {
        File mFile = getDirectoryCached();
        if (mFile == null) {
            createFullDialog(-1, R.string.title_info, R.string.title_settings, R.string.title_cancel,
                    getString(R.string.info_error_sdcard), () -> {
                        isCheckGoogle = false;
                        startActivityForResult(new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS), 0);

                    }, this::backToHome).show();
            return;
        }
        onInitData();

    }

    public void startGrantPermission() {
        if (IOUtils.isAndroid14() && !ApplicationUtils.isGrantAllPermission(this, LIST_STORAGE_PERMISSIONS_14)) {
            ActivityCompat.requestPermissions(this, LIST_STORAGE_PERMISSIONS_14, REQUEST_PERMISSION_CODE);
        } else if (IOUtils.isAndroid13() && !ApplicationUtils.isGrantAllPermission(this, LIST_STORAGE_PERMISSIONS_13)) {
            ActivityCompat.requestPermissions(this, LIST_STORAGE_PERMISSIONS_13, REQUEST_PERMISSION_CODE);
        } else if (IOUtils.isMarshmallow() &&
                !ApplicationUtils.isGrantAllPermission(this, getListPermissionNeedGrant())) {
            ActivityCompat.requestPermissions(this, getListPermissionNeedGrant(), REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (ApplicationUtils.isGrantAllPermission(grantResults)) {
                    onPermissionGranted();
                } else {
                    onPermissionDenied();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onPermissionDenied();
        }

    }

    public void onPermissionGranted() {
        startCheck();
    }

    public void onPermissionDenied() {
        showToast(R.string.info_permission_denied);
        backToHome();
    }


    public abstract void onInitData();

    public abstract File getDirectoryCached();

    public abstract String[] getListPermissionNeedGrant();

    protected abstract T getViewBinding();


    @Override
    public boolean backToHome() {
        onDestroyData();
        finish();
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
