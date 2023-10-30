package me.aren.chessgdx.obj.interfaces;

import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.net.ServerData;
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;

public interface IPiece extends IGameObject {
	int WIDTH = 96, HEIGHT = 96;
	// TODO: Deprecated functions: setValidPositionsCalculated, validPositionsCalculated
	default void update(float delta, Board board, OrthographicCamera cam) {
		// TODO : Maybe this should not be called every frame
		calculateValidPositions(board);
		
		if((board.getTurn() == true ? 1 : 2) != ServerData.getPlayerID() && GlobalSettings.multiplayer) {
			for(Tile[] tiles : board.tiles) {
	    		for(Tile tile : tiles) {
	    			if(tile.isGreen()) tile.setGreen(false);
	    		}
	    	}
		}
		
		if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
			if(GlobalSettings.multiplayer) {
				if(!ServerData.isRoomFull()) return;
				System.out.println("TURN: " + board.getTurn());
				if((board.getTurn() == true ? 1 : 2) != ServerData.getPlayerID()) {
					return;
				}
			}
		    Vector3 touch = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		    Vector2 finalPos = new Vector2(touch.x, touch.y);
		    
		    if(isWhite() == board.getTurn()) {
		    
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
			    			if(GlobalSettings.multiplayer) {
			    				JSONObject movementData = new JSONObject();
			    				Vector2 originalPos = board.findIndexOfTile(getParent());
			    				Vector2 targetPos = board.findIndexOfTile(tile);
			    				
			    				try {
									movementData.put("originalX", originalPos.x);
									movementData.put("originalY", originalPos.y);
									movementData.put("targetX", targetPos.x);
									movementData.put("targetY", targetPos.y);
									
									GlobalSettings.getSocket().emit("movement", movementData);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			}
			    			getParent().setGreen(false);
			    			getParent().removePiece();

							if(tile.isCapturable()) {

								if(GlobalSettings.multiplayer) {
									JSONObject data = new JSONObject();
									try {
										data.put("x", tile.getPosBoard().x);
										data.put("y", tile.getPosBoard().y);
										data.put("white", board.turnWhite);
										GlobalSettings.getSocket().emit("piece-captured", data);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

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

							if(tile.isEnPassantable()) {
								if(board.tiles[(int) (tile.getPosBoard().y - 1)][(int) tile.getPosBoard().x].getPiece() != null &&
										board.tiles[(int) (tile.getPosBoard().y - 1)][(int) tile.getPosBoard().x].getPiece().isWhite()) {
									if(GlobalSettings.debugModeEnabled) {
										Gdx.app.log("DEBUG", "Black captured a " +board.tiles[(int) (tile.getPosBoard().y - 1)][(int) tile.getPosBoard().x].getPiece().getName());
									}
									board.capturedPiecesWhite.add(board.tiles[(int) (tile.getPosBoard().y - 1)][(int) tile.getPosBoard().x].getPiece());
									board.tiles[(int) (tile.getPosBoard().y - 1)][(int) tile.getPosBoard().x].removePiece();

									if(GlobalSettings.multiplayer) {
										JSONObject data = new JSONObject();
										try {
											data.put("x", tile.getPosBoard().x);
											data.put("y", tile.getPosBoard().y - 1);
											data.put("white", true);
											GlobalSettings.getSocket().emit("piece-captured", data);
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								} else if(board.tiles[(int) (tile.getPosBoard().y + 1)][(int) tile.getPosBoard().x].getPiece() != null &&
										!board.tiles[(int) (tile.getPosBoard().y + 1)][(int) tile.getPosBoard().x].getPiece().isWhite()) {
									if(GlobalSettings.debugModeEnabled) {
										Gdx.app.log("DEBUG", "White captured a " +board.tiles[(int) (tile.getPosBoard().y + 1)][(int) tile.getPosBoard().x].getPiece().getName());
									}
									board.capturedPiecesBlack.add(board.tiles[(int) (tile.getPosBoard().y + 1)][(int) tile.getPosBoard().x].getPiece());
									board.tiles[(int) (tile.getPosBoard().y + 1)][(int) tile.getPosBoard().x].removePiece();
									if(GlobalSettings.multiplayer) {
										JSONObject data = new JSONObject();
										try {
											data.put("x", tile.getPosBoard().x);
											data.put("y", tile.getPosBoard().y + 1);
											data.put("white", true);
											GlobalSettings.getSocket().emit("piece-captured", data);
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								}
								tile.setEnPassantable(false);
								if(GlobalSettings.multiplayer) {
									JSONObject data = new JSONObject();
									try {
										data.put("x", tile.getPosBoard().x);
										data.put("y", tile.getPosBoard().y);
										data.put("enpassantable", false);
										GlobalSettings.getSocket().emit("tile-set-enpassantable", data);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}

			    			tile.addPiece(this);
			    			afterMove();
			    			board.setTurn(!board.getTurn());
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
	public void afterTurnChange(boolean newTurn);
}
