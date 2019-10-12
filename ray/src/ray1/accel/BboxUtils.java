package ray1.accel;

import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.surface.*;

// TODO#Ray tracing Part 2: Compute the bounding box and store the result in
// averagePosition, minBound, and maxBound.
public class BboxUtils {
	
	/**
	 * Computing Bounding box for a triangle
	 * */
	public static void triangleBBox(Triangle t) {
		// TODO#Ray Part 2 Task 1: Compute Bounding Box for a triangle:
		// Compute t.minBound, t.maxBound, t.averagePosition
		Vector3 p1 = t.owner.getMesh().positions.get(t.face.positions[0]).clone();
		Vector3 p2 = t.owner.getMesh().positions.get(t.face.positions[1]).clone();
		Vector3 p3 = t.owner.getMesh().positions.get(t.face.positions[2]).clone();
		double xMax, yMax, zMax;
		double xMin, yMin, zMin;
		xMax = Math.max(p1.x,Math.max(p2.x,p3.x));
		yMax = Math.max(p1.y,Math.max(p2.y,p3.y));
		zMax = Math.max(p1.z,Math.max(p2.z,p3.z));
		xMin = Math.min(p1.x,Math.min(p2.x,p3.x));
		yMin = Math.min(p1.y,Math.min(p2.y,p3.y));
		zMin = Math.min(p1.z,Math.min(p2.z,p3.z));

		t.minBound = new Vector3d(xMin, yMin, zMin);
		t.maxBound = new Vector3d(xMax, yMax, zMax);
		t.averagePosition = new Vector3d((p1.x + p2.x + p3.x)/3, (p1.y + p2.y + p3.y)/3, (p1.z + p2.z + p3.z)/3);
	}
	
	/**
	 * Computing Bounding box for a sphere
	 * */
	public static void sphereBBox(Sphere s) {
		// TODO#Ray Part 2 Task 1: Compute Bounding Box for a Sphere
		// Compute s.minBound, s.maxBound, s.averagePosition
		Vector3d  tempMax = new Vector3d();
		tempMax.set(s.getCenter().clone().add(s.getRadius(),s.getRadius(),s.getRadius()));
		s.maxBound = tempMax;
		Vector3d  tempMin = new Vector3d();
		tempMin.set(s.getCenter().clone().sub(s.getRadius(),s.getRadius(),s.getRadius()));
		s.minBound = tempMin;
		Vector3d tempAve = new Vector3d();
		tempAve.set(s.getCenter().clone().x, s.getCenter().clone().y, s.getCenter().clone().z);
		s.averagePosition = tempAve;
			
	}
	
	/**
	 * Computing Bounding box for a cylinder
	 * */
	public static void cylinderBBox(Cylinder c) {
		c.averagePosition = new Vector3d(c.getCenter());

		c.minBound = new Vector3d(Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		c.maxBound = new Vector3d(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		
		
		
		
		Vector3d[] v = new Vector3d[8];
		int count = 0;
		for (int i = -1; i < 2; i += 2) {
			for (int j = -1; j < 2; j += 2) {
				for (int k = -1; k < 2; k += 2) {
					v[count] = new Vector3d(c.getCenter());
					v[count].x += c.getRadius() * i;
					v[count].y += c.getRadius() * j;
					v[count].z += k * 0.5 * c.getHeight();
					count++;
				}
			}
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 3; j++) {
				if (v[i].get(j) < c.minBound.get(j))
					c.minBound.set(j, v[i].get(j));
				if (v[i].get(j) > c.maxBound.get(j))
					c.maxBound.set(j, v[i].get(j));
			}
		}
	}
	
	/**
	 * Computing Bounding box for a box
	 * */
	public static void boxBBox(Box b) {
		Vector3 minPt = b.getMinPt();
		Vector3 maxPt = b.getMaxPt();
		
		b.minBound = new Vector3d(Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		b.maxBound = new Vector3d(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		Vector3d[] v = new Vector3d[8];
		int[] k = new int[3];
		int count = 0;
		for (k[0] = 0; k[0] < 2; k[0]++) {
			for (k[1] = 0; k[1] < 2; k[1]++) {
				for (k[2] = 0; k[2] < 2; k[2]++) {
					v[count] = new Vector3d();
					for (int j = 0; j < 3; j++) {
						if (k[j] == 0)
							v[count].set(j,(double) minPt.get(j));
						else
							v[count].set(j,(double) maxPt.get(j));
					}
					count++;
				}
			}
		}

		b.averagePosition = new Vector3d();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 3; j++) {
				b.averagePosition.set(j, b.averagePosition.get(j) + v[i].get(j));
				if (v[i].get(j) < b.minBound.get(j))
					b.minBound.set(j, v[i].get(j));
				if (v[i].get(j) > b.maxBound.get(j))
					b.maxBound.set(j, v[i].get(j));
			}
		}
		b.averagePosition.mul(1.0 / 8);
	}
}
