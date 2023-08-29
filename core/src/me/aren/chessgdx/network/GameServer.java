package me.aren.chessgdx.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
	private ServerSocket ss;
	private int playerCount;
	private ServerSideConnection player1;
	private ServerSideConnection player2;
	
	public static int turn;
	public static int[] movement = new int[2];
	public static int turnCount;
	
	public GameServer() {
		// TODO Refactor the server to use the LibGDX ServerSocket
		System.out.println("Creating game server");
		playerCount = 0;
		turn = 1;
		
		try {
			ss = new ServerSocket(51734);
		} catch(IOException e) {
			System.err.println("IOException while creating ServerSocket");
		}
	}
	
	public void acceptConnections() {
		try {
			System.out.println("Waiting for connections...");
			
			while(playerCount < 2) {
				if(ss == null) System.exit(1);
				Socket socket = ss.accept();
				playerCount++;
				System.out.println("Player " + playerCount + " has connected.");
				ServerSideConnection connection = new ServerSideConnection(socket, playerCount);
				
				if(playerCount == 1) {
					player1 = connection;
				} else {
					player2 = connection;
				}
				
				Thread thread = new Thread(connection);
				thread.start();
			}
			
			System.out.println("Player limit reached - no longer accepting connections.");
		} catch(IOException e) {
			System.err.println("IOException while waiting for connections");
		}
	}
	
	public static void main(String[] args) {
		GameServer server = new GameServer();
		server.acceptConnections();
	}
}
