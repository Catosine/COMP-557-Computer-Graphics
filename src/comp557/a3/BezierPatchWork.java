/*
 * Pengnan Fan
 * 260768510
 */

package comp557.a3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;

import mintools.parameters.IntParameter;
import mintools.viewer.ShadowPipeline;

/**
 * BezierMesh is a class for loads a collection of Bezier patches and displaying them using OpenGL.
 */
public class BezierPatchWork {
    
	/**
	 * Bezier geometry patches, indexed first by patch number, then by axis.
	 * For instance:
	 * coordinatePatch[0][0] gives the 4 by 4 matrix of x control points for the first patch.
	 * coordinatePatch[0][1] gives the 4 by 4 matrix of y control points for the first patch.
	 * coordinatePatch[0][2] gives the 4 by 4 matrix of z control points for the first patch.
	 */
    private Matrix4d coordinatePatch[][];
    
    /**
     * The number of evaluations that should be made along each parameter direction 
     * when drawing the quad mesh for a given patch.
     */
    public IntParameter subdivisions = new IntParameter( "Bezier Patch Refinement", 12, 2, 24 );
		
    /**
     * @return the number of patches in the loaded model
     */
    public int getNumPatches() {
        return coordinatePatch.length;
    }

    /**
     * Draws the control points of the specified Bezier patch
     * @param gl 
     * @param patch 
     */
    public void drawControlPoints( GLAutoDrawable drawable, ShadowPipeline pipeline, int patch ) {
    	pipeline.setkd(drawable, 0.0, 0.9, 0.0 );

    	// TODO: Objective 1: Draw the control points of the selected patch (so that you can draw all of them)

    	
    }
    
    FloatBuffer vertexBuffer;
	FloatBuffer normalBuffer;
	ShortBuffer indexBuffer;
    FloatBuffer lineVertexBuffer;
	ShortBuffer lineIndexBuffer;
    
	// TODO: Note that there is not check if this buffer is exceeded!
	final int maxVerts = 10000*3;
	final int maxInds = 10000*3;
	
	int positionBufferID;
	int normalBufferID;
	int elementBufferID;
	
	int linePositionBufferID;
	int lineElementBufferID;

