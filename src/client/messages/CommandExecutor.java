package client.messages;

import client.session.Session;

public class CommandExecutor {

	Session session;
	MessageManager messages;

	public CommandExecutor(Session session, MessageManager messages) {
		this.session = session;
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
		default:
			messages.addTextMsg("Unknown command. Try !help");
		}
	}
}
