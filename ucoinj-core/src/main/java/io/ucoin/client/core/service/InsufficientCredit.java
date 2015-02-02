package io.ucoin.client.core.service;

import io.ucoin.client.core.technical.UCoinBusinessException;

public class InsufficientCredit extends UCoinBusinessException {

	private static final long serialVersionUID = -5260280401104018980L;

	public InsufficientCredit() {
        super();
    }

    public InsufficientCredit(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientCredit(String message) {
        super(message);
    }

    public InsufficientCredit(Throwable cause) {
        super(cause);
    }
	
}
