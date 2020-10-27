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
	Matrix4d m = new Matrix4d();
	
	Vector3d p0 = new Vector3d();
	boolean cont_drag = false;

	public ArcBall() {
		R.setIdentity();
		m.setIdentity();
	}
	
	/** 
	 * Convert the x y position of the mouse event to a vector for your arcball computations 
	 * @param e
	 * @param v
	 */
	public void setVecFromMouseEvent( MouseEvent e, Vector3d v ) {
		Component c = e.getComponent();
		Dimension dim = c.getSize();
		double width = dim.getWidth(); // width of the window
		double height = dim.getHeight(); // height of the window
		int mousex = e.getX();
		int mousey = e.getY();
		
		// TODO: Objective 1: finish arcball vector helper function
		double center_x = width/2;
		double center_y = height/2;
		double radius = (width > height) ? height : width;
		radius /= fit.getFloatValue();
		
		double pt_x = (mousex-center_x)/radius;
		double pt_y = (center_y-mousey)/radius;
		double pt_z = 0;
		
		double distance = Math.pow(pt_x, 2)  + Math.pow(pt_y, 2);
		
		if(distance > 1) 
		{
			pt_x /= Math.sqrt(distance);
			pt_y /= Math.sqrt(distance);
		}
		else
		{
			pt_z = Math.sqrt(1.0 - distance);
		}
		
		v.set(new double[] {pt_x, pt_y, pt_z});
		v.normalize();
		
	}
		
	public void attach( Component c ) {
		c.addMouseMotionListener( new MouseMotionListener() {			
			@Override
			public void mouseMoved( MouseEvent e ) {}
			@Override
			public void mouseDragged( MouseEvent e ) {				
				if ( (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 ) {
					// TODO: Objective 1: Finish arcball rotation update on mouse drag when button 1 down!					
					Matrix4d copy_m = new Matrix4d(m);
					if(cont_drag) {
						m.invert();
						R.mul(m);
					}
					Vector3d p1 = new Vector3d();
					setVecFromMouseEvent(e, p1);
					Vector3d axis = new Vector3d();
					axis.cross(p0, p1);
					axis.normalize();
					double radian = Math.acos(p0.dot(p1)) * gain.getFloatValue();
					cont_drag = radian!=0;
					AxisAngle4d temp = new AxisAngle4d(axis, radian);
					m.set(temp);
					R.mul(m);
				}
			}
		});
		c.addMouseListener( new MouseListener() {
			@Override
			public void mouseReleased( MouseEvent e) {}
			@Override
			public void mousePressed( MouseEvent e) {
				// TODO: Objective 1: arcball interaction starts when mouse is clicked
				setVecFromMouseEvent(e, p0);
				cont_drag = false;
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
