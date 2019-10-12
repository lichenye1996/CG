package ray1.shader;

import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.IntersectionRecord;
import ray1.Light;
import ray1.Ray;
import ray1.RayTracer;
import ray1.Scene;

public abstract class ReflectionShader extends Shader {

	/** BEDF used by this shader. */
	protected BRDF brdf = null;

	/** Coefficient for mirror reflection. */
	protected final Colorf mirrorCoefficient = new Colorf();
	public void setMirrorCoefficient(Colorf mirrorCoefficient) { this.mirrorCoefficient.set(mirrorCoefficient); }
	public Colorf getMirrorCoefficient() {return new Colorf(mirrorCoefficient);}

	public ReflectionShader() {
		super();
	}

	/**
	 * Evaluate the intensity for a given intersection using the Microfacet shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colorf outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		
		Vector3d incoming = new Vector3d();
		Vector3d outgoing = new Vector3d();
				
		outgoing.set(ray.origin).sub(record.location).normalize();
		Vector3d surfaceNormal = record.normal;
		Vector2 texCoords = new Vector2(record.texCoords);
		
		Colorf BRDFVal = new Colorf();
		
		// direct reflection from light sources
		outIntensity.setZero();
		
		// TODO#Ray Task 5: Fill in this function.
				// 1) Loop through each light in the scene.
				// 2) If the intersection point is shadowed, skip the calculation for the light.
				//	  See Shader.java for a useful shadowing function.
				// 3) Compute the incoming direction by subtracting
				//    the intersection point from the light's position.
				// 4) Compute the color of the point using the shading model. 
				//	  EvalBRDF method of brdf object should be called to evaluate BRDF value at the shaded surface point.
				// 5) Add the computed color value to the output.
				// 6) If mirrorCoefficient is not zero vector, add recursive mirror reflection
				//		6a) Compute the mirror reflection ray direction by reflecting the direction vector of "ray" about surface normal
				//		6b) Construct mirror reflection ray starting from the intersection point (record.location) and pointing along 
				//			direction computed in 6a) (Hint: remember to call makeOffsetRay to avoid self-intersecting)
				//      6c) Compute the Fresnel's refectance coefficient with Schlick's approximation 
				// 		6d) call RayTracer.shadeRay() with the mirror reflection ray and (depth+1)
				// 		6e) add returned color value in 6d) to output
		for (Light light : scene.getLights()) {
			Ray checkRay = new Ray();
			Vector3d positionD = new Vector3d(light.position.x, light.position.y, light.position.z);
			checkRay.set(record.location.clone(), positionD.clone().sub(record.location.clone()).normalize());
			checkRay.makeOffsetSegment(positionD.clone().sub(record.location.clone()).len());
			if (scene.getAnyIntersection(checkRay)) continue;
			incoming.set(positionD.clone().sub(record.location.clone()));
			if (incoming.dot(surfaceNormal) < 0 || outgoing.dot(surfaceNormal) < 0) continue;

			Colorf tempColor = new Colorf();
			brdf.EvalBRDF(incoming.clone(), outgoing.clone(), surfaceNormal.clone(), texCoords.clone(), tempColor);
			BRDFVal.set(BRDFVal.clone().add(tempColor.clone().mul(light.intensity)));
		}
		if (getMirrorCoefficient().len() > 0 && depth <= 12) {
			Vector3d reflection = surfaceNormal.clone().mul(2 * surfaceNormal.clone().dot(outgoing.clone().normalize())).sub(outgoing.clone().normalize());
			double cos = 1 - reflection.clone().dot(surfaceNormal.clone())/(reflection.len()*surfaceNormal.len());
			Vector3 Rth = mirrorCoefficient.clone().add(mirrorCoefficient.clone().sub(1, 1, 1).negate().mul((float)Math.pow(cos, 5)));
			Colorf recursive = new Colorf();
			Ray reflectRay = new Ray();
			reflectRay.set(record.location, reflection.clone().normalize());
			reflectRay.makeOffsetRay();
			RayTracer.shadeRay(recursive, scene, reflectRay, depth + 1);
			outIntensity.set(recursive.clone().mul(Rth));
		}

		outIntensity.set(outIntensity.clone().add(BRDFVal));
	
		// recursive reflection


		
	}

}