package me.aren.chessgdx.obj.pieces;

import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.obj.interfaces.IPiece;

public class Queen implements IPiece {
	
	final String QUEEN_PATH_WHITE = "queen_white.png";
	final String QUEEN_PATH_BLACK = "queen_black.png";
	Texture queenTexture;
	public Tile parent;
	SpriteBatch sb;
	OrthographicCamera cam;
	boolean selected = false;
	boolean calculatedValidPositions = false;
	boolean white;
//	boolean justMoved = false;
	boolean captured = false;
	int limitXTopR = 8;
	int limitXBottomR = 8;
	int limitXTopL = 0;
	int limitXBottomL = 0;
	Board board;
	LinkedBlockingQueue<Tile> validPositions;
	long lastMove = 0;
	long moveCooldown = 200;
	
	
	public Queen(SpriteBatch sb, OrthographicCamera cam, Board board, boolean white) {
		// TODO Auto-generated constructor stub
		this.sb = sb;
		this.cam = cam;
		this.board = board;
		this.white = white;
		
		validPositions = new LinkedBlockingQueue<Tile>();
		
		queenTexture = new Texture(white ? Gdx.files.internal(QUEEN_PATH_WHITE) : Gdx.files.internal(QUEEN_PATH_BLACK));
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
		int currentX = (int) board.findIndexOfTile(getParent()).x;
		int currentY = (int) board.findIndexOfTile(getParent()).y;
		int differenceTopY = 8 - currentY;
		
		for(int x1 = 0; x1 < 8 - currentX; x1++) {
			
			
			if(x1 < differenceTopY) {
				Tile tile = board.tiles[currentY + x1][currentX + x1];
				if(tile.doesHavePiece() && tile.getPiece() != this && currentX + x1 <= limitXBottomR) {
					limitXBottomR = currentX + x1;
				}
				
				if(currentX + x1 <= limitXBottomR) {
					if(tile.doesHavePiece() && tile.getPiece().isWhite() != white) {
						tile.setCapturable(true);
						getValidPositions().add(tile);
					} else if(!tile.doesHavePiece()) {
						getValidPositions().add(tile);
					}
				}
			}
			
			
			// top right
			if(x1 < currentY + 1) {
				Tile tile = board.tiles[currentY - x1][currentX + x1];
				if(tile.doesHavePiece() && tile.getPiece() != this && currentX + x1 <= limitXTopR) {
					limitXTopR = currentX + x1;
				}
				
				if(currentX + x1 <= limitXTopR) {
					if(tile.doesHavePiece() && tile.getPiece().isWhite() != white) {
						tile.setCapturable(true);
						getValidPositions().add(tile);
					} else if(!tile.doesHavePiece()) {
						getValidPositions().add(tile);
					}
					
				}
			}
		}
		
		for(int x2 = currentX; x2 > -1; x2--) {
			
			if(x2 < differenceTopY) {
				Tile tile = board.tiles[currentY + x2][currentX - x2];
				if(tile.doesHavePiece() && tile.getPiece() != this && currentX - x2 >= limitXBottomL) {
					limitXBottomL = currentX - x2;
				}
				
				if(currentX - x2 >= limitXBottomL) {
					if(tile.doesHavePiece() && tile.getPiece().isWhite() != white) {
						tile.setCapturable(true);
						getValidPositions().add(tile);
					} else if(!tile.doesHavePiece()) {
						getValidPositions().add(tile);
					}
				}
			}
			
			
			// top left
			if(x2 < currentY + 1) {
				Tile tile = board.tiles[currentY - x2][currentX - x2];
				if(tile.doesHavePiece() && tile.getPiece() != this && currentX - x2 >= limitXTopL) {
					limitXTopL = currentX - x2;
				}
				
				if(currentX - x2 >= limitXTopL) {
					if(tile.doesHavePiece() && tile.getPiece().isWhite() != white) {
						tile.setCapturable(true);
						getValidPositions().add(tile);
					} else if(!tile.doesHavePiece()) {
						getValidPositions().add(tile);
					}
					
				}
			}
		}

		for(Tile validPosition : getValidPositions()) {
			if(validPosition.doesHavePiece() && validPosition.getPiece() instanceof King) {
				King king = (King) validPosition.getPiece();

				if(king.isWhite() != isWhite()) {
					validPosition.setCheckable(true);
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
		if(getParent() != null) sb.draw(queenTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
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
		queenTexture.dispose();
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
		return "queen";
	}


	@Override
	public void afterMove() {
		// TODO Auto-generated method stub
		if(isCaptured()) {
			getParent().removePiece();
			setParent(null);
		}
		
		// this is done to make sure the limits aren't set to their old positions
		// the calculateValidPositions method will always recalculate them anyway so it just makes sure it is not set to the values they shouldn't be set to
		// this is not really very good practice but for now it will do
		// TODO: Maybe refactor
		limitXTopR = 8;
		limitXBottomR = 8;
		limitXTopL = 0;
		limitXBottomL = 0;
	}


	@Override
	public void afterCapture() {
		// TODO Auto-generated method stub
		
		// this is done to make sure the limits aren't set to their old positions
		// the calculateValidPositions method will always recalculate them anyway so it just makes sure it is not set to the values they shouldn't be set to
		// this is not really very good practice but for now it will do
		// TODO: Maybe refactor
		limitXTopR = 8;
		limitXBottomR = 8;
		limitXTopL = 0;
		limitXBottomL = 0;
	}


	@Override
	public void afterTurnChange(boolean newTurn) {
		// TODO Auto-generated method stub
		
		// this is done to make sure the limits aren't set to their old positions
		// the calculateValidPositions method will always recalculate them anyway so it just makes sure it is not set to the values they shouldn't be set to
		// this is not really very good practice but for now it will do
		// TODO: Maybe refactor
		limitXTopR = 8;
		limitXBottomR = 8;
		limitXTopL = 0;
		limitXBottomL = 0;
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
