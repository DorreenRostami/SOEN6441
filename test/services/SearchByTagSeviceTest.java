package services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.api.services.youtube.model.*;
import models.Cache;
import models.SearchHistory;
import models.VideoInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SearchByTagSeviceTest {

    private Cache cache;
    private SearchByTagSevice searchByTagSevice;

    @BeforeEach
    void setUp() {
        cache = mock(Cache.class);
        searchByTagSevice = new SearchByTagSevice(cache);
    }

    @Test
    void searchByTagReturnsSearchHistoryWithResults() throws IOException {
        String tag = "testTag";
        int resultsLength = 10;
        List<SearchResult> searchResults = new ArrayList<>();
        for (int i = 0; i < resultsLength; i++) {
            SearchResult searchResult = new SearchResult();
            searchResult.setId(new ResourceId().setVideoId("videoId" + i));
            SearchResultSnippet snippet = new SearchResultSnippet();
            snippet.setTitle("title" + i);
            snippet.setChannelTitle("channelTitle" + i);
            snippet.setChannelId("channelId" + i);
            snippet.setDescription("description" + i);
            snippet.setThumbnails(new ThumbnailDetails().setDefault(new Thumbnail().set("url", "thumbnailUrl" + i)));
            searchResult.setSnippet(snippet);
            searchResults.add(searchResult);
        }
        when(cache.get("##" + tag, false)).thenReturn(searchResults);

        SearchHistory searchHistory = searchByTagSevice.searchByTag(tag);

        assertEquals(tag, searchHistory.getQuery());
        assertEquals(resultsLength, searchHistory.getResults().size());
    }

    @Test
    void searchByTagReturnsEmptySearchHistoryWhenNoResults() throws IOException {
        String tag = "testTag";
        when(cache.get("##" + tag, false)).thenReturn(Collections.emptyList());

        SearchHistory searchHistory = searchByTagSevice.searchByTag(tag);

        assertEquals(tag, searchHistory.getQuery());
        assertTrue(searchHistory.getResults().isEmpty());
    }

    @Test
    void searchByTagThrowsIOExceptionWhenCacheFails() throws IOException {
        String tag = "testTag";
        when(cache.get("##" + tag, false)).thenThrow(new IOException());

        assertThrows(IOException.class, () -> searchByTagSevice.searchByTag(tag));
    }
}