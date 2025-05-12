/*
 * Copyright (c) 2018. Radio Polska - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://radiopolska.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onlineradiofm.trancemusicradio.model;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 4/20/18.
 */
public class ThemeModel extends AbstractModel {

    @SerializedName("grad_start_color")
    private String gradStartColor;

    @SerializedName("grad_end_color")
    private String gradEndColor;

    @SerializedName("grad_orientation")
    private int gradOrientation;

    private transient GradientDrawable.Orientation orientation;

    public ThemeModel(long id, String name, String image) {
        super(id, name, image);
    }

    public String getGradStartColor() {
        return gradStartColor;
    }


    public String getGradEndColor() {
        return gradEndColor;
    }


    public int getGradOrientation() {
        return gradOrientation;
    }

    public void setGradOrientation(int gradOrientation) {
        this.gradOrientation = gradOrientation;
    }

    public GradientDrawable.Orientation getOrientation() {
        if (orientation == null) {
            orientation = ApplicationUtils.getOrientation(gradOrientation);
        }
        return orientation;
    }

    public void setOrientation(GradientDrawable.Orientation orientation) {
        this.orientation = orientation;
    }

    public void setGradStartColor(String gradStartColor) {
        this.gradStartColor = gradStartColor;
    }

    public void setGradEndColor(String gradEndColor) {
        this.gradEndColor = gradEndColor;
    }

    @Override
    public String getArtWork() {
        if (!TextUtils.isEmpty(image) && !image.startsWith("http")) {
            image = XRadioNetUtils.URL_HOST + XRadioNetUtils.FOLDER_THEMES + image;
        }
        return super.getImage();
    }

    public static ThemeModel createDarkTheme(Context mContext) {
        ThemeModel model = new ThemeModel(IRadioConstants.DARK_MODE_THEME_ID
                , mContext.getString(R.string.title_dark_mode), "");
        model.setGradEndColor(IRadioConstants.DARK_MODE_BG_COLOR);
        model.setGradStartColor(IRadioConstants.DARK_MODE_BG_COLOR);
        return model;
    }

    @Override
    public AbstractModel cloneObject() {
        ThemeModel model = new ThemeModel(id, name, image);
        model.setGradStartColor(gradStartColor);
        model.setGradEndColor(gradEndColor);
        model.setOrientation(orientation);
        return model;
    }

    @NonNull
    @Override
    public String toString() {
        return "ThemeModel{" +
                "gradStartColor='" + gradStartColor + '\'' +
                ", gradEndColor='" + gradEndColor + '\'' +
                ", gradOrientation=" + gradOrientation +
                ", orientation=" + orientation +
                '}';
    }
}
