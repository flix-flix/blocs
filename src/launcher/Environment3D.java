package launcher;

import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.extended.ModelMap;
import client.window.panels.PanEnvironment;

public class Environment3D {

	private PanEnvironment panel;

	// =============== F3 (Dev infos) ===============
	/** Number of frames displayed the last second */
	public int fps;
	/** true : start the generation of a new image then turn to false */
	private boolean repaint = false;

	/** Chronometric marks */
	public long timeMat, timeDraw, timeQuadri;
	/** Number of cubes and chunks displayed */
	public int nbChunks, nbFaces;

	public int ticksPhys, ticksKeyBoard;

	// ============ Options ============
	/** Max frames/seconde allowed */
	public int FPSmax = 30;

	// =============== Thread ===============
	/** Refresh the image */
	private Thread threadActu;
	/** Processing the next image doesn't affect the others tasks */
	private Thread threadImage;
	/** true : currently generating an image */
	public boolean processing = false;

	/** true : stop calling for new image */
	private boolean paused = false;

	// ============= Engine ===================
	protected Engine engine;
	protected ModelMap map;
	protected Camera camera;

	// ============= Target ===================
	public Target target;

	// =========================================================================================================================

	public Environment3D() {
		panel = new PanEnvironment(this);

		threadActu = new Thread(new Actu());
		threadImage = new Thread(new GetImg());

		threadActu.setName("Max fps counter");
		threadImage.setName("Image generator");

		engine = new Engine(null, null);
	}

	public Environment3D(ModelMap map, Camera camera) {
		this();
		this.map = map;
		this.camera = camera;
	}

	// =========================================================================================================================

	public void start() {
		panel.setCamera(camera);

		engine.setModelCamera(map, camera);

		threadActu.start();
		threadImage.start();
	}

	// =========================================================================================================================

	public void setEngineBackground(int x) {
		engine.setBackground(x);
	}
	// =========================================================================================================================

	public void setTarget(int x, int y) {
		engine.setTarget(x, y);
	}

	public void setTargetCenter() {
		setTarget(panel.centerW, panel.centerH);
	}

	// =========================================================================================================================

	public void targetUpdate() {
		Target target = new Target(engine);

		boolean sameTarget = target.equals(this.target, isNeededQuadriPrecision());

		// If different target
		if (!sameTarget) {
			// If it replace an existant one => Update
			if (this.target != null && this.target.isValid())
				looseTarget();

			// If target isn't void => Update
			if (target.isValid())
				gainTarget(target);
		}

		this.target = target;
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
	public void gainTarget(Target target) {
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

	// =========================================================================================================================

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	public PanEnvironment getPanel() {
		return panel;
	}

	public void switchDevlopMode() {
		panel.showEngineData = !panel.showEngineData;
	}

	public Camera getCamera() {
		return camera;
	}

	public ModelMap getMap() {
		return map;
	}

	// =========================================================================================================================

	/** Set "repaint" to true when it need a new image */
	class Actu implements Runnable {
		public void run() {
			// Count the number of frames displayed since the last "second timer" restart
			int fps = 0;
			// The number of ms before generating another frame
			long wait = 0;
			// Store the time which the last second starts
			long time = System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() - time >= 1000) {// Update FPS infos
					time = System.currentTimeMillis();

					Environment3D.this.fps = fps;
					fps = 0;

					oneSecondTick();
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (!paused && !processing && wait <= System.currentTimeMillis()) {
					processing = true;
					wait = System.currentTimeMillis() + 1000 / FPSmax;

					repaint = true;
					fps++;
				}
			}
		}
	}

	/** Generates a new image if needed */
	class GetImg implements Runnable {
		public void run() {
			while (true) {
				if (repaint) {
					repaint = false;

					if (panel.width > 0 && panel.height > 0)
						panel.setImage(engine.getImage(panel.width, panel.height));

					updateTimeDev();
					targetUpdate();

					panel.repaint();
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				processing = false;
			}
		}
	}
}
