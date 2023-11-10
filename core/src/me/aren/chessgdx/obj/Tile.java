package me.aren.chessgdx.obj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.obj.interfaces.IGameObject;
import me.aren.chessgdx.obj.interfaces.IPiece;

public class Tile implements IGameObject {
	final String TILE_LIGHT_PATH = "tile_light_brown.png";
	final String TILE_DARK_PATH = "tile_dark_brown.png";
	SpriteBatch tileSb;
	boolean white;
	boolean capturable = false;
	boolean enPassantable = false;
	boolean checkable = false;
	private boolean green = false;
	private boolean red = false;
	private Vector2 pos;
	Texture tileTexture;
	IPiece piece;
	
	public Tile(boolean white, Vector2 pos) {
		tileSb = new SpriteBatch();
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
		update(delta);
		tileSb.begin();
		
		if(isGreen()) {
			tileSb.setColor(Color.LIME);
		} else if(isRed()) {
			tileSb.setColor(Color.RED);
		} else {
			tileSb.setColor(Color.WHITE);
		}

		tileSb.draw(tileTexture, getPos().x, getPos().y, 96, 96);
		
		if(piece != null) {
			Color previousColor = tileSb.getColor();
			tileSb.setColor(Color.WHITE);
			piece.render(delta);
			tileSb.setColor(previousColor);
		}
		
		tileSb.end();
	}
	
	public void addPiece(IPiece pieceToAdd) {
		if(piece == null) {
			piece = pieceToAdd;
			piece.setParent(this);
		}
	}
	
	public void removePiece() {
		if(piece != null) {
			//setGreen(false);
			piece.setParent(null);
			piece = null;
		}
	}

	public Vector2 getPos() {
		return pos;
	}

	public Vector2 getPosBoard() {
		return new Vector2(pos.x / 96, 7 - (pos.y / 96));
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		tileSb.dispose();
	}

	public boolean isGreen() {
		return green;
	}

	public void setGreen(boolean green) {
		
		if(GlobalSettings.debugModeEnabled) {
			Gdx.app.log("DEBUG", "Tile: " + this + " Green set to: " + green);
		}
		
		this.green = green;
	}

	public boolean isRed() {
		return red;
	}

	public void setRed(boolean red) {

		if(GlobalSettings.debugModeEnabled) {
			Gdx.app.log("DEBUG", "Tile: " + this + " Red set to: " + green);
		}

		this.red = red;
	}

	
	public boolean doesHavePiece() {
		return piece != null;
	}

	public boolean isPieceWhite() {
		return piece.isWhite();
	}

	public IPiece getPiece() {
		return piece;
    }

	public boolean isCapturable() {
		return capturable;
	}

	public void setCapturable(boolean capturable) {
		this.capturable = capturable;
	}

	public void setEnPassantable(boolean enPassantable) {
		this.enPassantable = enPassantable;
	}

	public boolean isEnPassantable() {
		return enPassantable;
	}

	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
		System.out.println("Set checkable " + checkable);
	}

	public boolean isCheckable() {
		return checkable;
	}
}
