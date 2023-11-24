package me.aren.chessgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import me.aren.chessgdx.ChessGdx;
import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.net.ServerData;

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
	private boolean https = true;
	
	public MainMenuScreen(ChessGdx game) {
		// TODO Auto-generated constructor stub
		this.game = game;
		if(!VisUI.isLoaded()) VisUI.load();
		stage = new Stage();
		table = new VisTable();
		table.setFillParent(true);
		stage.addActor(table);
		
		darkTileTex = new Texture(DARK_TILE);
		lightTileTex = new Texture(LIGHT_TILE);
		
		addButton("Play").addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				GlobalSettings.multiplayer = false;
				game.setScreen(new PlayScreen(game));
			}
			
		});
		addButton("Join Server").addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				VisDialog dialog = new VisDialog("Enter server address:", "dialog");

				VisTextField textField = new VisTextField();
				textField.setMessageText("Server IP Address");
				VisTextButton submitButton = new VisTextButton("Join");
				VisCheckBox httpsButton = new VisCheckBox("HTTPS");

				submitButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						String text = textField.getText().trim().toLowerCase();
						if(!text.isEmpty()) {
							if(text.contains("https://")) {
								text = text.substring(8);
							} else if(text.contains("http://")) {
								text = text.substring(7);
							}
							ServerData.setAddress((https ? "https://" : "http://") + text);
							GlobalSettings.multiplayer = true;
							game.setScreen(new PlayScreen(game));
						}
					}
				});

				httpsButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						VisCheckBox visCheckBox = (VisCheckBox) event.getListenerActor();
						https = visCheckBox.isChecked();
					}
				});

				httpsButton.setChecked(https);

				dialog.add(textField).fill().width(400);
				dialog.button(submitButton);
				dialog.add(httpsButton);

				dialog.addCloseButton();
				dialog.show(stage);
			}
		});
		addButton("Exit").addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				VisDialog dialog = new VisDialog("Goodbye :(", "dialog") {
				    public void result(Object obj) {
				        if((Boolean) obj) Gdx.app.exit();
				    }
				};

				dialog.text("Are you sure you want to quit?");
				dialog.button("Yes", true);
				dialog.button("No", false);
				dialog.key(Keys.ENTER, true);
				dialog.closeOnEscape();
				dialog.addCloseButton();
				dialog.show(stage);
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

	private void update(float delta) {
		if(Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
			if (Gdx.input.isKeyJustPressed(Keys.H)) {
				ServerData.setAddress("http://localhost:3131");
				GlobalSettings.multiplayer = true;
				game.setScreen(new PlayScreen(game));
			}
		}
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		update(delta);
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
