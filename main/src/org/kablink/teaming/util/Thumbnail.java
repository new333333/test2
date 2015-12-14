/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Thumbnail {
	private static Log logger = LogFactory.getLog(Thumbnail.class);

	/*
	 * Inner class used to catch headless errors potentially thrown by
	 * ImageIcon.
	 * 
	 * See:
	 *    http://java.sun.com/developer/technicalArticles/J2SE/Desktop/headless/
	 */
	private static class ThumbImageIcon {
		// By default, we'll use whatever headless setting that's
		// currently set.
		public  static Image getImage(byte[] imageData) {
			return getImage_Impl(imageData, null);
		}
		
		public static Image getImage(String imageFName) {
			return getImage_Impl(null, imageFName);
		}
		
		private static Image getImage_Impl(byte[] imageData, String imageFName) {
			ImageIcon imageIcon = null;
			Throwable th = null;
			
			try {
				// Can we construct the ImageIcon?
				if (null == imageData) {
					imageIcon = new ImageIcon(imageFName);
				} else {
					imageIcon = new ImageIcon(imageData);
				}
			}
			catch (Throwable t) {
				// No!  Save the Exception.
				th = t;
				imageIcon = null;
			}

			// If we weren't able to get an ImageIcon...
			if (null == imageIcon) {
				// ...tell the user about the problem and return null.
				if (null == th) {
					logger.error("Can't construct an image for the thumbnail.");
				} else {
					logger.error(th.getLocalizedMessage(), th);
				}
				return null;
			}
			
			// ...otherwise, return the Image from the ImageIcon.
			return imageIcon.getImage();
		}
	}

	
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
		Image inImage = ThumbImageIcon.getImage(imageData);
		if (null == inImage) {
			throw new ThumbnailException();
		}
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
		Image inImage = ThumbImageIcon.getImage(imageData);
		if (null == inImage) {
			throw new ThumbnailException();
		}
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
		Image inImage = ThumbImageIcon.getImage(imageData);
		if (null == inImage) {
			throw new ThumbnailException();
		}
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
		Image inImage = ThumbImageIcon.getImage(imageData);
		if (null == inImage) {
			throw new ThumbnailException();
		}
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
			Image inImage = ThumbImageIcon.getImage(orig);
			if (null == inImage) {
				throw new ThumbnailException();
			}
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
			Image inImage = ThumbImageIcon.getImage(orig);
			if (null == inImage) {
				throw new ThumbnailException();
			}
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
