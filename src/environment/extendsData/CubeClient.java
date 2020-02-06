
package environment.extendsData;

import java.util.ArrayList;

import data.id.ItemID;
import data.map.Cube;
import data.map.enumerations.Face;
import environment.extendsEngine.DrawCubeFace;
import environment.extendsEngine.DrawLayer;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Matrix;
import graphicEngine.calcul.Point3D;
import graphicEngine.calcul.Vector;
import graphicEngine.structures.Drawable;
import graphicEngine.structures.Modelisable;

public class CubeClient extends Cube implements Modelisable {
	private static final long serialVersionUID = -780214253477663150L;

	private static final double toRadian = Math.PI / 180;

	/** center after map rotation */
	public Point3D centerDecal;
	/** The cube will be generate with vectors from this point */
	public Point3D generationPoint;
	/** The 3 adjacents points of centerDecal after the map and cube rotations */
	public Point3D ppx, ppy, ppz;
	/** Vectors of the cube for the 3 axes (centerDecal to ppx, ppy, ppz) */
	public Vector vx, vy, vz;
	/**
	 * 3D Points of the cube calculed with the vectors
	 * 
	 * 0 (0,0,0) 1 (0,0,1) 2 (1,0,1) 3 (1,0,0) | 4,5,6,7 => y+1
	 */
	public Point3D[] points = new Point3D[8];

	/** Index of the cube (used to sort when centers are at the same location) */
	int index;
	/**
	 * true: the face won't be displayed (Handled by ModelMap when an adjacent cube
	 * is added)
	 */
	public boolean[] hideFace = { false, false, false, false, false, false };

	/** true : the cube will be transparent */
	private boolean preview = false;
	/** true : allow the cube to be selected */
	private boolean targetable = true;
	/** true : pointed by the player */
	private boolean highlight;

	// =================== Layer ===================
	private ArrayList<DrawLayer> layers;

	// =================== Quadri ===================
	public Face selectedFace = null;
	public static final int NO_QUADRI = -1;
	public int selectedQuadri = NO_QUADRI;

	// =================== Model ===================
	/**
	 * false: the cube won't be displayed<br>
	 * (Will be set to false only if the 6 adjacent blocs are opaque)
	 */
	private boolean visible = true;
	private ArrayList<Drawable> draws = new ArrayList<>();

	// =========================================================================================================================

	public CubeClient(double x, double y, double z, double rotaX, double rotaY, double rotaZ, double sizeX,
			double sizeY, double sizeZ, int itemID) {
		super(x, y, z, rotaX, rotaY, rotaZ, sizeX, sizeY, sizeZ, itemID);

		centerDecal = new Point3D(x, y, z);
	}

	public CubeClient(Cube c) {
		this(c.x, c.y, c.z, c.rotaX, c.rotaY, c.rotaZ, c.sizeX, c.sizeY, c.sizeZ, c.getItemID());

		this.shiftX = c.shiftX;
		this.shiftY = c.shiftY;
		this.shiftZ = c.shiftZ;

		this.rotation = c.rotation;
		this.orientation = c.orientation;

		this.miningState = c.miningState;
		this.onGrid = c.onGrid;

		this.multibloc = c.multibloc;
		this.unit = c.unit == null ? null : (c.unit instanceof UnitClient ? c.unit : new UnitClient(c.unit));
		this.build = c.build;
		this.resource = c.resource;

		this.multiblocX = c.multiblocX;
		this.multiblocY = c.multiblocY;
		this.multiblocZ = c.multiblocZ;
	}

	// =========================================================================================================================

