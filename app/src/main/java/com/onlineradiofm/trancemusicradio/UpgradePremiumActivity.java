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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.onlineradiofm.trancemusicradio.adapter.PremiumAdapter;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.dataMng.MemberShipManager;
import com.onlineradiofm.trancemusicradio.databinding.ActivityUpgradePremiumBinding;
import com.onlineradiofm.trancemusicradio.model.PremiumModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.executor.YPYExecutorSupplier;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

public class UpgradePremiumActivity extends RadioFragmentActivity<ActivityUpgradePremiumBinding> implements PurchasesUpdatedListener, View.OnClickListener {

    private PremiumAdapter mPremiumAdapter;

    private ArrayList<PremiumModel> mListPremiumModels;
    private PremiumModel mPremiumModel;
    private boolean isDestroy;

    private MemberShipManager memberShipManager;

    @Override
    protected ActivityUpgradePremiumBinding getViewBinding() {
        return ActivityUpgradePremiumBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onDoWhenDone() {
        super.onDoWhenDone();
        setActionBarTitle(R.string.title_pro_version);
        setUpRecyclerViewAsListView(viewBinding.recyclerViewIap);

        int mSmallMargin = getResources().getDimensionPixelOffset(R.dimen.small_margin);
        viewBinding.recyclerViewIap.setPadding(0, mSmallMargin, 0, mSmallMargin);
        viewBinding.tvPolicy.setOnClickListener(this);
        viewBinding.tvTos.setOnClickListener(this);
        viewBinding.btnManageSub.setOnClickListener(this);

        memberShipManager = new MemberShipManager(this, this);
        memberShipManager.setMemberManagerListener((isSuccess, error) -> {
            YPYLog.e(IRadioConstants.TAG, "==========>onFinishingCheckIAP error=" + error + "==>isSuccess=" + isSuccess);
            if (!isSuccess) {
                if (error == MemberShipManager.ERROR_SUB_NOT_SUPPORTED) {
                    XRadioSettingManager.setIdMember(this, 0);
                    goToMainActivity(R.string.info_billing_invalid);
                    return;
                }
            }
            YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(this::startLoadPremium);
        });
        memberShipManager.checkIAP();

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

        viewBinding.btnManageSub.setTextColor(getResources().getColor(isDark ? R.color.dark_color_accent : R.color.light_color_accent));

        if (isDark) {
            viewBinding.layoutBgPremium.setBackgroundColor(Color.TRANSPARENT);
            viewBinding.progressBarPre.setProgressColor(getResources().getColor(R.color.dark_color_accent));
        }
        else {
            float elevation = getResources().getDimensionPixelOffset(R.dimen.card_elevation);
            ViewCompat.setElevation(this.viewBinding.myToolbar.getRoot(), elevation);
        }

        int secondColor = ContextCompat.getColor(this, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);
        viewBinding.tvPolicy.setTextColor(secondColor);
        viewBinding.tvTos.setTextColor(secondColor);
        viewBinding.divider.setBackgroundColor(secondColor);

        int bgPagerColor = ContextCompat.getColor(this, isDark ? R.color.dark_pager_color_background : R.color.light_pager_color_background);
        viewBinding.layoutBgPremium.setBackgroundColor(bgPagerColor);
    }

    @Override
    public void setActionBarTitle(String title) {
        super.setActionBarTitle("");
        viewBinding.myToolbar.toolBarTitle.setText(title);
    }

    @SuppressLint("StringFormatInvalid")
    private void startLoadPremium() {
        try {
            if (memberShipManager == null) return;
            String[] mListMember = getResources().getStringArray(R.array.array_members);
            String[] mListItemIds = getResources().getStringArray(R.array.array_product_ids);
            String[] mListPrices = getResources().getStringArray(R.array.array_prices);
            String[] mListTimes = getResources().getStringArray(R.array.array_date_times);

            int size = mListMember.length;
            int memberId = XRadioSettingManager.getIdMember(this);
            int purchaseMemberId = 0;
            mListPremiumModels = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                ProductDetails mSkuDetails = memberShipManager.getProductDetails(mListItemIds[i]);
                PremiumModel mPremiumModel = new PremiumModel(TYPE_MEMBERS[i], mListMember[i], mListItemIds[i], IMG_MEMBERS[i]);
                mPremiumModel.setInfo1(String.format(getString(R.string.format_buy_pro1), mListTimes[i]));

                String price = mListPrices[i];
                if (mSkuDetails != null) {
                    ProductDetails.PricingPhase pricePhase = memberShipManager.getSubscriptionPricePhase(mSkuDetails);
                    if (pricePhase != null) {
                        price = pricePhase.getFormattedPrice();
                    }
                    YPYLog.e(IRadioConstants.TAG, "========>price=" + price + "==>itemId=" + mListItemIds[i]);

                    Purchase mPurchase = memberShipManager.getPurchase(mListItemIds[i]);
                    boolean isPurchased = memberShipManager.isSubscriptionSuccess(mPurchase);

                    if (isPurchased) {
                        purchaseMemberId = TYPE_MEMBERS[i];
                        mPremiumModel.setLabelBtnBuy("");
                        mPremiumModel.setStatusBtn(PremiumModel.STATUS_BTN_PURCHASED);
                    }
                }
                mPremiumModel.setPrice(price);
                mPremiumModel.setDuration(mListTimes[i]);
                updateInfoMemberId(mPremiumModel, memberId);
                mListPremiumModels.add(mPremiumModel);
            }
            XRadioSettingManager.setIdMember(this, purchaseMemberId);
            runOnUiThread(() -> {
                try {
                    if (isDestroy) return;
                    viewBinding.progressBarPre.setVisibility(View.GONE);
                    mPremiumAdapter = new PremiumAdapter(this, mListPremiumModels);
                    mPremiumAdapter.setListener(this::processPurchaseItem);
                    viewBinding.recyclerViewIap.setAdapter(mPremiumAdapter);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
        catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> goToMainActivity(R.string.title_purchase_error));
        }

    }

    private void updateInfoMemberId(PremiumModel mPremiumModel, int memberId) {
        int currentMember = (int) mPremiumModel.getId();
        if (currentMember < memberId) {
            mPremiumModel.setLabelBtnBuy(getString(R.string.title_skip));
            mPremiumModel.setStatusBtn(PremiumModel.STATUS_BTN_SKIP);
        }
        else if (currentMember > memberId) {
            mPremiumModel.setLabelBtnBuy(getString(R.string.title_buy_now));
            mPremiumModel.setStatusBtn(PremiumModel.STATUS_BTN_BUY);
        }
        else {
            mPremiumModel.setLabelBtnBuy("");
            mPremiumModel.setStatusBtn(PremiumModel.STATUS_BTN_PURCHASED);
        }
    }

    private void processPurchaseItem(PremiumModel premiumModel) {
        try {
            int status = premiumModel.getStatusBtn();
            if (status == PremiumModel.STATUS_BTN_SKIP || status == PremiumModel.STATUS_BTN_PURCHASED) {
                return;
            }
            if (!ApplicationUtils.isOnline(this)) {
                showDialogTurnOnInternetConnection();
                return;
            }
            if (memberShipManager != null) {
                ProductDetails skuDetails = memberShipManager.getProductDetails(premiumModel.getProductId());
                if (skuDetails == null) {
                    goToMainActivity(R.string.item_purchase_invalid);
                    return;
                }
                this.mPremiumModel = premiumModel;
                BillingResult mBillingResult = memberShipManager.launchBillingFlow(skuDetails);
                if (mBillingResult == null || mBillingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    goToMainActivity(R.string.item_purchase_invalid);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDoWhenResume() {
        super.onDoWhenResume();
        if (memberShipManager != null && mPremiumModel != null) {
            memberShipManager.onStartCheckSKUDetails();
        }

    }

    private void notifyData() {
        runOnUiThread(() -> {
            if (mPremiumAdapter != null) {
                mPremiumAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean backToHome() {
        if (viewBinding.progressBarPre.getVisibility() == View.VISIBLE) {
            return true;
        }
 String strFrom=getIntent().getStringExtra("from" );
 Log.e("TAG strFrom","strFrom="+strFrom);

        if(strFrom!=null && strFrom.equalsIgnoreCase("download"))
        {
        finish();
        }else{
            goToMainActivity(0);

        }
        return true;
    }

    private void goToMainActivity(int msgId) {
        try {
            if (msgId != 0) {
                showToast(msgId);
            }
            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            isDestroy = true;
            viewBinding.recyclerViewIap.setAdapter(null);
            if (mListPremiumModels != null) {
                mListPremiumModels.clear();
                mListPremiumModels = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
        if (memberShipManager != null) {
            memberShipManager.onDestroy();
        }
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        try {
            YPYLog.e("DCM", "====>onPurchasesUpdated billingResult=" + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                showToast(R.string.title_purchase_success);
                for (Purchase purchase : purchases) {
                    memberShipManager.handlePurchase(purchase, billingResult1 -> {
                        YPYLog.e("DCM", "====>acknowledge_purchase billingResult=" + billingResult1.getResponseCode());
                        if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            showToast(R.string.info_thanks_purchasing);
                            saveInfoPurchase(purchase);
                        }
                        else {
                            showToast(R.string.info_acknowledge_purchase_error);
                        }
                    });
                }
            }
            else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                goToMainActivity(R.string.info_purchase_cancelled);
            }
            else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_TIMEOUT) {
                goToMainActivity(R.string.info_purchase_timeout_error);
            }
            else {
                goToMainActivity(R.string.item_purchase_invalid);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showDialogTurnOnInternetConnection() {
        createFullDialog(-1, R.string.title_warning, R.string.title_settings, R.string.title_cancel,
                getString(R.string.info_lose_internet), () -> {
                    try {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }, null).show();
    }

    private void saveInfoPurchase(@NonNull Purchase mPurchase) {
        try {
            YPYLog.e("DCM", "====>saveInfoPurchaseToServer");
            try {
                if (mPremiumModel != null) {
                    XRadioSettingManager.setIdMember(this, (int) mPremiumModel.getId());
                    for (PremiumModel model : mListPremiumModels) {
                        updateInfoMemberId(model, (int) mPremiumModel.getId());
                    }
                    notifyData();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_policy) {
            goToUrl(getString(R.string.title_privacy_policy), URL_PRIVACY_POLICY);
        }
        else if (id == R.id.tv_tos) {
            goToUrl(getString(R.string.title_term_of_use), URL_TERM_OF_USE);
        }
        else if (id == R.id.btn_manage_sub) {
            MemberShipManager.goToManageSub(this);
        }
    }
}
