package comp557.a1;

import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;

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
	
	public RotaryJoint(String name) {
		super(name);
	}

	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		pipeline.push();
		
		// TODO: Objective 3: Freejoint, transformations must be applied before drawing children
		pipeline.setModelingMatrixUniform(drawable.getGL().getGL4());
		pipeline.translate(translationArray[0], translationArray[1], translationArray[2]);
		switch(axis) {
			case "0": pipeline.rotate(Math.toRadians((double)rotation.getValue()), 1, 0, 0); break;
			case "1": pipeline.rotate(Math.toRadians((double)rotation.getValue()), 0, 1, 0); break;
			case "2": pipeline.rotate(Math.toRadians((double)rotation.getValue()), 0, 0, 1); break;
		}
		
		super.display( drawable, pipeline );	
		pipeline.pop();
	}

	public void setPosition(Tuple3d t) {
		this.translationArray = new double[]{t.x, t.y, t.z};
	}

	public void setAxis(Tuple3d tuple3dAttr) {
		this.axis = Integer.toString((int) tuple3dAttr.x);
		dofs.add( rotation = new DoubleParameter( name+" r" + axis, 0,  tuple3dAttr.y,  tuple3dAttr.z ) );	
		
	}
	
}
