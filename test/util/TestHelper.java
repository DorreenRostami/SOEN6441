package util;

import com.google.api.services.youtube.model.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * class for functions used across multiple test classes
 * @author Dorreen Rostami
 */
public class TestHelper {
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
    public static SearchResult createMockSearchResult(String videoId, String title, String channelTitle, String channelId, String thumbnailUrl, String description) {
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
}
