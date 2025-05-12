package com.onlineradiofm.trancemusicradio.model;

import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.google.gson.annotations.SerializedName;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-02-10.
 */
public class TopRadioModel {

    @SerializedName("editor_choices")
    private ResultModel<RadioModel> listEditorChoices;

    @SerializedName("top_news")
    private ResultModel<RadioModel> listNewReleases;

    public TopRadioModel() {

    }

    public ResultModel<RadioModel> getListEditorChoices() {
        return listEditorChoices;
    }


    public ResultModel<RadioModel> getListNewReleases() {
        return listNewReleases;
    }

}
