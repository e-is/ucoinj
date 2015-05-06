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


import com.google.gson.Gson;
import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.service.search.CurrencyIndexerService;
import io.ucoin.client.core.service.search.InvalidSignatureException;
import io.ucoin.client.core.technical.gson.GsonUtils;
import io.ucoin.client.ui.application.UcoinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.PathParam;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.mimetypes.RestMimeTypes;
import org.wicketstuff.rest.contenthandling.webserialdeserial.TextualWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;

@ResourcePath("/rest/currency")
public class CurrencyRestService extends AbstractRestResource<TextualWebSerialDeserial> {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(CurrencyRestService.class);

    private static final long serialVersionUID = 1L;
    
    private boolean debug;

    private static Gson gson;

    public CurrencyRestService() {
        super(new TextualWebSerialDeserial("UTF-8", RestMimeTypes.APPLICATION_JSON,
                new GsonObjectSerialDeserial(initGson())));
        debug = log.isDebugEnabled();
    }

    private static Gson initGson() {
        if (gson == null) {
            gson = GsonUtils.newBuilder().create();
        }
        return gson;
    }

    @MethodMapping("/{currencyId}")
    public Currency getCurrencyById(@PathParam("currencyId") String currencyId) {
        CurrencyIndexerService service = ServiceLocator.instance().getCurrencyIndexerService();
        return service.getCurrencyById(currencyId);
    }

    @MethodMapping(value = "/add", produces = RestMimeTypes.TEXT_PLAIN)
    public String saveCurrency(@RequestParam("pubkey") String pubkey, @RequestParam("currency") String jsonCurrency, @RequestParam("sig") String signature) throws InvalidSignatureException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Asking to add new currency:\n - pubkey: %s\n - currency: %s\n - signature: %s", pubkey, jsonCurrency, signature));
        }

        CurrencyIndexerService service = ServiceLocator.instance().getCurrencyIndexerService();
        service.registerCurrency(pubkey, jsonCurrency, signature);

        /*CryptoService cryptoService = ServiceLocator.instance().getCryptoService();

        if (!cryptoService.verify(jsonCurrency, signature, pubkey)) {
            String currencyName = GsonUtils.getValueFromJSONAsString(jsonCurrency, "currencyName");
            log.warn(String.format("Currency not added, because bad signature. currency [%s]", currencyName));
            throw new InvalidSignatureException("Bad signature");
        }

        Currency currency = null;
        try {
            currency = gson.fromJson(jsonCurrency, Currency.class);
            Preconditions.checkNotNull(currency);
            Preconditions.checkNotNull(currency.getCurrencyName());
        } catch(Throwable t) {
            log.error("Error while reading currency JSON: " + jsonCurrency);
            return "Parse error. Currency not readable.";
        }

        CurrencyIndexerService service = ServiceLocator.instance().getCurrencyIndexerService();
        service.saveCurrency(currency, pubkey);
        */

        return "Currency added";
    }

    /* -- Internal methods -- */

    protected UcoinSession getUcoinSession() {
        UcoinSession session = (UcoinSession) UcoinSession.get();
        return session;
    }
}
