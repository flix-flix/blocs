package client.window.graphicEngine.calcul;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import client.textures.TexturePack;
import client.window.graphicEngine.extended.DrawCubeFace;
import client.window.graphicEngine.extended.ModelCube;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import client.window.graphicEngine.structures.Quadri;
import data.enumeration.Face;

public class Engine {

	public static final double toRadian = Math.PI / 180;

	public TexturePack texturePack;

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

	/** true : draw a sky on the background | false : it will be transparent */
	public boolean drawSky = true;

	// =========================================================================================================================

	public Engine(Camera camera, Model model, TexturePack texturePack) {
		this.camera = camera;
		this.model = model;
		this.texturePack = texturePack;
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
					setPixel(row, col, -13_396_261, StatePixel.FILL, 255);

			else if (row <= middle)
				// Fill the "middle top" with a blue to light blue gradient
				for (int col = 0; col < imgWidth; col++)
					setPixel(row, col, (-13_396_261 - ((int) (top - row) / 7 * 65792)), StatePixel.FILL, 255);

			else if (row <= bottom) {
				// Fill the "middle bottom" with a light blue to black gradient
				double lala = 1 - (bottom - row) / ((double) bottom - middle);

				int dR = (int) (red * lala);
				int dG = (int) (green * lala);
				int dB = (int) (blue * lala);

				for (int col = 0; col < imgWidth; col++)
					setPixel(row, col, middleColor - (dR * 256 * 256 + dG * 256 + dB), StatePixel.FILL, 255);
			} else
				// Fill the bottom with black
				for (int col = 0; col < imgWidth; col++)
					setPixel(row, col, -0xffffff, StatePixel.FILL, 255);
		}
	}

	// =========================================================================================================================

	public void draw() {
		model.init(camera, matrice);

		timeMat = System.currentTimeMillis();

		ArrayList<Draw> draws = model.getDraws(camera);
		draws.sort(null);

		timeDraw = System.currentTimeMillis();

		for (Draw d : draws) {
			Polygon poly = d.getPoly(this);
			// Test if at least one of the points appear on the screen
			for (int index = 0; index < 4; index++)
				if (poly.xpoints[index] < imgWidth && poly.xpoints[index] > 0 && poly.ypoints[index] < imgHeight
						&& poly.ypoints[index] > 0) {
					if (cubeTarget == null && poly.contains(cursorX, cursorY)
							&& ((DrawCubeFace) d).cube.isTargetable()) {
						faceTarget = ((DrawCubeFace) d).face;
						cubeTarget = ((DrawCubeFace) d).cube;
					}
				}
			for (Quadri q : d.getQuadri(this))
				// Test if at least one of the points appear on the screen
				for (int index = 0; index < 4; index++)
					if (q.points[index].x < imgWidth && q.points[index].x > 0 && q.points[index].y < imgHeight
							&& q.points[index].y > 0) {
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
					setPixel(row, col, q.color, q.state, q.alpha);
			}
		} else
			for (Line l : q.lines) {
				if (l.max < 0 || l.min >= imgHeight)
					return;

				int max = yInScreen(l.max);
				for (int row = yInScreen(l.min); row <= max; row++) {

					int right = xInScreen(l.getRight(row));
					for (int col = xInScreen(l.getLeft(row)); col <= right; col++)
						setPixel(row, col, q.color, q.state, q.alpha);
				}
			}
	}

	// =========================================================================================================================

	private void setPixel(int row, int col, int rgb, StatePixel state, int alpha) {
		// If colored transparence : generate mixed color
		if (statePixel[row * imgWidth + col] == StatePixel.TRANSPARENT)
			rgb = mix(rgb, dataBuffer.getElem(row * imgWidth + col));

		// Set color and state
		if (statePixel[row * imgWidth + col] != StatePixel.FILL) {
			dataBuffer.setElem(row * imgWidth + col, (alpha << 24) + rgb);
			statePixel[row * imgWidth + col] = state;
		}
	}

	// =========================================================================================================================

	/**
	 * Returns the mixed color from the two given colors
	 * 
	 * @param a
	 *            - first color {@link BufferedImage#TYPE_INT_RGB}
	 * @param b
	 *            - second color {@link BufferedImage#TYPE_INT_RGB}
	 * @return mixed color {@link BufferedImage#TYPE_INT_RGB}
	 */
	public static int mix(int a, int b) {
		int aR = (a >> 16) & 0xff;
		int aG = (a >> 8) & 0xff;
		int aB = a & 0xff;

		int bR = (b >> 16) & 0xff;
		int bG = (b >> 8) & 0xff;
		int bB = b & 0xff;

		int red = (aR + bR) / 2;
		int green = (aG + bG) / 2;
		int blue = (aB + bB) / 2;

		return ((((red) << 8) + green) << 8) + blue;
	}

	public static int lighter(int color, int shade) {
		color += 16_777_216;

		int red = (color / (256 * 256)) % 256;
		int green = (color / 256) % 256;
		int blue = color % 256;

		red = Math.min(255, red + shade);
		green = Math.min(255, green + shade);
		blue = Math.min(255, blue + shade);

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
