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

/**
 * Unit tests for the {@link SearchHistory} class
 * @author Dorreen & Hao
 */
public class SearchHistoryTest {

    @Mock
    private Cache cache;


    @Mock
    private SentimentAnalyzer sentimentAnalyzer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void testAddToSearchHistory() throws IOException {
        List<SearchHistory> searchHistoryList = new ArrayList<>();
        String query = "query";
        String cachedDescription = "Cached description";
        String videoId = "v1";
        when(cache.getDescription(videoId)).thenReturn(cachedDescription);

        SearchResult res = createMockSearchResult(videoId, "Title", "Channel", "c1", "https://thumbnail/1", cachedDescription);
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
        assertEquals(cachedDescription, firstResult.getDescription());
        assertEquals("/video?videoId=v1", firstResult.getTagsUrl());
        verify(cache, times(10)).getDescription(videoId);
    }

    /**
     * Tests that no more than 10 search history objects are kept in the search history list when the
     * {@link SearchHistory#addToSearchHistory} method is called
     * @author Dorreen Rostami
     */
    @Test
    void testAddToSearchHistory_keep10() throws IOException {
        String cachedDescription = "Cached description";
        String videoId = "v1";
        when(cache.getDescription(videoId)).thenReturn(cachedDescription);
        SearchResult res = createMockSearchResult(videoId, "Title", "Channel", "c1", "https://thumbnail/1", cachedDescription);
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

    /**
     * Tests that the description becomes an empty string when the cache throws an exception in the
     * {@link SearchHistory#addToSearchHistory} method
     * @author Dorreen Rostami
     */
    @Test
    void testAddToSearchHistory_throwException() throws IOException {
        String videoId = "v1";
        when(cache.getDescription(videoId)).thenThrow(new IOException("Mocked IOException"));
        SearchResult res = createMockSearchResult(videoId, "Title", "Channel", "c1", "https://thumbnail/1", "desc");
        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(res);
        }
        List<SearchHistory> searchHistoryList = SearchHistory.addToSearchHistory(new ArrayList<>(), "query", results, cache);;

        VideoInfo firstResult = searchHistoryList.get(0).getResults().get(0);
        assertEquals("", firstResult.getDescription());
        assertEquals(1, searchHistoryList.size());
    }

    /**
     * Tests that the getters and setters of the {@link SearchHistory} class
     * @author Hao
     */
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
}