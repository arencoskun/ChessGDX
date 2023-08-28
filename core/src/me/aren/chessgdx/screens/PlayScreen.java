package me.aren.chessgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import me.aren.chessgdx.ChessGdx;
import me.aren.chessgdx.GlobalSettings;
import me.aren.chessgdx.network.ClientSideConnection;
import me.aren.chessgdx.network.GameServer;
import me.aren.chessgdx.network.ServerSideConnection;
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.pieces.Bishop;
import me.aren.chessgdx.obj.pieces.Pawn;
import me.aren.chessgdx.obj.pieces.Rook;

public class PlayScreen implements Screen {
	SpriteBatch sb;
	OrthographicCamera cam;
	public Board board;
	BitmapFont font;
	
	private ClientSideConnection connection;
	private int currentPlayer;
	private int enemy;
	int turn = 1;
	
	public PlayScreen(ChessGdx game) {
		connectToServer();
		Gdx.graphics.setTitle(String.valueOf(getCurrentPlayerID()));
		sb = game.sb;
		board = new Board(sb, connection);
		board.turnOnline = connection.receiveTurn();
		cam = new OrthographicCamera(768, 768);
		font = new BitmapFont();
		
		cam.setToOrtho(false);
		font.setColor(Color.YELLOW);
		
		board.tiles[7][7].addPiece(new Rook(sb, cam, board, getCurrentPlayerID(), connection, true));
		board.tiles[7][0].addPiece(new Rook(sb, cam, board, getCurrentPlayerID(), connection, true));
		for(int i = 0; i < 8; i++) {
			board.tiles[6][i].addPiece(new Pawn(sb, cam, board, getCurrentPlayerID(), connection, true));
		}
		
		board.tiles[0][7].addPiece(new Rook(sb, cam, board, getCurrentPlayerID(), connection, false));
		board.tiles[0][0].addPiece(new Rook(sb, cam, board, getCurrentPlayerID(), connection, false));
		for(int i = 0; i < 8; i++) {
			board.tiles[1][i].addPiece(new Pawn(sb, cam, board, getCurrentPlayerID(), connection, false));
		}
		
		board.tiles[7][2].addPiece(new Bishop(sb, cam, board, getCurrentPlayerID(), connection, true));
		board.tiles[7][5].addPiece(new Bishop(sb, cam, board, getCurrentPlayerID(), connection, true));
		board.tiles[0][2].addPiece(new Bishop(sb, cam, board, getCurrentPlayerID(), connection, false));
		board.tiles[0][5].addPiece(new Bishop(sb, cam, board, getCurrentPlayerID(), connection, false));
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
			font.draw(sb, "Player ID: " + String.valueOf(getCurrentPlayerID()), 550, 50);
			
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.Z)) {
			System.out.println("Z PRESSED");
			System.out.println("IS THE NUMBER: " + connection.getTestNumber());
			connection.test();
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
		board.dispose();
		font.dispose();
	}
	
	public void connectToServer() {
		connection = new ClientSideConnection(this);
	}
	
	public void setCurrentPlayerID(int ID) {
		if(ID == 1) {
			currentPlayer = 1;
			enemy = 2;
		} else {
			currentPlayer = 2;
			enemy = 1;
		}
	}
	
	public int getCurrentPlayerID() {
		return currentPlayer;
	}
	
	public ClientSideConnection getClientConnection() {
		return connection;
	}

}
