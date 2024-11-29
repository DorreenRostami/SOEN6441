package services;

import models.Cache;
import models.SearchHistory;

import javax.inject.Inject;
import java.io.IOException;


/**
 * This class contains methods for searching videos by tag through the Youtube API
 * @author Yi Tian
 */
public class SearchByTagSevice {
    private final Cache cache;

    /**
     * Constructor for the SearchByTagService
     * @param cache The cache to use for storing search results
     * @author Yi Tian
     */
    @Inject
    public SearchByTagSevice(Cache cache) {
        this.cache = cache;
    }

    /**
     * Search for videos by tag
     * @param tag The tag to search for
     * @return SearchHistory object containing the search results
     * @throws IOException If an error occurs while fetching the search results
     * @author Yi Tian
     */
    public SearchHistory searchByTag(String tag) throws IOException {
        return cache.get("##" + tag, false);
    }
}
