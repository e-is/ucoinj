package io.ucoin.client.core.technical.gson;

import io.ucoin.client.core.model.Identity;
import io.ucoin.client.core.model.Member;

import com.google.common.collect.Multimap;
import com.google.gson.GsonBuilder;

public class GsonUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    public static GsonBuilder newBuilder() {
        return new GsonBuilder()
                // make sure date will be serialized
                .setDateFormat(DATE_PATTERN)
                // REgister Multimap adapter
                .registerTypeAdapter(Multimap.class, new MultimapTypeAdapter())
                // Register identity adapter
                .registerTypeAdapter(Identity.class, new IdentityTypeAdapter())
                .registerTypeAdapter(Member.class, new MemberTypeAdapter())
                ;
    }
}
