package io.ucoin.ucoinj.core.client.service.bma;

import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.client.model.bma.WotCertification;
import io.ucoin.ucoinj.core.client.model.bma.WotLookup;
import io.ucoin.ucoinj.core.client.model.local.Certification;
import io.ucoin.ucoinj.core.client.model.local.Identity;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.model.local.Wallet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface WotRemoteService extends Service {

    List<Identity> findIdentities(Set<Long> currenciesIds, String uidOrPubKey);

    WotLookup.Uid find(long currencyId, String uidOrPubKey);

    void getRequirments(long currencyId, String pubKey);

    WotLookup.Uid findByUid(long currencyId, String uid);

    WotLookup.Uid findByUidAndPublicKey(long currencyId, String uid, String pubKey);

    WotLookup.Uid findByUidAndPublicKey(Peer peer, String uid, String pubKey);

    Identity getIdentity(long currencyId, String uid, String pubKey);

    Identity getIdentity(long currencyId, String pubKey);

    Identity getIdentity(Peer peer, String uid, String pubKey);

    Collection<Certification> getCertifications(long currencyId, String uid, String pubkey, boolean isMember);

    WotCertification getCertifiedBy(long currencyId, String uid);

    int countValidCertifiers(long currencyId, String pubkey);
    
    WotCertification getCertifiersOf(long currencyId, String uid);

    String getSelfCertification(byte[] secKey, String uid, long timestamp);

    void sendSelf(long currencyId, byte[] pubKey, byte[] secKey, String uid, long timestamp);

    String getCertification(byte[] pubKey, byte[] secKey, String userUid,
                                   long userTimestamp,
                                   String userSignature,
                                   int blockNumber,
                                   String blockHash);

    String sendCertification(Wallet wallet, Identity identity);

    String sendCertification(long currencyId,
                                    byte[] pubKey, byte[] secKey,
                                  String uid, long timestamp,
                                  String userUid, String userPubKeyHash,
                                  long userTimestamp, String userSignature);

}
