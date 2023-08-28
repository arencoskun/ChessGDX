package me.aren.chessgdx.obj;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.network.ClientSideConnection;
import me.aren.chessgdx.network.GameServer;
import me.aren.chessgdx.obj.interfaces.IGameObject;
import me.aren.chessgdx.obj.interfaces.IPiece;

public class Board implements IGameObject {
	
	final int TILE_WIDTH = 96, TILE_HEIGHT = 96;
	final int BOARD_WIDTH = TILE_WIDTH * 8, BOARD_HEIGHT = TILE_HEIGHT * 8;
	// TODO: This should not be public - public only for testing purposes
	public Tile[][] tiles = new Tile[8][8];
	public boolean turnWhite = true;
	public int turnCounter = 0;
	public int turnOnline = 0;
	BitmapFont font = new BitmapFont();
	SpriteBatch sb;
	// TODO: Create getters and setters for these two linkedlists
	public LinkedList<IPiece> capturedPiecesWhite = new LinkedList<IPiece>();
	public LinkedList<IPiece> capturedPiecesBlack = new LinkedList<IPiece>();
	
	private ClientSideConnection connection;
	
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
	
	public Board(SpriteBatch sb, ClientSideConnection connection) {
		this.sb = sb;
		this.connection = connection;
		
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
		turnOnline = connection.receiveTurn();
		//System.out.println(connection.receiveTurn());
	}

	@Override
	public void render(float delta) {
		//update(delta);
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
	
	public void setTurn(boolean white, boolean multiplayer) {
		turnWhite = white;
		turnCounter++;
		
		for(Tile[] tileArray : tiles) {
			for(Tile tile : tileArray) {
				if(tile.doesHavePiece()) {
					tile.getPiece().afterTurnChange();
				}
			}
		}
		
		if(multiplayer) GameServer.turn = white ? 1 : 2;
		
	}
	
	public boolean getTurn() {
		return GameServer.turn == 1 ? true : false;
	}
	
	public int getTurnCount() {
		// TODO Auto-generated method stub
		return turnCounter;
	}

}
