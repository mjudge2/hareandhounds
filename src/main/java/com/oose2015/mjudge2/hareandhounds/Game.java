package com.oose2015.mjudge2.hareandhounds;

public class Game {
	private int status;
	private Player hare;
	private Player hound;
	private int gameId;
	private int mostRecentConfigurationId;
	
	public void setMostRecentConfigurationId(int mostRecentConfigurationId){
		this.mostRecentConfigurationId = mostRecentConfigurationId;
	}
	public int getMostRecentConfigurationId(){
		return this.mostRecentConfigurationId;
	}
	public void setStatus(int status){
		this.status = status;
	}
	public void setHare(Player hare){
		this.hare = hare;
	}
	public void setHound(Player hound){
		this.hound = hound;
	}
	public void setGameId(int gameId){
		this.gameId = gameId;
	}
	public int getStatus(){
		return this.status;
	}
	public Player getHare(){
		return this.hare;
	}
	public Player getHound(){
		return this.hound;
	}
	public int getGameId(){
		return this.gameId;
	}
	public void mapToDatabase(){
		
	}
	public void mapFromDatabase(){
		
	}
}
