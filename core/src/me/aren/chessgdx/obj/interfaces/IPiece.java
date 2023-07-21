package me.aren.chessgdx.obj.interfaces;

import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;

public interface IPiece extends IGameObject {
	int WIDTH = 96, HEIGHT = 96;
	// TODO: Deprecated functions: setValidPositionsCalculated, validPositionsCalculated
	default void update(float delta, Board board, OrthographicCamera cam) {
		calculateValidPositions(board);
		
		if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
		    Vector3 touch = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		    Vector2 finalPos = new Vector2(touch.x, touch.y);
		    
		    if(isWhite() == board.turnWhite) {
		    
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
			    			tile.addPiece(this);
			    			afterMove();
			    			board.setTurn(!board.getTurn());
			    			if(GlobalSettings.debugModeEnabled) {
			    				Gdx.app.log("DEBUG", "-------- Turn: " + (board.turnWhite ? " WHITE " : "BLACK ") + "--------");
			    			}
			    		} else {
			    			setSelected(false);
				    		tile.setGreen(false);
			    		}
			    	}
			    	
			    	getParent().setGreen(false);
			    	setValidPositionsCalculated(false);
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
}
