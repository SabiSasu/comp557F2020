package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;

	public Mesh() {
		super();
		this.soup = null;
	}			
		
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
		// TODO: Objective 7: ray triangle intersection for meshes

		for(int i = 0; i < soup.faceList.size(); i++) {
			
			int[] face = soup.faceList.get(i);
			Point3d v0 = soup.vertexList.get(face[0]).p;
			Point3d v1 = soup.vertexList.get(face[1]).p;
			Point3d v2 = soup.vertexList.get(face[2]).p;

			Vector3d edge1 = new Vector3d();
			edge1.sub(v1, v0);
			Vector3d edge2 = new Vector3d();
			edge2.sub(v2, v0);
			
			Vector3d pvec = new Vector3d();
			pvec.cross(ray.viewDirection, edge2);

			double t = 0;
			double det = 1.0/edge1.dot(pvec);

			if (det < 0) {
				edge1.sub(v2, v0);
				edge2.sub(v1, v0);
			}
			else {
				Vector3d tvec = new Vector3d();
				tvec.sub(ray.eyePoint, v0);
				double u = tvec.dot(pvec) * det;
	
				if (u > 0 && u < 1) {
					Vector3d qvec = new Vector3d();
					qvec.cross(tvec, edge1);
					double v = ray.viewDirection.dot(qvec) * det;
					if (v > 0 && (u + v) < 1)
						t = edge2.dot(qvec) * det;
				}
			}

			if (t > 0 && t < result.t) { 
				result.material = this.material;
				Vector3d normal = new Vector3d();
				normal.cross(edge1,edge2);
				result.n.set(normal);
				
				Point3d point = new Point3d(ray.viewDirection);
				point.scale(t);
				point.add(ray.eyePoint);
				result.p.set(point);
				
				result.t = t;
				
			}
		}
	}

}
