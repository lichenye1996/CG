package ray1.accel;

import egl.math.Vector3;
import ray1.Ray;
import egl.math.Vector3d;

/**
 * A class representing a node in a bounding volume hierarchy.
 * 
 * @author pramook 
 */
public class BvhNode {

	/** The current bounding box for this tree node.
	 *  The bounding box is described by 
	 *  (minPt.x, minPt.y, minPt.z) - (maxBound.x, maxBound.y, maxBound.z).
	 */
	public final Vector3d minBound, maxBound;
	
	/**
	 * The array of children.
	 * child[0] is the left child.
	 * child[1] is the right child.
	 */
	public final BvhNode child[];

	/**
	 * The index of the first surface under this node. 
	 */
	public int surfaceIndexStart;
	
	/**
	 * The index of the surface next to the last surface under this node.	 
	 */
	public int surfaceIndexEnd; 
	
	/**
	 * Default constructor
	 */
	public BvhNode()
	{
		minBound = new Vector3d();
		maxBound = new Vector3d();
		child = new BvhNode[2];
		child[0] = null;
		child[1] = null;		
		surfaceIndexStart = -1;
		surfaceIndexEnd = -1;
	}
	
	/**
	 * Constructor where the user can specify the fields.
	 * @param minBound
	 * @param maxBound
	 * @param leftChild
	 * @param rightChild
	 * @param start
	 * @param end
	 */
	public BvhNode(Vector3d minBound, Vector3d maxBound, BvhNode leftChild, BvhNode rightChild, int start, int end) 
	{
		this.minBound = new Vector3d();
		this.minBound.set(minBound);
		this.maxBound = new Vector3d();
		this.maxBound.set(maxBound);
		this.child = new BvhNode[2];
		this.child[0] = leftChild;
		this.child[1] = rightChild;		   
		this.surfaceIndexStart = start;
		this.surfaceIndexEnd = end;
	}
	
	/**
	 * @return true if this node is a leaf node
	 */
	public boolean isLeaf()
	{
		return child[0] == null && child[1] == null; 
	}
	
	/** 
	 * Check if the ray intersects the bounding box.
	 * @param ray
	 * @return true if ray intersects the bounding box
	 */
	public boolean intersects(Ray ray) {
		// TODO#Ray Part 2 Task 3: fill in this function.
		// You can find this in the slides.
		double tXEnter, tXExit, tXMin, tXMax;
		double tYEnter, tYExit, tYMin, tYMax;
		double tZEnter, tZExit, tZMin, tZMax;
		double tEnter, tExit;

		Vector3d dir = ray.direction.clone().normalize();
		Vector3d ori = ray.origin.clone();

		tXMin = dir.x == 0 ? 1f/0 : (this.minBound.x - ori.x) / dir.x;
		tXMax = dir.x == 0 ? 1f/0 : (this.maxBound.x - ori.x) / dir.x;
		tYMin = dir.y == 0 ? 1f/0 : (this.minBound.y - ori.y) / dir.y;
		tYMax = dir.y == 0 ? 1f/0 : (this.maxBound.y - ori.y) / dir.y;
		tZMin = dir.z == 0 ? 1f/0 : (this.minBound.z - ori.z) / dir.z;
		tZMax = dir.z == 0 ? 1f/0 : (this.maxBound.z - ori.z) / dir.z;

		tXEnter = Math.min(tXMin,tXMax);
		tXExit = Math.max(tXMin, tXMax);
		tYEnter = Math.min(tYMin,tYMax);
		tYExit = Math.max(tYMin, tYMax);
		tZEnter = Math.min(tZMin,tZMax);
		tZExit = Math.max(tZMin, tZMax);

		if ((tXEnter > tYExit) || (tYEnter > tXExit)) return false;
		tEnter = tYEnter > tXEnter ? tYEnter : tXEnter;
		tExit = tYExit > tXExit ? tXExit : tYExit;

		if ((tEnter > tZExit) || (tExit < tZEnter)) return false;

		return true;
	}
}
