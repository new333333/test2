/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import javax.swing.ImageIcon;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Thumbnail {
	public static void main(String[] args) {
		createThumbnail("c:/image.jpeg", "c:/image_thumbnailr.jpeg", Integer
				.parseInt("200"), Integer.parseInt("200"));
		createThumbnail("c:/image.jpeg", "c:/image_thumbnails.jpeg", Integer
				.parseInt("200"));
	}

	/**
	 * Reads an image in a file and creates a thumbnail in another file.
	 * @param imageData      An array of pixels in an image format supported
	 * by the AWT Toolkit, such as GIF or JPEG
	 * @param os     	The output stream for the generated thumbnail.
	 * @param maxWidth  The maximum width of the thumbnail.
	 * @param maxHeight The maximum height of the thumbnail.
	 * The optimum fit is selected based on the width and height of the input image.
	 * @throws IOException 
	 * @throws ImageFormatException 
	 */
	public static void createThumbnail(byte[] imageData, OutputStream os,
			int maxWidth, int maxHeight) throws ThumbnailException {
		// Get the image from the byte array.
		Image inImage = new ImageIcon(imageData).getImage();
		try {
			//createThumbnail(inImage, os, maxWidth, maxHeight);
			createRectThumbnail(inImage, os, maxWidth, maxHeight);
		} catch (ImageFormatException e) {
			throw new ThumbnailException(e);
		} catch (IOException e) {
			throw new ThumbnailException(e);
		}
	}

	/**
	 * Reads an image in a file and creates a thumbnail in another file.
	 * @param imageData      An array of pixels in an image format supported
	 * by the AWT Toolkit, such as GIF or JPEG
	 * @param os     	The output stream for the generated thumbnail.
	 * @param maxSize  The maximum size of the thumbnail.
	 * The optimum fit is selected based on the width and height of the input image.
	 * The resulting thumbnail is square, with the source image clipped in the
	 * larger dimension.
	 * @throws IOException 
	 * @throws ImageFormatException 
	 */
	public static void createThumbnail(byte[] imageData, OutputStream os,
			int maxSize) throws ThumbnailException {
		// Get the image from the byte array.
		Image inImage = new ImageIcon(imageData).getImage();
		try {
			//createThumbnail(inImage, os, maxWidth, maxHeight);
			createSquareThumbnail(inImage, os, maxSize);
		} catch (ImageFormatException e) {
			throw new ThumbnailException(e);
		} catch (IOException e) {
			throw new ThumbnailException(e);
		}
	}

	public static void createThumbnail(byte[] imageData, String thumb,
			int maxWidth, int maxHeight) throws ThumbnailException {
		// Get the image from the byte array.
		Image inImage = new ImageIcon(imageData).getImage();
		try {
			// Open output stream for the thumbnail file.
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					thumb));
			try {
				createRectThumbnail(inImage, os, maxWidth, maxHeight);
			} finally {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			throw new ThumbnailException(e);
		} catch (ImageFormatException e) {
			throw new ThumbnailException(e);
		}
	}

	public static void createThumbnail(byte[] imageData, String thumb,
			int maxSize) throws ThumbnailException {
		// Get the image from the byte array.
		Image inImage = new ImageIcon(imageData).getImage();
		try {
			// Open output stream for the thumbnail file.
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					thumb));
			try {
				createSquareThumbnail(inImage, os, maxSize);
			} finally {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			throw new ThumbnailException(e);
		} catch (ImageFormatException e) {
			throw new ThumbnailException(e);
		}
	}

	/**
	 * Reads an image in a file and creates a thumbnail in another file.
	 * @param orig      The name of image file.
	 * @param thumb     The name of thumbnail file. Will be created if necessary.
	 * @param maxWidth  The maximum width of the thumbnail.
	 * @param maxHeight The maximum height of the thumbnail.
	 * The optimum fit is selected based on the width and height of the input image.
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws ImageFormatException 
	 */
	public static void createThumbnail(String orig, String thumb, int maxWidth,
			int maxHeight) throws ThumbnailException {
		try {
			// Get the image from a file.
			Image inImage = new ImageIcon(orig).getImage();
			// Open output stream for the thumbnail file.
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					thumb));
			try {
				createRectThumbnail(inImage, os, maxWidth, maxHeight);
			} finally {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			throw new ThumbnailException(e);
		} catch (ImageFormatException e) {
			throw new ThumbnailException(e);
		}
	}

	
	public static void createThumbnail(String orig, String thumb, int maxSize) throws ThumbnailException {
		try {
			// Get the image from a file.
			Image inImage = new ImageIcon(orig).getImage();
			// Open output stream for the thumbnail file.
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					thumb));
			try {
				createSquareThumbnail(inImage, os, maxSize);
			} finally {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			throw new ThumbnailException(e);
		} catch (ImageFormatException e) {
			throw new ThumbnailException(e);
		}
	}

	
	
	
	
	
	private static void createRectThumbnail(Image inImage, OutputStream os,
			int maxWidth, int maxHeight) throws ImageFormatException,
			IOException {
		// Determine the scale.
		int sourceWidth = inImage.getWidth(null);
		int sourceHeight = inImage.getHeight(null);

		if(sourceWidth < 0 || sourceHeight < 0) {
			throw new ImageFormatException("Format not supported by AWT");
		}
		
		double scaleWidth = (double) maxWidth / (double) sourceWidth;
		double scaleHeight = (double) maxHeight / (double) sourceHeight;
		double scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;

		// If the image is smaller than the desired image size,
		// don't bother scaling.
		if(scale > 1.0d) {
			scale = 1.0d;
		}
		
		// Determine size of new image. 
		// One of them should equal maxDim.
		int scaledW = (int) (scale * sourceWidth);
		int scaledH = (int) (scale * sourceHeight);

		// Create an image buffer in which to paint on.
		BufferedImage outImage = new BufferedImage(scaledW, scaledH,
				BufferedImage.TYPE_INT_RGB);

		// Set the scale.
		AffineTransform tx = new AffineTransform();
		if (scale < 1.0d) {
			tx.scale(scale, scale);
		}

		// Paint image.
		Graphics2D g2d = outImage.createGraphics();
/*
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, scaledW, scaledH);
*/
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.drawImage(inImage, tx, null);
		g2d.dispose();

		// JPEG-encode the image and write to the stream
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(outImage);
        param.setQuality(0.85F, true);
		encoder.encode(outImage, param);
	}

	
	
	private static void createSquareThumbnail(Image inImage, OutputStream os,
			int maxSize) throws ImageFormatException,
			IOException {

		// We'll be using these numbers a lot
		int sourceWidth = inImage.getWidth(null);
		int sourceHeight = inImage.getHeight(null);

		if(sourceWidth < 0 || sourceHeight < 0) {
			throw new ImageFormatException("Format not supported by AWT");
		}
		
		// Determine the scale.
		double scaleWidth = (double) maxSize / (double) sourceWidth;
		double scaleHeight = (double) maxSize / (double) sourceHeight;
		double scale = scaleWidth < scaleHeight ? scaleHeight : scaleWidth;

		// Determine size of new image. 
		// One of them should equal maxDim.
		int scaledW = (int) (scale * sourceWidth);
		int scaledH = (int) (scale * sourceHeight);

		// Create an image buffer in which to paint on.
		BufferedImage outImage = new BufferedImage(scaledW, scaledH,
				BufferedImage.TYPE_INT_RGB);
		BufferedImage clippedImage;

		Graphics2D g2d = outImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);


        
		g2d.drawImage(inImage, 0, 0, scaledW, scaledH, null);
		g2d.dispose();

		// Establish a clipping region to make the output image square
		// (unless it is already square)

		if (scaledW > scaledH) {
			int offset = (scaledW - scaledH) / 2;
			clippedImage = outImage.getSubimage(offset, 0, scaledH, scaledH);
		} else if (scaledW < scaledH) {
			int offset = (scaledH - scaledW) / 2;
			clippedImage = outImage.getSubimage(0, offset, scaledW, scaledW);
		} else {
			clippedImage = outImage;
		}

		// JPEG-encode the image and write to the stream
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(clippedImage);
        param.setQuality(0.85F, true);
		encoder.encode(clippedImage, param);
	}
	
}
