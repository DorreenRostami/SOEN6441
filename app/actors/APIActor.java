package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class APIActor extends AbstractActor {

    static class QuerySearch{
        String keyword;
    }

    public static Props getProps() {
        return Props.create(APIActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(QuerySearch.class, message -> {
            String keyword = message.keyword;

        });
    }
}
