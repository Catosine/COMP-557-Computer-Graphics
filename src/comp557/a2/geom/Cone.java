package comp557.a2.geom;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;

import comp557.a2.ShadowPipeline;

/** For drawing cylinders with caps */
public class Cone {
	private static boolean initialized = false;
	private static int positionBufferID;
	private static int normalBufferID;
	private static int elementBufferID;
	
	private final static int slices = 32;
	private final static int stacks = 16;
		
	/** 
	 * Draws a sphere of unit radius
	 * @param drawable
	 * @param pipeline
	 */
	public static void draw( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		GL4 gl = drawable.getGL().getGL4();
		if ( ! initialized ) {
			initialized = true;
			
			int numVerts = (slices * stacks + 2) * 3;		
			int numIndices = (stacks + 1) * 2 * slices + (slices + 1) * 2;	    
			
			FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer( numVerts );
			FloatBuffer normalBuffer = GLBuffers.newDirectFloatBuffer( numVerts );
	        ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer( numIndices );
	        
	        float nx = (float) (2 / Math.sqrt(5));
	        float nz = (float) (1 / Math.sqrt(5));
	        
	        for ( int i = slices-1 ; i >= 0; i-- ) {
		    	float c = (float) Math.cos( Math.PI * 2 * i / slices );
		    	float s = (float) Math.sin( Math.PI * 2 * i / slices );	    	
		    	for ( int j = 0; j < stacks; j++ ) {
		    		float h = (float)(j/(stacks-1.0));
		    		vertexBuffer.put( c * h );
		    		vertexBuffer.put( s * h );
		    		vertexBuffer.put( 2-h*2 );		    		
		    		normalBuffer.put( c * nx  );
		    		normalBuffer.put( s * nx );
		    		normalBuffer.put( nz );
		    	}
		    }

			// first bunch of indices are the strips for the sides, then the second term is for the two caps
		    int N = slices * stacks;
			for ( int i = 0; i < slices; i++ ) {
				for ( int j = 0; j < stacks; j++ ) {
					indexBuffer.put( (short) ( i*stacks + j ) );
					indexBuffer.put( (short) ( (i*stacks + j + stacks) % N ) );
				}
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
		    for ( int i = 0; i < slices; i++) {
				gl.glDrawElements( GL4.GL_TRIANGLE_STRIP, 2*stacks, GL4.GL_UNSIGNED_SHORT, i*(2*stacks) * Short.BYTES );
			}
		    // draw the bottom cap
			pipeline.push();
			pipeline.rotate( drawable, Math.PI, 1, 0, 0) ; // this is a pretty dumb way to draw the bottom cap.
		    Disc.draw( drawable, pipeline );
		    pipeline.pop( drawable );
		}
	}
}
