package net.fribbtastic.coding.MyAnimelistTitleListMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import net.fribbtastic.coding.MyAnimelistTitleListMapping.utils.Constants;
import net.fribbtastic.coding.MyAnimelistTitleListMapping.utils.JSONUtils;
import net.fribbtastic.coding.MyAnimelistTitleListMapping.utils.MyAnimeListUtils;
import net.fribbtastic.coding.MyAnimelistTitleListMapping.utils.PropertyUtils;
import net.fribbtastic.coding.MyAnimelistTitleListMapping.utils.TheMovieDBUtils;
import net.fribbtastic.coding.MyAnimelistTitleListMapping.utils.TheTVDBUtils;
import net.fribbtastic.coding.MyAnimelistTitleListMapping.utils.Utils;

/**
 * 
 * @author Fribb
 *
 */
public class App {
	private static Logger logger = Logger.getLogger(App.class);
	
	/**
	 * Run the program and get the list of titles from the API for the current iteration as ID,
	 * use this list to get the ID for the mapping between MyAnimeList and TheTVDB.com and TheMovieDB.org 
	 */
	public void run() {
		logger.info("starting fetching titlelist and mapping");
		
		JSONObject actualTitles = null;
		
		if (PropertyUtils.getPropertyValue(PropertyUtils.TITLELIST).equalsIgnoreCase("true")) {
			
			// load existing JSONObject for this ID
			JSONObject existingTitles = JSONUtils.getExistingEntry("id", Constants.currentID, Constants.animeTitles);
			
			// get the response from the MAL API for the current ID
			JSONObject jsonResponse = JSONUtils.getJSONResponse(Constants.currentID);
			
			// extract all available titles (or error message) from the response
			JSONObject titles = MyAnimeListUtils.getTitles(jsonResponse);
			
			// update existing entry
			if (existingTitles != null) {
				JSONUtils.updateEntry(existingTitles, titles);
				actualTitles = existingTitles;
				logger.info("titles added");
			} else {
				Constants.animeTitles.put(titles);
				actualTitles = titles;
				logger.info("titles added");
			}
		}else {
			// load existing JSONObject for this ID
			JSONObject existingTitles = JSONUtils.getExistingEntry("id", Constants.currentID, Constants.animeTitles);
			// update existing entry
			
			if (existingTitles != null) {
				actualTitles = existingTitles;
				logger.info("titles added");
			}
		}
		
		if (PropertyUtils.getPropertyValue(PropertyUtils.MAPPING).equalsIgnoreCase("true")) {
			
			// load existing JSONObject for this ID
			JSONObject existingMapping = JSONUtils.getExistingEntry("mal_id", Constants.currentID, Constants.animeMapping);
			
			JSONObject mapping = new JSONObject(); 
			
			// add the MyAnimeList ID
			mapping.put("mal_id", actualTitles.getInt("id"));
			
			// request the id from either TheTVDB or TheMovieDB when the type is either TV or OVA or Movie
			if (actualTitles.has("type")) {
				String type = actualTitles.getString("type");
				Integer lookup = 0; // which lookup was used 0 = nothing, 1 = theTVDB, 2 = theMovieDB
				
				// get different titles
				String titleMain = null;
				String titleEnglish = null;
				String titleJapanese = null;
				String titleSynonym = null;
				
				if (actualTitles.has("title")) {
					titleMain = actualTitles.getString("title");
				}
				
				if (actualTitles.has("english")) {
					titleEnglish = actualTitles.getJSONArray("english").getString(0);
				}
				
				if (actualTitles.has("synonyms")) {
					titleSynonym = actualTitles.getJSONArray("synonyms").getString(0);
				}
				
				if (actualTitles.has("japanese")) {
					titleJapanese = actualTitles.getJSONArray("japanese").getString(0);
				}
				
				// get the different IDs for the different titles 
				Integer IDMain = null;
				Integer IDEnglish = null;
				Integer IDJapanese = null;
				Integer IDSynonym = null;
				
				if (type.equalsIgnoreCase("TV") || type.equalsIgnoreCase("OVA") || type.equalsIgnoreCase("Special")) {
					// request the ids from TheTVDB.com
					if (titleMain != null) {
						IDMain = TheTVDBUtils.getID(titleMain, "en");
					}
					
					if (titleEnglish != null) {
						IDEnglish = TheTVDBUtils.getID(titleEnglish, "en");
					}
					
					if (titleJapanese != null) {
						IDJapanese = TheTVDBUtils.getID(titleJapanese, "ja");
					}
					
					if (titleSynonym != null) {
						IDSynonym = TheTVDBUtils.getID(titleSynonym, "en");
					}
					
					lookup = 1;
					
				} else if (type.equalsIgnoreCase("Movie")) {
					// request the ids from TheMovieDB.org
					if (titleMain != null) {
						IDMain = TheMovieDBUtils.getID(titleMain, "en");
					}
					
					if (titleEnglish != null) {
						IDEnglish = TheMovieDBUtils.getID(titleEnglish, "en");
					}
					
					if (titleJapanese != null) {
						IDJapanese = TheMovieDBUtils.getID(titleJapanese, "ja");
					}
					
					if (titleSynonym != null) {
						IDSynonym = TheMovieDBUtils.getID(titleSynonym, "en");
					}
					
					lookup = 2;
				}
				
				// check ID variables against each other and select the one that is used the most
				List<Integer> titleIdList = new ArrayList<Integer>();
				if (IDMain != null) {
					titleIdList.add(IDMain);
				}
				
				if (IDEnglish != null) {
					titleIdList.add(IDEnglish);
				}
				
				if (IDJapanese != null) {
					titleIdList.add(IDJapanese);
				}
				
				if (IDSynonym != null) {
					titleIdList.add(IDSynonym);
				}
				
				// Sort the list
				Collections.sort(titleIdList);
				
				// get the most occurring element
				Integer mostOccurringID = Utils.getMostOccurringElement(titleIdList);
				
				if (lookup == 1) {
					if (mostOccurringID == null) {
						mapping.put("thetvdb_id", -1);
					} else {
						mapping.put("thetvdb_id", mostOccurringID);
					}
				} else if (lookup == 2) {
					if (mostOccurringID == null) {
						mapping.put("themoviedb_id", -1);
					} else {
						mapping.put("themoviedb_id", mostOccurringID);
					}
				}
			} else {
				Constants.currentID++;
				Utils.writeFiles();
				return;
			}
			
			// update the existing mapping
			if (existingMapping != null) {
				JSONUtils.updateEntry(existingMapping, mapping);
				logger.info("mapping added");
			} else {
				Constants.animeMapping.put(mapping);
				logger.info("mapping added");
			}
			
		}
		Constants.currentID++;
		
		Utils.writeFiles();
	}
}