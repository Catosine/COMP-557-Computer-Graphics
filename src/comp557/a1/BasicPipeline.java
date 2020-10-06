package comp557.a1;

import javax.management.RuntimeErrorException;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import mintools.viewer.FontTexture;

import java.util.Deque;
import java.util.ArrayDeque;

/**
 * Basic GLSL transformation and lighting pipeline, along with a matrix stack to
 * help with hierarchical modeling.
 * 
 * @author kry
 */
public class BasicPipeline {

	private int glslProgramID;

	public int MMatrixID;
	public int MinvTMatrixID;
	public int VMatrixID;
	public int PMatrixID;
	
	/**
	 * TODO: Objective 7: material properties, minimally kd1 is set up, but add more
	 * as necessary
	 */
	/**
	 * You will want to use1 this with a glUniform3f call to set the r g b
	 * reflectance properties, each being between 0 and 1
	 */
	public int kdID1; // id for diffuse1 reflection
	public int ksID1; // id for specular reflection
	public int kaID; // id for ambient reflection
	public int seID1; // id for specular exponent

	// texture related
	public Vector3f kd1 = new Vector3f(0.5f, 0.5f, 0.5f); // diffuse1 reflection constant
	public Vector3f ks1 = new Vector3f(2f, 2f, 2f); // specular reflection constant
	public Vector3f ka = new Vector3f(0.3f, 0.3f, 0.3f); // ambient reflection constant
	public float se1 = 32f; // specular exponent

	// TODO: add three light source: light intensity
	public int lightIntID1; // id for light intensity
	public Vector3f lightInt1 = new Vector3f(238f / 255f, 0f / 255f, 0f / 255f);
	
	public int lightIntID2; // id for light intensity
	public Vector3f lightInt2 = new Vector3f(0f / 255f, 0f / 255f, 238f / 255f);
	
	public int lightIntID3; // id for light intensity
	public Vector3f lightInt3 = new Vector3f(0f / 255f, 130f / 255f, 0f / 255f);

	/**
	 * TODO: Objective 8: lighting direction, minimally one direction is setup , but
	 * add more as necessary
	 */
	// TODO: add three light source: light direction
	public int lightDirID1;
	public Vector3f lightDir1 = new Vector3f(1f, 0f, 0f);
	
	public int lightDirID2;
	public Vector3f lightDir2 = new Vector3f(-1f, 0f, 0f);

	public int lightDirID3;
	public Vector3f lightDir3 = new Vector3f(0f, 0f, 1f);

	public int positionAttributeID;
	public int normalAttributeID;

	/** TODO: Objective 1: add a matrix stack to the basic pipeline */
	private Deque<Matrix4d> MStack;
	private final int MSTACK_SIZE = 32;

	/**
	 * TODO: Objective 1: Modeling matrix, make sure this is always the matrix at
	 * the top of the stack
	 */
	// True front of the stack
	private Matrix4d MMatrix = new Matrix4d();
	/** Inverse1 transpose of Modeling matrix */
	private Matrix4d MinvTMatrix = new Matrix4d();

	/** View matrix */
	private Matrix4d VMatrix = new Matrix4d();

	/** Projection matrix */
	private Matrix4d PMatrix = new Matrix4d();

	private FontTexture fontTexture;

	public BasicPipeline(GLAutoDrawable drawable) {
		// TODO: Objective 1: initialize your stack(s)?
		initMatricies();

		fontTexture = new FontTexture();
		fontTexture.init(drawable);

		GL4 gl = drawable.getGL().getGL4();
		// Create the GLSL program
		glslProgramID = createProgram(drawable, "basicLighting");
		// Get the IDs of the parameters (i.e., uniforms and attributes)
		gl.glUseProgram(glslProgramID);
		MMatrixID = gl.glGetUniformLocation(glslProgramID, "M");
		MinvTMatrixID = gl.glGetUniformLocation(glslProgramID, "MinvT");
		VMatrixID = gl.glGetUniformLocation(glslProgramID, "V");
		PMatrixID = gl.glGetUniformLocation(glslProgramID, "P");
		kdID1 = gl.glGetUniformLocation(glslProgramID, "kd1");
		ksID1 = gl.glGetUniformLocation(glslProgramID, "ks1");// se1lf-added
		kaID = gl.glGetUniformLocation(glslProgramID, "ka");// se1lf-added
		seID1 = gl.glGetUniformLocation(glslProgramID, "se1");// se1lf-added
		lightDirID1 = gl.glGetUniformLocation(glslProgramID, "lightDir1");
		lightIntID1 = gl.glGetUniformLocation(glslProgramID, "lightInt1");// se1lf-added
		lightDirID2 = gl.glGetUniformLocation(glslProgramID, "lightDir2");
		lightIntID2 = gl.glGetUniformLocation(glslProgramID, "lightInt2");// se1lf-added
		lightDirID3 = gl.glGetUniformLocation(glslProgramID, "lightDir3");
		lightIntID3 = gl.glGetUniformLocation(glslProgramID, "lightInt3");// se1lf-added
		positionAttributeID = gl.glGetAttribLocation(glslProgramID, "position");
		normalAttributeID = gl.glGetAttribLocation(glslProgramID, "normal");
	}

