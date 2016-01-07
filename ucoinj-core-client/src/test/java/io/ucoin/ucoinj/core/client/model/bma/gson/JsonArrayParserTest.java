package io.ucoin.ucoinj.core.client.model.bma.gson;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by blavenie on 05/01/16.
 */
public class JsonArrayParserTest {

    @Test
    public void getValues() {
        String obj = "{'id':'joe','ts':'2014-12-02T13:58:23.801+0100','foo':{'bar':{'v1':50019820,'v2':0,     'v3':0.001, 'v4':-100, 'v5':0.000001, 'v6':0.0, 'b':true}}}".replace("'", "\"");
        String string = String.format("[%s,%s,%s,%s]", obj , obj , obj , obj );

        JsonArrayParser parser = new JsonArrayParser();
        String[] result = parser.getValuesAsArray(string);

        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.length);
        Assert.assertEquals(obj, result[0]);

        result = parser.getValuesAsArray("[]");
        Assert.assertNull(result);
    }
}
