package me.aren.chessgdx;

import io.socket.client.Socket;

public abstract class GlobalSettings {
	public static boolean debugModeEnabled = false;
	public static boolean multiplayer = false;
	
	private static Socket socket;
	
	public static Socket getSocket() {
		return socket;
	}
	
	public static void setSocket(Socket socket) {
		GlobalSettings.socket = socket;
	}
}
