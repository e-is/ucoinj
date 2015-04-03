package io.ucoin.client.core.service.search;

import io.ucoin.client.core.technical.UCoinBusinessException;

/**
 * Created by Benoit on 03/04/2015.
 */
public class DuplicateCurrencyException extends UCoinBusinessException{

    public DuplicateCurrencyException() {
        super();
    }

    public DuplicateCurrencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateCurrencyException(String message) {
        super(message);
    }

    public DuplicateCurrencyException(Throwable cause) {
        super(cause);
    }

}
