package services;

import com.google.api.services.youtube.model.SearchResult;
import models.Cache;
import models.SearchHistory;
import models.VideoInfo;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


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
        List<SearchResult> results = cache.get("##" + tag, false);
        List<VideoInfo> videoInfos = results.stream().map(VideoInfo::new).collect(Collectors.toList());
        return new SearchHistory(tag, videoInfos);
    }
}
