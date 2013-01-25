package au.com.sensis.stubby.service.model;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.service.model.MatchResult.Field;

public class StubServiceExchange { // wrap exchange model with some extra runtime info

    private static final Logger LOGGER = Logger.getLogger(StubServiceExchange.class);

    private StubExchange exchange;
    private RequestPattern requestPattern;
    private List<MatchResult> attempts;
    
    public StubServiceExchange(StubExchange exchange) {
        this.exchange = exchange;
        this.requestPattern = new RequestPattern(exchange.getRequest());
        this.attempts = new ArrayList<MatchResult>();
    }
    
    public boolean matches(StubRequest message) throws URISyntaxException {
        MatchResult result = requestPattern.matches(message);
        for (Field field : result.fields) {
            LOGGER.trace("Match outcome: " + field);
        }
        LOGGER.trace("Match score: " + result.score());
        if (result.score() >= 5) { // only record attempts that match request path
            attempts.add(result);
        }
        return result.matches();
    }

    public StubExchange getExchange() {
        return exchange;
    }

    public List<MatchResult> getAttempts() {
        return attempts;
    }

    @Override
    public int hashCode() { // hash/equality is based on the request pattern only
        return requestPattern.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof StubServiceExchange)
                && ((StubServiceExchange)obj).requestPattern.equals(requestPattern);
    }

}
