package environment;

import data.map.enumerations.Orientation;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;

public class Environment3D {

	protected PanEnvironment panel;

	// =============== F3 (Dev infos) ===============
	/** Number of frames displayed the last second */
	public int fps;

	/** Chronometric marks */
	public long timeMat, timeDraw, timeQuadri;
	/** Number of chunks and faces displayed */
	public int nbChunks, nbFaces;

	/** Number of state-checks of the mouse and keyboard */
	public int ticksKeyBoard;
	/** Number of steps of the simulated environment */
	public int ticksPhys;

	// =============== Options ===============
	/** Max frames/seconde allowed */
	public int FPSmax = 30;

	// =============== Thread ===============
	boolean run = true;
	/** true : currently processing a new image */
	private boolean processing = false;
	/** true : suspend the generation of new images */
	private boolean paused = false;

	// =============== Engine ===============
	protected Engine engine;
	protected MapClient map;
	protected Camera camera;

	// =============== Target ===============
	public Target target = new Target();

	// =========================================================================================================================

	public Environment3D() {
		panel = new PanEnvironment(this);
		engine = new Engine(null, null);
	}

	public Environment3D(MapClient map, Camera camera) {
		this();
		this.map = map;
		this.camera = camera;

		panel.setCamera(camera);
		engine.setModelCamera(map, camera);
	}

	// =========================================================================================================================
	// Thread

	public void start() {
		if (!run)
			return;

		panel.setCamera(camera);
		engine.setModelCamera(map, camera);

		Thread thread = new Thread(new RefreshImage());
		thread.setName("Refresh Image");
		thread.start();
	}

	public void stop() {
		run = false;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}

	// =========================================================================================================================

	public void setEngineBackground(int x) {
		engine.setBackground(x);
	}

	// =========================================================================================================================

	public Orientation getCameraOrientation() {
		Orientation orientation = null;
		if (camera.getVx() >= 45 && camera.getVx() < 135)
			orientation = Orientation.EAST;
		else if (camera.getVx() >= 315 || camera.getVx() < 45)
			orientation = Orientation.NORTH;
		else if (camera.getVx() < 225)
			orientation = Orientation.SOUTH;
		else
			orientation = Orientation.WEST;
		return orientation;
	}

	// =========================================================================================================================

	/** Set target location (e.g. mouse location or cross-indicator) */
	public void setTarget(int x, int y) {
		engine.setTarget(x, y);
	}

	public void setTargetCenter() {
		setTarget(panel.envWidth / 2, panel.envHeight / 2);
	}

	public void setTargetNull() {
		setTarget(-10_000, -10_000);
	}

	// =========================================================================================================================

	public void targetUpdate() {
		Target target = new Target(engine);

		boolean sameTarget = target.equals(this.target, isNeededQuadriPrecision());

		// If different target
		if (!sameTarget) {
			// If it replace an existant one => Update
			if (this.target.isValid())
				looseTarget();

			this.target = target;

			// If target isn't void => Update
			if (target.isValid())
				gainTarget();
		}
	}

	// =========================================================================================================================

	public void updateTimeDev() {
		timeMat = engine.timeMat - engine.timeStart;
		timeDraw = engine.timeDraw - engine.timeMat;
		timeQuadri = engine.timeEnd - engine.timeDraw;

		nbChunks = map.nbChunks;
		nbFaces = map.nbFaces;
	}

	// =========================================================================================================================
	// Interface

	/** Called on new (non-null) target */
	public void gainTarget() {
	}

	/** Called on new Target (if previous was non-null) */
	public void looseTarget() {
	}

	/** Ask if the target must take care of the quadri id */
	public boolean isNeededQuadriPrecision() {
		return false;
	}

	/** Called each second : to update development data */
	public void oneSecondTick() {
	}

	/** Called when a new Image have been generated */
	public void repaintEnvironment() {
		panel.repaint();
	}

	// =========================================================================================================================

	public PanEnvironment getPanel() {
		return panel;
	}

	public void switchDevlopMode() {
		panel.showEngineInfos = !panel.showEngineInfos;
	}

	public Camera getCamera() {
		return camera;
	}

	public MapClient getMap() {
		return map;
	}

	// =========================================================================================================================
	// Repaint

	private void generatesNewImage() {
		if (panel.envWidth > 0 && panel.envHeight > 0)
			panel.setImage(engine.getImage(panel.envWidth, panel.envHeight));

		updateTimeDev();
		targetUpdate();

		repaintEnvironment();
	}

	/** Refresh the image "FPSmax times" per second and when panel is resized */
	class RefreshImage implements Runnable {
		public void run() {
			// Count the number of frames displayed since the last "second timer" restart
			int fps = 0;
			// Store the time which the last second starts
			long lastSecond = System.currentTimeMillis();
			// The time where a new image would be needed
			long waitTill = 0;

			while (run) {
				// Update FPS infos
				if (System.currentTimeMillis() - lastSecond >= 1000) {
					lastSecond = System.currentTimeMillis();

					Environment3D.this.fps = fps;
					fps = 0;

					oneSecondTick();
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// If panel resized or enough time flied since previous image
				// => Start the generation of a new image
				if (!paused && !processing && (waitTill <= System.currentTimeMillis() || panel.resized)) {
					panel.resized = false;
					processing = true;

					waitTill = System.currentTimeMillis() + 1000 / FPSmax;

					Thread thread = new Thread(new GeneratesImage());
					thread.start();

					fps++;
				}
			}
		}
	}

	/** Generates a new image */
	class GeneratesImage implements Runnable {
		public void run() {
			generatesNewImage();
			processing = false;
		}
	}
}
