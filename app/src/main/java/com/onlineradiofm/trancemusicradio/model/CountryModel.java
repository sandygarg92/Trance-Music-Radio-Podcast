package com.onlineradiofm.trancemusicradio.model;

import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.dataMng.XRadioNetUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 4/20/18.
 */
public class CountryModel extends AbstractModel {

    private String code;

    public CountryModel(long id, String name, String image) {
        super(id, name, image);
    }

    @Override
    public String getArtWork() {
        if (!TextUtils.isEmpty(image) && !image.startsWith("http")) {
            image = XRadioNetUtils.URL_HOST + XRadioNetUtils.FOLDER_COUNTRIES + image;
        }
        return super.getImage();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
