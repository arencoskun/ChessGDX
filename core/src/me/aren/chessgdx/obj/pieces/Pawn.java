package me.aren.chessgdx.obj.pieces;

import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.obj.interfaces.IPiece;

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
	//boolean justMoved = false;
	Board board;
	LinkedBlockingQueue<Tile> validPositions;
	boolean hasMoved = false;
	
	
	public Pawn(SpriteBatch sb, OrthographicCamera cam, Board board, boolean white) {
		// TODO Auto-generated constructor stub
		this.sb = sb;
		this.cam = cam;
		this.board = board;
		this.white = white;
		
		validPositions = new LinkedBlockingQueue<Tile>();
		
		pawnTexture = new Texture(white ? Gdx.files.internal(PAWN_PATH_WHITE) : Gdx.files.internal(PAWN_PATH_BLACK));
	}
	
	@Override
	public void calculateValidPositions(Board board) {
		// TODO Auto-generated method stub
		//if(!validPositionsCalculated()) {
		if(getValidPositions().size() != 0) {
			for(Tile tile : getValidPositions()) {
				getValidPositions().remove(tile);
			}
		}
			
		int y = white ? (int)(board.findIndexOfTile(parent).y - 1) : (int)(board.findIndexOfTile(parent).y + 1);
		int x = (int) board.findIndexOfTile(parent).x;
		
		if(!(y > 7 || y < 0 || x > 7 || x < 0) && !board.tiles[y][x].doesHavePiece())
			getValidPositions().add(board.tiles[y][x]);
		
		if(!hasMoved) {
			if(white) {
				getValidPositions().add(board.tiles[y - 1][x]);
			} else {
				getValidPositions().add(board.tiles[y + 1][x]);
			}
		}
		
		setValidPositionsCalculated(true);
		//}
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		update(delta, board, cam);
		sb.draw(pawnTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
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
	public LinkedBlockingQueue<Tile> getValidPositions() {
		// TODO Auto-generated method stub
		return validPositions;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validPositionsCalculated() {
		// TODO Auto-generated method stub
		return calculatedValidPositions;
	}

	@Override
	public void setValidPositionsCalculated(boolean calculated) {
		// TODO Auto-generated method stub
		calculatedValidPositions = calculated;
	}
	
	// GameObject update method
	
	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWhite() {
		// TODO Auto-generated method stub
		return white;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "pawn";
	}

	@Override
	public void afterMove() {
		// TODO Auto-generated method stub
		if(!hasMoved) hasMoved = true;
	}

}
