package com.pp.arithmo.verticles;

import org.apache.log4j.BasicConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.pp.arithmo.ArithmoModule;
import com.pp.arithmo.guice.GuiceVerticleFactory;
import com.pp.arithmo.guice.GuiceVertxDeploymentManager;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {
	
	@Override
    public void start(Future<Void> future) throws Exception {
        try {

        	startApplication(config());
            future.complete();
            log.debug("Successfully started...");
        } catch (Exception e) {
            vertx.close();
            future.fail(e);
            log.error("Failed to start...");
        }
    }

    private void startApplication(JsonObject config) throws Exception {
    	BasicConfigurator.configure();
    	ArithmoModule module = new ArithmoModule(vertx, config);
    	Injector injector = Guice.createInjector(module);
        GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(injector);
        vertx.registerVerticleFactory(guiceVerticleFactory);
        
        GuiceVertxDeploymentManager deploymentManager = new GuiceVertxDeploymentManager(vertx);

        deploymentManager.deployVerticle(HttpVerticle.class, new DeploymentOptions().setInstances(1).setConfig(config), this::failureHandler);
        deploymentManager.deployVerticle(ComputeExpProcessorVerticle.class, new DeploymentOptions().setInstances(1).setConfig(config), this::failureHandler);
    }

  
    private void failureHandler(AsyncResult<String> result) {
        if (result.failed()) {
            vertx.close();
            System.exit(0);
        }
    }

}
