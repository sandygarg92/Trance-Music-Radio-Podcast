package com.onlineradiofm.trancemusicradio.model;

import com.google.gson.annotations.SerializedName;

public class RadioListResponseItem{

	@SerializedName("lastlocalchecktime_iso8601")
	private String lastlocalchecktimeIso8601;

	@SerializedName("country")
	private String country;

	@SerializedName("lastlocalchecktime")
	private String lastlocalchecktime;

	@SerializedName("serveruuid")
	private String serveruuid;

	@SerializedName("countrycode")
	private String countrycode;

	@SerializedName("iso_3166_2")
	private Object iso31662;

	@SerializedName("lastchecktime_iso8601")
	private String lastchecktimeIso8601;

	@SerializedName("language")
	private String language;

	@SerializedName("bitrate")
	private int bitrate;

	@SerializedName("clicktrend")
	private int clicktrend;

	@SerializedName("lastcheckoktime")
	private String lastcheckoktime;

	@SerializedName("lastchecktime")
	private String lastchecktime;

	@SerializedName("changeuuid")
	private String changeuuid;

	@SerializedName("url_resolved")
	private String urlResolved;

	@SerializedName("lastcheckok")
	private int lastcheckok;

	@SerializedName("state")
	private String state;

	@SerializedName("clicktimestamp")
	private String clicktimestamp;

	@SerializedName("clickcount")
	private int clickcount;

	@SerializedName("favicon")
	private String favicon;

	@SerializedName("lastchangetime_iso8601")
	private String lastchangetimeIso8601;

	@SerializedName("languagecodes")
	private String languagecodes;

	@SerializedName("url")
	private String url;

	@SerializedName("hls")
	private int hls;

	@SerializedName("tags")
	private String tags;

	@SerializedName("geo_long")
	private Object geoLong;

	@SerializedName("codec")
	private String codec;

	@SerializedName("ssl_error")
	private int sslError;

	@SerializedName("lastchangetime")
	private String lastchangetime;

	@SerializedName("clicktimestamp_iso8601")
	private String clicktimestampIso8601;

	@SerializedName("geo_lat")
	private Object geoLat;

	@SerializedName("has_extended_info")
	private boolean hasExtendedInfo;

	@SerializedName("stationuuid")
	private String stationuuid;

	@SerializedName("name")
	private String name;

	@SerializedName("votes")
	private int votes;

	@SerializedName("homepage")
	private String homepage;

	@SerializedName("lastcheckoktime_iso8601")
	private String lastcheckoktimeIso8601;

	public void setLastlocalchecktimeIso8601(String lastlocalchecktimeIso8601){
		this.lastlocalchecktimeIso8601 = lastlocalchecktimeIso8601;
	}

