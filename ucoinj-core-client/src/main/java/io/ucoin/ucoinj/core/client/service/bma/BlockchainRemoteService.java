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
import io.ucoin.ucoinj.core.client.model.local.Identity;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainBlock;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainMemberships;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainParameters;
import io.ucoin.ucoinj.core.client.model.local.Currency;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.model.local.Wallet;
import io.ucoin.ucoinj.core.client.service.exception.PubkeyAlreadyUsedException;
import io.ucoin.ucoinj.core.client.service.exception.UidAlreadyUsedException;
import io.ucoin.ucoinj.core.client.service.exception.UidMatchAnotherPubkeyException;

import java.util.Map;

public interface BlockchainRemoteService extends Service {

    /**
     * get the blockchain parameters (currency parameters)
     *
     * @param currencyId
     * @param useCache
     * @return
     */
    BlockchainParameters getParameters(long currencyId, boolean useCache);

    /**
     * get the blockchain parameters (currency parameters)
     *
     * @param currencyId
     * @return
     */
    BlockchainParameters getParameters(long currencyId);

    /**
     * get the blockchain parameters (currency parameters)
     *
     * @param peer the peer to use for request
     * @return
     */
    BlockchainParameters getParameters(Peer peer);

    /**
     * Retrieve a block, by id (from 0 to current)
     *
     * @param currencyId
     * @param number
     * @return
     */
    BlockchainBlock getBlock(long currencyId, long number);

    /**
     * Retrieve the dividend of a block, by id (from 0 to current).
     * Usefull method to avoid to deserialize all the block
     *
     * @param currencyId
     * @param number
     * @return
     */
    Long getBlockDividend(long currencyId, long number);

    /**
     * Retrieve a block, by id (from 0 to current)
     *
     * @param peer   the peer to use for request
     * @param number the block number
     * @return
     */
    BlockchainBlock getBlock(Peer peer, int number);

    /**
     * Retrieve a block, by id (from 0 to current) as JSON string
     *
     * @param peer   the peer to use for request
     * @param number the block number
     * @return
     */
    String getBlockAsJson(Peer peer, int number);

    /**
     * Retrieve a block, by id (from 0 to current) as JSON string
     *
     * @param peer   the peer to use for request
     * @param number the block number
     * @return
     */
    String[] getBlocksAsJson(Peer peer, int count, int from);

    /**
     * Retrieve the current block (with short cache)
     *
     * @return
     */
    BlockchainBlock getCurrentBlock(long currencyId, boolean useCache);

    /**
     * Retrieve the current block
     *
     * @return
     */
    BlockchainBlock getCurrentBlock(long currencyId);

    /**
     * Retrieve the current block
     *
     * @param peer the peer to use for request
     * @return the last block
     */
    BlockchainBlock getCurrentBlock(Peer peer);

    /**
     * Retrieve the currency data, from peer
     *
     * @param peer
     * @return
     */
    Currency getCurrencyFromPeer(Peer peer);

    BlockchainParameters getBlockchainParametersFromPeer(Peer peer);

    /**
     * Retrieve the last block with UD
     *
     * @param currencyId id of currency
     * @return
     */
    long getLastUD(long currencyId);

    /**
     * Retrieve the last block with UD, from a peer
     *
     * @param currencyId id of currency
     * @return
     */
    long getLastUD(Peer peer);

    /**
     * Check is a identity is not already used by a existing member
     *
     * @param peer
     * @param identity
     * @throws UidAlreadyUsedException    if UID already used by another member
     * @throws PubkeyAlreadyUsedException if pubkey already used by another member
     */
    void checkNotMemberIdentity(Peer peer, Identity identity) throws UidAlreadyUsedException, PubkeyAlreadyUsedException;

    /**
     * Check is a wallet is a member, and load its attribute isMember and certTimestamp
     *
     * @param peer
     * @param wallet
     * @throws UidMatchAnotherPubkeyException is uid already used by another pubkey
     */
    void loadAndCheckMembership(Peer peer, Wallet wallet) throws UidMatchAnotherPubkeyException;

    /**
     * Load identity attribute isMember and timestamp
     *
     * @param identity
     */
    void loadMembership(long currencyId, Identity identity, boolean checkLookupForNonMember);


    BlockchainMemberships getMembershipByUid(long currencyId, String uid);

    BlockchainMemberships getMembershipByPublicKey(long currencyId, String pubkey);

    /**
     * Request to integrate the wot
     */
    void requestMembership(Wallet wallet);

    BlockchainMemberships getMembershipByPubkeyOrUid(long currencyId, String uidOrPubkey);

    BlockchainMemberships getMembershipByPubkeyOrUid(Peer peer, String uidOrPubkey);

    String getMembership(Wallet wallet,
                                BlockchainBlock block,
                                boolean sideIn);

    /**
     * Get UD, by block number
     *
     * @param currencyId
     * @param startOffset
     * @return
     */
    Map<Integer, Long> getUDs(long currencyId, long startOffset);

}