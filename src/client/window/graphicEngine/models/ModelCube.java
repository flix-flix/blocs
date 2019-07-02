package client.window.graphicEngine.models;

import java.util.ArrayList;

import client.textures.TexturePack;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.calcul.Vector;
import client.window.graphicEngine.draws.DrawCubeFace;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import data.enumeration.Face;
import data.enumeration.ItemID;
import data.map.Cube;

public class ModelCube extends Cube implements Model {

	private static final double toRadian = Math.PI / 180;

	public static TexturePack texturePack;

	/** center after map rotation */
	public Point3D centerDecal;
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

	/** Cube resolution (pixels) */
	public int resoX, resoY, resoZ;

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

	// =================== Model ===================
	private boolean visible = true;
	private ArrayList<Draw> draws = new ArrayList<>();

	// =========================================================================================================================

	public ModelCube(double x, double y, double z, int shiftX, int shiftY, int shiftZ, double rotaX, double rotaY,
			double rotaZ, double sizeX, double sizeY, double sizeZ, ItemID itemID) {
		super(x, y, z, shiftX, shiftY, shiftZ, rotaX, rotaY, rotaZ, sizeX, sizeY, sizeZ, itemID);

		this.centerDecal = new Point3D(x, y, z);

		this.resoX = texturePack.getFace(itemID.id, Face.EAST).width;
		this.resoY = texturePack.getFace(itemID.id, Face.NORTH).height;
		this.resoZ = texturePack.getFace(itemID.id, Face.NORTH).width;
	}

	public ModelCube(Cube c) {
		this(c.x, c.y, c.z, c.shiftX, c.shiftY, c.shiftZ, c.rotaX, c.rotaY, c.rotaZ, c.sizeX, c.sizeY, c.sizeZ,
				c.itemID);

		this.rotation = c.rotation;
		this.orientation = c.orientation;

		this.miningState = c.miningState;
		this.onGrid = c.onGrid;
		this.multibloc = c.multibloc;
		this.unit = c.unit;
	}

	// =========================================================================================================================

	public void initPoints() {
		centerDecal = new Point3D(x + (shiftX / (double) resoX), y + (shiftY / (double) resoY),
				z + (shiftZ / (double) resoZ));

		this.ppx = new Point3D(centerDecal.x + Math.cos(rotaY * toRadian) * Math.cos(rotaZ * toRadian) * sizeX,
				centerDecal.y + Math.sin(rotaZ * toRadian) * Math.cos(rotaX * toRadian) * sizeX,
				centerDecal.z + (Math.sin(rotaY * toRadian) * Math.cos(rotaZ * toRadian)
						+ Math.sin(rotaZ * toRadian) * Math.sin(rotaX * toRadian)) * sizeX);

		this.ppy = new Point3D(
				centerDecal.x - ((Math.sin(rotaZ * toRadian) * Math.cos(rotaY * toRadian))
						+ Math.sin(rotaX * toRadian) * Math.sin(rotaY * toRadian) * Math.cos(rotaZ * toRadian)) * sizeY,
				centerDecal.y + Math.cos(rotaZ * toRadian) * Math.cos(rotaX * toRadian) * sizeY,
				centerDecal.z + ((-Math.sin(rotaZ * toRadian) * Math.sin(rotaY * toRadian))
						+ Math.sin(rotaX * toRadian) * Math.cos(rotaY * toRadian) * Math.cos(rotaZ * toRadian))
						* sizeY);

		this.ppz = new Point3D(centerDecal.x + (-Math.sin(rotaY * toRadian) * Math.cos(rotaX * toRadian)) * sizeZ,
				centerDecal.y - Math.sin(rotaX * toRadian) * sizeZ,
				centerDecal.z + Math.cos(rotaY * toRadian) * Math.cos(rotaX * toRadian) * sizeZ);
	}

	public void recalcul() {
		vx = new Vector(centerDecal, ppx, resoX);
		vy = new Vector(centerDecal, ppy, resoY);
		vz = new Vector(centerDecal, ppz, resoZ);

		points[0] = vx.multiply(vy.multiply(vz.multiply(-shiftZ), -shiftY), -shiftX);
		points[1] = vz.multiply(points[0], resoZ);
		points[2] = vx.multiply(points[0], resoX);
		points[3] = vx.multiply(points[1], resoX);

		points[4] = vy.multiply(points[0], resoY);
		points[5] = vy.multiply(points[1], resoY);
		points[6] = vy.multiply(points[2], resoY);
		points[7] = vy.multiply(points[3], resoY);
	}

	// =========================================================================================================================

	public void setPreview(boolean b) {
		preview = b;
		if (multibloc != null)
			for (Cube c : multibloc.list) {
				((ModelCube) c).preview = b;
			}
	}

	public void setTargetable(boolean b) {
		targetable = b;
		if (multibloc != null)
			for (Cube c : multibloc.list)
				((ModelCube) c).targetable = b;
	}

	public void setHighlight(boolean b) {
		highlight = b;
		if (multibloc != null)
			for (Cube c : multibloc.list) {
				((ModelCube) c).highlight = b;
			}
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

	public void updateFromUnit() {
		if (unit == null)
			return;

		rotaX = unit.ax;
		rotaY = unit.ay;
		rotaZ = unit.az;

		x = unit.coord.x;
		y = unit.coord.y;
		z = unit.coord.z;

		gridCoord = unit.coord;

		rotation = unit.rotation;
		orientation = unit.orientation;

		switch (unit.rotationPoint) {
		case 0:
			shiftX = 0;
			shiftZ = 0;
			break;
		case 1:
			shiftX = 0;
			shiftZ = resoZ;
			break;
		case 2:
			shiftX = resoX;
			shiftZ = resoZ;
			break;
		case 3:
			shiftX = resoX;
			shiftZ = 0;
			break;
		case 4:// center
			shiftX = resoX / 2;
			shiftZ = resoZ / 2;
			break;
		}
	}

	// =========================================================================================================================

	@Override
	public void init(Camera camera, Matrix matrice) {
		updateFromUnit();

		matrice.transform(this);

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

		for (int j = 6; j >= 4; j--)
			for (int i = 0; i < 6; i++)
				if (faces[i] == j && !hideFace[i]) {
					draws.add(new DrawCubeFace(this, Face.faces[i], centerDecal, index * 10 + 6 - j,
							preview ? 127 : DrawCubeFace.DEFAULT_ALPHA));
					break;
				}
	}

	@Override
	public ArrayList<Draw> getDraws(Camera camera) {
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
