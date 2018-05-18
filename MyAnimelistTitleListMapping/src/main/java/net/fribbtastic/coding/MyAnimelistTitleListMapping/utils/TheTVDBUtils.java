/**
 * 
 */
package net.fribbtastic.coding.MyAnimelistTitleListMapping.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Fribb
 *
 */
public class TheTVDBUtils {
	private static Logger logger = Logger.getLogger(TheTVDBUtils.class);
	
	/**
	 * the authentication URL to request a token
	 */
	private static String AUTH_URL = "https://api.thetvdb.com/login";
	
	/**
	 * the authentication string to request a token (POST)
	 */
	private static String AUTH_STRING = "{\"apikey\":\"${apy_key}\"}";
	
	/**
	 * the API URL of TheTVDB.com
	 */
//	private static String API_URL = "https://api.thetvdb.com/search/series?name=${title}";
	private static String API_SCHEME = "https";
	private static String API_HOST = "api.thetvdb.com";
	private static String API_PATH = "/search/series";
	private static String API_QUERY = "name=${title}";
	

	/**
	 * @param title - string that contains the title
	 * @return the id found on TheTVDB or null
	 */
	public static Integer getID(String title, String language) {
		logger.debug("Searching on TheTVDB");
		
		try {
			Map<String, String> data = new HashMap<String, String>();
			data.put("title", title);
			String query = StrSubstitutor.replace(API_QUERY, data);
			URI uri = new URI(API_SCHEME, null, API_HOST, -1, API_PATH, query, null);
			
			String response = HTTPUtils.getResponse(uri.toASCIIString(), Constants.TVDBToken, language);
			
			if (response != null) {
				JSONObject responseObj = new JSONObject(response);
				
				if (responseObj.has("data")) {
					JSONArray tvdbdata = new JSONObject(response).getJSONArray("data");
					
					JSONObject entry = tvdbdata.getJSONObject(0);
					
					if (entry.has("id")) {
						return entry.getInt("id");
					}
				}
			} 
		} catch (URISyntaxException e) {
			logger.error("URI Syntax is wrong", e);
		} catch (JSONException e) {
			logger.error("Response could not be parsed to JSONObject", e);
		}
		
		return null;
	}

	/**
	 * Request the Token from the login URL
	 * 
	 */
	public static void requestToken() {
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("apy_key", PropertyUtils.getPropertyValue(PropertyUtils.THETVDBKEY));
		
		String message = StrSubstitutor.replace(AUTH_STRING, data);
		
		if (Constants.TVDBToken == null) {
			String tokenStr = HTTPUtils.postReponse(AUTH_URL, message);
			
			JSONObject token = new JSONObject(tokenStr);
			
			if (token.has("token")) {
				Constants.TVDBToken = token.getString("token");
			} else if (token.has("Error")) {
				logger.error("Token request returned error: " + token.getString("Error"));
			}
		}
	}
}