/**
 * COMP 557 - Assig 2
 * @author Sabina Sasu, 260803977
 */

package comp557.a2;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;

/** 
 * Left Mouse Drag Arcball
 * @author kry
 */
public class ArcBall {
		
	private DoubleParameter fit = new DoubleParameter( "Fit", 1, 0.5, 2 );
	private DoubleParameter gain = new DoubleParameter( "Gain", 1, 0.5, 2, true );
	
	/** The accumulated rotation of the arcball */
	Matrix4d R = new Matrix4d();

	public ArcBall() {
		R.setIdentity();
	}
	
	Vector3d clickVector = new Vector3d();
	Vector3d dragVector = new Vector3d();
	
	/** 
	 * Convert the x y position of the mouse event to a vector for your arcball computations 
	 * @param e
	 * @param v
	 */
	public void setVecFromMouseEvent( MouseEvent e, Vector3d v ) {
		Component c = e.getComponent();
		Dimension dim = c.getSize();
		double width = dim.getWidth();
		double height = dim.getHeight();
		int mousex = e.getX();
		int mousey = e.getY();
		
		// TODO: Objective 1: finish arcball vector helper function
		double radius = Math.min(width, height)/fit.getValue();
		
		double pointx = (mousex - width/2.0)/radius;
		double pointy = (mousey - height/2.0)/radius;
		double r = (pointx * pointx) + (pointy * pointy);
		
		if (r > Math.pow(radius, 2)) { 
			double s = 1.0 / Math.sqrt(r);
			v.x = pointx*s;
			v.y = pointy*s;
			v.z = 0;
		} 
		else {
			v.x = pointx;
			v.y = pointy;
			v.z = Math.sqrt(1.0-r);
		}
	}
		
	public void attach( Component c ) {
		c.addMouseMotionListener( new MouseMotionListener() {			
			@Override
			public void mouseMoved( MouseEvent e ) {}
			@Override
			public void mouseDragged( MouseEvent e ) {				
				if ( (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 ) {
					// TODO: Finish arcball rotation update on mouse drag when button 1 down!
					
					setVecFromMouseEvent(e, dragVector);
					Vector3d Perp = new Vector3d();
					Perp.cross(clickVector, dragVector);
					AxisAngle4d newRot = new AxisAngle4d( 0, 0, 0, Math.acos(0)*gain.getValue() );
					if (Perp.length() > 0) {
						newRot.x = Perp.x;
						newRot.y = Perp.y;
						newRot.z = Perp.z;
						newRot.angle =  Math.acos(clickVector.dot(dragVector))*gain.getValue();
						
					}
					Matrix4d temp = new Matrix4d();
					temp.set(newRot);
					R.mul(temp); 
				}
			}
		});
		c.addMouseListener( new MouseListener() {
			@Override
			public void mouseReleased( MouseEvent e) {}
			@Override
			public void mousePressed( MouseEvent e) {
				// TODO: Objective 1: arcball interaction starts when mouse is clickedcenter
				setVecFromMouseEvent( e, clickVector );
			}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
	}
	
	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();
		vfp.setBorder( new TitledBorder("ArcBall Controls"));
		vfp.add( fit.getControls() );
		vfp.add( gain.getControls() );
		return vfp.getPanel();
	}
		
}
