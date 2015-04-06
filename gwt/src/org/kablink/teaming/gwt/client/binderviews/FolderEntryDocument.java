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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;

/**
 * Class that holds the folder entry viewer document area.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntryDocument extends VibeFlowPanel {
	private FolderEntryCallback				m_fec;			// Callback to the folder entry composite.
	private FolderEntryDetails				m_fed;			// The full details about the folder entry being viewed.
	private Frame							m_htmlFrame;	// The <IFRAME> containing rendered HTML when it's available from the file.
	private GwtTeamingDataTableImageBundle	m_images;		// Access to Vibe's images.
	private Image							m_contentImage;	//

	private final static int	NO_VSCROLL_ADJUST		= 20;
	private final static String VIEW_DOCUMENT_FRAME_ID	= "ss_iframe_fileview_GWT";

	private final static int	IMAGE_MINIMUM   = 200;	// Minimum size an image should be scaled to.
	private final static int	IMAGE_OVERHEAD	=  40;	// Pixels of overhead required for an image within the document view.
	
	public FolderEntryDocument(FolderEntryCallback fec, FolderEntryDetails fed) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_fec = fec;
		m_fed = fed;
		
		// ...initialize the data members requiring it...
		m_images = GwtTeaming.getDataTableImageBundle();
		
		// ...and construct the document's content.
		createContent();
	}
	
	/*
	 * Creates the document's content.
	 */
	private void createContent() {
		// Do we have HTML to render for the document?
		String contentSpecificStyles;
		ViewFileInfo vfi = m_fed.getHtmlView();
		String htmlUrl = ((null != vfi) ? vfi.getViewFileUrl() : "");
		boolean isTrashed = m_fed.isTrashed();
		if (GwtClientHelper.hasString(htmlUrl)) {
			// Yes!  Create an <IFRAME> for to render into...
			m_htmlFrame = new Frame();
			m_htmlFrame.addStyleName("vibe-feView-documentFrame");
			Element dfE = m_htmlFrame.getElement(); 
			dfE.setId(VIEW_DOCUMENT_FRAME_ID);
			dfE.setAttribute("frameBorder",  "0");
			dfE.setAttribute("scrolling", "auto");
			m_htmlFrame.setUrl(  htmlUrl    );
			m_htmlFrame.setTitle(m_fed.getTitle());
			add(m_htmlFrame);
			
			// ...and add the HTML specific content style.
			contentSpecificStyles = "vibe-feView-documentPanelHtml";
		}
		
		else {
			// No, we don't have HTML to render for the document!  Do
			// we have a URL we can use as an <IMG> for the file?
			String imgUrl = m_fed.getDownloadUrl();
			String imgStyle;
			if ((!isTrashed) && m_fed.isContentImage() && GwtClientHelper.hasString(imgUrl)) {
				// Yes!  We'll display that with an appropriate style.
				imgStyle = "vibe-feView-documentContentImage";
				
				// ...and add the image specific content style.
				contentSpecificStyles = "vibe-feView-documentPanelImage";
			}
			
			else {
				// No, we don't have the URL for an image
				// representation of the file's data either!  Add the
				// unknown image...
				imgUrl   = m_images.unknown().getSafeUri().asString();
				imgStyle = "vibe-feView-documentUnknownImage";
				
				// ...and add the unknown specific content style.
				contentSpecificStyles = "vibe-feView-documentPanelUnknown";
			}

			// Add the appropriate image. 
			m_contentImage = GwtClientHelper.buildImage(imgUrl);
			m_contentImage.addStyleName(imgStyle);
			m_contentImage.setWidth(IMAGE_MINIMUM + "px");	// Will get corrected on the first resize cycle.  See onResize() below.
			int rotation = m_fed.getContentImageRotation();
			if (0 != rotation) {
				Style  styles = m_contentImage.getElement().getStyle();
				String genericRotateStyleValue = ("rotate(" + rotation + "deg)");
				styles.setProperty("transform", genericRotateStyleValue);
				if (GwtClientHelper.jsIsIE()) {
					switch (rotation) {
					default:   rotation = 0; break;
					case  90:  rotation = 1; break;
					case 180:  rotation = 2; break;
					case 270:  rotation = 3; break;
					}
					if (0 != rotation) {
						String ieRotateStyleValue = ("progid:DXImageTransform.Microsoft.BasicImage(rotation=" + rotation + ")");
						styles.setProperty("filter", ieRotateStyleValue);
					}
				}
				else {
					styles.setProperty("webkitTransform", genericRotateStyleValue);
					styles.setProperty("mozTransform",    genericRotateStyleValue);
				}
			}
			add(m_contentImage);
			
		}
		
		// Add the panel's styles...
		addStyleName("vibe-feView-documentPanel");
		addStyleName(contentSpecificStyles);
		
		// ...and tell the composite that we're ready.
		m_fec.viewComponentReady();
	}

	/**
	 * Called when the document gets resized.
	 * 
	 * Overrides the VibeFlowPanel.onReisize() method.
	 */
	@Override
	public void onResize() {
		// Allow the super class to process the sizing...
		super.onResize();

		// ...and set the content image width, if necessary.
		setContentImageWidth();
	}
	
	/*
	 * Sets a content image's width, as necessary.
	 */
	private void setContentImageWidth() {
		// If we're displaying a document as an <IMG>...
		if (m_fed.isContentImage() && (null != m_contentImage)) {
			// ...and the image is wider than the width we have to
			// ...display it in...
			int offsetWidth = (getOffsetWidth() - IMAGE_OVERHEAD);
			if (IMAGE_MINIMUM > offsetWidth) {
				offsetWidth = IMAGE_MINIMUM;
			}
			// ...scale the image to fit, otherwise, remove any scaling
			// ...we may have had.
			if (m_fed.getContentImageWidth() > offsetWidth)
			     m_contentImage.setWidth(offsetWidth + "px");
			else m_contentImage.getElement().getStyle().setProperty("width", "");
		}
	}
	
	/**
	 * Called to set the panel's height.
	 * 
	 * Overrides the UIObject.setHeight() method.
	 */
	@Override
	public void setHeight(final String height) {
		// If we've got an <IFRAME> containing HTML...
		if (null != m_htmlFrame) {
			// ...set its true size after things stabilize...
			m_htmlFrame.setWidth("1px");	// We first set it to 1px so that the getOffsetWidth() call below isn't affected by the current width.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					m_htmlFrame.setHeight(height);
					m_htmlFrame.setWidth((getOffsetWidth() - NO_VSCROLL_ADJUST) + "px");
				}
			});
		}
		else if (m_fed.isContentImage() && (null != m_contentImage)) {
			// ...set its true size after things stabilize...
			m_contentImage.setWidth("1px");	// We first set it to 1px so any getOffsetWidth() calls below aren't affected by the current width.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					setContentImageWidth();
				}
			});
		}

		// ...and pass the height to the super class.
		super.setHeight(height);
	}
}
