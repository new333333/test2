/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kablink.teaming.gwt.client.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that displays progress on an arbitrary scale.
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-ProgressBar-shell { primary style }</li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-bar { the actual progress bar }</li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text { text on the bar }</li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text-firstHalf { applied to text
 * when progress is less than 50 percent }</li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text-secondHalf { applied to text
 * when progress is greater than 50 percent }</li>
 * </ul>
 * 
 * 20120223 (DRF):
 *    I copied this class from the GWT incubator into the Vibe ssf
 *    (open) source tree.
 */
public class ProgressBar extends Widget implements RequiresResize {
	private static final String DEFAULT_TEXT_CLASS_NAME = "gwt-ProgressBar-text";

	private String m_textClassName				=  DEFAULT_TEXT_CLASS_NAME;
	private String m_textFirstHalfClassName		= (DEFAULT_TEXT_CLASS_NAME + "-firstHalf" );
	private String m_textSecondHalfClassName	= (DEFAULT_TEXT_CLASS_NAME + "-secondHalf");

	/**
	 * A formatter used to format the text displayed in the progress
	 * bar widget.
	 */
	public abstract static class TextFormatter {
		/**
		 * Generate the text to display in the ProgressBar based on the
		 * current value.
		 * 
		 * Override this method to change the text displayed within the
		 * ProgressBar.
		 * 
		 * @param bar
		 * @param curProgress
		 * 
		 * @return
		 */
		protected abstract String getText(ProgressBar bar, double curProgress);
	}

	private Element			m_barElement;			// The bar element that displays the progress.
	private double			m_curProgress;			// The current progress.
	private double			m_maxProgress;			// The maximum progress.
	private double			m_minProgress;			// The minimum progress.
	private boolean			m_textVisible = true;	// A boolean that determines if the text is visible.
	private Element			m_textElement;			// The element that displays text on the page.
	private TextFormatter	m_textFormatter;		// The current text formatter.

	/**
	 * Constructor method.
	 * 
	 * Create a progress bar with default range of 0 to 100.
	 */
	public ProgressBar() {
		this(0.0, 100.0, 0.0);
	}

	/**
	 * Constructor method.
	 * 
	 * Create a progress bar with an initial progress and a default
	 * range of 0  to 100.
	 * 
	 * @param curProgress
	 */
	public ProgressBar(double curProgress) {
		this(0.0, 100.0, curProgress);
	}

	/**
	 * Constructor method.
	 * 
	 * Create a progress bar within the given range.
	 * 
	 * @param minProgress
	 * @param maxProgress
	 */
	public ProgressBar(double minProgress, double maxProgress) {
		this(minProgress, maxProgress, 0.0);
	}

	/**
	 * Constructor method.
	 * 
	 * Create a progress bar within the given range starting at the
	 * specified progress amount.
	 * 
	 * @param minProgress
	 * @param maxProgress
	 * @param curProgress
	 */
	public ProgressBar(double minProgress, double maxProgress, double curProgress) {
		this(minProgress, maxProgress, curProgress, null);
	}

