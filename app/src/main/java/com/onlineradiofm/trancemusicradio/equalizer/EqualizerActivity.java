package com.onlineradiofm.trancemusicradio.equalizer;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.RadioFragmentActivity;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.databinding.ActivityEqualizerBinding;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.music.constant.IYPYStreamConstants;
import com.onlineradiofm.trancemusicradio.ypylibs.music.manager.YPYStreamManager;
import com.onlineradiofm.trancemusicradio.ypylibs.music.mediaplayer.YPYMediaPlayer;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.StringUtils;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.triggertrap.seekarc.SeekArc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

public class EqualizerActivity extends RadioFragmentActivity<ActivityEqualizerBinding> implements IRadioConstants, IYPYStreamConstants {

    private Object mMediaPlayer;

    private Equalizer mEqualizer;

    private String[] mLists;
    private ArrayList<VerticalSeekBar> listSeekBars = new ArrayList<>();

    private short bands;
    private short minEQLevel;
    private boolean isCreateLocal;
    private BassBoost mBassBoost;
    private Virtualizer mVirtualizer;

    @Override
    protected ActivityEqualizerBinding getViewBinding() {
        return ActivityEqualizerBinding.inflate(getLayoutInflater());
    }


    @Override
    public void onDoWhenDone() {
        super.onDoWhenDone();
        setActionBarTitle(R.string.title_equalizer);
    }

    @Override
    public void setActionBarTitle(String title) {
        super.setActionBarTitle("");
        viewBinding.myToolbar.toolBarTitle.setText(title);
    }

    @Override
    public void updateThemeColor(boolean isDark) {
        super.updateThemeColor(isDark);
        int actionBarColor = ContextCompat.getColor(this, !isDark ? R.color.light_action_bar_background : R.color.dark_action_bar_background);
        int actionBarTextColor = ContextCompat.getColor(this, !isDark ? R.color.light_action_bar_text_color : R.color.dark_action_bar_text_color);
        setUpCustomizeActionBar(actionBarColor, actionBarTextColor, true);
        viewBinding.myToolbar.toolBarTitle.setTextColor(actionBarTextColor);

        viewBinding.equalizerSwitch.setOnClickListener(v -> {
            XRadioSettingManager.setEqualizer(EqualizerActivity.this, viewBinding.equalizerSwitch.isChecked());
            startCheckEqualizer();
        });

        int bgColor = ContextCompat.getColor(this, isDark ? R.color.dark_color_background : R.color.light_color_background);
        viewBinding.layoutBg.setBackgroundColor(bgColor);

        if (!isDark) {
            float elevation = getResources().getDimensionPixelOffset(R.dimen.card_elevation);
            ViewCompat.setElevation(this.viewBinding.myToolbar.getRoot(), elevation);
        }

        int bgPagerColor = ContextCompat.getColor(this, isDark ? R.color.dark_pager_color_background : R.color.light_pager_color_background);
        viewBinding.scrollView.setBackgroundColor(bgPagerColor);

        int mainColor = ContextCompat.getColor(this, isDark ? R.color.dark_text_main_color : R.color.light_text_main_color);
        viewBinding.tvBass.setTextColor(mainColor);
        viewBinding.tvInfoBass.setTextColor(mainColor);
        viewBinding.tvVirtualizer.setTextColor(mainColor);
        viewBinding.tvInfoVirtualizer.setTextColor(mainColor);

        int secondColor = ContextCompat.getColor(this, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);
        int colorAccent = ContextCompat.getColor(this, isDark ? R.color.dark_color_accent : R.color.light_color_accent);
        setUpBassBooth(secondColor, colorAccent);
        setUpVirtualizer(secondColor, colorAccent);
        registerApplicationBroadcastReceiver(EqualizerActivity.this);
        setUpEffects(isDark,true);
    }

