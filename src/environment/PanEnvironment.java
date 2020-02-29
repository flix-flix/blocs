package environment;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import data.map.Map;
import data.map.enumerations.Orientation;
import graphicEngine.calcul.Camera;

public class PanEnvironment extends JPanel {
	private static final long serialVersionUID = -8972445552589270416L;

	protected Environment3D env;

	// =============== Repaint ===============
	protected Graphics graphics;
	protected BufferedImage img = null;

	// =============== Size ===============
	/** Size of the display of environment */
	public int envWidth, envHeight;

	/** true: the panel have been resized and need a new image to be generated */
	public boolean resized = false;

	// =============== Display Engine Infos ===============
	public boolean showEngineInfos;
	private Camera camera;

	// =============== Engine Data Display ===============
	private Font fontEngine = new Font("monospace", Font.PLAIN, 12);
	private FontMetrics fm = getFontMetrics(fontEngine);
	/** Space between text and background border (pixels) */
	static private final int margin = 5;
	/** Height of the text-backgrounds */
	static private final int size = 20;
	/** Number of strings already drawn (left/right) */
	private int right = 0, left = 0;

	// =========================================================================================================================

	public PanEnvironment(Environment3D env) {
		this.env = env;
		this.setLayout(null);
	}

	// =========================================================================================================================

	protected void paintComponent(Graphics g) {
		if (img == null)
			return;

		graphics = g;

		graphics.drawImage(img, 0, 0, null);
		drawEngineData();
	}

	// =========================================================================================================================

	protected void updateEnvironmentSize(int width, int height) {
		// Ignore if same size
		if (width == envWidth && height == envHeight)
			return;

		this.envWidth = width;
		this.envHeight = height;

		resized = true;
	}

	// =========================================================================================================================

	public void drawEngineData() {
		if (showEngineInfos) {
			left = 0;
			right = 0;
			// ======================= Camera =========================

			writeLeft(String.format("Camera: X = %.1f  Y = %.1f  Z = %.1f", camera.vue.x, camera.vue.y, camera.vue.z));
			writeLeft("Chunk: " + "X = " + Map.toChunkCoord(camera.vue.x) + " Z = " + Map.toChunkCoord(camera.vue.z));
			writeLeft("View: X = " + camera.getVx() + " Y = " + camera.getVy());
			writeLeft("Orientation: " + Orientation.getOrientation(env.getCamera().getVx()).toString());

			left++;

			// ======================= Target =========================
			String strBloc = "Bloc: ";
			String strFace = "Face: ";
			String strQuadri = "Quadri: ";

			if (env.target.cube != null) {
				strBloc += env.target.cube.toString();
				strFace += env.target.face.toString();
				strQuadri += env.target.quadri;
			} else {
				strBloc += "None";
				strFace += "None";
				strQuadri += "None";
			}

			writeLeft(strBloc);
			writeLeft(strFace);
			if (env.isNeededQuadriPrecision())
				writeLeft(strQuadri);

			// ======================= Engine =========================

			writeRight("Chunks: " + env.nbChunks + " Faces: " + env.nbFaces);
			right++;
			writeRight("timeMat: " + env.timeMat + " timeDraw: " + env.timeDraw + " timePixel: " + env.timeQuadri);
			right++;
			writeRight("FPS: " + env.fps);
			writeRight("Ticks (phys): " + env.ticksPhys);
			writeRight("Ticks (key): " + env.ticksKeyBoard);
		}
	}

	// =========================================================================================================================

	/**
	 * Write the string on the top left corner bellow the previous strings
	 * 
	 * @param str
	 */
	public void writeLeft(String str) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, margin + size * left, fm.stringWidth(str) + 2 * margin, size);
		graphics.setColor(Color.black);
		graphics.drawString(str, margin, size * (left + 1));
		left++;
	}

	/**
	 * Write the string on the top right corner bellow the previous strings
	 * 
	 * @param str
	 */
	public void writeRight(String str) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(envWidth - fm.stringWidth(str) - margin, margin + size * right,
				fm.stringWidth(str) + 2 * margin, size);
		graphics.setColor(Color.black);
		graphics.drawString(str, envWidth - fm.stringWidth(str), size * (right + 1));
		right++;
	}

	// =========================================================================================================================

	public void setImage(BufferedImage img) {
		this.img = img;
	}

	public boolean hasImage() {
		return img != null;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		updateEnvironmentSize(width, height);
	}
}
