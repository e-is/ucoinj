package io.ucoin.client.ui.service.rest;

/*
 * #%L
 * UCoin Java Client :: Web
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


import com.fasterxml.jackson.databind.ObjectMapper;
import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.service.CryptoService;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.service.search.CurrencyIndexerService;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import io.ucoin.client.core.technical.crypto.CryptoUtils;
import io.ucoin.client.core.technical.jackson.JacksonUtils;
import io.ucoin.client.ui.application.UcoinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.PathParam;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.JacksonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.mimetypes.RestMimeTypes;
import org.wicketstuff.rest.contenthandling.webserialdeserial.TextualWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import java.io.IOException;

@ResourcePath("/rest/currency")
public class CurrencyRestService extends AbstractRestResource<TextualWebSerialDeserial> {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(CurrencyRestService.class);

    private static final long serialVersionUID = 1L;
    
    private boolean debug;
    private ObjectMapper objectMapper;
    private static ObjectMapper staticObjectMapper;

    public CurrencyRestService() {
        super(new TextualWebSerialDeserial("UTF-8", RestMimeTypes.APPLICATION_JSON,
                new JacksonObjectSerialDeserial(initObjectMapper())));
        debug = log.isDebugEnabled();
        objectMapper = staticObjectMapper;
        staticObjectMapper = null;
    }

    protected static ObjectMapper initObjectMapper() {
        staticObjectMapper = JacksonUtils.newObjectMapper();
        return staticObjectMapper;
    }

    @MethodMapping("/{currencyId}")
    public Currency getCurrencyById(@PathParam("currencyId") String currencyId) {
        CurrencyIndexerService service = ServiceLocator.instance().getCurrencyIndexerService();
        return service.getCurrencyById(currencyId);
    }

    @MethodMapping(value = "/add", httpMethod = HttpMethod.POST)
    public void saveCurrency(@PathParam("pubkey") String pubkey, @PathParam("currency") String jsonCurrency, @PathParam("sig") String signature) throws InvalidSignatureException {

        // Check the signature
        CryptoService cryptoService = ServiceLocator.instance().getCryptoService();
        if (!cryptoService.verify(jsonCurrency, signature, pubkey)) {
            throw new InvalidSignatureException("Bad signature");
        }

        // Apply the save (will check if pubkey match the owner public key)
        Currency currency = null;
        try {
            currency = objectMapper.readValue(CryptoUtils.decodeUTF8(jsonCurrency), Currency.class);
        } catch (IOException e) {
           throw new UCoinTechnicalException(e.getMessage(), e);
        }
        CurrencyIndexerService service = ServiceLocator.instance().getCurrencyIndexerService();
        service.saveCurrency(currency, pubkey);
    }

    /* -- Internal methods -- */

    protected UcoinSession getUcoinSession() {
        UcoinSession session = (UcoinSession) UcoinSession.get();
        return session;
    }
}
