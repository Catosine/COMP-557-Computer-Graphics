package comp557.a2;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
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

	public ArcBall() {
		R.setIdentity();
	}
	
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
		
	}
		
	public void attach( Component c ) {
		c.addMouseMotionListener( new MouseMotionListener() {			
			@Override
			public void mouseMoved( MouseEvent e ) {}
			@Override
			public void mouseDragged( MouseEvent e ) {				
				if ( (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 ) {
					// TODO: Objective 1: Finish arcball rotation update on mouse drag when button 1 down!
				}
			}
		});
		c.addMouseListener( new MouseListener() {
			@Override
			public void mouseReleased( MouseEvent e) {}
			@Override
			public void mousePressed( MouseEvent e) {
				// TODO: Objective 1: arcball interaction starts when mouse is clicked
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
