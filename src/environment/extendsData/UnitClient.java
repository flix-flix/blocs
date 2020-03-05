package environment.extendsData;

import data.id.ItemID;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.MultiCube;
import data.map.buildings.Building;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.resources.Resource;
import data.map.units.Unit;
import environment.extendsEngine.DrawLayer;
import server.send.Action;

public class UnitClient extends Unit {
	private static final long serialVersionUID = -5512638098833522685L;

	// ========== Rotation (Maths) ==========
	/** For each axe : the rotation to perform to complete the rotation */
	public int movingAngleX = 0, movingAngleY = 0, movingAngleZ = 0;
	/** For each axe : the current rotation of the unit */
	public double ax = 0, ay = 0, az = 0;

	private MultiCube displayedPath;
	// =========================================================================================================================

	public UnitClient(Unit unit) {
		super(unit);
	}

	// =========================================================================================================================

	public void hidePath(MapClient map) {
		if (displayedPath == null || displayedPath.list.isEmpty())
			return;
		map.remove(displayedPath.getCube());
	}

	public void showPath(MapClient map) {
		if (path == null)
			return;

		displayedPath = new MultiCube();

		for (Coord coord : path) {
			CubeClient cube = new CubeClient(new Cube(coord, ItemID.INVISIBLE));
			for (Face face : Face.values()) {
				DrawLayer layer = new DrawLayer(cube, face);
				layer.drawPathEnd();
				cube.addLayer(layer);
			}
			displayedPath.add(cube);
		}

		if (!displayedPath.list.isEmpty())
			map.addPreview(displayedPath.getCube());
	}

	// =========================================================================================================================

	/**
	 * Increase the rotation by one step and re-calculate the corresponding angles
	 */
	@Override
	public boolean doStep(Map map) {
		if (!super.doStep(map))
			return false;

		ax = (movingStep / (double) movingStepFinal) * movingAngleX;
		ay = -(movingStep / (double) movingStepFinal) * movingAngleY;
		az = (movingStep / (double) movingStepFinal) * movingAngleZ;

		return true;
	}

	// =========================================================================================================================

	@Override
	public void doBuild(Map map, Building build) {
		if (map.gridGet(actionCube).build.addAlreadyBuild(1)) // Building created
			action = null;
	}

	public void _doHarvest(Map map) {
		super.doHarvest(map);

		if (resource == null)
			resource = new Resource(map.gridGet(actionCube).getResource().getType(), 0, maxCapacity);

		if (resource.isFull())
			action = null;// TODO Go Drop
		else {
			resource.add(1);

			// Cube break
			map.remove(actionCube);
			action = null;// TODO Go store
		}
	}

	public void _doStore(Map map, Building build) {
		super.doStore(map, build);

		if (build.addToStock(resource, 1))// Stock full
			action = null;// TODO Except if there is another empty stock around
		else if (resource == null || resource.isEmpty()) {// TODO Go Harvest
			removeResource();
			action = null;
		}
	}

	// =========================================================================================================================

	/** Update cube position after the rotation */
	public void arrive(Map map) {
		map.gridRemove(coord);

		ax = 0;
		ay = 0;
		az = 0;

		movingAngleX = 0;
		movingAngleY = 0;
		movingAngleZ = 0;

		super.arrive();
		map.gridAdd(this);

		if (displayedPath != null && !displayedPath.list.isEmpty()) {
			Cube cube = displayedPath.list.pollFirst();
			cube.multicube = null;
			map.remove(cube);
		}
	}

	@Override
	public void doNextMove() {
		super.doNextMove();
	}

	// =========================================================================================================================

	/** Initialize a rotation to an adjacent position */
	@Override
	public void rollAdjacent(Orientation dir) {
		super.rollAdjacent(dir);

		switch (dir) {
		case NORTH:
			movingAngleZ = -90;
			break;
		case EAST:
			movingAngleX = 90;
			break;
		case SOUTH:
			movingAngleZ = 90;
			break;
		case WEST:
			movingAngleX = -90;
			break;
		}
	}

	/** Initialize a rotation to a diagonal position */
	@Override
	public void rollDiago(int x) {
		super.rollDiago(x);
		switch (x) {
		case 0:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				movingAngleX = -90;
				movingAngleY = 90;
			} else {
				movingAngleY = -90;
				movingAngleZ = 90;
			}
			break;
		case 1:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				movingAngleX = 90;
				movingAngleY = -90;
			} else {
				movingAngleY = 90;
				movingAngleZ = 90;
			}
			break;
		case 2:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				movingAngleX = 90;
				movingAngleY = 90;
			} else {
				movingAngleY = -90;
				movingAngleZ = -90;
			}
			break;
		case 3:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				movingAngleX = -90;
				movingAngleY = -90;
			} else {
				movingAngleY = 90;
				movingAngleZ = -90;
			}
			break;
		}
	}

	/** Initialize a rotation to the upper position */
	@Override
	public void rollUp(Orientation dir) {
		super.rollUp(dir);

		switch (dir) {
		case NORTH:
			movingAngleZ = -90;
			break;
		case EAST:
			movingAngleX = 90;
			break;
		case SOUTH:
			movingAngleZ = 90;
			break;
		case WEST:
			movingAngleX = -90;
			break;
		}
	}

	/** Initialize a rotation to the position below */
	@Override
	public void rollDown(Orientation dir) {
		super.rollDown(dir);

		switch (dir) {
		case NORTH:
			movingAngleZ = -90;
			break;
		case EAST:
			movingAngleX = 90;
			break;
		case SOUTH:
			movingAngleZ = 90;
			break;
		case WEST:
			movingAngleX = -90;
			break;
		}
	}

	/** Initialize an on-place rotation to change the unit's rotation axe */
	@Override
	public void rotation() {
		super.rotation();

		if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
			movingAngleX = 90;
			movingAngleY = 90;
		} else {
			movingAngleY = -90;
			movingAngleZ = 90;
		}
	}

	// =========================================================================================================================

	@Override
	public boolean doAction(Action action, Map map, Coord coord) {
		return super.doAction(action, map, coord);
	}
}
