package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a1.geom.Cube;
import comp557.a1.geom.Sphere;
import mintools.parameters.DoubleParameter;

public class BodySphere extends GraphNode {

	DoubleParameter eulerX;
	DoubleParameter eulerY;
	DoubleParameter eulerZ;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
	double[] translationArray = new double[]{0, 0, 0};
	private double[] scaleArray = new double[]{1, 1, 1};
	private float[] colorArray = new float[] {1f, 1f, 1f};
		
	
	public BodySphere(String name) {
		super(name);
	}

	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		pipeline.push();
		
		// TODO: Objective 3: Freejoint, transformations must be applied before drawing children
		pipeline.setModelingMatrixUniform(drawable.getGL().getGL4());
		pipeline.translate(translationArray[0], translationArray[1], translationArray[2]);
		pipeline.scale(scaleArray[0], scaleArray[1], scaleArray[2]);
		pipeline.setColor(drawable.getGL().getGL4(), colorArray[0], colorArray[1], colorArray[2]);
		Sphere.draw(drawable, pipeline);
		super.display( drawable, pipeline );	
		pipeline.pop();
	}

	public void setCentre(Tuple3d t) {
		this.translationArray = new double[]{t.x, t.y, t.z};
		
	}

	public void setScale(Tuple3d t) {
		this.scaleArray = new double[]{t.x, t.y, t.z};
		
	}

	public void setColor(Tuple3d t) {
		this.colorArray = new float[]{(float) t.x, (float) t.y, (float) t.z};
		
	}
	
}
