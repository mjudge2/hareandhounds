package com.oose2015.mjudge2.hareandhounds;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import javax.sql.DataSource;

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
    	Map<String, String> contentToReturn = new HashMap<String, String>();
    	Player player1 = new Gson().fromJson(body, Player.class);
    	player1.setDefaultLocation();

    	Game game = new Game();
    	game.setStatus(0);
    	int gameId = mapToDatabase(game);
    	game.setGameId(gameId);
    	
    	BoardConfiguration configuration = createBoardConfiguration(gameId, player1);
    	int configurationId = mapToDatabase(configuration);

    	game.setStatus(0);
    	game.setMostRecentConfigurationId(configurationId);
    	updateGame(game);
    	
    	player1.setGameId(gameId);
    	int playerId = mapToDatabase(player1);

    	contentToReturn.put("gameId", ((Integer)gameId).toString());
    	contentToReturn.put("playerId", ((Integer)playerId).toString());
    	contentToReturn.put("pieceType", player1.getPieceType());

    	return contentToReturn;
    }
    
    public Map<String, String> joinGame(String id) throws HareHoundServiceException{
    	Map<String, String> contentToReturn = new HashMap<String, String>();
    	Player player1 = new Player();
    	Game game;
    	int gameId = Integer.parseInt(id);
    	try {
    		game = getGame(gameId);
    		List<Player> players = getPlayersInGame(gameId);
    		if (players.size() > 1){
    			contentToReturn.put("reason", "MORE_THAN_ONE_PLAYER");
    			return contentToReturn;
    		} else if (players.size() == 0){
    			contentToReturn.put("reason", "NO_OTHER_PLAYERS");
    			return contentToReturn;
    		}
    		Player player2 = new Player();
    		player1 = players.get(0);
            if (player1.isHare()){
            	player2.setPieceType("HOUND");
            } else {
            	player2.setPieceType("HARE");
            }
            player2.setGameId(gameId);
        	player2.setDefaultLocation();
        	int player2Id = mapToDatabase(player2);
        	
            contentToReturn.put("gameId", ((Integer)gameId).toString());
           	contentToReturn.put("playerId", ((Integer)player2Id).toString());
          	contentToReturn.put("pieceType", player2.getPieceType());
          	
          	BoardConfiguration configuration = new BoardConfiguration();
          	configuration.createDefaultBoardConfiguration();
          	configuration.setGameId(gameId);
        	int configurationId = mapToDatabase(configuration);
        	
        	game.setStatus(2);
        	game.setMostRecentConfigurationId(configurationId);
        	updateGame(game);
        	
        	return contentToReturn;
        	
    	} catch (HareHoundServiceException ex){
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

    	try (Connection conn = database.open()){
    		Game game = getGame(Integer.parseInt(gameId));
    		BoardConfiguration configuration = getBoardConfiguration(game.getMostRecentConfigurationId());
    		
    		if (game.getStatus() != 0) {
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
    		}
    		return contentToReturn;
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.determineBoardState: Failed to query database for id: %s", gameId), ex);
            throw new HareHoundServiceException(String.format("HareHoundService.determineBoardState: Failed to query database for id: %s", gameId), ex);
    	}
    }
    public Map<String, String> playHareAndHounds(String id, String body) throws HareHoundServiceException{
    	Map<String, String> contentToReturn = new HashMap<String, String>();
        int newStatus;
        int gameId = Integer.parseInt(id);
    	
        Game game;
    	BoardConfiguration boardConfiguration;
    	Player player;
    	Move move = new Gson().fromJson(body, Move.class);
    	
    	try {
    		game = getGame(gameId);
    	} catch (HareHoundServiceException ex){
    		contentToReturn.put("reason", "INVALID_GAME_ID");
    		return contentToReturn;
    	}
    	try {
    		player = getPlayer(move.getPlayerId(), game.getGameId());
    		move.setPieceType(player.getPieceType());
    	} catch (HareHoundServiceException ex){
    		contentToReturn.put("reason", "INVALID_PLAYER_ID");
    		return contentToReturn;
    	}
		try {
			boardConfiguration = getBoardConfiguration(game.getMostRecentConfigurationId());
			boardConfiguration.setGameId(gameId);
			if (boardConfiguration.isLocationOccupied(move.getToX(), move.getToY())){
				contentToReturn.put("reason", "ILLEGAL_MOVE");
				return contentToReturn;
			}
		} catch (HareHoundServiceException ex){
			contentToReturn.put("reason", "ILLEGAL_MOVE");
			return contentToReturn;
		}
		
    	if (!isPlayersTurn(game, player.getPieceType())){
    		contentToReturn.put("reason", "INCORRECT_TURN");
    		return contentToReturn;
    	}
    	if (!move.isValidMove(boardConfiguration)){
    		contentToReturn.put("reason", "ILLEGAL_MOVE");
    		return contentToReturn;
    	} else {
    		boardConfiguration.movePiece(move);
    		
        	if (boardConfiguration.isHareTrapped()){
        		game.setStatus(5);
        	}
        	if (boardConfiguration.noHoundToLeftOfHare()){
        		game.setStatus(3);
        	}
        	if (isStalling(boardConfiguration)){
        		game.setStatus(4);
        	}
    	}
    	int configurationId = mapToDatabase(boardConfiguration);
    	game.setMostRecentConfigurationId((Integer)configurationId);
    	if (game.getStatus() == 2){
    		game.setStatus(1);
    	} else if (game.getStatus() == 1){
    		game.setStatus(2);
    	}
    	updateGame(game);
    	
    	contentToReturn.put("playerId", ((Integer)move.getPlayerId()).toString());
    	return contentToReturn;
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
    private boolean isStalling(BoardConfiguration configuration) throws HareHoundServiceException{
    	String sqlGetBoardConfiguration = "SELECT * FROM configuration WHERE game_id = :gameId " +
    			"AND hare_x_location =:hareXLocation AND hare_y_location =:hareYLocation";
    	try (Connection conn = database.open()){
    		List<BoardConfiguration> configurations = conn.createQuery(sqlGetBoardConfiguration)
    				.addParameter("gameId", configuration.getGameId())
       				.addParameter("hareXLocation", configuration.getXLocationHare())
       				.addParameter("hareYLocation", configuration.getYLocationHare())
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
       				.addColumnMapping("configuration_id", "id")
       				.executeAndFetch(BoardConfiguration.class);
    		if (configurations.size() > 3){
    			for (BoardConfiguration pastConfiguration : configurations){
    				if(hasThisHoundLocationOccurred(pastConfiguration, configuration.getXLocationHound1(),
    						configuration.getYLocationHound1())){
    					if (hasThisHoundLocationOccurred(pastConfiguration, configuration.getXLocationHound2(),
    							configuration.getYLocationHound2())){
    						if (hasThisHoundLocationOccurred(pastConfiguration, configuration.getXLocationHound3(),
    								configuration.getYLocationHound3())){
    							return true;
    						}
    					}
    				} 
    			}
    		}
    		return false;
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.isStalling: Failed to query database for player: %s", configuration.getId()), ex);
    		throw new HareHoundServiceException(String.format("HareHoundService.isStalling: Failed to query database of id %s", configuration.getId()), ex);
    	}
    }
    private boolean hasThisHoundLocationOccurred(BoardConfiguration configuration, int xLoc, int yLoc){
    	List<Integer> xHoundCoordinates = new ArrayList<Integer>();
    	xHoundCoordinates.add(configuration.getXLocationHound1());
    	xHoundCoordinates.add(configuration.getXLocationHound2());
    	xHoundCoordinates.add(configuration.getXLocationHound3());
    	List<Integer> yHoundCoordinates = new ArrayList<Integer>();
    	yHoundCoordinates.add(configuration.getYLocationHound1());
    	yHoundCoordinates.add(configuration.getYLocationHound2());
    	yHoundCoordinates.add(configuration.getYLocationHound3());
    	for (int i = 0; i < yHoundCoordinates.size(); i++){
    		if (xHoundCoordinates.get(i) == xLoc && yHoundCoordinates.get(i) == yLoc){
    			return true;
    		}
    	}
    	return false;
    }
    private void updateGame(Game game) throws HareHoundServiceException{
    	String updateGameStatusSql = "UPDATE game SET status = :status, most_recent_config = :configId WHERE game_id = :gameId";
    	try (Connection conn = database.open()){
    		conn.createQuery(updateGameStatusSql)
    			.bind(game)
    			.addParameter("status", game.getStatus())
    			.addParameter("configId", game.getMostRecentConfigurationId())
				.addParameter("gameId", game.getGameId())
				.executeUpdate();
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.getBoardConfiguration: Failed to query database for game:"), ex);
    		throw new HareHoundServiceException(String.format("HareHoundService.getBoardConfiguration: Failed to query database "), ex);
    	}
    }
    private BoardConfiguration createBoardConfiguration(int gameId, Player player){
    	BoardConfiguration configuration = new BoardConfiguration();
    	configuration.setGameId(gameId);
		configuration.setFrequency(1);
    	if (player.isHare()){
    		configuration.setXLocationHare((int)player.getCurrentLocation().get(0).getX());
    		configuration.setYLocationHare((int) player.getCurrentLocation().get(0).getY());
    	} else {
    		configuration.setXLocationHound1((int)player.getCurrentLocation().get(0).getX());
    		configuration.setYLocationHound1((int)player.getCurrentLocation().get(0).getY());
    		configuration.setXLocationHound2((int)player.getCurrentLocation().get(1).getX());
    		configuration.setYLocationHound2((int)player.getCurrentLocation().get(1).getY());
    		configuration.setXLocationHound3((int)player.getCurrentLocation().get(2).getX());
    		configuration.setYLocationHound3((int)player.getCurrentLocation().get(2).getY());
    	}
		return configuration;
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
    private List<Player> getPlayersInGame(int gameId) throws HareHoundServiceException{
    	String sqlIsValidPlayer = "SELECT player_id, game_id, piece_type FROM player WHERE game_id = :gameId";
    	try (Connection conn = database.open()){
    		return conn.createQuery(sqlIsValidPlayer)
    			.addParameter("gameId", gameId)
    			.addColumnMapping("player_id", "playerId")
    			.addColumnMapping("game_id", "gameId")
    			.addColumnMapping("piece_type", "pieceType")
    			.executeAndFetch(Player.class);
    	} catch(Sql2oException ex){
    		logger.error(String.format("HareHoundService.isValidPlayerId: Failed to query database for player: %s", gameId), ex);
    		throw new HareHoundServiceException(String.format("HareHoundService.isValidPlayerID: Failed to query database of id %s", gameId), ex);
    	}
    }
    private int mapToDatabase(Game game) throws HareHoundServiceException{
    	String insertGameSql = "INSERT INTO game (status) VALUES (:gameStatus)";
    	try (Connection conn = database.open()){
    		Object gameId = conn.createQuery(insertGameSql, true)
    				.addParameter("gameStatus", game.getStatus())
    				.executeUpdate()
    				.getKey();
    		return (int) gameId;
    	} catch(Sql2oException ex){
    		logger.error("HareHoundService.mapGameToDatabase: Failed to create new entry");
    		throw new HareHoundServiceException(String.format("HareHoundService.createNewGame: failed to insert new game"), ex);
    	}
    }
    private int mapToDatabase(Player player) throws HareHoundServiceException{
    	String insertHarePlayerSql = "INSERT INTO player (piece_type, x_location_1, y_location_1, game_id) " + 
    			"VALUES (:pieceType, :xLocation1, :yLocation1, :gameId)";
		String insertHoundPlayerSql = "INSERT INTO player (piece_type, x_location_1, y_location_1, " +
				"x_location_2, y_location_2, x_location_3, y_location_3, game_id) " + 
    			"VALUES (:pieceType, :xLocation1, :yLocation1, :xLocation2, :yLocation2, :xLocation3, :yLocation3, :gameId)";
    	Object playerId;
		try (Connection conn = database.open()){
    		if (player.isHare()){
    			playerId = conn.createQuery(insertHarePlayerSql, true)
    					.addParameter("pieceType", player.getPieceType())
    					.addParameter("xLocation1", player.getXLocationsAsStrings().get(0))
    					.addParameter("yLocation1", player.getYLocationsAsStrings().get(0))
    					.addParameter("gameId", player.getGameId())
    					.executeUpdate()
    					.getKey();
    		} else {
    			playerId = conn.createQuery(insertHoundPlayerSql, true)
   					.addParameter("pieceType", player.getPieceType())
   					.addParameter("xLocation1", player.getXLocationsAsStrings().get(0))
   					.addParameter("yLocation1", player.getYLocationsAsStrings().get(0))
   					.addParameter("xLocation2", player.getXLocationsAsStrings().get(1))
    				.addParameter("yLocation2", player.getYLocationsAsStrings().get(1))
    				.addParameter("xLocation3", player.getXLocationsAsStrings().get(2))
    				.addParameter("yLocation3", player.getYLocationsAsStrings().get(2))
    				.addParameter("gameId", player.getGameId())
    				.executeUpdate()
					.getKey();
    		}
    		return (int) playerId;
    	} catch(Sql2oException ex){
    		logger.error("HareHoundService.mapGameToDatabase: Failed to create new entry");
    		throw new HareHoundServiceException(String.format("HareHoundService.createNewGame: failed to insert new game"), ex);
    	}
    }
    private int mapToDatabase(BoardConfiguration configuration) throws HareHoundServiceException {
   		String insertBoardConfigurationSql = "INSERT INTO configuration (hare_x_location, hare_y_location, hound_1_x_location, " +
   				"hound_1_y_location, hound_2_x_location, hound_2_y_location, hound_3_x_location, hound_3_y_location, frequency, game_id) " +
   				"VALUES (:hareXLocation, :hareYLocation, :hound1XLocation, :hound1YLocation, :hound2XLocation, :hound2YLocation, :hound3XLocation, " +
   				":hound3YLocation, :frequency, :gameId)";
   		try (Connection conn = database.open()){
   			Object configurationId = conn.createQuery(insertBoardConfigurationSql, true)
   				.addParameter("hareXLocation", configuration.getXLocationHare())
   				.addParameter("hareYLocation", configuration.getYLocationHare())
   				.addParameter("hound1XLocation", configuration.getXLocationHound1())      				
   				.addParameter("hound1YLocation", configuration.getYLocationHound1())
   				.addParameter("hound2XLocation", configuration.getXLocationHound2())       				
   				.addParameter("hound2YLocation", configuration.getYLocationHound2())
   				.addParameter("hound3XLocation", configuration.getXLocationHound3())
   				.addParameter("hound3YLocation", configuration.getYLocationHound3())
   				.addParameter("frequency", configuration.getFrequency())
   				.addParameter("gameId", configuration.getGameId())
   				.executeUpdate()
   				.getKey();
   			return (int) configurationId;
   		} catch(Sql2oException ex){
    		logger.error("HareHoundService.mapGameToDatabase: Failed to create new entry");
    		throw new HareHoundServiceException(String.format("HareHoundService.createNewGame: failed to insert new game"), ex);
    	}
    }
    public static class HareHoundServiceException extends Exception {
		public HareHoundServiceException(String message, Throwable cause) {
       		super(message, cause);
   		}
	}

}
