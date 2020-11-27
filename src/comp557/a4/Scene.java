package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        
        render.init(w, h, showPanel);
         
        double samples = render.samples;
        
        for ( int j = 0; j < h && !render.isDone(); j++ ) {
            for ( int i = 0; i < w && !render.isDone(); i++ ) {
            	
            	Color3f c = new Color3f(0f, 0f, 0f);
            	for (double y = 0; y < Math.sqrt(samples); y++) {
                    for (double x = 0; x < Math.sqrt(samples); x++) {
            	
		                // TODO: Objective 1: generate a ray (use the generateRay method)
		            	Ray ray = new Ray();
		            	double[] offset = {(y + 0.5) / Math.sqrt(samples), (x + 0.5) / Math.sqrt(samples)};
						generateRay(i, j, offset, cam, ray);
						
		                
						// TODO: Objective 2: test for intersection with scene surfaces
						IntersectResult intersectResult = new IntersectResult();  
						for (Intersectable surface : surfaceList)
		                    surface.intersect(ray, intersectResult);
					
		                // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
						Ray shadowRay = new Ray();
						IntersectResult shadowResult = new IntersectResult();
						
						
						if(intersectResult.t != Double.POSITIVE_INFINITY) {	
							c = new Color3f(
									ambient.x * intersectResult.material.diffuse.x,
									ambient.y * intersectResult.material.diffuse.y,
									ambient.z * intersectResult.material.diffuse.z);
							intersectResult.n.normalize();
							 
							for(Light light: lights.values()){
								if(!SceneNode.nodeMap.containsKey("root") || !inShadow(intersectResult, light, SceneNode.nodeMap.get("root"), shadowResult, shadowRay)){
									Vector3d l = new Vector3d();
		                            l.sub(light.from, intersectResult.p);
		                            l.normalize();
		
		                            Vector3d v = new Vector3d();
		                            v.sub(cam.from, intersectResult.p);
		                            v.normalize();
		
		                            Vector3d hlight = new Vector3d();
		                            hlight.add(l, v);
		                            hlight.normalize();
		
		                            float ndotl = (float) intersectResult.n.dot(l);
		                            float ndoth = (float) intersectResult.n.dot(hlight);
		
		                            float max1 = (0 > ndotl) ? 0 : ndotl;
		                            float max2 = (0 > ndoth) ? 0 : ndoth;
		                            max2 = (float) Math.pow(max2, intersectResult.material.shinyness);
		
		                            float Ix = (float) (light.color.x * light.power);
		                            float Iy = (float) (light.color.y * light.power);
		                            float Iz = (float) (light.color.z * light.power);
		
		                            c.x += intersectResult.material.diffuse.x * Ix * max1 + intersectResult.material.specular.x * Ix * max2;
		                            c.y += intersectResult.material.diffuse.y * Iy * max1 + intersectResult.material.specular.y * Iy * max2;
		                            c.z += intersectResult.material.diffuse.z * Iz * max1 + intersectResult.material.specular.z * Iz * max2;}
							}
						}
						else
							c.add(render.bgcolor);
                    }       
                    }
            	// Here is an example of how to calculate the pixel value.
				c.scale((float) (1 / samples));
				c.clamp(0, 1);
				int r = (int)(255*c.x > 255? 255: 255*c.x);
                int g = (int)(255*c.y > 255? 255: 255*c.y);
                int b = (int)(255*c.z > 255? 255: 255*c.z);
                int a = 255;
                int argb = (a<<24 | r<<16 | g<<8 | b);    
                
                // update the render image
                render.setPixel(i, j, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }

    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {
		
		// TODO: Objective 1: generate rays given the provided parmeters
		
		double height = cam.imageSize.height;
		double width = cam.imageSize.width;
		double aspectRatio = width / height;
		double distance  = cam.from.distance(cam.to);
		
		double t = Math.tan(Math.toRadians(cam.fovy)/2.0) * distance;
		double l = -aspectRatio*t;
		double r = aspectRatio*t;
		double b = -t;
		double U = l + (r - l) * (i+offset[0]) / width; 
		double V = b + (t - b) * (j+offset[1]) / height; 

		Vector3d w = new Vector3d(cam.from);
		w.sub(cam.to);
		w.normalize();	

		Vector3d u = new Vector3d();
		u.cross(cam.up, w);
		u.normalize();
		
		Vector3d v = new Vector3d();
		v.cross(u, w);
		v.normalize();				

		Vector3d viewDirection = new Vector3d(
				(U*u.x) + (V*v.x) - (distance*w.x), 
				(U*u.y) + (V*v.y) - (distance*w.y), 
				(U*u.z) + (V*v.z) - (distance*w.z));
		viewDirection.normalize();
		ray.set(cam.from, viewDirection);
	} 
	
	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param surface The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	public static boolean inShadow(final IntersectResult result, final Light light, final Intersectable surface, IntersectResult shadowResult, Ray shadowRay) {
		
		// TODO: Objective 5: check for shdows and use it in your lighting computation

		Vector3d t = new Vector3d(light.from);
        t.sub(result.p);
        t.normalize();
        Point3d p = new Point3d(t);
        p.scale(1e-9);
        p.add(result.p);
        shadowRay.set(p, t);
        
        for (Intersectable childNode : ((SceneNode)surface).children) {
        	childNode.intersect(shadowRay, shadowResult);
            if(shadowResult.t != Double.POSITIVE_INFINITY && shadowResult.t > 0) { 
            	double lightDistance = light.from.distance(p);
            	double shadowDistance = shadowResult.p.distance(p);
                if(shadowDistance < lightDistance)
                    return true;
            }
        }
		return false;
	}  
}
