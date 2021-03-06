//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
// Code structure also modeled off of the TodoController written for the OOSE 2015 class. 
// https://github.com/jhu-oose/todo/blob/master/src/main/java/com/todoapp/TodoController.java
//-------------------------------------------------------------------------------------------------------------//
package com.oose2015.mjudge2.hareandhounds;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HareHoundController {
    private static final String API_CONTEXT = "/haresandhounds/api/games";

	private final HareHoundService service; 
	private final Logger logger = LoggerFactory.getLogger(HareHoundController.class);

	public HareHoundController(HareHoundService service){
		this.service = service;
		createEndpoints();
	}
    /**
     * Creates endpoints for the url requests. See {@link spark.Spark} package).
     */
	private void createEndpoints(){
        post(API_CONTEXT, "application/json", (request, response) -> {
            try {
            	Map<String, String> contentToReturn = service.createNewGame(request.body());
            	response.status(201);
            	return contentToReturn;
            } catch (HareHoundService.HareHoundServiceException ex) {
                logger.error("Failed to create new game");
                response.status(400);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());
   
        put(API_CONTEXT + "/:gameId", "application/json", (request, response) -> {
        	try {
        		Map<String, String> contentToReturn = service.joinGame(request.params(":gameId"));
        	 	if (contentToReturn.containsKey("reason")){
        	 		logger.error("Game already has 2 players");
               		response.status(410);               		
        	 	}
        		response.status(200);
        		return contentToReturn;
        	} catch (HareHoundService.HareHoundServiceException ex){
        		logger.error("Failed to find the game");
        		response.status(404);
        		return Collections.EMPTY_MAP;
        	}
        }, new JsonTransformer());
        
        get(API_CONTEXT + "/:gameId/state", "application/json", (request, response) -> {
            try {
            	Map<String, String> contentToReturn = service.determineGameState(request.params(":gameId"));
            	response.status(200);
            	return contentToReturn;
            } catch (HareHoundService.HareHoundServiceException ex) {
                logger.error(String.format("Failed to find game with id: %s", request.params(":gameId")));
                response.status(404);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/:gameId/board", "application/json", (request, response) -> {
            try {
            	List<Map<String, String>> contentToReturn = service.determineBoardState(request.params(":gameId"));
            	response.status(200);
            	return contentToReturn;
            } catch (HareHoundService.HareHoundServiceException ex) {
                logger.error(String.format("Failed to find game with id: %s", request.params(":gameId")));
                response.status(404);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());
        
        post(API_CONTEXT + "/:gameId/turns", "application/json", (request, response) -> {
           	Map<String, String> contentToReturn = service.playHareAndHounds(request.params(":gameId"), request.body());
           	if(contentToReturn.containsKey("reason")){
           		if(contentToReturn.get("reason").equals("INVALID_GAME_ID")){
           			response.status(404);
           			return contentToReturn;
           		}
           		if(contentToReturn.get("reason").equals("INVALID_PLAYER_ID")){
           			response.status(404);
           			return contentToReturn;
           		}
           		if(contentToReturn.get("reason").equals("INCORRECT_TURN")){
           			response.status(422);
           			return contentToReturn;
           		}
           		if(contentToReturn.get("reason").equals("ILLEGAL_MOVE")){
           			response.status(422);
       				return contentToReturn;
           		}
           	}
           	return contentToReturn;
        }, new JsonTransformer());
	}
}