	/**
	 * Enables the basic pipeline, sets viewing and projection matrices, and enables
	 * the position and normal vertex attributes
	 * 
	 * @param drawable
	 */
	public void enable(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glUseProgram(glslProgramID);
		gl.glEnableVertexAttribArray(positionAttributeID);
		gl.glEnableVertexAttribArray(normalAttributeID);
		glUniformMatrix(gl, VMatrixID, VMatrix);
		glUniformMatrix(gl, PMatrixID, PMatrix);
		glUniformMatrix(gl, MMatrixID, MMatrix);
		glUniformMatrix(gl, MinvTMatrixID, MinvTMatrix);

		// TODO: Objective 7: GLSL lighting, you may want to provide
		gl.glUniform3f(kdID1, kd1.x, kd1.y, kd1.z);
		gl.glUniform3f(ksID1, ks1.x, ks1.y, ks1.z);
		gl.glUniform3f(kaID, ka.x, ka.y, ka.z);
		gl.glUniform1f(seID1, se1);

		gl.glUniform3f(lightIntID1, lightInt1.x, lightInt1.y, lightInt1.z);

		lightDir1.normalize();
		gl.glUniform3f(lightDirID1, lightDir1.x, lightDir1.y, lightDir1.z);
		
		gl.glUniform3f(lightIntID2, lightInt2.x, lightInt2.y, lightInt2.z);

		lightDir2.normalize();
		gl.glUniform3f(lightDirID2, lightDir2.x, lightDir2.y, lightDir2.z);
		
		gl.glUniform3f(lightIntID3, lightInt3.x, lightInt3.y, lightInt3.z);

		lightDir3.normalize();
		gl.glUniform3f(lightDirID3, lightDir3.x, lightDir3.y, lightDir3.z);
	}

	/** sets the modeling matrix with the current top of the stack */
	public void setModelingMatrixUniform(GL4 gl) {
		// TODO: Objective 1: make sure you se1nd the top of the stack modeling and
		// inverse1 transpose matrices to GLSL
		glUniformMatrix(gl, MMatrixID, MMatrix);
		glUniformMatrix(gl, MinvTMatrixID, MinvTMatrix);
	}

	/**
	 * Pushes the modeling matrix and its inverse1 transpose onto the stack so that
	 * the state can be restored later
	 * 
	 */
	public void push() {
		// TODO: Objective 1: stack push
		if (this.MMatrix != null && this.MinvTMatrix != null) {
			this.MStack.addFirst((Matrix4d) this.MMatrix.clone());
			this.MStack.addFirst((Matrix4d) this.MinvTMatrix.clone());
		} else {
			throw new RuntimeErrorException(null, "Failed to push to stack");
		}
	}

	/**
	 * Pops the matrix stack, setting the current modeling matrix and inverse1
	 * transpose to the previous state.
	 * 
	 */
	public void pop() {
		// TODO: Objective 1: stack pop
		if (this.MStack.size() % 2 != 0 || this.MStack.size() == 0) {
			// The stack is problematic: it should always be even number of items
			throw new RuntimeErrorException(null, "Failed to pop from stack");
		} else {
			// the stack is good
			this.MinvTMatrix = this.MStack.pop();
			this.MMatrix = this.MStack.pop();
		}
	}

