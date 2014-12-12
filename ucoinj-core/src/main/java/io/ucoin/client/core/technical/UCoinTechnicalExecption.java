package io.ucoin.client.core.technical;

/**
 * A uCoin technical exception
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 
 *
 */
public class UCoinTechnicalExecption extends RuntimeException{

    private static final long serialVersionUID = -6715624222174163366L;

    public UCoinTechnicalExecption() {
        super();
    }

    public UCoinTechnicalExecption(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UCoinTechnicalExecption(String message, Throwable cause) {
        super(message, cause);
    }

    public UCoinTechnicalExecption(String message) {
        super(message);
    }

    public UCoinTechnicalExecption(Throwable cause) {
        super(cause);
    }
    
}
