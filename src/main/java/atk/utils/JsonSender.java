package atk.utils;


import atk.SharedObjects;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class JsonSender {
    public static void sendResource(HttpServerExchange exchange, String resourceName, Object responseObject) throws JsonProcessingException {
        final String jsonString = SharedObjects.objectMapper.writer().withRootName(resourceName).writeValueAsString(responseObject);
        exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + jsonString.length());
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        Sender sender = exchange.getResponseSender();
        sender.send(jsonString);
    }

    public static void sendError(HttpServerExchange exchange, String message, int responseCode) {
        String jsonString;
        try {
            jsonString = SharedObjects.objectMapper.writer().withRootName("error").writeValueAsString(message);
        } catch (JsonProcessingException e) {
            jsonString = "{\"error\"=\"Unknown errors\"}";
        }
        exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + jsonString.length());
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.setResponseCode(responseCode);
        Sender sender = exchange.getResponseSender();
        sender.send(jsonString);
    }
}