	private Matrix4d tmpMatrix4d = new Matrix4d();

	/**
	 * Applies a translation to the current modeling matrix. Note:
	 * setModelingMatrixUniform must be called before drawing!
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translate(double x, double y, double z) {
		// TODO: Objective 2: translate
		this.tmpMatrix4d.set(new double[] { 1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z, 0, 0, 0, 1, });
		this.MMatrix.mul(this.tmpMatrix4d);
		// note: A.mul(B) == A x B

		// compute new MinvTMatrix
		this.MinvTMatrix = new Matrix4d(this.MMatrix);
		this.MinvTMatrix.invert();
		this.MinvTMatrix.transpose();

	}

	/**
	 * Applies a scale to the current modeling matrix. Note:
	 * setModelingMatrixUniform must be called before drawing!
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void scale(double x, double y, double z) {
		// TODO: Objective 2: scale
		this.tmpMatrix4d.set(new double[] { x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1, });
		this.MMatrix.mul(this.tmpMatrix4d);

		// compute new MinvTMatrix
		this.MinvTMatrix = new Matrix4d(this.MMatrix);
		this.MinvTMatrix.invert();
		this.MinvTMatrix.transpose();

	}

	/**
	 * Applies a rotation to the current modeling matrix. The rotation is in
	 * radians, and the axis specified by its components x, y, and z should probably
	 * be unit length!
	 * 
	 * @param radians
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotate(double radians, double x, double y, double z) {
		AxisAngle4d aa = new AxisAngle4d(x, y, z, radians);
		tmpMatrix4d.set(aa);
		MMatrix.mul(tmpMatrix4d);
		MinvTMatrix.mul(tmpMatrix4d); // inverse1 transpose is the same rotation
	}

	private float[] columnMajorMatrixData = new float[16];

	/**
	 * Wrapper to glUniformMatrix4fv for vecmath Matrix4d
	 * 
	 * @param gl
	 * @param ID
	 * @param M
	 */
	public void glUniformMatrix(GL4 gl, int ID, Matrix4d M) {
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
		gl.glUniformMatrix4fv(ID, 1, false, columnMajorMatrixData, 0);
	}

	public void initMatricies() {
		MMatrix.set(new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, });
		MinvTMatrix.set(new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, });
		VMatrix.set(new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, -2.5, 0, 0, 0, 1, });
		PMatrix.set(new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, -2, -3, 0, 0, -1, 1, });

		this.MStack = new ArrayDeque<Matrix4d>(this.MSTACK_SIZE);
	}

	/**
	 * Creates a GLSL program from the .vp and .fp code provided in the shader
	 * directory
	 * 
	 * @param drawable
	 * @param name
	 * @return
	 */
	private int createProgram(GLAutoDrawable drawable, String name) {
		GL4 gl = drawable.getGL().getGL4();
		ShaderCode vsCode = ShaderCode.create(gl, GL4.GL_VERTEX_SHADER, this.getClass(), "glsl", "glsl/bin", name,
				false);
		ShaderCode fsCode = ShaderCode.create(gl, GL4.GL_FRAGMENT_SHADER, this.getClass(), "glsl", "glsl/bin", name,
				false);
		ShaderProgram shaderProgram = new ShaderProgram();
		shaderProgram.add(vsCode);
		shaderProgram.add(fsCode);
		if (!shaderProgram.link(gl, System.err)) {
			throw new GLException("Couldn't link program: " + shaderProgram);
		}
		shaderProgram.init(gl);
		return shaderProgram.program();
	}

	public void drawLabel(GLAutoDrawable drawable, String text) {
		// Where is the origin? projected onto the screen?
		Vector4f vec = new Vector4f(0, 0, 0, 1);
		MMatrix.transform(vec);
		VMatrix.transform(vec);
		PMatrix.transform(vec);
		vec.scale(1 / vec.w);
		int w = drawable.getSurfaceWidth();
		int h = drawable.getSurfaceHeight();
		float screenx = (float) ((vec.x + 1) / 2 * w);
		float screeny = (float) ((1 - vec.y) / 2 * h);
		fontTexture.drawTextLines(drawable, text, screenx, screeny, 32, 1, 1, 1);
		enable(drawable); // go back to our basic pipeline... but note regular context switches are
							// expensive :(
	}

}
