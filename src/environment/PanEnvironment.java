package environment;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import data.map.Map;
import graphicEngine.calcul.Camera;

public class PanEnvironment extends JPanel {

	private static final long serialVersionUID = -8972445552589270416L;

	Environment3D env;

	protected BufferedImage img = null;

	// =============== Size ===============
	public int width, height;
	public int centerW, centerH;

	// =============== Engine Data ===============
	public boolean showEngineData;
	private Graphics graphics;
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
		setLayout(null);

		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {

			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		this.width = width;
		this.height = height;

		centerW = width / 2;
		centerH = height / 2;
	}

	// =========================================================================================================================

	public void paintComponent(Graphics g) {
		if (img == null)
			return;

		graphics = g;

		g.drawImage(img, 0, 0, null);

		env.setProcessing(false);

		drawEngineData();
	}

	// =========================================================================================================================

	public void drawEngineData() {
		if (showEngineData) {
			left = 0;
			right = 0;
			// ======================= Camera =========================

			writeLeft(String.format("Camera: X = %.1f  Y = %.1f  Z = %.1f", camera.vue.x, camera.vue.y, camera.vue.z));
			writeLeft("Chunk: " + "X = " + Map.toChunkCoord(camera.vue.x) + " Z = " + Map.toChunkCoord(camera.vue.z));
			writeLeft("View: X = " + camera.getVx() + " Y = " + camera.getVy());
			writeLeft("Orientation: " + env.getCameraOrientation().toString());

			left++;

			// ======================= Target =========================
			String strBloc = "Bloc: ";
			String strFace = "Face: ";

			if (env.target.cube != null) {
				strBloc += env.target.cube.toString();
				strFace += env.target.face.toString();
			} else {
				strBloc += "None";
				strFace += "None";
			}

			writeLeft(strBloc);
			writeLeft(strFace);

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
		graphics.fillRect(getWidth() - fm.stringWidth(str) - margin, margin + size * right,
				fm.stringWidth(str) + 2 * margin, size);
		graphics.setColor(Color.black);
		graphics.drawString(str, getWidth() - fm.stringWidth(str), size * (right + 1));
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
}
