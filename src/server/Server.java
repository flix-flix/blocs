package server;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import data.dynamic.TickClock;
import data.generation.WorldGeneration;
import data.map.Coord;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.units.Unit;
import server.game.MapServer;
import server.game.Player;
import server.game.messages.CommandExecutor;
import server.game.messages.Message;
import server.model.ServerAbstract;
import server.send.Action;
import server.send.SendAction;
import utils.Utils;

public class Server extends ServerAbstract {

	public static final int defaultPort = 1212;

	// =============== Data ===============
	MapServer map;
	CommandExecutor commands;

	// =========================================================================================================================

	public Server(int port, String name) throws IOException {
		super(port, name);

		map = new MapServer(WorldGeneration.generateMap(), this);
		commands = new CommandExecutor(this);

		TickClock clock = new TickClock("Server Clock");
		clock.add(map);

		clock.start();
	}

	public Server(int port) throws IOException {
		this(port, "Default Name");
	}

	public Server() throws IOException {
		this(defaultPort);
	}

	// =========================================================================================================================

	@Override
	protected void addListener(Socket socket) {
		PlayerListener client = new PlayerListener(this, socket);
		client.start();
		putClient(client.getID(), client);
	}

	// =========================================================================================================================

	public void sendToAllSeeing(Object obj) {

	}

	// =========================================================================================================================

	@Override
	public void receive(Object obj, int id) {
		if (obj instanceof Player)
			receivePlayer((Player) obj, id);
		else if (obj instanceof Message)
			receiveMessage((Message) obj, id);
		else if (obj instanceof SendAction)
			receiveSend((SendAction) obj, id);
		else
			Utils.debug("[Server RECEIVE] Unknown object");
	}

	// =========================================================================================================================

	public void receiveSend(SendAction send, int id) {
		System.out.println("[Server RECEIVE] " + send.action);
		switch (send.action) {
		case UNIT_GOTO:
			Unit unit = map.getUnit(send.id1);
			LinkedList<Coord> path = unit.generatePath(map, send.coord);

			unit.setPath(new LinkedList<>(path));
			sendToAll(SendAction.goTo(unit, path));
			break;
		case UNIT_BUILD:
			map.getUnit(send.id1).building(map, map.getBuilding(send.id2));
			break;
		case UNIT_HARVEST:
			map.getUnit(send.id1).harvest(map, send.coord);
			break;
		case UNIT_STORE:
			map.getUnit(send.id1).store(map, map.getBuilding(send.id2));
			break;
		default:
			break;
		}

		if (send.action != Action.UNIT_GOTO)
			sendToAll(send);
	}

	// =========================================================================================================================

	/** Client send Player to connect to this Server */
	public void receivePlayer(Player player, int id) {
		getPlayer(id).setPlayer(player);
		sendToPlayer(map, id);
	}

	public void receiveMessage(Message msg, int id) {
		if (!msg.getText().isEmpty() && msg.getText().charAt(0) == '!')
			commands.exec(getPlayer(id).player, msg.getText());
		else
			sendToAll(msg);
	}

	// =========================================================================================================================

	public void addCube(Cube c) {
		System.out.println("[SERVER] Add : " + c.toString());
		sendToAll(SendAction.add(c));
	}

	public void removeCube(int x, int y, int z) {
		System.out.println("[SERVER] Remove : " + new Coord(x, y, z).toString());
		new Coord(x, y, z);
		sendToAll(SendAction.remove(new Coord(x, y, z)));
	}

	// =========================================================================================================================

	public void unitArrive(Unit unit) {
		sendToAll(SendAction.arrive(unit));
	}

	public void unitHarvest(Unit unit, Coord coord) {
		sendToAll(SendAction.harvest(unit, coord).finished());
	}

	public void unitStore(Unit unit, Building build) {
		sendToAll(SendAction.store(unit, build).finished());
	}

	// =========================================================================================================================
	// Getters

	public PlayerListener getPlayer(int id) {
		return (PlayerListener) getClient(id);
	}
}
