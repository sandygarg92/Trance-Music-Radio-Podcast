package com.onlineradiofm.trancemusicradio.ypylibs.googlecast;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.gms.cast.CastMediaControlIntent;

import androidx.annotation.NonNull;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 11/10/18.
 */
public class YPYCastButton {

    private final MediaRouteButton mBtnCast;
    private final MediaRouter mMediaRouter;
    private final MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;

    public YPYCastButton(@NonNull Context mContext, @NonNull MediaRouteButton mBtnCast
            , @NonNull String castId, Drawable mDrawable) {
        this.mBtnCast=mBtnCast;
        this.mMediaRouter = MediaRouter.getInstance(mContext.getApplicationContext());
        this.mMediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(castId)).build();
        this.mBtnCast.setRouteSelector(mMediaRouteSelector);
        if(mDrawable!=null){
            this.mBtnCast.setRemoteIndicatorDrawable(mDrawable);
        }

    }

    public void setMediaCallback(MediaRouter.Callback mediaCallback){
        try{
            if(mMediaRouter!=null && mediaCallback!=null && mMediaRouteSelector!=null){
                this.mMediaRouterCallback=mediaCallback;
                mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                        MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onRemoveCallback(){
        try{
            if(mMediaRouter!=null && mMediaRouterCallback!=null ){
                mMediaRouter.removeCallback(mMediaRouterCallback);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
