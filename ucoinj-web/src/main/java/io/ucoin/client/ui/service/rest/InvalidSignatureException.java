package io.ucoin.client.ui.service.rest;

import io.ucoin.client.core.technical.UCoinBusinessException;

/**
 * Created by Benoit on 03/04/2015.
 */
public class InvalidSignatureException extends UCoinBusinessException {


    public InvalidSignatureException() {
        super();
    }

    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSignatureException(String message) {
        super(message);
    }

    public InvalidSignatureException(Throwable cause) {
        super(cause);
    }
}
