package com.onlineradiofm.trancemusicradio.fragment;

import static com.onlineradiofm.trancemusicradio.constants.IRadioConstants.IMG_MEMBERS;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.UserMessagingPlatform;
import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.dataMng.RetroRadioNetUtils;
import com.onlineradiofm.trancemusicradio.databinding.FragmentProfileBinding;
import com.onlineradiofm.trancemusicradio.databinding.ItemSettingBinding;
import com.onlineradiofm.trancemusicradio.gdpr.GDPRManager;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.fragment.YPYFragment;
import com.onlineradiofm.trancemusicradio.ypylibs.imageloader.GlideImageLoader;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.task.IYPYCallback;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ShareActionUtils;

import io.reactivex.Observable;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 4/20/21.
 */
public class FragmentProfile extends YPYFragment<FragmentProfileBinding> implements View.OnClickListener {

    private MainActivity mContext;

    @ColorInt
    private int bgColor;
    @ColorInt
    private int imgColor;
    @ColorInt
    private int textColor;
    @ColorInt
    private int dividerColor;
    @ColorInt
    private int chevronColor;
    @ColorInt
    private int rippleColor;

    private GoogleSignInClient mGoogleSignInClient;

    @NonNull
    @Override
    protected FragmentProfileBinding getViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileBinding.inflate(inflater);
    }

    @Override
    public void findView() {
        mContext = (MainActivity) requireActivity();
        viewBinding.tvVersion.setText(String.format(mContext.getString(R.string.format_version)
                ,ApplicationUtils.getVersionName(mContext)));
        initGoogleSignIn();
        startLoadData();
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
    }

    @Override
    public void startLoadData() {
        super.startLoadData();
        if (!isLoadingData()) {
            setLoadingData(true);
            setUpAccount();
            setUpLayoutSetting();
        }
    }

    private void setUpAccount() {
        boolean isSignedIn = XRadioSettingManager.isSignedIn(mContext);

        String infoMember = isSignedIn ? XRadioSettingManager.getUserEmail(mContext)
                : mContext.getString(R.string.info_tap_login);
        viewBinding.tvInfoMember.setText(infoMember);
        viewBinding.imgMember.setVisibility(mContext.isPremiumMember() ? View.VISIBLE : View.GONE);

        if (mContext.isPremiumMember()) {
            int id = XRadioSettingManager.getIdMember(mContext);
            viewBinding.imgMember.setImageResource(IMG_MEMBERS[id - 1]);
            if (isSignedIn) {
                String typePremium = mContext.getResources().getStringArray(R.array.array_members)[id - 1];
                String infoPremium = String.format(getString(R.string.format_info_member), typePremium);
                viewBinding.tvInfoMember.setText(infoPremium);
            }
        }

        String userName = isSignedIn ? XRadioSettingManager.getDisplayName(mContext, true) : mContext.getString(R.string.app_name);
        viewBinding.tvUserName.setText(userName);

        String avatarUri = isSignedIn ? XRadioSettingManager.getUserAvatar(mContext) : null;
        GlideImageLoader.displayImage(mContext, viewBinding.imgAvatar, avatarUri, R.drawable.ic_account_default);

        viewBinding.layoutBtnSignOut.setVisibility(isSignedIn ? View.VISIBLE : View.GONE);
        viewBinding.layoutBtnDelete.setVisibility(isSignedIn ? View.VISIBLE : View.GONE);
        viewBinding.btnDeleteAccount.setOnClickListener(this);
        viewBinding.btnSignOut.setOnClickListener(this);
        viewBinding.layoutInfoMember.setOnClickListener(!isSignedIn ? v -> mContext.goToLogin() : null);

    }


    private void setUpLayoutSetting() {
        viewBinding.layoutItemSetting.removeAllViews();
        boolean isDark = XRadioSettingManager.isDarkMode(mContext);
        boolean isRTL = ApplicationUtils.isSupportRTL();

        bgColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_list_bg_color : R.color.light_list_bg_color);
        imgColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_color_accent : R.color.light_color_accent);
        dividerColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_list_color_divider : R.color.light_list_color_divider);
        textColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_list_color_main_text : R.color.light_list_color_main_text);
        chevronColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_list_color_secondary_text : R.color.light_list_color_secondary_text);
        rippleColor = ContextCompat.getColor(mContext, isDark ? R.color.dark_ripple_button_color : R.color.light_ripple_button_color);

        if (!mContext.isPremiumMember()) {
            addSettingItem(isRTL, R.drawable.ic_crown_36dp, R.string.title_pro_version, () -> mContext.goToPremium());
        }
        addSettingItem(isRTL, R.drawable.ic_heart_white_36dp, R.string.title_rate_me, () -> {
            String urlApp = String.format(URL_FORMAT_LINK_APP, mContext.getPackageName());
            ShareActionUtils.goToUrl(mContext, urlApp);
        });

        addSettingItem(isRTL, R.drawable.ic_more_radio_36dp, R.string.title_more_apps, () -> {
            mContext.goToUrl(mContext.getString(R.string.title_more_apps), IRadioConstants.URL_MORE_APPS);
        });

        addSettingItem(isRTL, R.drawable.ic_share_white_24dp, R.string.title_menu_share, this::shareApp);

        if (!TextUtils.isEmpty(IRadioConstants.URL_WEBSITE)) {
            addSettingItem(isRTL, R.drawable.ic_website_white_36dp, R.string.title_website, () -> {
                mContext.goToUrl(mContext.getString(R.string.title_website), IRadioConstants.URL_WEBSITE);
            });
        }


        addSettingItem(isRTL, R.drawable.ic_email_24dp, R.string.title_contact_us, () -> {
            ShareActionUtils.shareViaEmail(mContext, IRadioConstants.YOUR_CONTACT_EMAIL, "", "");
        });
        addSettingItem(isRTL, R.drawable.ic_policy_white_36dp, R.string.title_privacy_policy, () -> {
            mContext.goToUrl(mContext.getString(R.string.title_privacy_policy), IRadioConstants.URL_PRIVACY_POLICY);
        });
        addSettingItem(isRTL, R.drawable.ic_tos_white_36dp, R.string.title_term_of_use, () -> {
            mContext.goToUrl(mContext.getString(R.string.title_term_of_use), IRadioConstants.URL_TERM_OF_USE);
        });

        ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(mContext);
        boolean isAvailable = consentInformation.isConsentFormAvailable();
        boolean isGDPR = !mContext.isPremiumMember() && isAvailable;
        if (isGDPR) {
            addSettingItem(isRTL, R.drawable.ic_settings_24dp, R.string.title_setting_ads, () -> {
                GDPRManager.getInstance().loadConsentForm(mContext, null);
            });
        }

    }

    private void shareApp() {
        String urlApp1 = String.format(URL_FORMAT_LINK_APP, mContext.getPackageName());
        String msg = String.format(getString(R.string.info_share_app), getString(R.string.app_name), urlApp1);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.title_menu_share)));
    }

    private void addSettingItem(boolean isRTL, @DrawableRes int iconId, @StringRes int textId, IYPYCallback mCallback) {
        ItemSettingBinding itemViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_setting,
                viewBinding.layoutItemSetting, false);
        itemViewBinding.layoutRoot.setBackgroundColor(bgColor);
        itemViewBinding.layoutRippleSetting.setRippleColor(rippleColor);
        itemViewBinding.imgView.setBackgroundColor(imgColor);
        itemViewBinding.tvName.setTextColor(textColor);
        itemViewBinding.listDivider.setBackgroundColor(dividerColor);
        itemViewBinding.imgChevron.setTextColor(chevronColor);

        itemViewBinding.imgView.setImageResource(iconId);
        itemViewBinding.tvName.setText(textId);
        if (isRTL) {
            itemViewBinding.tvName.setGravity(Gravity.END);
            itemViewBinding.imgChevron.setText(Html.fromHtml(mContext.getString(R.string.icon_chevron_left)));
        }
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewBinding.layoutItemSetting.addView(itemViewBinding.getRoot(), mLayoutParams);

        itemViewBinding.layoutRoot.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onAction();
            }
        });

    }

    private void deleteAccount() {
        try {
            Observable<ResultModel<AbstractModel>> mObservable = RetroRadioNetUtils.deleteAccount(mContext);
            mContext.addObservableToObserver(mObservable, resultModel -> signOut());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signOut() {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut();
        }
        XRadioSettingManager.logOut(mContext);
        setUpAccount();
        viewBinding.scrollView.postDelayed(() -> viewBinding.scrollView.smoothScrollTo(0, 0), 100);
        mContext.resetLoadWhenSignOut();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_sign_out) {
            mContext.showFullDialog(R.string.title_confirm, getString(R.string.info_logout),
                    R.string.title_sign_out, R.string.title_cancel, this::signOut);
        }
        else if (id == R.id.btn_delete_account) {
            String msgDeleteAccount = String.format(getString(R.string.format_delete_account), getString(R.string.app_name));
            mContext.showFullDialog(R.string.title_confirm, msgDeleteAccount,
                    R.string.title_delete_account, R.string.title_cancel, this::deleteAccount);
        }
    }

}
