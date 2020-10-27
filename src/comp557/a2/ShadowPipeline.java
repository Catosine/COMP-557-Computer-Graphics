package comp557.a2;

import java.awt.Component;

import javax.management.RuntimeErrorException;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.swing.VerticalFlowPanel;
import mintools.viewer.FontTexture;

public class ShadowPipeline {
	
	public GLSLProgram currentGLSLProgram; 
	
	public GLSLProgram depthDrawDebug; 
	public GLSLProgram drawLightDepth; 
	public GLSLProgram basicLighting;
	public GLSLProgram perFragmentLighting;

	// lighting uniforms, if using the lighting program
    private int kdID;
    private int ksID;
    private int shininessID;
    private int enableLightingID;
    private int lightPosID;
    private int lightColorID;

    private int sigmaID;
    private int shadowMapID;
    
    private int lightVID;
    private int lightPID;
    
    // extra uniforms for using the depth draw debug program
	public int alphaID;
	public int depthTextureID;
    
    /** Modeling matrix stack */
    private Matrix4d stack[] = new Matrix4d[32];
    /** Need an modeling inverse transpose matrix stack too */
    private Matrix3d stackInvT[] = new Matrix3d[32];
    private int stackPos = 0;
    
    private FontTexture fontTexture;
    
    private Camera camera = new Camera();
    private ArcBall arcball = new ArcBall();
    PointLightCamera light = new PointLightCamera();

    private Vector4d tmpLightPos = new Vector4d();

    ShadowMap shadowMap = new ShadowMap( 1024 );
	        
	public ShadowPipeline( GLAutoDrawable drawable ) {
				
		for ( int i = 0; i < stack.length; i++ ) {
			stack[i] = new Matrix4d();
			stack[i].setIdentity();
			stackInvT[i] = new Matrix3d();
			stackInvT[i].setIdentity();
		}
		
		fontTexture = new FontTexture();
		fontTexture.init(drawable);
		
		GL4 gl = drawable.getGL().getGL4();
		
		// Create the GLSL program and get the IDs of the parameters (i.e., uniforms and attributes)
		basicLighting = new GLSLProgram( drawable, "basicLighting" );		
        kdID = gl.glGetUniformLocation( basicLighting.glslProgramID, "kd" );
        ksID = gl.glGetUniformLocation( basicLighting.glslProgramID, "ks" );
        shininessID = gl.glGetUniformLocation( basicLighting.glslProgramID, "shininess" );
        lightPosID = gl.glGetUniformLocation( basicLighting.glslProgramID, "lightPos" );
        enableLightingID = gl.glGetUniformLocation( basicLighting.glslProgramID, "enableLighting" );
        lightColorID = gl.glGetUniformLocation( basicLighting.glslProgramID, "lightColor" );
        sigmaID = gl.glGetUniformLocation( basicLighting.glslProgramID, "sigma" );
        shadowMapID = gl.glGetUniformLocation( basicLighting.glslProgramID, "shadowMap" );       
        // TODO: Objective 7, add extra uniforms to the basic lighting program help compute fragment in the light camera canonical viewing volume
        lightVID = gl.glGetUniformLocation(basicLighting.glslProgramID, "light_v");
        lightPID = gl.glGetUniformLocation(basicLighting.glslProgramID, "light_p");
        
        // Texture and program for drawing depth view from light and debugging the depth view
        shadowMap.setupDepthTextureFrameBuffer(drawable);
        drawLightDepth = new GLSLProgram( drawable, "drawLightDepth" ); 
        depthDrawDebug = new GLSLProgram( drawable, "depthDrawDebug" );
        alphaID = gl.glGetUniformLocation( depthDrawDebug.glslProgramID, "alpha" );
        depthTextureID = gl.glGetUniformLocation( depthDrawDebug.glslProgramID, "depthTexture" );
	}

	public void attachInteractors( Component c ) {
		arcball.attach( c );
	}
	
