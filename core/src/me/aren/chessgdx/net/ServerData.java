package me.aren.chessgdx.net;

public abstract class ServerData {
	private static int turn;
	private static int playerID;
	private static boolean roomFull;
	private static String address;
	private static boolean showingMessage;
	
	public static void setPlayerID(int playerID) {
		ServerData.playerID = playerID;
	}
	
	public static void setTurn(int turn) {
		ServerData.turn = turn;
	}
	
	public static void setRoomFull(boolean roomFull) {
		ServerData.roomFull = roomFull;
	}

	public static void setAddress(String address) {
		ServerData.address = address;
	}

	public static int getPlayerID() {
		return playerID;
	}
	
	public static int getTurn() {
		return turn;
	}
	
	public static boolean isRoomFull() {
		return roomFull;
	}

	public static String getAddress() {
		return address;
	}

	public static boolean isShowingMessage() {
		return showingMessage;
	}

	public static void setShowingMessage(boolean showingMessage) {
		ServerData.showingMessage = showingMessage;
	}
}
