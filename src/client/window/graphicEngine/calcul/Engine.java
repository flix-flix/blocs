package client.window.graphicEngine.calcul;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import client.textures.TexturePack;
import client.window.graphicEngine.draws.DrawCubeFace;
import client.window.graphicEngine.models.ModelCube;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import client.window.graphicEngine.structures.Quadri;
import data.enumeration.Face;

public class Engine {

	public static final double toRadian = Math.PI / 180;

	public static TexturePack texturePack;

	/** Contains the data to be drawn */
	private Model model;
	/** The point of view and inclinations */
	private Camera camera;

	/** View angles */
	private double vx = Math.tan(60 * toRadian);
	private double vy = Math.tan(45 * toRadian);

	// ================ Target =====================
	public int cursorX = 100, cursorY = 100;

	static public ModelCube cubeTarget;
	static public Face faceTarget;

	// ================ Dev (F3) =====================
	public long timeInit = 0, timeMat = 0, timeDraw = 0, timePixel;

	// ================ Data =====================
	private Matrix matrice;

	private BufferedImage bimg;
	private DataBuffer dataBuffer;
	/** Mark the already filled pixels */
	private StatePixel[] statePixel;

	public int imgWidth = 100, imgHeight = 100;
	public int centerX, centerY;
	/**
	 * Width calculated from imgHeight to keep a good width/height ratio on drawed
	 * cubes
	 */
	public int widthRatio;

	/** if true draw a sky on the background else fill it with background Color */
	public boolean drawSky = true;
	/** Color of the backgroud if the sky isn't drawn */
	public Color background = Color.BLACK;

	// =========================================================================================================================

	public Engine(Camera camera, Model model) {
		this.camera = camera;
		this.model = model;
	}

	// =========================================================================================================================

	public BufferedImage getImage(int w, int h) {
		if (imgWidth != w || imgHeight != h)
			init(w, h);

		matrice = new Matrix(-camera.getVx(), -camera.getVy(), camera.vue);

		Arrays.fill(statePixel, StatePixel.EMPTY);

		cubeTarget = null;
		faceTarget = null;

		timeInit = System.currentTimeMillis();

		draw();

		if (drawSky)
			drawSky();
		else
			for (int row = 0; row < h; row++)
				for (int col = 0; col < w; col++)
					setPixel(row, col, background.getRGB(), StatePixel.FILL);

		return bimg;
	}

	public void init(int w, int h) {
		imgWidth = w;
		imgHeight = h;

		widthRatio = (int) (imgHeight * (1920. / 1080));

		centerX = imgWidth / 2;
		centerY = imgHeight / 2;

		bimg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		dataBuffer = bimg.getRaster().getDataBuffer();

		statePixel = new StatePixel[imgWidth * imgHeight];
	}

	// =========================================================================================================================

	public void drawSky() {
		double angleToRow = imgHeight / 120;

		int top = (int) (imgHeight / 2 + (camera.getVy() - 20) * angleToRow);

		int middle = (int) (imgHeight / 2 + (camera.getVy() + 10) * angleToRow);

		int bottom = (int) (imgHeight / 2 + (camera.getVy() + 40) * angleToRow);

		int middleColor = (-13_396_261 - (int) (top - middle) / 7 * 65_792);

		int red = ((middleColor + 16_777_216) / (256 * 256)) % 256;
		int green = ((middleColor + 16_777_216) / 256) % 256;
		int blue = (middleColor + 16_777_216) % 256;

		for (int row = 0; row < imgHeight; row++) {
			if (row < top)
				// Fill the top with blue
				for (int col = 0; col < imgWidth; col++)
					setPixel(row, col, -13_396_261, StatePixel.FILL);

			else if (row <= middle)
				// Fill the "middle top" with a blue to light blue gradient
				for (int col = 0; col < imgWidth; col++)
					setPixel(row, col, (-13_396_261 - ((int) (top - row) / 7 * 65792)), StatePixel.FILL);

			else if (row <= bottom) {
				// Fill the "middle bottom" with a light blue to black gradient
				double lala = 1 - (bottom - row) / ((double) bottom - middle);

				int dR = (int) (red * lala);
				int dG = (int) (green * lala);
				int dB = (int) (blue * lala);

				for (int col = 0; col < imgWidth; col++)
					setPixel(row, col, middleColor - (dR * 256 * 256 + dG * 256 + dB), StatePixel.FILL);
			} else
				// Fill the bottom with black
				for (int col = 0; col < imgWidth; col++)
					setPixel(row, col, -0xffffff, StatePixel.FILL);
		}
	}

