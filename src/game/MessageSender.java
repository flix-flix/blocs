package game;

import server.game.messages.Message;

public interface MessageSender {
	public void sendMessage(Message msg);
}
