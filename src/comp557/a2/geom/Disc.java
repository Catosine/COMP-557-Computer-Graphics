package comp557.a2.geom;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;

import comp557.a2.ShadowPipeline;

/** For drawing cylinders with caps */
public class Disc {
	private static boolean initialized = false;
	private static int positionBufferID;
	private static int normalBufferID;
	private static int elementBufferID;
	
	private final static int slices = 32;
		
	/** 
	 * Draws a filled circle of unit radius, positive z normal, at the origin
	 * @param drawable
	 * @param pipeline
	 */
	public static void draw( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		GL4 gl = drawable.getGL().getGL4();
		if ( ! initialized ) {
			initialized = true;
			
			int numVerts = slices * 3;
			int numIndices = slices;
			
			FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer( numVerts );
			FloatBuffer normalBuffer = GLBuffers.newDirectFloatBuffer( numVerts );
	        ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer( numIndices );
			
	        for ( int i = slices-1 ; i >= 0; i-- ) {
		    	float c = (float) Math.cos( Math.PI * 2 * i / slices );
		    	float s = (float) Math.sin( Math.PI * 2 * i / slices );	    	
	    		vertexBuffer.put( c );
	    		vertexBuffer.put( s );
	    		vertexBuffer.put( 0 );
	    		normalBuffer.put( 0 );
	    		normalBuffer.put( 0 );
	    		normalBuffer.put( 1 );	    		
	        }
	        
			// now the indices			
		    for ( int i = 0; i < slices; i++ ) {
				indexBuffer.put( (short) i );
			}		
			
		    vertexBuffer.position(0);
		    normalBuffer.position(0);
		    indexBuffer.position(0);
		    
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
	        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Short.BYTES, indexBuffer, GL4.GL_STATIC_DRAW );
		} else {			
			pipeline.currentGLSLProgram.bindPositionBuffer(gl, positionBufferID);
			pipeline.currentGLSLProgram.bindNormalBuffer(gl, normalBufferID);			
			gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
		    gl.glDrawElements( GL4.GL_TRIANGLE_FAN, slices, GL4.GL_UNSIGNED_SHORT, 0 );
		}
	}
}
