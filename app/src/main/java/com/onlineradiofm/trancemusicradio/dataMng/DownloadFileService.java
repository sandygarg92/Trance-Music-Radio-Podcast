package com.onlineradiofm.trancemusicradio.dataMng;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 9/14/18.
 */
public interface DownloadFileService {

    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFile(@Url String fileUrl);
}
