package io.ucoin.ucoinj.web.security.keypair;

/**
 * Created by blavenie on 06/01/16.
 */
public interface ChallengeMessageStore {
    /**
     * Validate a given challenge
     * @return
     */
    boolean validateChallenge(String challenge);

    /**
     * Compute a new challenge message, and remember it to validate it later.
     * @return
     */
    String createNewChallenge();

}
