package comp557.a1;

import javax.vecmath.Matrix4d;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class RotaryJoint extends GraphNode {

	String axis;
	DoubleParameter rotation;
	Matrix4d translation;
	double[] translationArray;
		
	public RotaryJoint( String name, double[] translation, String rotationAxis, double minRotation, double maxRotation) {
		super(name);
		this.axis = rotationAxis;
		translationArray = translation;
		dofs.add( rotation = new DoubleParameter( name+" r" + axis, 0, minRotation, maxRotation ) );	
	}
	
	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		pipeline.push();
		
		// TODO: Objective 3: Freejoint, transformations must be applied before drawing children
		pipeline.setModelingMatrixUniform(drawable.getGL().getGL4());
		pipeline.translate(translationArray[0], translationArray[1], translationArray[2]);
		switch(axis) {
			case "x": pipeline.rotate(Math.toRadians((double)rotation.getValue()), 1, 0, 0); break;
			case "y": pipeline.rotate(Math.toRadians((double)rotation.getValue()), 0, 1, 0); break;
			case "z": pipeline.rotate(Math.toRadians((double)rotation.getValue()), 0, 0, 1); break;
		}
		
		super.display( drawable, pipeline );	
		pipeline.pop();
	}
	
}
