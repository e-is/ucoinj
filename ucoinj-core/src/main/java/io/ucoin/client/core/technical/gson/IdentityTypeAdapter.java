package io.ucoin.client.core.technical.gson;

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


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.ucoin.client.core.model.Identity;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

public class IdentityTypeAdapter implements JsonDeserializer<Identity>{

    @Override
    public Identity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String identityStr = json.getAsString();
        if (StringUtils.isBlank(identityStr)) {
            return null;
        }
        
        String[] identityParts = identityStr.split(":");
        
        Identity result = new Identity();
        int i = 0;
        
        result.setPubkey(identityParts[i++]);
        result.setSignature(identityParts[i++]);
        result.setTimestamp(Integer.parseInt(identityParts[i++]));
        result.setUid(identityParts[i++]);

        return result;
    }
}
