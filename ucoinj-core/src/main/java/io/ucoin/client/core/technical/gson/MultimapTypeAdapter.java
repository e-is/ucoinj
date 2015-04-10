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


import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MultimapTypeAdapter implements JsonSerializer<Multimap>, JsonDeserializer<Multimap> {

    @Override
    public JsonElement serialize(final Multimap src, final Type typeOfSrc, final JsonSerializationContext context) {
        return context.serialize(src.asMap(), createMapType(typeOfSrc));
    }

    @Override
    public Multimap deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException {
        final Multimap multimap = HashMultimap.create();
        final Map map = context.deserialize(json, createMapType(typeOfT));
        for (final Object key : map.keySet()) {
            final Collection values = (Collection) map.get(key);
            multimap.putAll(key, values);
        }

        return multimap;
    }

    private Type createMapType(final Type multimapType) {
        Preconditions.checkArgument(multimapType instanceof ParameterizedType);
        final ParameterizedType paramType = (ParameterizedType)multimapType;
        final Type[] typeArguments = paramType.getActualTypeArguments();
        Preconditions.checkArgument(2 == typeArguments.length, "Type must contain exactly 2 type arguments.");

        final ParameterizedTypeImpl valueType = new ParameterizedTypeImpl(Collection.class, null, typeArguments[1]);
        final ParameterizedTypeImpl mapType = new ParameterizedTypeImpl(Map.class, null, typeArguments[0], valueType);
        return mapType;
    }

}