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
@Root(name = "enclosure", strict = false)
public class RssEnclosureModel {

    @Attribute(name = "url", required = false)
    private String url;

    @Attribute(name = "type", required = false)
    private String type;

    @Attribute(name = "length", required = false)
    private String length;

    public RssEnclosureModel() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
