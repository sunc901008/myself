package com.tree;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class BuildController implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        String path = event.request().getParam("path");
        String table = event.request().getParam("table");
        String column = event.request().getParam("column");
        JsonObject json = new JsonObject().put("table", table).put("column", column).put("path", path);
        event.vertx().executeBlocking(future -> {
            future.complete(IndexTrieMain.buildTrie(json));
        }, result -> {
            event.response().end(result.result() + " total milliseconds");
        });
    }

}
