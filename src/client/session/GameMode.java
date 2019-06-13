package client.session;

public enum GameMode {

	CLASSIC(0), CREATIVE(1), SPECTATOR(2);

	int id;

	GameMode(int n) {
		id = n;
	}

	public int getNumero() {
		return id;
	}
}
