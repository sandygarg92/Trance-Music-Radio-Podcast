package com.onlineradiofm.trancemusicradio.ypylibs.googlecast.queue;

import com.google.android.gms.cast.MediaQueueItem;

import java.util.List;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 11/14/18.
 */
public class YPYQueueUtils {

    public static MediaQueueItem[] rebuildQueue(List<MediaQueueItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        MediaQueueItem[] rebuiltQueue = new MediaQueueItem[items.size()];
        for (int i = 0; i < items.size(); i++) {
            rebuiltQueue[i] = rebuildQueueItem(items.get(i));
        }

        return rebuiltQueue;
    }

    public static MediaQueueItem[] rebuildQueueAndAppend(List<MediaQueueItem> items,
                                                         MediaQueueItem currentItem) {
        if (items == null || items.isEmpty()) {
            return new MediaQueueItem[]{currentItem};
        }
        MediaQueueItem[] rebuiltQueue = new MediaQueueItem[items.size() + 1];
        for (int i = 0; i < items.size(); i++) {
            rebuiltQueue[i] = rebuildQueueItem(items.get(i));
        }
        rebuiltQueue[items.size()] = currentItem;

        return rebuiltQueue;
    }

    public static MediaQueueItem rebuildQueueItem(MediaQueueItem item) {
        return new MediaQueueItem.Builder(item).clearItemId().build();
    }
}
