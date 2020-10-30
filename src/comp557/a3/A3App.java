/*
 * Pengnan Fan
 * 260768510
 */
package comp557.a3;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.parameters.IntParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.EasyViewer;
import mintools.viewer.SceneGraphNode;
import mintools.viewer.ShadowPipeline;
import mintools.viewer.geom.FancyAxis;
import mintools.viewer.geom.Quad;

/**
 * Assignment 3, Bezier tensor product patches
 */
public class A3App implements SceneGraphNode {
	
	/**
	 * Main function, launches the EasyViewer
	 * @param args 
	 */
	public static void main(String[] args){
		// TODO: Add your name and student number to the window title and source files
	    String windowTitle = "Bezier Patch Drawer - Pengnan Fan - 260768510";
	    SceneGraphNode scene = new A3App();
	    Dimension glWindowSize = new Dimension(512,256);
	    Dimension controlWindowSize = new Dimension(640,640);
	    EasyViewer ev = new EasyViewer( windowTitle, scene, glWindowSize, controlWindowSize );
	    ev.controls.add("Scene", scene.getControls() );
	    ev.controls.setSelectedTab("Scene");
	}
		
    /**
     * Constructor, initialize settings
     */
    public A3App() {
        //patchwork = new BezierPatchWork("data/a3data/bezier.txt");
        patchwork = new BezierPatchWork("data/a3data/testPatches.txt");
        //patchwork = new BezierPatchWork("data/a3data/testPatch.txt");
        numPatches = patchwork.getNumPatches();
        patchNumber = new IntParameter("Patch Number", 0, 0, numPatches-1 );        
    }
    
    /** The loaded model */
	private BezierPatchWork patchwork;

	/** Number of patches in the loaded model */
    private int numPatches;
    
    private BooleanParameter displayControlPoints = new BooleanParameter( "Display Control Points", true );
    
    private BooleanParameter displayMesh = new BooleanParameter( "Display Bezier Mesh", true );
    
    private BooleanParameter wireframe = new BooleanParameter( "Wire Frame", true );

    private BooleanParameter displaySurfaceAxis = new BooleanParameter( "Display Surface Axis", false );

    private DoubleParameter surfaceAxisSCoord = new DoubleParameter("Surface Axis s Coordinate", .5, 0, 1 );
    
    private DoubleParameter surfaceAxisTCoord = new DoubleParameter("Surface Axis t Coordinate", .5, 0, 1 );
    
    private BooleanParameter displaySelectedPatchOnly = new BooleanParameter( "Display Selected Patch Only", false );        

    private IntParameter patchNumber; 
    
    @Override
    /**
     * Draws the scene (this is called by the windowing system every time the scene needs to be drawn)
     */
    public void display( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
        GL4 gl = drawable.getGL().getGL4();

        pipeline.push();
        pipeline.setkd(drawable, 0.8, 0.4, 0 );
        pipeline.scale(drawable, 3, 3, 3);
        Quad.draw(drawable, pipeline);
        pipeline.pop(drawable);
        
        pipeline.push();
        pipeline.scale(drawable, 0.3, 0.3, 0.3);
        
        FancyAxis.draw(drawable, pipeline);

        // TODO: Note that the world frame is on the plane, but loaded models are displayed slightly above!!!
        pipeline.translate(drawable, 0, 0, 0.2); // this is good for getting the teapot off the ground
        
        if (wireframe.getValue()) {
            gl.glPolygonMode( GL4.GL_FRONT_AND_BACK, GL4.GL_LINE );
        } else {
            gl.glPolygonMode( GL4.GL_FRONT_AND_BACK, GL4.GL_FILL );            
        }
       
        // displays the requested mesh parts
        if ( displayMesh.getValue() ) {
            if ( displaySelectedPatchOnly.getValue() ) {
                patchwork.draw( drawable, pipeline, patchNumber.getValue() );
            } else {
                for ( int i = 0 ; i < numPatches ; i++ ) {
                    patchwork.draw( drawable, pipeline, i );
                }
            }
        }
        
        gl.glPolygonMode( GL4.GL_FRONT_AND_BACK, GL4.GL_FILL );            

        // displays a surface axis at the specified s,t location
        if ( displaySurfaceAxis.getValue() ) {
            double s = surfaceAxisSCoord.getValue();
            double t = surfaceAxisTCoord.getValue();
            if ( displaySelectedPatchOnly.getValue() ) {
                patchwork.drawSurfaceTangents( drawable, pipeline, patchNumber.getValue(), s, t );
            } else  {
                for ( int i = 0 ; i < numPatches; i++ ) {              
                    patchwork.drawSurfaceTangents( drawable, pipeline, i, s, t );
                }
            }
        }        
        
        if ( displayControlPoints.getValue() ) {
            // display the control points with large red circles
            if ( displaySelectedPatchOnly.getValue() )  {
                patchwork.drawControlPoints( drawable, pipeline, patchNumber.getValue() );
            } else {
                for (int i=0 ; i < numPatches; i++) {
                    patchwork.drawControlPoints( drawable, pipeline, i );
                }
            }
        }
        
        pipeline.pop(drawable);
    }
    
    @Override
    /**
     * Called by the viewer class when we are setting up for rendering
     */
    public void init(GLAutoDrawable drawable) {    
        patchwork.init(drawable);
    }
    
    @Override
    /**
     * Initializes the Control Panel
     */
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.add( displayControlPoints.getControls() );        
        vfp.add( displayMesh.getControls() );        
        vfp.add( wireframe.getControls() );
        vfp.add( displaySurfaceAxis.getControls() );
        vfp.add( surfaceAxisSCoord.getControls() );
        vfp.add( surfaceAxisTCoord.getControls() );
        vfp.add( patchwork.subdivisions.getControls() );
        vfp.add( displaySelectedPatchOnly.getControls() );
        vfp.add( patchNumber.getControls() );
        return vfp.getPanel();
    }

}
