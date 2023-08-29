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
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;

public interface IPiece extends IGameObject {
	int WIDTH = 96, HEIGHT = 96;

	// TODO: Deprecated functions: setValidPositionsCalculated, validPositionsCalculated
	default void update(float delta, Board board, OrthographicCamera cam) {
		// TODO : Maybe this should not be called every frame
		calculateValidPositions(board);
		
		if(getClientSideConnection().receiveTurnCount() != 0 && !getClientSideConnection().boardUpdated) {
			 int turnSide = getClientSideConnection().receiveTurnCount() % 2;
			 
			 if(turnSide == 0 && getCurrentID() == 1) {
				 getClientSideConnection().onReceivedMovementInfo();
			 } else if(turnSide == 1 && getCurrentID() == 2) {
				 getClientSideConnection().onReceivedMovementInfo();
			 }
		}
		
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
				    			getClientSideConnection().sendMovement(
				    					board.boardIndexTo1D(board.tiles[(int) board.findIndexOfTile(getParent()).y][(int) board.findIndexOfTile(getParent()).x]),
				    					board.boardIndexTo1D(board.tiles[(int) board.findIndexOfTile(tile).y][(int) board.findIndexOfTile(tile).x]));
				    			int[] movement = getClientSideConnection().receiveMovementInfo();
				    			System.out.println("MOVEMENT: " + movement[0] + " -> " + movement[1]);
				    			
				    			
				    			
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
				    			getClientSideConnection().sendTurnCount(getClientSideConnection().receiveTurnCount() + 1);
				    			getClientSideConnection().boardUpdated = false;
				    			
				    			System.out.println("AFTER SET TURN::: " + getClientSideConnection().getTurnInfo());
				    			
				    			if(GlobalSettings.debugModeEnabled) {
				    				Gdx.app.log("DEBUG", "-------- Turn: " + (board.turnWhite ? " WHITE " : "BLACK ") + "--------");
				    				Gdx.app.log("DEBUG", "Recalculating valid positions!");
				    			}
				    			
				    			calculateValidPositions(board);
				    			
						    	if(board.tiles[(int) board.findIndexOfTile(board.indexToBoardIndex(movement[0])).y][(int) board.findIndexOfTile(board.indexToBoardIndex(movement[0])).x].doesHavePiece()) {
						    		IPiece originalPiece = board.tiles[(int) board.findIndexOfTile(board.indexToBoardIndex(movement[0])).y][(int) board.findIndexOfTile(board.indexToBoardIndex(movement[0])).x].getPiece();
						    		board.tiles[(int) board.findIndexOfTile(board.indexToBoardIndex(movement[0])).y][(int) board.findIndexOfTile(board.indexToBoardIndex(movement[0])).x].removePiece();
						    		board.tiles[(int) board.findIndexOfTile(board.indexToBoardIndex(movement[1])).y][(int) board.findIndexOfTile(board.indexToBoardIndex(movement[1])).x].addPiece(originalPiece);
						    	}
				    			
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
