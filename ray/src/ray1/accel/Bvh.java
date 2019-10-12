
package ray1.accel;

import java.util.Arrays;
import java.util.Comparator;

import egl.math.Vector3d;
import org.lwjgl.Sys;
import ray1.IntersectionRecord;
import ray1.Ray;
import ray1.surface.Surface;

/**
 * Class for Axis-Aligned-Bounding-Box to speed up the intersection look up time.
 *
 * @author ss932, pramook
 */
public class Bvh implements AccelStruct {
	/**
	 * Performance counter
	 * */
	public static int hitCount = 0;
	public static int missCount = 0;
	
	/** A shared surfaces array that will be used across every node in the tree. */
	private Surface[] surfaces;

	/** A comparator class that can sort surfaces by x, y, or z coordinate.
	 *  See the subclass declaration below for details.
	 */
	static MyComparator cmp = new MyComparator();
	
	/** The root of the BVH tree. */
	BvhNode root;

	public Bvh() { }

	/**
	 * Set outRecord to the first intersection of ray with the scene. Return true
	 * if there was an intersection and false otherwise. If no intersection was
	 * found outRecord is unchanged.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param rayIn the ray to intersect
	 * @param anyIntersection if true, will immediately return when found an intersection
	 * @return true if and intersection is found.
	 */
	public boolean intersect(IntersectionRecord outRecord, Ray rayIn, boolean anyIntersection) {
		return intersectHelper(root, outRecord, rayIn, anyIntersection);
	}
	
	/**
	 * A helper method to the main intersect method. It finds the intersection with
	 * any of the surfaces under the given BVH node.  
	 *   
	 * @param node a BVH node that we would like to find an intersection with surfaces under it
	 * @param outRecord the output InsersectionMethod
	 * @param rayIn the ray to intersect
	 * @param anyIntersection if true, will immediately return when found an intersection
	 * @return true if an intersection is found with any surface under the given node
	 */
	private boolean intersectHelper(BvhNode node, IntersectionRecord outRecord, Ray rayIn, boolean anyIntersection)
	{
		// TODO#Ray Part 2 Task 3: fill in this function.
		// Hint: For a leaf node, use a normal linear search. Otherwise, search in the left and right children.
		// Another hint: save time by checking if the ray intersects the node first before checking the childrens.
		
		// ==== Step 1 ====
		// Check whether the ray intersect with the current node's bounding box, if not return false
		if (!node.intersects(rayIn)) return false;
		
		// ==== Step 2 ====
		// Check if current node is leaf
		// If current node is leaf, loop over all the surface in this leaf, do surface intersection check, find the first intersection
		// If current node is not a leaf, call intersectHelper recursively for left and right child of the node,
		Ray myRay = new Ray();
		myRay.origin.set(rayIn.origin);
		myRay.direction.set(rayIn.direction);
		myRay.start = rayIn.start;
		myRay.end = rayIn.end;

		boolean isHit = false;
		if (node.isLeaf()) {
			int start = node.surfaceIndexStart;
			int end = node.surfaceIndexEnd;
			for (int i = start; i < end; i++) {
				boolean tempHit = surfaces[i].intersect(outRecord, myRay);
				isHit =  tempHit || isHit;
				if (isHit){
					myRay.end = outRecord.t;
				}
				if (anyIntersection && isHit) {
					return true;
				}
			}
		}
		else {
			boolean is1 = intersectHelper(node.child[0], outRecord, myRay, anyIntersection);
			myRay.end = outRecord.t;
			boolean is2 = intersectHelper(node.child[1], outRecord, myRay, anyIntersection);
			boolean tempHit = is1 || is2;
			isHit = tempHit;
		}
		return isHit;
	}


	@Override
	public void build(Surface[] surfaces) {
		this.surfaces = surfaces;
		for(Surface s:this.surfaces)
			s.computeBoundingBox();
		root = createTree(0, surfaces.length);
		System.out.println("Bvh: " + surfaces.length + " surfaces");
		System.out.println("Bvh: " + nodeCount(root) + " nodes, " + leafCount(root) + " leaves");
		System.out.println("Bvh: max depth " + maxDepth(root));
		System.out.println("Bvh: average child volume ratio " + volRatio(root).mean);
	}
	
