package com.onlineradiofm.trancemusicradio.itunes.model.rss;

import android.text.TextUtils;

import org.simpleframework.xml.Attribute;
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
@Root(name = "image", strict = false)
public class RssArtworkModel {

    @Attribute(name = "href", required = false)
    private String href;

    @Element(name = "url", required = false)
    private String url;

    public RssArtworkModel() {

    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage(){
        if(!TextUtils.isEmpty(url) && url.startsWith("http")){
            return url;
        }
        return href;
    }
}
