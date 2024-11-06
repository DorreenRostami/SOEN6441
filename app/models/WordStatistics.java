// models/WordFrequencyAnalyzer.java

package models;

import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class WordStatistics {

    /**
     * Analyzes a text and returns a sorted list of word counts in descending order
     *
     * @param text List of text entries to analyze
     * @return A sorted list of words and their frequencies
     * @author Dorreen Rostami
     */
    public static List<Tuple2<String, Long>> getWordStats(List<String> text) {
        Map<String, Long> wordCount = text.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .flatMap(t -> Arrays.stream(t.split(" ")))
                .map(String::toLowerCase)
                .filter(word -> word.matches("\\p{L}+"))  //no non-letter words
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        // sort in descending order
        return wordCount.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .map(entry -> new Tuple2<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
