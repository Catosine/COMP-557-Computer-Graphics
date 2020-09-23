package comp557.a1;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a1.geom.SimpleAxis;
import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.swing.CollapsiblePanel;
import mintools.swing.VerticalFlowPanel;

/**
 * Base class for scene graph nodes.  
 * 
 * YOU DO NOT NEED TO EDIT THIS CLASS.​‌​​​‌‌​​​‌‌​​​‌​​‌‌‌​​‌
 * 
 * You should be aware of its methods and members.
 * 
 * @author kry
 */
public abstract class GraphNode {
    
	String name = "";
	
    LinkedList<GraphNode> children = new LinkedList<GraphNode>();

    Collection<DoubleParameter> dofs = new LinkedList<DoubleParameter>();
    
    /** parameter to enable debugging, which is added to the interface by the main application */
    static final BooleanParameter debugFrames = new BooleanParameter( "debug frames", false );
        
    public GraphNode( String name ) {
    	this.name = name;
    }

    public void add( GraphNode n ) {
    	children.add( n );
    }
    
    /**
     * Draws the node and all its children.  
     * 
     * You will need to override this method, and you will likewise want to call 
     * super.display(drawable) at some point in your implementation!
     * 
     * Note that we do not pass the transform to this method as was seen in the class notes
     * because the transform is stored in the BasicPipeline object.
     *  
     * @param drawable 
     */
    public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
    	// visualizing the frames may or may not help you figure things out!
    	if ( debugFrames.getValue() ) {
    		SimpleAxis.draw( drawable, pipeline );
    		pipeline.drawLabel( drawable, name );
    	}    	
		for ( GraphNode n : children ) {
			n.display( drawable, pipeline );
		}
    }
    
    /**
     * Recursively creates the controls for the DOFs of the nodes.
     * Note that if instancing occurs then the controls will appear
     * multiple times in the returned JPanel.
     * @return
     */
    public JPanel getControls() {
    	if ( dofs.isEmpty() && children.isEmpty() ) return null;
    	VerticalFlowPanel vfp = new VerticalFlowPanel();
    	vfp.setBorder( new TitledBorder(name) );
    	for ( DoubleParameter p : dofs ) {
    		vfp.add( p.getControls() );
    	}
    	for ( GraphNode n : children ) {
    		JPanel p = n.getControls();
    		if ( p != null ) {
    			vfp.add( p );
    		}
    	}
    	CollapsiblePanel cp = new CollapsiblePanel( vfp.getPanel() );
    	return cp;
    }
    
    /**
     * Recursively collects all the DOFs for use in creating key poses.
     * @param dofs
     */
    public void getDOFs( Collection<DoubleParameter> dofs ) {
    	dofs.addAll( this.dofs );
    	for ( GraphNode n : children ) {
			n.getDOFs(dofs);
		}
    }
    
}
