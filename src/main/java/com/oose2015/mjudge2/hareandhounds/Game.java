package com.oose2015.mjudge2.hareandhounds;

public class Game {
	private int status;
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
	public void setGameId(int gameId){
		this.gameId = gameId;
	}
	public int getStatus(){
		return this.status;
	}
	public int getGameId(){
		return this.gameId;
	}
	public void mapToDatabase(){
		
	}
	public void mapFromDatabase(){
		
	}
}
