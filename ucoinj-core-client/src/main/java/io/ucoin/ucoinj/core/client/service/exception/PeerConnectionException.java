package io.ucoin.ucoinj.core.client.service.exception;

import io.ucoin.ucoinj.core.exception.BusinessException;

/**
 * Created by eis on 05/02/15.
 */
public class PeerConnectionException extends BusinessException{

    public PeerConnectionException() {
        super();
    }

    public PeerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PeerConnectionException(String message) {
        super(message);
    }

    public PeerConnectionException(Throwable cause) {
        super(cause);
    }
}
