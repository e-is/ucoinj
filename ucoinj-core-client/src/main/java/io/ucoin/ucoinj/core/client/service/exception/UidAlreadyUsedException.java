package io.ucoin.ucoinj.core.client.service.exception;

import io.ucoin.ucoinj.core.exception.BusinessException;

/**
 * Created by eis on 11/02/15.
 */
public class UidAlreadyUsedException extends BusinessException{

    private static final long serialVersionUID = -5260280401104018980L;

    public UidAlreadyUsedException() {
        super();
    }

    public UidAlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UidAlreadyUsedException(String message) {
        super(message);
    }

    public UidAlreadyUsedException(Throwable cause) {
        super(cause);
    }
}
