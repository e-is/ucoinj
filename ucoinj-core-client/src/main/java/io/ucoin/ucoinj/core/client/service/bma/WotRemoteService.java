package io.ucoin.ucoinj.core.client.service.bma;

/*
 * #%L
 * UCoin Java :: Core Client API
 * %%
 * Copyright (C) 2014 - 2016 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
