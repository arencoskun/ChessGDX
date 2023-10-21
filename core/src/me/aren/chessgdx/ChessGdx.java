package me.aren.chessgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.aren.chessgdx.screens.MainMenuScreen;

public class ChessGdx extends Game {
	public SpriteBatch sb;
	
	@Override
	public void create () {
		sb = new SpriteBatch();
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		sb.dispose();
		// Done to make sure the program actually exits
		System.exit(0);
	}
}
