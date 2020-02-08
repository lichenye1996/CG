package manip;

import egl.math.*;
import gl.RenderObject;

public class ScaleManipulator extends Manipulator {

	public ScaleManipulator (ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public ScaleManipulator (RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	@Override
	protected Matrix4 getReferencedTransform () {
		if (this.reference == null) {
			throw new RuntimeException ("Manipulator has no controlled object!");
		}
		return new Matrix4().set(reference.scale)
				.mulAfter(reference.rotationZ)
				.mulAfter(reference.rotationY)
				.mulAfter(reference.rotationX)
				.mulAfter(reference.translation);
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#A3: Modify this.reference.scale given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.

		// A3 SOLUTION BEGIN
		if (this.axis == ManipulatorAxis.X) {
			Vector3 malOrig = new Vector3(0, 0, 0);
			Vector3 malDir = new Vector3(1, 0, 0);
			float t1 = this.getAxisT(malOrig, malDir, viewProjection, curMousePos);
			float t2 = this.getAxisT(malOrig, malDir, viewProjection, lastMousePos);
			this.reference.scale.set(0, 0, this.reference.scale.get(0, 0) * (t1/t2));
		} else if (this.axis == ManipulatorAxis.Y) {
			Vector3 malOrig = new Vector3(0, 0, 0);
			Vector3 malDir = new Vector3(0, 1, 0);
			float t1 = this.getAxisT(malOrig, malDir, viewProjection, curMousePos);
			float t2 = this.getAxisT(malOrig, malDir, viewProjection, lastMousePos);
			this.reference.scale.set(1, 1, this.reference.scale.get(1, 1) * (t1/t2));
		} else if (this.axis == ManipulatorAxis.Z) {
			Vector3 malOrig = new Vector3(0, 0, 0);
			Vector3 malDir = new Vector3(0, 0, 1);
			float t1 = this.getAxisT(malOrig, malDir, viewProjection, curMousePos);
			float t2 = this.getAxisT(malOrig, malDir, viewProjection, lastMousePos);
			this.reference.scale.set(2, 2, this.reference.scale.get(2, 2) * (t1/t2));
		}
		// A3 SOLUTION END
	}

	@Override
	protected String meshPath () {
		return "data/meshes/Scale.obj";
	}

}
