package io.ucoin.ucoinj.elasticsearch.action;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

public class HelloRestHandler extends BaseRestHandler {

    @Inject
    public HelloRestHandler(Settings settings, RestController controller, Client client) {
        super(settings, controller, client);
        controller.registerHandler(GET, "/_hello", this);
    }

    @Override
    protected void handleRequest(RestRequest restRequest, RestChannel restChannel, Client client) throws Exception {
        String who = restRequest.param("who");
        String whoSafe = (who!=null) ? who : "world";
        restChannel.sendResponse(new BytesRestResponse(OK, "Hello, " + whoSafe + "!"));
    }

}