    private void setUpBassBooth(int secondColor, int colorAccent) {
        viewBinding.seekBass.setProgressColor(colorAccent);
        viewBinding.seekBass.setArcColor(secondColor);
        viewBinding.seekBass.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                try {
                    viewBinding.tvInfoBass.setText(String.valueOf(progress));
                    if (fromUser) {
                        if (mBassBoost != null) {
                            XRadioSettingManager.setBassBoost(EqualizerActivity.this, (short) progress);
                            mBassBoost.setStrength((short) (progress * RATE_EFFECT));
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });
    }

    private void setUpVirtualizer(int secondColor, int colorAccent) {
        viewBinding.seekVir.setProgressColor(colorAccent);
        viewBinding.seekVir.setArcColor(secondColor);
        viewBinding.seekVir.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                try {
                    viewBinding.tvInfoVirtualizer.setText(String.valueOf(progress));
                    if (fromUser) {
                        if (mVirtualizer != null) {
                            XRadioSettingManager.setVirtualizer(EqualizerActivity.this, (short) progress);
                            mVirtualizer.setStrength((short) (progress * RATE_EFFECT));
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });
    }


    private void setUpEqualizerParams() {
        if (mEqualizer != null) {
            String presetStr = XRadioSettingManager.getEqualizerPreset(this);
            if (!TextUtils.isEmpty(presetStr)) {
                if (StringUtils.isNumber(presetStr)) {
                    short preset = Short.parseShort(presetStr);
                    short numberPreset = mEqualizer.getNumberOfPresets();
                    if (numberPreset > 0) {
                        if (preset < numberPreset - 1 && preset >= 0) {
                            mEqualizer.usePreset(preset);
                            viewBinding.spinnerPreset.setSelection(preset);
                            return;
                        }
                    }
                }
            }
            setUpEqualizerCustom();
        }
    }

    private void setUpEqualizerCustom() {
        try {
            if (mEqualizer != null) {
                String params = XRadioSettingManager.getEqualizerParams(this);
                if (!TextUtils.isEmpty(params)) {
                    String[] mEqualizerParams = params.split(":");
                    if (mEqualizerParams.length > 0) {
                        int size = mEqualizerParams.length;
                        for (int i = 0; i < size; i++) {
                            mEqualizer.setBandLevel((short) i, Short.parseShort(mEqualizerParams[i]));
                            listSeekBars.get(i).setProgress(Short.parseShort(mEqualizerParams[i]) - minEQLevel);
                        }
                        viewBinding.spinnerPreset.setSelection(mLists.length - 1);
                        XRadioSettingManager.setEqualizerPreset(this, String.valueOf(mLists.length - 1));
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveEqualizerParams() {
        try {
            if (mEqualizer != null) {
                if (bands > 0) {
                    StringBuilder data = new StringBuilder();
                    for (short i = 0; i < bands; i++) {
                        if (i < bands - 1) {
                            data.append(mEqualizer.getBandLevel(i)).append(":");
                        }
                    }
                    XRadioSettingManager.setEqualizerPreset(this, String.valueOf(mLists.length - 1));
                    XRadioSettingManager.setEqualizerParams(this, data.toString());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startCheckEqualizer() {
        try {
            boolean b = XRadioSettingManager.getEqualizer(this);
            viewBinding.spinnerPreset.setEnabled(b);

            if (mEqualizer != null) {
                mEqualizer.setEnabled(b);
            }
            if (listSeekBars.size() > 0) {
                for (int i = 0; i < listSeekBars.size(); i++) {
                    listSeekBars.get(i).setEnabled(b);
                }
            }
            viewBinding.seekBass.setEnabled(b);
            viewBinding.seekVir.setEnabled(b);
            viewBinding.equalizerSwitch.setChecked(b);
            if (mBassBoost != null) {
                mBassBoost.setEnabled(b);
            }

            if (mVirtualizer != null) {
                mVirtualizer.setEnabled(b);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupEqualizerFxAndUI(boolean isDark, boolean isFirstTime) {
        try {
            mEqualizer = YPYStreamManager.getInstance().getEqualizer();
            if (mEqualizer == null) {
                int audioSession = getAudioSessionId();
                if (audioSession == 0) {
                    backToHome();
                    return;
                }
                mEqualizer = new Equalizer(0, audioSession);
                mEqualizer.setEnabled(XRadioSettingManager.getEqualizer(this));
            }
            bands = mEqualizer.getNumberOfBands();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (bands == 0) {
            backToHome();
            return;
        }
        short[] bandRange = null;
        try {
            bandRange = mEqualizer.getBandLevelRange();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (bandRange == null || bandRange.length < 2) {
            backToHome();
            return;
        }
        minEQLevel = bandRange[0];
        short maxEQLevel = bandRange[1];

        int secondColor = ContextCompat.getColor(this, isDark ? R.color.dark_text_second_color : R.color.light_text_second_color);
        int colorAccent = ContextCompat.getColor(this, isDark ? R.color.dark_color_accent : R.color.light_color_accent);

        if (isFirstTime) {
            for (short i = 0; i < bands; i++) {
                final short band = i;
                View mView = LayoutInflater.from(this).inflate(R.layout.equalizer_vertical_item, null);
                TextView minDbTextView = mView.findViewById(R.id.tv_min_db);
                minDbTextView.setText((minEQLevel / 100) + " dB");

                TextView maxDbTextView = mView.findViewById(R.id.tv_max_db);
                maxDbTextView.setText((maxEQLevel / 100) + " dB");

                VerticalSeekBar mSliderView = mView.findViewById(R.id.mySeekBar);
                mSliderView.setMax(maxEQLevel - minEQLevel);
                mSliderView.setProgress(mEqualizer.getBandLevel(band) - minEQLevel);
                Drawable mDrawable = mSliderView.getProgressDrawable();
                if (mDrawable != null) {
                    if (mDrawable instanceof LayerDrawable) {
                        LayerDrawable mLayerDrawable = (LayerDrawable) mDrawable;
                        Drawable mDrawableBg = mLayerDrawable.findDrawableByLayerId(android.R.id.background);
                        if (mDrawableBg != null) {
                            mDrawable.setColorFilter(secondColor, PorterDuff.Mode.SRC_ATOP);
                        }
                        Drawable mDrawableProgress = mLayerDrawable.findDrawableByLayerId(android.R.id.progress);
                        if (mDrawableProgress != null) {
                            mDrawableProgress.setColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP);
                        }
                        mSliderView.postInvalidate();

                    }
                    else if (mDrawable instanceof StateListDrawable) {
                        StateListDrawable mStateListDrawable = (StateListDrawable) mDrawable;
                        try {
                            int[] currentState = new int[]{android.R.attr.state_enabled};
                            int[] currentState1 = new int[]{-android.R.attr.state_enabled};
                            Method getStateDrawableIndex = StateListDrawable.class.getMethod("getStateDrawableIndex", int[].class);
                            Method getStateDrawable = StateListDrawable.class.getMethod("getStateDrawable", int.class);

                            int index = (int) getStateDrawableIndex.invoke(mStateListDrawable, currentState);
                            int index1 = (int) getStateDrawableIndex.invoke(mStateListDrawable, currentState1);
                            Drawable drawable = (Drawable) getStateDrawable.invoke(mStateListDrawable, index);
                            drawable.setColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP);

                            Drawable drawable1 = (Drawable) getStateDrawable.invoke(mStateListDrawable, index1);
                            drawable1.setColorFilter(secondColor, PorterDuff.Mode.SRC_ATOP);
                        }
                        catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        mSliderView.postInvalidate();
                    }
                    Drawable mThumb = ContextCompat.getDrawable(this, com.triggertrap.seekarc.R.drawable.thumb_default);
                    mThumb.setColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP);
                    mSliderView.setThumb(mThumb);
                }

                mSliderView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            try {
                                mEqualizer.setBandLevel(band, (short) (progress + minEQLevel));
                                saveEqualizerParams();
                                viewBinding.spinnerPreset.setSelection(mLists.length - 1);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                listSeekBars.add(mSliderView);
                LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                viewBinding.layoutBands.addView(mView, mLayoutParams);
            }
        }

    }


    private void setUpBassVirtualizer() {
        try {
            int audioSession = getAudioSessionId();
            if (audioSession == 0) return;
            BassBoost mBassBoost = YPYStreamManager.getInstance().getBassBoost();
            if (mBassBoost == null) {
                mBassBoost = new BassBoost(0, audioSession);
            }
            if (mBassBoost.getStrengthSupported()) {
                Virtualizer mVirtualizer = YPYStreamManager.getInstance().getVirtualizer();
                if (mVirtualizer == null) {
                    mVirtualizer = new Virtualizer(0, audioSession);
                }
                if (mVirtualizer.getStrengthSupported()) {
                    short mCurrentShort = XRadioSettingManager.getBassBoost(this);
                    mBassBoost.setStrength((short) (mCurrentShort * RATE_EFFECT));
                    mBassBoost.setEnabled(XRadioSettingManager.getEqualizer(this));

                    short mCurrentVir = XRadioSettingManager.getVirtualizer(this);
                    mVirtualizer.setStrength((short) (mCurrentVir * RATE_EFFECT));
                    mVirtualizer.setEnabled(XRadioSettingManager.getEqualizer(this));

                    viewBinding.seekBass.setProgress(mCurrentShort);
                    viewBinding.seekVir.setProgress(mCurrentVir);

                    this.mBassBoost = mBassBoost;
                    this.mVirtualizer = mVirtualizer;
                }
                else {
                    viewBinding.layoutBassVir.setVisibility(View.GONE);
                }

            }
            else {
                viewBinding.layoutBassVir.setVisibility(View.GONE);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            viewBinding.layoutBassVir.setVisibility(View.GONE);
        }
    }

    private int getAudioSessionId() {
        try {
            if (mMediaPlayer instanceof MediaPlayer) {
                return ((MediaPlayer) mMediaPlayer).getAudioSessionId();
            }
            if (mMediaPlayer instanceof YPYMediaPlayer) {
                return ((YPYMediaPlayer) mMediaPlayer).getAudioSession();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setUpPresetName() {
        if (mLists != null) {
            return;
        }
        if (mEqualizer != null) {
            short numberPreset = mEqualizer.getNumberOfPresets();
            if (numberPreset > 0) {
                mLists = new String[numberPreset + 1];
                for (short i = 0; i < numberPreset; i++) {
                    mLists[i] = mEqualizer.getPresetName(i);
                }
                mLists[numberPreset] = getString(R.string.title_custom);
                PresetAdapter dataAdapter = new PresetAdapter(this, R.layout.equalizer_item_preset_name, mLists);
                viewBinding.spinnerPreset.setAdapter(dataAdapter);

                dataAdapter.setPresetListener(position -> viewBinding.spinnerPreset.setSelection(position));
                viewBinding.spinnerPreset.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        XRadioSettingManager.setEqualizerPreset(EqualizerActivity.this, String.valueOf(position));
                        try {
                            if (position < mLists.length - 1) {
                                mEqualizer.usePreset((short) position);
                            }
                            else {
                                setUpEqualizerCustom();
                            }
                            for (short i = 0; i < bands; i++) {
                                VerticalSeekBar bar = listSeekBars.get(i);
                                bar.setProgress(mEqualizer.getBandLevel(i) - minEQLevel);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            else {
                viewBinding.spinnerPreset.setVisibility(View.INVISIBLE);
            }
        }
        else {
            viewBinding.spinnerPreset.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToHome();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean backToHome() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listSeekBars != null) {
            listSeekBars.clear();
            listSeekBars = null;
        }
        if (isCreateLocal) {
            try {
                if (mMediaPlayer != null) {
                    if (mMediaPlayer instanceof MediaPlayer) {
                        ((MediaPlayer) mMediaPlayer).release();
                        mMediaPlayer = null;
                    }
                    if (mMediaPlayer instanceof YPYMediaPlayer) {
                        ((YPYMediaPlayer) mMediaPlayer).release();
                        mMediaPlayer = null;
                    }
                }
                if (mEqualizer != null) {
                    mEqualizer.release();
                    mEqualizer = null;
                }
                if (mBassBoost != null) {
                    mBassBoost.release();
                    mBassBoost = null;
                }
                if (mVirtualizer != null) {
                    mVirtualizer.release();
                    mVirtualizer = null;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void processBroadcast(String actionPlay, long value) {
        if (actionPlay.equalsIgnoreCase(ACTION_LOADING)) {
            showProgressDialog();
        }
        if (actionPlay.equalsIgnoreCase(ACTION_DIMINISH_LOADING)) {
            dismissProgressDialog();
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_PLAY)) {
            setUpEffects(XRadioSettingManager.isDarkMode(this), false);
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_STOP) || actionPlay.equalsIgnoreCase(ACTION_ERROR)) {
            dismissProgressDialog();
            backToHome();
        }

    }

    private synchronized void setUpEffects(boolean isDark, boolean isFirstTime) {
        try {
            mMediaPlayer = YPYStreamManager.getInstance().getMediaPlayer();
            if (mMediaPlayer == null) {
                isCreateLocal = true;
                mMediaPlayer = new MediaPlayer();
            }
            setupEqualizerFxAndUI(isDark,isFirstTime);
            setUpBassVirtualizer();

            setUpPresetName();

            if (isFirstTime) {
                startCheckEqualizer();
                setUpEqualizerParams();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
