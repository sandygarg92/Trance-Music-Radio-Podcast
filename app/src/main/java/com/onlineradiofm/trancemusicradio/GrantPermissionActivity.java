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

package com.onlineradiofm.trancemusicradio;

import static com.onlineradiofm.trancemusicradio.ShowUrlActivity.KEY_HEADER;
import static com.onlineradiofm.trancemusicradio.ShowUrlActivity.KEY_SHOW_URL;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.dataMng.TotalDataManager;
import com.onlineradiofm.trancemusicradio.databinding.ActivityGrantPermissionBinding;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.activity.YPYSplashActivity;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;

import java.io.File;


public class GrantPermissionActivity extends YPYSplashActivity<ActivityGrantPermissionBinding> implements IRadioConstants, View.OnClickListener {

    private TotalDataManager mTotalMng;

    @Override
    protected ActivityGrantPermissionBinding getViewBinding() {
        return ActivityGrantPermissionBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isNeedCheckGoogleService = false;
        super.onCreate(savedInstanceState);
        setUpOverlayBackground(true);
        mTotalMng = TotalDataManager.getInstance(getApplicationContext());

        String data = getString(R.string.format_request_permission);
        viewBinding.tvInfo.setText(Html.fromHtml(data));
        this.viewBinding.tvInfo.setText(Html.fromHtml(data));

        this.viewBinding.tvPolicy.setOnClickListener(this);
        this.viewBinding.tvTos.setOnClickListener(this);
        this.viewBinding.btnAllow.setOnClickListener(this);
        this.viewBinding.btnSkip.setOnClickListener(this);

    }

    @Override
    public void onInitData() {
        startCheckData();
    }

    @Override
    public File getDirectoryCached() {
        return mTotalMng.getDirectoryCached(getApplicationContext());
    }

    @Override
    public String[] getListPermissionNeedGrant() {
        return LIST_STORAGE_PERMISSIONS;
    }

    private void startCheckData() {
        showProgressDialog();
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            mTotalMng.readAllCache();
            runOnUiThread(this::goToMainActivity);
        });
    }


    public void goToMainActivity() {
        dismissProgressDialog();
        Intent mIntent = new Intent(this, MainActivity.class);
        startActivity(mIntent);
        finish();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_policy) {
            goToUrl(getString(R.string.title_privacy_policy), URL_PRIVACY_GOOGLE);
        } else if (id == R.id.tv_tos) {
            goToUrl(getString(R.string.title_term_of_use), URL_TERM_OF_USE);
        } else if (id == R.id.btn_allow) {
            startGrantPermission();
        } else if (id == R.id.btn_skip) {
            XRadioSettingManager.setSkipNow(this, true);
            startCheckData();
        }
    }

    @Override
    public void onPermissionDenied() {
    }

    @Override
    public void onPermissionGranted() {
        XRadioSettingManager.setSkipNow(this, false);
        super.onPermissionGranted();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToHome();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void goToUrl(String name, String url) {
        Intent mIntent = new Intent(this, ShowUrlActivity.class);
        mIntent.putExtra(KEY_HEADER, name);
        mIntent.putExtra(KEY_SHOW_URL, url);
        startActivity(mIntent);
    }

    @Override
    public void onUpdateUIWhenSupportRTL() {
        super.onUpdateUIWhenSupportRTL();
        viewBinding.tvInfo.setGravity(Gravity.END);
    }
}
