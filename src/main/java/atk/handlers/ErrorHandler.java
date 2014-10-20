package atk.handlers;

import atk.model.ErrorMessage;
import atk.utils.JsonSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.undertow.Handlers;
import io.undertow.io.Sender;
import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import javax.xml.ws.spi.http.HttpExchange;
import io.undertow.server.HttpHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ErrorHandler implements HttpHandler {
    private volatile io.undertow.server.HttpHandler next = ResponseCodeHandler.HANDLE_404;

    /**
     * The response codes that this handler will handle. If this is null then it will handle all 4xx and 5xx codes.
     */
    private volatile Set<Integer> responseCodes = null;

    public ErrorHandler(final io.undertow.server.HttpHandler next) {
        this.next = next;
    }

    public ErrorHandler() {
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.addDefaultResponseListener(new DefaultResponseListener() {
            @Override
            public boolean handleDefaultResponse(final HttpServerExchange exchange) {
                if (!exchange.isResponseChannelAvailable()) {
                    return false;
                }
                Set<Integer> codes = responseCodes;
                if (codes == null ? exchange.getResponseCode() >= 400 : codes.contains(Integer.valueOf(exchange.getResponseCode()))) {
                    final ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.message = "not found";
                    errorMessage.requestUrl = exchange.getRequestURL();
                    exchange.setResponseCode(404);
                    try {
                        JsonSender.sendResource(exchange, "error", errorMessage);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        try {
            next.handleRequest(exchange);
        } catch (Exception e) {
            if(exchange.isResponseChannelAvailable()) {
                final ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.stackTrace = ExceptionUtils.getStackTrace(e);
                errorMessage.requestUrl = exchange.getRequestURL();
                exchange.setResponseCode(500);
                JsonSender.sendResource(exchange, "error", errorMessage);
            }
        }
    }

    public io.undertow.server.HttpHandler getNext() {
        return next;
    }

    public ErrorHandler setNext(final io.undertow.server.HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;
    }

    public Set<Integer> getResponseCodes() {
        return Collections.unmodifiableSet(responseCodes);
    }

    public ErrorHandler setResponseCodes(final Set<Integer> responseCodes) {
        this.responseCodes = new HashSet<Integer>(responseCodes);
        return this;
    }

    public ErrorHandler setResponseCodes(final Integer... responseCodes) {
        this.responseCodes = new HashSet<Integer>(Arrays.asList(responseCodes));
        return this;
    }
}
