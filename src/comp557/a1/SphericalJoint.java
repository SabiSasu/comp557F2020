package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class SphericalJoint extends GraphNode {

	DoubleParameter eulerX;
	DoubleParameter eulerY;
	DoubleParameter eulerZ;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
	double[] translationArray;
		
	public SphericalJoint( String name, double[] translation ) {
		super(name);
		this.translationArray = translation;
		dofs.add( rx = new DoubleParameter( name+" rx", 0, -180, 180 ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, -180, 180 ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, -180, 180 ) );
	}
	
	public SphericalJoint(String name) {
		// TODO Auto-generated constructor stub
		super(name);
	}

	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		pipeline.push();
		
		// TODO: Objective 3: Freejoint, transformations must be applied before drawing children
		pipeline.setModelingMatrixUniform(drawable.getGL().getGL4());
		pipeline.translate(translationArray[0], translationArray[1], translationArray[2]);
		pipeline.rotate(Math.toRadians((double)rx.getValue()), 1, 0, 0);
		pipeline.rotate(Math.toRadians((double)ry.getValue()), 0, 1, 0);
		pipeline.rotate(Math.toRadians((double)rz.getValue()), 0, 0, 1);
		
		super.display( drawable, pipeline );	
		pipeline.pop();
	}

	public void setPosition(Tuple3d t) {
		this.translationArray = new double[]{t.x, t.y, t.z};
		dofs.add( rx = new DoubleParameter( name+" rx", 0, -180, 180 ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, -180, 180 ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, -180, 180 ) );
		
	}
	
}
