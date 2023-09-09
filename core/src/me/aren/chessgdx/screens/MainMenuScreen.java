package me.aren.chessgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import me.aren.chessgdx.ChessGdx;

public class MainMenuScreen implements Screen {
	
	ChessGdx game;
	private Stage stage;
	private VisTable table;
	private final String DARK_TILE = "tile_dark_gray.png";
	private final String LIGHT_TILE = "tile_light_gray.png";
	private final int TILE_WIDTH = 96, TILE_HEIGHT = 96;
	private final int BOARD_WIDTH = TILE_WIDTH * 8, BOARD_HEIGHT = TILE_HEIGHT * 8;
	private Texture darkTileTex;
	private Texture lightTileTex;
	
	public MainMenuScreen(ChessGdx game) {
		// TODO Auto-generated constructor stub
		this.game = game;
		VisUI.load();
		stage = new Stage();
		table = new VisTable();
		table.setFillParent(true);
		stage.addActor(table);
		
		darkTileTex = new Texture(DARK_TILE);
		lightTileTex = new Texture(LIGHT_TILE);
		
		addButton("Play").addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new PlayScreen(game));
			}
			
		});
		addButton("Join Server");
		addButton("Exit").addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
			
		});

	}
	
	private VisTextButton addButton(String text) {
		VisTextButton button = new VisTextButton(text);
		table.add(button).fill().pad(10).width(384).height(96);
		table.row();
		
		return button;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		ScreenUtils.clear(0, 0, 0, 0);

		game.sb.begin();
		
		for(int x = 0; x < 768; x += 96) {
			for(int y = 768 - 96; y >= 0; y -= 96) {
				boolean white = (x / TILE_WIDTH + ((BOARD_HEIGHT - TILE_HEIGHT) - y) / TILE_HEIGHT) % 2 == 0;
				
				game.sb.draw(white ? lightTileTex : darkTileTex, x, y, TILE_WIDTH, TILE_HEIGHT);
			}
		}
		
		game.sb.end();
		
		stage.act();
		stage.draw();
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
		
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
		VisUI.dispose();
		stage.dispose();
	}

}