	/**
	 * Create the vertex normal and index buffers for both drawing a patch, but also for drawing a debugging line for each tangent.
	 * @param drawable
	 */
	public void init( GLAutoDrawable drawable ) {
	    vertexBuffer = GLBuffers.newDirectFloatBuffer( maxVerts );
		normalBuffer = GLBuffers.newDirectFloatBuffer( maxVerts );
		indexBuffer = GLBuffers.newDirectShortBuffer( maxInds );
		lineVertexBuffer = GLBuffers.newDirectFloatBuffer( 6 );
		lineIndexBuffer = GLBuffers.newDirectShortBuffer( new short[] { 0 , 1 } );
		GL4 gl = drawable.getGL().getGL4();
        int[] bufferIDs = new int[5];
        gl.glGenBuffers( 5, bufferIDs, 0 );
        positionBufferID = bufferIDs[0];
        normalBufferID = bufferIDs[1];
        elementBufferID = bufferIDs[2];
        linePositionBufferID = bufferIDs[3];
        lineElementBufferID = bufferIDs[4];
        // Actually don't need to bind it now... just when we need to fill it out again.
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_DYNAMIC_DRAW );
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, normalBuffer.capacity() * Float.BYTES, normalBuffer, GL4.GL_DYNAMIC_DRAW );
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Short.BYTES, indexBuffer, GL4.GL_DYNAMIC_DRAW );

        // 6 values for the end points of 2 lines, and then 2 static (never changing) indices 0 and 1 to draw the line
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, lineVertexBuffer.capacity() * Float.BYTES, lineVertexBuffer, GL4.GL_DYNAMIC_DRAW ); 
        // This one won't change, so we can make it a static draw.
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, lineElementBufferID );
        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, lineIndexBuffer.capacity() * Short.BYTES, lineIndexBuffer, GL4.GL_STATIC_DRAW );
	}
    
	/**
	 * Draws the specified Bezier patch.
	 * @param gl     OpenGL drawing context
	 * @param patch  the index of the patch to draw
	 */
	public void draw( GLAutoDrawable drawable, ShadowPipeline pipeline, int patch ) {
		GL4 gl = drawable.getGL().getGL4();
		
        Vector3d coord, normal;
		
        // TODO: Note that this code will evaluate your surface and fill buffers for drawing
        vertexBuffer.rewind();
        normalBuffer.rewind();
		int N = subdivisions.getValue();
		int vertDataCount = 3*N*N;
		for ( int i = 0; i < N; i++ ) {
			for ( int j = 0; j < N; j++ ) {
			    double s1 = (double)i/(N-1);
			    double t1 = (double)j/(N-1);
				coord = evaluateCoordinate( s1, t1, patch );
				normal = evalNormal( s1, t1, patch );
				vertexBuffer.put( (float) coord.x );
				vertexBuffer.put( (float) coord.y );
				vertexBuffer.put( (float) coord.z );
				normalBuffer.put( (float) normal.x );
				normalBuffer.put( (float) normal.y );
				normalBuffer.put( (float) normal.z );				
			}
		}

		indexBuffer.rewind();
		int numInds = 2*N*(N-1);
		for ( int i = 0; i < N-1; i++ ) {
			for ( int j = 0; j < N; j++ ) {
				indexBuffer.put( (short)(i*N+j) );
				indexBuffer.put( (short)((i+1)*N+j) );
			}
		}
		
		vertexBuffer.rewind();
		normalBuffer.rewind();
		indexBuffer.rewind();
		
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertDataCount * Float.BYTES, vertexBuffer, GL4.GL_DYNAMIC_DRAW );
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertDataCount * Float.BYTES, normalBuffer, GL4.GL_DYNAMIC_DRAW );
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, numInds * Short.BYTES, indexBuffer, GL4.GL_DYNAMIC_DRAW );
		
		pipeline.currentGLSLProgram.bindPositionBuffer(gl, positionBufferID);
		pipeline.currentGLSLProgram.bindNormalBuffer(gl, normalBufferID);
		gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
		for ( int i = 0; i < N-1; i++ ) {
			gl.glDrawElements( GL4.GL_TRIANGLE_STRIP, 2*N, GL4.GL_UNSIGNED_SHORT, i*2*N*Short.BYTES );
		}
	
	}
	
	/**
	 * Draws a local surface tangents on the selected patch at the given coordinates
	 * @param gl 
	 * @param patch 
	 * @param s
	 * @param t
	 */
	public void drawSurfaceTangents( GLAutoDrawable drawable, ShadowPipeline pipeline, int patch, double s, double t ) {
	    GL4 gl = drawable.getGL().getGL4();	    
		
	    // TODO: Note this code to draw your tangents for objective 3
		Vector3d coord,ds,dt;
		coord = evaluateCoordinate( s, t, patch );
		ds = differentiateS(s, t, patch);
        dt = differentiateT(s, t, patch);
                
        pipeline.disableLighting(drawable);
		pipeline.currentGLSLProgram.bindPositionBuffer( gl, linePositionBufferID );

        // draw a red line
        pipeline.setkd(drawable, 1, 0, 0);
        lineVertexBuffer.rewind();
        lineVertexBuffer.put( new float[] { 
        		(float) coord.x, 
        		(float) coord.y, 
        		(float) coord.z, 
        		(float) (coord.x + ds.x), 
        		(float) (coord.y + ds.y), 
        		(float) (coord.z + ds.z) } );
        lineVertexBuffer.rewind();         
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, linePositionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, lineVertexBuffer.capacity() * Float.BYTES, lineVertexBuffer, GL4.GL_DYNAMIC_DRAW ); 
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, lineElementBufferID);
        gl.glDrawElements( GL4.GL_LINES, 2, GL4.GL_UNSIGNED_SHORT, 0 );

        // draw a green line
        pipeline.setkd(drawable, 0, 1, 0);
        lineVertexBuffer.rewind();
        lineVertexBuffer.put( new float[] { 
        		(float) coord.x, 
        		(float) coord.y, 
        		(float) coord.z, 
        		(float) (coord.x + dt.x), 
        		(float) (coord.y + dt.y), 
        		(float) (coord.z + dt.z) } );
        lineVertexBuffer.rewind();         
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, linePositionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, lineVertexBuffer.capacity() * Float.BYTES, lineVertexBuffer, GL4.GL_DYNAMIC_DRAW ); 
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, lineElementBufferID );
        gl.glDrawElements( GL4.GL_LINES, 2, GL4.GL_UNSIGNED_SHORT, 0 );
        
        pipeline.enableLighting(drawable);        
	}
	
	/**
	 * Constructor: Loads a Bezier Mesh contained in a file
	 * @param file 
	 */
	public BezierPatchWork( String file ) {	    
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			if (input != null) {
				int numPatches = Integer.parseInt(input.readLine());
				String[] controls;
				int[][][] controlQuads = new int[numPatches][4][4];
				for ( int i = 0; i < numPatches ; i++ ) {
					controls = input.readLine().split(",");
					for ( int j = 0 ; j < 4 ; j++ ) {
						for ( int k = 0; k < 4 ; k++ ) {
							controlQuads[i][j][k] = Integer.parseInt(controls[j*4+k])-1;						
						}
					}
				}
				int numPoints = Integer.parseInt(input.readLine());
				float [][] coords = new float[numPoints][3];
				for (int i=0; i < numPoints ; i++ ) {
					controls = input.readLine().split(",");
					for (int j=0;j<3;j++) {
						coords[i][j] = Float.parseFloat(controls[j]);
					}
				}
				coordinatePatch = new Matrix4d[controlQuads.length][3];
				for (int i=0; i < controlQuads.length ; i++) {
					for (int j=0; j < 3 ; j++) {
						coordinatePatch[i][j] = new Matrix4d();
						for (int k=0; k < 4 ; k++) {
							for (int l=0; l < 4 ; l++) {
								coordinatePatch[i][j].setElement(k, l, coords[controlQuads[i][k][l]][j]);
							}
						}
					}
				}
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
	
	/**
	 *  returns the xyz coordinates of the Bezier mesh at the parametric point (s,t)
	 */
	private Vector3d evaluateCoordinate( double s, double t, int patch ) {
		// TODO: Objective 2: Evaluate the surface positions (as opposed to the zero vector)
		
		
		return new Vector3d();
	}
	
	/**
	 *  differentiates the Bezier mesh along the parametric 's' direction
	 */
	private Vector3d differentiateS(double s,double t,int patch) {
		// TODO: Objective 3: Evaluate the surface derivative in the s direction
		
		return new Vector3d();
	}
	/**
	 *  differentiates the Bezier mesh along the parametric 't' direction
	 */
	private Vector3d differentiateT(double s,double t,int patch) {
		// TODO: Objective 3: Evaluate the surface derivative in the t direction

		return new Vector3d();
	}
	
	
	/**
	 *  returns the normal of the Bezier mesh at the parametric (s,t) point.
	 */
	private Vector3d evalNormal(double s, double t, int patch) {
		// TODO: Objective 4,5: compute the normal, and make sure the normal is always well defined!

		
		return new Vector3d();
	}
}
