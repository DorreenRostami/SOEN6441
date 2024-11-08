package models;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import models.SearchHistory;
import models.VideoInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.SentimentAnalyzer;
//import services.Cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SearchHistoryTest {

    @Mock
    private Cache cache;

    @Mock
    private SentimentAnalyzer sentimentAnalyzer;

    private SearchResult createMockSearchResult(String videoId, String title, String channelTitle, String channelId, String thumbnailUrl) {
        SearchResult searchResult = mock(SearchResult.class);
        SearchResultSnippet snippet = mock(SearchResultSnippet.class);

        when(searchResult.getId().getVideoId()).thenReturn(videoId);
        when(snippet.getTitle()).thenReturn(title);
        when(snippet.getChannelTitle()).thenReturn(channelTitle);
        when(snippet.getChannelId()).thenReturn(channelId);
        when(snippet.getThumbnails().getDefault().getUrl()).thenReturn(thumbnailUrl);
        when(searchResult.getSnippet()).thenReturn(snippet);

        return searchResult;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchHistoryConstructorAndGetters() {
        // Arrange
        List<VideoInfo> videoInfoList = List.of(
                new VideoInfo("Video1", "https://www.youtube.com/watch?v=1", "Channel1", "https://channel1", "https://thumbnail1", "Description1", "https://tags1"),
                new VideoInfo("Video2", "https://www.youtube.com/watch?v=2", "Channel2", "https://channel2", "https://thumbnail2", "Description2", "https://tags2")
        );
        SentimentAnalyzer.Sentiment sentiment = SentimentAnalyzer.Sentiment.POSITIVE;

        // Act
        SearchHistory searchHistory = new SearchHistory("query", videoInfoList, sentiment);

        // Assert
        assertEquals("query", searchHistory.getQuery());
        assertEquals(videoInfoList, searchHistory.getResults());
        assertEquals(sentiment.emoji, searchHistory.getSentimentEmoji());
    }

//    @Test
//    public void testAddToSearchHistory() throws IOException {
//        // Arrange
//        List<SearchHistory> searchHistoryList = new ArrayList<>();
//        String query = "test query";
//
//        List<SearchResult> searchResults = List.of(
//                createMockSearchResult("videoId1", "Video Title 1", "Channel 1", "channelId1", "https://thumbnail1"),
//                createMockSearchResult("videoId2", "Video Title 2", "Channel 2", "channelId2", "https://thumbnail2")
//        );
//
//        when(cache.getDescription("videoId1")).thenReturn("Description 1");
//        when(cache.getDescription("videoId2")).thenReturn("Description 2");
//        SentimentAnalyzer.Sentiment sentiment = SentimentAnalyzer.Sentiment.POSITIVE;
//        when(SentimentAnalyzer.getSentiment(any())).thenReturn(sentiment);
//
//        // Act
//        List<SearchHistory> updatedHistory = SearchHistory.addToSearchHistory(searchHistoryList, query, searchResults, cache);
//
//        // Assert
//        assertEquals(1, updatedHistory.size());
//        SearchHistory newHistory = updatedHistory.get(0);
//        assertEquals(query, newHistory.getQuery());
//        assertEquals(2, newHistory.getResults().size());
//        assertEquals(sentiment.emoji, newHistory.getSentimentEmoji());
//
//        VideoInfo video1 = newHistory.getResults().get(0);
//        assertEquals("Video Title 1", video1.getVideoTitle());
//        assertEquals("https://www.youtube.com/watch?v=videoId1", video1.getVideoUrl());
//        assertEquals("Channel 1", video1.getChannelTitle());
//        assertEquals("/channel?query=channelId1", video1.getChannelUrl());
//        assertEquals("https://thumbnail1", video1.getThumbnailUrl());
//        assertEquals("Description 1", video1.getDescription());
//        assertEquals("/video?videoId=videoId1", video1.getTagsUrl());
//
//        VideoInfo video2 = newHistory.getResults().get(1);
//        assertEquals("Video Title 2", video2.getVideoTitle());
//        assertEquals("https://www.youtube.com/watch?v=videoId2", video2.getVideoUrl());
//        assertEquals("Channel 2", video2.getChannelTitle());
//        assertEquals("/channel?query=channelId2", video2.getChannelUrl());
//        assertEquals("https://thumbnail2", video2.getThumbnailUrl());
//        assertEquals("Description 2", video2.getDescription());
//        assertEquals("/video?videoId=videoId2", video2.getTagsUrl());
//
//        // Check if history list size is limited to 10
//        for (int i = 0; i < 12; i++) {
//            SearchHistory.addToSearchHistory(searchHistoryList, query, searchResults, cache);
//        }
//        assertEquals(10, searchHistoryList.size());
//    }

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