package services;

import com.google.api.services.youtube.model.Video;
import models.Cache;

import javax.inject.Inject;
import java.io.IOException;

public class VideoDetailSevice {
    private final Cache cache;

    @Inject
    public VideoDetailSevice(Cache cache) {
        this.cache = cache;
    }

    public Video getVideo(String videoId) throws IOException {
        return cache.getVideo(videoId);
    }
}
