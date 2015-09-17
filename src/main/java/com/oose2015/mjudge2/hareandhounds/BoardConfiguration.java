package com.oose2015.mjudge2.hareandhounds;

import java.util.ArrayList;
import java.util.List;
/**
 * A board configuration holds the coordinates of the hare and three hound game pieces.
 * The board configuration is also associated with a specific game, and therefore also 
 * has a gameId field, in addition to a configuration id field. 
 */
public class BoardConfiguration {
	private int xLocationHound1;
	private int yLocationHound1;
	private int xLocationHound2;
	private int yLocationHound2;
	private int xLocationHound3;
	private int yLocationHound3;
	private int xLocationHare;
	private int yLocationHare;
	private int gameId;
	private int frequency;
	private int id;

	/**
     * Sets the x coordinate of the first hound's location.
     * 
     * @param xLocationHound1 the first hound's x coordinate
     */
	public void setXLocationHound1(int xLocationHound1){
		this.xLocationHound1 = xLocationHound1;
	}
	/**
     * Sets the x coordinate of the second hound's location.
     * 
     * @param xLocationHound2 the second hound's x coordinate
     */
	public void setXLocationHound2(int xLocationHound2){
		this.xLocationHound2 = xLocationHound2;
	}
	/**
     * Sets the x coordinate of the third hound's location.
     * 
     * @param xLocationHound3 the third hound's x coordinate
     */
	public void setXLocationHound3(int xLocationHound3){
		this.xLocationHound3 = xLocationHound3;
	}
	/**
     * Sets the y coordinate of the first hound's location.
     * 
     * @param yLocationHound1 the first hound's y coordinate
     */
	public void setYLocationHound1(int yLocationHound1){
		this.yLocationHound1 = yLocationHound1;
	}
	/**
     * Sets the y coordinate of the second hound's location.
     * 
     * @param yLocationHound2 the second hound's y coordinate
     */
	public void setYLocationHound2(int yLocationHound2){
		this.yLocationHound2 = yLocationHound2;
	}
	/**
     * Sets the y coordinate of the third hound's location.
     * 
     * @param yLocationHound3 the third hound's y coordinate
     */
	public void setYLocationHound3(int yLocationHound3){
		this.yLocationHound3 = yLocationHound3;
	}
	/**
     * Sets the y coordinate of the hare's location.
     * 
     * @param yLocationHare the hare's y coordinate
     */
	public void setYLocationHare(int yLocationHare){
		this.yLocationHare = yLocationHare;
	}
	/**
     * Sets the x coordinate of the hare's location.
     * 
     * @param xLocationHare the hare's x coordinate
     */
	public void setXLocationHare(int xLocationHare){
		this.xLocationHare = xLocationHare;
	}
	/**
     * Sets the id of the game with which the board configuration is associated.
     * 
     * @param gameId the game id 
     */
	public void setGameId(int gameId){
		this.gameId = gameId;
	}
	/**
     * Sets the number of times the configuration has occurred over the course of the game.
     * 
     * @param frequency the frequency
     */
	public void setFrequency(int frequency){
		this.frequency = frequency;
	}
	/**
     * Gets the x coordinate of the first of the hound pieces.
     * 
     * @returns the first hound's x coordinate
     */
	public int getXLocationHound1(){
		return this.xLocationHound1;
	}
	/**
     * Gets the x coordinate of the second of the hound pieces.
     * 
     * @returns the second hound's x coordinate
     */
	public int getXLocationHound2(){
		return this.xLocationHound2;
	}
	/**
     * Gets the x coordinate of the third of the hound pieces.
     * 
     * @returns the third hound's x coordinate
     */
	public int getXLocationHound3(){
		return this.xLocationHound3;
	}
	/**
     * Gets the y coordinate of the first of the hound pieces.
     * 
     * @returns the first hound's y coordinate
     */
	public int getYLocationHound1(){
		return this.yLocationHound1;
	}
	/**
     * Gets the y coordinate of the second of the hound pieces.
     * 
     * @returns the second hound's y coordinate
     */
	public int getYLocationHound2(){
		return this.yLocationHound2;
	}
	/**
     * Gets the y coordinate of the third of the hound pieces.
     * 
     * @returns the third hound's y coordinate
     */
	public int getYLocationHound3(){
		return this.yLocationHound3;
	}
	/**
     * Gets the x coordinate of the hare piece.
     * 
     * @returns the hare's x coordinate
     */
	public int getXLocationHare(){
		return this.xLocationHare;
	}
	/**
     * Gets the y coordinate of the hare piece.
     * 
     * @returns the hare's y coordinate
     */
	public int getYLocationHare(){
		return this.yLocationHare;
	}
	/**
     * Gets the id of the game with which the board configuration is associated.
     * 
     * @returns the id of the game
     */
	public int getGameId(){
		return this.gameId;
	}
	/**
     * Gets the number of times the game configuration has occurred.
     * 
     * @returns the frequency of the game configuration
     */
	public int getFrequency(){
		return this.frequency;
	}
	/**
     * Gets the id of the game configuration
     * 
     * @returns the id of the game configuration
     */
	public int getId(){
		return this.id;
	}
	/**
     * Creates a board configuration with the default hare and hound locations.
     * 
     */
	public void createDefaultBoardConfiguration(){
		this.setXLocationHare(4);
		this.setYLocationHare(1);
		this.setXLocationHound1(0);
		this.setYLocationHound1(1);
		this.setXLocationHound2(1);
		this.setYLocationHound2(0);
		this.setXLocationHound3(1);
		this.setYLocationHound3(2);
	}
	/**
     * Checks whether the location to which the piece is moving is occupied.
     * 
     * @param toY the location to which the piece is moving
     * @param toX the location to which the piece is moving
     * @return true if the location is occupied, false if it is not
     */
    public boolean isLocationOccupied(int toX, int toY){
    	if ((toX == this.getXLocationHound1()) && (toY == this.getYLocationHound1())){
    		return true;
    	}
   		if ((toX == this.getXLocationHound2()) && (toY == this.getYLocationHound2())){
    		return true;
   		}
    	if ((toX == this.getXLocationHound3()) && (toY == this.getYLocationHound3())){
    		return true;
    	}
    	if ((toX == this.getXLocationHare()) && (toY == this.getYLocationHare())){
    		return true;
    	}
    	return false;
    }
	/**
     * Moves the piece from one location to another location.
     * 
     * @param move the move
     */
    public void movePiece(Move move){
    	if (move.getPieceType().equals("HARE")){
    		this.setXLocationHare(move.getToX());
    		this.setYLocationHare(move.getToY());
    	} else {
    		if (this.getXLocationHound1() == move.getFromX() && this.getYLocationHound1() == move.getFromY()){
    			this.setXLocationHound1(move.getToX());
    			this.setYLocationHound1(move.getToY());
    		} else if (this.getXLocationHound2() == move.getFromX() && this.getYLocationHound2() == move.getFromY()){
    			this.setXLocationHound2(move.getToX());
    			this.setYLocationHound2(move.getToY());
    		} else if (this.getXLocationHound3() == move.getFromX() && this.getYLocationHound3() == move.getFromY()){
    			this.setXLocationHound3(move.getToX());
    			this.setYLocationHound3(move.getToY());
    		}
    	}
    }
	/**
     * Checks whether there are hounds to the left of the hare. This is done by 
     * checking whether the x coordinates of the hound and hare pieces are the same. 
     * 
     * @return true if there are no hounds to the left of a hare, false otherwise
     */
    public boolean noHoundToLeftOfHare(){
    	List<Integer> houndXCoordinates = new ArrayList<Integer>();
    	houndXCoordinates.add((Integer)this.getXLocationHound1());
    	houndXCoordinates.add((Integer)this.getXLocationHound2());
    	houndXCoordinates.add((Integer)this.getXLocationHound3());
    	
    	int houndCounter = 0;
    	for (Integer houndXCoordinate : houndXCoordinates){
    		if (this.getXLocationHare() <= houndXCoordinate){
    			houndCounter++;
    		}
    	}
    	if (houndCounter == 3){
    		return true;
    	}
    	return false;
    }
	/**
     * Checks whether the hare is trapped. The hare is only trapped when the hounds 
     * are located in the column where their x coordinates are equal to 3. 
     * 
     * @return true if the hare is trapped, false otherwise.
     */
    public boolean isHareTrapped(){
    	List<Integer> houndXCoordinates = new ArrayList<Integer>();
    	houndXCoordinates.add((Integer)this.getXLocationHound1());
    	houndXCoordinates.add((Integer)this.getXLocationHound2());
    	houndXCoordinates.add((Integer)this.getXLocationHound3());
    	
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
	/**
     * Since hound pieces are not unique, to check whether a hound location has been repeated, 
     * the hound piece needs to be compared to every other hound piece in the configuration.
     * 
     * @param xLoc the x location of a hound piece
     * @param yLoc the y location of a hound piece
     * @return true if the hound location has occurred, false otherwise.
     */
    public boolean hasThisHoundLocationOccurred(int xLoc, int yLoc){
    	List<Integer> xHoundCoordinates = new ArrayList<Integer>();
    	xHoundCoordinates.add(this.getXLocationHound1());
    	xHoundCoordinates.add(this.getXLocationHound2());
    	xHoundCoordinates.add(this.getXLocationHound3());
    	List<Integer> yHoundCoordinates = new ArrayList<Integer>();
    	yHoundCoordinates.add(this.getYLocationHound1());
    	yHoundCoordinates.add(this.getYLocationHound2());
    	yHoundCoordinates.add(this.getYLocationHound3());
    	for (int i = 0; i < yHoundCoordinates.size(); i++){
    		if (xHoundCoordinates.get(i) == xLoc && yHoundCoordinates.get(i) == yLoc){
    			return true;
    		}
    	}
    	return false;
    }
}
