package com;

import com.tree.BuildController;
import com.tree.SearchController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;


public class Rest extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Rest.class);

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CookieHandler.create());

        //  /index/search/v2?prefix=abc&limit=5&ignore-case=true
        router.get("/index/search/v2").handler(new SearchController());
        router.get("/index/build").handler(new BuildController());

        vertx.createHttpServer().requestHandler(router::accept).listen(8999, r -> {
            if (r.succeeded())
                logger.info("Http start success!");
            else
                logger.error(r.cause());
        });
    }
}
