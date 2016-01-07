package io.ucoin.ucoinj.core.client.service.exception;

import io.ucoin.ucoinj.core.exception.TechnicalException;

/**
 * Created by blavenie on 05/01/16.
 */
public class JsonSyntaxException extends TechnicalException {

    public JsonSyntaxException() {
        super();
    }

    public JsonSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonSyntaxException(String message) {
        super(message);
    }

    public JsonSyntaxException(Throwable cause) {
        super(cause);
    }
}
