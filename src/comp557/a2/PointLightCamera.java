package comp557.a2;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a2.geom.FancyAxis;
import comp557.a2.geom.QuadWithTexCoords;
import comp557.a2.geom.WireCube;
import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;

public class PointLightCamera extends Camera {	

    public BooleanParameter debugLightFrustum = new BooleanParameter( "debug light frustum" , true );
    //public DoubleParameter sigma = new DoubleParameter( "self shadowing offset", 0.0015, 0, 0.1 );
    public DoubleParameter sigma = new DoubleParameter( "self shadowing offset", 0.0015, 0, 0.002 );
    
    public PointLightCamera() {
    	super();
    	position.set( new Vector3d(3, 3, 3) );
    	fovy.setDefaultValue(55.0);
    }
    
    public void getPositionInWorld( Vector4d pos ) {
    	pos.set( position.x, position.y, position.z, 1 );
    }
    
    Matrix4d Vinv = new Matrix4d();
    Matrix4d Pinv = new Matrix4d();
    
    public void draw( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
    	if ( !debugLightFrustum.getValue() ) return;

    	// TODO: Objective 4,5,6: Note lighting disabled for drawing things that should only be drawn in a solid colour using kd.
    	if(V.determinant()!=0)
			Vinv.invert(this.V);
		if(P.determinant()!=0)
			Pinv.invert(this.P);
		pipeline.disableLighting(drawable);

    	// TODO: Objective 4: draw the light frame using a fancy axis... You must set up the right transformation!
		pipeline.push();
		pipeline.multMatrix(drawable, Vinv);
		FancyAxis.draw(drawable, pipeline);
		pipeline.pop(drawable);


		// TODO: Objective 5: draw the light camera frustum using the inverse projection with a wire cube. You must set up the right transformation!
		// apply inverse view and inverse projection matrices for the light view on the modelview matrix stack, and then draw a wire cube that is 2 units across.
		pipeline.push();
		Matrix4d temp = new Matrix4d();
		temp.mul(P, V);
		temp.invert();
		if(temp.determinant() != 0) {
			pipeline.multMatrix(drawable, temp);
			pipeline.scale(drawable, 2, 2, 2);
			
		}
		pipeline.setkd( drawable, 1, 1, 1 );
		WireCube.draw( drawable, pipeline );
		pipeline.pop(drawable);
		 double tanHFov = Math.atan(Math.toRadians(fovy.getValue()) / 2);
    	// double aspectRatio = width / height;
    	 
    	 //double hNearWidth = tanHFov * near.getValue() * aspectRatio;
    	 double hNearHeight = tanHFov * near.getValue();
    	 
		// TODO: Objective 5: draw the light view on the near plane of the frustum. You must set up the right transformation! 
		// That is, translate and scale the x and y directions of the -1 to 1 quad so that the quad fits exactly the l r t b portion of the near plane
		pipeline.push();
		//pipeline.multMatrix(drawable, Vinv);
		pipeline.multMatrix(drawable, temp);
		pipeline.translate(drawable, 0, 0, -near.getValue());
		pipeline.scale(drawable, 1, 1, -1);
		
		pipeline.debugLightTexture(drawable);
		QuadWithTexCoords.draw( drawable, pipeline );
		pipeline.pop(drawable);
    }
    
    /**
     * @return controls for the shadow mapped light
     */
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.setBorder(new TitledBorder("Point Light Camera Controls"));
        vfp.add( super.getControls() );
        vfp.add( sigma.getControls() );
        vfp.add( debugLightFrustum.getControls() );        
        return vfp.getPanel();
    }
    
}
