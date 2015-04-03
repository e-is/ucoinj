package io.ucoin.client.core.service.search;

import io.ucoin.client.core.technical.UCoinBusinessException;

/**
 * Created by Benoit on 03/04/2015.
 */
public class AccessDeniedException extends UCoinBusinessException{

    public AccessDeniedException() {
        super();
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }

}
