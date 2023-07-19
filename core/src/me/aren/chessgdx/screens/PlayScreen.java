package me.aren.chessgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import me.aren.chessgdx.ChessGdx;
import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.obj.Board;

public class PlayScreen implements Screen {
	SpriteBatch sb;
	Board board;
	BitmapFont font = new BitmapFont();
	
	public PlayScreen(ChessGdx game) {
		sb = game.sb;
		board = new Board(sb);
	}
	
	@Override
	public void show() {
		
	}
	
	private void update(float delta) {
		if(Gdx.input.isKeyJustPressed(Keys.D)) {
			GlobalSettings.debugModeEnabled = !GlobalSettings.debugModeEnabled;
			Gdx.app.log("DEBUG", GlobalSettings.debugModeEnabled ? "Debug mode enabled." : "Debug mode disabled.");
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		ScreenUtils.clear(0, 0, 0, 1);
		sb.begin();
		board.render(delta);
		if(GlobalSettings.debugModeEnabled) {
			font.draw(sb, "Press D to disable Debug Mode", 550, 25);
		}
		sb.end();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		
	}

}
