package com.oose2015.mjudge2.hareandhounds;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Player {
	private int gameId;
	private int toX;
	private int toY;
	private int fromX;
	private int fromY;
	private int playerId;
	private String pieceType;
	private List<Point> currentLocation;
	
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
	public void setPieceType(String pieceType){
		this.pieceType = pieceType;
	}
	public void setCurrentLocation(List<Point> currentLocation){
		this.currentLocation = currentLocation;
	}
	public void setGameId(int gameId){
		this.gameId = gameId;
	}
	public void setPlayerId(int playerId){
		this.playerId = playerId;
	}
	public String getPieceType(){
		return this.pieceType;
	}
	public List<Point> getCurrentLocation(){
		return this.currentLocation;
	}
	public int getGameId(){
		return this.gameId;
	}
	public int getPlayerId(){
		return this.playerId;
	}
	public List<String> getXLocationsAsStrings(){
		List<String> xLocations = new ArrayList<String>();
		for (int i = 0; i < currentLocation.size(); i++){
			Integer x = (int) currentLocation.get(i).getX();
			xLocations.add(x.toString());
		}
		return xLocations;
	}
	public List<String> getYLocationsAsStrings(){
		List<String> yLocations = new ArrayList<String>();
		for (int i = 0; i < currentLocation.size(); i++){
			Integer y = (int) currentLocation.get(i).getY();
			yLocations.add(y.toString());
		}
		return yLocations;
	}
	public boolean isHare(){
		if (this.pieceType.equals("HARE")){
			return true;
		}
		return false;
	}
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
