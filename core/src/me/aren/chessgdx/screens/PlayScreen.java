package me.aren.chessgdx.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.obj.pieces.*;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.aren.chessgdx.ChessGdx;
import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.net.ServerData;
import me.aren.chessgdx.net.handlers.PlayerIDHandler;
import me.aren.chessgdx.obj.Board;

public class PlayScreen implements Screen {
	// TODO: Remove this
	ChessGdx game;
	
	SpriteBatch sb;
	OrthographicCamera cam;
	Board board;
	BitmapFont font;
	
	private Socket socket;
	private String playerID;
	PlayerIDHandler idHandler;

	long currentTime = 0;
	long serverTimeout = 2000;
	long lastServerConnectAttempt = 0;
	long lastServerSync = 0;
	long syncTime = 300;
	long startTime = 0;
	long textAnimateTime = 2000;
	long lastTextAnimate = 0;

	boolean connectedToServer = false;
	boolean showTurnMessage = true;
	boolean messageOnScreen = false;

	private Stage stage;
	private VisDialog waitingDialog;
	private VisLabel waitingDialogText;
	
	public PlayScreen(ChessGdx game) {
		if(GlobalSettings.multiplayer) {
			connectSocket();
			configSocketEvents();
			
			GlobalSettings.setSocket(socket);
		}
		
		this.game = game;
		sb = game.sb;
		cam = new OrthographicCamera(768, 768);
		font = new BitmapFont();
		board = new Board(sb, cam);
		
		cam.setToOrtho(false);
		font.setColor(Color.YELLOW);

		stage = new Stage();
		
		board.tiles[7][7].addPiece(new Rook(sb, cam, board, true));
		board.tiles[7][0].addPiece(new Rook(sb, cam, board, true));
		for(int i = 0; i < 8; i++) {
			board.tiles[6][i].addPiece(new Pawn(sb, cam, board, true));
		}
		
		//board.tiles[4][2].addPiece(new Knight(sb, cam, board, false));
		
		board.tiles[0][7].addPiece(new Rook(sb, cam, board, false));
		board.tiles[0][0].addPiece(new Rook(sb, cam, board, false));
		for(int i = 0; i < 8; i++) {
			board.tiles[1][i].addPiece(new Pawn(sb, cam, board, false));
		}
		
		board.tiles[7][2].addPiece(new Bishop(sb, cam, board, true));
		board.tiles[7][5].addPiece(new Bishop(sb, cam, board, true));
		board.tiles[0][2].addPiece(new Bishop(sb, cam, board, false));
		board.tiles[0][5].addPiece(new Bishop(sb, cam, board, false));
		
		board.tiles[7][3].addPiece(new Queen(sb, cam, board, true));
		board.tiles[0][3].addPiece(new Queen(sb, cam, board, false));

		board.tiles[7][1].addPiece(new Knight(sb, cam, board, true));
		board.tiles[7][6].addPiece(new Knight(sb, cam, board, true));

		board.tiles[0][1].addPiece(new Knight(sb, cam, board, false));
		board.tiles[0][6].addPiece(new Knight(sb, cam, board, false));

		board.tiles[7][4].addPiece(new King(sb, cam, board, true));
		board.tiles[0][4].addPiece(new King(sb, cam, board, false));

		board.tiles[3][4].addPiece(new King(sb, cam, board, false));

		startTime = System.currentTimeMillis();
	}
	
