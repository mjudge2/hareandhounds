package com.oose2015.mjudge2.hareandhounds;

import java.util.ArrayList;
import java.util.List;

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

	public int getId(){
		return this.id;
	}
	public void setXLocationHound1(int xLocationHound1){
		this.xLocationHound1 = xLocationHound1;
	}
	public void setXLocationHound2(int xLocationHound2){
		this.xLocationHound2 = xLocationHound2;
	}
	public void setXLocationHound3(int xLocationHound3){
		this.xLocationHound3 = xLocationHound3;
	}
	public void setYLocationHound1(int yLocationHound1){
		this.yLocationHound1 = yLocationHound1;
	}
	public void setYLocationHound2(int yLocationHound2){
		this.yLocationHound2 = yLocationHound2;
	}
	public void setYLocationHound3(int yLocationHound3){
		this.yLocationHound3 = yLocationHound3;
	}
	public void setYLocationHare(int yLocationHare){
		this.yLocationHare = yLocationHare;
	}
	public void setXLocationHare(int xLocationHare){
		this.xLocationHare = xLocationHare;
	}
	public void setGameId(int gameId){
		this.gameId = gameId;
	}
	public void setFrequency(int frequency){
		this.frequency = frequency;
	}
	public int getXLocationHound1(){
		return this.xLocationHound1;
	}
	public int getXLocationHound2(){
		return this.xLocationHound2;
	}
	public int getXLocationHound3(){
		return this.xLocationHound3;
	}
	public int getYLocationHound1(){
		return this.yLocationHound1;
	}
	public int getYLocationHound2(){
		return this.yLocationHound2;
	}
	public int getYLocationHound3(){
		return this.yLocationHound3;
	}
	public int getXLocationHare(){
		return this.xLocationHare;
	}
	public int getYLocationHare(){
		return this.yLocationHare;
	}
	public int getGameId(){
		return this.gameId;
	}
	public int getFrequency(){
		return this.frequency;
	}
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
}
