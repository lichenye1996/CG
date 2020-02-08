package manip;

import egl.math.*;
import gl.RenderObject;

public class RotationManipulator extends Manipulator {

	protected String meshPath = "Rotate.obj";

	public RotationManipulator(ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public RotationManipulator(RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	//assume X, Y, Z on stack in that order
	@Override
	protected Matrix4 getReferencedTransform() {
		Matrix4 m = new Matrix4();
		switch (this.axis) {
		case X:
			m.set(reference.rotationX).mulAfter(reference.translation);
			break;
		case Y:
			m.set(reference.rotationY)
				.mulAfter(reference.rotationX)
				.mulAfter(reference.translation);
			break;
		case Z:
			m.set(reference.rotationZ)
			.mulAfter(reference.rotationY)
			.mulAfter(reference.rotationX)
			.mulAfter(reference.translation);
			break;
		}
		return m;
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#Manipulator: Modify this.reference.rotationX, this.reference.rotationY, or this.reference.rotationZ
		//   given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.
		//mouse ray in view space
		Vector3 p1 = new Vector3(lastMousePos.x, lastMousePos.y, -1);
		Vector3 p2 = new Vector3(lastMousePos.x, lastMousePos.y, 1);
		Matrix4 mVPI = viewProjection.clone().invert();
		//mouse ray in world space
		mVPI.mulPos(p1);
		mVPI.mulPos(p2);
		Vector3 lastRayOrigin = p1;
		Vector3 lastRayDirection = p2.clone().sub(p1);

		Vector3 p3 = new Vector3(curMousePos.x, curMousePos.y, -1);
		Vector3 p4 = new Vector3(curMousePos.x, curMousePos.y, 1);
		//mouse ray in world space
		mVPI.mulPos(p3);
		mVPI.mulPos(p4);
		Vector3 currentRayOrigin = p3;
		Vector3 currentRayDirection = p4.clone().sub(p3);

		Vector3 newWorld =new Vector3(0, 0, 0);
		Vector3 newX = new Vector3(1, 0, 0);
		Vector3 newY = new Vector3(0, 1, 0);
		Vector3 newZ = new Vector3(0, 0, 1);

		if (this.axis == ManipulatorAxis.X) {
			float tLast = -lastRayOrigin.x/lastRayDirection.x;
			Vector3 intersectLast = lastRayOrigin.clone().add(lastRayDirection.clone().mul(tLast));

			float tCurrent = -currentRayOrigin.x/currentRayDirection.x;
			Vector3 intersectCurrent = currentRayOrigin.clone().add(currentRayDirection.clone().mul(tCurrent));

			Vector3 rayLastPlane = intersectLast.clone().sub(newWorld.clone());
			Vector3 rayCurrentPlane = intersectCurrent.clone().sub(newWorld.clone());

			float cos = rayLastPlane.clone().normalize().dot(rayCurrentPlane.clone().normalize());
			Vector3 dir = rayLastPlane.clone().normalize().cross(rayCurrentPlane.clone().normalize());
			float neg = dir.x >0 ? 1 : dir.x == 0 ? 0 : -1;
			//sometimes this number will be 1.000001, so floor it to avoid Nan
			if (cos > 1) cos = 1;
			float phi = (float)Math.acos(cos);
			Matrix4 rotation = new Matrix4();
			rotation.set(rotation.createRotationX(phi * neg));
			this.reference.rotationX.set(this.reference.rotationX.clone().mulBefore(rotation.clone()));
		} else if (this.axis == ManipulatorAxis.Y) {
			float tLast = -lastRayOrigin.y/lastRayDirection.y;
			Vector3 intersectLast = lastRayOrigin.clone().add(lastRayDirection.clone().mul(tLast));

			float tCurrent = -currentRayOrigin.y/currentRayDirection.y;
			Vector3 intersectCurrent = currentRayOrigin.clone().add(currentRayDirection.clone().mul(tCurrent));

			Vector3 rayLastPlane = intersectLast.clone().sub(newWorld.clone());
			Vector3 rayCurrentPlane = intersectCurrent.clone().sub(newWorld.clone());

			float cos = rayLastPlane.clone().normalize().dot(rayCurrentPlane.clone().normalize());
			Vector3 dir = rayLastPlane.clone().normalize().cross(rayCurrentPlane.clone().normalize());
			float neg = dir.y >0 ? 1 : dir.y == 0 ? 0 : -1;
			//sometimes this number will be 1.000001, so floor it to avoid Nan
			if (cos > 1) cos = 1;
			float phi = (float)Math.acos(cos);
			Matrix4 rotation = new Matrix4();
			rotation.set(rotation.createRotationY(phi * neg));
			this.reference.rotationY.set(this.reference.rotationY.clone().mulBefore(rotation.clone()));
		} else if (this.axis == ManipulatorAxis.Z) {
			float tLast = -lastRayOrigin.z/lastRayDirection.z;
			Vector3 intersectLast = lastRayOrigin.clone().add(lastRayDirection.clone().mul(tLast));

			float tCurrent = -currentRayOrigin.z/currentRayDirection.z;
			Vector3 intersectCurrent = currentRayOrigin.clone().add(currentRayDirection.clone().mul(tCurrent));

			Vector3 rayLastPlane = intersectLast.clone().sub(newWorld.clone());
			Vector3 rayCurrentPlane = intersectCurrent.clone().sub(newWorld.clone());

			float cos = rayLastPlane.clone().normalize().dot(rayCurrentPlane.clone().normalize());
			Vector3 dir = rayLastPlane.clone().normalize().cross(rayCurrentPlane.clone().normalize());
			float neg = dir.z >0 ? 1 : dir.z == 0 ? 0 : -1;
			//sometimes this number will be 1.000001, so floor it to avoid Nan
			if (cos > 1) cos = 1;
			float phi = (float)Math.acos(cos);
			Matrix4 rotation = new Matrix4();
			rotation.set(rotation.createRotationZ(phi * neg));
			this.reference.rotationZ.set(this.reference.rotationZ.clone().mulBefore(rotation.clone()));
		}

	}


	@Override
	protected String meshPath () {
		return "data/meshes/Rotate.obj";
	}
}
