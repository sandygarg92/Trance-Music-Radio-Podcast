package com.onlineradiofm.trancemusicradio.itunes.constants;


public interface IITunesConstants {

    String FORMAT_URL_ITUNES_SEARCH = "https://itunes.apple.com/search?";
    String FORMAT_URL_ITUNES_LOOKUP = "https://itunes.apple.com/lookup?";
    String FORMAT_URL_ITUNES_TOP_CHART = "https://rss.itunes.apple.com/api/v1/%1$s/%2$s/%3$s/all/%4$s/explicit.json";

    String PARAM_TERM  = "term=";
    String PARAM_ID = "id=";

    String PARAM_MEDIA = "&media=";
    String PARAM_ENTITY = "&entity=";
    String PARAM_LIMIT = "&limit=";
    String PARAM_COUNTRY = "&country=";

    String ITUNES_MEDIA_TYPE_PODCAST = "podcast";
    String ITUNES_ENTITY_PODCAST = "podcast";

    String ITUNES_RSS_MEDIA_TYPE_PODCAST = "podcasts";
    String ITUNES_RSS_FEED_TYPE_TOP_PODCAST = "top-podcasts";


}
