package server.game.messages;

import java.awt.Color;

import server.Server;
import server.game.GameMode;
import server.game.Player;
import utils.TextPlus;
import utils.TextPlusPart;

public class CommandExecutor {

	Server server;

	public CommandExecutor(Server server) {
		this.server = server;
	}

	// =========================================================================================================================

	// TODO [Feature] Tab-completion for commands
	public void exec(Player player, String cmd) {
		String[] parts = cmd.split(" ");

		switch (parts[0]) {
		case "!help":
			consoleMsg("=============== HELP ===============", player);
			errorMsg("Commands are a Work In Progress", player);

			TextPlus plus = new TextPlus();
			plus.add(new TextPlusPart("!help", null, Color.BLACK));
			plus.add(new TextPlusPart(": Display this help", null, Color.WHITE));

			consoleMsg(plus, player);
			break;
		case "!gamemode":
			if (parts.length != 3) {
				consoleMsg("!gamemode <player> <mode>", player);
				return;
			}

			if (parts[2].equals(GameMode.CLASSIC.name())) {
				consoleMsg("Gamemode updated to " + GameMode.CLASSIC.name(), player);
			} else if (parts[2].equals(GameMode.CREATIVE.name())) {
				consoleMsg("Gamemode updated to " + GameMode.CLASSIC.name(), player);
			} else
				errorMsg("Available modes : " + GameMode.CLASSIC.name() + ", " + GameMode.CREATIVE.name(), player);

			break;
		default:
			errorMsg("Unknown command. Try !help", player);
		}
	}

	// =========================================================================================================================

	public void msg(Message msg, Player player) {
		server.sendToPlayer(msg, player.id);
	}

	public void consoleMsg(TextPlus text, Player player) {
		server.sendToPlayer(new Message(text, TypeMessage.CONSOLE), player.id);
	}

	public void msg(String text, TypeMessage type, Player player) {
		server.sendToPlayer(new Message(text, type), player.id);
	}

	public void textMsg(String str, Player player) {
		msg(new Message(str, TypeMessage.TEXT), player);
	}

	public void consoleMsg(String str, Player player) {
		msg(new Message(str, TypeMessage.CONSOLE), player);
	}

	public void errorMsg(String str, Player player) {
		msg(new Message(str, TypeMessage.ERROR), player);
	}

	// =========================================================================================================================

	public boolean isOP(Player player) {
		// TODO [Feature] Verify if the player is OP
		return true;
	}
}
