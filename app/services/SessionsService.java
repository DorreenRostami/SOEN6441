package services;

import play.mvc.Http;
import java.util.UUID;

/**
 * This class provides services pertaining to user sessions.
 * @author Hamza Asghar Khan
 */
public class SessionsService {
    /**
     * Returns the SessionId associated with a request.
     * @param request Target Request object
     * @return The SessionId associated with the request or a new SessionId if the request lacks it.
     * @author Hamza Asghar Khan
     */
    public static String getSessionId(Http.Request request){
        String sessionId = request.session().get("sessionId").orElse(null);
        if (sessionId == null){
            sessionId = UUID.randomUUID().toString();
        }
        return sessionId;
    }

    /**
     * Checks whether a provide Request object has a SessionId
     * @param request Target Request Object
     * @return true if and only if the provided Request object has a SessionId; false otherwise.
     * @author Hamza Asghar Khan
     */
    public static boolean hasSessionId(Http.Request request){
        return request.session().get("sessionId").isPresent();
    }
}
