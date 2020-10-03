package comp557.a1;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class RotaryJoint extends GraphNode{
	/**
	 * This class allows to create a joint that can rotate at its local coordinates
	 */
	
	double tx_max = 2;
	double tx_min = -2;
	double tx = 0;
	
	double ty_max = 2;
	double ty_min = -2;
	double ty = 0;
	
	double tz_max = 2;
	double tz_min = -2;
	double tz = 0;
	
	DoubleParameter rot;
	
	int x = 0;
	int y = 0;
	int z = 0;
	
	public RotaryJoint( String name, String axis, double tx, double ty, double tz) {
		super(name);
		
		if (tx<=tx_max && tx>=tx_min) {
			this.tx = tx;
		}
		if (ty<=ty_max && ty>=ty_min) {
			this.ty = ty;
		}
		if (tz<=tz_max && tz>=tz_min) {
			this.tz = tz;
		}
		
		dofs.add( rot = new DoubleParameter( name+" rot", 0, -180, 180 ) );
		
		if (axis == "x") {
			x = 1;
		} else if (axis == "y") {
			y = 1;
		} else if (axis == "z") {
			z = 1;
		} else {
			x = 1;
			System.out.println("Wrong input. Only support x/t/z. By default it would be x-axis");
		}
	}
	
	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		pipeline.push();// save the previous state of transformation matrices
		
		// translate first
		pipeline.translate(tx, ty, tz);
		
		// following the yaw-pitch-row (y-z-x)
		// rot
		pipeline.rotate(rot.getValue()*Math.PI/180.0, this.x, this.y, this.z);
				
		pipeline.setModelingMatrixUniform(drawable.getGL().getGL4());

		super.display( drawable, pipeline );
		pipeline.pop();// restore the previous state of transformation matrices
	}
}
