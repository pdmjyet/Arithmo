package com.pp.arithmo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.pp.arithmo.configs.ComputeExpConfig;
import com.pp.arithmo.constants.Constants;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

public class ArithmoModule extends AbstractModule{
	@Getter
	private Vertx vertx;
	
	@Getter
	private ComputeExpConfig config;
	
	public ArithmoModule(Vertx vertx, JsonObject config) {
		this.vertx = vertx;
		this.config = config.mapTo(ComputeExpConfig.class);
	}
	
	@Override
	protected void configure() {
		bind(Vertx.class).toInstance(this.vertx);
		bind(EventBus.class).toInstance(this.vertx.eventBus());
	}
	
	@Provides
	@Named(Constants.COMPUTE_EXP_CONFIG)
	public ComputeExpConfig provideComputeExpConfig() {
		return this.config;
	}
}

