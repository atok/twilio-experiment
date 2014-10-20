package atk.handlers;

import atk.SharedObjects;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.xnio.Pooled;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

/**
 * A replacement for RoutingHandler in case of RESTful access.
 */
public class RestHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String elementId = null;

        String relativePath = exchange.getRelativePath();
        if(relativePath.startsWith("/") && relativePath.length() > 1) {
            elementId = relativePath.substring(1);
        }

        HttpString method = exchange.getRequestMethod();
        if(elementId == null) {
            if(method.equals(Methods.GET)) {
                get(exchange);
            } else if(method.equals(Methods.POST)) {
                post(exchange);
            } else if(method.equals(Methods.PUT)) {
                put(exchange);
            } else if(method.equals(Methods.DELETE)) {
                delete(exchange);
            } else {
                sendNotImplemented(exchange);
            }
        } else {
            if(method.equals(Methods.GET)) {
                get(exchange, elementId);
            } else if(method.equals(Methods.POST)) {
                post(exchange, elementId);
            } else if(method.equals(Methods.PUT)) {
                put(exchange, elementId);
            } else if(method.equals(Methods.DELETE)) {
                delete(exchange, elementId);
            } else {
                sendNotImplemented(exchange);
            }
        }
    }

    public void get(HttpServerExchange exchange) throws Exception {
        sendNotImplemented(exchange);
    }
    public void post(HttpServerExchange exchange) throws Exception {
        sendNotImplemented(exchange);
    }
    public void put(HttpServerExchange exchange) throws Exception {
        sendNotImplemented(exchange);
    }
    public void delete(HttpServerExchange exchange) throws Exception {
        sendNotImplemented(exchange);
    }
    public void get(HttpServerExchange exchange, String elementId) throws Exception {
        sendNotImplemented(exchange);
    }
    public void post(HttpServerExchange exchange, String elementId) throws Exception {
        sendNotImplemented(exchange);
    }
    public void put(HttpServerExchange exchange, String elementId) throws Exception {
        sendNotImplemented(exchange);
    }
    public void delete(HttpServerExchange exchange, String elementId) throws Exception {
        sendNotImplemented(exchange);
    }

    protected void sendResource(HttpServerExchange exchange, String resourceName, Object responseObject) throws JsonProcessingException {
        final String jsonString = SharedObjects.objectMapper.writer().withRootName(resourceName).writeValueAsString(responseObject);
        exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + jsonString.length());
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        Sender sender = exchange.getResponseSender();
        sender.send(jsonString);
    }

    protected void sendNotImplemented(HttpServerExchange exchange) throws JsonProcessingException {
        Logger.getGlobal().warning("Method not implemented: " + exchange.getRequestMethod() + " " + exchange.getRequestURL());
        exchange.setResponseCode(405);
        sendResource(exchange, "error", "Method not implemented");
    }

    /*
    not so sure about that.
     */
    protected String readRequestBody(HttpServerExchange exchange) throws IOException {
        long len = exchange.getRequestContentLength();

        Pooled<ByteBuffer> pooledByteBuffer = exchange.getConnection().getBufferPool().allocate();
        ByteBuffer byteBuffer = pooledByteBuffer.getResource();

        int limit = byteBuffer.limit();
        byteBuffer.clear();

        exchange.getRequestChannel().read(byteBuffer);
        int pos = byteBuffer.position();
        byteBuffer.rewind();
        byte[] bytes = new byte[pos];
        byteBuffer.get(bytes);

        String requestBody = new String(bytes, Charset.forName("UTF-8") );

        byteBuffer.clear();
        pooledByteBuffer.free();

        return requestBody;
    }

    protected <T> T readObjectFromRequestBody(HttpServerExchange exchange, Class<T> valueType) throws IOException {
        String body = readRequestBody(exchange);
        return SharedObjects.objectMapper.readValue(body, valueType);
    }
}
