package me.aren.chessgdx.obj.pieces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.obj.interfaces.IPiece;
import java.util.LinkedList;

public class Pawn implements IPiece {
	
	final String PAWN_PATH_WHITE = "pawn_white.png";
	final String PAWN_PATH_BLACK = "pawn_black.png";
	Texture pawnTexture;
	public Tile parent;
	SpriteBatch sb;
	OrthographicCamera cam;
	boolean selected = false;
	boolean calculatedValidPositions = false;
	boolean white;
	boolean justMoved = false;
	Board board;
	LinkedList<Tile> validPositions;
	
	
	public Pawn(SpriteBatch sb, OrthographicCamera cam, Board board, boolean white) {
		// TODO Auto-generated constructor stub
		this.sb = sb;
		this.cam = cam;
		this.board = board;
		this.white = white;
		
		validPositions = new LinkedList<Tile>();
		
		pawnTexture = new Texture(white ? Gdx.files.internal(PAWN_PATH_WHITE) : Gdx.files.internal(PAWN_PATH_BLACK));
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
		// TODO Piece selecting does not work properly most of the time
		
		if(!calculatedValidPositions) {
			if(validPositions.size() != 0) {
				for(Tile tile : validPositions) {
					validPositions.remove(tile);
				}
			}
			
			int y = white ? (int)(board.findIndexOfTile(parent).y - 1) : (int)(board.findIndexOfTile(parent).y + 1);
			int x = (int) board.findIndexOfTile(parent).x;
			
			if(!(y > 7 || y < 0 || x > 7 || x < 0))
				validPositions.add(board.tiles[y][x]);
			
			calculatedValidPositions = true;
		}
		
		
		if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
		    Vector3 touch = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		    Vector2 finalPos = new Vector2(touch.x, touch.y);
		    
		    if(new Rectangle(parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT).contains(finalPos) && validPositions.size() > 0){
		    	setSelected(true);
		    	// --------------- DEBUG
		    	if(GlobalSettings.debugModeEnabled) {
		        	Gdx.app.log("DEBUG", white ? "White pawn selected at " + "X: " + parent.getPos().x / 96 + " Y: " + (7 - parent.getPos().y / 96) 
		        			: "Black pawn selected at " + "X: " + parent.getPos().x / 96 + " Y: " + (7 - parent.getPos().y / 96));
		        	String positionsToPrint = "This pawn can move to these positions: ";
		        	
		        	for(Tile validTile : getValidPositions()) {
		        		positionsToPrint += "\nX: " + board.findIndexOfTile(validTile).x + " Y: " + board.findIndexOfTile(validTile).y;
		        	}
		        	
		        	Gdx.app.log("DEBUG", positionsToPrint);
		        }
		        // --------------- DEBUG
		    	
		        parent.setGreen(true);
		        
		        if(!justMoved) {
			        for(Tile tile : validPositions) {
			        	tile.setGreen(true);
			        }
			    }
		        
		        
		        
		        //justMoved = false;
		    } else if(isSelected()) {
		    	for(Tile tile : validPositions) {
		    		if(new Rectangle(tile.getPos().x, tile.getPos().y, WIDTH, HEIGHT).contains(finalPos)) {
		    			parent.setGreen(false);
		    			parent.removePiece();
		    			tile.addPiece(this);
		    			
		    			
		    		} else {
		    			setSelected(false);
			    		tile.setGreen(false);
		    		}
		    	}
		    	
		    	justMoved = true;
		    	
		    	parent.setGreen(false);
		    	calculatedValidPositions = false;
		    	setSelected(false);
		    }
		    
		    
		    if(justMoved && selected) {
		    	setSelected(false);
		    	justMoved = false;
		    }
		}
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		sb.draw(pawnTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
		update(delta);
	}

	@Override
	public void setParent(Tile tile) {
		parent = tile;
	}

	@Override
	public Tile getParent() {
		return parent;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return selected;
	}
	
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		this.selected = selected;
		//parent.setGreen(true);
		
	}

	@Override
	public LinkedList<Tile> getValidPositions() {
		// TODO Auto-generated method stub
		return validPositions;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
