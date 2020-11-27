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
		Vector3d edge = new Vector3d();
		Vector3d edge2 = new Vector3d();
		Vector3d pVec = new Vector3d();
		Vector3d tVec = new Vector3d();
		Vector3d qVec = new Vector3d();
		Point3d eyepoint = ray.eyePoint;
		Vector3d viewDir = ray.viewDirection;
		float t = -1;

		for(int[] face : soup.faceList) { 
			Point3d p0 = soup.vertexList.get(face[0]).p;
			Point3d p1 = soup.vertexList.get(face[1]).p;
			Point3d p2 = soup.vertexList.get(face[2]).p;

			edge.sub(p1, p0);
			edge2.sub(p2, p0);

			pVec.cross(viewDir, edge2);

			float det = (float) edge.dot(pVec);

			if (det < 1e-9) { 
				//reverse of mesh
				edge.sub(p2, p0);
				edge2.sub(p1, p0);
			}

			if (det > 1e-9) { 
				float invDet = 1.0f/det;

				tVec.sub(eyepoint, p0);

				float u = (float)tVec.dot(pVec) * invDet;

				if (u > 0 && u < 1) {
					qVec.cross(tVec, edge);

					float v = (float) viewDir.dot(qVec) * invDet;

					if (v > 0 && (u+v) < 1)
						t = (float) edge2.dot(qVec) * invDet;
				}
			}

			if (t > 1e-9 && t < result.t) { 
				//check which intersection is closer
				result.t = t;

				double pointX = eyepoint.x + t*viewDir.x;
				double pointY = eyepoint.y + t*viewDir.y;
				double pointZ = eyepoint.z + t*viewDir.z;
				
				Point3d p = new Point3d(pointX,pointY,pointZ);
				result.p.set(p);

				Vector3d n = new Vector3d();
				n.cross(edge,edge2);
				result.n.set(n);

				result.material = this.material;
			}
		}
	}

}
