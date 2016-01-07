package io.ucoin.ucoinj.core.client.model.bma.gson;

import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.util.ObjectUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonAttributeParser {

        public static final String REGEX_ATTRIBUTE_STRING_VALUE = "\\\"%s\\\"\\s*:\\s*\"([^\"]+)\\\"";
        public static final String REGEX_ATTRIBUTE_NUMERIC_VALUE = "\\\"%s\\\"\\s*:\\s*([\\d]+(?:[.][\\d]+)?)";

        private Pattern pattern;
        private Pattern numericPattern;
        private DecimalFormat decimalFormat;
        private String attributeName;

        public JsonAttributeParser(String attributeName) {
            ObjectUtils.checkNotNull(attributeName);

            this.attributeName = attributeName;
            this.numericPattern = Pattern.compile(String.format(REGEX_ATTRIBUTE_NUMERIC_VALUE, attributeName));
            this.pattern = Pattern.compile(String.format(REGEX_ATTRIBUTE_STRING_VALUE, attributeName));
            this.decimalFormat = new DecimalFormat();
            this.decimalFormat.getDecimalFormatSymbols().setDecimalSeparator('.');
        }

        public Number getValueAsNumber(String jsonString) {
            ObjectUtils.checkNotNull(jsonString);

            Matcher matcher = numericPattern.matcher(jsonString);

            if (!matcher.find()) {
                return null;
            }
            String group = matcher.group(1);
            try {
                Number result = decimalFormat.parse(group);
                return result;
            } catch (ParseException e) {
                throw new TechnicalException(String.format("Error while parsing json numeric value, for attribute [%s]: %s", attributeName,e.getMessage()), e);
            }
        }

        public int getValueAsInt(String jsonString) {
            Number numberValue = getValueAsNumber(jsonString);
            if (numberValue == null) {
                return 0;
            }
            return numberValue.intValue();
        }

        public String getValueAsString(String jsonString) {
            Matcher matcher = pattern.matcher(jsonString);
            if (!matcher.find()) {
                return null;
            }

            return matcher.group(1);
        }

        public List<String> getValues(String jsonString) {
            Matcher matcher = pattern.matcher(jsonString);
            List<String> result = new ArrayList<>();
            while (matcher.find()) {
                String group = matcher.group(1);
                result.add(group);
            }

            return result;
        }

    }