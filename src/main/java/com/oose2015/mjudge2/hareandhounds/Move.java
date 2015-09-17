package com.oose2015.mjudge2.hareandhounds;
/**
 * A move holds the information relevant to a player's turn. Specifically, it 
 * specifies the locations from which and to which the player is moving, the
 * player's id, and the player's piece type. 
 */
public class Move {
	private int playerId;
	private String pieceType;
	private int toX;
	private int toY;
	private int fromX;
	private int fromY;
    
	/**
     * Sets the piece type of the move.
     * 
     * @param pieceType the type of the piece that is making the move
     */
	public void setPieceType(String pieceType){
		this.pieceType = pieceType;
	}
	/**
     * Sets the player id of the move.
     * 
     * @param playerId the id of player that is making the move
     */
	public void setPlayerId(int playerId){
		this.playerId = playerId;
	}
	/**
     * Sets the x coordinate of the location to which the piece is moving.
     * 
     * @param toX the x coordinate
     */
	public void setToX(int toX){
		this.toX = toX;
	}
	/**
     * Sets the y coordinate of the location to which the piece is moving.
     * 
     * @param toY the y coordinate
     */
	public void setToY(int toY){
		this.toY = toY;
	}
	/**
     * Sets the x coordinate of the location from which the piece is moving.
     * 
     * @param fromX the x coordinate
     */
	public void setFromX(int fromX){
		this.fromX = fromX;
	}
	/**
     * Sets the y coordinate of the location from which the piece is moving.
     * 
     * @param fromY the y coordinate
     */
	public void setFromY(int fromY){
		this.fromY = fromY;
	}
	/**
     * Gets the id of the player that is making the move.
     * 
     * @returns the id of the player that moving
     */
	public int getPlayerId(){
		return this.playerId;
	}
	/**
     * Gets the x coordinate of the location to which the piece is moving.
     * 
     * @returns the x coordinate 
     */
	public int getToX(){
		return this.toX;
	}
	/**
     * Gets the y coordinate of the location to which the piece is moving.
     * 
     * @returns the y coordinate 
     */
	public int getToY(){
		return this.toY;
	}
	/**
     * Gets the x coordinate of the location from which the piece is moving.
     * 
     * @returns the x coordinate 
     */
	public int getFromX(){
		return this.fromX;
	}
	/**
     * Gets the y coordinate of the location from which the piece is moving.
     * 
     * @returns the y coordinate 
     */
	public int getFromY(){
		return this.fromY;
	}
	/**
     * Gets the piece type of the player that is making the move.
     * 
     * @returns the piece type
     */
	public String getPieceType(){
		return this.pieceType;
	}
	/**
     * Checks whether the move is valid. Confirms that the location to which a piece is moving is not occupied by another
     * hound or hare piece. 
     * 
     * A move is valid if the shifts in the x coordinates and the y coordinates are both less than or equal to 1. Furthermore, 
     * since not every location that meets this requirement is connected to another location, an additional check is implemented. 
     * If the sum of the x and y coordinates from which the player is moving mod 2 is equal to 0, and the sum of the x and y coordinates
     * to which the player is moving mod 2 is equal to 0, then the move is not valid. 
     * 
     * @returns true if the move is valid, false if it is not
     */
    public boolean isValidMove(BoardConfiguration configuration){
    	if (this.pieceType.equals("HOUND")){
    		if (this.fromX > this.toX){
    			return false;
    		}
    		if ((configuration.getXLocationHound1() != this.fromX) || (configuration.getYLocationHound1() != this.fromY)){
        		if ((configuration.getXLocationHound2() != this.fromX) || (configuration.getYLocationHound2() != this.fromY)){
            		if ((configuration.getXLocationHound3() != this.fromX) || (configuration.getYLocationHound3() != this.fromY)){
            			return false;
            		}
        		}
    		}
    	} else {
    		if ((configuration.getXLocationHare() != this.fromX) || (configuration.getYLocationHare() != this.fromY)){
    			return false;
    		}
    	}
    	int xShift = Math.abs(this.toX - this.fromX);
    	int yShift = Math.abs(this.toY - this.fromY);
    	int toSum = Math.abs(this.toX + this.toY);
    	int fromSum = Math.abs(this.fromX + this.fromY);
    	if (xShift <= 1 && yShift <= 1){
    		if ((fromSum % 2 == 0) && (toSum % 2 == 0)){
    			return false;
    		}
    		return true;
    	}
    	return false;
    }
}
