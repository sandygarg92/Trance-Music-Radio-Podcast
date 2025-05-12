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
@Root(name = "category", strict = false)
public class RssCategoryModel {
    @Attribute(name = "text", required = false)
    private String text;

    public RssCategoryModel() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
