package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import models.Cache;
import models.SearchHistory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import services.YouTubeService;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class APIActorTest {

    private ActorSystem system;
    private TestKit probe;
    private ActorRef apiActor;
    @Mock
    private YouTubeService mockYouTubeService;

    @Before
    public void setup() {
        system = ActorSystem.create();
        probe = new TestKit(system);
        apiActor = system.actorOf(APIActor.getProps());
        mockYouTubeService = mock(YouTubeService.class);
    }

    @After
    public void tearDown() {
        TestKit.shutdownActorSystem(system);
    }

    @Test
    public void testHandleSearchQuery() throws Exception {
        String query = "testQuery";
        SearchHistory mockedSearchHistory = mock(SearchHistory.class);
        try (MockedStatic<Cache> mockCache = Mockito.mockStatic(Cache.class)) {
            mockCache.when(() -> Cache.getSearchHistory(query, false)).thenReturn(mockedSearchHistory);
            System.out.println(Cache.getSearchHistory(query, false)); //THIS WORKS

            Thread.sleep(100);

            APIActor.SearchMessage searchMessage = new APIActor.SearchMessage(query, APIActor.SearchType.QUERY);
            apiActor.tell(searchMessage, probe.getRef()); //BUT THIS CALLS THE ACTUAL CACHE WTF


            APIActor.QueryResponse response = probe.expectMsgClass(APIActor.QueryResponse.class);

            CompletableFuture<Object> future = response.future;
            SearchHistory result = (SearchHistory) future.get();

            assertEquals(mockedSearchHistory, result);
        }
    }
}
