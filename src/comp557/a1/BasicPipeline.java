/**
 * COMP 557 - Assig 1
 * @author Sabina Sasu, 260803977
 */
package comp557.a1;

import java.util.Stack;

import javax.management.RuntimeErrorException;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import mintools.viewer.FontTexture;

/**
 * Basic GLSL transformation and lighting pipeline, along with a matrix stack
 * to help with hierarchical modeling.
 * @author kry
 */
public class BasicPipeline {

    private int glslProgramID;
    
    public int MMatrixID;
    public int MinvTMatrixID;
    public int VMatrixID;
    public int PMatrixID;
    
    /** TODO: Objective 7: material properties, minimally kd is set up, but add more as necessary */
    /** You will want to use this with a glUniform3f call to set the r g b reflectance properties, each being between 0 and 1 */
    public int kdID; //diffuse light coefficient
    
    /** TODO: Objective 8: lighting direction, minimally one direction is setup , but add more as necessary */
    public int lightDirID;
    public int lightDirID2;
    public int lightDirID3;
    public int lightColorID;
    public int viewDirID;
    
    public int positionAttributeID;
    public int normalAttributeID;
    
    /** TODO: Objective 1: add a matrix stack to the basic pipeline */    
    public Stack<Matrix4d> matrixStack;
    
	/** TODO: Objective 1: Modeling matrix, make sure this is always the matrix at the top of the stack */
    private Matrix4d MMatrix = new Matrix4d();
    /** Inverse Transpose of Modeling matrix */
    private Matrix4d MinvTMatrix = new Matrix4d();
    /** View matrix */
    private Matrix4d VMatrix = new Matrix4d();
    /** Projection matrix */
    private Matrix4d PMatrix = new Matrix4d();
    
    private FontTexture fontTexture;
    
	public BasicPipeline( GLAutoDrawable drawable ) {
		// TODO: Objective 1: initialize your stack(s)?
		initMatricies();
		matrixStack = new Stack<Matrix4d>();
		matrixStack.setSize(64);
		
		fontTexture = new FontTexture();
		fontTexture.init(drawable);
		
		GL4 gl = drawable.getGL().getGL4();
		// Create the GLSL program 
		glslProgramID = createProgram( drawable, "basicLighting" );
		// Get the IDs of the parameters (i.e., uniforms and attributes)
        gl.glUseProgram( glslProgramID );
        MMatrixID = gl.glGetUniformLocation( glslProgramID, "M" );
        MinvTMatrixID = gl.glGetUniformLocation( glslProgramID, "MinvT" );
        VMatrixID = gl.glGetUniformLocation( glslProgramID, "V" );
        PMatrixID = gl.glGetUniformLocation( glslProgramID, "P" );
        kdID = gl.glGetUniformLocation( glslProgramID, "kd" );
        lightDirID = gl.glGetUniformLocation( glslProgramID, "lightPos" );
        lightDirID2 = gl.glGetUniformLocation( glslProgramID, "lightPos2" );
        lightDirID3 = gl.glGetUniformLocation( glslProgramID, "lightPos3" );
        lightColorID = gl.glGetUniformLocation( glslProgramID, "lightColor" );
        viewDirID = gl.glGetUniformLocation( glslProgramID, "viewPos" );
        positionAttributeID = gl.glGetAttribLocation( glslProgramID, "position" );
        normalAttributeID = gl.glGetAttribLocation( glslProgramID, "normal" );
	}
	
	/**
	 * Enables the basic pipeline, sets viewing and projection matrices, and enables
	 * the position and normal vertex attributes
	 * @param drawable
	 */
	public void enable( GLAutoDrawable drawable ) {
		GL4 gl = drawable.getGL().getGL4();
        gl.glUseProgram( glslProgramID );
		gl.glEnableVertexAttribArray( positionAttributeID );
        gl.glEnableVertexAttribArray( normalAttributeID );
        glUniformMatrix( gl, VMatrixID, VMatrix );
        glUniformMatrix( gl, PMatrixID, PMatrix );
        glUniformMatrix( gl, MMatrixID, MMatrix );
        glUniformMatrix( gl, MinvTMatrixID, MinvTMatrix );

        // TODO: Objective 7: GLSL lighting, you may want to provide 
        Vector3f lightDir = new Vector3f( 1f, 0f, 1f );
        //lightDir.normalize();
        gl.glUniform3f( lightDirID, lightDir.x, lightDir.y, lightDir.z );
        
        Vector3f lightColor = new Vector3f( 0.5f, 0.5f, 0.5f );
        //lightColor.normalize();
        gl.glUniform3f( lightColorID, lightColor.x, lightColor.y, lightColor.z );
        
        Vector3f viewDir = new Vector3f( 0f, 0f, 2.5f );
        //viewDir.normalize();
        gl.glUniform3f( viewDirID, viewDir.x, viewDir.y, viewDir.z );
        
        
        Vector3f lightDir2 = new Vector3f( -5f, 5f, 0f );
        //lightDir2.normalize();
        gl.glUniform3f( lightDirID2, lightDir2.x, lightDir2.y, lightDir2.z );
        
        Vector3f lightDir3 = new Vector3f( 0f, -1f, 1f );
        //lightDir2.normalize();
        gl.glUniform3f( lightDirID3, lightDir3.x, lightDir3.y, lightDir3.z );
        
	}
	
