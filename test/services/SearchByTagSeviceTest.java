//package services;
//
//import com.google.api.services.youtube.model.*;
//import models.Cache;
//import models.SearchHistory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//
///**
// * Unit tests for the {@link SearchByTagSevice} class
// * @author Yi Tian
// */
//class SearchByTagSeviceTest {
//
//    private Cache cache;
//    private SearchByTagSevice searchByTagSevice;
//
//    /**
//     * Set up the test environment
//     * @author Yi Tian
//     */
//    @BeforeEach
//    void setUp() {
//        cache = mock(Cache.class);
//        searchByTagSevice = new SearchByTagSevice();
//    }
//
//    /**
//     * Test searchByTag method with search results
//     * @throws IOException  In case an IOException is thrown by the YouTube API.
//     * @author Yi Tian
//     */
//    @Test
//    void searchByTagReturnsSearchHistoryWithResults() throws IOException {
//        String tag = "testTag";
//        int resultsLength = 10;
//        List<SearchResult> searchResults = new ArrayList<>();
//        for (int i = 0; i < resultsLength; i++) {
//            SearchResult searchResult = new SearchResult();
//            searchResult.setId(new ResourceId().setVideoId("videoId" + i));
//            SearchResultSnippet snippet = new SearchResultSnippet();
//            snippet.setTitle("title" + i);
//            snippet.setChannelTitle("channelTitle" + i);
//            snippet.setChannelId("channelId" + i);
//            snippet.setDescription("description" + i);
//            snippet.setThumbnails(new ThumbnailDetails().setDefault(new Thumbnail().set("url", "thumbnailUrl" + i)));
//            searchResult.setSnippet(snippet);
//            searchResults.add(searchResult);
//        }
//        when(cache.get("##" + tag, false)).thenReturn(searchResults);
//
//        SearchHistory searchHistory = searchByTagSevice.searchByTag(tag);
//
//        assertEquals(tag, searchHistory.getQuery());
//        assertEquals(resultsLength, searchHistory.getResults().size());
//    }
//
//    /**
//     * Test searchByTag method with no search results
//     * @throws IOException  In case an IOException is thrown by the YouTube API.
//     * @author Yi Tian
//     */
//    @Test
//    void searchByTagReturnsEmptySearchHistoryWhenNoResults() throws IOException {
//        String tag = "testTag";
//        when(cache.get("##" + tag, false)).thenReturn(Collections.emptyList());
//
//        SearchHistory searchHistory = searchByTagSevice.searchByTag(tag);
//
//        assertEquals(tag, searchHistory.getQuery());
//        assertTrue(searchHistory.getResults().isEmpty());
//    }
//
//    /**
//     * Test searchByTag method throws IOException when cache fails
//     * @throws IOException  In case an IOException is thrown by the YouTube API
//     * @author Yi Tian
//     */
//    @Test
//    void searchByTagThrowsIOExceptionWhenCacheFails() throws IOException {
//        String tag = "testTag";
//        when(cache.get("##" + tag, false)).thenThrow(new IOException());
//
//        assertThrows(IOException.class, () -> searchByTagSevice.searchByTag(tag));
//    }
//}