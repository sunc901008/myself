package com;

import com.lucene.Index;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class Controller implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        String query = event.request().getParam("query");
        int count = Integer.parseInt(event.request().getParam("count"));
        JsonObject list = null;
        try {
            list = Index.searchIndex(query, count);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject json = new JsonObject().put("contents", list);

        event.response().end(json.toString());

    }

}
