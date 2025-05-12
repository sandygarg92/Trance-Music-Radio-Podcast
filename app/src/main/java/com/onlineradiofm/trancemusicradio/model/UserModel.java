package com.onlineradiofm.trancemusicradio.model;

import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2019-05-16.
 */
public class UserModel extends AbstractModel {

    @SerializedName("token")
    private String userToken;

    @SerializedName("email")
    private String email;

    public UserModel(long id, String name, String image) {
        super(id, name, image);
    }

    public String getUserToken() {
        return userToken;
    }

    public String getEmail() {
        return email;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserModel{" +
                "userToken='" + userToken + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
