package com.openweathermap.api.model;

import java.io.Serializable;

import org.json.JSONObject;

import com.openweathermap.api.util.Constants;

abstract class AbstractResponse implements Serializable {
	
	/**
	 * Assigns default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * Instance variables
	 */
	private final int responseCode;
	private final String rawResponse;
	
	/*
	 * Constructors
	 */
	AbstractResponse(){
		this.responseCode = Integer.MIN_VALUE;
		this.rawResponse = null;
	}
	
	AbstractResponse(JSONObject jsonObj) {
		this.responseCode = (jsonObj != null) ? jsonObj.optInt(Constants.JSON_RESPONSE_CODE, Integer.MIN_VALUE) : Integer.MIN_VALUE;
		this.rawResponse = (jsonObj != null) ? jsonObj.toString() : null;
	}
	
	/**
	 * @return true if response code is valid (downloaded and parsed correctly), otherwise false
	 */
	public boolean isValid() {
		return this.responseCode == 200;
	}
	
	/**
	 * @return true if response code is available, otherwise false
	 */
	public boolean hasResponseCode() {
		return this.responseCode != Integer.MIN_VALUE;
	}
	
	/**
	 * @return true if raw response is available, otherwise false
	 */
	public boolean hasRawResponse() {
		return !this.rawResponse.isEmpty();
	}

	/**
	 * @return Response code if available, otherwise return Integer.MIN_VALUE
	 */
	public int getResponseCode() {
		return responseCode;
	}
	
	/**
	 * @return Raw response if available, otherwise return null
	 */
	public String getRawResponse() {
		return rawResponse;
	}
		
}
