package data.dynamic;

import java.util.ArrayList;

public class TickClock implements Runnable {

	public int ticks;

	private long lastTime;

	ArrayList<Tickable> tickables = new ArrayList<>();

	private boolean paused = false;

	// =========================================================================================================================

	public TickClock() {
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

			if (paused)
				continue;

			ticks++;

			for (Tickable t : tickables) {
				t.tick();
			}
		}
	}

	// =========================================================================================================================

	public void add(Tickable t) {
		tickables.add(t);
	}

	public void remove(Tickable t) {
		tickables.remove(t);
	}

	// =========================================================================================================================

	public void setPaused(boolean b) {
		paused = b;
	}
}
