package io.ucoin.ucoinj.web.security.keypair;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * Created by blavenie on 06/01/16.
 */
public class ChallengeMessageStoreImpl implements ChallengeMessageStore, InitializingBean{


    private String prefix;
    private long validityDurationInSeconds;
    private LoadingCache<String, String> chalengeMessageCache;

    public ChallengeMessageStoreImpl() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.prefix, "An not null prefix must be set");
        Assert.isTrue(this.validityDurationInSeconds > 0, "An validityDurationInSeconds must be set (and not equals to zero)");
        this.chalengeMessageCache = initGeneratedMessageCache();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setValidityDurationInSeconds(long validityDurationInSeconds) {
        this.validityDurationInSeconds = validityDurationInSeconds;
    }

    @Override
    public boolean validateChallenge(String challenge) {
        Preconditions.checkArgument(StringUtils.isNotBlank(challenge));

        String storedChallenge = chalengeMessageCache.getIfPresent(challenge);

        // if no value in cache => maybe challenge expired
        return ObjectUtils.equals(storedChallenge, challenge);
    }

    @Override
    public String createNewChallenge() {
        String challenge = newChallenge();
        chalengeMessageCache.put(challenge, challenge);
        return newChallenge();
    }



    /* -- internal methods -- */

    protected String newChallenge() {
        return String.valueOf(prefix + System.currentTimeMillis() * System.currentTimeMillis());
    }


    protected LoadingCache<String, String> initGeneratedMessageCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(validityDurationInSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String challenge) throws Exception {
                        // not used. Filled manually
                        return null;
                    }
                });
    }
}
