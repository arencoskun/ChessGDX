package me.aren.chessgdx.obj.pieces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.obj.interfaces.IPiece;

import java.util.concurrent.LinkedBlockingQueue;

public class King implements IPiece {
    final String KING_PATH_WHITE = "king_white.png";
    final String KING_PATH_BLACK = "king_black.png";
    Texture kingTexture;
    public Tile parent;
    SpriteBatch sb;
    OrthographicCamera cam;
    boolean selected = false;
    boolean calculatedValidPositions = false;
    boolean captured = false;
    boolean white;
    //boolean justMoved = false;
    Board board;
    LinkedBlockingQueue<Tile> validPositions;
    boolean hasMoved = false;
    long lastMove = 0;
    long moveCooldown = 200;


    public King(SpriteBatch sb, OrthographicCamera cam, Board board, boolean white) {
        // TODO Auto-generated constructor stub
        this.sb = sb;
        this.cam = cam;
        this.board = board;
        this.white = white;

        validPositions = new LinkedBlockingQueue<>();

        kingTexture = new Texture(white ? Gdx.files.internal(KING_PATH_WHITE) : Gdx.files.internal(KING_PATH_BLACK));
    }

    @Override
    public void calculateValidPositions(Board board) {
        // TODO Auto-generated method stub
        if(!getValidPositions().isEmpty()) {
            for(Tile tile : getValidPositions()) {
                getValidPositions().remove(tile);
            }
        }

        if(parent == null) return;

        int x = (int) parent.getPosBoard().x;
        int y = (int) parent.getPosBoard().y;

        for(int iY = y - 1; iY <= y + 1; iY++) {
            for(int iX = x - 1; iX <= x + 1; iX++) {
                if(iY > -1 && iY < 8 && iX > -1 && iX < 8) {
                    if((board.tiles[iY][iX].doesHavePiece() && board.tiles[iY][iX].isPieceWhite() != white) ||
                        !board.tiles[iY][iX].doesHavePiece()) getValidPositions().add(board.tiles[iY][iX]);
                    if((board.tiles[iY][iX].doesHavePiece() && board.tiles[iY][iX].isPieceWhite() != white)) {
                        board.tiles[iY][iX].setCapturable(true);
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub
        update(delta, board, cam);
        if(getParent() != null) sb.draw(kingTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
    }

    @Override
    public void setParent(Tile tile) {
        parent = tile;
    }

    @Override
    public Tile getParent() {
        return parent;
    }

    @Override
    public boolean isSelected() {
        // TODO Auto-generated method stub
        return selected;
    }

    public void setSelected(boolean selected) {
        // TODO Auto-generated method stub
        this.selected = selected;
        //parent.setGreen(true);

    }

    @Override
    public LinkedBlockingQueue<Tile> getValidPositions() {
        // TODO Auto-generated method stub
        return validPositions;
    }

    @Override
    public boolean isCaptured() {
        return captured;
    }

    @Override
    public void setCaptured(boolean captured) {
        this.captured = true;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        kingTexture.dispose();
    }

    @Override
    public boolean validPositionsCalculated() {
        // TODO Auto-generated method stub
        return calculatedValidPositions;
    }

    @Override
    public void setValidPositionsCalculated(boolean calculated) {
        // TODO Auto-generated method stub
        calculatedValidPositions = calculated;
    }

    // GameObject update method

    @Override
    public void update(float delta) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isWhite() {
        // TODO Auto-generated method stub
        return white;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "king";
    }

    @Override
    public void afterMove() {
        // TODO Auto-generated method stub

        if(isCaptured()) {
            getParent().removePiece();
            getParent().setCheckable(false);
            getParent().setRed(false);
            setParent(null);
        }
    }

    @Override
    public void afterCapture() {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterTurnChange(boolean newTurn) {
        // TODO Auto-generated method stub
    }

    @Override
    public long getLastMoveTime() {
        return lastMove;
    }

    @Override
    public void setLastMoveTime(long lastMoveTime) {
        this.lastMove = lastMoveTime;
    }

    @Override
    public long getMoveCooldown() {
        return moveCooldown;
    }
}
