package models;

import com.google.api.services.youtube.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import services.SentimentAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SearchHistoryTest {

    @Mock
    private Cache cache;  // Example of a mock object


    @Mock
    private SentimentAnalyzer sentimentAnalyzer;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }


    /**
     * Creates a mock instance of {@link SearchResult} for testing
     *
     * @param videoId id of youtube video
     * @param title title of video
     * @param channelTitle title of channel who has uploaded the video
     * @param channelId id of channel
     * @param thumbnailUrl url of channel thumbnail
     * @return a mocked item of type SearchResult (youtube model)
     * @author Hao - initial implementation
     * @author Dorreen - added the mock of more complex classes (those related to thumbnails and resourceId)
     */
    private SearchResult createMockSearchResult(String videoId, String title, String channelTitle, String channelId, String thumbnailUrl, String description) {
        // Mock the SearchResult object and its Snippet
        SearchResult searchResult = mock(SearchResult.class);
        SearchResultSnippet snippet = mock(SearchResultSnippet.class);
        ResourceId resourceId = mock(ResourceId.class);
        ThumbnailDetails thumbnailDetails = mock(ThumbnailDetails.class);
        Thumbnail thumbnail = mock(Thumbnail.class);

        when(searchResult.getId()).thenReturn(resourceId);
        when(resourceId.getVideoId()).thenReturn(videoId);
        when(snippet.getTitle()).thenReturn(title);
        when(snippet.getChannelTitle()).thenReturn(channelTitle);
        when(snippet.getChannelId()).thenReturn(channelId);
        when(snippet.getDescription()).thenReturn(description);
        when(snippet.getThumbnails()).thenReturn(thumbnailDetails);
        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
        when(thumbnail.getUrl()).thenReturn(thumbnailUrl);
        when(searchResult.getSnippet()).thenReturn(snippet);

        return searchResult;
    }


    /**
     * Test case for the {@link SearchHistory#addToSearchHistory} method.
     * Check that the search history is correctly updated when new search results (a list of 10 results) are added
     * @author Dorreen Rostami
     */
    @Test
    void testAddToSearchHistory() {
        List<SearchHistory> searchHistoryList = new ArrayList<>();
        String query = "query";

        SearchResult res = createMockSearchResult("v1", "Title", "Channel", "c1", "https://thumbnail/1", "description");
        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(res);
        }

        List<SearchHistory> updatedSearchHistory = SearchHistory.addToSearchHistory(searchHistoryList, query, results, cache);

        assertEquals(1, updatedSearchHistory.size());
        assertEquals(query, updatedSearchHistory.get(0).getQuery());
        assertEquals(10, updatedSearchHistory.get(0).getResults().size());

        VideoInfo firstResult = updatedSearchHistory.get(0).getResults().get(0);
        assertEquals("Title", firstResult.getVideoTitle());
        assertEquals("https://www.youtube.com/watch?v=v1", firstResult.getVideoUrl());
        assertEquals("Channel", firstResult.getChannelTitle());
        assertEquals("/channel?query=c1", firstResult.getChannelUrl());
        assertEquals("https://thumbnail/1", firstResult.getThumbnailUrl());
        assertEquals("description", firstResult.getDescription());
        assertEquals("/video?videoId=v1", firstResult.getTagsUrl());
    }

    /**
     * Tests that no more than 10 search history objects are kept in the search history list when the
     * {@link SearchHistory#addToSearchHistory} method is called
     * @author Dorreen Rostami
     */
    @Test
    void testAddToSearchHistory_keep10() {
        SearchResult res = createMockSearchResult("v1", "Title", "Channel", "c1", "https://thumbnail/1", "description");
        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(res);
        }

        List<SearchHistory> searchHistoryList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            searchHistoryList = SearchHistory.addToSearchHistory(searchHistoryList, "query", results, cache);
        }

        assertEquals(10, searchHistoryList.size());
    }

    @Test
    public void testSearchHistoryConstructorAndGetters() {
        List<VideoInfo> videoInfoList = List.of(
                new VideoInfo("Video1", "https://www.youtube.com/watch?v=1", "Channel1", "https://channel1", "https://thumbnail1", "Description1", "https://tags1"),
                new VideoInfo("Video2", "https://www.youtube.com/watch?v=2", "Channel2", "https://channel2", "https://thumbnail2", "Description2", "https://tags2")
        );
        SentimentAnalyzer.Sentiment sentiment = SentimentAnalyzer.Sentiment.POSITIVE;

        SearchHistory searchHistory = new SearchHistory("query", videoInfoList, sentiment);

        assertEquals("query", searchHistory.getQuery());
        assertEquals(videoInfoList, searchHistory.getResults());
        assertEquals(sentiment.emoji, searchHistory.getSentimentEmoji());
    }


//    @Test
//    public void testAddToSearchHistoryHandlesIOException() throws IOException {
//        // Arrange
//        List<SearchHistory> searchHistoryList = new ArrayList<>();
//        String query = "test query";
//
//        List<SearchResult> searchResults = List.of(
//                createMockSearchResult("videoId1", "Video Title 1", "Channel 1", "channelId1", "https://thumbnail1")
//        );
//
//        when(cache.getDescription("videoId1")).thenThrow(new IOException("Test IOException"));
//
//        SentimentAnalyzer.Sentiment sentiment = SentimentAnalyzer.Sentiment.NEGATIVE;
//        when(SentimentAnalyzer.getSentiment(any())).thenReturn(sentiment);
//
//        // Act
//        List<SearchHistory> updatedHistory = SearchHistory.addToSearchHistory(searchHistoryList, query, searchResults, cache);
//
//        // Assert
//        assertEquals(1, updatedHistory.size());
//        SearchHistory newHistory = updatedHistory.get(0);
//        assertEquals("test query", newHistory.getQuery());
//        assertEquals(1, newHistory.getResults().size());
//
//        VideoInfo video = newHistory.getResults().get(0);
//        assertEquals("Video Title 1", video.getVideoTitle());
//        assertEquals("https://www.youtube.com/watch?v=videoId1", video.getVideoUrl());
//        assertEquals("Channel 1", video.getChannelTitle());
//        assertEquals("/channel?query=channelId1", video.getChannelUrl());
//        assertEquals("https://thumbnail1", video.getThumbnailUrl());
//        assertEquals("", video.getDescription());  // Description should be empty due to IOException
//        assertEquals("/video?videoId=videoId1", video.getTagsUrl());
//    }
}