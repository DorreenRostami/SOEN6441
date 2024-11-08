//package models;
//
//import com.google.api.services.youtube.model.SearchResult;
//import com.google.api.services.youtube.model.SearchResultSnippet;
//import com.google.api.services.youtube.model.Thumbnail;
//import com.google.api.services.youtube.model.ThumbnailDetails;
//import com.google.api.services.youtube.model.ResourceId;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import services.SentimentAnalyzer;
//import services.SentimentAnalyzer.Sentiment;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class SearchHistoryTest {
//
//    @Mock
//    private Cache cache;
//
//    @Mock
//    private Sentiment sentiment;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testConstructorAndGetters() {
//        List<VideoInfo> videoInfoList = List.of(
//                new VideoInfo("Video Title", "https://www.youtube.com/watch?v=123", "Channel Title",
//                        "/channel?query=123", "https://img.youtube.com/vi/123/hqdefault.jpg",
//                        "Sample description", "/video?videoId=123")
//        );
//
//        // Create a SearchHistory instance
//        SearchHistory searchHistory = new SearchHistory("sample query", videoInfoList, sentiment);
//
//        // Verify query and results are correctly set
//        assertEquals("sample query", searchHistory.getQuery());
//        assertEquals(videoInfoList, searchHistory.getResults());
//        assertNull(searchHistory.getSentimentEmoji());
//
//        // Set up the emoji for sentiment and test again
//        when(sentiment.emoji).thenReturn("😊");
//        assertEquals("😊", searchHistory.getSentimentEmoji());
//    }
//
//    @Test
//    void testAddToSearchHistoryWithNewEntry() throws IOException {
//        String query = "sample query";
//        List<SearchResult> results = List.of(mockSearchResult("123", "Sample Video", "Sample Channel", "sampleChannelId"));
//
//        List<SearchHistory> searchHistoryList = new ArrayList<>();
//
//        // Mock cache behavior
//        when(cache.getDescription("123")).thenReturn("Sample description");
//        when(SentimentAnalyzer.getSentiment(any())).thenReturn(sentiment);
//        when(sentiment.emoji).thenReturn("😊");
//
//        // Add to search history
//        List<SearchHistory> updatedHistory = SearchHistory.addToSearchHistory(searchHistoryList, query, results, cache);
//
//        // Assertions to verify results
//        assertEquals(1, updatedHistory.size());
//        SearchHistory history = updatedHistory.get(0);
//        assertEquals(query, history.getQuery());
//        assertEquals("Sample Video", history.getResults().get(0).getVideoTitle());
//        assertEquals("Sample description", history.getResults().get(0).getDescription());
//        assertEquals("😊", history.getSentimentEmoji());
//    }
//
//    @Test
//    void testAddToSearchHistoryWithMaxEntries() throws IOException {
//        String query = "sample query";
//        List<SearchResult> results = List.of(mockSearchResult("123", "Sample Video", "Sample Channel", "sampleChannelId"));
//
//        // Create an initial list with 10 entries
//        List<SearchHistory> searchHistoryList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            searchHistoryList.add(new SearchHistory("query" + i, List.of(), sentiment));
//        }
//
//        // Mock cache and sentiment behavior
//        when(cache.getDescription("123")).thenReturn("Sample description");
//        when(SentimentAnalyzer.getSentiment(any())).thenReturn(sentiment);
//
//        // Add to search history
//        List<SearchHistory> updatedHistory = SearchHistory.addToSearchHistory(searchHistoryList, query, results, cache);
//
//        // Verify that size is still 10, and the oldest entry is removed
//        assertEquals(10, updatedHistory.size());
//        assertEquals(query, updatedHistory.get(0).getQuery());
//        assertEquals("query8", updatedHistory.get(9).getQuery()); // Last element should be the 9th initial query
//    }
//
//    @Test
//    void testAddToSearchHistoryWithIOException() throws IOException {
//        String query = "sample query";
//        List<SearchResult> results = List.of(mockSearchResult("123", "Sample Video", "Sample Channel", "sampleChannelId"));
//
//        List<SearchHistory> searchHistoryList = new ArrayList<>();
//
//        // Simulate an IOException when fetching description
//        when(cache.getDescription("123")).thenThrow(new IOException("IO Exception"));
//
//        // Mock sentiment behavior
//        when(SentimentAnalyzer.getSentiment(any())).thenReturn(sentiment);
//
//        // Add to search history
//        List<SearchHistory> updatedHistory = SearchHistory.addToSearchHistory(searchHistoryList, query, results, cache);
//
//        // Assertions to verify description is empty on exception
//        assertEquals(1, updatedHistory.size());
//        SearchHistory history = updatedHistory.get(0);
//        assertEquals(query, history.getQuery());
//        assertEquals("Sample Video", history.getResults().get(0).getVideoTitle());
//        assertEquals("", history.getResults().get(0).getDescription());
//    }
//
//    // Helper method to create a mock SearchResult
//    private SearchResult mockSearchResult(String videoId, String videoTitle, String channelTitle, String channelId) {
//        SearchResult searchResult = mock(SearchResult.class);
//        SearchResultSnippet snippet = mock(SearchResultSnippet.class);
//        ResourceId resourceId = mock(ResourceId.class);
//        ThumbnailDetails thumbnails = mock(ThumbnailDetails.class);
//        Thumbnail thumbnail = mock(Thumbnail.class);
//
//        when(resourceId.getVideoId()).thenReturn(videoId);
//        when(snippet.getTitle()).thenReturn(videoTitle);
//        when(snippet.getChannelTitle()).thenReturn(channelTitle);
//        when(snippet.getChannelId()).thenReturn(channelId);
//        when(thumbnails.getDefault()).thenReturn(thumbnail);
//        when(thumbnail.getUrl()).thenReturn("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg");
//
//        when(searchResult.getSnippet()).thenReturn(snippet);
//        when(searchResult.getId()).thenReturn(resourceId);
//        when(snippet.getThumbnails()).thenReturn(thumbnails);
//
//        return searchResult;
//    }
//}