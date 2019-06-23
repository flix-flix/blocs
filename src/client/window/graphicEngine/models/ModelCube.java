package client.window.graphicEngine.models;

import java.util.ArrayList;

import client.window.graphicEngine.calcul.Engine;
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

	// center cloned and changed by map rotation
	public Point3D centerDecal;
	// The 3 adjacents points of centerDecal after the map and cube rotations
	public Point3D ppx, ppy, ppz;
	// Vectors of the cube for the 3 axes (centerDecal to ppx, ppy, ppz)
	public Vector vx, vy, vz;
	// 3D Points of the cube calculed with the vectors
	// 0 (0,0,0) 1 (0,0,1) 2 (1,0,1) 3 (1,0,0) | 4,5,6,7 => y+1
	public Point3D[] points = new Point3D[8];

	// Number of pixels
	public int resoX, resoY, resoZ;

	// Index of the cube (used to sort when centers are at the same location)
	int index;
	// true: the face won't be displayed (Set auto when an adjacent bloc is added)
	public boolean[] hideFace = { false, false, false, false, false, false };

	// true : the bloc will be transparent
	private boolean preview = false;
	// true : allow the bloc to be selected
	private boolean targetable = true;
	// true : pointed by the player
	private boolean highlight;

	// =================== Model ===================
	private boolean visible = true;
	private ArrayList<Draw> draws = new ArrayList<>();

	// =========================================================================================================================

	public ModelCube(double x, double y, double z, int _decalX, int _decalY, int _decalZ, double _ax, double _ay,
			double _sizeX, double _sizeY, double _sizeZ, ItemID itemID) {
		super(x, y, z, _decalX, _decalY, _decalZ, _ax, _ay, _sizeX, _sizeY, _sizeZ, itemID);

		this.centerDecal = new Point3D(x, y, z);

		this.resoX = Engine.texturePack.getFace(itemID.id, Face.EAST).color[0].length;
		this.resoY = Engine.texturePack.getFace(itemID.id, Face.NORTH).color.length;
		this.resoZ = Engine.texturePack.getFace(itemID.id, Face.NORTH).color[0].length;

		initPoints();

		recalcul();
	}

	public ModelCube(Cube c) {
		this(c.x, c.y, c.z, c.shiftX, c.shiftY, c.shiftZ, c.ax, c.ay, c.sizeX, c.sizeY, c.sizeZ, c.itemID);
		this.miningState = c.miningState;
		this.onGrid = c.onGrid;
		this.multibloc = c.multibloc;
	}

	// =========================================================================================================================

	public void initPoints() {
		Point3D start = new Point3D(x, y, z);
		centerDecal = start.clone();

		this.ppx = new Point3D(start.x + Math.cos(ax * toRadian) * Math.cos(ay * toRadian) * sizeX,
				start.y + Math.sin(ay * toRadian) * sizeX,
				start.z - Math.sin(ax * toRadian) * Math.cos(ay * toRadian) * sizeX);

		this.ppy = new Point3D(start.x - Math.sin(ay * toRadian) * Math.cos(ax * toRadian) * sizeY,
				start.y + Math.cos(ay * toRadian) * sizeY,
				start.z + Math.sin(ay * toRadian) * Math.sin(ax * toRadian) * sizeY);

		this.ppz = new Point3D(start.x + Math.sin(ax * toRadian) * sizeZ, start.y,
				start.z + Math.cos(ax * toRadian) * sizeZ);
	}

	public void recalcul() {
		vx = new Vector(centerDecal, ppx, resoX);
		vy = new Vector(centerDecal, ppy, resoY);
		vz = new Vector(centerDecal, ppz, resoZ);

		points[0] = vx.multiply(vy.multiply(vz.multiply(-resoZ * shiftZ / resoZ), -resoY * shiftY / resoY),
				-resoX * shiftX / resoX);
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

	@Override
	public void init(Point3D camera, Matrix matrice) {
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
					if (preview)
						draws.add(new DrawCubeFace(itemID.id, Face.faces[i], vx, vy, vz, points, centerDecal,
								index * 10 + 6 - j, this, 127));
					else
						draws.add(new DrawCubeFace(itemID.id, Face.faces[i], vx, vy, vz, points, centerDecal,
								index * 10 + 6 - j, this));
					break;
				}
	}

	@Override
	public ArrayList<Draw> getDraws() {
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
