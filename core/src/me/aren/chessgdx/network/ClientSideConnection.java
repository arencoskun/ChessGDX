package me.aren.chessgdx.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.badlogic.gdx.Gdx;

import me.aren.chessgdx.screens.PlayScreen;

public class ClientSideConnection {
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private PlayScreen playScreen;
	
	int turn = 1;
	int testNumber;
	
	public ClientSideConnection(PlayScreen playScreen) {
		Gdx.app.log("CLIENT", "Creating client side connection...");
		
		this.playScreen = playScreen;
		
		try {
			socket = new Socket("localhost", 51734);
			dataIn = new DataInputStream(socket.getInputStream());
			dataOut = new DataOutputStream(socket.getOutputStream());
			playScreen.setCurrentPlayerID(dataIn.readInt());
			Gdx.app.log("CLIENT", "Connected to server as player " + playScreen.getCurrentPlayerID());
		} catch(IOException e) {
			Gdx.app.error("CLIENT", "IOException while creating client socket");
		}
	}
	
	public int receiveTurn() {
		
		
		
		try {
			dataOut.writeInt(2);
			dataOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return GameServer.turn;
	}
	
	public int getTestNumber() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					dataOut.writeInt(17);
					dataOut.flush();
					
					System.out.println("SENT 17 TO SERVER");
					return;
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		t.start();
		
		System.out.println("RUNNING THE FUNCTION TO GET THE ANSWER...");
		int answer = getAnswerFromServer();
		
		return answer;
	}
	
	private int getAnswerFromServer() {
		System.out.println("GETTING ANSWER FROM SERVER...");
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					int in = dataIn.readInt();
					System.out.println("GOT ANSWER FROM SERVER -- " + in);
					testNumber = in;
					System.out.println("GOT ANSWER - RETURNING FROM THREAD");
				} catch(IOException e) {
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
		
		System.out.println("THE SECRET NUMBER IS: " + testNumber);
		
		return testNumber;
	}
	
	public int getTurnInfo() {
		Thread askToServer = new Thread(new Runnable() {
			public void run() {
				try {
					dataOut.writeInt(99);
					dataOut.flush();
					
					System.out.println("SENT 99 TO SERVER");
					
					int in = dataIn.readInt();
					System.out.println("GOT TURN: " + in);
					turn = in;
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		askToServer.start();
		
		try {
			askToServer.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return turn;
	}
	
	public void setTurn(int newTurn) {
		Thread sendToServer = new Thread(new Runnable() {
			public void run() {
				try {
					if(newTurn == 1) {
						dataOut.writeInt(100);
						dataOut.flush();
					} else if(newTurn == 2) {
						dataOut.writeInt(101);
						dataOut.flush();
					}
					
					System.out.println("SENT 100 TO SERVER");
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		sendToServer.start();
		
		try {
			sendToServer.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test() {
		try {
			System.out.println("Writing 2 to dataOut");
			dataOut.writeInt(2);
			dataOut.writeInt(8);
			dataOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
