package data.dynamic;

import java.util.ArrayList;

public class TickClock implements Runnable {

	boolean run = true;

	public int ticks;

	private long lastTime;

	ArrayList<Tickable> tickables = new ArrayList<>();

	private boolean paused = false;

	private String threadName;

	// =========================================================================================================================

	public TickClock() {
		this("TickClock default name");
	}

	public TickClock(String threadName) {
		this.threadName = threadName;
	}

	// =========================================================================================================================

	@Override
	public void run() {
		lastTime = System.currentTimeMillis();
		while (run) {
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

	public void start() {
		Thread t = new Thread(this);
		t.setName(threadName);
		t.start();
	}

	public void stop() {
		run = false;
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
