package comp557.a1.geom;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a1.BasicPipeline;

public class Cube {
	public static void draw( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		// silly way to do it... and might get some tearing if we are not careful?
		pipeline.push();
		pipeline.scale(.5,.5,.5);
		pipeline.translate(0,0,1);
		Quad.draw(drawable, pipeline);
		pipeline.pop();
		pipeline.push();
		pipeline.rotate(Math.PI/2,0,1,0);
		pipeline.scale(.5,.5,.5);
		pipeline.translate(0,0,1);
		Quad.draw(drawable, pipeline);
		pipeline.pop();
		pipeline.push();
		pipeline.rotate(Math.PI,0,1,0);
		pipeline.scale(.5,.5,.5);
		pipeline.translate(0,0,1);
		Quad.draw(drawable, pipeline);
		pipeline.pop();
		pipeline.push();
		pipeline.rotate(-Math.PI/2,0,1,0);
		pipeline.scale(.5,.5,.5);
		pipeline.translate(0,0,1);
		Quad.draw(drawable, pipeline);
		pipeline.pop();
		pipeline.push();
		pipeline.rotate(Math.PI/2,1,0,0);
		pipeline.scale(.5,.5,.5);
		pipeline.translate(0,0,1);
		Quad.draw(drawable, pipeline);
		pipeline.pop();
		pipeline.push();
		pipeline.rotate(-Math.PI/2,1,0,0);
		pipeline.scale(.5,.5,.5);
		pipeline.translate(0,0,1);
		Quad.draw(drawable, pipeline);
		pipeline.pop();
	}
}
