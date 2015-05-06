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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.GsonBuilder;
import io.ucoin.client.core.model.Identity;
import io.ucoin.client.core.model.Member;
import io.ucoin.client.core.technical.UCoinTechnicalException;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GsonUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String REGEX_ATTRIBUTE_STRING_VALUE = "\\\"%s\\\"\\s*:\\s*\"([^\"]+)\\\"";

    public static final String REGEX_ATTRIBUTE_NUMERIC_VALUE = "\\\"%s\\\"\\s*:\\s*([\\d]+(?:[.][\\d]+)?)";

    public static GsonBuilder newBuilder() {
        return new GsonBuilder()
                // make sure date will be serialized
                .setDateFormat(DATE_PATTERN)
                // Register Multimap adapter
                .registerTypeAdapter(Multimap.class, new MultimapTypeAdapter())
                // Register identity adapter
                .registerTypeAdapter(Identity.class, new IdentityTypeAdapter())
                .registerTypeAdapter(Member.class, new MemberTypeAdapter())
                ;
    }

    public static List<String> getValuesFromJSONAsString(String jsonString, String attributeName) {
        Pattern pattern = Pattern.compile(String.format(REGEX_ATTRIBUTE_STRING_VALUE, attributeName));
        Matcher matcher = pattern.matcher(jsonString);
        List<String> result = Lists.newArrayList();
        while (matcher.find()) {
            String group = matcher.group(1);
            result.add(group);
        }

        return result;
    }

    public static String getValueFromJSONAsString(String jsonString, String attributeName) {
        Pattern pattern = Pattern.compile(String.format(REGEX_ATTRIBUTE_STRING_VALUE, attributeName));
        Matcher matcher = pattern.matcher(jsonString);
        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1);
    }

    public static Number getValueFromJSONAsNumber(String jsonString, String attributeName) {
        Preconditions.checkNotNull(jsonString);
        Preconditions.checkNotNull(attributeName);

        Pattern pattern = Pattern.compile(String.format(REGEX_ATTRIBUTE_NUMERIC_VALUE, attributeName));
        Matcher matcher = pattern.matcher(jsonString);

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.getDecimalFormatSymbols().setDecimalSeparator('.');

        if (!matcher.find()) {
            return null;
        }
        String group = matcher.group(1);
        try {
            Number result = decimalFormat.parse(group);
            return result;
        } catch (ParseException e) {
            throw new UCoinTechnicalException(String.format("Error while parsing json numeric value, for attribute [%s]: %s", attributeName,e.getMessage()), e);
        }

    }

    public static int getValueFromJSONAsInt(String jsonString, String attributeName) {
        Number numberValue = getValueFromJSONAsNumber(jsonString, attributeName);
        if (numberValue == null) {
            return 0;
        }
        return numberValue.intValue();
    }
}
