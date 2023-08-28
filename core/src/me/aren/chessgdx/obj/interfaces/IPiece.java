package me.aren.chessgdx.obj.interfaces;

import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.network.ClientSideConnection;
import me.aren.chessgdx.network.GameServer;
import me.aren.chessgdx.network.ServerSideConnection;
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.screens.PlayScreen;

public interface IPiece extends IGameObject {
	int WIDTH = 96, HEIGHT = 96;
	// TODO: Deprecated functions: setValidPositionsCalculated, validPositionsCalculated
	default void update(float delta, Board board, OrthographicCamera cam) {
		// TODO : Maybe this should not be called every frame
		calculateValidPositions(board);
		
		// TODO: If you click between two tiles it will select both
		
		if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
		    Vector3 touch = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		    Vector2 finalPos = new Vector2(touch.x, touch.y);
		    boolean isWhiteMultiplayer = getCurrentID() == 1 ? true : false;
		    
		    if(getClientSideConnection().getTurnInfo() == getCurrentID() && isWhite() == isWhiteMultiplayer) {
		    	
		    	//System.out.println("ID: " + getCurrentID() + " - Turn: " + GameServer.turn);
			    
				    if(new Rectangle(getParent().getPos().x, getParent().getPos().y, WIDTH, HEIGHT).contains(finalPos)){
				    	setSelected(true);
				    	// --------------- DEBUG
				    	if(GlobalSettings.debugModeEnabled) {
				        	Gdx.app.log("DEBUG", isWhite() ? "White " + getName() + " selected at " + "X: " + getParent().getPos().x / 96 + " Y: " + (7 - getParent().getPos().y / 96) 
				        			: "Black " + getName() + " selected at " + "X: " + getParent().getPos().x / 96 + " Y: " + (7 - getParent().getPos().y / 96));
				        	String positionsToPrint = "This " + getName() + " can move to these positions: ";
				        	
				        	for(Tile validTile : getValidPositions()) {
				        		positionsToPrint += "\nX: " + board.findIndexOfTile(validTile).x + " Y: " + board.findIndexOfTile(validTile).y;
				        	}
				        	
				        	Gdx.app.log("DEBUG", positionsToPrint);
				        }
				        // --------------- DEBUG
				    	
				        getParent().setGreen(true);
				        
				        for(Tile tile : getValidPositions()) {
				        	tile.setGreen(true);
				        }
				        
				        
				        
				        //justMoved = false;
				    } else if(isSelected()) {
				    	for(Tile tile : getValidPositions()) {
				    		if(new Rectangle(tile.getPos().x, tile.getPos().y, WIDTH, HEIGHT).contains(finalPos)) {
				    			getParent().setGreen(false);
				    			getParent().removePiece();
	
								if(tile.isCapturable()) {
									if(board.turnWhite) {
										board.capturedPiecesWhite.add(tile.getPiece());
										if(GlobalSettings.debugModeEnabled) {
											Gdx.app.log("DEBUG", "White captured a " + tile.getPiece().getName());
										}
									} else {
										if(GlobalSettings.debugModeEnabled) {
											Gdx.app.log("DEBUG", "Black captured a " + tile.getPiece().getName());
										}
										board.capturedPiecesBlack.add(tile.getPiece());
									}
	
									tile.removePiece();
									tile.setCapturable(false);
									afterCapture();
								}
	
				    			tile.addPiece(this);
				    			afterMove();
				    			// NETWORKING CODE THAT SHOULD BE CHANGED HERE!!!!!!!!!!!!!!!!!!!!!
				    			
				    			board.setTurn(false, true);
				    			getClientSideConnection().setTurn(getClientSideConnection().getTurnInfo() == 1 ? 2 : 1);
				    			System.out.println("AFTER SET TURN::: " + getClientSideConnection().getTurnInfo());
				    			if(GlobalSettings.debugModeEnabled) {
				    				Gdx.app.log("DEBUG", "-------- Turn: " + (board.turnWhite ? " WHITE " : "BLACK ") + "--------");
				    				Gdx.app.log("DEBUG", "Recalculating valid positions!");
				    			}
				    			
				    			calculateValidPositions(board);
				    			
				    		} else {
				    			setSelected(false);
					    		tile.setGreen(false);
				    		}
				    	}
				    	
				    	if(getParent() != null) getParent().setGreen(false);
				    	
				    	for(Tile[] tiles : board.tiles) {
				    		for(Tile tile : tiles) {
				    			if(tile.isGreen()) tile.setGreen(false);
				    			if(tile.doesHavePiece()) {
		    						tile.getPiece().afterTurnChange();
		    					}
				    		}
				    	}
				    	//calculateValidPositions(board);
				    	setSelected(false);
				    }
		    }
		}
	};
	public String getName();
	public void render(float delta);
	public void setParent(Tile tile);
	public Tile getParent();
	public boolean isSelected();
	public void setSelected(boolean selected);
	public boolean validPositionsCalculated();
	public void setValidPositionsCalculated(boolean calculated);
	public boolean isWhite();
	public void calculateValidPositions(Board board);
	public void afterMove();
	public LinkedBlockingQueue<Tile> getValidPositions();
	public boolean isCaptured();
	public void setCaptured(boolean captured);
	public void afterCapture();
	public void afterTurnChange();
	public int getCurrentID();
	public ClientSideConnection getClientSideConnection();
}
