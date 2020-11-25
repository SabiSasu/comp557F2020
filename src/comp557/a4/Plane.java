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
    	if(denominator != 0) {
            Vector3d vector = new Vector3d(0,0,0);
            vector.sub(ray.eyePoint);
            double t = (n.dot(vector)/denominator);
            

            if (t > 0 && t < result.t) { 
                result.t = t;
                
                double pointX = ray.eyePoint.x + t*ray.viewDirection.x;
                double pointY = ray.eyePoint.y + t*ray.viewDirection.y;
                double pointZ = ray.eyePoint.z + t*ray.viewDirection.z;
                
                Point3d point = new Point3d(pointX,pointY,pointZ);
                result.p.set(point);

                result.n.set(n);

                if(material2 != null) {
                    int x = (int) Math.floor(point.x);
                    x = Math.abs(x);
                    int z = (int) Math.floor(point.z);
                    z = Math.abs(z);

                    if((x+z)%2 == 0)
                        result.material = this.material;
                    else
                        result.material = this.material2;
                }
                else
                    result.material = this.material;
            }
            
            
    	}
	}
    
}
