package server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;

import client.Client;
import client.messages.Message;
import client.session.Player;

public class Server extends Emitter {

	public static final int port = 1212;

	HashMap<Player, InetAddress> clients = new HashMap<>();

	// =========================================================================================================================

	public Server() {
		super(port);
	}

	// =========================================================================================================================

	@Override
	public void receive(DatagramPacket packet) {
		Object obj = deserialize(packet.getData());

		System.out.print("From: " + packet.getAddress() + ":" + packet.getPort() + " : ");

		if (obj instanceof Player) {
			Player p = (Player) obj;
			for (Player client : clients.keySet())
				if (client.equals(p))
					return;
			
			System.out.println("New Player : " + p.getName());
			clients.put(p, packet.getAddress());
		} else if (obj instanceof String)
			System.out.println(((String) obj));
		else if (obj instanceof Message)
			receiveMessage(((Message) obj));
		else
			System.out.println("[Unknow object]");
	}

	// =========================================================================================================================

	public void sendAll(Object obj) {
		for (InetAddress client : clients.values())
			send(client, Client.port, serialize(obj));
	}

	// =========================================================================================================================

	public void receiveMessage(Message msg) {
		System.out.println(msg);
		if (msg.getText().charAt(0) == '!')
			sendAll("COMMAND");
		else
			sendAll(msg);
	}
}
