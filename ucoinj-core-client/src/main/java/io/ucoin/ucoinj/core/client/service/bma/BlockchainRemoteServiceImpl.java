package io.ucoin.ucoinj.core.client.service.bma;
import io.ucoin.ucoinj.core.client.config.Configuration;
import io.ucoin.ucoinj.core.client.model.bma.gson.JsonArrayParser;
import io.ucoin.ucoinj.core.client.model.local.Currency;
import io.ucoin.ucoinj.core.client.model.local.Identity;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.model.local.Wallet;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainBlock;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainMemberships;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainParameters;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import io.ucoin.ucoinj.core.client.service.exception.HttpBadRequestException;
import io.ucoin.ucoinj.core.client.service.exception.PubkeyAlreadyUsedException;
import io.ucoin.ucoinj.core.client.service.exception.UidAlreadyUsedException;
import io.ucoin.ucoinj.core.client.service.exception.UidMatchAnotherPubkeyException;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.service.CryptoService;
import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.core.util.cache.Cache;
import io.ucoin.ucoinj.core.util.cache.SimpleCache;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockchainRemoteServiceImpl extends BaseRemoteServiceImpl implements BlockchainRemoteService {

    private static final Logger log = LoggerFactory.getLogger(BlockchainRemoteServiceImpl.class);

    private static final String JSON_DIVIDEND_ATTR = "\"dividend\":";

    public static final String URL_BASE = "/blockchain";

    public static final String URL_PARAMETERS = URL_BASE + "/parameters";

    public static final String URL_BLOCK = URL_BASE + "/block/%s";

    public static final String URL_BLOCKS_FROM = URL_BASE + "/blocks/%s/%s";

    public static final String URL_BLOCK_CURRENT = URL_BASE + "/current";

    public static final String URL_BLOCK_WITH_UD = URL_BASE + "/with/ud";

    public static final String URL_MEMBERSHIP = URL_BASE + "/membership";

    public static final String URL_MEMBERSHIP_SEARCH = URL_BASE + "/memberships/%s";


    private NetworkRemoteService networkRemoteService;

    private Configuration config;

    // Cache need for wallet refresh : iteration on wallet should not
    // execute a download of the current block
    private Cache<Long, BlockchainBlock> mCurrentBlockCache;

    // Cache on blockchain parameters
    private Cache<Long, BlockchainParameters> mParametersCache;

    public BlockchainRemoteServiceImpl() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        networkRemoteService = ServiceLocator.instance().getNetworkRemoteService();
        config = Configuration.instance();

        // Initialize caches
        initCaches();
    }

    @Override
    public BlockchainParameters getParameters(long currencyId, boolean useCache) {
        if (!useCache) {
            return getParameters(currencyId);
        } else {
            return mParametersCache.get(currencyId);
        }
    }

    @Override
    public BlockchainParameters getParameters(long currencyId) {
        // get blockchain parameter
        BlockchainParameters result = executeRequest(currencyId, URL_PARAMETERS, BlockchainParameters.class);
        return result;
    }

    @Override
    public BlockchainParameters getParameters(Peer peer) {
        // get blockchain parameter
        BlockchainParameters result = executeRequest(peer, URL_PARAMETERS, BlockchainParameters.class);
        return result;
    }

    @Override
    public BlockchainBlock getBlock(long currencyId, long number) {
        // get blockchain parameter
        String path = String.format(URL_BLOCK, number);
        BlockchainBlock result = executeRequest(currencyId, path, BlockchainBlock.class);
        return result;
    }

    @Override
    public Long getBlockDividend(long currencyId, long number) {
        // get blockchain parameter
        String path = String.format(URL_BLOCK, number);
        String json = executeRequest(currencyId, path, String.class);
        return getDividendFromBlockJson(json);
    }


    @Override
    public BlockchainBlock getBlock(Peer peer, int number) {
        // get blockchain parameter
        String path = String.format(URL_BLOCK, number);
        BlockchainBlock result = executeRequest(peer, path, BlockchainBlock.class);
        return result;
    }

    @Override
    public String getBlockAsJson(Peer peer, int number) {
        // get blockchain parameter
        String path = String.format(URL_BLOCK, number);
        return executeRequest(peer, path, String.class);
    }

    @Override
    public String[] getBlocksAsJson(Peer peer, int count, int from) {
        // get blockchain parameter
        String path = String.format(URL_BLOCKS_FROM, count, from);
        String jsonBlocksStr = executeRequest(peer, path, String.class);

        // Parse only array content, but deserialize array item
        JsonArrayParser parser = new JsonArrayParser();
        return parser.getValuesAsArray(jsonBlocksStr);
    }

    /**
     * Retrieve the current block (with short cache)
     *
     * @return
     */
    public BlockchainBlock getCurrentBlock(long currencyId, boolean useCache) {
        if (!useCache) {
            return getCurrentBlock(currencyId);
        } else {
            return mCurrentBlockCache.get(currencyId);
        }
    }

    @Override
    public BlockchainBlock getCurrentBlock(long currencyId) {
        // get blockchain parameter
        BlockchainBlock result = executeRequest(currencyId, URL_BLOCK_CURRENT, BlockchainBlock.class);
        return result;
    }

    @Override
    public BlockchainBlock getCurrentBlock(Peer peer) {
        // get blockchain parameter
        BlockchainBlock result = executeRequest(peer, URL_BLOCK_CURRENT, BlockchainBlock.class);
        return result;
    }

    @Override
    public Currency getCurrencyFromPeer(Peer peer) {
        BlockchainParameters parameter = getParameters(peer);
        BlockchainBlock firstBlock = getBlock(peer, 0);
        BlockchainBlock lastBlock = getCurrentBlock(peer);

        Currency result = new Currency();
        result.setCurrencyName(parameter.getCurrency());
        result.setFirstBlockSignature(firstBlock.getSignature());
        result.setMembersCount(lastBlock.getMembersCount());
        result.setLastUD(parameter.getUd0());

        return result;
    }

    @Override
    public BlockchainParameters getBlockchainParametersFromPeer(Peer peer){
        return getParameters(peer);
    }

    @Override
    public long getLastUD(long currencyId) {
        // get block number with UD
        String blocksWithUdResponse = executeRequest(currencyId, URL_BLOCK_WITH_UD, String.class);
        Integer blockNumber = getLastBlockNumberFromJson(blocksWithUdResponse);

        // If no result (this could happen when no UD has been send
        if (blockNumber == null) {
            // get the first UD from currency parameter
            BlockchainParameters parameter = getParameters(currencyId);
            return parameter.getUd0();
        }

        // Get the UD from the last block with UD
        Long lastUD = getBlockDividend(currencyId, blockNumber);

        // Check not null (should never append)
        if (lastUD == null) {
            throw new TechnicalException("Unable to get last UD from server");
        }
        return lastUD.longValue();
    }

    @Override
    public long getLastUD(Peer peer) {
        // get block number with UD
        String blocksWithUdResponse = executeRequest(peer, URL_BLOCK_WITH_UD, String.class);
        Integer blockNumber = getLastBlockNumberFromJson(blocksWithUdResponse);

        // If no result (this could happen when no UD has been send
        if (blockNumber == null) {
            // get the first UD from currency parameter
            BlockchainParameters parameter = getParameters(peer);
            return parameter.getUd0();
        }

        // Get the UD from the last block with UD
        String path = String.format(URL_BLOCK, blockNumber);
        String json = executeRequest(peer, path, String.class);
        Long lastUD = getDividendFromBlockJson(json);

        // Check not null (should never append)
        if (lastUD == null) {
            throw new TechnicalException("Unable to get last UD from server");
        }
        return lastUD.longValue();
    }

    /**
     * Check is a identity is not already used by a existing member
     *
     * @param peer
     * @param identity
     * @throws UidAlreadyUsedException    if UID already used by another member
     * @throws PubkeyAlreadyUsedException if pubkey already used by another member
     */
    public void checkNotMemberIdentity(Peer peer, Identity identity) throws UidAlreadyUsedException, PubkeyAlreadyUsedException {
        ObjectUtils.checkNotNull(peer);
        ObjectUtils.checkNotNull(identity);
        ObjectUtils.checkArgument(StringUtils.isNotBlank(identity.getUid()));
        ObjectUtils.checkArgument(StringUtils.isNotBlank(identity.getPubkey()));

        // Read membership data from the UID
        BlockchainMemberships result = getMembershipByPubkeyOrUid(peer, identity.getUid());

        // uid already used by another member
        if (result != null) {
            throw new UidAlreadyUsedException(String.format("User identifier '%s' is already used by another member", identity.getUid()));
        }

        result = getMembershipByPubkeyOrUid(peer, identity.getPubkey());

        // pubkey already used by another member
        if (result != null) {
            throw new PubkeyAlreadyUsedException(String.format("Pubkey key '%s' is already used by another member", identity.getPubkey()));
        }
    }

    /**
     * Check is a wallet is a member, and load its attribute isMember and certTimestamp
     *
     * @param wallet
     * @throws UidMatchAnotherPubkeyException is uid already used by another pubkey
     */
    public void loadAndCheckMembership(Peer peer, Wallet wallet) throws UidMatchAnotherPubkeyException {
        ObjectUtils.checkNotNull(wallet);

        // Load membership data
        loadMembership(null, peer, wallet.getIdentity(), true);

        // Something wrong on pubkey : uid already used by another pubkey !
        if (wallet.getIdentity().getIsMember() == null) {
            throw new UidMatchAnotherPubkeyException(wallet.getPubKeyHash());
        }
    }

    /**
     * Load identity attribute isMember and timestamp
     *
     * @param identity
     */
    public void loadMembership(long currencyId, Identity identity, boolean checkLookupForNonMember) {
        loadMembership(currencyId, null, identity, checkLookupForNonMember);
    }


    public BlockchainMemberships getMembershipByUid(long currencyId, String uid) {
        ObjectUtils.checkArgument(StringUtils.isNotBlank(uid));

        BlockchainMemberships result = getMembershipByPubkeyOrUid(currencyId, uid);
        if (result == null || !uid.equals(result.getUid())) {
            return null;
        }
        return result;
    }

    public BlockchainMemberships getMembershipByPublicKey(long currencyId, String pubkey) {
        ObjectUtils.checkArgument(StringUtils.isNotBlank(pubkey));

        BlockchainMemberships result = getMembershipByPubkeyOrUid(currencyId, pubkey);
        if (result == null || !pubkey.equals(result.getPubkey())) {
            return null;
        }
        return result;
    }

    /**
     * Request to integrate the wot
     */
    public void requestMembership(Wallet wallet) {
        ObjectUtils.checkNotNull(wallet);
        ObjectUtils.checkNotNull(wallet.getCurrencyId());

        BlockchainBlock block = getCurrentBlock(wallet.getCurrencyId());

        // Compute membership document
        String membership = getMembership(wallet,
                block,
                true /*sideIn*/);

        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "Will send membership document: \n------\n%s------",
                    membership));
        }

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("membership", membership));

        HttpPost httpPost = new HttpPost(getPath(wallet.getCurrencyId(), URL_MEMBERSHIP));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            throw new TechnicalException(e);
        }

        String membershipResult = executeRequest(httpPost, String.class);
        if (log.isDebugEnabled()) {
            log.debug("received from /tx/process: " + membershipResult);
        }

        executeRequest(httpPost, String.class);
    }

    public BlockchainMemberships getMembershipByPubkeyOrUid(long currencyId, String uidOrPubkey) {
        String path = String.format(URL_MEMBERSHIP_SEARCH, uidOrPubkey);

        // search blockchain membership
        try {
            BlockchainMemberships result = executeRequest(currencyId, path, BlockchainMemberships.class);
            return result;
        } catch (HttpBadRequestException e) {
            log.debug("No member matching this pubkey or uid: " + uidOrPubkey);
            return null;
        }
    }

    public BlockchainMemberships getMembershipByPubkeyOrUid(Peer peer, String uidOrPubkey) {
        String path = String.format(URL_MEMBERSHIP_SEARCH, uidOrPubkey);

        // search blockchain membership
        try {
            BlockchainMemberships result = executeRequest(peer, path, BlockchainMemberships.class);
            return result;
        } catch (HttpBadRequestException e) {
            log.debug("No member matching this pubkey or uid: " + uidOrPubkey);
            return null;
        }
    }

    public String getMembership(Wallet wallet,
                                BlockchainBlock block,
                                boolean sideIn
    ) {

        // Create the member ship document
        String membership = getMembership(wallet.getUid(),
                wallet.getPubKeyHash(),
                wallet.getCurrency(),
                block.getNumber(),
                block.getHash(),
                sideIn,
                wallet.getCertTimestamp()
        );

        // Add signature
        CryptoService cryptoService = ServiceLocator.instance().getCryptoService();
        String signature = cryptoService.sign(membership, wallet.getSecKey());

        return new StringBuilder().append(membership).append(signature)
                .append('\n').toString();
    }

    /**
     * Get UD, by block number
     *
     * @param currencyId
     * @param startOffset
     * @return
     */
    public Map<Integer, Long> getUDs(long currencyId, long startOffset) {
        log.debug(String.format("Getting block's UD from block [%s]", startOffset));

        int[] blockNumbersWithUD = getBlocksWithUD(currencyId);

        Map<Integer, Long> result = new LinkedHashMap<Integer,Long>();

//         Insert the UD0 (if need)
//        if (startOffset <= 0) {
//            BlockchainParameters parameters = getParameters(currencyId, true/*with cache*/);
//            result.put(0, parameters.getUd0());
//        }

        boolean previousBlockInsert = false;
        if (blockNumbersWithUD != null && blockNumbersWithUD.length != 0) {
            Integer previousBlockNumberWithUd = null;
            for (Integer blockNumber : blockNumbersWithUD) {
                if (blockNumber >= startOffset) {
                    if(!previousBlockInsert){
                        Long previousUd = getParameters(currencyId, true/*with cache*/).getUd0();
                        Integer previousBlockNumber = 0;
                        if(previousBlockNumberWithUd!=null){
                            previousUd = getBlockDividend(currencyId, previousBlockNumberWithUd);
                            if (previousUd == null) {
                                throw new TechnicalException(
                                        String.format("Unable to get UD from server block [%s]",
                                                previousBlockNumberWithUd)
                                );
                            }
                            previousBlockNumber = previousBlockNumberWithUd;
                        }
                        result.put(previousBlockNumber, previousUd);
                        previousBlockInsert = true;
                    }
                    Long ud = getBlockDividend(currencyId, blockNumber);
                    // Check not null (should never append)
                    if (ud == null) {
                        throw new TechnicalException(String.format("Unable to get UD from server block [%s]", blockNumber));
                    }
                    result.put(blockNumber, ud);
                }else{
                    previousBlockNumberWithUd = blockNumber;
                }
            }
        }else{
            result.put(0, getParameters(currencyId, true/*with cache*/).getUd0());
        }

        return result;
    }

    /* -- Internal methods -- */

    /**
     * Initialize caches
     */
    protected void initCaches() {
        int cacheTimeInMillis = config.getNetworkCacheTimeInMillis();

        mCurrentBlockCache = new SimpleCache<Long, BlockchainBlock>(cacheTimeInMillis) {
            @Override
            public BlockchainBlock load(Long currencyId) {
                return getCurrentBlock(currencyId);
            }
        };

        mParametersCache = new SimpleCache<Long, BlockchainParameters>(/*eternal cache*/) {
            @Override
            public BlockchainParameters load(Long currencyId) {
                return getParameters(currencyId);
            }
        };
    }


    protected void loadMembership(Long currencyId, Peer peer, Identity identity, boolean checkLookupForNonMember) {
        ObjectUtils.checkNotNull(identity);
        ObjectUtils.checkArgument(StringUtils.isNotBlank(identity.getUid()));
        ObjectUtils.checkArgument(StringUtils.isNotBlank(identity.getPubkey()));
        ObjectUtils.checkArgument(peer != null || currencyId != null);

        // Read membership data from the UID
        BlockchainMemberships result = peer != null
                ? getMembershipByPubkeyOrUid(peer, identity.getUid())
                : getMembershipByPubkeyOrUid(currencyId, identity.getUid());

        // uid not used = not was member
        if (result == null) {
            identity.setMember(false);

            if (checkLookupForNonMember) {
                WotRemoteService wotService = ServiceLocator.instance().getWotRemoteService();
                Identity lookupIdentity = peer != null
                        ? wotService.getIdentity(peer, identity.getUid(), identity.getPubkey())
                        : wotService.getIdentity(currencyId, identity.getUid(), identity.getPubkey());

                // Self certification exists, update the cert timestamp
                if (lookupIdentity != null) {
                    identity.setTimestamp(lookupIdentity.getTimestamp());
                }

                // Self certification not exists: make sure the cert time is cleaning
                else {
                    identity.setTimestamp(-1);
                }
            }
        }

        // UID and pubkey is a member: fine
        else if (identity.getPubkey().equals(result.getPubkey())) {
            identity.setMember(true);
            identity.setTimestamp(result.getSigDate());
        }

        // Something wrong on pubkey : uid already used by anither pubkey !
        else {
            identity.setMember(null);
        }

    }

    private int[] getBlocksWithUD(long currencyId) {
        log.debug("Getting blocks with UD");

        String json = executeRequest(currencyId, URL_BLOCK_WITH_UD, String.class);



        int startIndex = json.indexOf("[");
        int endIndex = json.lastIndexOf(']');

        if (startIndex == -1 || endIndex == -1) {
            return null;
        }

        String blockNumbersStr = json.substring(startIndex + 1, endIndex).trim();

        if (StringUtils.isBlank(blockNumbersStr)) {
            return null;
        }


        String[] blockNumbers = blockNumbersStr.split(",");
        int[] result = new int[blockNumbers.length];
        try {
            int i=0;
            for (String blockNumber : blockNumbers) {
                result[i++] = Integer.parseInt(blockNumber.trim());
            }
        }
        catch(NumberFormatException e){
            if (log.isDebugEnabled()) {
                log.debug(String.format("Bad format of the response '%s'.", URL_BLOCK_WITH_UD));
            }
            throw new TechnicalException("Unable to read block with UD numbers: " + e.getMessage(), e);
        }

        return result;
    }

    private String getMembership(String uid,
                                 String publicKey,
                                 String currency,
                                 long blockNumber,
                                 String blockHash,
                                 boolean sideIn,
                                 long certificationTime
    ) {
        StringBuilder result = new StringBuilder()
                .append("Version: 1\n")
                .append("Type: Membership\n")
                .append("Currency: ").append(currency).append('\n')
                .append("Issuer: ").append(publicKey).append('\n')
                .append("Block: ").append(blockNumber).append('-').append(blockHash).append('\n')
                .append("Membership: ").append(sideIn ? "IN" : "OUT").append('\n')
                .append("UserID: ").append(uid).append('\n')
                .append("CertTS: ").append(certificationTime).append('\n');

        return result.toString();
    }

    private Integer getLastBlockNumberFromJson(final String json) {

        int startIndex = json.lastIndexOf(',');
        int endIndex = json.lastIndexOf(']');
        if (startIndex == -1 || endIndex == -1) {
            return null;
        }

        String blockNumberStr = json.substring(startIndex+1,endIndex).trim();
        try {
            return Integer.parseInt(blockNumberStr);
        } catch(NumberFormatException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not parse JSON (block numbers)");
            }
            throw new TechnicalException("Could not parse server response");
        }
    }


    protected Long getDividendFromBlockJson(String blockJson) {

        int startIndex = blockJson.indexOf(JSON_DIVIDEND_ATTR);
        if (startIndex == -1) {
            return null;
        }
        startIndex += JSON_DIVIDEND_ATTR.length();
        int endIndex = blockJson.indexOf(',', startIndex);
        if (endIndex == -1) {
            return null;
        }

        String dividendStr = blockJson.substring(startIndex, endIndex).trim();
        if (dividendStr.length() == 0
                || "null".equals(dividendStr)) {
            return null;
        }

        return Long.parseLong(dividendStr);
    }

}
