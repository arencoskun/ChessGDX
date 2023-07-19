package me.aren.chessgdx.obj.interfaces;

import java.util.LinkedList;

import me.aren.chessgdx.obj.Tile;

public interface IPiece extends IGameObject {
	int WIDTH = 96, HEIGHT = 96;
	public void update(float delta);
	public void render(float delta);
	public void setParent(Tile tile);
	public Tile getParent();
	public boolean isSelected();
	// TODO: Maybe this should not be a LinkedList??
	public LinkedList<Tile> getValidPositions();
}
