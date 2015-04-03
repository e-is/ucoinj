package io.ucoin.client.core.technical.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.google.gson.GsonBuilder;
import io.ucoin.client.core.model.Identity;
import io.ucoin.client.core.model.Member;
import io.ucoin.client.core.technical.gson.IdentityTypeAdapter;
import io.ucoin.client.core.technical.gson.MemberTypeAdapter;
import io.ucoin.client.core.technical.gson.MultimapTypeAdapter;

import java.text.SimpleDateFormat;

/**
 * Created by Benoit on 03/04/2015.
 */
public class JacksonUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static ObjectMapper newObjectMapper() {
        return new ObjectMapper()
                // make sure date will be serialized
                .setDateFormat(new SimpleDateFormat(DATE_PATTERN))
                ;
    }
}
