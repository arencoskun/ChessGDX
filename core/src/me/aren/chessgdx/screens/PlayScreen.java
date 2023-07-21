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
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.pieces.Pawn;
import me.aren.chessgdx.obj.pieces.Rook;

public class PlayScreen implements Screen {
	SpriteBatch sb;
	OrthographicCamera cam;
	Board board;
	BitmapFont font;
	
	public PlayScreen(ChessGdx game) {
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
		
		board.tiles[0][7].addPiece(new Rook(sb, cam, board, false));
		board.tiles[0][0].addPiece(new Rook(sb, cam, board, false));
		for(int i = 0; i < 8; i++) {
			board.tiles[1][i].addPiece(new Pawn(sb, cam, board, false));
		}
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
		board.dispose();
		font.dispose();
	}

}
