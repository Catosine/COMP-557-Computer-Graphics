package comp557.a1;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a1.geom.Cube;
import comp557.a1.geom.Quad;
import comp557.a1.geom.SimpleAxis;
import comp557.a1.geom.Sphere;

public class GeometryNode extends GraphNode {
	
	double tx;
	double ty;
	double tz;
	
	double scale;
	
	String type;
	
	public GeometryNode(String type, double tx, double ty, double tz, double scale) {
		super(type);
		this.type = type;
		
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		
		this.scale = scale;
	}
	
	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		
		pipeline.push();
		
		// translate first
		pipeline.translate(tx, ty, tz);
		pipeline.scale(scale, scale, scale);
		
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
