package comp557.a1;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.ImageUtil;

import mintools.parameters.BooleanParameter;

public class CanvasRecorder {

    /** Image for sending to the image processor */
    private BufferedImage image;
    
    /** Image Buffer for reading pixels */
    private Buffer imageBuffer;
    
    BooleanParameter record = new BooleanParameter( "record", false );
    
    private String dumpName = "dump";
    
    private int nextFrameNum = 0;
    
    private NumberFormat format = new DecimalFormat("00000");
    
    public JPanel getControls() {
    	return record.getControls();
    }
    
    /**
     * Saves a snapshot of the current canvas to a file.
     * The image is saved in png format and will be of the same size as the canvas.
     * Use ffmpeg to assemble into a video after exporting frames.
     * @param drawable
     * @param file
     * @return true on success
     */
    public void saveCanvasToFile( GLAutoDrawable drawable ) {
    	if ( !record.getValue() ) return;
    	int width = drawable.getSurfaceWidth();
    	int height = drawable.getSurfaceHeight();
    	if ( image == null || image.getWidth() != width || image.getHeight() != height ) {
            //image = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );            
            image = new BufferedImage( width, height, BufferedImage.TYPE_3BYTE_BGR );
            imageBuffer = ByteBuffer.wrap(((DataBufferByte)image.getRaster().getDataBuffer()).getData());
    	}
    	
        // write the frame
        File file = new File( "stills/" + dumpName + format.format(nextFrameNum) + ".png" );                                             
        nextFrameNum++;
        file = new File(file.getAbsolutePath().trim());
    	
        GL4 gl = drawable.getGL().getGL4();
        //gl.glReadPixels( 0, 0, width, height, GL4.GL_ABGR_EXT, GL4.GL_UNSIGNED_BYTE, imageBuffer );            
        gl.glReadPixels( 0, 0, width, height, GL4.GL_BGR, GL4.GL_UNSIGNED_BYTE, imageBuffer );
        ImageUtil.flipImageVertically(image);
        
        try {
            if ( ! ImageIO.write( image, "png", file) ) {
                System.err.println("Error writing file using ImageIO (unsupported file format?)");
                return;
            }
        } catch (IOException e) {    
            System.err.println("trouble writing " + file );
            e.printStackTrace();
            return;
        }
        
        String text =  "RECORDED: "+ file.toString();
        System.out.println( text );
    }

}
