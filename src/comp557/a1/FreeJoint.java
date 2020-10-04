package comp557.a1;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

import javax.vecmath.Tuple3d;

public class FreeJoint extends GraphNode {

	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
		
	public FreeJoint( String name) {
		super(name);
		dofs.add( tx = new DoubleParameter( name+" tx", 0, -2, 2 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -2, 2 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -2, 2 ) );
		dofs.add( rx = new DoubleParameter( name+" rx", 0, -180, 180 ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, -180, 180 ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, -180, 180 ) );
	}
	
	public void setPosition(Tuple3d t) {
		this.tx.setDefaultValue(t.x);
		this.ty.setDefaultValue(t.y);
		this.tz.setDefaultValue(t.z);
	}
	
	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {		
		pipeline.push();// save the previous state of transformation matrices
		
		// TODO: Objective 3: Freejoint, transformations must be applied before drawing children
		// translate first
		pipeline.translate(tx.getFloatValue(), ty.getFloatValue(), tz.getFloatValue());
		
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
