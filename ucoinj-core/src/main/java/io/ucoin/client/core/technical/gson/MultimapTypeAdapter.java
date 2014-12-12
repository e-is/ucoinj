package io.ucoin.client.core.technical.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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