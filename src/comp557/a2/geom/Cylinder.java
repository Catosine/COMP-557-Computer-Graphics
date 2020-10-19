package comp557.a2.geom;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;

import comp557.a2.ShadowPipeline;

/** For drawing cylinders with caps */
public class Cylinder {
	private static boolean initialized = false;
	private static int positionBufferID;
	private static int normalBufferID1;
	private static int normalBufferID2;
	private static int elementBufferID;
	
	private final static int slices = 32;
	
	private static int bottomCapStart;
	private static int topCapStart;
	
	/** 
	 * Draws a sphere of unit radius
	 * @param drawable
	 * @param pipeline
	 */
	public static void draw( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		GL4 gl = drawable.getGL().getGL4();
		if ( ! initialized ) {
			initialized = true;
			
			int numVerts = (slices * 2 + 2) * 3;		
			int numIndices = 2 * slices + (slices + 1) * 2;	    
			
			FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer( numVerts );
			FloatBuffer normalBuffer1 = GLBuffers.newDirectFloatBuffer( numVerts );
			FloatBuffer normalBuffer2 = GLBuffers.newDirectFloatBuffer( numVerts );
	        ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer( numIndices );
	        	        
	        for ( int i = slices-1 ; i >= 0; i-- ) {
		    	float c = (float) Math.cos( Math.PI * 2 * i / (slices-1) );
		    	float s = (float) Math.sin( Math.PI * 2 * i / (slices-1) );	    	
	    		vertexBuffer.put( c );
	    		vertexBuffer.put( s );
	    		vertexBuffer.put( -1 );
	    		vertexBuffer.put( c );
	    		vertexBuffer.put( s );
	    		vertexBuffer.put( 1 );
	    		
	    		normalBuffer1.put( c );
	    		normalBuffer1.put( s );
	    		normalBuffer1.put( 0 );
	    		
	    		normalBuffer1.put( c );
	    		normalBuffer1.put( s );
	    		normalBuffer1.put( 0 );
	    		
	    		normalBuffer2.put( 0 );
	    		normalBuffer2.put( 0 );
	    		normalBuffer2.put( -1 );
	    		
	    		normalBuffer2.put( 0 );
	    		normalBuffer2.put( 0 );
	    		normalBuffer2.put( 1 );
	        }
	        
			vertexBuffer.put(  0 );
			vertexBuffer.put(  0 );
			vertexBuffer.put( -1 );
			vertexBuffer.put(  0 );
			vertexBuffer.put(  0 );
			vertexBuffer.put(  1 );
    		
			normalBuffer2.put( 0 );
    		normalBuffer2.put( 0 );
    		normalBuffer2.put( -1 );
    		normalBuffer2.put( 0 );
    		normalBuffer2.put( 0 );
    		normalBuffer2.put( 1 );
		    
			// now the indices
			
			// first bunch of indices are the strips for the sides, then the second term is for the two caps
		    for ( int i = 0; i < slices*2; i++ ) {
				indexBuffer.put( (short) i );
			}		
			
			bottomCapStart = indexBuffer.position();
			indexBuffer.put( (short) (slices*2) );
			for ( int i = 0; i < slices; i++ ) {
				indexBuffer.put( (short) (i*2) );
			}		
			
			topCapStart = indexBuffer.position();
			indexBuffer.put( (short) (slices*2+1) );
			//for ( int i = 0; i < slices; i++ ) {
			for ( int i = slices-1; i >= 0; i-- ) {
				indexBuffer.put( (short) (i*2+1) );
			}		

		    vertexBuffer.position(0);
		    normalBuffer1.position(0);
		    normalBuffer2.position(0);
		    indexBuffer.position(0);
		    
	        int[] bufferIDs = new int[4];
	        gl.glGenBuffers( 4, bufferIDs, 0 );
	        positionBufferID = bufferIDs[0];
	        normalBufferID1 = bufferIDs[1];
	        normalBufferID2 = bufferIDs[2];
	        elementBufferID = bufferIDs[3];
	        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
	        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_STATIC_DRAW );
	        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID1 );
	        gl.glBufferData( GL4.GL_ARRAY_BUFFER, normalBuffer1.capacity() * Float.BYTES, normalBuffer1, GL4.GL_STATIC_DRAW );
	        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID2 );
	        gl.glBufferData( GL4.GL_ARRAY_BUFFER, normalBuffer2.capacity() * Float.BYTES, normalBuffer2, GL4.GL_STATIC_DRAW );
	        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
	        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Short.BYTES, indexBuffer, GL4.GL_STATIC_DRAW );
		} else {			
			pipeline.currentGLSLProgram.bindPositionBuffer(gl, positionBufferID);
			pipeline.currentGLSLProgram.bindNormalBuffer(gl, normalBufferID1);
			gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
			gl.glDrawElements( GL4.GL_TRIANGLE_STRIP, 2*slices, GL4.GL_UNSIGNED_SHORT, 0 );
			pipeline.currentGLSLProgram.bindNormalBuffer(gl, normalBufferID2);
		    gl.glDrawElements( GL4.GL_TRIANGLE_FAN, slices+1, GL4.GL_UNSIGNED_SHORT, bottomCapStart * Short.BYTES );
		    gl.glDrawElements( GL4.GL_TRIANGLE_FAN, slices+1, GL4.GL_UNSIGNED_SHORT, topCapStart * Short.BYTES );	  
		}
	}
}
