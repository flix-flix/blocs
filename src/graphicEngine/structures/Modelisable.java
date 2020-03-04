package graphicEngine.structures;

import java.util.ArrayList;

import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Matrix;

public interface Modelisable {

	public abstract ArrayList<Drawable> getDraws(Camera camera);

	public abstract void init(Camera camera, Matrix matrix);

}
