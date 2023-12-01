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
//	boolean justMoved = false;
	boolean captured = false;
	Board board;
	LinkedBlockingQueue<Tile> validPositions;
	long lastMove = 0;
	long moveCooldown = 200;
	
	
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
		if(!getValidPositions().isEmpty()) {
			for(Tile tile : getValidPositions()) {
				getValidPositions().remove(tile);
			}
		}
		
		//int y = white ? (int)(board.findIndexOfTile(parent).y - 1) : (int)(board.findIndexOfTile(parent).y + 1);
		//int x = (int) board.findIndexOfTile(parent).x;
		
		// TODO: Revision
		
		if(board.findIndexOfTile(getParent()).x != 7) {
			for(int x1 = (int) board.findIndexOfTile(getParent()).x; x1 < 8; x1++) {
				Tile tile = board.tiles[(int) board.findIndexOfTile(getParent()).y][x1];
				if(tile != getParent() && tile.doesHavePiece() ) {
					if(tile.isPieceWhite() == board.getTurn()) {
						break;
					} else {
						getValidPositions().add(tile);
						tile.setCapturable(true);
						break;
					}

				}
				if(!getValidPositions().contains(tile)) getValidPositions().add(tile);
			}
		}
		
		if(board.findIndexOfTile(getParent()).x != 0) {
			for(int x2 = (int) board.findIndexOfTile(getParent()).x; x2 > -1; x2--) {
				Tile tile = board.tiles[(int) board.findIndexOfTile(getParent()).y][x2];
				if(tile != getParent() && tile.doesHavePiece() ) {
					if(tile.isPieceWhite() == board.getTurn()) {
						break;
					} else {
						getValidPositions().add(tile);
						tile.setCapturable(true);
						break;
					}

				}
				if(!getValidPositions().contains(tile)) getValidPositions().add(tile);
			}
		}
		
		for(int y1 = (int) board.findIndexOfTile(getParent()).y; y1 < 8; y1++) {
			Tile tile = board.tiles[y1][(int) board.findIndexOfTile(getParent()).x];
			if(tile != getParent() && tile.doesHavePiece() ) {
				if(tile.isPieceWhite() == board.getTurn()) {
					break;
				} else {
					getValidPositions().add(tile);
					tile.setCapturable(true);
					break;
				}

			}
			if(!getValidPositions().contains(tile)) getValidPositions().add(tile);
		}
	
		for(int y2 = (int) board.findIndexOfTile(getParent()).y; y2 > -1; y2--) {
			Tile tile = board.tiles[y2][(int) board.findIndexOfTile(getParent()).x];
			if(tile != getParent() && tile.doesHavePiece() ) {
				if(tile.isPieceWhite() == board.getTurn()) {
					break;
				} else {
					getValidPositions().add(tile);
					tile.setCapturable(true);
					break;
				}

			}
			if(!getValidPositions().contains(tile)) getValidPositions().add(tile);
		}
		
		/*if(!(y > 7 || y < 0 || x > 7 || x < 0) && !board.tiles[y][x].doesHavePiece())
			getValidPositions().add(board.tiles[y][x]);
		*/
		//setValidPositionsCalculated(true);
		//}

		for(Tile validPosition : getValidPositions()) {
			if(validPosition.doesHavePiece() && validPosition.getPiece() instanceof King) {
				King king = (King) validPosition.getPiece();

				if(king.isWhite() != isWhite()) {
					validPosition.setCheckable(true);
					board.setCheckWhite(king.isWhite());
				}
			} else {
				validPosition.setCheckable(false);
			}
		}
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		update(delta, board, cam);
		if(getParent() != null) sb.draw(rookTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
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
	public boolean isCaptured() {
		return captured;
	}

	@Override
	public void setCaptured(boolean captured) {
		this.captured = captured;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		rookTexture.dispose();
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
		if(isCaptured()) {
			getParent().removePiece();
			setParent(null);
		}
	}


	@Override
	public void afterCapture() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterTurnChange(boolean newTurn) {
		// TODO Auto-generated method stub
	}

	@Override
	public long getLastMoveTime() {
		return lastMove;
	}

	@Override
	public void setLastMoveTime(long lastMoveTime) {
		this.lastMove = lastMoveTime;
	}

	@Override
	public long getMoveCooldown() {
		return moveCooldown;
	}
}
