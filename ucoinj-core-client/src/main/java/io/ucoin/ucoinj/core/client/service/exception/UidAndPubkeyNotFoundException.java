package io.ucoin.ucoinj.core.client.service.exception;

import io.ucoin.ucoinj.core.exception.BusinessException;

/**
 * Created by eis on 11/02/15.
 */
public class UidAndPubkeyNotFoundException extends BusinessException{

    private static final long serialVersionUID = -5260280401104018980L;

    public UidAndPubkeyNotFoundException() {
        super();
    }

    public UidAndPubkeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UidAndPubkeyNotFoundException(String message) {
        super(message);
    }

    public UidAndPubkeyNotFoundException(Throwable cause) {
        super(cause);
    }
}
