package services;

import models.VideoInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * This class contains the unit tests for SentimentAnalyzer
 * @author Hamza Asghar Khan
 */
public class SentimentAnalyzerTest{
    /**
     * Test the getSentiment() method with a stream of videos that have an average positive sentiment
     * @author Hamza Asghar Khan
     */
    @Test
    public void testGetSentiment_positiveSentiment(){
        List<VideoInfo> videos = List.of(
                new VideoInfo("", "", "",  "", "",
                        "The adorable, blissful, adorable, agile puppy."),
                new VideoInfo("", "", "",  "", "",
                        "The beloved, awesome, lovely, brave, charming coder."),
                new VideoInfo("", "", "",  "", "",
                        "The colorful, comfortable, beautiful, delightful, desirable bed"),
                new VideoInfo("", "", "",  "", "",
                        "The eager, fluent, eloquent, agile, charming parrot."),
                new VideoInfo("", "", "",  "", "",
                        "The adorable, blissful, adorable, agile puppy."),
                new VideoInfo("", "", "",  "", "",
                        "The beloved, awesome, lovely, brave, charming coder."),
                new VideoInfo("", "", "",  "", "",
                        "The colorful, comfortable, beautiful, delightful, desirable bed"),
                new VideoInfo("", "", "",  "", "",
                        "The eager, fluent, eloquent, agile, charming parrot."),
                new VideoInfo("", "", "",  "", "",
                        "They were jubilant, sincere, and remarkably cheerful"),
                new VideoInfo("", "", "",  "", "",
                        "Her brilliant charm and heartfelt kindness shone brightly.")
        );
        SentimentAnalyzer.Sentiment outputSentiment = SentimentAnalyzer.getSentiment(videos.stream());
        SentimentAnalyzer.Sentiment expectedSentiment = SentimentAnalyzer.Sentiment.POSITIVE;
        Assert.assertEquals(expectedSentiment, outputSentiment);
    }

    /**
     * Test the getSentiment() method with a stream of videos that have an average negative sentiment
     * @author Hamza Asghar Khan
     */
    @Test
    public void testGetSentiment_negativeSentiment(){
        List<VideoInfo> videos = List.of(
                new VideoInfo("", "", "",  "", "",
                        "The annoying, ugly, twisted, lazy, weird, whiny puppy."),
                new VideoInfo("", "", "",  "", "",
                        "The hated, hateful, resentful, sad, revolting coder."),
                new VideoInfo("", "", "",  "", "",
                        "The dull, uncomfortable, ugly, dirty, hated bed"),
                new VideoInfo("", "", "",  "", "",
                        "The harsh, hostile, lazy, illogical, revolting parrot."),
                new VideoInfo("", "", "",  "", "",
                        "The adorable, blissful, adorable, agile puppy."),
                new VideoInfo("", "", "",  "", "",
                        "The annoying, ugly, twisted, lazy puppy."),
                new VideoInfo("", "", "",  "", "",
                        "The hated, hateful, resentful, sad, revolting coder."),
                new VideoInfo("", "", "",  "", "",
                        "The dull, uncomfortable, ugly, dirty, hated bed"),
                new VideoInfo("", "", "",  "", "",
                        "The harsh, hostile, lazy, illogical, revolting parrot."),
                new VideoInfo("", "", "",  "", "",
                        "The adorable, blissful, adorable, agile puppy."),
                new VideoInfo("", "", "",  "", "",
                        "Her brilliant charm and heartfelt kindness shone brightly.")
        );
        SentimentAnalyzer.Sentiment outputSentiment = SentimentAnalyzer.getSentiment(videos.stream());
        SentimentAnalyzer.Sentiment expectedSentiment = SentimentAnalyzer.Sentiment.NEGATIVE;
        Assert.assertEquals(expectedSentiment, outputSentiment);
    }

    /**
     * Test the getSentiment() method with a stream of videos that have an average neutral sentiment
     * @author Hamza Asghar Khan
     */
    @Test
    public void testGetSentiment_neutralSentiment(){
        List<VideoInfo> videos = List.of(
                new VideoInfo("", "", "",  "", "",
                        "The adorable, blissful, adorable, agile puppy."),
                new VideoInfo("", "", "",  "", "",
                        "The beloved, awesome, lovely, brave, charming coder."),
                new VideoInfo("", "", "",  "", "",
                        "The colorful, comfortable, beautiful, delightful, desirable bed"),
                new VideoInfo("", "", "",  "", "",
                        "The eager, fluent, eloquent, agile, charming parrot."),
                new VideoInfo("", "", "",  "", "",
                        "The adorable, blissful, adorable, agile puppy."),
                new VideoInfo("", "", "",  "", "",
                        "The annoying, ugly, twisted, lazy puppy."),
                new VideoInfo("", "", "",  "", "",
                        "The hated, hateful, resentful, sad, revolting coder."),
                new VideoInfo("", "", "",  "", "",
                        "The dull, uncomfortable, ugly, dirty, hated bed"),
                new VideoInfo("", "", "",  "", "",
                        "The harsh, hostile, lazy, illogical, revolting parrot."),
                new VideoInfo("", "", "",  "", "",
                        "The adorable, blissful, adorable, agile puppy."),
                new VideoInfo("", "", "",  "", "",
                        "Her brilliant charm and heartfelt kindness shone brightly.")
        );
        SentimentAnalyzer.Sentiment outputSentiment = SentimentAnalyzer.getSentiment(videos.stream());
        SentimentAnalyzer.Sentiment expectedSentiment = SentimentAnalyzer.Sentiment.NEUTRAL;
        Assert.assertEquals(expectedSentiment, outputSentiment);
    }
}