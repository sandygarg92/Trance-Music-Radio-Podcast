package com.onlineradiofm.trancemusicradio.itunes.model.rss;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-01-18.
 */
@Root(name = "rss", strict = false)
public class RssFeedModel {

    @Element(name = "channel", required = false)
    private RssChannelModel channel;

    public RssFeedModel() {

    }

    public RssChannelModel getChannel() {
        return channel;
    }

    public void setChannel(RssChannelModel channel) {
        this.channel = channel;
    }
}
