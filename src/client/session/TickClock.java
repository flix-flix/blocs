package client.session;

import java.util.ArrayList;

import client.window.panels.StateHUD;

public class TickClock implements Runnable {

	private Session session;
	public int ticks;

	private long lastTime;

	ArrayList<Tickable> tickables = new ArrayList<>();

	// =========================================================================================================================

	TickClock(Session session) {
		this.session = session;
	}

	// =========================================================================================================================

	public void add(Tickable t) {
		tickables.add(t);
	}

	public void remove(Tickable t) {
		tickables.remove(t);
	}

	// =========================================================================================================================

	@Override
	public void run() {
		lastTime = System.currentTimeMillis();
		while (true) {
			if (System.currentTimeMillis() - lastTime < 50) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			} else
				lastTime = System.currentTimeMillis();

			if (session.stateGUI != StateHUD.GAME)
				continue;

			ticks++;

			for (Tickable t : tickables) {
				t.tick();
			}
		}
	}
}
