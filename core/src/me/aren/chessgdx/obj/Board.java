package me.aren.chessgdx.obj;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.net.ServerData;
import me.aren.chessgdx.obj.interfaces.IGameObject;
import me.aren.chessgdx.obj.interfaces.IPiece;

import java.util.LinkedList;

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
	long requestCooldown = 1000;
	// TODO: Create getters and setters for these two linkedlists
	public LinkedList<IPiece> capturedPiecesWhite = new LinkedList<IPiece>();
	public LinkedList<IPiece> capturedPiecesBlack = new LinkedList<IPiece>();
	
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
	
	public Board(SpriteBatch sb) {
		this.sb = sb;
		
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
			GlobalSettings.getSocket().emit("turn-changed", white == true ? 1 : 2);
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
				return ServerData.getTurn() == 1 ? true : false;
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

}
