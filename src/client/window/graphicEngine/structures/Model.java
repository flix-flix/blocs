package client.window.graphicEngine.structures;

import java.util.ArrayList;

import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Matrix;

public interface Model {

	public abstract ArrayList<Draw> getDraws(Camera camera);

	public abstract void init(Camera camera, Matrix matrix);

	// =========================================================================================================================

	public abstract boolean isVisible();

	public abstract void setVisible(boolean visible);
}
