package me.aren.chessgdx.obj.pieces;

import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.obj.interfaces.IPiece;

public class Bishop implements IPiece {
	final String BISHOP_PATH_WHITE = "bishop_white.png";
	final String BISHOP_PATH_BLACK = "bishop_black.png";
	Texture bishopTexture;
	public Tile parent;
	SpriteBatch sb;
	OrthographicCamera cam;
	boolean selected = false;
	boolean calculatedValidPositions = false;
	boolean white;
	boolean justMoved = false;
	Board board;
	LinkedBlockingQueue<Tile> validPositions;
	
	public Bishop(SpriteBatch sb, OrthographicCamera cam, Board board, boolean white) {
		// TODO Auto-generated constructor stub
		this.sb = sb;
		this.cam = cam;
		this.board = board;
		this.white = white;
		
		validPositions = new LinkedBlockingQueue<Tile>();
		
		bishopTexture = new Texture(white ? Gdx.files.internal(BISHOP_PATH_WHITE) : Gdx.files.internal(BISHOP_PATH_BLACK));
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
		
		//System.out.println(7 - board.findIndexOfTile(getParent()).x);
		
		int currentX = (int) board.findIndexOfTile(getParent()).x;
		int currentY = (int) board.findIndexOfTile(getParent()).y;
		
		for(int x1 = 0; x1 < 8 - currentX; x1++) {
			int differenceTopY = 8 - currentY;
			int differenceBottomY = currentY;
			
			if(x1 < differenceTopY) {
				getValidPositions().add(board.tiles[currentY + x1][currentX + x1]);
			}
			
			if(x1 < differenceBottomY + 1) {
				getValidPositions().add(board.tiles[currentY - x1][currentX + x1]);
			}
		}
		
		for(int x2 = currentX; x2 > -1; x2--) {
			int differenceTopY = 8 - currentY;
			int differenceBottomY = currentY;
			
			if(x2 < differenceTopY) {
				getValidPositions().add(board.tiles[currentY + x2][currentX - x2]);
			}
			
			if(x2 < differenceBottomY + 1) {
				getValidPositions().add(board.tiles[currentY - x2][currentX - x2]);
			}
		}
		
	}
	
	@Override
	public void render(float delta) {
		update(delta, board, cam);
		sb.draw(bishopTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
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
		return "rook";
	}


	@Override
	public void afterMove() {
		// TODO Auto-generated method stub
		
	}
}