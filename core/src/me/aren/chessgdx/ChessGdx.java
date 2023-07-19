package me.aren.chessgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import me.aren.chessgdx.screens.PlayScreen;

public class ChessGdx extends Game {
	public SpriteBatch sb;
	
	@Override
	public void create () {
		sb = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		sb.dispose();
	}
}
