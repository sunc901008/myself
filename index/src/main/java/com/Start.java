package com;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.FileInputStream;
import java.util.logging.LogManager;

/**
 * user: sunc
 * data: 2017/4/11.
 */
public class Start extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Start.class);

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(Rest.class.getName(), handler -> handler(Rest.class.getName(), handler));//web
        logger.info("System is ready!");

    }

    private static void handler(String className, AsyncResult<String> handler) {
        logger.info(className + " is start! deployVerticleID is " + handler.result());
    }

    public static void main(String[] args) throws Exception {

        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/conf/logging.properties");
        LogManager.getLogManager().readConfiguration(fis);
        fis.close();

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Start.class.getName());
    }

}
