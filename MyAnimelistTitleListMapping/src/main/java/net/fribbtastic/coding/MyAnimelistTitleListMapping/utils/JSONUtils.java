/**
 * 
 */
package net.fribbtastic.coding.MyAnimelistTitleListMapping.utils;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
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
public class JSONUtils {
	private static Logger logger = Logger.getLogger(JSONUtils.class);

	/**
	 * load a file that contains a JSON Array
	 * 
	 * @param path - the path to the file that should be read
	 * @return a JSONArray either filled with the files content, empty if the file doesn't exist or null on error 
	 */
	public static JSONArray loadFile(String path) {
		logger.debug("Reading file at location: " + path);
			
		String content = FileUtils.readFile(path);
		
		if (content != null) {
			if (content.length() > 0) {
				return new JSONArray(content);
			}	
		}
		return new JSONArray();
	}

	/**
	 * writes the JSONArray to the file 
	 * 
	 * @param json - the JSONArray
	 * @param path - the path to the file
	 */
	public static void writeFile(JSONArray json, String path) {
		logger.debug("Writing file at location: " + path);
		
		FileUtils.writeFile(json.toString(), path);
	}

	/**
	 * get the response in JSON format
	 * 
	 * @param id - the ID of the entry
	 * @return the response as JSON Object
	 * @throws MalformedURLException 
	 * @throws JSONException 
	 */
	public static JSONObject getJSONResponse(Integer id) {
		logger.debug("getting the JSON Response");

		Map<String, String> data = new HashMap<String, String>();
		data.put("id", id.toString());

		StrSubstitutor substitutor = new StrSubstitutor(data);

		String urlString = substitutor.replace(Constants.malURL);
		
		JSONObject result = null;
		
		try {
			String response = HTTPUtils.getResponse(urlString, null, null);
			
			if (response != null) {
				if (Utils.tryParseInt(response)) {
					result = new JSONObject();
					result.put("error", "Error " + Integer.parseInt(response));
				} else {
					result = new JSONObject(response);
				}
			} else {
				result = new JSONObject();
				result.put("error", "Error");
			}
			
		} catch (JSONException e) {
			logger.error("There was an Error while parsing the Response to a JSONObject", e);
		}
		
		return result;
	}

	/**
	 * return the entry that matches with the current ID
	 * 
	 * @param currentID - the current ID
	 * @param array - JSONArray witht the existing entries
	 * @return the JSONObject that was found witht his ID or null if not
	 */
	public static JSONObject getExistingEntry(String idKey, Integer currentID, JSONArray array) {
		logger.debug("searching for existing entry");
		
		for (Object obj : array) {
			
			JSONObject entry = (JSONObject) obj;
			
			if (entry.has(idKey)) {
				if (entry.getInt(idKey) == currentID) {
					return entry;
				}
			} 
		}
		
		return null;
	}

	/**
	 * Update the original JSONObject with Values of the newJSON JSONObject
	 * 
	 * @param original - the original JSONObject that should be updated
	 * @param newJSON - the JSONObject that will update the original
	 */
	public static void updateEntry(JSONObject original, JSONObject newJSON) {
		
		Iterator<?> keysIterator = newJSON.keys();
		
		while (keysIterator.hasNext()) {
			String key = (String) keysIterator.next();
			
			original.put(key, newJSON.get(key));			
		}
	}
}