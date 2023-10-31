package me.aren.chessgdx.obj;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.net.ServerData;
import me.aren.chessgdx.obj.interfaces.IGameObject;
import me.aren.chessgdx.obj.interfaces.IPiece;
import me.aren.chessgdx.obj.pieces.Bishop;
import me.aren.chessgdx.obj.pieces.Pawn;
import me.aren.chessgdx.obj.pieces.Queen;
import me.aren.chessgdx.obj.pieces.Rook;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Objects;

public class Board implements IGameObject {
	
	final int TILE_WIDTH = 96, TILE_HEIGHT = 96;
	final int BOARD_WIDTH = TILE_WIDTH * 8, BOARD_HEIGHT = TILE_HEIGHT * 8;
	// TODO: This should not be public - public only for testing purposes
	public Tile[][] tiles = new Tile[8][8];
	public boolean turnWhite = true;
	public int turnCounter = 0;
	BitmapFont font = new BitmapFont();
	SpriteBatch sb;
	long lastTurnRequest;
	long requestCooldown = 0;
	// TODO: Create getters and setters for these two linkedlists
	public LinkedList<IPiece> capturedPiecesWhite = new LinkedList<IPiece>();
	public LinkedList<IPiece> capturedPiecesBlack = new LinkedList<IPiece>();
	private OrthographicCamera cam;
	
	public Vector2 findIndexOfTile(Tile tileToSearch) {	
		for(int y = 0; y < 8; y++) {
			Tile[] tilesY = tiles[y];
			
			for(int x = 0; x < 8; x++) {
				if(tilesY[x] == tileToSearch) {
					return new Vector2(x, y);
				}
			}
		}
		
		return new Vector2(-1, -1);
	}
	
	public Board(SpriteBatch sb, OrthographicCamera cam) {
		this.sb = sb;
		this.cam = cam;

		for(int x = 0; x < 768; x += 96) {
			for(int y = 768 - 96; y >= 0; y -= 96) {
				tiles[((BOARD_HEIGHT - TILE_HEIGHT) - y) / TILE_HEIGHT][x / TILE_WIDTH] = 
						new Tile((x / TILE_WIDTH + ((BOARD_HEIGHT - TILE_HEIGHT) - y) / TILE_HEIGHT) % 2 == 0, new Vector2(x, y));
			}
		}
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		for(Tile[] tilesY : tiles) {
			for(Tile tile : tilesY) {
				tile.render(delta);
				
				if(GlobalSettings.debugModeEnabled) {
					Vector2 tilePos = findIndexOfTile(tile);
					if(tilePos == new Vector2(-1, -1)) break;
					String position = "(" + tilePos.x + "," + tilePos.y + ")";
					
					font.draw(sb, position, tilePos.x * TILE_WIDTH, BOARD_HEIGHT - tilePos.y * TILE_HEIGHT);
					
					font.draw(sb, String.valueOf(tile.isGreen()), tilePos.x * TILE_WIDTH, BOARD_HEIGHT - tilePos.y * TILE_HEIGHT - 15);
					
					if(tile.piece != null) font.draw(sb, String.valueOf(tile.piece.isSelected()), tilePos.x * TILE_WIDTH, BOARD_HEIGHT - tilePos.y * TILE_HEIGHT - 30);
					font.draw(sb, "Turn: " + (getTurn() ? "White" : "Black"), 550, 50);
					font.draw(sb, "Turn count: " + getTurnCount(), 650, 50);
				}
			}
		}
	}

	@Override
	public void dispose() {
		for(Tile[] tileArr : tiles) {
			for(Tile tile : tileArr) {
				tile.dispose();
			}
		}
		
	}
	
	public void setTurn(boolean white) {
		if(GlobalSettings.multiplayer) {
			GlobalSettings.getSocket().emit("turn-changed", white ? 1 : 2);
		}
		turnWhite = white;
		turnCounter++;
		
		for(Tile[] tileArray : tiles) {
			for(Tile tile : tileArray) {
				if(tile.doesHavePiece()) {
					tile.getPiece().afterTurnChange(turnWhite);
					tile.getPiece().setSelected(false);
				}
			}
		}
	}
	
	public boolean getTurn() {
		if(GlobalSettings.multiplayer) {
			if(System.currentTimeMillis() >= lastTurnRequest + requestCooldown) {
				GlobalSettings.getSocket().emit("get-turn");
				lastTurnRequest = System.currentTimeMillis();
			}
			
			if(ServerData.getTurn() == 1 || ServerData.getTurn() == 2)
				return ServerData.getTurn() == 1;
		}
		return turnWhite;
	}
	
	public int getTurnCount() {
		// TODO Auto-generated method stub
		return turnCounter;
	}
	
	public void updateBoard(int originalX, int originalY,
							int targetX, int targetY) {
		IPiece originalPiece = tiles[originalY][originalX].getPiece();
		tiles[originalY][originalX].removePiece();
		tiles[targetY][targetX].addPiece(originalPiece);
		
	}

	public void updateCapturedPiece(int x, int y, boolean white) {
		IPiece capturedPiece = tiles[y][x].getPiece();
		if(white) {
			capturedPiecesWhite.add(capturedPiece);
		} else {
			capturedPiecesBlack.add(capturedPiece);
		}

		tiles[y][x].removePiece();
	}

	public void setEnPassantable(int tileX, int tileY, boolean enpassantable) {
		tiles[tileY][tileX].setEnPassantable(enpassantable);
	}

	public void setPawnMoveCount(int tileX, int tileY, int moveCount) {
		if(tiles[tileY][tileX].getPiece() instanceof Pawn) {
			Pawn pawn = (Pawn) tiles[tileY][tileX].getPiece();

			pawn.setMoveCount(moveCount);
		}
	}

}
