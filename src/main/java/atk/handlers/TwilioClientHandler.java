package atk.handlers;

import atk.SharedObjects;
import com.twilio.sdk.client.TwilioCapability;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.Headers;


public class TwilioClientHandler implements HttpHandler {

    public final String accountSid;
    public final String authToken;
    final ResourceHandler resourceHandler;

    public TwilioClientHandler(ResourceHandler resourceHandler, String accountSid, String authToken) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.resourceHandler = resourceHandler;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String applicationSid = "APabe7650f654fc34655fc81ae71caa3ff";

        TwilioCapability capability = new TwilioCapability(accountSid, authToken);
        capability.allowClientOutgoing(applicationSid);

        String token = null;
        try {
            token = capability.generateToken();
        } catch (TwilioCapability.DomainException e) {
            e.printStackTrace();
        }

        final String jsonString = SharedObjects.objectMapper.writer().withRootName("token").writeValueAsString(token);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        Sender sender = exchange.getResponseSender();
        sender.send(jsonString);
    }
}
