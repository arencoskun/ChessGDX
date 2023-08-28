package me.aren.chessgdx.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerSideConnection implements Runnable {
	
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private int ID;
	private boolean running = false;
	
	private int testNumber = 13;
	private int turn = 1;
	
	public ServerSideConnection(Socket socket, int ID) {
		// TODO Auto-generated constructor stub
		System.out.println("Creating server side connection");
		this.socket = socket;
		this.ID = ID;
		
		try {
			dataIn = new DataInputStream(socket.getInputStream());
			dataOut = new DataOutputStream(socket.getOutputStream());
		} catch(IOException e) {
			System.err.println("IOException while creating server side client");
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		running = true;
		try {
			dataOut.writeInt(ID);
			dataOut.flush();
			
			while(running) {
				//dataOut.writeInt(GameServer.turn);
				int in = dataIn.readInt();
				System.out.println("INPUT: " + in);
				/*GameServer.turn = in;
				testNumber = in;
				System.out.println("Turn:" + GameServer.turn);
				System.out.println("Test number according to Server side: " + testNumber);*/
				
				// 17: GET SECRET NUMBER
				if(in == 17) {
					System.out.println("RECEIVED 17 FROM CLIENT");
					sendTestNumber();
				}
				
				// 99: GET TURN INFO
				if(in == 99) {
					System.out.println("RECEIVED 99 FROM CLIENT - SENDING TURN INFO");
					sendTurnInfo();
				}
				
				// 100: SET TURN TO 1
				if(in == 100) {
					System.out.println("RECEIVED 100 FROM CLIENT - SETTING TURN TO 1");
					GameServer.turn = 1;
				}
				
				if(in == 101) {
					System.out.println("RECEIVED 101 FROM CLIENT - SETTING TURN TO 2");
					GameServer.turn = 2;
				}
				//dataOut.flush();
				
			}
		} catch(IOException e) {
			System.err.println("IOException while trying to run server side connection");
		}
	}
	
	public void sendTestNumber() {
		System.out.println("SENDING TEST NUMBER...");
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("THREAD STARTED");
					dataOut.writeInt(testNumber);
					dataOut.flush();
					
					System.out.println("SENT TEST NUMBER TO CLIENT");
					
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		t.start();
	}
	
	public void sendTurnInfo() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("SENT TURN INFO");
					dataOut.writeInt(GameServer.turn);
					dataOut.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
