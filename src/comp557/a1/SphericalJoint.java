package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;
/**
 * COMP 557 - Assig 1
 * @author Sabina Sasu, 260803977
 */
public class SphericalJoint extends GraphNode {

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
		super(name);
	}

	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		pipeline.push();
		
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
		
	}
	
	public void setAxisX(Tuple3d t) {
		dofs.add( rx = new DoubleParameter( name+" rx", t.x, t.y, t.z ) );
		
	}
	
	public void setAxisY(Tuple3d t) {
		dofs.add( ry = new DoubleParameter( name+" ry", t.x, t.y, t.z ) );
		
	}
	
	public void setAxisZ(Tuple3d t) {
		dofs.add( rz = new DoubleParameter( name+" rz", t.x, t.y, t.z ) );
		
	}
	
}
