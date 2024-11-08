package models;

import models.VideoInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VideoInfoTest {

    private VideoInfo videoInfo;
    private final String videoTitle = "Sample Video";
    private final String videoUrl = "https://www.youtube.com/watch?v=sample";
    private final String channelTitle = "Sample Channel";
    private final String channelUrl = "https://www.youtube.com/channel/sample";
    private final String thumbnailUrl = "https://img.youtube.com/vi/sample/hqdefault.jpg";
    private final String description = "This is a sample description.";
    private final String tagsUrl = "https://www.youtube.com/tags/sample";
    private List<String> tags;

    @BeforeEach
    public void setUp() {
        tags = Arrays.asList("Tag1", "Tag2", "Tag3");
        videoInfo = new VideoInfo(videoTitle, videoUrl, channelTitle, channelUrl, thumbnailUrl, description, tagsUrl);
    }

    @Test
    public void testGetVideoTitle() {
        assertEquals(videoTitle, videoInfo.getVideoTitle());
    }

    @Test
    public void testGetVideoUrl() {
        assertEquals(videoUrl, videoInfo.getVideoUrl());
    }

    @Test
    public void testGetChannelTitle() {
        assertEquals(channelTitle, videoInfo.getChannelTitle());
    }

    @Test
    public void testGetChannelUrl() {
        assertEquals(channelUrl, videoInfo.getChannelUrl());
    }

    @Test
    public void testGetThumbnailUrl() {
        assertEquals(thumbnailUrl, videoInfo.getThumbnailUrl());
    }

    @Test
    public void testGetDescription() {
        assertEquals(description, videoInfo.getDescription());
    }

    @Test
    public void testGetTags() {
        // tags are not set in the constructor
        assertNull(videoInfo.getTags());
    }

    @Test
    public void testGetTagsUrl() {
        assertEquals(tagsUrl, videoInfo.getTagsUrl());
    }
}