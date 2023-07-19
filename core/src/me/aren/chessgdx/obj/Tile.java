package me.aren.chessgdx.obj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import me.aren.chessgdx.obj.interfaces.IGameObject;
import me.aren.chessgdx.obj.interfaces.IPiece;

public class Tile implements IGameObject {
	final String TILE_LIGHT_PATH = "tile_light_brown.png";
	final String TILE_DARK_PATH = "tile_dark_brown.png";
	SpriteBatch sb;
	boolean white;
	private Vector2 pos;
	Texture tileTexture;
	IPiece piece;
	
	public Tile(SpriteBatch sb, boolean white, Vector2 pos) {
		this.sb = sb;
		this.white = white;
		this.setPos(pos);
		
		tileTexture = new Texture(white ? Gdx.files.internal(TILE_LIGHT_PATH) : Gdx.files.internal(TILE_DARK_PATH));
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		sb.draw(tileTexture, getPos().x, getPos().y, 96, 96);
		if(piece != null) {
			piece.render(delta);
		}
	}
	
	public void addPiece(IPiece pieceToAdd) {
		if(piece == null) {
			piece = pieceToAdd;
			piece.setParent(this);
		}
	}
	
	public void removePiece() {
		if(piece != null) piece = null;
	}

	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}
}
