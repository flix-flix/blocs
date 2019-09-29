package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public abstract class Emitter implements Runnable {

	Thread thread;

	DatagramSocket emitter;
	boolean running = true;

	ArrayList<InetAddress> clients = new ArrayList<>();

	// =========================================================================================================================

	public Emitter(int port) {
		try {
			emitter = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		thread = new Thread(this);
		thread.start();
	}

	// =========================================================================================================================

	@Override
	public void run() {
		try {
			while (running) {
				byte[] buffer = new byte[8192];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

				emitter.receive(packet);

				if (!running)
					return;

				receive(packet);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		running = false;
		emitter.close();
	}

	// =========================================================================================================================

	public abstract void receive(DatagramPacket packet);

	public void send(InetAddress address, int port, byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);

		try {
			emitter.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(InetAddress address, int port, Object obj) {
		send(address, port, serialize(obj));
	}

	// =========================================================================================================================

	public static byte[] serialize(Object obj) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os;

		try {
			os = new ObjectOutputStream(out);
			os.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	public static Object deserialize(byte[] data) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is;
		Object obj = null;

		try {
			is = new ObjectInputStream(in);
			obj = is.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return obj;
	}
}
