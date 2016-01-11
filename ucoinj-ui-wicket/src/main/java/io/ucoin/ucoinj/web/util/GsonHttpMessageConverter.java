package io.ucoin.ucoinj.web.util;

/*
 * #%L
 * Reef DB :: Quadrige2 Synchro server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2015 Ifremer
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


import io.ucoin.ucoinj.core.client.model.bma.gson.GsonUtils;

/**
 * Override default converter to use specific gson configuration :<ul>
 *     <li>multimap support</li>
 *     <li>date pattern</li>
 *     <li>...</li>
 * </ul>
 * @see fr.ifremer.quadrige2.core.dao.technical.gson.GsonUtils
 */
public class GsonHttpMessageConverter extends org.springframework.http.converter.json.GsonHttpMessageConverter {

    public GsonHttpMessageConverter() {
        super();
        setGson(GsonUtils.newBuilder().create());
    }

}
