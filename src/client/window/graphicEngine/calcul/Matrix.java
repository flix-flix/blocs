package client.window.graphicEngine.calcul;

import client.window.graphicEngine.extended.ModelCube;

public class Matrix {

	private static final double toRadian = Math.PI / 180;

	/** Vectors vx, vy, vz and for each x,y,z */
	double[][] tab = new double[3][3];
	Point3D point;

	double memX, memY, memZ;

	private Matrix(double a, boolean vx) {// Change of basis
		a *= toRadian;
		if (vx) {// Y stable
			tab[0][0] = Math.cos(a);
			tab[0][2] = -Math.sin(a);
			tab[1][1] = 1;
			tab[2][0] = Math.sin(a);
			tab[2][2] = Math.cos(a);
		} else {// Z stable
			tab[0][0] = Math.cos(a);
			tab[0][1] = -Math.sin(a);
			tab[1][0] = Math.sin(a);
			tab[1][1] = Math.cos(a);
			tab[2][2] = 1;
		}
	}

	public Matrix(double vx, double vy, Point3D p) {
		tab[0][0] = 1;
		tab[1][1] = 1;
		tab[2][2] = 1;

		point = p.clone();

		Matrix m1 = new Matrix(vx, true);
		Matrix m2 = new Matrix(vy, false);

		multiply(m1);
		multiply(m2);
	}

	// =========================================================================================================================

	public void multiply(Matrix mm) {
		double[][] mem = new double[3][3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				for (int k = 0; k < 3; k++)
					mem[i][j] += tab[k][j] * mm.tab[i][k];
		tab = mem;
	}

	// =========================================================================================================================

	public void decal(Point3D p) {
		p.x -= point.x;
		p.y -= point.y;
		p.z -= point.z;
	}

	// =========================================================================================================================

	public double line(Point3D p, int line) {
		return p.x * tab[line][0] + p.y * tab[line][1] + p.z * tab[line][2];
	}

	// =========================================================================================================================

	public void transform(Point3D p) {
		decal(p);
		Point3D pp = p.clone();
		p.x = (line(pp, 0));
		p.y = (line(pp, 1));
		p.z = (line(pp, 2));
	}

	// =========================================================================================================================

	public void transform(ModelCube c) {
		c.initPoints();

		transform(c.centerDecal);
		transform(c.ppx);
		transform(c.ppy);
		transform(c.ppz);
		c.recalcul();
	}
}
