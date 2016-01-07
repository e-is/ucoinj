package io.ucoin.ucoinj.core.client.model.bma.gson;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
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


import com.google.gson.*;
import io.ucoin.ucoinj.core.client.model.Member;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainBlock;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

public class JoinerTypeAdapter implements JsonDeserializer<BlockchainBlock.Joiner>, JsonSerializer<BlockchainBlock.Joiner>{

    @Override
    public BlockchainBlock.Joiner deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String identityStr = json.getAsString();
        if (StringUtils.isBlank(identityStr)) {
            return null;
        }
        
        String[] identityParts = identityStr.split(":");
        if (identityParts.length != 6) {
            throw new JsonParseException(String.format("Bad format for BlockchainBlock.Identity. Should have 6 parts, but found %s.", identityParts.length));
        }

        BlockchainBlock.Joiner result = new BlockchainBlock.Joiner();
        int i = 0;
        
        result.setPubkey(identityParts[i++]);
        result.setSignature(identityParts[i++]);
        result.setNumber(identityParts[i++]);
        result.setHash(identityParts[i++]);
        result.setTimestamp(Integer.parseInt(identityParts[i++]));
        result.setUid(identityParts[i++]);

        return result;
    }

    @Override
    public JsonElement serialize(BlockchainBlock.Joiner member, Type type, JsonSerializationContext context) {
        String result = new StringBuilder()
                .append(member.getPubkey()).append(":")
                .append(member.getSignature()).append(":")
                .append(member.getNumber()).append(":")
                .append(member.getHash()).append(":")
                .append(member.getTimestamp()).append(":")
                .append(member.getUid()).toString();

        return context.serialize(result.toString(), String.class);
    }
}
