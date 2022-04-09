package com.pp.arithmo.verticles;

import static io.vertx.core.eventbus.ReplyFailure.RECIPIENT_FAILURE;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.pp.arithmo.constants.ApiConstants;
import com.pp.arithmo.models.requests.EvaluateRequest;
import com.pp.arithmo.models.requests.GetProblemRequest;
import com.pp.arithmo.services.CollectionService;
import com.pp.arithmo.services.ComputeExpressionService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComputeExpProcessorVerticle extends AbstractVerticle{
	@Inject
	private EventBus eventbus;
	
	@Inject
	private ComputeExpressionService ceSvc;
	
	@Inject
	private CollectionService colSvc;
	
	public void start(Future<Void> startFuture) throws Exception {
		addRoutes(startFuture);
		System.out.print("Successfully started the ComputeExpProcessorVerticle");
	}
	
	private void addRoutes(Future<Void> startFuture) {
		eventbus.consumer(ApiConstants.EVALUATE, (Handler<Message<JsonObject>>) message -> handleMessage(ApiConstants.EVALUATE, message));
		eventbus.consumer(ApiConstants.GET_PROBLEM, (Handler<Message<JsonObject>>) message -> handleMessage(ApiConstants.GET_PROBLEM, message));
		eventbus.consumer(ApiConstants.GET_COLLECTIONS, (Handler<Message<JsonObject>>) message -> handleMessage(ApiConstants.GET_COLLECTIONS, message));
	}
	
	private void handleMessage(String address, final Message<JsonObject> message) {
		vertx.executeBlocking(future -> {
			try {
				Object response = delegateMethodHandlers(address, message);
				future.complete(response);
			} catch (Throwable e) {
				future.fail(new ReplyException(RECIPIENT_FAILURE, SC_INTERNAL_SERVER_ERROR, e.getMessage()));
			}
		}, false, asyncResult -> {
			if (asyncResult.succeeded()) {
				message.reply(asyncResult.result());
			} else {
				if(asyncResult.cause() instanceof  ReplyException){
					ReplyException exception = (ReplyException) asyncResult.cause();
					message.fail(exception.failureCode(), exception.getMessage());
				} else {
					message.fail(SC_INTERNAL_SERVER_ERROR, asyncResult.cause().getMessage());
				}
			}
		});
	}
	
	private Object delegateMethodHandlers(String address, final Message<JsonObject> message) throws Throwable {
		JsonObject  request = message.body();
		JsonObject response = new JsonObject();
		
		JsonObject   body = request.getJsonObject("body");
		JsonObject params = request.getJsonObject("params");
		
		switch(address) {
			case ApiConstants.EVALUATE:
				response = ceSvc.evaluate(body.mapTo(EvaluateRequest.class));
				break;
			case ApiConstants.GET_PROBLEM:
				response = JsonObject.mapFrom(ceSvc.problem(new ObjectMapper().readValue(params.encode(), GetProblemRequest.class)));  
				break;
			case ApiConstants.GET_COLLECTIONS:
				response = JsonObject.mapFrom(colSvc.getCollections());  
				break;
			default:
				break;
		}
		return response;
	}
}
