package atk.handlers;


import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


public class TwiMLHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        TwiMLResponse response = new TwiMLResponse();
        Say say = new Say("Hello, and welcome!");
        say.setVoice("man");

        try {
            response.append(say);
        } catch (TwiMLException e) {
            e.printStackTrace();
        }

        String responseString = response.toXML();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/xml");
        exchange.getResponseSender().send(responseString);
    }
}
