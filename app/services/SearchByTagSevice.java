package services;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import models.Cache;
import models.SearchHistory;
import models.VideoInfo;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SearchByTagSevice {
    private final Cache cache;

    @Inject
    public SearchByTagSevice(Cache cache) {
        this.cache = cache;
    }

    public SearchHistory searchByTag(String tag) throws IOException {
        List<SearchResult> results = cache.get("##" + tag, false);
        List<VideoInfo> videoInfos = results.stream().map(VideoInfo::new).collect(Collectors.toList());
        return new SearchHistory(tag, videoInfos);
    }
}
