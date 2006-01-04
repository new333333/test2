package com.sitescape.ef.util;

	import java.awt.Image;
	import java.awt.Graphics2D;
	import java.awt.geom.AffineTransform;
	import java.awt.image.BufferedImage;
	import java.io.IOException;
	import java.io.OutputStream;
	import java.io.FileOutputStream;
	import javax.swing.ImageIcon;
	import com.sun.image.codec.jpeg.JPEGCodec;
	import com.sun.image.codec.jpeg.JPEGImageEncoder;

	public class Thumbnail {
	    public static void main(String[] args) {
	        createThumbnail("c:/image.jpeg", "c:/image_thumbnail.jpeg", 
	        		Integer.parseInt("200"), Integer.parseInt("200"));
	    }
     /**
     * Reads an image in a file and creates a thumbnail in another file.
     * @param imageData      An array of pixels in an image format supported
     * by the AWT Toolkit, such as GIF or JPEG
     * @param thumb     The name of thumbnail file. Will be created if necessary.
     * @param maxWidth  The maximum width of the thumbnail.
     * @param maxHeight The maximum height of the thumbnail.
     * The optimum fit is selected based on the width and height of the input image.
     */
    public static void createThumbnail(byte[] imageData, String thumb, int maxWidth, int maxHeight) {
    	// Get the image from the byte array.
    	Image inImage = new ImageIcon(imageData).getImage();
    	createThumbnail(inImage, thumb, maxWidth, maxHeight);
    }
    
     /**
     * Reads an image in a file and creates a thumbnail in another file.
     * @param orig      The name of image file.
     * @param thumb     The name of thumbnail file. Will be created if necessary.
     * @param maxWidth  The maximum width of the thumbnail.
     * @param maxHeight The maximum height of the thumbnail.
     * The optimum fit is selected based on the width and height of the input image.
     */
    public static void createThumbnail(String orig, String thumb, int maxWidth, int maxHeight) {
    	// Get the image from a file.
        Image inImage = new ImageIcon(orig).getImage();
    	createThumbnail(inImage, thumb, maxWidth, maxHeight);
    }
    
    private static void createThumbnail(Image inImage, String thumb, int maxWidth, int maxHeight) {
        try {
            // Determine the scale.
		    double scaleWidth = (double)maxWidth/(double)inImage.getWidth(null);
		    double scaleHeight = (double)maxHeight/(double)inImage.getHeight(null);
	        double scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth ;
	
	        // Determine size of new image. 
	        // One of them should equal maxDim.
	        int scaledW = (int)(scale*inImage.getWidth(null));
	        int scaledH = (int)(scale*inImage.getHeight(null));
	
	        // Create an image buffer in which to paint on.
	        BufferedImage outImage = new BufferedImage(
	        		scaledW, scaledH,BufferedImage.TYPE_INT_RGB);
	
	        // Set the scale.
	        AffineTransform tx = new AffineTransform();
	
	        // If the image is smaller than the desired image size,
	        // don't bother scaling.
	        if (scale < 1.0d) {
	            tx.scale(scale, scale);
	        }
	
	        // Paint image.
	        Graphics2D g2d = outImage.createGraphics();
	        g2d.drawImage(inImage, tx, null);
	        g2d.dispose();
	
	        // JPEG-encode the image and write to file.
	        OutputStream os = new FileOutputStream(thumb);
	        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
	        encoder.encode(outImage);
	        os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
