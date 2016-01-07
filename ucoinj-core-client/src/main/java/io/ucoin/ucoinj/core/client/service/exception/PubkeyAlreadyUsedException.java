package io.ucoin.ucoinj.core.client.service.exception;

import io.ucoin.ucoinj.core.exception.BusinessException;

/**
 * Created by eis on 11/02/15.
 */
public class PubkeyAlreadyUsedException extends BusinessException{

    private static final long serialVersionUID = -5260280401104018980L;

    public PubkeyAlreadyUsedException() {
        super();
    }

    public PubkeyAlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PubkeyAlreadyUsedException(String message) {
        super(message);
    }

    public PubkeyAlreadyUsedException(Throwable cause) {
        super(cause);
    }
}
