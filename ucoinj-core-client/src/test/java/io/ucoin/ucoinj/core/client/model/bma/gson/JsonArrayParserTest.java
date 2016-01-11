package io.ucoin.ucoinj.core.client.model.bma.gson;

/*
 * #%L
 * UCoin Java :: Core Client API
 * %%
 * Copyright (C) 2014 - 2016 EIS
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
