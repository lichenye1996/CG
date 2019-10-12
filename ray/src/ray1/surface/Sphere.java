package ray1.surface;

import egl.math.Vector2d;
import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.accel.BboxUtils;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {

  /** The center of the sphere. */
  protected final Vector3 center = new Vector3();
  public void setCenter(Vector3 center) { this.center.set(center); }
  public Vector3 getCenter() {return this.center.clone();}

  /** The radius of the sphere. */
  protected float radius = 1.0f;
  public void setRadius(float radius) { this.radius = radius; }
  public float getRadius() {return this.radius;}

  protected final double M_2PI = 2 * Math.PI;

  public Sphere() { }

  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#Ray Task 2: fill in this function.
    Vector3d direction = rayIn.direction;
    Vector3d origin = rayIn.origin;
    Vector3d centerd = new Vector3d(center.x, center.y, center.z);
    double delta = (direction.clone().dot(origin.clone()) - direction.clone().dot(centerd.clone()))
            * (direction.clone().dot(origin.clone()) - direction.clone().dot(centerd.clone()))
            - (direction.clone().dot(direction.clone()))
            * ((origin.clone().sub(centerd.clone())).dot(origin.clone().sub(centerd.clone())) - radius*radius);
    if (delta <= 0) {
      return false;
    }
    double t1 = (centerd.clone().dot(direction.clone()) - origin.clone().dot(direction.clone()) - Math.sqrt(delta)) / direction.clone().dot(direction.clone());
    double t2 = (centerd.clone().dot(direction.clone()) - origin.clone().dot(direction.clone()) + Math.sqrt(delta)) / direction.clone().dot(direction.clone());
    double t = t1 >= rayIn.start ? t1 : t2;
    if (rayIn.end != 0 && t > rayIn.end) return false;
    if (rayIn.start != 0 && t < rayIn.start) return false;
    Vector3d location = origin.clone().add(direction.clone().mul(t));

    Vector3d normal = location.clone().sub(centerd.clone()).normalize();
    Vector2d myTexCoords = new Vector2d(0, 0);
    Vector3d texVector = location.clone().sub(centerd.clone());
    double vPosition = texVector.clone().y  + radius / (2 * radius);
    double uPosition = (Math.atan(texVector.x / texVector.z)  + Math.PI) / 2 / Math.PI;
    myTexCoords.set(uPosition, vPosition);



    // If there was an intersection, fill out the intersection record
    outRecord.location.set(location);
    outRecord.normal.set(normal);
    outRecord.texCoords.set(myTexCoords);
    outRecord.surface = this;
    outRecord.t = t;

    // If there was an intersection, fill out the intersection record
    return true;
  }

  /**
   * Compute Bounding Box for sphere
   * */
  public void computeBoundingBox() {
    BboxUtils.sphereBBox(this);
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "sphere " + center + " " + radius + " " + shader + " end";
  }

}