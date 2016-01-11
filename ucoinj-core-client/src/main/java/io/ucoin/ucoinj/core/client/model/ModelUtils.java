package io.ucoin.ucoinj.core.client.model;

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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ucoin.ucoinj.core.client.model.local.Certification;
import io.ucoin.ucoinj.core.client.model.local.Movement;

/**
 * Helper class on model entities
 * Created by eis on 04/04/15.
 */
public class ModelUtils {

    /**
     * Order certification by cert time (DESC), uid ASC, pubkey (ASC)
     * @return a new comparator
     */
    public static Comparator<Certification> newWotCertificationComparatorByDate() {
        return new Comparator<Certification>() {
            @Override
            public int compare(Certification lhs, Certification rhs) {
                int result = 0;

                // cert time (order DESC)
                long lct = lhs.getTimestamp();
                long rct = rhs.getTimestamp();
                if (lct != rct) {
                    return lct < rct ? 1 : -1;
                }

                // uid
                if (lhs.getUid() != null) {
                    result = lhs.getUid().compareToIgnoreCase(rhs.getUid());
                    if (result != 0) {
                        return result;
                    }
                }
                else if (rhs.getUid() != null) {
                    return 1;
                }

                // pub key
                if (lhs.getPubkey() != null) {
                    result = lhs.getPubkey().compareToIgnoreCase(rhs.getPubkey());
                    if (result != 0) {
                        return result;
                    }
                }
                else if (rhs.getPubkey() != null) {
                    return 1;
                }
                return 0;
            }
        };
    }


    /**
     * Order certification by uid (ASC), pubkey (ASC), cert time (DESC)
     * @return a new comparator
     */
    public static Comparator<Certification> newWotCertificationComparatorByUid() {
        return new Comparator<Certification>() {
            @Override
            public int compare(Certification lhs, Certification rhs) {
                int result = 0;
                // uid
                if (lhs.getUid() != null) {
                    result = lhs.getUid().compareToIgnoreCase(rhs.getUid());
                    if (result != 0) {
                        return result;
                    }
                }
                else if (rhs.getUid() != null) {
                    return 1;
                }

                // pub key
                if (lhs.getPubkey() != null) {
                    result = lhs.getPubkey().compareToIgnoreCase(rhs.getPubkey());
                    if (result != 0) {
                        return result;
                    }
                }
                else if (rhs.getPubkey() != null) {
                    return 1;
                }

                // cert time (order DESC)
                long lct = lhs.getTimestamp();
                long rct = rhs.getTimestamp();
                return lct < rct ? 1 : (lct == rct ? 0 : -1);
            }
        };
    }

    /**
     * Transform a list of sources, into a Map, using the fingerprint as key
     * @param movements
     * @return
     */
    public static Map<String, Movement> movementsToFingerprintMap(List<Movement> movements) {

        Map<String, Movement> result = new HashMap<>();
        for(Movement movement: movements) {
            result.put(movement.getFingerprint(), movement);
        }

        return result;
    }

    /**
     * Return a small string, for the given pubkey.
     * @param pubkey
     * @return
     */
    public static String minifyPubkey(String pubkey) {
        if (pubkey == null || pubkey.length() < 6) {
            return pubkey;
        }
        return pubkey.substring(0, 6);
    }
}
