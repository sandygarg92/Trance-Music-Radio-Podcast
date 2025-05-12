package com.onlineradiofm.trancemusicradio.dataMng;

import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.TYPE_MEMBERS;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ShareActionUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;

import java.util.ArrayList;
import java.util.List;


public class MemberShipManager {

    public static final String FORMAT_CANCEL_SUB = "https://play.google.com/store/account/subscriptions?package=%s";

    public static final int ERROR_SUB_NOT_SUPPORTED = -1;
    public static final int ERROR_UNKNOWN_CODE = -2;
    public static final int ERROR_BILLING_SET_UP = -3;
    public static final int ERROR_BILLING_DISCONNECT = -4;
    public static final int ERROR_BILLING_QRY_SKU = -5;

    public static final int SUCCESS_QRY = 1;

    private final PurchasesUpdatedListener purchasesUpdatedListener;
    private BillingClient billingClient;
    private List<ProductDetails> productDetailsList;
    private List<Purchase> listPurchases;

    private final Context mContext;
    private IMemberManagerListener memberManagerListener;


    public MemberShipManager(@NonNull Context mContext, @Nullable PurchasesUpdatedListener purchasesUpdatedListener) {
        this.mContext = mContext;
        this.purchasesUpdatedListener = purchasesUpdatedListener;
    }

