package com.openweathermap.api.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface WeatherService {

	JSONObject getWeatherDetailsByCityName(String cityName, String appId);
	JSONArray getWeatherDetailsByCityId(String cityId, String appId);
	
}
