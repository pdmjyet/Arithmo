package com.pp.arithmo.verticles;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.pp.arithmo.constants.ApiConstants;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpVerticle extends AbstractVerticle{
	@Inject
	protected EventBus eventbus;
	
	@Override
	public void start(Future<Void> future) {
		try {
			Router router = Router.router(vertx);
			router.route().handler(BodyHandler.create());
			
			router.route(HttpMethod.GET, ApiConstants.GET_PROBLEM).produces("application/json").handler(rc -> postRequestHandler(rc.normalisedPath(), rc));
			router.route(HttpMethod.GET, ApiConstants.GET_COLLECTIONS).produces("application/json").handler(rc -> postRequestHandler(rc.normalisedPath(), rc));
//			router.route(HttpMethod.POST, ApiConstants.HONDA_INACTIVE_MODEL_API).produces("application/json").handler(rc -> postRequestHandler(rc.normalisedPath(), rc));
			router.route(HttpMethod.POST, ApiConstants.EVALUATE).consumes("application/json").produces("application/json").handler(rc -> postRequestHandler(rc.normalisedPath(), rc));
			
			startApplication(config(), future, router);
			System.out.print("Successfully started the httpverticle");
		}catch(Exception ex) {
			future.fail(ex);
		}
	}
	
	@Override
	public void stop(Future<Void> future) {
		System.out.print("Successfully stopped the httpverticle");
	}
	
	private void startApplication(JsonObject config, Future<Void> future, Router router) {
		vertx.createHttpServer().requestHandler(router::accept).listen(8095, response -> {
			if(response.succeeded()) {
				future.complete();
			}
			else {
				future.fail(response.cause());
			}
		});
	}
	
	private void postRequestHandler(String path, RoutingContext rc) {
		Object body = getBody(rc);
		JsonObject params = getParams(rc);
		JsonObject data = new JsonObject().put("body", body).put("params", params);
		eventbus.request(path, data, asyncResult -> sendResponse(rc, asyncResult));
//		rc.response().end(JsonObject.mapFrom(body).encode());
//		rc.response().setStatusCode(HttpResponseStatus.OK.code());
	}
	
	private Object getBody(RoutingContext rc) {
		Object body = new JsonObject();
		try {
			body = StringUtils.isEmpty(rc.getBodyAsString()) ? body : new JsonObject(rc.getBodyAsString()); 
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return body;
	}
	
	private JsonObject getParams(RoutingContext rc) {
		try {
			System.out.println(rc.request().params());
			return rc.request().params().isEmpty() ? new JsonObject() : JsonObject.mapFrom(rc.request().params().entries().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
			
		}catch(Exception ex) {
			//throw new Exception();
		}
		return new JsonObject();
	}
	
	protected <T> void sendResponse(RoutingContext routingContext, AsyncResult<Message<T>> asyncResult) {
		HttpServerResponse response = routingContext.response();
		if (!response.closed()) {
			if (asyncResult != null && asyncResult.succeeded()) {
				sendResponseWithSuccess(routingContext, asyncResult.result().body());
			} else {
				Throwable failureCause = (asyncResult != null) ? asyncResult.cause() : routingContext.failure();
				sendResponseWithFailure(routingContext, (failureCause != null) ? failureCause.getMessage() : null, HttpResponseStatus.INTERNAL_SERVER_ERROR );
			}
		} else {
	//		log.error("HTTP response was already closed for api={}", routingContext.request().path());
		}
	}
	
	protected void sendResponseWithSuccess(RoutingContext routingContext, Object resultJavaObject) {
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", routingContext.getAcceptableContentType() != null ? routingContext.getAcceptableContentType() : "application/json");
		response.setStatusCode(HttpResponseStatus.OK.code());
		//response.end(JsonObject.mapFrom(resultJavaObject).toString());
		response.end(resultJavaObject.toString());
//		?log.info("call succeeded; api={}, result={}", routingContext.request().path(), resultJavaObject.toString());
	}

	protected void sendResponseWithFailure(RoutingContext routingContext, String errorMessage, HttpResponseStatus httpStatus) {
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", routingContext.getAcceptableContentType() != null ? routingContext.getAcceptableContentType() : "application/json");
		String errorString = null != errorMessage ? errorMessage : "some error occured";
		Object resultJavaObject = new JsonObject().put("success", false).put("msg", errorString);
		response.setStatusCode(httpStatus.code());
//		log.error("call failed; api={}, cause={}",routingContext.request().path(), errorString);
		response.end(JsonObject.mapFrom(resultJavaObject).toString());
	}
}
