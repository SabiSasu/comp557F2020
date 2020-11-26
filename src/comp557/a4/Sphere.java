package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 2: intersection of ray with sphere
    	
    	Vector3d L = new Vector3d();
    	L.sub(ray.eyePoint, this.center);
    	
    	double a = ray.viewDirection.dot(ray.viewDirection); 
    	double b = 2 * ray.viewDirection.dot(L); 
    	double c = (L.dot(L)) - (this.radius*this.radius); 
    	double discr = (b*b) - (4*a*c); 
    	double quadratic = (-b - Math.sqrt(discr))/(2*a);
    	
    	if(discr >= 0 && quadratic < result.t) { 
    		result.material = this.material;
    		result.t = quadratic;
    		ray.getPoint(result.t, result.p);
    		
    		result.n.sub(result.p, this.center);
    		result.n.normalize();
    	}
    }
}
