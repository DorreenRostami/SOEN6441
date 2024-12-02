package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import models.Cache;
import models.SearchHistory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import services.YouTubeService;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
        CompletableFuture<Object> mockedFuture = mock(CompletableFuture.class);
        try (MockedStatic<Cache> mockCache = mockStatic(Cache.class)) {
            mockCache.when(() -> Cache.getSearchHistory(query, false)).thenReturn(mockedSearchHistory);
            mockCache.when(() -> Cache.getSearchHistory(query, true)).thenReturn(mockedSearchHistory);
            ActorSystem system = ActorSystem.create();
            TestKit probe = new TestKit(system);
            ActorRef apiActor = system.actorOf(Props.create(APIActor.class));
            APIActor.SearchMessage searchMessage = new APIActor.SearchMessage(query, APIActor.SearchType.QUERY);
            apiActor.tell(searchMessage, probe.getRef());
            APIActor.QueryResponse response = probe.expectMsgClass(APIActor.QueryResponse.class);
            when(mockedFuture.get()).thenReturn(mockedSearchHistory);
            response.future = mockedFuture;
            SearchHistory result = (SearchHistory) response.future.get();
            assertEquals(mockedSearchHistory, result);
        }
    }
}
