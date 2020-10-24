package comp557.a2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jogamp.opengl.DebugGL4;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import mintools.swing.ControlFrame;

/**
 * Main function and class for running the arcball and shadow map assignment.
 */
public class A2App implements GLEventListener {

    /**
     * Creates a Basic GL Window and links it to a GLEventListener
     * @param args
     */
    public static void main(String[] args) {
    	new A2App();
    }
         
    /** Shadow lighting pipeline, and other tools */
    private ShadowPipeline shadowPipeline;
    
    private Scene scene;
    private GLCanvas glCanvas;
    private ControlFrame controls;
    
    public A2App() {
    	// TODO: replace your name and student number
        String windowName = "ArcBall and Shadow Maps - Pengnan Fan - 260768510";
        GLProfile glp = GLProfile.get( GLProfile.GL4 );
        GLCapabilities glcap = new GLCapabilities(glp);
        glCanvas = new GLCanvas( glcap );
        final FPSAnimator animator; 
        animator = new FPSAnimator(glCanvas, 30);
        animator.start();
        controls = new ControlFrame("Controls", new Dimension( 600,600 ), new Point(680,0) );
        
        controls.setVisible(true);    
        JFrame frame = new JFrame(windowName);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(glCanvas, BorderLayout.CENTER);
        glCanvas.setSize(500,500); // 640x360 for half 720p resolution woudl be nice, but need to fix projection windowing transformation.
        glCanvas.addGLEventListener( this);
        try {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            frame.pack(); // want our frame to come out the right size!
            frame.setVisible(true);
            glCanvas.requestFocus(); // activates the Event Listeners
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    /** 
     * initializes the canvas with some reasonable default settings
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL4(drawable.getGL().getGL4()));
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.1f, 0.1f, 0.1f, 1f); // almost black background
        gl.glClearDepth(1.0f); // Depth Buffer Setup
        gl.glEnable(GL4.GL_DEPTH_TEST); // Enables Depth Testing
        gl.glDepthFunc(GL4.GL_LEQUAL); // The Type Of Depth Testing To Do
        gl.glEnable( GL4.GL_BLEND );
        gl.glBlendFunc( GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA );
        gl.glEnable( GL4.GL_LINE_SMOOTH );

        shadowPipeline = new ShadowPipeline(drawable);        
        scene = new Scene();
        shadowPipeline.attachInteractors( glCanvas );
        controls.add("controls", shadowPipeline.getControls());
    }
    
    @Override
    public void display( GLAutoDrawable drawable ) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        shadowPipeline.startLightViewPass(drawable);
        scene.display( drawable, shadowPipeline );
        
        shadowPipeline.startCameraViewPass( drawable );        
        scene.display( drawable, shadowPipeline );

		shadowPipeline.light.draw(drawable, shadowPipeline);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {}
    @Override
    public void dispose(GLAutoDrawable drawable) {}
}