package comp557.a1;

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

/**​‌​​​‌‌​​​‌‌​​​‌​​‌‌‌​​‌
 * Class for Assignment 1, provides a viewing interface and keyframing interface for
 * a scene graph constructed with DAGNodes.
 * @author kry
 */
public class A1App implements GLEventListener {

    /**
     * Creates a Basic GL Window and links it to a GLEventListener
     * @param args
     */
    public static void main(String[] args) {
    	new A1App();
    }
 
    /** Object to create an animated character and to drive its motion */
    private KeyFrameAnimatedScene scene = new KeyFrameAnimatedScene();

    /** Helper for recording images to the directory "stills", should you like to make a video of your character animation */
    private CanvasRecorder canvasRecorder = new CanvasRecorder();
    
    /** Basic lighting pipeline, and other tools */
    private BasicPipeline pipeline;
    
    public A1App() {
        String windowName = "Assignment 1 - " + CharacterMaker.name;
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities glcap = new GLCapabilities(glp);
        GLCanvas glCanvas = new GLCanvas( glcap );
        final FPSAnimator animator; 
        animator = new FPSAnimator(glCanvas, 30);
        animator.start();
        ControlFrame controls = new ControlFrame("Controls", new Dimension( 600,600 ), new Point(680,0) );
        controls.add("Key Frame Controls", scene.getControls() );
        controls.add("Canvas Recorder Controls", canvasRecorder.getControls() );
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
        gl.glClearColor(0.5f, 0.0f, 0.0f, 1f); // TODO: Objective 0, change the background colour as you like!
        gl.glClearDepth(1.0f); // Depth Buffer Setup
        gl.glEnable(GL4.GL_DEPTH_TEST); // Enables Depth Testing
        gl.glDepthFunc(GL4.GL_LEQUAL); // The Type Of Depth Testing To Do
        gl.glEnable( GL4.GL_BLEND );
        gl.glBlendFunc( GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA );
        gl.glEnable( GL4.GL_LINE_SMOOTH );

		pipeline = new BasicPipeline( drawable );
    }
    
    @Override
    public void display( GLAutoDrawable drawable ) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        pipeline.enable( drawable );
        scene.display( drawable, pipeline );
        canvasRecorder.saveCanvasToFile( drawable );
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {}
    @Override
    public void dispose(GLAutoDrawable drawable) {}
}