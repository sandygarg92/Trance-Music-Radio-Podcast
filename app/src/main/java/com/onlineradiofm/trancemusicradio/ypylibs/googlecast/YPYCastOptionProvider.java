package com.onlineradiofm.trancemusicradio.ypylibs.googlecast;

import android.content.Context;

import com.onlineradiofm.trancemusicradio.R;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;
import com.google.android.gms.cast.framework.media.ImageHints;
import com.google.android.gms.cast.framework.media.ImagePicker;
import com.google.android.gms.cast.framework.media.MediaIntentReceiver;
import com.google.android.gms.cast.framework.media.NotificationOptions;
import com.google.android.gms.common.images.WebImage;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;


/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 10/18/18.
 */
public class YPYCastOptionProvider implements OptionsProvider {

    @Override
    public CastOptions getCastOptions(Context context) {
        NotificationOptions notificationOptions = new NotificationOptions.Builder()
                .setSmallIconDrawableResId(R.drawable.ic_notification_24dp)
                .setActions(Arrays.asList(MediaIntentReceiver.ACTION_SKIP_NEXT,
                        MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK,
                        MediaIntentReceiver.ACTION_STOP_CASTING), new int[]{1, 2})
                .setTargetActivityClassName(YPYCastExpandActivity.class.getName())
                .build();

        CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
                .setImagePicker(new ImagePickerImpl())
                .setNotificationOptions(notificationOptions)
                .setExpandedControllerActivityClassName(YPYCastExpandActivity.class.getName())
                .build();

        return new CastOptions.Builder()
                .setReceiverApplicationId(context.getString(R.string.google_cast_app_id))
                .setCastMediaOptions(mediaOptions)
                .build();
    }


    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
    private static class ImagePickerImpl extends ImagePicker {
        @Override
        public WebImage onPickImage(MediaMetadata mediaMetadata, @NonNull ImageHints imageHints) {
            if ((mediaMetadata == null) || !mediaMetadata.hasImages()) {
                return null;
            }
            List<WebImage> images = mediaMetadata.getImages();
            if (images.size() == 1) {
                return images.get(0);
            }
            else {
                int type=imageHints.getType();
                if (type == ImagePicker.IMAGE_TYPE_MEDIA_ROUTE_CONTROLLER_DIALOG_BACKGROUND) {
                    return images.get(0);
                }
                else {
                    return images.get(1);
                }
            }
        }
    }
}
