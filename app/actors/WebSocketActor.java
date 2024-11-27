package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.api.services.youtube.model.SearchResult;
import models.Cache;
import models.Database;
import models.SearchHistory;
import models.VideoInfo;
import scala.concurrent.duration.Duration;
import services.YouTubeService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WebSocketActor extends AbstractActorWithTimers {
    private final ActorRef out;
    private final ActorRef apiActor;
    private final Cache cache;
    private final YouTubeService youTubeService;
    private final Database database;

    private static final int POLL_TIME = 20;

    /**
     * Constructor for WebSocketActor
     *
     * @param out               WebSocket connection
     * @author Dorreen
     */
    private WebSocketActor(ActorRef out) throws GeneralSecurityException, IOException {
        this.out = out;
        this.youTubeService = new YouTubeService();
        this.cache = new Cache(this.youTubeService);
        this.database = new Database();
    }

    /**
     * Create Props for WebSocketActor
     *
     * @param out WebSocket connection
     * @return Props instance
     * @author Dorreen
     */
    public static Props props(ActorRef out) {
        return Props.create(WebSocketActor.class, () -> new WebSocketActor(out));
    }

    // Message class for updates
    public static class CheckForUpdates {}

    @Override
    public void preStart() {
        // Immediately send the latest results
//        try {
//            List<SearchResult> initialResults = cache.get(query, false);
//            for (SearchResult result : initialResults) {
//                out.tell(result.getSnippet().getTitle(), self());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // Start polling for new results
        getTimers().startPeriodicTimer(
                "pollingTimer",    //ID
                new CheckForUpdates(), //Message
                Duration.create(POLL_TIME, TimeUnit.SECONDS)
        );
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, msg -> apiActor.tell(APIActor.QuerySearch(msg), getSelf()))
                .build();
    }

    private void checkForUpdates() {
        boolean changed = false;
        List<SearchHistory> updatedHistory = new ArrayList<>();
        try {
            List<SearchHistory> searchHistory = database.get(sessionId);

            // Check each query in the user's search history
            for (int i = 0; i < searchHistory.size(); i++) {
                SearchHistory sh = searchHistory.get(i);
                String query = sh.getQuery();
                List<VideoInfo> oldResults = sh.getResults();

                // Fetch the latest results from the YT API
                List<SearchResult> newResults = youTubeService.searchVideos(query);
                updatedHistory = SearchHistory.editSearchHistory(database.get(sessionId), query, newResults, cache);

                if (!areResultsEqual(oldResults, updatedHistory.get(i).getResults())) {
                    cache.put(query, newResults, false);
                    changed = true;
                }
            }
        } catch (IOException e) {
            //nothing
        }

        if (changed) {
            out.tell(updatedHistory, self());
        }
        System.out.println(updatedHistory.get(0).getQuery());
    }

//    private String formatResults(List<SearchHistory> results) {
//        // Format the results as HTML or JSON
//        return views.html.hello.render(results).toString();
//    }

    /**
     * Compares two lists of search results by comparing the video URLs
     *
     * @param oldResults The previous list of search results
     * @param newResults The latest list of search results
     * @return true if both lists are equal, false otherwise
     * @author Dorreen
     */
    private boolean areResultsEqual(List<VideoInfo> oldResults, List<VideoInfo> newResults) {
        List<String> oldIds = oldResults.stream().map(VideoInfo::getVideoUrl).collect(Collectors.toList());
        List<String> newIds = newResults.stream().map(VideoInfo::getVideoUrl).collect(Collectors.toList());
        return oldIds.equals(newIds);
    }
}