	/**
	 * Create a BVH [sub]tree.  This tree node will be responsible for storing
	 * and processing surfaces[start] to surfaces[end-1]. If the range is small enough,
	 * this will create a leaf BvhNode. Otherwise, the surfaces will be sorted according
	 * to the axis of the axis-aligned bounding box that is widest, and split into 2
	 * children.
	 * 
	 * @param start The start index of surfaces
	 * @param end The end index of surfaces
	 */
	private BvhNode createTree(int start, int end) {
		// TODO#Ray Part 2 Task 2: fill in this function.

		// ==== Step 1 ====
		// Find out the BIG bounding box enclosing all the surfaces in the range [start, end)
		// and store them in minB and maxB.
		// Hint: To find the bounding box for each surface, use getMinBound() and getMaxBound() */
		Vector3d minB = new Vector3d(1f/0,1f/0,1f/0);
		Vector3d maxB = new Vector3d(-1f/0,-1f/0,-1f/0);
		for (int i = start; i < end; i ++) {
			Surface surf = surfaces[i];
			minB.set(Math.min(minB.x, surf.getMinBound().x), Math.min(minB.y, surf.getMinBound().y), Math.min(minB.z, surf.getMinBound().z));
			maxB.set(Math.max(maxB.x, surf.getMaxBound().x), Math.max(maxB.y, surf.getMaxBound().y), Math.max(maxB.z, surf.getMaxBound().z));

		}


		// ==== Step 2 ====
		// Check for the base case. 
		// If the range [start, end) is small enough (e.g. less than or equal to 10), just return a new leaf node.
		if (end - start <= 10) {
			BvhNode node = new BvhNode(minB, maxB, null, null, start, end);
			return node;
		}

		
		// ==== Step 3 ====
		// Figure out the widest dimension (x or y or z).
		// If x is the widest, set widestDim = 0. If y, set widestDim = 1. If z, set widestDim = 2.
		int widestDim;
		double widthX = maxB.x - minB.x;
		double widthY = maxB.y - minB.y;
		double widthZ = maxB.z - minB.z;
		if (widthX >= widthY && widthX >= widthZ) {
			widestDim = 0;
		}
		else if (widthY >= widthZ && widthY >= widthX) {
			widestDim = 1;
		}
		else {
			widestDim = 2;
		}

		
		// ==== Step 4 ====
		// Sort surfaces according to the widest dimension.
		// Bubble Sort

//		for (int i = start; i< end; i++) {
//			for (int j = end - 1; j > i; j--) {
//				if (surfaces[j].averagePosition.get(widestDim) > surfaces[j-1].averagePosition.get(widestDim)) {
//					Surface temp = surfaces[j];
//					surfaces[j] = surfaces[j - 1];
//					surfaces[j - 1] = temp;
//				}
//			}
//		}
		//HeapSort
		Surface[] tempArr = Arrays.copyOfRange(surfaces, start, end);
		heapSort(tempArr, widestDim);
		int count = 0;
		for (int i=start; i<end; i++) {
			surfaces[i] = tempArr[count];
			count += 1;
		}


		// ==== Step 5 ====
		// Recursively create left and right children.
		BvhNode node = new BvhNode(minB, maxB, createTree(start, (end + start)/2), createTree((end + start)/2, end), start, end);


		return node;
	}

	private static void heapify(Surface[] array, int length, int i, int dim) {
		int leftChild = 2*i+1;
		int rightChild = 2*i+2;
		int largest = i;

		// if the left child is larger than parent
		if (leftChild < length && array[leftChild].averagePosition.get(dim) < array[largest].averagePosition.get(dim)) {
			largest = leftChild;
		}

		// if the right child is larger than parent
		if (rightChild < length && array[rightChild].averagePosition.get(dim) < array[largest].averagePosition.get(dim)) {
			largest = rightChild;
		}

		// if a swap needs to occur
		if (largest != i) {
			Surface temp = array[i];
			array[i] = array[largest];
			array[largest] = temp;
			heapify(array, length, largest, dim);
		}
	}

	public static void heapSort(Surface[] array, int dim) {
		if (array.length == 0) return;

		// Building the heap
		int length = array.length;
		// we're going from the first non-leaf to the root
		for (int i = length / 2-1; i >= 0; i--)
			heapify(array, length, i, dim);

		for (int i = length-1; i >= 0; i--) {
			Surface temp = array[0];
			array[0] = array[i];
			array[i] = temp;

			heapify(array, i, 0, dim);
		}
	}


	private int maxDepth(BvhNode node) {
		if (node.isLeaf())
			return 0;
		return 1 + Math.max(maxDepth(node.child[0]), maxDepth(node.child[1]));
	}
	

	private int nodeCount(BvhNode node) {
		if (node.isLeaf())
			return 1;
		return 1 + nodeCount(node.child[0]) + nodeCount(node.child[1]);
	}

	private int leafCount(BvhNode node) {
		if (node.isLeaf())
			return 1;
		return leafCount(node.child[0]) + leafCount(node.child[1]);
	}
	
	/*
	 * Average over all internal nodes of the ratio between the sum
	 * of the children's volumes and the parent's volume.
	 */
	private class RatioResult { 
		double mean; int count; 
		RatioResult(double mean, int count) {
			this.mean = mean;
			this.count = count;
		}
	}
	
	private RatioResult volRatio(BvhNode node) {
		if (node.isLeaf())
			return new RatioResult(0.0, 0);
		RatioResult r0 = volRatio(node.child[0]);
		RatioResult r1 = volRatio(node.child[1]);
		int count = 1 + r0.count + r1.count;
		double ratio = (nodeVol(node.child[0]) + nodeVol(node.child[1])) / nodeVol(node);
		double mean = (ratio + r0.mean * r0.count + r1.mean * r1.count) / count;
		return new RatioResult(mean, count);
	}
	
	private double nodeVol(BvhNode node) {
		return ((node.maxBound.x - node.minBound.x) * 
				(node.maxBound.y - node.minBound.y) * 
				(node.maxBound.z - node.minBound.z));
	}

}

/**
 * A subclass that compares the average position two surfaces by a given
 * axis. Use the setIndex(i) method to select which axis should be considered.
 * i=0 -> x-axis, i=1 -> y-axis, and i=2 -> z-axis.  
 *
 */
class MyComparator implements Comparator<Surface> {
	int index;
	public MyComparator() {  }

	public void setIndex(int index) {
		this.index = index;
	}

	public int compare(Surface o1, Surface o2) {
		double v1 = o1.getAveragePosition().get(index);
		double v2 = o2.getAveragePosition().get(index);
		if(v1 < v2) return 1;
		if(v1 > v2) return -1;
		return 0;
	}

}
