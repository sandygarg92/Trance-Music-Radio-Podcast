package com.onlineradiofm.trancemusicradio.itunes.model.rss;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-01-18.
 */
@Root(name = "image", strict = false)
public class RssItemImageModel {

    @Attribute(name = "href", required = false)
    private String href;

    public RssItemImageModel() {

    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
