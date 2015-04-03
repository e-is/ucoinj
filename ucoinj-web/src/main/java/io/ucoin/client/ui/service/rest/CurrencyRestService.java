package io.ucoin.client.ui.service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.service.CryptoService;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.service.search.SearchService;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import io.ucoin.client.core.technical.crypto.CryptoUtils;
import io.ucoin.client.core.technical.jackson.JacksonUtils;
import io.ucoin.client.ui.application.UcoinSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.PathParam;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.JacksonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.mimetypes.RestMimeTypes;
import org.wicketstuff.rest.contenthandling.webserialdeserial.TextualWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@ResourcePath("/rest/currency")
public class CurrencyRestService extends AbstractRestResource<TextualWebSerialDeserial> {

    // Logger
    private static final Log log = LogFactory.getLog(CurrencyRestService.class);

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
        SearchService service = ServiceLocator.instance().getSearchService();
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
        SearchService service = ServiceLocator.instance().getSearchService();
        service.saveCurrency(currency, pubkey);
    }

    /* -- Internal methods -- */

    protected UcoinSession getUcoinSession() {
        UcoinSession session = (UcoinSession) UcoinSession.get();
        return session;
    }
}
