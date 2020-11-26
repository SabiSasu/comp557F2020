package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;
	
	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d( 0, 1, 0 );
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    }

        
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 4: intersection of ray with plane
    	
    	double denominator = n.dot(ray.viewDirection);
    	Vector3d vector = new Vector3d(0,0,0);
        vector.sub(ray.eyePoint);
    	double numerator = n.dot(vector);
    	if(denominator != 0) {
            double t = numerator/denominator;
            
            if (t > 0 && t < result.t) { 
                Point3d point = new Point3d(
                		ray.eyePoint.x + t*ray.viewDirection.x,
                		ray.eyePoint.y + t*ray.viewDirection.y,
                		ray.eyePoint.z + t*ray.viewDirection.z);
                result.t = t;
                result.p.set(point);
                result.n.set(n);
                double x = Math.abs(Math.floor(point.x));
                double z = Math.abs(Math.floor(point.z));
                result.material = (material2 != null && (x+z) % 2 == 0) ? this.material : this.material2;
            }
            
    	}
	}
    
}
