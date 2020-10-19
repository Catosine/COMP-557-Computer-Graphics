package comp557.a2.geom;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;

import comp557.a2.ShadowPipeline;

public class Quad {
	
	private static boolean initialized = false;
	private static int positionBufferID;
	private static int normalBufferID;
	private static int elementBufferID;
	
	public static void draw( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		GL4 gl = drawable.getGL().getGL4();
		if ( ! initialized ) {
			initialized = true;
			FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer( new float[] {-1,-1, 0, 1,-1, 0,-1, 1, 0, 1, 1, 0} );
	        FloatBuffer normalBuffer = GLBuffers.newDirectFloatBuffer( new float[] { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1} );
	        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer( new short[] {0,1,2,3} );
	        int[] bufferIDs = new int[3];
	        gl.glGenBuffers( 3, bufferIDs, 0 );
	        positionBufferID = bufferIDs[0];
	        normalBufferID = bufferIDs[1];
	        elementBufferID = bufferIDs[2];
	        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
	        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_STATIC_DRAW );
	        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID );
	        gl.glBufferData( GL4.GL_ARRAY_BUFFER, normalBuffer.capacity() * Float.BYTES, normalBuffer, GL4.GL_STATIC_DRAW );
	        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
	        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, GL4.GL_STATIC_DRAW );
		} else {
			pipeline.currentGLSLProgram.bindPositionBuffer(gl, positionBufferID);
			pipeline.currentGLSLProgram.bindNormalBuffer(gl, normalBufferID);			
			gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
    		gl.glDrawElements( GL4.GL_TRIANGLE_STRIP, 4, GL4.GL_UNSIGNED_SHORT, 0 );
		}
	}
}
