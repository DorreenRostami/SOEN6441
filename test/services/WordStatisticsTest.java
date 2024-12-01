package services;

import org.junit.Test;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * This class contains unit tests for {@link WordStatistics}
 * @author Dorreen Rostami
 */
public class WordStatisticsTest {

    /**
     * Test WordStatistics.getWordStats with sentences that have punctuations, have mix cases, are not in the latin alphabet
     * @author Dorreen Rostami
     */
    @Test
    public void testGetWordStats() {
        List<String> text = Arrays.asList("Hello .music.", "Hello - Adele", "music adele! !", "hello MusIC", "سلام");
        List<Tuple2<String, Long>> expected = Arrays.asList(
                new Tuple2<>("hello", 3L),
                new Tuple2<>("music", 3L),
                new Tuple2<>("adele", 2L),
                new Tuple2<>("سلام", 1L)
        );

        List<Tuple2<String, Long>> result = WordStatistics.getWordStats(text);

        assertEquals(expected, result);
    }

    /**
     * Test WordStatistics.getWordStats with empty and null sentences
     * @author Dorreen Rostami
     */
    @Test
    public void testGetWordStats_empty() {
        List<String> text = Arrays.asList("", "   ", null);
        List<Tuple2<String, Long>> expected = Arrays.asList();

        List<Tuple2<String, Long>> result = WordStatistics.getWordStats(text);

        assertEquals(expected, result);
    }

    /**
     * Test WordStatistics.getWordStats with a special case of punctuation being inside a word
     * @author Dorreen Rostami
     */
    @Test
    public void testGetWordStats_special() {
        List<String> text = Arrays.asList("Mus.Ic", "mus");
        List<Tuple2<String, Long>> expected = Arrays.asList(
                new Tuple2<>("mus", 2L),
                new Tuple2<>("ic", 1L)
        );

        List<Tuple2<String, Long>> result = WordStatistics.getWordStats(text);

        assertEquals(expected, result);
    }
}
