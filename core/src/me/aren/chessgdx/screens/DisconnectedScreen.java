package me.aren.chessgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import me.aren.chessgdx.ChessGdx;
import me.aren.chessgdx.GlobalSettings;

public class DisconnectedScreen implements Screen {
	
	BitmapFont font;
	SpriteBatch sb;
	ChessGdx game;
	
	public DisconnectedScreen(ChessGdx game) {
		this.game = game;
		
		sb = new SpriteBatch();
		
		font = new BitmapFont();
		font.setColor(Color.WHITE);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		//GlobalSettings.setSocket(null);

		if(GlobalSettings.multiplayer) {
			GlobalSettings.multiplayer = false;
		}
	}
	
	private void update() {
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			game.setScreen(new MainMenuScreen(game));
		}
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		update();
		ScreenUtils.clear(Color.BLACK);
		sb.begin();
		font.draw(sb, "Disconnected", 300, 300);
		font.draw(sb, "Press ENTER to return to main menu", 300, 200);
		sb.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		//font.dispose();
	}

}
