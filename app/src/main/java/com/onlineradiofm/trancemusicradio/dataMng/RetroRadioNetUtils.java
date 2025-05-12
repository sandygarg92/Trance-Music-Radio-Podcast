package com.onlineradiofm.trancemusicradio.dataMng;

import android.content.Context;

import androidx.annotation.NonNull;

import com.onlineradiofm.trancemusicradio.constants.IRadioConstants;
import com.onlineradiofm.trancemusicradio.model.UserModel;
import com.onlineradiofm.trancemusicradio.setting.XRadioSettingManager;
import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.ApplicationUtils;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYOKHttpClient;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 7/16/20.
 */
public class RetroRadioNetUtils implements IRadioConstants {

    private static final int  DELTA_PERCENTAGE_UPDATE = 3;
    public static final String ABC_XYZ_FUCK = "~1x2Y4z@3$%^Radio11";
    private static final String MULTIPART_FORM = "multipart/form-data";

    private static RetroRadioApiService service;

    private static RetroRadioApiService getInstanceAPIService() {
        if (service == null) {
            OkHttpClient mOkHttp = YPYOKHttpClient.build();
            Retrofit mRetrofit = new Retrofit.Builder()
                    .baseUrl(XRadioNetUtils.URL_HOST + FOLDER_API)
                    .client(mOkHttp)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = mRetrofit.create(RetroRadioApiService.class);
        }
        return service;
    }

    public static void onDestroy() {
        service = null;
    }

    public static Observable<ResultModel<UserModel>> signIn(Context mContext, String email, String password, String img, String name) {
        RetroRadioApiService mService = getInstanceAPIService();
        String strSign = email + ABC_XYZ_FUCK + password;
        String sign = ApplicationUtils.getMd5Hash(strSign);

        YPYLog.e(TAG, "==>signIn=" + strSign + "==>sign=" + sign);

        RequestBody emailBd = createRequestBodyFromStr(email);
        RequestBody passBd = createRequestBodyFromStr(password);
        RequestBody imageBd = createRequestBodyFromStr(img);
        RequestBody nameBd = createRequestBodyFromStr(name);
        RequestBody signBd = createRequestBodyFromStr(sign);
        RequestBody apiKeyBD = createRequestBodyFromStr(XRadioNetUtils.API_KEY);
        return mService.signIn(apiKeyBD, emailBd, passBd, imageBd, nameBd, signBd);
    }

    public static Observable<ResultModel<AbstractModel>> deleteAccount(Context mContext) {
        RetroRadioApiService mService = getInstanceAPIService();

        long userId = XRadioSettingManager.getUserId(mContext);
        String userToken = XRadioSettingManager.getUserToken(mContext);

        RequestBody userIdBd = createRequestBodyFromStr(String.valueOf(userId));
        RequestBody tokenBd = createRequestBodyFromStr(userToken);

        String strSign = userId + ABC_XYZ_FUCK + userToken;
        String sign = ApplicationUtils.getMd5Hash(strSign);
        YPYLog.e(TAG, "==>deleteAccount=" + strSign + "==>sign=" + sign);

        RequestBody signBd = createRequestBodyFromStr(sign);
        RequestBody apiKeyBD = createRequestBodyFromStr(XRadioNetUtils.API_KEY);
        return mService.deleteAccount(apiKeyBD, userIdBd, tokenBd, signBd);
    }

    public static Observable<ResultModel<AbstractModel>> updateCount(Context mContext, long radioId, String type, int value) {
        RetroRadioApiService mService = getInstanceAPIService();

        long userId = XRadioSettingManager.getUserId(mContext);
        String userToken = XRadioSettingManager.getUserToken(mContext);
        RequestBody userIdBd = createRequestBodyFromStr(String.valueOf(userId));
        RequestBody tokenBd = createRequestBodyFromStr(userToken);
        RequestBody radioBd = createRequestBodyFromStr(String.valueOf(radioId));

        RequestBody typeBd = createRequestBodyFromStr(String.valueOf(type));
        RequestBody valueBd = createRequestBodyFromStr(String.valueOf(value));
        RequestBody apiKeyBD = createRequestBodyFromStr(XRadioNetUtils.API_KEY);
        if (userId > 0) {
            return mService.updateCount(apiKeyBD, userIdBd, tokenBd, radioBd, typeBd, valueBd);
        }
        else {
            return mService.updateCount(apiKeyBD, radioBd, typeBd, valueBd);
        }
    }

