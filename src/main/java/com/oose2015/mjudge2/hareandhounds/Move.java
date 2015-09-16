package com.oose2015.mjudge2.hareandhounds;

public class Move {
	private int playerId;
	private String pieceType;
	private int toX;
	private int toY;
	private int fromX;
	private int fromY;
	
	public void setPieceType(String pieceType){
		this.pieceType = pieceType;
	}
	public void setPlayerId(int playerId){
		this.playerId = playerId;
	}
	public void setToX(int toX){
		this.toX = toX;
	}
	public void setToY(int toY){
		this.toY = toY;
	}
	public void setFromX(int fromX){
		this.fromX = fromX;
	}
	public void setFromY(int fromY){
		this.fromY = fromY;
	}
	public int getPlayerId(){
		return this.playerId;
	}
	public int getToX(){
		return this.toX;
	}
	public int getToY(){
		return this.toY;
	}
	public int getFromX(){
		return this.fromX;
	}
	public int getFromY(){
		return this.fromY;
	}
	public String getPieceType(){
		return this.pieceType;
	}
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
