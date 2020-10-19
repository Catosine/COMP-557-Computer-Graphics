package comp557.a2;

import java.awt.Dimension;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * Creates a frame buffer object for a shadow map
 */
public class ShadowMap {
	
	private int[] depthTexture = new int[1];
	private int[] depthFBO = new int[1];
	private final Dimension depthFBOSize;
    	
    /**
     * Creates a shadow map of the given size
     * @param size must be a power of 2
     */
    public ShadowMap( int size ) {        
        depthFBOSize = new Dimension(size,size);
    }
		
    /** 
     * Creates a frame buffer object and sets it up as a depth texture for shadow mapping
     * @param drawable
     */
    public void setupDepthTextureFrameBuffer( GLAutoDrawable drawable ) {
        GL4 gl = drawable.getGL().getGL4();	
		
        // SET UP RENDER TO TEXTURE FOR LIGHT DEPTH OFF SCREEN RENDERING
		gl.glGenTextures( 1, depthTexture, 0 );
		gl.glBindTexture( GL4.GL_TEXTURE_2D, depthTexture[0] );
		
		// By clamping texture lookups to the border, we can force the use of an arbitrary depth value
		// on the edge and outside of our depth map. {1,1,1,1} is max depth, while {0,0,0,0} is min depth
		// Ultimately, you may alternatively want to deal with clamping issues in a fragment program.
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameterfv(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_BORDER_COLOR, new float[] {1,1,1,1}, 0 );
		// The default filtering parameters not appropriate for depth maps, so we set them here! 
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);  
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		
		// You can also try GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT24 for the internal format.
		// Alternatively GL_DEPTH24_STENCIL8_EXT can be used (GL_EXT_packed_depth_stencil).
		// Here, null means reserve texture memory without initializing the contents.
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT32, depthFBOSize.width, depthFBOSize.height, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_UNSIGNED_INT, null);
		gl.glGenFramebuffers( 1, depthFBO, 0);
		gl.glBindFramebuffer( GL4.GL_FRAMEBUFFER, depthFBO[0] );
		gl.glFramebufferTexture2D( GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, GL4.GL_TEXTURE_2D, depthTexture[0], 0);
		gl.glDrawBuffer(GL4.GL_NONE);
		gl.glReadBuffer(GL4.GL_NONE);
		StringBuilder status = new StringBuilder();
        checkFramebufferStatus(gl, status );
        System.out.println( status );
        
		// Restore the original screen rendering frame buffer binding
		gl.glBindFramebuffer( GL4.GL_FRAMEBUFFER, 0 );
    }       
   	
    /**
     * Prepares for drawing the light view
     * @param drawable
     */
    public void bindLightPassFrameBuffer( GLAutoDrawable drawable ) {        
        GL4 gl = drawable.getGL().getGL4(); 
		//////////////////////////////////////////////////////////////////
		// Render to our off-screen depth frame buffer object (render to texture)
		gl.glBindFramebuffer( GL4.GL_FRAMEBUFFER, depthFBO[0] );
		gl.glClear( GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT );
		gl.glViewport( 0, 0, depthFBOSize.width, depthFBOSize.height ); 		
    }
    
    /**
     * Prepares for drawing with the shadow map depth test
     * @param drawable
     */
    public void bindPrimaryFrameBuffer(GLAutoDrawable drawable ) {
        GL4 gl = drawable.getGL().getGL4();        
		// Render to the screen
		gl.glBindFramebuffer( GL4.GL_FRAMEBUFFER, 0 );
		gl.glClear( GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT );
		gl.glViewport( 0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight() ); 
    }

    private int checkFramebufferStatus(GL4 gl, StringBuilder statusString) {
        int framebufferStatus = gl.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER);
        switch (framebufferStatus) {
            case GL4.GL_FRAMEBUFFER_COMPLETE:
                statusString.append("GL_FRAMEBUFFER_COMPLETE");
                break;
            case GL4.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                statusString.append("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENTS");
                break;
            case GL4.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                statusString.append("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
                break;
            case GL4.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                statusString.append("GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS");
                break;
            case GL4.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
                statusString.append("GL_FRAMEBUFFER_INCOMPLETE_FORMATS");
                break;
            case GL4.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                statusString.append("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
                break;
            case GL4.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                statusString.append("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
                break;
            case GL4.GL_FRAMEBUFFER_UNSUPPORTED:
                statusString.append("GL_FRAMEBUFFER_UNSUPPORTED");
                break;
        }
        return framebufferStatus;
    }
    
}
