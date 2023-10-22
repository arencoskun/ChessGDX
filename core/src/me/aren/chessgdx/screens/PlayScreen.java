package me.aren.chessgdx.screens;

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
import me.aren.chessgdx.obj.pieces.Bishop;
import me.aren.chessgdx.obj.pieces.Pawn;
import me.aren.chessgdx.obj.pieces.Queen;
import me.aren.chessgdx.obj.pieces.Rook;

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
	
	public PlayScreen(ChessGdx game) {
		if(GlobalSettings.multiplayer) {
			connectSocket();
			configSocketEvents();
			
			GlobalSettings.setSocket(socket);
		}
		
		this.game = game;
		sb = game.sb;
		board = new Board(sb);
		cam = new OrthographicCamera(768, 768);
		font = new BitmapFont();
		
		cam.setToOrtho(false);
		font.setColor(Color.YELLOW);
		
		board.tiles[7][7].addPiece(new Rook(sb, cam, board, true));
		board.tiles[7][0].addPiece(new Rook(sb, cam, board, true));
		for(int i = 0; i < 8; i++) {
			board.tiles[6][i].addPiece(new Pawn(sb, cam, board, true));
		}
		
		board.tiles[4][2].addPiece(new Pawn(sb, cam, board, false));
		board.tiles[3][2].addPiece(new Pawn(sb, cam, board, true));
		
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
				System.out.println("ROOM FULL");
			}
		}).on("player-left", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				ServerData.setRoomFull(false);
				Gdx.app.log("SocketIO", "Other player left");
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
	}
	
	@Override
	public void show() {
		
	}
	
	private void update(float delta) {
		if(playerID == null && GlobalSettings.multiplayer) {
			playerID = idHandler.getPlayerID();
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
	}

	@Override
	public void dispose() {
		board.dispose();
		font.dispose();
	}

}
