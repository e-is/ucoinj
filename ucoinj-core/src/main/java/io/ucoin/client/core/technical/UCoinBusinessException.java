package io.ucoin.client.core.technical;

/**
 * A uCoin business exception
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 *
 */
public class UCoinBusinessException extends RuntimeException{

    private static final long serialVersionUID = -6715624222174163366L;

    public UCoinBusinessException() {
        super();
    }

    public UCoinBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UCoinBusinessException(String message) {
        super(message);
    }

    public UCoinBusinessException(Throwable cause) {
        super(cause);
    }
    
}