	/** Sets the modeling matrix with the current top of the stack */
	public void setModelingMatrixUniform( GL4 gl ) {
		// TODO: Objective 1: make sure you send the top of the stack modeling and inverse transpose matrices to GLSL
		glUniformMatrix( gl, MMatrixID, MMatrix );
		glUniformMatrix( gl, MinvTMatrixID, MinvTMatrix);
		
	}
	
	private Matrix4d tmpMatrix4d1 = new Matrix4d();
	private Matrix4d tmpMatrix4d2 = new Matrix4d();
	
	/** 
	 * Pushes the modeling matrix and its inverse transpose onto the stack so 
	 * that the state can be restored later
	 */
	public void push() {
		// TODO: Objective 1: stack push
		matrixStack.push(new Matrix4d(MinvTMatrix));
		matrixStack.push(new Matrix4d(MMatrix));
	}

	/** 
	 * Pops the matrix stack, setting the current modeling matrix and inverse transpose
	 * to the previous state.
	 * @return peek at the new top of the stack
	 */
	public void pop() {
		// TODO: Objective 1: stack pop
		if(!matrixStack.isEmpty()) {
			MMatrix = matrixStack.pop();
			MinvTMatrix = matrixStack.pop();
		}
		
	}
	
	private Matrix4d tmpMatrix4d = new Matrix4d();
	
	/**
	 * Applies a translation to the current modeling matrix.
	 * Note: setModelingMatrixUniform must be called before drawing!
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translate( double x, double y, double z ) {
		// TODO: Objective 2: translate
		tmpMatrix4d.set( new double[] {
        		1,  0,  0,  x,
        		0,  1,  0,  y,
        		0,  0,  1,  z,
        		0,  0,  0,  1,
        } );
		MMatrix.mul(tmpMatrix4d);
		MinvTMatrix.mul(tmpMatrix4d);
	}

	/**
	 * Applies a scale to the current modeling matrix.
	 * Note: setModelingMatrixUniform must be called before drawing!
	 * @param x
	 * @param y
	 * @param z
	 */
	public void scale( double x, double y, double z ) {
		// TODO: Objective 2: scale
		tmpMatrix4d.set( new double[] {
        		x,  0,  0,  0,
        		0,  y,  0,  0,
        		0,  0,  z,  0,
        		0,  0,  0,  1,
        } );
		MMatrix.mul(tmpMatrix4d);
		MinvTMatrix.mul(tmpMatrix4d);
	}
	
	/**
	 * Applies a rotation to the current modeling matrix.
	 * The rotation is in radians, and the axis specified by its
	 * components x, y, and z should probably be unit length!
	 * @param radians
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotate( double radians, double x, double y, double z ) {
		AxisAngle4d aa = new AxisAngle4d( x, y, z, radians );
		tmpMatrix4d.set( aa );
		MMatrix.mul(tmpMatrix4d);
		MinvTMatrix.mul(tmpMatrix4d); // inverse transpose is the same rotation
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
	
	public void initMatricies() {
        MMatrix.set( new double[] {
        		1,  0,  0,  0,
        		0,  1,  0,  0,
        		0,  0,  1,  0,
        		0,  0,  0,  1,
        } );
        MinvTMatrix.set( new double[] {
        		1,  0,  0,  0,
        		0,  1,  0,  0,
        		0,  0,  1,  0,
        		0,  0,  0,  1,
        } );
        VMatrix.set( new double[] {
        		1,  0,  0,  0,
        		0,  1,  0,  0,
        		0,  0,  1, -2.5,
        		0,  0,  0,  1,
        } );
        PMatrix.set( new double[] {
        		1,  0,  0,  0,
        		0,  1,  0,  0,
        		0,  0, -2, -3,
        		0,  0, -1,  1,
        } );
    }
    
    /**
	 * Creates a GLSL program from the .vp and .fp code provided in the shader directory 
	 * @param drawable
	 * @param name
	 * @return
	 */
	private int createProgram( GLAutoDrawable drawable, String name ) {
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
        return shaderProgram.program();
	}
	
	public void drawLabel( GLAutoDrawable drawable, String text ) {
		// Where is the origin? projected onto the screen?
		Vector4f vec = new Vector4f(0,0,0,1);
		MMatrix.transform(vec);
		VMatrix.transform(vec);
		PMatrix.transform(vec);
		vec.scale( 1/vec.w );
        int w = drawable.getSurfaceWidth();
        int h = drawable.getSurfaceHeight();
        float screenx = (float) ((vec.x + 1) / 2 * w);
        float screeny = (float) ((1 - vec.y) / 2 * h); 
		fontTexture.drawTextLines(drawable, text, screenx, screeny, 32, 1,1,1 );
		enable(drawable); // go back to our basic pipeline... but note regular context switches are expensive :(
	}

	public void setColor(GL4 gl, float f, float g, float h) {
		gl.glUniform3f(this.kdID, f, g, h);
		
	}
	
}
