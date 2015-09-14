package com.oose2015.mjudge2.hareandhounds;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import javax.sql.DataSource;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HareHoundService {
	private Sql2o database;
    private final Logger logger = LoggerFactory.getLogger(HareHoundService.class);
    private final String[] status = new String[]{"WAITING_FOR_SECOND_PLAYER", "TURN_HARE", "TURN_HOUND", "WIN_HARE_BY_ESCAPE", 
    		"WIN_HARE_BY_STALLING", "WIN_HOUND"};
    
    public HareHoundService(DataSource dataSource) throws HareHoundServiceException {
        database = new Sql2o(dataSource);
        try (Connection conn = database.open()) {
            String sqlGameTable = "CREATE TABLE IF NOT EXISTS game (game_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         				"status INTEGER, most_recent_config INTEGER)" ;
            String sqlPlayerTable = "CREATE TABLE IF NOT EXISTS player (player_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    	 				"piece_type TEXT, x_location_1 INTEGER, y_location_1 INTEGER, x_location_2 INTEGER, y_location_2 INTEGER, " +
                    	 				"x_location_3 INTEGER, y_location_3 INTEGER, game_id INTEGER)" ;
            String sqlBoardConfigurationTable = "CREATE TABLE IF NOT EXISTS configuration (configuration_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    	 				"hare_x_location INTEGER, hare_y_location INTEGER, hound_1_x_location INTEGER, hound_1_y_location INTEGER, " +
                    	 				"hound_2_x_location INTEGER, hound_2_y_location INTEGER, hound_3_x_location INTEGER, hound_3_y_location INTEGER, " +
                    	 				"frequency INTEGER, game_id INTEGER)";
            conn.createQuery(sqlGameTable).executeUpdate();
            conn.createQuery(sqlPlayerTable).executeUpdate();
            conn.createQuery(sqlBoardConfigurationTable).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new HareHoundServiceException("Failed to create schema at startup", ex);
        }
    }
    public Map<String, String> createNewGame(String body) throws HareHoundServiceException{
    	Boolean hareIdentifier = false;
    	Player player1 = new Gson().fromJson(body, Player.class);
    	Game game = new Game();
    	Map<String, String> contentToReturn = new HashMap<String, String>();
    	
    	if (player1.getPieceType().equals("HARE")){
    		List<Point> currentLocation = new ArrayList<Point>();
    		currentLocation.add(new Point(4,1));
    		player1.setCurrentLocation(currentLocation);
    		game.setHare(player1);
    		hareIdentifier = true;
    	} else if (player1.getPieceType().equals("HOUND")){
    		game.setHound(player1);
    		List<Point> currentLocation = new ArrayList<Point>();
    		currentLocation.add(new Point(0,1));
    		currentLocation.add(new Point(1,0));
    		currentLocation.add(new Point(1,2));
    		player1.setCurrentLocation(currentLocation);
    		game.setHound(player1);
    	}
    	game.setStatus(0);
    	String insertGameSql = "INSERT INTO game (status) VALUES (:gameStatus)";

    	try (Connection conn = database.open()){
    		Object playerId, gameId;
    		gameId = conn.createQuery(insertGameSql, true)
    				.addParameter("gameStatus", game.getStatus())
    				.executeUpdate()
    				.getKey();

    		if (hareIdentifier){
    			//System.out.println(player1.getPieceType());
    	    	String insertHarePlayerSql = "INSERT INTO player (piece_type, x_location_1, y_location_1, game_id) " + 
    	    			"VALUES (:pieceType, :xLocation1, :yLocation1, :gameId)";
    			playerId = conn.createQuery(insertHarePlayerSql, true)
    				.addParameter("pieceType", player1.getPieceType())
    				.addParameter("xLocation1", player1.getXLocationsAsStrings().get(0))
    				.addParameter("yLocation1", player1.getYLocationsAsStrings().get(0))
    				.addParameter("gameId", gameId.toString())
    				.executeUpdate()
    				.getKey();
    		} else {
    			String insertHoundPlayerSql = "INSERT INTO player (piece_type, x_location_1, y_location_1, " +
    					"x_location_2, y_location_2, x_location_3, y_location_3, game_id) " + 
    	    			"VALUES (:pieceType, :xLocation1, :yLocation1, :xLocation2, :yLocation2, :xLocation3, :yLocation3, :gameId)";
    			playerId = conn.createQuery(insertHoundPlayerSql, true)
    				.addParameter("pieceType", player1.getPieceType())
    				.addParameter("xLocation1", player1.getXLocationsAsStrings().get(0))
    				.addParameter("yLocation1", player1.getYLocationsAsStrings().get(0))
    				.addParameter("xLocation2", player1.getXLocationsAsStrings().get(1))
    				.addParameter("yLocation2", player1.getYLocationsAsStrings().get(1))
    				.addParameter("xLocation3", player1.getXLocationsAsStrings().get(2))
    				.addParameter("yLocation3", player1.getYLocationsAsStrings().get(2))
    				.addParameter("gameId", gameId.toString())
    				.executeUpdate()
    				.getKey();
    		}
    		contentToReturn.put("gameId", gameId.toString());
    		contentToReturn.put("playerId", playerId.toString());
    		contentToReturn.put("pieceType", player1.getPieceType());
    		//System.out.println(gameId);
    	} catch(Sql2oException ex){
    		logger.error("HareHoundService.createNewGame: Failed to create new entry");
    	}
    	return contentToReturn;
    }

    public Map<String, String> joinGame(String gameId) throws HareHoundServiceException{
    	Map<String, String> contentToReturn = new HashMap<String, String>();
        String sqlFindGame = "SELECT game_id FROM game WHERE game_id = :gameId";
        String sqlFindPlayer = "SELECT piece_type, game_id, player_id FROM player WHERE game_id = :gameId";
        try (Connection conn = database.open()) {
        	Object playerId;
            Game game = conn.createQuery(sqlFindGame)
                .addParameter("gameId", Integer.parseInt(gameId))
                .addColumnMapping("game_id", "gameId")
                .executeAndFetchFirst(Game.class);
        	List<Player> player = conn.createQuery(sqlFindPlayer)
        		.addParameter("gameId", Integer.parseInt(gameId))
        		.addColumnMapping("player_id", "playerId")
        		.addColumnMapping("game_id", "gameId")
        		.addColumnMapping("piece_type", "pieceType")
        		.executeAndFetch(Player.class);
        	if (player.size() > 1){
        		return null;
        	}
        	Player player2 = new Player();
        	if (player.get(0).getPieceType().equals("HARE")){
        		//System.out.println("here in join game");
        		player2.setPieceType("HOUND");
        		List<Point> currentLocation = new ArrayList<Point>();
        		currentLocation.add(new Point(0,1));
        		currentLocation.add(new Point(1,0));
        		currentLocation.add(new Point(1,2));
        		player2.setCurrentLocation(currentLocation);
        		
    			String insertHoundPlayerSql = "INSERT INTO player (piece_type, x_location_1, y_location_1, " +
    					"x_location_2, y_location_2, x_location_3, y_location_3, game_id) " + 
    	    			"VALUES (:pieceType, :xLocation1, :yLocation1, :xLocation2, :yLocation2, :xLocation3, :yLocation3, :gameId)";
    			playerId = conn.createQuery(insertHoundPlayerSql, true)
    				.addParameter("pieceType", player2.getPieceType())
    				.addParameter("xLocation1", player2.getXLocationsAsStrings().get(0))
    				.addParameter("yLocation1", player2.getYLocationsAsStrings().get(0))
    				.addParameter("xLocation2", player2.getXLocationsAsStrings().get(1))
    				.addParameter("yLocation2", player2.getYLocationsAsStrings().get(1))
    				.addParameter("xLocation3", player2.getXLocationsAsStrings().get(2))
    				.addParameter("yLocation3", player2.getYLocationsAsStrings().get(2))
    				.addParameter("gameId", gameId.toString())
    				.executeUpdate()
    				.getKey();
        	} else {
        		//System.out.println("there in join game");
        		player2.setPieceType("HARE");
        		List<Point> currentLocation = new ArrayList<Point>();
        		currentLocation.add(new Point(4,1));
        		player2.setCurrentLocation(currentLocation);
        		String insertHarePlayerSql = "INSERT INTO player (piece_type, x_location_1, y_location_1, game_id) " + 
    	    			"VALUES (:pieceType, :xLocation1, :yLocation1, :gameId)";
    			playerId = conn.createQuery(insertHarePlayerSql, true)
    				.addParameter("pieceType", player2.getPieceType())
    				.addParameter("xLocation1", player2.getXLocationsAsStrings().get(0))
    				.addParameter("yLocation1", player2.getYLocationsAsStrings().get(0))
    				.addParameter("gameId", gameId.toString())
    				.executeUpdate()
    				.getKey();
        	}
        	contentToReturn.put("gameId", gameId.toString());
        	contentToReturn.put("playerId", playerId.toString());
       		contentToReturn.put("pieceType", player2.getPieceType());
    
       		
       		Integer frequency = 1;
       		String insertBoardConfigurationSql = "INSERT INTO configuration (hare_x_location, hare_y_location, hound_1_x_location, " +
       				"hound_1_y_location, hound_2_x_location, hound_2_y_location, hound_3_x_location, hound_3_y_location, frequency, game_id) " +
       				"VALUES (:hareXLocation, :hareYLocation, :hound1XLocation, :hound1YLocation, :hound2XLocation, :hound2YLocation, :hound3XLocation, " +
       				":hound3YLocation, :frequency, :gameId)";
       		Object configurationId = conn.createQuery(insertBoardConfigurationSql, true)
       				.addParameter("hareXLocation", "4")
       				.addParameter("hareYLocation", "1")
       				.addParameter("hound1XLocation", "0")      				
       				.addParameter("hound1YLocation", "1")
       				.addParameter("hound2XLocation", "1")       				
       				.addParameter("hound2YLocation", "0")
       				.addParameter("hound3XLocation", "1")
       				.addParameter("hound3YLocation", "2")
       				.addParameter("frequency", frequency.toString())
       				.addParameter("gameId", gameId.toString())
       				.executeUpdate()
       				.getKey();
       		
       		String updateGameStatusSql = "UPDATE game SET status = :status, most_recent_config = :configId WHERE game_id = :gameId";
       		conn.createQuery(updateGameStatusSql)
       			.addParameter("gameId", gameId.toString())
       			.addParameter("status", "2")
       			.addParameter("configId", Integer.parseInt(configurationId.toString()))
      			.executeUpdate();
       		return contentToReturn;
        } catch(Sql2oException ex) {
            logger.error(String.format("HareHoundService.joinGame: Failed to query database for id: %s", gameId), ex);
            throw new HareHoundServiceException(String.format("HareHoundService.joinGame: Failed to query database for id: %s", gameId), ex);
        }
    }
    
    public Map<String, String> determineGameState (String gameId) throws HareHoundServiceException {
    	Map<String, String> contentToReturn = new HashMap<String, String>();
    	String sqlGetGameState = "SELECT status FROM game WHERE game_id = :gameId ";
    	try (Connection conn = database.open()) {
            Game game = conn.createQuery(sqlGetGameState)
                .addParameter("gameId", Integer.parseInt(gameId))
                .addColumnMapping("status", "status")
                .executeAndFetchFirst(Game.class);
            if (game != null){
            	contentToReturn.put("state", status[game.getStatus()]);
            }
            return contentToReturn;
        } catch(Sql2oException ex) {
            logger.error(String.format("HareHoundService.determineGameState: Failed to query database for id: %s", gameId), ex);
            throw new HareHoundServiceException(String.format("HareHoundService.determineGameState: Failed to query database for id: %s", gameId), ex);
        }
    }
    
    public List<Map<String, String>> determineBoardState (String gameId) throws HareHoundServiceException {    	
    	List<Map<String, String>> contentToReturn = new ArrayList<Map<String, String>>();
    	String sqlGetBoardConfiguration = "SELECT * FROM configuration WHERE configuration_id= :configurationId";
    	//insert provision that there must be two players to 
		Game game = getGame(Integer.parseInt(gameId));

    	try (Connection conn = database.open()){
    		BoardConfiguration configuration = conn.createQuery(sqlGetBoardConfiguration)
    				.addParameter("configurationId", game.getMostRecentConfigurationId())
    				.addColumnMapping("configuration_id", "id")
    				.addColumnMapping("hare_x_location", "xLocationHare")
    				.addColumnMapping("hare_y_location", "yLocationHare")
    				.addColumnMapping("hound_1_x_location", "xLocationHound1")
    				.addColumnMapping("hound_1_y_location", "yLocationHound1")
    				.addColumnMapping("hound_2_x_location", "xLocationHound2")
    				.addColumnMapping("hound_2_y_location", "yLocationHound2")
    				.addColumnMapping("hound_3_x_location", "xLocationHound3")
    				.addColumnMapping("hound_3_y_location", "yLocationHound3")
    				.addColumnMapping("game_id", "gameId")
    				.addColumnMapping("frequency", "frequency")
    				.executeAndFetchFirst(BoardConfiguration.class);
    		Map<String, String> hareLocations = new HashMap<String, String>();
    		hareLocations.put("pieceType", "HARE");
    		hareLocations.put("x", ((Integer)configuration.getXLocationHare()).toString());
    		hareLocations.put("y", ((Integer)configuration.getYLocationHare()).toString());
    		contentToReturn.add(hareLocations);
    		Map<String, String> hound1Locations = new HashMap<String, String>();
    		hound1Locations.put("pieceType", "HOUND");
    		hound1Locations.put("x", ((Integer)configuration.getXLocationHound1()).toString());
    		hound1Locations.put("y", ((Integer)configuration.getYLocationHound1()).toString());
    		contentToReturn.add(hound1Locations);
    		Map<String, String> hound2Locations = new HashMap<String, String>();
    		hound2Locations.put("pieceType", "HOUND");
    		hound2Locations.put("x", ((Integer)configuration.getXLocationHound2()).toString());
    		hound2Locations.put("y", ((Integer)configuration.getYLocationHound2()).toString());
    		contentToReturn.add(hound2Locations);
    		Map<String, String> hound3Locations = new HashMap<String, String>();
    		hound3Locations.put("pieceType", "HOUND");
    		hound3Locations.put("x", ((Integer)configuration.getXLocationHound3()).toString());
    		hound3Locations.put("y", ((Integer)configuration.getYLocationHound3()).toString());
    		contentToReturn.add(hound3Locations);
    		return contentToReturn;
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.determineBoardState: Failed to query database for id: %s", gameId), ex);
            throw new HareHoundServiceException(String.format("HareHoundService.determineBoardState: Failed to query database for id: %s", gameId), ex);
    	}
    }
    public Map<String, String> playHareAndHounds(String gameId, String body) throws HareHoundServiceException{
    	Map<String, String> contentToReturn = new HashMap<String, String>();
    	Game game;
    	BoardConfiguration boardConfiguration;
        Player paramPlayer = new Gson().fromJson(body, Player.class);
        Player player;
        int newStatus;
    	try {
    		game = getGame(paramPlayer.getGameId());
    	} catch (HareHoundServiceException ex){
    		contentToReturn.put("reason", "INVALID_GAME_ID");
    		return contentToReturn;
    	}
    	try {
    		player = getPlayer(paramPlayer.getPlayerId(), game.getGameId());
    		//System.out.println("piece type: " + player.getPieceType());
    		player.setFromX(paramPlayer.getFromX());
    		player.setToX(paramPlayer.getToX());
    		player.setFromY(paramPlayer.getFromY());
    		player.setToY(paramPlayer.getToY());
    	} catch (HareHoundServiceException ex){
    		contentToReturn.put("reason", "INVALID_PLAYER_ID");
    		return contentToReturn;
    	}
    	if (!isPlayersTurn(game, player.getPieceType())){
    		contentToReturn.put("reason", "INCORRECT_TURN");
    		return contentToReturn;
    	}
		try {
			boardConfiguration = getBoardConfiguration(game.getMostRecentConfigurationId());
			if (isLocationOccupied(boardConfiguration, paramPlayer.getToX(), paramPlayer.getToY(), player.getPieceType())){
				//System.out.println("illegal move here");
				contentToReturn.put("reason", "ILLEGAL_MOVE");
				return contentToReturn;
			}
		} catch (HareHoundServiceException ex){
			//System.out.println("illegal move here 2");
			contentToReturn.put("reason", "ILLEGAL_MOVE");
			return contentToReturn;
		}
    	if (!isValidMove(player.getFromX(), player.getToX(), player.getFromY(), player.getToY(), player.getPieceType())){
    		//System.out.println("illegal move here 3");
    		contentToReturn.put("reason", "ILLEGAL_MOVE");
    		return contentToReturn;
    	} else {
        	if (player.getPieceType().equals("HARE")){
        		boardConfiguration.setXLocationHare(player.getToX());
        		boardConfiguration.setYLocationHare(player.getToY());
        	} else {
        		if (boardConfiguration.getXLocationHound1() == player.getFromX() && boardConfiguration.getYLocationHound1() == player.getFromY()){
        			boardConfiguration.setXLocationHound1(player.getToX());
        			boardConfiguration.setYLocationHound1(player.getToY());
        		} else if (boardConfiguration.getXLocationHound2() == player.getFromX() && boardConfiguration.getYLocationHound2() == player.getFromY()){
        			boardConfiguration.setXLocationHound2(player.getToX());
        			boardConfiguration.setYLocationHound2(player.getToY());
        		} else if (boardConfiguration.getXLocationHound3() == player.getFromX() && boardConfiguration.getYLocationHound3() == player.getFromY()){
        			boardConfiguration.setXLocationHound3(player.getToX());
        			boardConfiguration.setYLocationHound3(player.getToY());
        		}
        	}
        	if (isHareTrapped(boardConfiguration)){
        		game.setStatus(5);
        	}
        	if (noHoundToLeftOfHare(boardConfiguration)){
        		game.setStatus(3);
        	}
        	if (isStalling(boardConfiguration)){
        		game.setStatus(4);
        	}
    	}
    	try (Connection conn = database.open()){
       		String insertBoardConfigurationSql = "INSERT INTO configuration (hare_x_location, hare_y_location, hound_1_x_location, " +
       				"hound_1_y_location, hound_2_x_location, hound_2_y_location, hound_3_x_location, hound_3_y_location, frequency, game_id) " +
       				"VALUES (:hareXLocation, :hareYLocation, :hound1XLocation, :hound1YLocation, :hound2XLocation, :hound2YLocation, :hound3XLocation, " +
       				":hound3YLocation, :frequency, :gameId)";
       		Object configurationId = conn.createQuery(insertBoardConfigurationSql, true)
       				.addParameter("hareXLocation", boardConfiguration.getXLocationHare())
       				.addParameter("hareYLocation", boardConfiguration.getYLocationHare())
       				.addParameter("hound1XLocation", boardConfiguration.getXLocationHound1())      				
       				.addParameter("hound1YLocation", boardConfiguration.getYLocationHound1())
       				.addParameter("hound2XLocation", boardConfiguration.getXLocationHound2())       				
       				.addParameter("hound2YLocation", boardConfiguration.getYLocationHound2())
       				.addParameter("hound3XLocation", boardConfiguration.getXLocationHound3())
       				.addParameter("hound3YLocation", boardConfiguration.getYLocationHound3())
       				.addParameter("frequency", 1)
       				.addParameter("gameId", game.getGameId())
       				.executeUpdate()
       				.getKey();
       		//System.out.println("most recent config" + configurationId.toString());
       		game.setMostRecentConfigurationId((Integer)configurationId);
       		//System.out.println("most recent config game" + game.getMostRecentConfigurationId());
       		String updateGameStatusSql = "UPDATE game SET status = :status, most_recent_config = :mostRecentConfigurationId WHERE game_id = :gameId";
    		if (game.getStatus() == 2){
            	game.setStatus(1);
            } else if (game.getStatus() == 1){
            	game.setStatus(2);
            }
    		conn.createQuery(updateGameStatusSql)
    			.bind(game)
                .addParameter("gameId", Integer.parseInt(gameId))
                .executeUpdate();
    		
        } catch(Sql2oException ex) {
            logger.error(String.format("HareHoundService.determineGameState: Failed to query database for id: %s", gameId), ex);
            throw new HareHoundServiceException(String.format("HareHoundService.determineGameState: Failed to query database for id: %s", gameId), ex);
        }
    	//System.out.println("id:" + paramPlayer.getPlayerId());
    	contentToReturn.put("playerId", ((Integer)paramPlayer.getPlayerId()).toString());
    	return contentToReturn;
    }
    private boolean isLocationOccupied(BoardConfiguration boardConfiguration, int toX, int toY, String pieceType){
    	if (pieceType.equals("HARE")){
    		if ((toX == boardConfiguration.getXLocationHound1()) && (toY == boardConfiguration.getYLocationHound1())){
    			return true;
    		}
    		if ((toX == boardConfiguration.getXLocationHound2()) && (toY == boardConfiguration.getYLocationHound2())){
    			return true;
    		}
    		if ((toX == boardConfiguration.getXLocationHound3()) && (toY == boardConfiguration.getYLocationHound3())){
    			return true;
    		}
    	} else {
    		if ((toX == boardConfiguration.getXLocationHare()) && (toY == boardConfiguration.getYLocationHare())){
    			return true;
    		}
    	}
    	return false;
    }
    private BoardConfiguration getBoardConfiguration(int configurationId) throws HareHoundServiceException{
    	String sqlGetBoardConfiguration = "SELECT * FROM configuration WHERE configuration_id = :configurationId";
    	try (Connection conn = database.open()){
    		return conn.createQuery(sqlGetBoardConfiguration)
       				.addParameter("configurationId", configurationId)
       				.addColumnMapping("configuration_id", "id")
       				.addColumnMapping("hare_x_location", "xLocationHare")
       				.addColumnMapping("hare_y_location", "yLocationHare")
       				.addColumnMapping("hound_1_x_location", "xLocationHound1")      				
       				.addColumnMapping("hound_1_y_location", "yLocationHound1")
       				.addColumnMapping("hound_2_x_location", "xLocationHound2")       				
       				.addColumnMapping("hound_2_y_location", "yLocationHound2")
       				.addColumnMapping("hound_3_x_location", "xLocationHound3")
       				.addColumnMapping("hound_3_y_location", "yLocationHound3")
       				.addColumnMapping("frequency", "frequency")
       				.addColumnMapping("game_id", "gameId")
       				.executeAndFetchFirst(BoardConfiguration.class);
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.getBoardConfiguration: Failed to query database for game: %s", configurationId), ex);
    		throw new HareHoundServiceException(String.format("HareHoundService.getBoardConfiguration: Failed to query database of id %s", configurationId), ex);
    	}
    }
    private Game getGame(int gameId) throws HareHoundServiceException{
    	String sqlIsValidGame = "SELECT * FROM game WHERE game_id = :gameId";
    	try (Connection conn = database.open()){
    		return conn.createQuery(sqlIsValidGame)
    			.addParameter("gameId", gameId)
    			.addColumnMapping("game_id", "gameId")
    			.addColumnMapping("most_recent_config", "mostRecentConfigurationId")
    			.addColumnMapping("status", "status")
    			.executeAndFetchFirst(Game.class);
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.isValidGameId: Failed to query database for game: %s", gameId), ex);
    		throw new HareHoundServiceException(String.format("HareHoundService.isValidGameID: Failed to query database of id %s", gameId), ex);
    	}
    }
    private Player getPlayer(int playerId, int gameId) throws HareHoundServiceException{
    	String sqlIsValidPlayer = "SELECT player_id, game_id, piece_type FROM player WHERE player_id = :playerId AND game_id = :gameId";
    	try (Connection conn = database.open()){
    		return conn.createQuery(sqlIsValidPlayer)
    			.addParameter("playerId", playerId)
    			.addParameter("gameId", gameId)
    			.addColumnMapping("player_id", "playerId")
    			.addColumnMapping("game_id", "gameId")
    			.addColumnMapping("piece_type", "pieceType")
    			.executeAndFetchFirst(Player.class);
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.isValidPlayerId: Failed to query database for player: %s", playerId), ex);
    		throw new HareHoundServiceException(String.format("HareHoundService.isValidPlayerID: Failed to query database of id %s", playerId), ex);
    	}
    }
    private boolean isPlayersTurn(Game game, String pieceType){
    	int gameStatus = game.getStatus();
    	boolean isTurn = false;
    	if (gameStatus == 2 && pieceType.equals("HOUND")){
    		isTurn = true;
    	} else if (gameStatus == 1 && pieceType.equals("HARE")) {
    		isTurn = true;
    	}
    	return isTurn;
    }

    private boolean isValidMove(int fromX, int toX, int fromY, int toY, String pieceType){
    	if (pieceType.equals("HOUND")){
    		if (fromX > toX){
    			return false;
    		}
    	}
    	int xShift = Math.abs(toX - fromX);
    	int yShift = Math.abs(toY - fromY);
    	int toSum = Math.abs(toX + toY);
    	int fromSum = Math.abs(fromX + fromY) % 2;
    	if (xShift <= 1 && yShift <= 1){
    		if ((fromSum % 2 == 0) && (toSum % 2 == 0)){
    			return false;
    		}
    		return true;
    	}
    	return false;
    }
    private boolean noHoundToLeftOfHare(BoardConfiguration configuration){
    	Integer hareXCoordinate = (Integer) configuration.getXLocationHare();
    	List<Integer> houndXCoordinates = new ArrayList<Integer>();
    	houndXCoordinates.add((Integer)configuration.getXLocationHound1());
    	houndXCoordinates.add((Integer)configuration.getXLocationHound2());
    	houndXCoordinates.add((Integer)configuration.getXLocationHound3());
    	
    	int houndCounter = 0;
    	for (Integer houndXCoordinate : houndXCoordinates){
    		if (hareXCoordinate <= houndXCoordinate){
    			houndCounter++;
    		}
    	}
    	if (houndCounter == 3){
    		return true;
    	}
    	return false;
    }
    private boolean isHareTrapped(BoardConfiguration configuration){
    	List<Integer> houndXCoordinates = new ArrayList<Integer>();
    	houndXCoordinates.add((Integer)configuration.getXLocationHound1());
    	houndXCoordinates.add((Integer)configuration.getXLocationHound2());
    	houndXCoordinates.add((Integer)configuration.getXLocationHound3());
    	
    	int houndCounter = 0;
    	for (Integer houndXCoordinate : houndXCoordinates){
    		if (houndXCoordinate == 3){
    			houndCounter++;
    		}
    	}
    	if (houndCounter == 3){
    		return true;
    	}
    	return false;
    }
    private boolean isStalling(BoardConfiguration configuration) throws HareHoundServiceException{
    	String sqlGetBoardConfiguration = "SELECT configuration_id FROM configuration WHERE game_id = :gameId " +
    			"AND hare_x_location =:hareXLocation AND hare_y_location =:hareYLocation AND hound_1_x_location =:hound1XLocation AND hound_1_y_location =:hound1YLocation " +
    			"AND hound_2_x_location =:hound2XLocation AND hound_2_y_location =:hound2YLocation AND hound_3_x_location =:hound3XLocation " +
    			"AND hound_3_y_location =:hound3YLocation";
    	System.out.println("is stalling game id" + configuration.getGameId());
    	try (Connection conn = database.open()){
    		List<BoardConfiguration> configurations = conn.createQuery(sqlGetBoardConfiguration)
       				.addParameter("hareXLocation", configuration.getXLocationHare())
       				.addParameter("hareYLocation", configuration.getYLocationHare())
       				.addParameter("hound1XLocation", configuration.getXLocationHound1())      				
       				.addParameter("hound1YLocation", configuration.getYLocationHound1())
       				.addParameter("hound2XLocation", configuration.getXLocationHound2())       				
       				.addParameter("hound2YLocation", configuration.getYLocationHound2())
       				.addParameter("hound3XLocation", configuration.getXLocationHound3())
       				.addParameter("hound3YLocation", configuration.getYLocationHound3())
    				.addParameter("gameId", configuration.getGameId())
       				.addColumnMapping("configuration_id", "id")
       				.executeAndFetch(BoardConfiguration.class);
    		System.out.println("configurations.size() = " + configurations.size());
    		if (configurations.size() >= 3){
    			return true;
    		}
    		return false;
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.isStalling: Failed to query database for player: %s", configuration.getId()), ex);
    		throw new HareHoundServiceException(String.format("HareHoundService.isStalling: Failed to query database of id %s", configuration.getId()), ex);
    	}
    }
    public static class HareHoundServiceException extends Exception {
		public HareHoundServiceException(String message, Throwable cause) {
       		super(message, cause);
   		}
	}

}
