package com.openweathermap.api.model;

import org.json.JSONObject;

import com.openweathermap.api.util.Constants;

public class WeatherDetails extends AbstractWeather {
	
	private static final long serialVersionUID = 1L;
	
	private final long cityId;
	private final String cityName;
	private final Main main;
	
	public WeatherDetails(JSONObject jsonObj) {
		super(jsonObj);
		
		this.cityId = jsonObj.optLong(Constants.JSON_ID, Long.MIN_VALUE);
		this.cityName = jsonObj.optString(Constants.JSON_NAME, null);
		
		JSONObject mainObj = (jsonObj != null) ? jsonObj.optJSONObject(Constants.JSON_MAIN) : null;
		this.main = (jsonObj != null) ? new Main(mainObj) : new Main();
	}

	public long getCityId() {
		return this.cityId;
	}

	public String getCityName() {
		return this.cityName;
	}
	
	public Main getMainInstance() {
		return this.main;
	}
	
	public static class Main extends AbstractWeather.Main {
		
		private static final long serialVersionUID = 1L;

		Main(){
			super();
		}
		
		Main(JSONObject jsonObj) {
			super(jsonObj);
		}
		
	}
	
}