	public String getLastlocalchecktimeIso8601(){
		return lastlocalchecktimeIso8601;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setLastlocalchecktime(String lastlocalchecktime){
		this.lastlocalchecktime = lastlocalchecktime;
	}

	public String getLastlocalchecktime(){
		return lastlocalchecktime;
	}

	public void setServeruuid(String serveruuid){
		this.serveruuid = serveruuid;
	}

	public String getServeruuid(){
		return serveruuid;
	}

	public void setCountrycode(String countrycode){
		this.countrycode = countrycode;
	}

	public String getCountrycode(){
		return countrycode;
	}

	public void setIso31662(Object iso31662){
		this.iso31662 = iso31662;
	}

	public Object getIso31662(){
		return iso31662;
	}

	public void setLastchecktimeIso8601(String lastchecktimeIso8601){
		this.lastchecktimeIso8601 = lastchecktimeIso8601;
	}

	public String getLastchecktimeIso8601(){
		return lastchecktimeIso8601;
	}

	public void setLanguage(String language){
		this.language = language;
	}

	public String getLanguage(){
		return language;
	}

	public void setBitrate(int bitrate){
		this.bitrate = bitrate;
	}

	public int getBitrate(){
		return bitrate;
	}

	public void setClicktrend(int clicktrend){
		this.clicktrend = clicktrend;
	}

	public int getClicktrend(){
		return clicktrend;
	}

	public void setLastcheckoktime(String lastcheckoktime){
		this.lastcheckoktime = lastcheckoktime;
	}

	public String getLastcheckoktime(){
		return lastcheckoktime;
	}

	public void setLastchecktime(String lastchecktime){
		this.lastchecktime = lastchecktime;
	}

	public String getLastchecktime(){
		return lastchecktime;
	}

	public void setChangeuuid(String changeuuid){
		this.changeuuid = changeuuid;
	}

	public String getChangeuuid(){
		return changeuuid;
	}

	public void setUrlResolved(String urlResolved){
		this.urlResolved = urlResolved;
	}

	public String getUrlResolved(){
		return urlResolved;
	}

	public void setLastcheckok(int lastcheckok){
		this.lastcheckok = lastcheckok;
	}

	public int getLastcheckok(){
		return lastcheckok;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setClicktimestamp(String clicktimestamp){
		this.clicktimestamp = clicktimestamp;
	}

	public String getClicktimestamp(){
		return clicktimestamp;
	}

	public void setClickcount(int clickcount){
		this.clickcount = clickcount;
	}

	public int getClickcount(){
		return clickcount;
	}

	public void setFavicon(String favicon){
		this.favicon = favicon;
	}

	public String getFavicon(){
		return favicon;
	}

	public void setLastchangetimeIso8601(String lastchangetimeIso8601){
		this.lastchangetimeIso8601 = lastchangetimeIso8601;
	}

	public String getLastchangetimeIso8601(){
		return lastchangetimeIso8601;
	}

	public void setLanguagecodes(String languagecodes){
		this.languagecodes = languagecodes;
	}

	public String getLanguagecodes(){
		return languagecodes;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	public void setHls(int hls){
		this.hls = hls;
	}

	public int getHls(){
		return hls;
	}

	public void setTags(String tags){
		this.tags = tags;
	}

	public String getTags(){
		return tags;
	}

	public void setGeoLong(Object geoLong){
		this.geoLong = geoLong;
	}

	public Object getGeoLong(){
		return geoLong;
	}

	public void setCodec(String codec){
		this.codec = codec;
	}

	public String getCodec(){
		return codec;
	}

	public void setSslError(int sslError){
		this.sslError = sslError;
	}

	public int getSslError(){
		return sslError;
	}

	public void setLastchangetime(String lastchangetime){
		this.lastchangetime = lastchangetime;
	}

	public String getLastchangetime(){
		return lastchangetime;
	}

	public void setClicktimestampIso8601(String clicktimestampIso8601){
		this.clicktimestampIso8601 = clicktimestampIso8601;
	}

	public String getClicktimestampIso8601(){
		return clicktimestampIso8601;
	}

	public void setGeoLat(Object geoLat){
		this.geoLat = geoLat;
	}

	public Object getGeoLat(){
		return geoLat;
	}

	public void setHasExtendedInfo(boolean hasExtendedInfo){
		this.hasExtendedInfo = hasExtendedInfo;
	}

	public boolean isHasExtendedInfo(){
		return hasExtendedInfo;
	}

	public void setStationuuid(String stationuuid){
		this.stationuuid = stationuuid;
	}

	public String getStationuuid(){
		return stationuuid;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setVotes(int votes){
		this.votes = votes;
	}

	public int getVotes(){
		return votes;
	}

	public void setHomepage(String homepage){
		this.homepage = homepage;
	}

	public String getHomepage(){
		return homepage;
	}

	public void setLastcheckoktimeIso8601(String lastcheckoktimeIso8601){
		this.lastcheckoktimeIso8601 = lastcheckoktimeIso8601;
	}

	public String getLastcheckoktimeIso8601(){
		return lastcheckoktimeIso8601;
	}
}