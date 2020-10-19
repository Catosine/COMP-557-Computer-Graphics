package comp557.a2.geom;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;

import comp557.a2.ShadowPipeline;

public class WireCube {
	private static boolean initialized = false;
	private static int positionBufferID;
	private static int elementBufferID;

	public static void draw( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		GL4 gl = drawable.getGL().getGL4();
		if ( !initialized ) {
			initialized = true;
			FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer( new float[] {
		    	    -1, -1,  1,
		    	     1, -1,  1,
		    	     1,  1,  1,
		    	    -1,  1,  1,
		    	    -1, -1, -1,
		    	     1, -1, -1,
		    	     1,  1, -1,
		    	    -1,  1, -1,
		    	  });
			ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer( new short [] {
					0,1,
					1,2,
					2,3,
					3,0,
					4,5,
					5,6,
					6,7,
					7,4,
					0,4,
					1,5,
					2,6,
					3,7,	
			});
			vertexBuffer.position(0);
			indexBuffer.position(0);
			int[] bufferIDs = new int[2];
			gl.glGenBuffers( 2, bufferIDs, 0 );
			positionBufferID = bufferIDs[0];
			elementBufferID = bufferIDs[1];
			gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
			gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_STATIC_DRAW );
			gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
			gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Short.BYTES, indexBuffer, GL4.GL_STATIC_DRAW );
		} else {
			pipeline.disableLighting( drawable );
			pipeline.currentGLSLProgram.bindPositionBuffer(gl, positionBufferID);
			gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
    		gl.glDrawElements( GL4.GL_LINES, 24, GL4.GL_UNSIGNED_SHORT, 0 );			
			pipeline.enableLighting( drawable );
		}

	}
}