	public void initPoints() {
		generationPoint = new Point3D(x + shiftX * sizeX, y + shiftY * sizeY, z + shiftZ * sizeZ);
		centerDecal = new Point3D(x, y, z);

		this.ppx = new Point3D(generationPoint.x + Math.cos(rotaY * toRadian) * Math.cos(rotaZ * toRadian) * sizeX,
				generationPoint.y + Math.sin(rotaZ * toRadian) * Math.cos(rotaX * toRadian) * sizeX,
				generationPoint.z + (Math.sin(rotaY * toRadian) * Math.cos(rotaZ * toRadian)
						+ Math.sin(rotaZ * toRadian) * Math.sin(rotaX * toRadian)) * sizeX);

		this.ppy = new Point3D(
				generationPoint.x - ((Math.sin(rotaZ * toRadian) * Math.cos(rotaY * toRadian))
						+ Math.sin(rotaX * toRadian) * Math.sin(rotaY * toRadian) * Math.cos(rotaZ * toRadian)) * sizeY,
				generationPoint.y + Math.cos(rotaZ * toRadian) * Math.cos(rotaX * toRadian) * sizeY,
				generationPoint.z + ((-Math.sin(rotaZ * toRadian) * Math.sin(rotaY * toRadian))
						+ Math.sin(rotaX * toRadian) * Math.cos(rotaY * toRadian) * Math.cos(rotaZ * toRadian))
						* sizeY);

		this.ppz = new Point3D(generationPoint.x + (-Math.sin(rotaY * toRadian) * Math.cos(rotaX * toRadian)) * sizeZ,
				generationPoint.y - Math.sin(rotaX * toRadian) * sizeZ,
				generationPoint.z + Math.cos(rotaY * toRadian) * Math.cos(rotaX * toRadian) * sizeZ);
	}

	public void recalcul() {
		vx = new Vector(generationPoint, ppx);
		vy = new Vector(generationPoint, ppy);
		vz = new Vector(generationPoint, ppz);

		points[0] = vx.multiply(vy.multiply(vz.multiply(-shiftZ), -shiftY), -shiftX);
		points[1] = vz.multiply(points[0], 1);
		points[2] = vx.multiply(points[0], 1);
		points[3] = vx.multiply(points[1], 1);

		points[4] = vy.multiply(points[0], 1);
		points[5] = vy.multiply(points[1], 1);
		points[6] = vy.multiply(points[2], 1);
		points[7] = vy.multiply(points[3], 1);
	}

	// =========================================================================================================================

	public void setPreview(boolean b) {
		preview = b;
		if (multibloc != null)
			for (Cube c : multibloc.list) {
				((CubeClient) c).preview = b;
			}
	}

	public void setTargetable(boolean b) {
		targetable = b;
		if (multibloc != null)
			for (Cube c : multibloc.list)
				((CubeClient) c).targetable = b;
	}

	public void setHighlight(boolean b) {
		highlight = b;
		if (multibloc != null) {
			for (Cube c : multibloc.list) {
				((CubeClient) c).highlight = b;
			}
		}
	}

	// =========================================================================================================================

	public void removeLayer(int i) {
		if (layers != null && i < layers.size())
			layers.set(i, null);
	}

	// =========================================================================================================================

	public boolean isPreview() {
		return preview;
	}

	public boolean isTargetable() {
		return targetable;
	}

	public boolean isHighlight() {
		return highlight;
	}

	// =========================================================================================================================

	public void setSelectedQuadri(Face face, int id) {
		selectedFace = face;
		selectedQuadri = id;
	}

	// =========================================================================================================================

	public void updateFromUnit() {
		rotaX = ((UnitClient) unit).ax;
		rotaY = ((UnitClient) unit).ay;
		rotaZ = ((UnitClient) unit).az;

		x = unit.coord.x;
		y = unit.coord.y;
		z = unit.coord.z;

		gridCoord = unit.coord;

		rotation = unit.rotation;
		orientation = unit.orientation;

		shiftX = 0;
		shiftY = 0;
		shiftZ = 0;

		switch (unit.rotationPoint) {
		case 0:
			break;
		case 1:
			shiftZ = 1;
			break;
		case 2:
			shiftX = 1;
			shiftZ = 1;
			break;
		case 3:
			shiftX = 1;
			break;
		case 4:
			shiftY = 1;
			break;
		case 5:
			shiftY = 1;
			shiftZ = 1;
			break;
		case 6:
			shiftX = 1;
			shiftY = 1;
			shiftZ = 1;
			break;
		case 7:
			shiftX = 1;
			shiftY = 1;
			break;
		}
	}

