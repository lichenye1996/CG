package manip;

import camera.Camera;
import egl.math.*;
import gl.RenderObject;

public class TranslationManipulator extends Manipulator {
	// hard coded mesh for now


	public TranslationManipulator (ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public TranslationManipulator (RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	@Override
	protected Matrix4 getReferencedTransform () {
		if (this.reference == null) {
			throw new RuntimeException ("Manipulator has no controlled object!");
		}
		return new Matrix4().set(reference.translation);
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#A3: Modify this.reference.translation given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.

		// A3 SOLUTION START
		if (this.axis == ManipulatorAxis.X) {
			Vector3 malOrig = new Vector3(0, 0, 0);
			Vector3 malDir = new Vector3(1, 0, 0);
			float t1 = this.getAxisT(malOrig, malDir, viewProjection, curMousePos);
			float t2 = this.getAxisT(malOrig, malDir, viewProjection, lastMousePos);
			this.reference.translation.set(0, 3, this.reference.translation.get(0, 3) + (t1-t2));
		} else if (this.axis == ManipulatorAxis.Y) {
			Vector3 malOrig = new Vector3(0, 0, 0);
			Vector3 malDir = new Vector3(0, 1, 0);
			float t1 = this.getAxisT(malOrig, malDir, viewProjection, curMousePos);
			float t2 = this.getAxisT(malOrig, malDir, viewProjection, lastMousePos);
			this.reference.translation.set(1, 3, this.reference.translation.get(1, 3) + (t1-t2));
		} else if (this.axis == ManipulatorAxis.Z) {
			Vector3 malOrig = new Vector3(0, 0, 0);
			Vector3 malDir = new Vector3(0, 0, 1);
			float t1 = this.getAxisT(malOrig, malDir, viewProjection, curMousePos);
			float t2 = this.getAxisT(malOrig, malDir, viewProjection, lastMousePos);
			this.reference.translation.set(2, 3, this.reference.translation.get(2, 3) + (t1-t2));
		}

		// A3 SOLUTION END
	}

	@Override
	protected String meshPath () {
		return "data/meshes/Translate.obj";
	}

}
