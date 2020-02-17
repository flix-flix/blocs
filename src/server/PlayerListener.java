package server;

import java.net.Socket;

import server.game.Player;
import server.model.ClientListener;
import server.model.ServerAbstract;

public class PlayerListener extends ClientListener {

	Player player;

	// =========================================================================================================================

	public PlayerListener(ServerAbstract server, Socket client) {
		super(server, client);
	}

	// =========================================================================================================================

	public void setPlayer(Player player) {
		this.player = player;
		player.setID(id);
	}
}
