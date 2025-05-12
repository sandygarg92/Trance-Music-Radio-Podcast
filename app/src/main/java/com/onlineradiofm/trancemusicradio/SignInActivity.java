package com.onlineradiofm.trancemusicradio;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.onlineradiofm.trancemusicradio.dataMng.RetroRadioNetUtils;
import com.onlineradiofm.trancemusicradio.databinding.ActivitySignInBinding;
import com.onlineradiofm.trancemusicradio.model.UserModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import io.reactivex.Observable;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 7/16/20.
 */
public class SignInActivity extends RadioFragmentActivity<ActivitySignInBinding> implements View.OnClickListener {

    private static final int RC_SIGN_IN = 101;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected ActivitySignInBinding getViewBinding() {
        return ActivitySignInBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onDoWhenDone() {
        super.onDoWhenDone();
        setActionBarTitle(R.string.title_sign_in);
        viewBinding.tvPolicy.setOnClickListener(this);
        viewBinding.tvTos.setOnClickListener(this);
        viewBinding.signInButton.setOnClickListener(this);

        initGoogleSignIn();
    }

    private void initGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

        int textColor = ContextCompat.getColor(this, isDark ? R.color.dark_color_accent : R.color.light_color_accent);
        int secondColor = ContextCompat.getColor(this, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);

        viewBinding.tvHeaderLogin.setTextColor(textColor);
        viewBinding.tvInfoHeaderLogin.setTextColor(secondColor);
        viewBinding.tvPolicy.setTextColor(secondColor);
        viewBinding.tvTos.setTextColor(secondColor);
        viewBinding.divider.setBackgroundColor(secondColor);

        int colorContainerBg = ContextCompat.getColor(this, isDark ? R.color.dark_pager_color_background
                : R.color.white);
        viewBinding.layoutContainer.setBackgroundColor(colorContainerBg);
        findViewById(R.id.layout_bottom).setBackgroundColor(colorContainerBg);

        if (!isDark) {
            float elevation = getResources().getDimensionPixelOffset(R.dimen.card_elevation);
            ViewCompat.setElevation(this.viewBinding.myToolbar.getRoot(), elevation);
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        super.setActionBarTitle("");
        viewBinding.myToolbar.toolBarTitle.setText(title);
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
        else if (id == R.id.sign_in_button) {
            signIn();
        }
    }

    @Override
    public boolean backToHome() {
        Intent mIntent = new Intent(this, MainActivity.class);
        startActivity(mIntent);
        finish();
        return true;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        }
        catch (ApiException e) {
            YPYLog.e("DCM", "=======>signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        try {
            if (account != null) {
                String id = account.getId();
                String email = account.getEmail();
                String img = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
                String name = !TextUtils.isEmpty(account.getDisplayName()) ? account.getDisplayName() : "Unknown";
                String password = ApplicationUtils.getMd5Hash("gg_" + id);
                if (name != null && name.length() >= MAX_LENGTH_USER_NAME) {
                    name = name.substring(0, MAX_LENGTH_USER_NAME) + "...";
                }
                Observable<ResultModel<UserModel>> mObservable = RetroRadioNetUtils.signIn(this, email, password, img, name);
                addObservableToObserver(mObservable, resultModel -> {
                    UserModel signInModel = resultModel.firstModel();
                    if (signInModel != null) {
                        showToastWithLongTime(R.string.info_sign_in_success);
                        XRadioSettingManager.saveUserModel(this, signInModel);
                        backToHome();
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
