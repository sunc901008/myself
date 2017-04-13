package com.tree;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class SearchController implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        ///index/search/v2?prefix=abc&limit=5&ignore-case=true
        String query = event.request().getParam("prefix");
        String c = event.request().getParam("limit");
        int count = 10;
        if(!"".equals(c)){
            count = Integer.parseInt(c);
        }

        JsonObject jsonObject = IndexTrieMain.search(query, count);

        event.response().headers().set("Content-Type", "text/html; charset=UTF-8");
        event.response().end(jsonObject.toString());

    }

}
