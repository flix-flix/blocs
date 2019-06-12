package client.window.graphicEngine.models;

import java.awt.Point;
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

	public static Engine engine;

	// Model
	public boolean visible = true;
	protected ArrayList<Draw> draws = new ArrayList<>();

	public boolean frontOfCamera = false;

	// True: the face won't be displayed (Set auto when adjacent bloc)
	public boolean[] hideFace = { false, false, false, false, false, false };

	// =========================================================================================================================

	// =================== Data ==================

	public Point3D[] points = new Point3D[8];
	public Point3D[] pointsTransform = new Point3D[8];
	public Point[] p2D = new Point[8];
	public Point3D depDecal;

	// =================== Basic Infos ==================

	public Point3D dep;
	public Vector vx, vy, vz;

	public Point3D ppx, ppy, ppz;

	// =================== ?? temp or not ==================

	double[] mem;
	int[] sommets;

	// =================== Infos (Draw) ==================

	// Index of the cube (in case of many cubes at the same location)
	int index;

	// =================== Rotation ==================

	// Shift for the rotation point (nb pixels)
	public int decalX, decalY, decalZ;

	// =========================================================================================================================

	public ModelCube(double x, double y, double z, int _decalX, int _decalY, int _decalZ, double _ax, double _ay,
			double _sizeX, double _sizeY, double _sizeZ, ItemID itemID) {
		super(x, y, z, _decalX, _decalY, _decalZ, _ax, _ay, _sizeX, _sizeY, _sizeZ, itemID);

		this.dep = center.clone();

		initPoints();

		recalcul();
	}

	public ModelCube(Cube c) {
		this(c.center.x, c.center.y, c.center.z, c.shiftX, c.shiftY, c.shiftZ, c.ax, c.ay, c.sizeX, c.sizeY, c.sizeZ,
				c.itemID);
		this.miningState = c.miningState;
		this.onGrid = c.onGrid;
	}

	// =========================================================================================================================

	public void initPoints() {
		for (int i = 0; i < 8; i++)
			this.p2D[i] = new Point(0, 0);

		depDecal = dep.clone();

		this.ppx = new Point3D(depDecal.x + Math.cos(ax * toRadian) * Math.cos(ay * toRadian) * sizeX,
				depDecal.y + Math.sin(ay * toRadian) * sizeX,
				depDecal.z - Math.sin(ax * toRadian) * Math.cos(ay * toRadian) * sizeX);

		this.ppy = new Point3D(depDecal.x - Math.sin(ay * toRadian) * Math.cos(ax * toRadian) * sizeY,
				depDecal.y + Math.cos(ay * toRadian) * sizeY,
				depDecal.z + Math.sin(ay * toRadian) * Math.sin(ax * toRadian) * sizeY);

		this.ppz = new Point3D(depDecal.x + Math.sin(ax * toRadian) * sizeZ, depDecal.y,
				depDecal.z + Math.cos(ax * toRadian) * sizeZ);
	}

	public void recalcul() {
		vx = new Vector(depDecal, ppx, resoX);
		vy = new Vector(depDecal, ppy, resoY);
		vz = new Vector(depDecal, ppz, resoZ);

		points[0] = vx.multiply(vy.multiply(vz.multiply(-resoZ * decalZ / resoZ), -resoY * decalY / resoY),
				-resoX * decalX / resoX);
		points[1] = vz.multiply(points[0], resoZ);
		points[2] = vx.multiply(points[0], resoX);
		points[3] = vx.multiply(points[1], resoX);

		points[4] = vy.multiply(points[0], resoY);
		points[5] = vy.multiply(points[1], resoY);
		points[6] = vy.multiply(points[2], resoY);
		points[7] = vy.multiply(points[3], resoY);
	}

	// =========================================================================================================================

	@Override
	public void init(Point3D camera, Matrix matrice) {
		center = dep.clone();
		matrice.decal(center);

		matrice.transform(this);
		to2D();

		mem = new double[] { 1000000, 1000000, 1000000 };
		sommets = new int[] { 0, 0, 0 };

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
					draws.add(new DrawCubeFace(itemID.id, Face.faces[i], vx, vy, vz, points, center, index * 10 + 6 - j,
							this));
					break;
				}

		for (Draw draw : draws) {
			draw.engine = engine;
			draw.init(camera, matrice);
		}
	}

	// =========================================================================================================================

	private void to2D() {
		for (int i = 0; i < 8; i++) {
			frontOfCamera = points[i].x > 0;
			if (!frontOfCamera)
				return;

			p2D[i].x = engine.centerX + (int) (points[i].z / engine.vue[0] * points[i].x * engine.screenWidth);
			p2D[i].y = engine.centerY + (int) (-points[i].y / (engine.vue[1] * points[i].x) * engine.screenHeight);
		}
	}

	// =========================================================================================================================

	@Override
	public boolean isVisible() {
		return visible && frontOfCamera;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public ArrayList<Draw> getDraws() {
		return draws;
	}
}
