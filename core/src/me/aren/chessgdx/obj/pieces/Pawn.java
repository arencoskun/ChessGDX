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
import org.json.JSONException;
import org.json.JSONObject;

public class Pawn implements IPiece {
	
	final String PAWN_PATH_WHITE = "pawn_white.png";
	final String PAWN_PATH_BLACK = "pawn_black.png";
	Texture pawnTexture;
	public Tile parent;
	SpriteBatch sb;
	OrthographicCamera cam;
	boolean selected = false;
	boolean calculatedValidPositions = false;
	boolean captured = false;
	boolean white;
	//boolean justMoved = false;
	Board board;
	LinkedBlockingQueue<Tile> validPositions;
	boolean hasMoved = false;
	private int moveCount = 0;
	long lastMove = 0;
	long moveCooldown = 200;
	
	
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
		if(!getValidPositions().isEmpty()) {
			for(Tile tile : getValidPositions()) {
				getValidPositions().remove(tile);
			}
		}
			
		int y = white ? (int)(board.findIndexOfTile(parent).y - 1) : (int)(board.findIndexOfTile(parent).y + 1);
		int currentY = (int)(board.findIndexOfTile(parent).y);
		int x = (int) board.findIndexOfTile(parent).x;
		
		if(!(y > 7 || y < 0 || x > 7 || x < 0)) {
			 if(!board.tiles[y][x].doesHavePiece()) getValidPositions().add(board.tiles[y][x]);
			
			if(!hasMoved) {
				if(white && y - 1 > 0) {
					if(!board.tiles[y - 1][x].doesHavePiece() && !board.tiles[y][x].doesHavePiece()) getValidPositions().add(board.tiles[y - 1][x]);
				} else if(y + 1 < 8) {
					if(!board.tiles[y + 1][x].doesHavePiece() && !board.tiles[y][x].doesHavePiece()) getValidPositions().add(board.tiles[y + 1][x]);
				}
			}
			
			Tile[] positionsToCheckForCapture = new Tile[2];

			if(x - 1 > -1 && currentY - 1 > -1 && currentY + 1 < 8 && x + 1 < 8) {
				if (board.tiles[currentY][x - 1].getPiece() instanceof Pawn) {
					Pawn pawn = (Pawn) board.tiles[currentY][x - 1].getPiece();

					if (pawn.getMoveCount() == 1 && pawn.isWhite() != white) {
						if(pawn.isWhite()) {
							board.tiles[currentY + 1][x - 1].setEnPassantable(true);
							if(GlobalSettings.multiplayer) {
								try {
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("x", x - 1);
									jsonObject.put("y", currentY + 1);
									jsonObject.put("enpassantable", true);
									GlobalSettings.getSocket().emit("tile-set-enpassantable", jsonObject);
								}	catch (JSONException e) {
									e.printStackTrace();
								}
							}
							getValidPositions().add(board.tiles[currentY + 1][x - 1]);
						} else {
							board.tiles[currentY - 1][x - 1].setEnPassantable(true);
							if(GlobalSettings.multiplayer) {
								try {
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("x", x - 1);
									jsonObject.put("y", currentY - 1);
									jsonObject.put("enpassantable", true);
									GlobalSettings.getSocket().emit("tile-set-enpassantable", jsonObject);
								}	catch (JSONException e) {
									e.printStackTrace();
								}
							}
							getValidPositions().add(board.tiles[currentY - 1][x - 1]);
						}
					}
				}

				if (board.tiles[currentY][x + 1].getPiece() instanceof Pawn) {
					Pawn pawn = (Pawn) board.tiles[currentY][x + 1].getPiece();

					if (pawn.getMoveCount() == 1) {
						if(pawn.isWhite()) {
							board.tiles[currentY + 1][x + 1].setEnPassantable(true);
							if(GlobalSettings.multiplayer) {
								try {
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("x", x + 1);
									jsonObject.put("y", currentY + 1);
									jsonObject.put("enpassantable", true);
									GlobalSettings.getSocket().emit("tile-set-enpassantable", jsonObject);
								}	catch (JSONException e) {
									e.printStackTrace();
								}
							}
							getValidPositions().add(board.tiles[currentY + 1][x + 1]);
						} else {
							board.tiles[currentY - 1][x + 1].setEnPassantable(true);
							if(GlobalSettings.multiplayer) {
								try {
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("x", x + 1);
									jsonObject.put("y", currentY - 1);
									jsonObject.put("enpassantable", true);
									GlobalSettings.getSocket().emit("tile-set-enpassantable", jsonObject);
								}	catch (JSONException e) {
									e.printStackTrace();
								}
							}
							getValidPositions().add(board.tiles[currentY - 1][x + 1]);
						}
					}
				}
			}

			positionsToCheckForCapture[0] = board.tiles[y][x];
			positionsToCheckForCapture[1] = board.tiles[y][x];
			
			if(x == 7) {
				positionsToCheckForCapture[0] = null;
				positionsToCheckForCapture[1] = board.tiles[y][x - 1];
			} else if(x == 0) {
				positionsToCheckForCapture[0] = board.tiles[y][x + 1];
				positionsToCheckForCapture[1] = null;
			} else {
				positionsToCheckForCapture[0] = board.tiles[y][x +  1];
				positionsToCheckForCapture[1] = board.tiles[y][x - 1];
			}

			for(Tile tileToCapture : positionsToCheckForCapture) {
				if(tileToCapture != null) {
					if(tileToCapture.doesHavePiece() && tileToCapture.getPiece().isWhite() != white) {
						tileToCapture.setCapturable(true);
						getValidPositions().add(tileToCapture);
					}
				}
			}
		}

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
		if(getParent() != null) sb.draw(pawnTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
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
		this.captured = true;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		pawnTexture.dispose();
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
		moveCount++;
		if(GlobalSettings.multiplayer) {
			JSONObject jsonData = new JSONObject();

			try {
				jsonData.put("x", getParent().getPosBoard().x);
				jsonData.put("y", getParent().getPosBoard().y);
				jsonData.put("moveCount", moveCount);

				GlobalSettings.getSocket().emit("pawn-move-count", jsonData);
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}

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

	public int getMoveCount() {
		return moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}
}
