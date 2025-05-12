package com.onlineradiofm.trancemusicradio.dataMng;

import com.onlineradiofm.trancemusicradio.model.UserModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile:
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 7/16/20.
 */

public interface RetroRadioApiService {

    @Multipart
    @POST("api.php?method=signIn")
    Observable<ResultModel<UserModel>> signIn(@Part("api_key") RequestBody apiKey,
                                              @Part("email") RequestBody email,
                                              @Part("password") RequestBody password,
                                              @Part("img") RequestBody img,
                                              @Part("name") RequestBody name,
                                              @Part("sign") RequestBody sign);

    @Multipart
    @POST("api.php?method=deleteAccount")
    Observable<ResultModel<AbstractModel>> deleteAccount(@Part("api_key") RequestBody apiKey,
                                                         @Part("user_id") RequestBody userId,
                                                         @Part("token") RequestBody token,
                                                         @Part("sign") RequestBody sign);


    @Multipart
    @POST("api.php?method=updateFav")
    Observable<ResultModel<AbstractModel>> updateFav(@Part("api_key") RequestBody apiKey,
                                                     @Part("user_id") RequestBody userId,
                                                     @Part("token") RequestBody token,
                                                     @Part("radio_id") RequestBody radioId,
                                                     @Part("value") RequestBody value,
                                                     @Part("piority") RequestBody priority);

    @Multipart
    @POST("api.php?method=updateCount")
    Observable<ResultModel<AbstractModel>> updateCount(@Part("api_key") RequestBody apiKey,
                                                       @Part("user_id") RequestBody userId,
                                                       @Part("token") RequestBody token,
                                                       @Part("radio_id") RequestBody radioId,
                                                       @Part("type") RequestBody type,
                                                       @Part("value") RequestBody value);

    @Multipart
    @POST("api.php?method=updateCount")
    Observable<ResultModel<AbstractModel>> updateCount(@Part("api_key") RequestBody apiKey,
                                                       @Part("radio_id") RequestBody radioId,
                                                       @Part("type") RequestBody type,
                                                       @Part("value") RequestBody value);

    @Multipart
    @POST("api.php?method=updateReport")
    Observable<ResultModel<AbstractModel>> updateReport(@Part("api_key") RequestBody apiKey,
                                                     @Part("user_id") RequestBody userId,
                                                     @Part("token") RequestBody token,
                                                     @Part("radio_id") RequestBody radioId);
}
