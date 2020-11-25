package comp557.a4;

import javax.vecmath.Point3d;

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
		// Objective 6: intersection of Ray with axis aligned box
				double txMin = (min.x - ray.eyePoint.x)/ray.viewDirection.x;
				double txMax = (max.x - ray.eyePoint.x)/ray.viewDirection.x;
				double tyMin = (min.y - ray.eyePoint.y)/ray.viewDirection.y;
				double tyMax = (max.y - ray.eyePoint.y)/ray.viewDirection.y;
				double tzMin = (min.z - ray.eyePoint.z)/ray.viewDirection.z;
				double tzMax = (max.z - ray.eyePoint.z)/ray.viewDirection.z;
				boolean txSwapped = false;
				boolean tySwapped = false;
				boolean tzSwapped = false;
				if(txMin > txMax){
					txMin = swapDouble(txMax, txMax = txMin);
					txSwapped = true;
				}
				if(tyMin > tyMax){
					tyMin = swapDouble(tyMax, tyMax = tyMin);
					tySwapped = true;
				}
				if(tzMin > tzMax){
					tzMin = swapDouble(tzMax, tzMax = tzMin);
					tzSwapped = true;
				}
				double tMin = Math.max(txMin, Math.max(tyMin, tzMin));
				double tMax = Math.min(txMax, Math.min(tyMax, tzMax));
				if(tMin < tMax && tMin > 1e-9 && tMin < result.t){
					result.t = tMin;
					result.p.scaleAdd(tMin, ray.viewDirection, ray.eyePoint);
					result.material = this.material;
					if(txMin > Math.max(tyMin, tzMin)){
						if(!txSwapped){
							result.n.set(-1,0,0);
						}else{
							result.n.set(1,0,0);
						}
					}else if(tyMin > Math.max(txMin, tzMin)){
						if(!tySwapped){
							result.n.set(0,-1,0);
						}else{
							result.n.set(0,1,0);
						}
					}else{
						if(!tzSwapped){
							result.n.set(0,0,-1);
						}else{
							result.n.set(0, 0, 1);
						}
					}

				}
	}	

	private double swapDouble(double a, double b){
		return a;
	}

}
