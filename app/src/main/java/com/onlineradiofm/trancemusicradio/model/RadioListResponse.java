package com.onlineradiofm.trancemusicradio.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RadioListResponse{

	@SerializedName("RadioListResponse")
	private List<RadioListResponseItem> radioListResponse;

	public void setRadioListResponse(List<RadioListResponseItem> radioListResponse){
		this.radioListResponse = radioListResponse;
	}

	public List<RadioListResponseItem> getRadioListResponse(){
		return radioListResponse;
	}
}