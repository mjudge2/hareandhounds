package com.oose2015.mjudge2.hareandhounds;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
/**
 * A player holds information relevant to an individual playing the hare and hounds game.
 * Specifically, it holds the player's id, the id of the game in which the player is participating,
 * the player's piece type (HARE or HOUND), and a list of the current locations of the pieces.
 */
public class Player {
	private int gameId;
	private int playerId;
	private String pieceType;
	private List<Point> currentLocation;
    
	/**
     * Sets the piece type of the player.
     * 
     * @param pieceType the type of the player
     */
	public void setPieceType(String pieceType){
		this.pieceType = pieceType;
	} 
	/**
     * Sets the list of current locations of the player's pieces.
     * 
     * @param currentLocation the list of piece locations
     */
	public void setCurrentLocation(List<Point> currentLocation){
		this.currentLocation = currentLocation;
	}
	/**
     * Sets the id of the game in which the player is participating.
     * 
     * @param gameId the id of the game
     */
	public void setGameId(int gameId){
		this.gameId = gameId;
	}   
	/**
     * Sets the id of the player.
     * 
     * @param playerId the id of the player
     */
	public void setPlayerId(int playerId){
		this.playerId = playerId;
	}
	/**
     * Returns the piece type of the move.
     * 
     * @returns the piece type of the move
     */
	public String getPieceType(){
		return this.pieceType;
	}
	/**
     * Returns the list of current locations of the player's pieces.
     * 
     * @returns the locations list
     */
	public List<Point> getCurrentLocation(){
		return this.currentLocation;
	}
	/**
     * Returns the id of the game in which the player is participating.
     * 
     * @returns the id of the game
     */
	public int getGameId(){
		return this.gameId;
	}
	/**
     * Returns the id of the player.
     * 
     * @returns the player id
     */
	public int getPlayerId(){
		return this.playerId;
	}
	/**
     * Converts the x locations of the player's pieces from Points to Strings.
     * 
     * @returns the list of x locations in String format
     */
	public List<String> getXLocationsAsStrings(){
		List<String> xLocations = new ArrayList<String>();
		for (int i = 0; i < currentLocation.size(); i++){
			Integer x = (int) currentLocation.get(i).getX();
			xLocations.add(x.toString());
		}
		return xLocations;
	}
	/**
     * Converts the y locations of the player's pieces from Points to Strings.
     * 
     * @returns the list of y locations in String format
     */
	public List<String> getYLocationsAsStrings(){
		List<String> yLocations = new ArrayList<String>();
		for (int i = 0; i < currentLocation.size(); i++){
			Integer y = (int) currentLocation.get(i).getY();
			yLocations.add(y.toString());
		}
		return yLocations;
	}
	/**
     * Checks whether the player's piece type is HARE.
     * 
     * @returns true if the piece type is HARE, false if the piece type is HOUND
     */
	public boolean isHare(){
		if (this.pieceType.equals("HARE")){
			return true;
		}
		return false;
	}
	/**
     * Sets the default locations of the hare and hound objects, depending upon the piece
     * type of the player.
     */
	public void setDefaultLocation(){
		this.currentLocation = new ArrayList<Point>();
    	if (this.isHare()){
    		this.currentLocation.add(new Point(4,1));
    	} else {
    		this.currentLocation.add(new Point(0,1));
    		this.currentLocation.add(new Point(1,0));
    		this.currentLocation.add(new Point(1,2));
    	}
	}
}