	public void startLightViewPass( GLAutoDrawable drawable ) {	
		GL4 gl = drawable.getGL().getGL4();
		currentGLSLProgram = drawLightDepth;
		shadowMap.bindLightPassFrameBuffer(drawable);
		light.updateMatrix( 512, 512 );
		currentGLSLProgram.use( gl ) ;
		currentGLSLProgram.setP( gl, light.P );
		currentGLSLProgram.setV( gl, light.V );
		currentGLSLProgram.setM( gl, stack[stackPos] );		
	}
	
	/**
	 * Enables the basic pipeline, sets viewing and projection matrices, and enables
	 * the position and normal vertex attributes
	 * @param drawable
	 */
	public void startCameraViewPass( GLAutoDrawable drawable ) {
		GL4 gl = drawable.getGL().getGL4();
		
		currentGLSLProgram = basicLighting; 
		shadowMap.bindPrimaryFrameBuffer(drawable);
		camera.updateMatrix( drawable.getSurfaceWidth(), drawable.getSurfaceHeight() );
		light.updateMatrix( 512, 512 );
		camera.V.mul( arcball.R );
		currentGLSLProgram.use(gl);
		currentGLSLProgram.setP( gl, camera.P );
		currentGLSLProgram.setV( gl, camera.V );
		currentGLSLProgram.setM( gl, stack[stackPos] );		
		currentGLSLProgram.setMinvT( gl, stackInvT[stackPos] );
        
        light.getPositionInWorld( tmpLightPos );
        camera.V.transform( tmpLightPos );

        gl.glUniform3f( lightPosID, (float) tmpLightPos.x, (float) tmpLightPos.y, (float) tmpLightPos.z );
        gl.glUniform3f( kdID, 1, 1, 1 ); // default to white material
        gl.glUniform3f( ksID, 1, 1, 1 ); // default to white specular highlights 
        gl.glUniform1f( shininessID, (float) 64.0 ); // default to very shiny (127 is max?)
        gl.glUniform3f( lightColorID, 1, 1, 1 ); // default to white light
        gl.glUniform1i( enableLightingID, 1 ); // enable lighting by default

        gl.glUniform1f( sigmaID, light.sigma.getFloatValue() ); // offset for avoiding self shadowing
		gl.glUniform1i( shadowMapID, 0 ); // texture unit zero

		// TODO: Objective 7: be sure to set extra uniforms to transform fragments to the camera canonical viewing volume.
		basicLighting.glUniformMatrix(gl, lightVID, light.V);
		basicLighting.glUniformMatrix(gl, lightPID, light.P);
	}

	/**
	 * Sets OpenGL to use the light texture debugging program, where the depth texture 
	 * previously rendered can be drawn onto a quad, with alpha being the amount of transparency.
	 * @param drawable
	 */
	public void debugLightTexture( GLAutoDrawable drawable ) {
		GL4 gl = drawable.getGL().getGL4();
		currentGLSLProgram = depthDrawDebug;
		currentGLSLProgram.use(gl);
		currentGLSLProgram.setP( gl, camera.P );
		currentGLSLProgram.setV( gl, camera.V );
		currentGLSLProgram.setM( gl, stack[stackPos] );		

		gl.glUniform1i( depthTextureID, 0 ); // texture unit zero
        gl.glUniform1f( alphaID, 0.5f ); // TODO: could be set by a double parameter instead
	}
		
	public void disableLighting( GLAutoDrawable drawable ) {
		if ( currentGLSLProgram != basicLighting ) return;
		GL4 gl = drawable.getGL().getGL4();
        gl.glUniform1i( enableLightingID, 0 );
	}
	 
	public void enableLighting( GLAutoDrawable drawable ) {
		if ( currentGLSLProgram != basicLighting ) return;
		GL4 gl = drawable.getGL().getGL4();
        gl.glUniform1i( enableLightingID, 1 );
	}
	
	public void setkd( GLAutoDrawable drawable, double r, double g, double b ) {
		if ( currentGLSLProgram != basicLighting ) return;
		GL4 gl = drawable.getGL().getGL4();
		gl.glUniform3f( kdID, (float)r, (float)g, (float)b );
	}
	
