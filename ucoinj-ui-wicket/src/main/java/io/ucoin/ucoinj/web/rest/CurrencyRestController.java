package io.ucoin.ucoinj.web.rest;

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


import io.ucoin.ucoinj.core.client.model.elasticsearch.Currency;
import io.ucoin.ucoinj.elasticsearch.service.CurrencyIndexerService;
import io.ucoin.ucoinj.elasticsearch.service.ServiceLocator;
import io.ucoin.ucoinj.elasticsearch.service.exception.InvalidSignatureException;
import io.ucoin.ucoinj.web.application.WebSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/currency")
public class CurrencyRestController {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(CurrencyRestController.class);

    @RequestMapping(value="/{currencyId}", method = RequestMethod.GET)
    public @ResponseBody Currency getCurrencyById(@PathVariable(value="currencyId") String currencyId) {
        CurrencyIndexerService service = ServiceLocator.instance().getCurrencyIndexerService();
        return service.getCurrencyById(currencyId);
    }

    @ResponseStatus(value= org.springframework.http.HttpStatus.OK, reason="Currency successfully register")
    @RequestMapping(value="/add", method = RequestMethod.POST)
    public String registerNewCurrency(@RequestParam("pubkey") String pubkey, @RequestParam("currency") String jsonCurrency, @RequestParam("sig") String signature) throws InvalidSignatureException {
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

    protected WebSession getWebSession() {
        WebSession session = (WebSession) WebSession.get();
        return session;
    }
}
