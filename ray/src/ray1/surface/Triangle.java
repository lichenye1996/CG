package ray1.surface;

import egl.math.Matrix3d;
import egl.math.Vector2;
import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.shader.Shader;
import ray1.OBJFace;
import ray1.accel.BboxUtils;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3 norm;

  /** The mesh that contains this triangle */
  public Mesh owner;

  /** The face that contains this triangle */
  public OBJFace face = null;

  double a, b, c, d, e, f;
  public Triangle(Mesh owner, OBJFace face, Shader shader) {
    this.owner = owner;
    this.face = face;

    Vector3 v0 = owner.getMesh().getPosition(face,0);
    Vector3 v1 = owner.getMesh().getPosition(face,1);
    Vector3 v2 = owner.getMesh().getPosition(face,2);

    if (!face.hasNormals()) {
      Vector3 e0 = new Vector3(), e1 = new Vector3();
      e0.set(v1).sub(v0);
      e1.set(v2).sub(v0);
      norm = new Vector3();
      norm.set(e0).cross(e1).normalize();
    }

    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;

    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;

    this.setShader(shader);
  }

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
    // TODO#Ray Part 1 Task 2: fill in this function.
    Vector3 aa = owner.getMesh().getPosition(face,0);
    Vector3d origin = rayIn.origin;
    Vector3d direction = rayIn.direction;
    Vector3d a_b = new Vector3d(a, b, c);
    Vector3d a_c = new Vector3d(d, e, f);
    Vector3d constant = new Vector3d(aa.x - origin.x, aa.y - origin.y, aa.z - origin.z);
    Matrix3d Matrix = new Matrix3d(a_b, a_c, direction);
    Matrix3d tempMatrix1 = new Matrix3d(constant, a_c, direction);
    Matrix3d tempMatrix2 = new Matrix3d(a_b, constant, direction);
    Matrix3d tempMatrix3 = new Matrix3d(a_b, a_c, constant);
    double belta = tempMatrix1.determinant()/Matrix.clone().determinant();
    double gamma = tempMatrix2.determinant()/Matrix.clone().determinant();
    double t = tempMatrix3.determinant()/Matrix.clone().determinant();
    if (t < rayIn.start) return false;
    if (rayIn.end != 0 && t > rayIn.end) return false;
    if (!(belta > 0 && gamma > 0 && belta + gamma < 1)) {
      return false;
    }
    outRecord.location.set(origin.clone().add(direction.clone().mul(t)));
    if (!face.hasNormals()) {
      outRecord.normal.set(norm);
    } else {
      Vector3 norm1 = owner.getMesh().getNormal(face, 0);
      Vector3 norm2 = owner.getMesh().getNormal(face, 1);
      Vector3 norm3 = owner.getMesh().getNormal(face, 2);
      Vector3 finalNorm = norm1.clone().mul((float)(1 - belta - gamma)).add(norm2.clone().mul((float)belta)).add(norm3.clone().mul((float)gamma));
      outRecord.normal.set(finalNorm.normalize());
    }
    outRecord.surface = this;
    if (face.hasUVs()) {
      Vector2 tex1 = owner.getMesh().getUV(face, 0);
      Vector2 tex2 = owner.getMesh().getUV(face, 1);
      Vector2 tex3 = owner.getMesh().getUV(face, 2);
      outRecord.texCoords.set(tex1.clone().mul((float) (1 - belta - gamma)).add(tex2.clone().mul((float)belta).add(tex3.clone().mul((float)gamma))));
    }

    outRecord.t = t;

    // If there was an intersection, fill out the intersection record
    return true;
  }

  public void computeBoundingBox(){
    BboxUtils.triangleBBox(this);
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}