package com.onlineradiofm.trancemusicradio.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CountriesResponse{

	@SerializedName("CountriesResponse")
	private List<CountriesResponseItem> countriesResponse;

	public void setCountriesResponse(List<CountriesResponseItem> countriesResponse){
		this.countriesResponse = countriesResponse;
	}

	public List<CountriesResponseItem> getCountriesResponse(){
		return countriesResponse;
	}
}