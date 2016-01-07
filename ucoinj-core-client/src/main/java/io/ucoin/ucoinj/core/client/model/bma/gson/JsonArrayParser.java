package io.ucoin.ucoinj.core.client.model.bma.gson;

import com.google.gson.JsonParseException;
import io.ucoin.ucoinj.core.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse JSON array content, without deserialize each item.
 *
 * Created by blavenie on 05/01/16.
 */
public class JsonArrayParser {

    enum ParserState {
        READING_OBJECT,
        READING_ARRAY
    }

    public String[] getValuesAsArray(String jsonArray) throws JsonParseException {
        List<String> result = getValuesAsList(jsonArray);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.toArray(new String[result.size()]);
    }

    public List<String> getValuesAsList(String jsonArray) throws JsonParseException {
        ParserState state = ParserState.READING_ARRAY;
        List<String> result = new ArrayList<String>();
        StringBuilder currentObject = null;
        int i = 0;
        int parenthesisBalance = 0;
        for (char c : jsonArray.toCharArray()) {
            switch (c) {
                case '{': {
                    if (state == ParserState.READING_ARRAY) {
                        state = ParserState.READING_OBJECT;
                        currentObject = new StringBuilder();
                    }
                    parenthesisBalance++;
                    currentObject.append(c);
                    break;
                }
                case '}': {
                    if (state == ParserState.READING_ARRAY) {
                        throw new JsonParseException("unexpected '}' at " + i);
                    } else {
                        currentObject.append(c);
                        parenthesisBalance--;
                        if (parenthesisBalance == 0) {
                            state = ParserState.READING_ARRAY;
                            result.add(currentObject.toString());
                        }
                    }
                    break;
                }
                default: {
                    if (state == ParserState.READING_OBJECT) {
                        currentObject.append(c);
                    }
                }
            }
            i++;
        }
        return result;
    }
}
