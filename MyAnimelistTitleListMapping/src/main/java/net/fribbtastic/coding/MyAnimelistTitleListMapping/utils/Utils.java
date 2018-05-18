/**
 * 
 */
package net.fribbtastic.coding.MyAnimelistTitleListMapping.utils;

import java.io.File;
import java.util.List;

/**
 * @author Fribb
 *
 */
public class Utils {
	
	/**
	 * create the Strings used for the Filenames with the correct path in which they will be read and saved
	 * 
	 * @param start - the number at which the program will start
	 * @param end - the number at which the program will end
	 */
	public static void buildFileNames(Integer start, Integer end) {
		
		String path = PropertyUtils.getPropertyValue(PropertyUtils.PATH);
		
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		
		// build the directories the files should be saved in
		String mappingDir = path + Constants.mappingFolder + File.separator;
		String titlesDir = path + Constants.titlesFolder + File.separator;
		
		// build up the filenames in which this iteration should save them
		Constants.animeMappingPath = mappingDir + "animeMapping_" + start + "-" + end + ".json";
		Constants.animeTitlesPath = titlesDir + "animeTitles_" + start + "-" + end + ".json";
		
//		animeMapping_fullPath = mappingDir + "animeMapping_full.json";
//		animeTitles_fullPath = titlesDir + "animeTitles_full.json";
	}
	
	/**
	 * load the files into the constants variables to use them in the whole project
	 */
	public static void loadFiles() {
		
		// load the existing json files
		Constants.animeMapping = JSONUtils.loadFile(Constants.animeMappingPath);
		Constants.animeTitles = JSONUtils.loadFile(Constants.animeTitlesPath);
		
//		Constants.animeMapping_full = JSONUtils.loadFile(animeMapping_fullPath);
//		Constants.animeTitles_full = JSONUtils.loadFile(animeTitles_fullPath);
	}
	
	/**
	 * write the files into the the files specified
	 */
	public static void writeFiles() {
		JSONUtils.writeFile(Constants.animeMapping, Constants.animeMappingPath);
		JSONUtils.writeFile(Constants.animeTitles, Constants.animeTitlesPath);
		
//		JSONUtils.writeFile(Constants.animeMapping_full, animeMapping_fullPath);
//		JSONUtils.writeFile(Constants.animeTitles_full, animeTitles_fullPath);
	}
	
	/**
	 * Return the most occurring element from a list
	 * 
	 * @see Collections.sort()
	 * 
	 * @param list - a sorted list
	 * @return the most occurring element of the list
	 */
	public static <T> T getMostOccurringElement(List<T> list) {
	    int size = list.size();
	    if(size == 0)
	        return null;
	     
	    int count = 0;
	    int maxCount = 0;
	    T element = list.get(0);
	    T mostOccuringElement = element;
	     
	    for(int index = 0; index < size; index++) {
//	    	if (list.get(index).equals(-1)) {
//				continue;
//			}
	        if(list.get(index).equals(element)) {
	            count++;
	            if(count > maxCount) {
	                maxCount = count;
	                mostOccuringElement = element;
	            }
	        } else {
	            count = 1;
	        }
	        element = list.get(index);
	    }
	    return mostOccuringElement;
	}
	
	/**
	 * checks if a string can be parsed as integer
	 * 
	 * @param value - the string
	 * @return true or false
	 */
	public static boolean tryParseInt(String value) {  
	     try {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}
}