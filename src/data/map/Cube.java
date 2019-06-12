package data.map;

import client.session.Session;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import data.enumeration.Face;
import data.enumeration.ItemID;
import data.enumeration.SensBloc;
import utils.FlixBlocksUtils;

public class Cube {

	public SensBloc sens = SensBloc.AUCUN;

	public Point3D center;

	public int x, y, z;
	public ItemID itemID;

	// true : pointed by the player
	public boolean isTarget;

	// Step of the bloc's "mining state"
	public int miningState = FlixBlocksUtils.NO_MINING;

	// =========================================================================================================================

	// Shift the point of rotation (pixel)
	public int shiftX, shiftY, shiftZ;

	// Rotation relative to the shifted center (degree)
	public double ax, ay;

	// Number of pixels
	public int resoX, resoY, resoZ;

	// Size of the cube
	public double sizeX, sizeY, sizeZ;

	// Like a brick in the wall
	public boolean onGrid = false;

	// =========================================================================================================================

	public Cube(double x, double y, double z, int _decalX, int _decalY, int _decalZ, double _ax, double _ay,
			double _sizeX, double _sizeY, double _sizeZ, ItemID itemID) {

		this.itemID = itemID;

		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;

		this.center = new Point3D(x, y, z);

		ax = _ax;
		ay = _ay;

		shiftX = _decalX;
		shiftY = _decalY;
		shiftZ = _decalZ;

		this.resoX = Engine.texturePack.getFace(itemID.id, Face.EAST).color[0].length;
		this.resoY = Engine.texturePack.getFace(itemID.id, Face.NORTH).color.length;
		this.resoZ = Engine.texturePack.getFace(itemID.id, Face.NORTH).color[0].length;

		sizeX = _sizeX;
		sizeY = _sizeY;
		sizeZ = _sizeZ;

		ax *= -1;
	}

	public Cube(int x, int y, int z, ItemID itemID) {
		this(x, y, z, 0, 0, 0, 0, 0, 1, 1, 1, itemID);
		onGrid = true;
	}

	// =========================================================================================================================

	public int getX() {
		return (int) center.x;
	}

	public int getY() {
		return (int) center.y;
	}

	public int getZ() {
		return (int) center.z;
	}

	// =========================================================================================================================

	public boolean hasAction() {
		return false;
	}

	public void doAction(Session session) {
		System.out.println("Do Action");
	}

	@Override
	public String toString() {
		return "Cube [x=" + x + ", y=" + y + ", z=" + z + ", itemID=" + itemID + "]";
	}
}
