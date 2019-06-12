package client.window.graphicEngine.structures;

import java.util.ArrayList;

import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.calcul.Point3D;

public interface Model {

	public abstract ArrayList<Draw> getDraws();

	public abstract void init(Point3D camera, Matrix matrice);

	// =========================================================================================================================

	public abstract boolean isVisible();

	public abstract void setVisible(boolean visible);
}