	// =========================================================================================================================

	public void draw() {
		model.init(camera, matrice);

		timeMat = System.currentTimeMillis();

		ArrayList<Draw> draws = model.getDraws(camera);
		draws.sort(null);

		timeDraw = System.currentTimeMillis();

		for (Draw d : draws)
			for (Quadri q : d.getQuadri(this)) {
				// Test if at least one of the points appear on the screen
				for (int index = 0; index < 4; index++)
					if (q.points[index].x < imgWidth && q.points[index].x > 0 && q.points[index].y < imgHeight
							&& q.points[index].y > 0) {
						if (cubeTarget == null && q.statePixel == StatePixel.CONTOUR
								&& q.getPoly().contains(cursorX, cursorY) && ((DrawCubeFace) d).cube.isTargetable()) {
							faceTarget = ((DrawCubeFace) d).face;
							cubeTarget = ((DrawCubeFace) d).cube;
						}
						drawQuadri(q);
						break;
					}
			}

		timePixel = System.currentTimeMillis();
	}

	// =========================================================================================================================

	private int xInScreen(int x) {
		if (x < 0)
			return 0;
		if (x >= imgWidth)
			return imgWidth - 1;
		return x;
	}

	private int yInScreen(int y) {
		if (y < 0)
			return 0;
		if (y >= imgHeight)
			return imgHeight - 1;
		return y;
	}

	// =========================================================================================================================

	private void drawQuadri(Quadri q) {
		if (q.fill) {
			int top = yInScreen(q.getTop());
			int bottom = yInScreen(q.getBottom());
			for (int row = top; row <= bottom; row++) {
				int right = xInScreen(q.getRight(row));
				for (int col = xInScreen(q.getLeft(row)); col <= right; col++)
					setPixel(row, col, q.color, q.statePixel, q.alpha);
			}
		} else
			for (Line l : q.lines) {
				if (l.max < 0 || l.min >= imgHeight)
					return;

				int max = yInScreen(l.max);
				for (int row = yInScreen(l.min); row <= max; row++) {

					int right = xInScreen(l.getRight(row));
					for (int col = xInScreen(l.getLeft(row)); col <= right; col++)
						setPixel(row, col, q.color, q.statePixel, q.alpha);
				}
			}
	}

	// =========================================================================================================================

	private void setPixel(int row, int col, int rgb, StatePixel state) {
		setPixel(row, col, rgb, state, 0);
	}

	private void setPixel(int row, int col, int rgb, StatePixel state, int alpha) {
		// If colored transparence : generate mixed color
		if (statePixel[row * imgWidth + col] == StatePixel.TRANSPARENT)
			rgb = mix(rgb, dataBuffer.getElem(row * imgWidth + col));

		// Set color and state
		if (statePixel[row * imgWidth + col] != StatePixel.FILL
				&& statePixel[row * imgWidth + col] != StatePixel.CONTOUR) {
			dataBuffer.setElem(row * imgWidth + col, rgb);
			statePixel[row * imgWidth + col] = state;
		}
	}

	// =========================================================================================================================

	public static int mix(int a, int b) {
		a += 16_777_216;
		b += 16_777_216;

		int aR = a / (256 * 256);
		int aG = (a / 256) % 256;
		int aB = a % 256;

		int bR = b / (256 * 256);
		int bG = (b / 256) % 256;
		int bB = b % 256;

		int red = (aR + bR) / 2;
		int green = (aG + bG) / 2;
		int blue = (aB + bB) / 2;

		return -16_777_216 + red * 256 * 256 + green * 256 + blue;
	}

	// =========================================================================================================================

	public Point to2D(Point3D p) {
		if (p.x <= 0)
			return new Point(0, 0);
		double xx = p.z / (vx * p.x);
		double yy = p.y / (vy * p.x);

		int x = centerX + (int) (xx * widthRatio);
		int y = centerY + (int) (-yy * imgHeight);

		return new Point(x, y);
	}
}
