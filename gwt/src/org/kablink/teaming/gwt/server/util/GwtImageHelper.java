/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.swing.ImageIcon;

import javaxt.io.Image;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.MiscUtil;

import org.springframework.util.FileCopyUtils;

/**
 * Helper methods for the GWT UI server code that services requests
 * dealing with images.
 *
 * @author drfoster@novell.com
 */
public class GwtImageHelper {
	protected static Log m_logger = LogFactory.getLog(GwtImageHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtImageHelper() {
		// Nothing to do.
	}

	/*
	 * If possible, determines an image's rotation and returns it.  If
	 * the rotation can't be determined, 0 is returned.
	 */
	private static int getImageRotation(byte[] imageData) {
		String desc = "*Can't Deteremine Rotation*";
		int reply = 0;

		try {
			Image image = new Image(imageData);
			HashMap<Integer, Object> exif = image.getExifTags();
			if (MiscUtil.hasItems(exif)) {
				switch ((Integer) exif.get(0x0112)) {
				default: desc = "*Unknown Rotation*";                                                   break;
				case 1:  desc = "Top, left side (Horizontal / normal)";                                 break;
				case 2:  desc = "Top, right side (Mirror horizontal)";                                  break;
				case 3:  desc = "Bottom, right side (Rotate 180)";                         reply = 180; break;
				case 4:  desc = "Bottom, left side (Mirror vertical)";                                  break;
				case 5:  desc = "Left side, top (Mirror horizontal and rotate 270 CW)";    reply = 270; break;
				case 6:  desc = "Right side, top (Rotate 90 CW)";                          reply =  90; break;
				case 7:  desc = "Right side, bottom (Mirror horizontal and rotate 90 CW)"; reply =  90; break;
				case 8:  desc = "Left side, bottom (Rotate 270 CW)";                       reply = 270; break;
				}
			}
		}
		
		catch (Exception ex) {
			m_logger.debug("GwtImageHelper.getImageRotation( EXCEPTION ):  ", ex);
		}
		
		m_logger.debug("GwtImageHelper.getImageRotation( " + reply + " ):  " + desc);
		return reply;
	}
	
	/**
	 * If the file's data can be viewed as an image, returns a URL to
	 * reference in an <IMG> tag.  Otherwise, returns null.
	 * 
	 * @param bs
	 * @param request
	 * @param fed
	 * @param fe
	 * @param fa
	 */
	public static void setImageContentDetails(AllModulesInjected bs, HttpServletRequest request, FolderEntryDetails fed, FolderEntry fe, FileAttachment fa) {
		try {
			// Can we get a filename from the attachment?
			String fName = fa.getFileItem().getName();
			int fNameLength;
			if (null != fName) {
				// Yes!  Trim it and convert it to lower case.
				fName = fName.trim().toLowerCase();
				fNameLength = fName.length();
			}
			else {
				fNameLength = 0;
			}
			if (0 == fNameLength) {
				// No, then we don't have an image!  Bail.
				fed.setContentIsImage(false);
				return;
			}
			
			// Does that filename have an extension?
			int pPos = fName.lastIndexOf('.');
			if (0 >= pPos) {
				// No, then we don't have an image!  Bail.
				fed.setContentIsImage(false);
				return;
			}

			// Is the filename's extension one we recognize as an
			// image?
			String fExt = fName.substring(pPos + 1);
			boolean contentIsImage = (fExt.equals("gif") || fExt.equals("jpg") || fExt.equals("jpeg") || fExt.equals("png"));
			if (!contentIsImage) {
				// No, then we don't have an image!  Bail.
				fed.setContentIsImage(false);
				return;
			}

			// Can we construct a Java ImageIcon from the file's data?
			InputStream	inputStream = bs.getFileModule().readFile(fe.getParentBinder(), fe, fa);
			byte[]		inputData   = FileCopyUtils.copyToByteArray(inputStream);
			ImageIcon	imageIcon   = new ImageIcon(inputData);
			contentIsImage =
				((null != imageIcon)                 &&
				 (0     < imageIcon.getIconHeight()) &&
				 (0     < imageIcon.getIconWidth()));
			fed.setContentIsImage(contentIsImage);
			if (contentIsImage) {
				// Yes!  Determine the image's size and rotation.
				int height   = imageIcon.getIconHeight();
				int rotation = getImageRotation(inputData);
				int width    = imageIcon.getIconWidth();
				switch (Math.abs(rotation)) {
				default:
				case 0:
				case 180: break;	// Leave rotation asis.
				case 90:
				case 270:
					// To rotate the image by 1/4 or 3/4, we need to
					// swap the height and width.
					int heightTmp = height;
					height        = width;
					width         = heightTmp;
				}
				fed.setContentImageHeight(  height  );
				fed.setContentImageWidth(   width   );
				fed.setContentImageRotation(rotation);
			}
		}
		
		catch (Exception ex) {
			// Any exception we handle as though the file can't be
			// displayed as an image. 
			fed.setContentIsImage(false);
		}
	}
}
