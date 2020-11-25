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

        for ( int j = 0; j < h && !render.isDone(); j++ ) {
            for ( int i = 0; i < w && !render.isDone(); i++ ) {
            	
                // TODO: Objective 1: generate a ray (use the generateRay method)
            	Ray ray = new Ray();
            	double[] offset = {0.5, 0.5}; 
				generateRay(i, j, offset, cam, ray);
				Color3f shading = new Color3f(0f, 0f, 0f);
                
				
				IntersectResult intersectResult = new IntersectResult();  
				for (Intersectable surface : surfaceList) {
					// TODO: Objective 2: test for intersection with scene surfaces
                    surface.intersect(ray, intersectResult);
                    
                    // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
                    
    				if(intersectResult.t != Double.POSITIVE_INFINITY) {	
    					//TODO: modify
    					//Ambient Shading
    					shading.x += ambient.x * intersectResult.material.diffuse.x;
    					shading.y += ambient.y * intersectResult.material.diffuse.y;
    					shading.z += ambient.z * intersectResult.material.diffuse.z;
    					
    					for(Light light: lights.values()){

    						Ray shadowRay = new Ray();
    						IntersectResult shadowResult = new IntersectResult();
    							
    						if(!SceneNode.nodeMap.containsKey("root") || !inShadow(intersectResult, light, SceneNode.nodeMap.get("root"), shadowResult, shadowRay)){
    							//Vector from point to light source
    							Vector3d l = new Vector3d(light.from);
    							l.sub(intersectResult.p);
    							l.normalize();

    							//Lambertian Shading
    							shading.x += (float)(light.color.x*light.power*intersectResult.material.diffuse.x*Math.max(0, intersectResult.n.dot(l)));
    							shading.y += (float)(light.color.y*light.power*intersectResult.material.diffuse.y*Math.max(0, intersectResult.n.dot(l)));
    							shading.z += (float)(light.color.z*light.power*intersectResult.material.diffuse.z*Math.max(0, intersectResult.n.dot(l)));

    							//Bisector vector
    							Vector3d h1 = new Vector3d(ray.viewDirection);
    							h1.negate();
    							h1.add(l);
    							h1.normalize();
    							
    							//Blinn-Phong Shading
    							shading.x += (float)(light.power * intersectResult.material.specular.x * Math.pow(Math.max(0, h1.dot(intersectResult.n)), intersectResult.material.shinyness));
    							shading.y += (float)(light.power * intersectResult.material.specular.y * Math.pow(Math.max(0, h1.dot(intersectResult.n)), intersectResult.material.shinyness));
    							shading.z += (float)(light.power * intersectResult.material.specular.z * Math.pow(Math.max(0, h1.dot(intersectResult.n)), intersectResult.material.shinyness));
    						}
    					}
    				}
    				else
    					shading.add(render.bgcolor);
				}
                    
                
            	// Here is an example of how to calculate the pixel value.
				//shading.scale(1 / ((float) Math.pow(n, 2)));
				//shading.clamp(0, 1);
				int r = (int)(255*shading.x > 255? 255: 255*shading.x);
                int g = (int)(255*shading.y > 255? 255: 255*shading.y);
                int b = (int)(255*shading.z > 255? 255: 255*shading.z);
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
		shadowRay.eyePoint = result.p;
		shadowRay.viewDirection.sub(light.from,  result.p);
		surface.intersect(shadowRay, shadowResult);
		if(shadowResult.t != Double.POSITIVE_INFINITY ){
			double intersectDistance = (shadowResult.p).distance(result.p);
			if(intersectDistance > 0){
				return true;
			}
		}
		return false;
	}  
}
