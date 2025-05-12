package com.onlineradiofm.trancemusicradio.model;

import com.google.gson.annotations.SerializedName;

public class CountriesResponseItem{

	@SerializedName("stationcount")
	private int stationcount;

	@SerializedName("name")
	private String name;

	public CountriesResponseItem(String name) {
		this.name = name;
	}

	public CountriesResponseItem() {

	}

	public void setStationcount(int stationcount){
		this.stationcount = stationcount;
	}

	public int getStationcount(){
		return stationcount;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}