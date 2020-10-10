/**
 * Pengnan Fan, 260768510
 */
package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a1.geom.Cube;
import comp557.a1.geom.Quad;
import comp557.a1.geom.SimpleAxis;
import comp557.a1.geom.Sphere;

public class GeometryNode extends GraphNode {
	
	double tx;
	double ty;
	double tz;
	
	double sx;
	double sy;
	double sz;
	
	double rx;
	double ry;
	double rz;
	
	String type;
	String name;
	
	public GeometryNode(String name, String type) {
		super(name);
		this.name = name;
		this.type = type;
	}
	
	public GeometryNode(String name, String type, double tx, double ty, double tz, double scale) {
		super(name);
		this.name = name;
		this.type = type;
		
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		
		this.sx = scale;
		this.sy = scale;
		this.sz = scale;
	}
	
	public GeometryNode(String name, String type, double tx, double ty, double tz, double sx, double sy, double sz) {
		super(name);
		this.name = name;
		this.type = type;
		
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
	}
	
	public void setCentre(Tuple3d t) {
		this.tx = t.x;
		this.ty = t.y;
		this.tz = t.z;
	}
	
	public void setScale(Tuple3d t) {
		this.sx = t.x;
		this.sy = t.y;
		this.sz = t.z;
	}
	
	public void setRotation(Tuple3d t) {
		this.rx = t.x;
		this.ry = t.y;
		this.rz = t.z;
	}
	
	public void setColor(Tuple3d t) {
		System.out.println("Dummy function");
	}
	
	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		
		pipeline.push();
		
		// translate first
		pipeline.translate(tx, ty, tz);
		pipeline.rotate(ry*Math.PI/180.0, 0, 1, 0);
		pipeline.rotate(rx*Math.PI/180.0, 1, 0, 0);
		pipeline.rotate(rz*Math.PI/180.0, 0, 0, 1);
		pipeline.scale(sx, sy, sz);
		
		// then draw the geometry
		if (type == "cube") {
			Cube.draw(drawable, pipeline);
		} else if (type == "quad") {
			Quad.draw(drawable, pipeline);
		} else if (type == "sphere") {
			Sphere.draw(drawable, pipeline);
		} else if (type == "axis"){
			SimpleAxis.draw(drawable, pipeline);
		}
		
		pipeline.setModelingMatrixUniform(drawable.getGL().getGL4());
		super.display( drawable, pipeline );
		pipeline.pop();
	}
}
