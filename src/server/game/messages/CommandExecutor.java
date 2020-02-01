package server.game.messages;

import game.Game;
import game.MessageManager;
import server.game.GameMode;

public class CommandExecutor {

	Game game;
	MessageManager messages;

	public CommandExecutor(Game game, MessageManager messages) {
		this.game = game;
		this.messages = messages;
	}

	// =========================================================================================================================

	// TODO [Feature] Tab-completion for commands
	// TODO [Feature] Verify if the player is OP
	public void exec(String name, String line) {
		String[] parts = line.split(" ");

		switch (parts[0]) {
		case "!help":
			messages.addTextMsg("-------------------- HELP -----------------------------");
			messages.addTextMsg("===== Commands are work in progress =====");
			break;
		case "!gamemode":
			if (parts.length != 3) {
				messages.addTextMsg("!gamemode <player> <mode>");
				return;
			}

			if (parts[2].equals(GameMode.CLASSIC.name())) {
				game.setGameMode(GameMode.CLASSIC);
				messages.addTextMsg("Gamemode updated to " + GameMode.CLASSIC.name());
			} else if (parts[2].equals(GameMode.CREATIVE.name())) {
				game.setGameMode(GameMode.CREATIVE);
				messages.addTextMsg("Gamemode updated to " + GameMode.CLASSIC.name());
			} else
				messages.addTextMsg("Available modes : " + GameMode.CLASSIC.name() + ", " + GameMode.CREATIVE.name());

			break;
		default:
			messages.addTextMsg("Unknown command. Try !help");
		}
	}
}
