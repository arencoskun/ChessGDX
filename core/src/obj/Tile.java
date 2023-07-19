package obj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Tile implements GameObject {
	final String TILE_LIGHT_PATH = "tile_light_brown.png";
	final String TILE_DARK_PATH = "tile_dark_brown.png";
	SpriteBatch sb;
	boolean white;
	Vector2 pos;
	Texture tileTexture;
	
	public Tile(SpriteBatch sb, boolean white, Vector2 pos) {
		this.sb = sb;
		this.white = white;
		this.pos = pos;
		
		tileTexture = new Texture(white ? Gdx.files.internal(TILE_LIGHT_PATH) : Gdx.files.internal(TILE_DARK_PATH));
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		sb.draw(tileTexture, pos.x, pos.y, 96, 96);
	}
}
