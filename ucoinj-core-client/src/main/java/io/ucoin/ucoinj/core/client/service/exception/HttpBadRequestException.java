package io.ucoin.ucoinj.core.client.service.exception;

import io.ucoin.ucoinj.core.exception.TechnicalException;

/**
 * Created by eis on 11/02/15.
 */
public class HttpBadRequestException extends TechnicalException{

    private static final long serialVersionUID = -5260280401104018980L;

    public HttpBadRequestException() {
        super();
    }

    public HttpBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpBadRequestException(String message) {
        super(message);
    }

    public HttpBadRequestException(Throwable cause) {
        super(cause);
    }
}