    public boolean checkIAP() {
        try {
            if (ApplicationUtils.isOnline(mContext)) {
                initBilling();
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    private void initBilling() {
        try {
            if (billingClient == null) {
                BillingClient.Builder mBuilder = BillingClient.newBuilder(mContext).enablePendingPurchases();
                if (purchasesUpdatedListener != null) {
                    mBuilder.setListener(purchasesUpdatedListener);
                }
                billingClient = mBuilder.build();
                billingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                        YPYLog.e(IRadioConstants.TAG, "=======>onBillingSetupFinished result ok = " + (billingResult.getResponseCode() == 0));
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            MemberShipManager.this.onStartCheckSKUDetails();
                        }
                        else {
                            onFinishingCheckIAP(false, ERROR_BILLING_SET_UP);
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {
                        onFinishingCheckIAP(false, ERROR_BILLING_DISCONNECT);
                    }
                });
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        onFinishingCheckIAP(false, ERROR_UNKNOWN_CODE);

    }

    public void onStartCheckSKUDetails() {
        try {
            if (billingClient != null) {
                BillingResult billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
                YPYLog.e(IRadioConstants.TAG, "=======>onStartCheckSKUDetails subscription supported = " + (billingResult.getResponseCode() == 0));
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    XRadioSettingManager.setIdMember(mContext, 0);
                    onFinishingCheckIAP(false, ERROR_SUB_NOT_SUPPORTED);
                    return;
                }
                Log.e("DCM", "=======>start check SKU");
                querySKUDetails();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void querySKUDetails() {
        billingClient.queryProductDetailsAsync(buildSkuPrams(), (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                MemberShipManager.this.productDetailsList = list;
                QueryPurchasesParams params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build();
                billingClient.queryPurchasesAsync(params, (billingResult1, list1) -> onLoadPurchaseSuccess(list1));
            }
            else {
                XRadioSettingManager.setIdMember(mContext, 0);
                onFinishingCheckIAP(false, ERROR_BILLING_QRY_SKU);
            }

        });
    }

    private void onLoadPurchaseSuccess(@Nullable List<Purchase> listPurchases) {
        try {
            YPYLog.e(IRadioConstants.TAG, "=======>onLoadPurchaseSuccess=" + (listPurchases != null ? listPurchases.size() : 0));
            boolean isAllowReset = true;
            this.listPurchases = listPurchases;
            if (productDetailsList != null && productDetailsList.size() > 0) {
                for (ProductDetails product : productDetailsList) {
                    Purchase purchase = getPurchase(product.getProductId());
                    if (isSubscriptionSuccess(purchase)) {
                        XRadioSettingManager.setIdMember(mContext, getMemberId(mContext, product.getProductId()));
                        isAllowReset = false;
                    }
                }
            }
            YPYLog.e(IRadioConstants.TAG, "=======>isAllowReset=" + isAllowReset);
            if (isAllowReset) {
                XRadioSettingManager.setIdMember(mContext, 0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        onFinishingCheckIAP(true, SUCCESS_QRY);

    }


    public boolean isSubscriptionSuccess(Purchase mPurchase) {
        return mPurchase != null && mPurchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED;
    }

    public Purchase getPurchase(@NonNull String productId) {
        try {
            if (listPurchases != null && listPurchases.size() > 0) {
                for (Purchase mPurchase : listPurchases) {
                    List<String> listProductId = mPurchase.getProducts();
                    if (!listProductId.isEmpty() && listProductId.contains(productId)) {
                        return mPurchase;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BillingResult launchBillingFlow(@NonNull ProductDetails skuDetails) {
        try {
            if (billingClient != null) {
                List<ProductDetails.SubscriptionOfferDetails> offerDetails = skuDetails.getSubscriptionOfferDetails();
                if (offerDetails != null && offerDetails.size() > 0) {
                    String offerToken = offerDetails.get(0).getOfferToken();
                    ArrayList<BillingFlowParams.ProductDetailsParams> listParams = new ArrayList<>();
                    listParams.add(BillingFlowParams.ProductDetailsParams
                            .newBuilder().setProductDetails(skuDetails).setOfferToken(offerToken).build());
                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(listParams).build();
                    return billingClient.launchBillingFlow((Activity) mContext, flowParams);
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void handlePurchase(@NonNull Purchase purchase, AcknowledgePurchaseResponseListener listener) {
        try {
            if (billingClient != null) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    // Grant entitlement to the user.
                    // Acknowledge the purchase if it hasn't already been acknowledged.
                    if (!purchase.isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, listener);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    public ProductDetails getProductDetails(@NonNull String productId) {
        try {
            if (productDetailsList != null && productDetailsList.size() > 0) {
                for (ProductDetails mSkuDetails : productDetailsList) {
                    if (mSkuDetails.getProductId().equalsIgnoreCase(productId)) {
                        return mSkuDetails;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setMemberManagerListener(IMemberManagerListener memberManagerListener) {
        this.memberManagerListener = memberManagerListener;
    }

    private void onFinishingCheckIAP(boolean isErrorIAP, int errorCode) {
        if (memberManagerListener != null) {
            memberManagerListener.onFinishingCheckIAP(isErrorIAP, errorCode);
        }
    }

    public void onDestroy() {
        try {
            if (billingClient != null) {
                billingClient.endConnection();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getMemberId(Context mContext, String sku) {
        try {
            int len = TYPE_MEMBERS.length;
            String[] mListItems = mContext.getResources().getStringArray(R.array.array_product_ids);
            for (int i = 0; i < len; i++) {
                String item = mListItems[i];
                if (item.equalsIgnoreCase(sku)) {
                    return TYPE_MEMBERS[i];
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isIAPremiumMember(Context mContext) {
        try {
            int id = XRadioSettingManager.getIdMember(mContext);
            int len = TYPE_MEMBERS.length;
            if (id >= TYPE_MEMBERS[0] && id <= TYPE_MEMBERS[len - 1]) {
                for (int typeMember : TYPE_MEMBERS) {
                    if (id == typeMember) {
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

    @Nullable
    public ProductDetails.PricingPhase getSubscriptionPricePhase(@Nullable ProductDetails productDetails) {
        if (productDetails == null) return null;
        List<ProductDetails.SubscriptionOfferDetails> details = productDetails.getSubscriptionOfferDetails();
        if (details != null && details.size() > 0) {
            ProductDetails.SubscriptionOfferDetails offer = details.get(0);
            List<ProductDetails.PricingPhase> pricePhases = offer.getPricingPhases().getPricingPhaseList();
            if (!pricePhases.isEmpty()) {
                for (ProductDetails.PricingPhase price : pricePhases) {
                    YPYLog.e(IRadioConstants.TAG, "=====>stupid price=" + price.getFormattedPrice() + "===>recurring=" + price.getRecurrenceMode());
                    if (price.getRecurrenceMode() == ProductDetails.RecurrenceMode.INFINITE_RECURRING) {
                        return price;
                    }
                }
            }
        }
        return null;

    }

    private QueryProductDetailsParams buildSkuPrams() {
        String[] mListItems = mContext.getResources().getStringArray(R.array.array_product_ids);
        List<QueryProductDetailsParams.Product> skuList = new ArrayList<>();
        for (String productId : mListItems) {
            QueryProductDetailsParams.Product product = QueryProductDetailsParams
                    .Product.newBuilder()
                    .setProductId(productId).setProductType(BillingClient.ProductType.SUBS).build();
            skuList.add(product);
        }
        QueryProductDetailsParams.Builder params = QueryProductDetailsParams.newBuilder();
        params.setProductList(skuList);
        return params.build();
    }

    public static void goToManageSub(@NonNull Activity context) {
        String url = String.format(FORMAT_CANCEL_SUB, context.getPackageName());
        ShareActionUtils.goToUrl(context, url);
    }

    public interface IMemberManagerListener {
        void onFinishingCheckIAP(boolean isSuccess, int errorCode);
    }


}
