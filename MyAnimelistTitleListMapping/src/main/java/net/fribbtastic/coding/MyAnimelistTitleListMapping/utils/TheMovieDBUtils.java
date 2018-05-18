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
public class TheMovieDBUtils {
	private static Logger logger = Logger.getLogger(TheMovieDBUtils.class);
	
	/**
	 * the API URL for TheMovieDB.org
	 */
//	private static String API_URL = "http://api.tmdb.org/3/search/movie?api_key=${api_key}&query=${title}&year=&language=${language}&include_adult=true";
	private static String API_SCHEME = "https";
	private static String API_HOST = "api.tmdb.org";
	private static String API_PATH = "/3/search/movie";
	private static String API_QUERY = "api_key=${api_key}&query=${title}&year=&language=${language}&include_adult=true";

	/**
	 * @param actualTitles
	 * @return
	 */
	public static Integer getID(String title, String language) {
		logger.debug("Searching on TheMovieDB");
		
		try {
			String apiKey = PropertyUtils.getPropertyValue(PropertyUtils.THEMOVIEDBKEY);
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("title", title);
			data.put("api_key",apiKey);
			data.put("language", language);
			
			String query = StrSubstitutor.replace(API_QUERY, data);
			URI uri = new URI(API_SCHEME, null, API_HOST, -1, API_PATH, query, null);
			
			String response = HTTPUtils.getResponse(uri.toASCIIString(), null, null);
			
			JSONObject search = new JSONObject(response);
			if (search.has("results")) {
				JSONArray results = search.getJSONArray("results");
				
				if (results.length() > 0) {
					JSONObject entry = results.getJSONObject(0);
					
					return entry.getInt("id");
				} else {
					logger.debug("no results found");
				}
			} else {
				logger.debug("no results found");
			}
			
		} catch (URISyntaxException e) {
			logger.error("URI Syntax is wrong", e);
		} catch (JSONException e) {
			logger.error("Response could not be parsed to JSONObject", e);
		}
		
		return null;
	}
}