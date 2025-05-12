package com.onlineradiofm.trancemusicradio.itunes.model.rss;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.ArrayList;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: http://radiopolska.com
 * Created by radiopolska on 2020-01-18.
 */
@Root(name = "channel", strict = false)
public class RssChannelModel {

    @Element(name = "title", required = false)
    private String title;

    @Path("link")
    @Text(required = false)
    private String link;

    @Path("description")
    @Text(required = false)
    private String description;

    @Path("author")
    @Text(required = false)
    private String iTunesAuthor;

    @Element(name = "language", required = false)
    private String language;

    @ElementList(inline = true, name = "category", required = false)
    private ArrayList<RssCategoryModel> categoryModels;

    @ElementList(inline = true, name = "image", required = false)
    private ArrayList<RssArtworkModel> artworkModels;

    @ElementList(inline = true, name = "item", required = false)
    private ArrayList<RssItemModel> itemModels;

    public RssChannelModel() {

    }

    public String getTitle() {
        return title!=null?title.trim():null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getITunesAuthor() {
        return iTunesAuthor !=null ? iTunesAuthor.trim():null;
    }

    public void setITunesAuthor(String iTunesAuthor) {
        this.iTunesAuthor = iTunesAuthor;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ArrayList<RssCategoryModel> getCategoryModels() {
        return categoryModels;
    }

    public void setCategoryModels(ArrayList<RssCategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
    }

    public ArrayList<RssArtworkModel> getArtworkModels() {
        return artworkModels;
    }

    public String getImage(){
        return artworkModels!=null && artworkModels.size()>0 ? artworkModels.get(0).getImage():null;
    }

    public void setArtworkModels(ArrayList<RssArtworkModel> artworkModels) {
        this.artworkModels = artworkModels;
    }

    public ArrayList<RssItemModel> getItemModels() {
        return itemModels;
    }

    public void setItemModels(ArrayList<RssItemModel> itemModels) {
        this.itemModels = itemModels;
    }

    public String getLink() {
        return link;
    }

}
