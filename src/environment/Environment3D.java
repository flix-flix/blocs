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
	private Actu actu;
	/** Processing the next image doesn't affect the others tasks */
	private GetImg getImg;
	private Thread threadActu, threadImage;
	/** true : currently generating an image */
	public boolean processing = false;

	/** true : stop calling for new image */
	private boolean paused = false;

	// ============= Engine ===================
	protected Engine engine;
	protected MapClient map;
	protected Camera camera;

	// ============= Target ===================
	public Target target = new Target();

	// =========================================================================================================================

	public Environment3D() {
		panel = new PanEnvironment(this);

		threadActu = new Thread(actu = new Actu());
		threadImage = new Thread(getImg = new GetImg());

		threadActu.setName("Max fps counter");
		threadImage.setName("Image generator");

		engine = new Engine(null, null);
	}

	public Environment3D(MapClient map, Camera camera) {
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

	public void stop() {
		actu.stop();
		getImg.stop();
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

	public void setTarget(int x, int y) {
		engine.setTarget(x, y);
	}

	public void setTargetCenter() {
		setTarget(panel.envCenterW, panel.envCenterH);
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

	/** Repaint the panel */
	public void repaint() {
		panel.repaint();
	}

	// =========================================================================================================================

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

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

	/** Set "repaint" to true when it need a new image */
	class Actu implements Runnable {
		boolean run = true;

		public void run() {
			// Count the number of frames displayed since the last "second timer" restart
			int fps = 0;
			// The number of ms before generating another frame
			long wait = 0;
			// Store the time which the last second starts
			long time = System.currentTimeMillis();
			while (run) {
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

		public void stop() {
			run = false;
		}
	}

	/** Generates a new image if needed */
	class GetImg implements Runnable {
		boolean run = true;

		public void run() {
			while (run) {
				if (repaint) {
					repaint = false;

					if (panel.envWidth > 0 && panel.envHeight > 0)
						panel.setImage(engine.getImage(panel.envWidth, panel.envHeight));

					updateTimeDev();
					targetUpdate();

					repaint();
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				processing = false;
			}
		}

		public void stop() {
			run = false;
		}
	}
}
