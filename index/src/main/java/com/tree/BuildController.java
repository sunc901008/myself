package com.tree;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;

public class BuildController implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        String[] contents = new String[]{"teach", "te", "hello", "world"};
        List<ValueInfo> list = new ArrayList<>();
        for (String content : contents) {
            ValueInfo valueInfo = new ValueInfo();
            valueInfo.setTable("users");
            valueInfo.setColumn("displayName");
            valueInfo.setType("columnValue");
            valueInfo.setScore(10);
            valueInfo.setContent(content);
            list.add(valueInfo);
        }
        event.vertx().executeBlocking(future -> {
            future.complete(IndexTrieMain.buildTrie(list));
        }, result -> {
            event.response().end(result.result() + " total milliseconds");
        });
    }

}
