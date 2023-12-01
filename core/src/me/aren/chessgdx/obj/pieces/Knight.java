package me.aren.chessgdx.obj.pieces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.aren.chessgdx.obj.Board;
import me.aren.chessgdx.obj.Tile;
import me.aren.chessgdx.obj.interfaces.IPiece;

import java.util.concurrent.LinkedBlockingQueue;

public class Knight implements IPiece {
    final String KNIGHT_PATH_WHITE = "knight_white.png";
    final String KNIGHT_PATH_BLACK = "knight_black.png";
    Texture knightTexture;
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


    public Knight(SpriteBatch sb, OrthographicCamera cam, Board board, boolean white) {
        // TODO Auto-generated constructor stub
        this.sb = sb;
        this.cam = cam;
        this.board = board;
        this.white = white;

        validPositions = new LinkedBlockingQueue<>();

        knightTexture = new Texture(white ? Gdx.files.internal(KNIGHT_PATH_WHITE) : Gdx.files.internal(KNIGHT_PATH_BLACK));
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

        if(x - 2 > -1) {
            if(y - 1 > -1) {
                if(board.tiles[y - 1][x - 2].doesHavePiece()) {
                     if(board.tiles[y - 1][x - 2].isPieceWhite() != white) {
                         getValidPositions().add(board.tiles[y - 1][x - 2]);
                         board.tiles[y - 1][x - 2].setCapturable(true);
                     }
                } else {
                    getValidPositions().add(board.tiles[y - 1][x - 2]);
                }
            }

            if(y + 1 < 8) {
                if(board.tiles[y + 1][x - 2].doesHavePiece()) {
                    if(board.tiles[y + 1][x - 2].isPieceWhite() != white) {
                        getValidPositions().add(board.tiles[y + 1][x - 2]);
                        board.tiles[y + 1][x - 2].setCapturable(true);
                    }
                } else {
                    getValidPositions().add(board.tiles[y + 1][x - 2]);
                }
            }
        }

        if(x - 1 > -1) {
            if(y - 2 > -1) {
                if(board.tiles[y - 2][x - 1].doesHavePiece()) {
                    if(board.tiles[y - 2][x - 1].isPieceWhite() != white) {
                        getValidPositions().add(board.tiles[y - 2][x - 1]);
                        board.tiles[y - 2][x - 1].setCapturable(true);
                    }
                } else {
                    getValidPositions().add(board.tiles[y - 2][x - 1]);
                }
            }

            if(y + 2 < 8) {
                if(board.tiles[y + 2][x - 1].doesHavePiece()) {
                    if(board.tiles[y + 2][x - 1].isPieceWhite() != white) {
                        getValidPositions().add(board.tiles[y + 2][x - 1]);
                        board.tiles[y + 2][x - 1].setCapturable(true);
                    }
                } else {
                    getValidPositions().add(board.tiles[y + 2][x - 1]);
                }
            }
        }

        if(x + 1 < 8) {
            if(y - 2 > -1) {
                if(board.tiles[y - 2][x + 1].doesHavePiece()) {
                    if(board.tiles[y - 2][x + 1].isPieceWhite() != white) {
                        getValidPositions().add(board.tiles[y - 2][x + 1]);
                        board.tiles[y - 2][x + 1].setCapturable(true);
                    }
                } else {
                    getValidPositions().add(board.tiles[y - 2][x + 1]);
                }
            }

            if(y + 2 < 8) {
                if(board.tiles[y + 2][x + 1].doesHavePiece()) {
                    if(board.tiles[y + 2][x + 1].isPieceWhite() != white) {
                        getValidPositions().add(board.tiles[y + 2][x + 1]);
                        board.tiles[y + 2][x + 1].setCapturable(true);
                    }
                } else {
                    getValidPositions().add(board.tiles[y + 2][x + 1]);
                }
            }
        }

        if(x + 2 < 8) {
            if(y - 1 > -1) {
                if(board.tiles[y - 1][x + 2].doesHavePiece()) {
                    if(board.tiles[y - 1][x + 2].isPieceWhite() != white) {
                        getValidPositions().add(board.tiles[y - 1][x + 2]);
                        board.tiles[y - 1][x + 2].setCapturable(true);
                    }
                } else {
                    getValidPositions().add(board.tiles[y - 1][x + 2]);
                }
            }

            if(y + 1 < 8) {
                if(board.tiles[y + 1][x + 2].doesHavePiece()) {
                    if(board.tiles[y + 1][x + 2].isPieceWhite() != white) {
                        getValidPositions().add(board.tiles[y + 1][x + 2]);
                        board.tiles[y + 1][x + 2].setCapturable(true);
                    }
                } else {
                    getValidPositions().add(board.tiles[y + 1][x + 2]);
                }
            }
        }

        for(Tile validPosition : getValidPositions()) {
            if(validPosition.doesHavePiece() && validPosition.getPiece() instanceof King) {
                King king = (King) validPosition.getPiece();

                if(king.isWhite() != isWhite()) {
                    validPosition.setCheckable(true);
                    board.setCheckWhite(king.isWhite());
                }
            } else {
                validPosition.setCheckable(false);
            }
        }
    }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub
        update(delta, board, cam);
        if(getParent() != null) sb.draw(knightTexture, parent.getPos().x, parent.getPos().y, WIDTH, HEIGHT);
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
        knightTexture.dispose();
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
        return "knight";
    }

    @Override
    public void afterMove() {
        // TODO Auto-generated method stub
        if(isCaptured()) {
            getParent().removePiece();
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
