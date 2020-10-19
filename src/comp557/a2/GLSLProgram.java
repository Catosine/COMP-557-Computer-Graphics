package comp557.a2;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

/**
 * Base GLSLProgram with common uniform and attribute IDs
 */
public class GLSLProgram {

	protected int glslProgramID;
    protected int PMatrixID;
    protected int VMatrixID;
	protected int MMatrixID;
    protected int MinvTMatrixID;
    protected int positionAttributeID;
    protected int normalAttributeID;
    protected int texCoordAttributeID;

    public GLSLProgram( GLAutoDrawable drawable, String name ) {
    	createProgram( drawable, name );
	}
    
    /**
	 * Creates a GLSL program from the .vp and .fp code provided in the shader directory 
	 * @param drawable
	 * @param name
	 * @return
	 */
	private void createProgram( GLAutoDrawable drawable, String name ) {
		GL4 gl = drawable.getGL().getGL4();
		ShaderCode vsCode = ShaderCode.create( gl, GL4.GL_VERTEX_SHADER, this.getClass(), "glsl", "glsl/bin", name, false );
		ShaderCode fsCode = ShaderCode.create( gl, GL4.GL_FRAGMENT_SHADER, this.getClass(), "glsl", "glsl/bin", name, false );
		ShaderProgram shaderProgram = new ShaderProgram();
		shaderProgram.add( vsCode );
		shaderProgram.add( fsCode );
		if ( !shaderProgram.link( gl, System.err ) ) {
			throw new GLException( "Couldn't link program: " + shaderProgram );
		}	
		shaderProgram.init(gl);
		
		glslProgramID = shaderProgram.program();
		
		// get common shared uniforms and attributes, if they exist
        PMatrixID = gl.glGetUniformLocation( glslProgramID, "P" );
        VMatrixID = gl.glGetUniformLocation( glslProgramID, "V" );
		MMatrixID = gl.glGetUniformLocation( glslProgramID, "M" );
        MinvTMatrixID = gl.glGetUniformLocation( glslProgramID, "MinvT" );
		
		positionAttributeID = gl.glGetAttribLocation( glslProgramID, "position" );
		normalAttributeID = gl.glGetAttribLocation( glslProgramID, "normal" );
		texCoordAttributeID = gl.glGetAttribLocation( glslProgramID, "texCoord" );
	}

	public void use( GL4 gl ) {
		gl.glUseProgram( glslProgramID );
		gl.glEnableVertexAttribArray( positionAttributeID );
		if ( normalAttributeID != -1 ) gl.glEnableVertexAttribArray( normalAttributeID );
		if ( texCoordAttributeID != -1 ) gl.glEnableVertexAttribArray( texCoordAttributeID );
	}
	
	public void setP( GL4 gl, Matrix4d P ) {
        glUniformMatrix( gl, PMatrixID, P );
	}

	public void setV( GL4 gl, Matrix4d V ) {
        glUniformMatrix( gl, VMatrixID, V );
	}

	public void setM( GL4 gl, Matrix4d M ) {
        glUniformMatrix( gl, MMatrixID, M );
	}

	public void setMinvT( GL4 gl, Matrix3d MinvT ) {
		if ( MinvTMatrixID == -1 ) return;
        glUniformMatrix3d( gl, MinvTMatrixID, MinvT );
	}

	public void bindPositionBuffer( GL4 gl, int positionBufferID ) {
		gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );		
		gl.glVertexAttribPointer( positionAttributeID, 3, GL4.GL_FLOAT, false, 3*Float.BYTES, 0 );			
	}	

	public void bindNormalBuffer( GL4 gl, int normalBufferID ) {
		if ( normalAttributeID == -1 ) return;
		gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID );		
	    gl.glVertexAttribPointer( normalAttributeID, 3, GL4.GL_FLOAT, false, 3*Float.BYTES, 0 );
	}	

	public void bindTexCoordBuffer( GL4 gl, int texCoordBufferID ) {
		if ( texCoordAttributeID == -1 ) return;
		gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, texCoordBufferID );		
	    gl.glVertexAttribPointer( texCoordAttributeID, 2, GL4.GL_FLOAT, false, 2*Float.BYTES, 0 );
	}	
	
    private float[] columnMajorMatrixData = new float[16];
    
    /**
     * Wrapper to glUniformMatrix4fv for vecmath Matrix4d
     * @param gl
     * @param ID
     * @param M
     */
    public void glUniformMatrix( GL4 gl, int ID, Matrix4d M ) {
    	columnMajorMatrixData[0] = (float) M.m00;
        columnMajorMatrixData[1] = (float) M.m10;
        columnMajorMatrixData[2] = (float) M.m20;
        columnMajorMatrixData[3] = (float) M.m30;
        columnMajorMatrixData[4] = (float) M.m01;
        columnMajorMatrixData[5] = (float) M.m11;
        columnMajorMatrixData[6] = (float) M.m21;
        columnMajorMatrixData[7] = (float) M.m31;
        columnMajorMatrixData[8] = (float) M.m02;
        columnMajorMatrixData[9] = (float) M.m12;
        columnMajorMatrixData[10] = (float) M.m22;
        columnMajorMatrixData[11] = (float) M.m32;
        columnMajorMatrixData[12] = (float) M.m03;
        columnMajorMatrixData[13] = (float) M.m13;
        columnMajorMatrixData[14] = (float) M.m23;
        columnMajorMatrixData[15] = (float) M.m33;
        gl.glUniformMatrix4fv( ID, 1, false, columnMajorMatrixData, 0 );
    }
	
    /**
     * Wrapper to glUniformMatrix4fv for vecmath Matrix4d
     * @param gl
     * @param ID
     * @param M
     */
    public void glUniformMatrix3d( GL4 gl, int ID, Matrix3d M ) {
    	columnMajorMatrixData[0] = (float) M.m00;
        columnMajorMatrixData[1] = (float) M.m10;
        columnMajorMatrixData[2] = (float) M.m20;
        columnMajorMatrixData[3] = (float) M.m01;
        columnMajorMatrixData[4] = (float) M.m11;
        columnMajorMatrixData[5] = (float) M.m21;
        columnMajorMatrixData[6] = (float) M.m02;
        columnMajorMatrixData[7] = (float) M.m12;
        columnMajorMatrixData[8] = (float) M.m22;
        gl.glUniformMatrix3fv( ID, 1, false, columnMajorMatrixData, 0 );
    }
	
}
