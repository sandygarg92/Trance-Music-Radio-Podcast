package com.onlineradiofm.trancemusicradio.itunes.model.rss;

import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.model.RadioModel;

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
@Root(name = "item", strict = false)
public class RssItemModel {

    @Path("title")
    @Text(required = false)
    private String title;

    @Path("description")
    @Text(required = false)
    private String description;

    @Element(name = "summary", required = false)
    private String iTunesSummary;

    @Element(name = "pubDate", required = false)
    private String pubDate;

    @Element(name = "duration", required = false)
    private String duration;

    @ElementList(inline = true, name = "enclosure", required = false)
    private ArrayList<RssEnclosureModel> rssEnclosureModels;

    @ElementList(inline = true, name = "image", required = false)
    private ArrayList<RssItemImageModel> rssItemImageModels;

    public RssItemModel() {

    }

    public String getTitle() {
        return title;
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

    public String getITunesSummary() {
        return iTunesSummary;
    }

    public void setITunesSummary(String iTunesSummary) {
        this.iTunesSummary = iTunesSummary;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public ArrayList<RssEnclosureModel> getRssEnclosureModels() {
        return rssEnclosureModels;
    }

    public void setRssEnclosureModels(ArrayList<RssEnclosureModel> rssEnclosureModels) {
        this.rssEnclosureModels = rssEnclosureModels;
    }


    public RadioModel convertToRadioModel(String channel, String author, String channelImg) {
        try {
            if (rssEnclosureModels != null && rssEnclosureModels.size() > 0) {
                String link = rssEnclosureModels.get(0).getUrl();
                if (!TextUtils.isEmpty(link) && link.startsWith("http")) {
//                    String img = rssItemImageModels != null && rssItemImageModels.size()>0 ? rssItemImageModels.get(0).getHref(): null;
//                    if(TextUtils.isEmpty(img)){
//                        img = channelImg;
//                    }
                    RadioModel model = new RadioModel(link.hashCode(), title != null ? title.trim() : null, channelImg);
                    model.setCalculateCount(true);
                    model.setIsPodCast(true);
                    model.setSourceRadio("Other");
                    model.setLinkRadio(link);
                    model.setArtist(author);
                    model.setTags(channel);
                    return model;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
