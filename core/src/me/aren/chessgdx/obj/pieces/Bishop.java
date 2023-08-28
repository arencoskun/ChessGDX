package me.aren.chessgdx.obj.pieces;

import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.aren.chessgdx.GlobalSettings;
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
//	boolean justMoved = false;
	boolean captured = false;
	Board board;
	LinkedBlockingQueue<Tile> validPositions;
	int limitXTopR = 8;
	int limitXBottomR = 8;
	int limitXTopL = 0;
	int limitXBottomL = 0;
	boolean foundPiece1 = false;
	
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
		
		// limits need to be recalculated after pieces move
		
		if(getValidPositions().size() != 0) {
			for(Tile tile : getValidPositions()) {
				getValidPositions().remove(tile);
			}
		}
		
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

			// bottom left
			/*
			 if(currentY + x2 < 8 && currentX - x2 > -1) {
				if(!foundPiece3 && board.tiles[currentY + x2][currentX - x2].getPiece() != this) {
		            if(x2 < differenceTopY) {
						if(!board.tiles[currentY + x2][currentX - x2].doesHavePiece() && board.tiles[currentY + x2][currentX - x2].getPiece() != this) {
							getValidPositions().add(board.tiles[currentY + x2][currentX - x2]);
						} else if(board.tiles[currentY + x2][currentX - x2].isPieceWhite() != white) {
							board.tiles[currentY + x2][currentX - x2].setCapturable(true);
							getValidPositions().add(board.tiles[currentY + x2][currentX - x2]);
							foundPiece3 = true;
						} else {
							foundPiece3 = true;
						}
					}
				}
			}

			// top left
			if(currentY - x2 > -1 && currentX - x2 > -1) {
				if(!foundPiece4 && board.tiles[currentY - x2][currentX - x2].getPiece() != this) {
					if(x2 < currentY + 1) {
						if(!board.tiles[currentY - x2][currentX - x2].doesHavePiece() && board.tiles[currentY - x2][currentX - x2].getPiece() != this) {
							getValidPositions().add(board.tiles[currentY - x2][currentX - x2]);
						} else if(board.tiles[currentY - x2][currentX - x2].isPieceWhite() != white) {
							board.tiles[currentY - x2][currentX - x2].setCapturable(true);
							getValidPositions().add(board.tiles[currentY - x2][currentX - x2]);
							foundPiece4 = true;
						} else {
							foundPiece4 = true;
						}
					}
				}
			}
			*/
			
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
			
			/*if(GlobalSettings.debugModeEnabled) {
				Gdx.app.log("DEBUG", "Limit X Bottom R: " + limitXBottomR);
				Gdx.app.log("DEBUG", "Limit X Bottom L: " + limitXBottomL);
				Gdx.app.log("DEBUG", "Limit X Top R: " + limitXTopR);
				Gdx.app.log("DEBUG", "Limit X Top L: " + limitXTopL);
			}*/
		}
	}

	@Override
	public void render(float delta) {
		update(delta, board, cam);
		if(getParent() != null) sb.draw(bishopTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
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
		return "bishop";
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
		limitXTopR = 8;
		limitXBottomR = 8;
		limitXTopL = 0;
		limitXBottomL = 0;
	}


	@Override
	public void afterTurnChange() {
		// TODO Auto-generated method stub
		limitXTopR = 8;
		limitXBottomR = 8;
		limitXTopL = 0;
		limitXBottomL = 0;
	}
}