	// =========================================================================================================================
	// Layers

	public boolean hasLayer() {
		return layers != null;
	}

	public ArrayList<DrawLayer> getLayers() {
		return layers;
	}

	public void addLayer(DrawLayer layer) {
		if (layers == null)
			layers = new ArrayList<>();
		layers.add(layer);
	}

	public void setLayer(int index, DrawLayer layer) {
		layers.set(index, layer);
	}

	// =========================================================================================================================
	// Modelisable

	@Override
	public void init(Camera camera, Matrix matrix) {
		if (unit != null)
			updateFromUnit();

		transform(matrix);

		double[] mem = new double[] { 1000000, 1000000, 1000000 };
		int[] sommets = new int[] { 0, 0, 0 };

		for (int i = 0; i < 8; i++) {
			double d = points[i].distToOrigin();
			for (int j = 0; j < 3; j++)
				if (d < mem[j]) {
					for (int jj = 1; jj >= j; jj--) {
						mem[jj + 1] = mem[jj];
						sommets[jj + 1] = sommets[jj];
					}
					mem[j] = d;
					sommets[j] = i;
					break;
				}
		}

		int lala = 3;

		int[] faces = new int[6];
		for (int s : sommets) {
			switch (s) {
			case 0:
				faces[1] += lala;
				faces[3] += lala;
				faces[5] += lala;
				break;
			case 1:
				faces[1] += lala;
				faces[3] += lala;
				faces[4] += lala;
				break;
			case 2:
				faces[1] += lala;
				faces[2] += lala;
				faces[5] += lala;
				break;
			case 3:
				faces[1] += lala;
				faces[2] += lala;
				faces[4] += lala;
				break;
			case 4:
				faces[0] += lala;
				faces[3] += lala;
				faces[5] += lala;
				break;
			case 5:
				faces[0] += lala;
				faces[3] += lala;
				faces[4] += lala;
				break;
			case 6:
				faces[0] += lala;
				faces[2] += lala;
				faces[5] += lala;
				break;
			case 7:
				faces[0] += lala;
				faces[2] += lala;
				faces[4] += lala;
				break;
			}
			lala--;
		}
		draws.clear();

		// Only draw the 3 visible faces of the cube (except exception)
		// 2 is for 3 faces (faces[] values: 0,1,2,X,4,5,6)
		drawFaces(drawAllFaces() ? 6 : 2, faces);
	}

	private boolean drawAllFaces() {
		// If it's an unit the cube can be rolling
		if (unit != null)
			return true;
		//
		if (getItemID() == ItemID.EDITOR_PREVIEW)
			return true;
		return false;
	}

	private void drawFaces(int nb, int[] faces) {
		for (int j = 6; j >= 6 - nb; j--)
			// Draw the faces from the closest to the farthest
			for (int i = 0; i < 6; i++)
				if (faces[i] == j)
					// Ignore the face hidden by a bloc (except for unit cause they can be rolling)
					if (!hideFace[i] || unit != null) {
						draws.add(new DrawCubeFace(this, Face.faces[i], centerDecal, index + 10 + 6 - j));
						break;
					}
	}

	public void transform(Matrix matrix) {
		initPoints();

		matrix.transform(generationPoint);
		matrix.transform(centerDecal);
		matrix.transform(ppx);
		matrix.transform(ppy);
		matrix.transform(ppz);
		recalcul();
	}

	@Override
	public ArrayList<Drawable> getDraws(Camera camera) {
		return draws;
	}

	// =========================================================================================================================

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
