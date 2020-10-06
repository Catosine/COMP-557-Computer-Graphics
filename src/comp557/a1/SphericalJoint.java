package comp557.a1;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

import javax.vecmath.Tuple3d;

public class SphericalJoint extends GraphNode {

	double tx = 0;
	
	double ty = 0;
	
	double tz = 0;
	
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
		
	public SphericalJoint( String name) {
		super(name);
		dofs.add( rx = new DoubleParameter( name+" rx", 0, -180, 180 ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, -180, 180 ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, -180, 180 ) );
	}
	
	public void setRx(Tuple3d t) {
		rx.setDefaultValue(t.x);
		rx.setMinimum(t.y);
		rx.setMaximum(t.z);
	}
	
	public void setRy(Tuple3d t) {
		ry.setDefaultValue(t.x);
		ry.setMinimum(t.y);
		ry.setMaximum(t.z);
	}
	
	public void setRz(Tuple3d t) {
		rz.setDefaultValue(t.x);
		rz.setMinimum(t.y);
		rz.setMaximum(t.z);
	}
	
	public void setPosition(Tuple3d t) {
		this.tx = t.x;
		this.ty = t.y;
		this.tz = t.z;
	}
	
	public void setRotation(Tuple3d t) {
		this.rx.setDefaultValue(t.x);
		this.ry.setDefaultValue(t.y);
		this.rz.setDefaultValue(t.z);
	}
	
	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {		
		pipeline.push();// save the previous state of transformation matrices
		
		// TODO: Objective 3: Sphericaljoint, transformations must be applied before drawing children
		// translate first
		pipeline.translate(tx, ty, tz);
		
		// rotate the second
		// following the yaw-pitch-row
		pipeline.rotate(ry.getValue()*Math.PI/180.0, 0, 1, 0);
		pipeline.rotate(rx.getValue()*Math.PI/180.0, 1, 0, 0);
		pipeline.rotate(rz.getValue()*Math.PI/180.0, 0, 0, 1);
		
		pipeline.setModelingMatrixUniform(drawable.getGL().getGL4());
		
		super.display( drawable, pipeline );
		pipeline.pop();// restore the previous state of transformation matrices
	}
	
}