	/**
	 * Constructor method.
	 * 
	 * Create a progress bar within the given range starting at the
	 * specified progress amount.
	 * 
	 * @param minProgress
	 * @param maxProgress
	 * @param curProgress
	 * @param textFormatter
	 */
	public ProgressBar(double minProgress, double maxProgress, double curProgress, TextFormatter textFormatter) {
		m_minProgress = minProgress;
		m_maxProgress = maxProgress;
		m_curProgress = curProgress;
		setTextFormatter(textFormatter);

		// Create the outer shell
		setElement(DOM.createDiv());
		DOM.setStyleAttribute(getElement(), "position", "relative");
		setStyleName("gwt-ProgressBar-shell");

		// Create the bar element
		m_barElement = DOM.createDiv();
		DOM.appendChild(getElement(), m_barElement);
		DOM.setStyleAttribute(m_barElement, "height", "100%");
		setBarStyleName("gwt-ProgressBar-bar");

		// Create the text element
		m_textElement = DOM.createDiv();
		DOM.appendChild(getElement(), m_textElement);
		DOM.setStyleAttribute(m_textElement, "position", "absolute");
		DOM.setStyleAttribute(m_textElement, "top", "0px");

		// Set the current progress
		setProgress(m_curProgress);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public    boolean       isTextVisible()    {return m_textVisible;  }
	public    double        getMaxProgress()   {return m_maxProgress;  }
	public    double        getMinProgress()   {return m_minProgress;  }
	public    double        getProgress()      {return m_curProgress;  }
	protected Element       getBarElement()    {return m_barElement;   }
	protected Element       getTextElement()   {return m_textElement;  }
	public    TextFormatter getTextFormatter() {return m_textFormatter;}

	/**
	 * Get the current percent complete, relative to the minimum and
	 * maximum values.  The percent will always be between 0.0 - 1.0.
	 * 
	 * @return
	 */
	public double getPercent() {
		// If we have no range
		if (m_maxProgress <= m_minProgress) {
			return 0.0;
		}

		// Calculate the relative progress
		double percent = ((m_curProgress - m_minProgress) / (m_maxProgress - m_minProgress));
		return Math.max(0.0, Math.min(1.0, percent));
	}

	/**
	 * This method is called when the dimensions of the parent element
	 * change.
	 */
	@Override
	public void onResize() {
		int width  = DOM.getElementPropertyInt(getElement(), "clientWidth");
		onResizeImpl(width);
	}

	/*
	 * Move the text to the center of the progress bar.
	 */
	private void onResizeImpl(int width) {
		if (m_textVisible) {
			int textWidth = DOM.getElementPropertyInt(m_textElement, "offsetWidth");
			int left      = ((width / 2) - (textWidth / 2));
			
			DOM.setStyleAttribute(m_textElement, "left", left + "px");
		}
	}

	/**
	 * Redraw the progress bar when something changes the layout.
	 */
	public void redraw() {
		if (isAttached()) {
			int width  = DOM.getElementPropertyInt(getElement(), "clientWidth");
			onResizeImpl(width);
		}
	}

	public void setBarStyleName(String barClassName) {
		DOM.setElementProperty(m_barElement, "className", barClassName);
	}

	/**
	 * Set the maximum progress. If the minimum progress is more than
	 * the current progress, the current progress is adjusted to be
	 * within the new range.
	 * 
	 * @param maxProgress
	 */
	public void setMaxProgress(double maxProgress) {
		m_maxProgress = maxProgress;
		m_curProgress = Math.min(m_curProgress, m_maxProgress);
		
		resetProgress();
	}

	/**
	 * Set the minimum progress. If the minimum progress is more than
	 * the current progress, the current progress is adjusted to be
	 * within the new range.
	 * 
	 * @param minProgress
	 */
	public void setMinProgress(double minProgress) {
		m_minProgress = minProgress;
		m_curProgress = Math.max(m_curProgress, m_minProgress);
		
		resetProgress();
	}

	/**
	 * Set the current progress.
	 * 
	 * @param curProgress
	 */
	public void setProgress(double curProgress) {
		m_curProgress = Math.max(m_minProgress, Math.min(m_maxProgress, curProgress));

		// Calculate percent complete
		int percent = (int) (100 * getPercent());
		DOM.setStyleAttribute( m_barElement,  "width",     (percent + "%"));
		DOM.setElementProperty(m_textElement, "innerHTML", generateText(m_curProgress));
		
		updateTextStyle(percent);

		// Realign the text
		redraw();
	}

	/**
	 * Increments the current progress by the given amount.
	 * 
	 * @param curIncrement
	 */
	public void incrProgress(double curIncrement) {
		setProgress(m_curProgress + curIncrement);
	}

	public void setTextFirstHalfStyleName(String textFirstHalfClassName) {
		m_textFirstHalfClassName = textFirstHalfClassName;
		onTextStyleChange();
	}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setTextFormatter(          TextFormatter textFormatter)           {m_textFormatter           = textFormatter;                              }
	public void setTextSecondHalfStyleName(String        textSecondHalfClassName) {m_textSecondHalfClassName = textSecondHalfClassName;onTextStyleChange();}
	public void setTextStyleName(          String        textClassName)           {m_textClassName           = textClassName; onTextStyleChange();         }

	/**
	 * Sets whether the text is visible over the bar.
	 * 
	 * @param textVisible
	 */
	public void setTextVisible(boolean textVisible) {
		m_textVisible = textVisible;
		if (m_textVisible) {
			DOM.setStyleAttribute(m_textElement, "display", "");
			redraw();
			
		} else {
			DOM.setStyleAttribute(m_textElement, "display", "none");
		}
	}

	/**
	 * Generate the text to display within the progress bar. Override
	 * this function to change the default progress percent to a more
	 * informative message, such as the number of kilobytes downloaded.
	 * 
	 * @param curProgress
	 *            
	 * @return
	 */
	protected String generateText(double curProgress) {
		if (m_textFormatter != null)
		     return m_textFormatter.getText(this, curProgress);
		else return (int) (100 * getPercent()) + "%";
	}

	/**
	 * This method is called immediately after a widget becomes
	 * attached to the browser's document.
	 */
	@Override
	protected void onLoad() {
		super.onLoad();
		
		// Reset the position attribute of the parent element
		DOM.setStyleAttribute(getElement(), "position", "relative");
		redraw();
	}

	@Override
	protected void onUnload() {
		super.onUnload();
	}

	/**
	 * Reset the progress text based on the current minimum and maximum
	 * progress range.
	 */
	protected void resetProgress() {
		setProgress(getProgress());
	}
	
	private void onTextStyleChange() {
		int percent = ((int) (100 * getPercent()));
		updateTextStyle(percent);
	}

	private void updateTextStyle(int percent) {
		// Set the style depending on the size of the bar
		if (percent < 50)
		     DOM.setElementProperty(m_textElement, "className", (m_textClassName + " " + m_textFirstHalfClassName ));
		else DOM.setElementProperty(m_textElement, "className", (m_textClassName + " " + m_textSecondHalfClassName));
	}
}
