package models;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.SentimentAnalyzer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link SearchHistory} class
 * @author Dorreen & Hao
 */
public class SearchHistoryTest {

    @Mock
    private SentimentAnalyzer sentimentAnalyzer;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

        SearchHistory searchHistory = new SearchHistory("query", videoInfoList);

        assertEquals("query", searchHistory.getQuery());
        assertEquals(videoInfoList, searchHistory.getResults());
    }
}