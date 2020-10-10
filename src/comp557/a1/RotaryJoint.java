/**
 * Pengnan Fan, 260768510
 */
package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class RotaryJoint extends GraphNode{
	/**
	 * This class allows to create a joint that can rotate at its local coordinates
	 */
	
	double tx = 0;
	
	double ty = 0;
	
	double tz = 0;
	
	DoubleParameter rot;
	
	double x = 1;
	double y = 0;
	double z = 0;
	
	public RotaryJoint( String name ) {
		super(name);
		
		dofs.add( rot = new DoubleParameter( name+" rotation", 0, -180, 180 ) );
	}
	
	public void setRot(Tuple3d t) {
		rot.setDefaultValue(t.x);
		rot.setMinimum(t.y);
		rot.setMaximum(t.z);
	}
	
	public void setPosition(Tuple3d t) {
		this.tx = t.x;
		this.ty = t.y;
		this.tz = t.z;
	}
	
	public void setAxis(Tuple3d t) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
	}
	
	public void setRotation(double rot) {
		this.rot.setDefaultValue(rot);
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
