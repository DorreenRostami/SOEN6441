package services;

import models.Cache;
import models.SearchHistory;

import java.io.IOException;


/**
 * This class contains methods for searching videos by tag through the Youtube API
 * @author Yi Tian
 */
public class SearchByTagSevice {
    /**
     * Search for videos by tag
     * @param tag The tag to search for
     * @return SearchHistory object containing the search results
     * @throws IOException If an error occurs while fetching the search results
     * @author Yi Tian
     */
    public static SearchHistory searchByTag(String tag) throws IOException {
        return Cache.getSearchHistory("##" + tag, false);
    }
}
