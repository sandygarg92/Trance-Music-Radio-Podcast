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

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onlineradiofm.trancemusicradio.databinding.ActivityShowUrlBinding;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/19/17.
 */
public class ShowUrlActivity extends RadioFragmentActivity<ActivityShowUrlBinding> {

    public static final String KEY_HEADER = "KEY_HEADER";
    public static final String KEY_SHOW_URL = "KEY_SHOW_URL";

    private String mUrl;
    private String mNameHeader;

    @Override
    protected void onDoBeforeSetView() {
        super.onDoBeforeSetView();
    }


    @Override
    protected ActivityShowUrlBinding getViewBinding() {
        return ActivityShowUrlBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onDoWhenDone() {
        Intent args = getIntent();
        if (args != null) {
            mUrl = args.getStringExtra(KEY_SHOW_URL);
            mNameHeader = args.getStringExtra(KEY_HEADER);
        }
        if (TextUtils.isEmpty(mUrl)) {
            backToHome();
            return;
        }
        super.onDoWhenDone();
        if (!TextUtils.isEmpty(mNameHeader)) {
            setActionBarTitle(mNameHeader);
        }
        setUpWebView();
    }

    @Override
    public void updateThemeColor(boolean isDark) {
        super.updateThemeColor(isDark);
        int actionBarColor = ContextCompat.getColor(this, !isDark ? R.color.light_action_bar_background : R.color.dark_action_bar_background);
        int actionBarTextColor = ContextCompat.getColor(this, !isDark ? R.color.light_action_bar_text_color : R.color.dark_action_bar_text_color);
        setUpCustomizeActionBar(actionBarColor, actionBarTextColor, true);
        viewBinding.myToolbar.toolBarTitle.setTextColor(actionBarTextColor);

        int bgColor = ContextCompat.getColor(this, isDark ? R.color.dark_color_background : R.color.light_color_background);
        viewBinding.layoutBg.setBackgroundColor(bgColor);
        viewBinding.webview.setBackgroundColor(ContextCompat.getColor(this, isDark ? R.color.dark_pager_color_background
                : R.color.light_pager_color_background));

        if(!isDark){
            float elevation = getResources().getDimensionPixelOffset(R.dimen.card_elevation);
            ViewCompat.setElevation( this.viewBinding.myToolbar.getRoot(), elevation);
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        super.setActionBarTitle("");
        viewBinding.myToolbar.toolBarTitle.setText(title);
    }


    private void setUpWebView() {
        viewBinding.webview.getSettings().setJavaScriptEnabled(true);
        viewBinding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                viewBinding.progressBar1.setVisibility(View.GONE);
            }
        });

        if (ApplicationUtils.isOnline(this)) {
            if (!mUrl.startsWith("http")) {
                mUrl = "http://" + mUrl;
            }
            viewBinding.webview.loadUrl(mUrl);
        }
    }

    @Override
    public void onDoWhenNetworkOn() {
        super.onDoWhenNetworkOn();
        viewBinding.webview.loadUrl(mUrl);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewBinding.webview.destroy();
    }

    @Override
    public boolean backToHome() {
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewBinding.webview.canGoBack()) {
                viewBinding.webview.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
