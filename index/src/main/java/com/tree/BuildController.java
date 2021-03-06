package com.tree;

import com.commons.Commons;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class BuildController implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        String path = event.request().getParam("path");
        String table = event.request().getParam("table");
        String column = event.request().getParam("column");
        String param = event.request().getParam("type");
        param = "".equals(param) ? "" : param;
        int type = Commons.columnNumber(param);
        event.vertx().executeBlocking(future -> {
            future.complete(IndexTrieMain.buildTrie(table, column, type, path));
        }, result -> {
            event.response().end(result.result() + " total milliseconds");
        });
    }

}
