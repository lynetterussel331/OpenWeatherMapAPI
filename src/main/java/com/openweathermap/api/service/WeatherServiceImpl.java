package com.openweathermap.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openweathermap.api.model.WeatherDetails;
import com.openweathermap.api.repository.OWMRepository;
import com.openweathermap.api.model.FormatResponse;
import com.openweathermap.api.model.Weather;
import com.openweathermap.api.util.Constants;

@Service("/owmService")
public class WeatherServiceImpl implements WeatherService {
	
	private static final Logger Log = LoggerFactory.getLogger(WeatherServiceImpl.class);
	
    private final OWMAddress owmAddress;
    private final OWMResponse owmResponse;
    private final FormatResponse formatResponse;
    
    @Autowired
    OWMRepository owmRepository;
    	
    public WeatherServiceImpl() {
        this.owmAddress = new OWMAddress();
        this.owmResponse = new OWMResponse();
        this.formatResponse = new FormatResponse();
    }
    
	public JSONObject getWeatherDetailsByCityName(String cityName, String appId){
		String response = owmResponse.httpGETResponseFromOWM(
				owmAddress.getOwmAddressUrl(Constants.URL_WEATHER, Constants.PARAM_CITY_NAME, cityName, appId));
		
		WeatherDetails weatherDetails = new WeatherDetails(new JSONObject(response));
		
		return saveAndRetrieveResponse(weatherDetails);
	}
	
	public JSONArray getWeatherDetailsByCityId(String city_Id, String appId){
		String response = owmResponse.httpGETResponseFromOWM(
				owmAddress.getOwmAddressUrl(Constants.URL_GROUP, Constants.PARAM_CITY_ID, city_Id, appId));
		
		JSONArray jsonArrayResp = new JSONArray();
		JSONArray currentWeatherArray = new JSONObject(response).optJSONArray(Constants.JSON_LIST);
		
		if (currentWeatherArray != null && currentWeatherArray != Collections.emptyList()) {
			for(int i = 0; i < currentWeatherArray.length(); i++) {
				
				JSONObject weatherObj = currentWeatherArray.optJSONObject(i);
				WeatherDetails weatherDetails = new WeatherDetails(weatherObj);
				
				jsonArrayResp.put(saveAndRetrieveResponse(weatherDetails));
			}
		}
		
		return jsonArrayResp;
	}
	
	private JSONObject saveAndRetrieveResponse(WeatherDetails weatherDetails) {
		
		Weather weatherObj = new Weather();
		
		weatherObj.setCityId(weatherDetails.getCityId());
		weatherObj.setCityName(weatherDetails.getCityName());
		weatherObj.setWeather(formatResponse.formatWeatherListToString(weatherDetails));
		weatherObj.setTemp(weatherDetails.getMainInstance().getTemp());
		
		owmRepository.save(weatherObj);
		
		return formatResponse.formatJSONResponse(weatherObj);
	}
	
	
	private class OWMAddress {
		
		private String getOwmAddressUrl(String type, String param, String cityId, String appId) {
			String owmAddressUrl = new StringBuilder()
					.append(Constants.URL_API).append(type).append(param).append(cityId)
					.append(Constants.PARAM_APP_ID).append(appId).toString();
			Log.info(owmAddressUrl);
			return owmAddressUrl;
		}
		
	}
	
	private class OWMResponse {
		
	    private String httpGETResponseFromOWM(String requestAddress) {

			URL urlRequest;
			HttpURLConnection connection = null;
			BufferedReader reader = null;
			
			String tmpStr;
			String response = null;
			
			try {
				urlRequest = new URL(requestAddress);
				
				connection = (HttpURLConnection) urlRequest.openConnection();
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(false);
				connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
				connection.connect();
				
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					
					String encoding = connection.getContentEncoding();
					
					try {
						if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())));
						} else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
							reader = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream(), new Inflater(true))));
						} else {
							reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						}
						
						while ((tmpStr = reader.readLine()) != null) {
							response = tmpStr;
						}
					}  catch (IOException e) {
						Log.error(e.getMessage());
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException e) {
								Log.error(e.getMessage());
							}
						}
					}
							
				} else {
                    try {
                        reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        while ((tmpStr = reader.readLine()) != null) {
                            response = tmpStr;
                        }
                    } catch (IOException e) {
                    	Log.error(e.getMessage());
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                            	Log.error(e.getMessage());
                            }
                        }
                    }
				}

			} catch (IOException e) {
				Log.error(e.getMessage());
				response = null;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
			
			return response;
		}
	}

}
