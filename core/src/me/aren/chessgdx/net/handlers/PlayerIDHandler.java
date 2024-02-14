package me.aren.chessgdx.net.handlers;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.aren.chessgdx.net.ServerData;

public class PlayerIDHandler implements Emitter.Listener {
	
	private String playerID;
	private Socket socket;
	
	public PlayerIDHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void call(Object... args) {
		JSONObject data = (JSONObject) args[0];
		String playerID;
		try {
			playerID = data.getString("id");
			this.playerID = playerID;
			ServerData.setPlayerID(Integer.parseInt(playerID));
			Gdx.app.log("SocketIO", "ID: " + playerID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Gdx.app.error("SocketIO", "Could not get player ID - disconnecting...");
			socket.disconnect();
		}
	}
	
	public String getPlayerID() {
		return playerID;
	}

}
