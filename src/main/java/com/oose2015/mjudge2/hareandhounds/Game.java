package com.oose2015.mjudge2.hareandhounds;
/**
 * A game is an instance of the hare and hounds game. It stores information relevant
 * to the game, such as its id and status. The object also contains the id of the
 * most recent legal board configuration that was played.
 */
public class Game {
	private int status;
	private int gameId;
	private int mostRecentConfigurationId;
	
	/**
     * Sets the id of the most recent legal board configuration that was played.
     * 
     * @param mostRecentConfigurationId the id of the board configuration
     */
	public void setMostRecentConfigurationId(int mostRecentConfigurationId){
		this.mostRecentConfigurationId = mostRecentConfigurationId;
	}
	/**
     * Sets the status of the game.
     * 
     * @param status the integer value corresponding to a specific game status
     */
	public void setStatus(int status){
		this.status = status;
	}
	/**
     * Sets the id of the game.
     * 
     * @param gameId the id of the game
     */
	public void setGameId(int gameId){
		this.gameId = gameId;
	}
	/**
     * Gets the id of the most recent legal board configuration that was played.
     * 
     * @returns the id of the board configuration
     */
	public int getMostRecentConfigurationId(){
		return this.mostRecentConfigurationId;
	}
	/**
     * Gets the status of the game
     * 
     * @returns the status of the game
     */	
	public int getStatus(){
		return this.status;
	}
	/**
     * Gets the id of the game
     * 
     * @returns the id of the game
     */	
	public int getGameId(){
		return this.gameId;
	}
}
