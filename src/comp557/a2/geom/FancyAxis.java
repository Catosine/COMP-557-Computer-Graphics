package comp557.a2.geom;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a2.ShadowPipeline;

public class FancyAxis {
	
	/**
	 * Draws a unit sized axis 
	 * (Note drawing code is a bit dumb and inefficient)
	 * @param drawable
	 * @param pipeline
	 */
	public static void draw( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		pipeline.push();
		pipeline.scale(drawable,0.1, 0.1, 0.1);
		pipeline.setkd(drawable, 0.7, 0.7, 0.7);
		Sphere.draw( drawable, pipeline );		
		pipeline.setkd(drawable, 0, 0, 1);
		drawUnitArrowInZ( drawable, pipeline );
		pipeline.rotate(drawable,-Math.PI/2, 1, 0, 0);
		pipeline.setkd(drawable, 0, 1, 0);
		drawUnitArrowInZ( drawable, pipeline );
		pipeline.rotate(drawable,Math.PI/2, 0, 1, 0);
		pipeline.setkd(drawable, 1, 0, 0);
		drawUnitArrowInZ( drawable, pipeline );
		pipeline.pop(drawable);
	}

	private static void drawUnitArrowInZ( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		pipeline.push();
		pipeline.scale(drawable,0.5,0.5,4);
		pipeline.translate(drawable,0,0,1);
		Cylinder.draw(drawable, pipeline);
		pipeline.pop(drawable);
		pipeline.push();
		pipeline.translate(drawable,0,0,8);
		Cone.draw(drawable,pipeline);
		pipeline.pop(drawable);
	}
	
}