	/** Sets the modeling matrix with the current top of the stack */
	private void setModelingMatrixUniform( GL4 gl ) {
        currentGLSLProgram.setM( gl, stack[stackPos] );
        currentGLSLProgram.setMinvT( gl, stackInvT[stackPos] );
	}
	
	/** Pushes a copy of the current top of the stack onto the stack */
	public void push() {
		if ( ++stackPos > stack.length ) {
			throw new RuntimeErrorException( new Error("stack overflow") );
		}
		stack[stackPos].set( stack[stackPos-1] );
		stackInvT[stackPos].set( stackInvT[stackPos-1] );
	}

	/** 
	 * Pops and discards the top of the matrix and returns a peek of the new top
	 * NOTE: this might be confusing behaviour, but is more useful.
	 * @return peek at the new top of the stack
	 */
	public void pop( GLAutoDrawable drawable ) {
		if ( --stackPos < 0 ) {
			throw new RuntimeErrorException( new Error("stack underflow") );
		}
		setModelingMatrixUniform( drawable.getGL().getGL4() );
	}
	
	private Matrix4d tmpMatrix4d = new Matrix4d();
	private Matrix3d tmpMatrix3d = new Matrix3d();
	
	/**
	 * Applies a translation to the matrix at top of the stack.
	 * Note: setModelingMatrixUniform must be called before drawing!
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translate( GLAutoDrawable drawable, double x, double y, double z ) {
		tmpMatrix4d.setIdentity();
		tmpMatrix4d.m03 = x;
		tmpMatrix4d.m13 = y;
		tmpMatrix4d.m23 = z;
		stack[stackPos].mul(tmpMatrix4d);
		setModelingMatrixUniform( drawable.getGL().getGL4() );
	}

	/**
	 * Applies a scale to the matrix at top of the stack.
	 * Note: setModelingMatrixUniform must be called before drawing!
	 * @param x
	 * @param y
	 * @param z
	 */
	public void scale( GLAutoDrawable drawable, double x, double y, double z ) {
		tmpMatrix4d.setIdentity();
		tmpMatrix4d.m00 = x;
		tmpMatrix4d.m11 = y;
		tmpMatrix4d.m22 = z;
		stack[stackPos].mul(tmpMatrix4d);
		tmpMatrix3d.setIdentity();
		tmpMatrix3d.m00 = 1/x;
		tmpMatrix3d.m11 = 1/y;
		tmpMatrix3d.m22 = 1/z;
		stackInvT[stackPos].mul( tmpMatrix3d ); // accumulate inverse transpose
		setModelingMatrixUniform( drawable.getGL().getGL4() );
	}
	
	public void rotate( GLAutoDrawable drawable, double radians, double x, double y, double z ) {
		AxisAngle4d aa = new AxisAngle4d( x, y, z, radians );
		tmpMatrix4d.set( aa );
		stack[stackPos].mul(tmpMatrix4d);
		tmpMatrix3d.set( aa );
		stackInvT[stackPos].mul(tmpMatrix3d); // inverse transpose is the same rotation
		setModelingMatrixUniform( drawable.getGL().getGL4() );
	}
	
	public void multMatrix( GLAutoDrawable drawable, Matrix4d M ) {
		stack[stackPos].mul( M );
		tmpMatrix4d.invert( M ); // NOTE: this is potentially dangerous (i.e., if the matrix is not invertible */
		tmpMatrix4d.transpose();
		tmpMatrix4d.getRotationScale(tmpMatrix3d);
		stackInvT[stackPos].mul(tmpMatrix3d); 
		setModelingMatrixUniform( drawable.getGL().getGL4() );
	}
	
	/**
	 * Controls for the cameras and lights used with this pipeline
	 * @return
	 */
	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();
		vfp.add( camera.getControls() );
		vfp.add( arcball.getControls() );
		vfp.add( light.getControls() );
		return vfp.getPanel();
	}
	
}