	private void connectSocket() {
		try {
			// https://shrimp-precise-pony.ngrok-free.app
			Gdx.app.log("SocketIO", "Trying to connect to server: " + ServerData.getAddress());
			socket = IO.socket(ServerData.getAddress());
			socket.connect();
			Gdx.app.log("SocketIO", "Socket connected..");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void configSocketEvents() {
		
		idHandler = new PlayerIDHandler(socket);
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected.");
			}
		})
		  .on("update-board", new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				JSONObject movement = (JSONObject) args[0];
				try {
					board.updateBoard(movement.getInt("originalX"), movement.getInt("originalY"),
									  movement.getInt("targetX"), movement.getInt("targetY"));
				} catch(JSONException e) {
					Gdx.app.error("SocketIO", "Error while receiving movement");
					e.printStackTrace();
				}
			}
		}).on("update-board-captured", new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				JSONObject captureData = (JSONObject) args[0];
				try {
					board.updateCapturedPiece(captureData.getInt("x"), captureData.getInt("y"), captureData.getBoolean("white"));
				} catch(JSONException e) {
					Gdx.app.error("SocketIO", "Error while receiving captured piece");
					e.printStackTrace();
				}
			}
		}).on("tile-receive-enpassantable", new Emitter.Listener() {

					@Override
					public void call(Object... args) {
						JSONObject tileData = (JSONObject) args[0];
						try {
							board.setEnPassantable(tileData.getInt("x"), tileData.getInt("y"), tileData.getBoolean("enpassantable"));
						} catch(JSONException e) {
							e.printStackTrace();
						}
					}
		}).on("pawn-change-move-count", new Emitter.Listener() {

					@Override
					public void call(Object... args) {
						JSONObject data = (JSONObject) args[0];
						try {
							board.setPawnMoveCount(data.getInt("x"), data.getInt("y"), data.getInt("moveCount"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
		}).on("room-full", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				ServerData.setRoomFull(true);
				showInfoMessage();
				System.out.println("ROOM FULL");
			}
		}).on("player-left", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				ServerData.setRoomFull(false);
				Gdx.app.log("SocketIO", "Other player left");
				socket.disconnect();
			}
		}).on("receive-turn", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				
				try {
					int turnInfo = data.getInt("turn");
					ServerData.setTurn(turnInfo);

				} catch(JSONException e) {
					Gdx.app.error("SocketIO", "JSONException while receiving receive-turn.");
					e.printStackTrace();
				}
			}
		}).on("turn-changed", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				if(ServerData.isRoomFull()) {
					VisCheckBox disableBtn = new VisCheckBox("Disable this message");
					VisLabel label = new VisLabel("It is now your turn.");
					VisDialog dialog = new VisDialog("", "dialog") {
						public void result(Object obj) {
							if ((Boolean) obj) {
								close();
							}
						}
					};

					disableBtn.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							VisCheckBox visCheckBox = (VisCheckBox) event.getListenerActor();
							System.out.println(visCheckBox.isChecked());
							showTurnMessage = !visCheckBox.isChecked();
						}
					});

					dialog.text(label);
					dialog.button("OK", true);
					dialog.getButtonsTable().row();
					dialog.getButtonsTable().add(disableBtn);
					dialog.key(Keys.ENTER, true);
					dialog.addCloseButton();
					dialog.closeOnEscape();
					dialog.setCenterOnAdd(true);
					if (stage.getActors().size == 0 && showTurnMessage) dialog.show(stage);
				}
			}
		}).on("playerID", idHandler).on("info-max-client-number-reached", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String message = data.getString("info");
					Gdx.app.error("SocketIO", message);
				} catch(JSONException e) {
					Gdx.app.error("SocketIO", "JSONException while receiving info-max-client-number-reached.");
					e.printStackTrace();
				}
				
			}
		});

		lastServerConnectAttempt = System.currentTimeMillis();
	}

	private void showInfoMessage() {
		if(GlobalSettings.multiplayer && (ServerData.getPlayerID() == 1 || ServerData.getPlayerID() == 2)) {
			VisDialog dialog = new VisDialog("", "dialog") {
				public void result(Object obj) {
					if ((Boolean) obj) {
						close();
					}
				}
			};

			dialog.text("You are player" + (ServerData.getPlayerID() == 1 ? "WHITE." : "BLACK."));
			dialog.getContentTable().row();
			if (ServerData.getPlayerID() == 1) {
				dialog.text("You move first, while the other player waits for your move.");
			} else {
				dialog.text("You wait for the WHITE player to move first, and then you will be able to make your move.");
			}
			dialog.button("OK", true);
			dialog.key(Keys.ENTER, true);
			dialog.addCloseButton();
			dialog.closeOnEscape();
			dialog.setCenterOnAdd(true);
			dialog.show(stage);
		}
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}
	
	private void update(float delta) {
		ServerData.setShowingMessage(stage.getActors().size > 0);
		if(GlobalSettings.multiplayer) {
			if(waitingDialog == null) {
				waitingDialog = new VisDialog("", "dialog");
				waitingDialogText = new VisLabel("Waiting for the other player to join.");
				waitingDialog.getContentTable().pad(30);
				waitingDialog.text(waitingDialogText);
			}

			if(currentTime > lastTextAnimate + textAnimateTime) {
				int count = (int) waitingDialogText.getText().chars().filter(ch -> ch == '.').count();

				if(count <= 4) {
					waitingDialogText.setText(waitingDialogText.getText() + ".");
				} else {
					waitingDialogText.setText(waitingDialogText.getText().substring(0, waitingDialogText.getText().length - 4));
				}
				lastTextAnimate = System.currentTimeMillis();
			}

			currentTime = System.currentTimeMillis();
			if(currentTime > lastServerConnectAttempt + serverTimeout && !socket.connected()) {
				game.setScreen(new DisconnectedScreen(game));
			} else if(socket.connected()) {
				connectedToServer = true;
			}

			if(connectedToServer && !socket.connected()) {
				game.setScreen(new DisconnectedScreen(game));
			}

			if(playerID == null && connectedToServer) playerID = idHandler.getPlayerID();

			if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
				VisDialog dialog = new VisDialog("Hey there!", "dialog") {
					public void result(Object obj) {
						if ((Boolean) obj) {
							socket.disconnect();
						}
					}
				};

				dialog.text("Are you sure you want to leave the room?");
				dialog.button("Yes", true);
				dialog.button("No", false);
				dialog.key(Keys.ENTER, true);
				dialog.addCloseButton();
				dialog.closeOnEscape();
				if(stage.getActors().size == 0)	dialog.show(stage);
			}

			if(!ServerData.isRoomFull()) {
				if(stage.getActors().size == 0) waitingDialog.show(stage);
			} else {
				waitingDialog.hide();
			}
		}
		
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
			font.setColor(Color.YELLOW);
			font.draw(sb, "Press D to disable Debug Mode", 550, 25);
			if(GlobalSettings.multiplayer) {
				font.setColor(Color.GREEN);
				font.draw(sb, "Multiplayer ID: " + playerID, 550, 75);
			}
		}
		sb.end();

		stage.act();
		stage.draw();
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
		if(GlobalSettings.multiplayer) {
			try {
				socket.disconnect();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		board.dispose();
		font.dispose();
		stage.dispose();
	}

}
