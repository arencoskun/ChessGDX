package me.aren.chessgdx.obj;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.obj.interfaces.IGameObject;

public class Board implements IGameObject {
	
	final int TILE_WIDTH = 96, TILE_HEIGHT = 96;
	final int BOARD_WIDTH = TILE_WIDTH * 8, BOARD_HEIGHT = TILE_HEIGHT * 8;
	// TODO: This should not be public - public only for testing purposes
	public Tile[][] tiles = new Tile[8][8];
	BitmapFont font = new BitmapFont();
	SpriteBatch sb;
	
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
						new Tile(sb, (x / TILE_WIDTH + ((BOARD_HEIGHT - TILE_HEIGHT) - y) / TILE_HEIGHT) % 2 != 0, new Vector2(x, y));
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
				}
			}
		}
	}

}
