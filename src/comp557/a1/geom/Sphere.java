package comp557.a1.geom;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;

import comp557.a1.BasicPipeline;

public class Sphere {
	private static boolean initialized = false;
	private static int positionBufferID;
	private static int normalBufferID;
	private static int elementBufferID;
	
	private final static int slices = 32;
	private final static int stacks = 16;
	
	private static int bottomCapStart;
	private static int topCapStart;
	
	/** 
	 * Draws a sphere of unit radius
	 * @param drawable
	 * @param pipeline
	 */
	public static void draw( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		GL4 gl = drawable.getGL().getGL4();
		pipeline.setModelingMatrixUniform( gl );
		if ( ! initialized ) {
			initialized = true;
			
			int numVerts = (slices * stacks + 2) * 3;		
			int numIndices = (stacks + 1) * 2 * slices + (slices + 1) * 2;	    
			
			FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer( numVerts );
	        ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer( numIndices );
	        
	        for ( int i = slices-1 ; i >= 0; i-- ) {
		    	float c = (float) Math.cos( Math.PI * 2 * i / slices );
		    	float s = (float) Math.sin( Math.PI * 2 * i / slices );	    	
		    	for ( int j = 0; j < stacks; j++ ) {
		    		float c2 = (float) Math.cos( Math.PI * (j+1) / (stacks+1) );
		    		float s2 = (float) Math.sin( Math.PI * (j+1) / (stacks+1) );
		    		vertexBuffer.put( c*s2 );
		    		vertexBuffer.put( s*s2 );
		    		vertexBuffer.put( c2 );
		    	}
		    }
			vertexBuffer.put(  0 );
			vertexBuffer.put(  0 );
			vertexBuffer.put(  1 );
			vertexBuffer.put(  0 );
			vertexBuffer.put(  0 );
			vertexBuffer.put( -1 );
		    
			// first bunch of indices are the strips for the sides, then the second term is for the two caps
		    int N = slices * stacks;
			for ( int i = 0; i < slices; i++ ) {
				for ( int j = 0; j < stacks; j++ ) {
					indexBuffer.put( (short) ( i*stacks + j ) );
					indexBuffer.put( (short) ( (i*stacks + j + stacks) % N ) );
				}
			}		
			
			bottomCapStart = indexBuffer.position();
			indexBuffer.put( (short) N );
			for ( int i = 0; i < slices; i++ ) {
				indexBuffer.put( (short) ((slices-1-i)*stacks) );
			}		
			indexBuffer.put( (short) ((slices-1)*stacks) );
			
			topCapStart = indexBuffer.position();
			indexBuffer.put( (short) (N+1) );
			for ( int i = slices-1; i >= 0; i-- ) {
				indexBuffer.put( (short) (N-1-i*stacks) );
			}		
			indexBuffer.put( (short) (N-1-(slices-1)*stacks) );

		    vertexBuffer.position(0);        	    
		    indexBuffer.position(0);
		    
	        int[] bufferIDs = new int[3];
	        gl.glGenBuffers( 3, bufferIDs, 0 );
	        positionBufferID = bufferIDs[0];
	        normalBufferID = bufferIDs[1];
	        elementBufferID = bufferIDs[2];
	        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
	        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_STATIC_DRAW );
	        // vertex positions are the normal directions in this case
	        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID );
	        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_STATIC_DRAW );
	        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
	        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Short.BYTES, indexBuffer, GL4.GL_STATIC_DRAW );
		} else {			
			gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );		
			gl.glVertexAttribPointer( pipeline.positionAttributeID, 3, GL4.GL_FLOAT, false, 3*Float.BYTES, 0 );
			gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID );		
		    gl.glVertexAttribPointer( pipeline.normalAttributeID, 3, GL4.GL_FLOAT, false, 3*Float.BYTES, 0 );			
			gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
		    for ( int i = 0; i < slices; i++) {
				gl.glDrawElements( GL4.GL_TRIANGLE_STRIP, 2*stacks, GL4.GL_UNSIGNED_SHORT, i*(2*stacks) * Short.BYTES );
			}
		    gl.glDrawElements( GL4.GL_TRIANGLE_FAN, slices+2, GL4.GL_UNSIGNED_SHORT, bottomCapStart * Short.BYTES );
		    gl.glDrawElements( GL4.GL_TRIANGLE_FAN, slices+2, GL4.GL_UNSIGNED_SHORT, topCapStart * Short.BYTES );	  
		}
	}
}
