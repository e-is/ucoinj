package io.ucoin.client.ui.service.rest;

import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.service.search.SearchService;
import io.ucoin.client.core.technical.jackson.JacksonUtils;
import io.ucoin.client.ui.application.UcoinSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.PathParam;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.JacksonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.mimetypes.RestMimeTypes;
import org.wicketstuff.rest.contenthandling.webserialdeserial.TextualWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

@ResourcePath("/rest/currency")
public class CurrencyRestService extends AbstractRestResource<TextualWebSerialDeserial> {

    // Logger
    private static final Log log = LogFactory.getLog(CurrencyRestService.class);

    private static final long serialVersionUID = 1L;
    
    private boolean debug;

    public CurrencyRestService() {
        super(new TextualWebSerialDeserial("UTF-8", RestMimeTypes.APPLICATION_JSON,
            new JacksonObjectSerialDeserial(JacksonUtils.newObjectMapper())));
        debug = log.isDebugEnabled();
    }

    @MethodMapping("/currency/{currencyId}")
    public Currency getCurrencyById(@PathParam("currencyId") String currencyId) {

        SearchService service = ServiceLocator.instance().getSearchService();
        return service.getCurrencyById(currencyId);
    }

    @MethodMapping(value = "/register", httpMethod = HttpMethod.POST)
    public void saveCurrency(@RequestBody Currency currency) {

       SearchService service = ServiceLocator.instance().getSearchService();
       service.indexCurrency(currency);
    }

    /* -- Internal methods -- */

    protected UcoinSession getUcoinSession() {
        UcoinSession session = (UcoinSession) UcoinSession.get();
        return session;
    }
}
