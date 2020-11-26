package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }	

	@Override
	public void intersect(Ray ray, IntersectResult result) {
		// TODO: Objective 6: intersection of Ray with axis aligned box
		double t0x = (min.x - ray.eyePoint.x)/ray.viewDirection.x;
		double t1x = (max.x - ray.eyePoint.x)/ray.viewDirection.x;
		double t0y = (min.y - ray.eyePoint.y)/ray.viewDirection.y;
		double t1y = (max.y - ray.eyePoint.y)/ray.viewDirection.y;
		double t0z = (min.z - ray.eyePoint.z)/ray.viewDirection.z;
		double t1z = (max.z - ray.eyePoint.z)/ray.viewDirection.z;

		double txmin = ray.viewDirection.x >= 0 ? t0x : t1x;
		double txmax = ray.viewDirection.x >= 0 ? t1x : t0x;
		double tymin = ray.viewDirection.y >= 0 ? t0y : t1y;
		double tymax = ray.viewDirection.y >= 0 ? t1y : t0y;
		double tzmin = ray.viewDirection.z >= 0 ? t0z : t1z;
		double tzmax = ray.viewDirection.z >= 0 ? t1z : t0z;
		
		if(txmin > tymax || tymin > txmax)
			return;
		if (tymin > txmin)
			txmin = tymin;
		if (tymax < txmax)
			txmax = tymax;

		
		if (txmin > tzmax || tzmin > txmax)
			return;
		if (tzmin > txmin)txmin = tzmin;
		if (tzmax < txmax)txmax = tzmax;
		if(txmin <= 0)
			return;
		

		Point3d intersection = new Point3d();
		ray.getPoint(txmin, intersection); 		
		result.p = intersection;
		result.material = this.material;
		result.t = txmin;
		
		float epsilon = 0.00001f;
		Vector3d normal = new Vector3d(intersection);

		if(Math.abs(normal.x - min.x) < epsilon) 
			normal.set(-1,0,0);
		else if(Math.abs(normal.x - max.x) < epsilon) 
			normal.set(1,0,0);
		else if(Math.abs(normal.y - min.y) < epsilon) 
			normal.set(0,-1,0);
		else if(Math.abs(normal.y - max.y) < epsilon) 
			normal.set(0,1,0);
		else if(Math.abs(normal.z - min.z) < epsilon) 
			normal.set(0,0,-1);
		else if(Math.abs(normal.z - max.z) < epsilon) 
			normal.set(0,0,1);
		
		result.n = normal;
	}	



}
