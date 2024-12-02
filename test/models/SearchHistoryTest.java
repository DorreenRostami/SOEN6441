package models;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.SentimentAnalyzer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

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


    @Test
    public void testEquals() {
        VideoInfo video1 = new VideoInfo("Title1", "url1", "Channel1", "channelUrl1", "thumbUrl1", "Description1", "tagsUrl1");
        VideoInfo video2 = new VideoInfo("Title2", "url2", "Channel2", "channelUrl2", "thumbUrl2", "Description2", "tagsUrl2");
        VideoInfo video3 = new VideoInfo("Title3", "url3", "Channel3", "channelUrl3", "thumbUrl3", "Description3", "tagsUrl3");

        SearchHistory history1 = new SearchHistory("query1", Arrays.asList(video1, video2));
        history1.setSentiment(SentimentAnalyzer.Sentiment.POSITIVE);

        SearchHistory history2 = new SearchHistory("query1", Arrays.asList(video1, video2));
        history2.setSentiment(SentimentAnalyzer.Sentiment.POSITIVE);
        assertTrue(history1.equals(history2)); //same

        SearchHistory history3 = new SearchHistory("query2", Arrays.asList(video1, video2));
        history2.setSentiment(SentimentAnalyzer.Sentiment.POSITIVE);
        assertFalse(history1.equals(history3)); //diff query

        SearchHistory history4 = new SearchHistory("query1", Arrays.asList(video1));
        history2.setSentiment(SentimentAnalyzer.Sentiment.POSITIVE);
        assertFalse(history1.equals(history4)); //diff video list size

        SearchHistory history5 = new SearchHistory("query1", Arrays.asList(video1, video3));
        history2.setSentiment(SentimentAnalyzer.Sentiment.POSITIVE);
        assertFalse(history1.equals(history5)); //diff video
    }
}