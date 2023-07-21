package me.aren.chessgdx.obj.pieces;

import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.obj.interfaces.IPiece;

public class Rook implements IPiece {
	
	final String ROOK_PATH_WHITE = "rook_white.png";
	final String ROOK_PATH_BLACK = "rook_black.png";
	Texture rookTexture;
	public Tile parent;
	SpriteBatch sb;
	OrthographicCamera cam;
	boolean selected = false;
	boolean calculatedValidPositions = false;
	boolean white;
	boolean justMoved = false;
	Board board;
	LinkedBlockingQueue<Tile> validPositions;
	
	
	public Rook(SpriteBatch sb, OrthographicCamera cam, Board board, boolean white) {
		// TODO Auto-generated constructor stub
		this.sb = sb;
		this.cam = cam;
		this.board = board;
		this.white = white;
		
		validPositions = new LinkedBlockingQueue<Tile>();
		
		rookTexture = new Texture(white ? Gdx.files.internal(ROOK_PATH_WHITE) : Gdx.files.internal(ROOK_PATH_BLACK));
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
		
		//int y = white ? (int)(board.findIndexOfTile(parent).y - 1) : (int)(board.findIndexOfTile(parent).y + 1);
		//int x = (int) board.findIndexOfTile(parent).x;
		
		if(board.findIndexOfTile(getParent()).x != 7) {
			for(int x1 = (int) board.findIndexOfTile(getParent()).x; x1 < 8; x1++) {
				if(board.tiles[(int) board.findIndexOfTile(getParent()).y][x1] != getParent() &&
						board.tiles[(int) board.findIndexOfTile(getParent()).y][x1].doesHavePiece()) break;
				getValidPositions().add(board.tiles[(int) board.findIndexOfTile(getParent()).y][x1]);
			}
		}
		
		if(board.findIndexOfTile(getParent()).x != 0) {
			for(int x2 = (int) board.findIndexOfTile(getParent()).x; x2 > -1; x2--) {
				if(board.tiles[(int) board.findIndexOfTile(getParent()).y][x2] != getParent() &&
						board.tiles[(int) board.findIndexOfTile(getParent()).y][x2].doesHavePiece()) break;
				getValidPositions().add(board.tiles[(int) board.findIndexOfTile(getParent()).y][x2]);
			}
		}
		
		for(int y1 = (int) board.findIndexOfTile(getParent()).y; y1 < 8; y1++) {
			if(board.tiles[y1][(int) board.findIndexOfTile(getParent()).x] != getParent() &&
					board.tiles[y1][(int) board.findIndexOfTile(getParent()).x].doesHavePiece()) break;
			getValidPositions().add(board.tiles[y1][(int) board.findIndexOfTile(getParent()).x]);
		}
	
		for(int y2 = (int) board.findIndexOfTile(getParent()).y; y2 > -1; y2--) {
			if(board.tiles[y2][(int) board.findIndexOfTile(getParent()).x] != getParent() &&
					board.tiles[y2][(int) board.findIndexOfTile(getParent()).x].doesHavePiece()) break;
			getValidPositions().add(board.tiles[y2][(int) board.findIndexOfTile(getParent()).x]);
		}
		
		/*if(!(y > 7 || y < 0 || x > 7 || x < 0) && !board.tiles[y][x].doesHavePiece())
			getValidPositions().add(board.tiles[y][x]);
		*/
		//setValidPositionsCalculated(true);
		//}
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		update(delta, board, cam);
		sb.draw(rookTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
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
