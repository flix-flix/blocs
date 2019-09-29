package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import client.messages.Message;
import client.session.Session;
import server.Emitter;
import server.Server;

public class Client extends Emitter {

	public static final int port = 1213;

	InetAddress server;

	Session session;

	// =========================================================================================================================

	public Client(Session session) {
		super(port);
		this.session = session;

		try {
			// TODO Other than LocalHost
			server = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================================================

	public void send(Object obj) {
		super.send(server, Server.port, obj);
	}

	@Override
	public void receive(DatagramPacket packet) {
		Object obj = deserialize(packet.getData());
		System.out.println(obj);

		if (obj instanceof Message)
			session.messages.receive((Message) obj);
		else
			System.out.println("Unknown object");
	}

	// =========================================================================================================================

}
