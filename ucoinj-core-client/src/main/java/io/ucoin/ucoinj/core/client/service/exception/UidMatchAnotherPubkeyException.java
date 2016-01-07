package io.ucoin.ucoinj.core.client.service.exception;

import io.ucoin.ucoinj.core.exception.BusinessException;

/**
 * Created by eis on 11/02/15.
 */
public class UidMatchAnotherPubkeyException extends BusinessException {

    private static final long serialVersionUID = -5260280401104018980L;

    public UidMatchAnotherPubkeyException() {
        super();
    }

    public UidMatchAnotherPubkeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UidMatchAnotherPubkeyException(String message) {
        super(message);
    }

    public UidMatchAnotherPubkeyException(Throwable cause) {
        super(cause);
    }
}