    public static Observable<ResultModel<AbstractModel>> updateReport(Context mContext, long radioId) {
        RetroRadioApiService mService = getInstanceAPIService();
        long userId = XRadioSettingManager.getUserId(mContext);
        String userToken = XRadioSettingManager.getUserToken(mContext);
        RequestBody userIdBd = createRequestBodyFromStr(String.valueOf(userId));
        RequestBody tokenBd = createRequestBodyFromStr(userToken);
        RequestBody radioBd = createRequestBodyFromStr(String.valueOf(radioId));
        RequestBody apiKeyBD = createRequestBodyFromStr(XRadioNetUtils.API_KEY);
        return mService.updateReport(apiKeyBD, userIdBd, tokenBd, radioBd);
    }

    private static RequestBody createRequestBodyFromStr(String data) {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM), data);
    }

    public static Observable<ResultModel<File>> downloadFile(@NonNull String linkDownload, @NonNull String mDirectoryPath) {
        OkHttpClient okHttp = YPYOKHttpClient.build();
        int fromIndex = linkDownload.indexOf("://");
        int startIndex = linkDownload.indexOf("/", fromIndex + 3);
        String urlRoot;
        if (startIndex >= 0) {
            urlRoot = linkDownload.substring(0, startIndex) + "/";
        }
        else {
            urlRoot = linkDownload.substring(0, linkDownload.lastIndexOf("/")) + "/";
        }
        Retrofit mRetrofit = new Retrofit.Builder()
                .client(okHttp)
                .baseUrl(urlRoot)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DownloadFileService service = mRetrofit.create(DownloadFileService.class);
        String newNameFile = ApplicationUtils.getMd5Hash(linkDownload) + FORMAT_SAVED;
        return service.downloadFile(linkDownload).flatMap(processResponse(mDirectoryPath, newNameFile));

    }

    private static Function<Response<ResponseBody>, Observable<ResultModel<File>>> processResponse(@NonNull String mDirectoryPath, @NonNull String mFileName) {
        return response -> saveToDiskRx(response, mDirectoryPath, mFileName);
    }

    private static Observable<ResultModel<File>> saveToDiskRx(final Response<ResponseBody> response, @NonNull String mDirectoryPath, @NonNull String mFileName) {
        return Observable.create(emitter -> {
            try {
                File mFileRoot = new File(mDirectoryPath);
                if (!mFileRoot.exists()) {
                    mFileRoot.mkdirs();
                }
                ResponseBody mBody = response.body();
                long contentLength = mBody != null ? mBody.contentLength() : 0;
                if (contentLength > 0) {
                    ForwardingSource forwardingSource = new ForwardingSource(mBody.source()) {
                        private long totalBytesRead = 0L;
                        private int pivotProcess = 0;

                        @Override
                        public long read(@NonNull Buffer sink, long byteCount) {
                            try {
                                long bytesRead = super.read(sink, byteCount);
                                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                                boolean done = bytesRead == -1;
                                int progress = (int) (done ? 100f : 100f * totalBytesRead / contentLength);
                                int delta = progress - pivotProcess;

                                if (delta >= DELTA_PERCENTAGE_UPDATE) {
                                    this.pivotProcess = progress;
                                    ResultModel<File> mResultModel = new ResultModel<>(ResultModel.STATUS_DOWNLOADING);
                                    mResultModel.setPercentage(progress);
                                    emitter.onNext(mResultModel);
                                }
                                return bytesRead;
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            return -1;

                        }
                    };

                    File destinationFile = new File(mFileRoot, mFileName);
                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(destinationFile));
                    bufferedSink.writeAll(forwardingSource);
                    bufferedSink.close();

                    ResultModel<File> resultModel = new ResultModel<>(ResultModel.STATUS_OK, "Success");
                    ArrayList<File> mListFiles = new ArrayList<>();
                    mListFiles.add(destinationFile);
                    resultModel.setListModels(mListFiles);

                    if (!emitter.isDisposed()) {
                        emitter.onNext(resultModel);
                        emitter.onComplete();
                    }
                }
                else {
                    if (!emitter.isDisposed()) {
                        emitter.onError(new Exception("File Not Found"));
                    }
                }

            }
            catch (Exception e) {
                e.printStackTrace();
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }
        });
    }
}
