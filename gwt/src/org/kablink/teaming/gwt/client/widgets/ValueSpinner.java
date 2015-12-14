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

import org.kablink.teaming.gwt.client.widgets.Spinner.SpinnerResources;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link ValueSpinner} is a combination of a {@link TextBox} and a
 * {@link Spinner} to allow spinning <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-ValueSpinner { primary style }</li>
 * <li>.gwt-ValueSpinner .textBox { the textbox }</li>
 * <li>.gwt-ValueSpinner .arrows { the spinner arrows }</li>
 * </ul>
 * 
 * 20110712 (DRF):
 *    I copied this class from the GWT incubator into the Vibe OnPrem
 *    ssf (open) source tree.
 */
public class ValueSpinner extends HorizontalPanel {
	/**
	 * Resources used.
	 */
	public interface ValueSpinnerResources extends ClientBundle {
		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/bg_textbox.png")
		ImageResource background();
	}

	private static final String STYLENAME_DEFAULT = "gwt-ValueSpinner";

	private Spinner spinner;
	private final TextBox valueBox = new TextBox();

	private SpinnerListener spinnerListener = new SpinnerListener() {
		@Override
		public void onSpinning(double value) {
			if (getSpinner() != null) {
				getSpinner().setValue(value, false);
			}
			valueBox.setText(formatValue(value));
		}
	};

	private KeyPressHandler keyPressHandler = new KeyPressHandler() {

		@Override
		public void onKeyPress(KeyPressEvent event) {
			int index = valueBox.getCursorPos();
			String previousText = valueBox.getText();
			String newText;
			if (valueBox.getSelectionLength() > 0) {
				newText = previousText.substring(0, valueBox.getCursorPos())
						+ event.getCharCode()
						+ previousText.substring(valueBox.getCursorPos()
								+ valueBox.getSelectionLength(),
								previousText.length());
			} else {
				newText = previousText.substring(0, index)
						+ event.getCharCode()
						+ previousText.substring(index, previousText.length());
			}

	        int keyCode;

	        // Get the key the user pressed
	        keyCode = event.getNativeEvent().getKeyCode();
	        
	        // Only cancel the key pressed if the key is not a navigation key or delete key.
	        if ( keyCode != KeyCodes.KEY_TAB &&
	           	 keyCode != KeyCodes.KEY_BACKSPACE &&
	           	 keyCode != KeyCodes.KEY_DELETE &&
	           	 keyCode != KeyCodes.KEY_ENTER &&
	           	 keyCode != KeyCodes.KEY_HOME &&
	           	 keyCode != KeyCodes.KEY_END &&
	           	 keyCode != KeyCodes.KEY_LEFT &&
	           	 keyCode != KeyCodes.KEY_UP &&
	           	 keyCode != KeyCodes.KEY_RIGHT &&
	           	 keyCode != KeyCodes.KEY_DOWN )
	        {
				valueBox.cancelKey();
	        }

	        try {
				double newValue = parseValue(newText);
				if (spinner.isConstrained()
						&& (newValue > spinner.getMax() || newValue < spinner
								.getMin())) {
					return;
				}
				spinner.setValue(newValue, true);
			} catch (Exception e) {
				// valueBox.cancelKey();
			}
		}
	};

	/**
	 * @param value	Initial value.
	 */
	public ValueSpinner(double value) {
		this(value, 0, 0, 1, 99, false);
	}

	/**
	 * @param value		Initial value.
	 * @param styles	The styles and images used by this widget.
	 * @param images	The images used by the spinner.
	 */
	public ValueSpinner(double value, ValueSpinnerResources styles, SpinnerResources images) {
		this(value, 0, 0, 1, 99, false, styles, images);
	}

	/**
	 * @param value	Initial value.
	 * @param min	Minimum value.
	 * @param max	Maximum value.
	 */
	public ValueSpinner(double value, int min, int max) {
		this(value, min, max, 1, 99, true);
	}

	/**
	 * @param value		Initial value.
	 * @param min		Minimum value.
	 * @param max		Maximum value.
	 * @param minStep	Minimum value for stepping
	 * @param maxStep	Maxiumum value for stepping
	 */
	public ValueSpinner(double value, int min, int max, int minStep, int maxStep) {
		this(value, min, max, minStep, maxStep, true);
	}

	/**
	 * @param value			Initial value.
	 * @param min			Minimum value.
	 * @param max			Maximum value.
	 * @param minStep		Minimum value for stepping.
	 * @param maxStep		Maximum value for stepping.
	 * @param constrained	If set to false minimum and maximum values will not have any effect.
	 */
	public ValueSpinner(double value, int min, int max, int minStep, int maxStep, boolean constrained) {
		this(value, min, max, minStep, maxStep, constrained, null);
	}

	/**
	 * @param value			Initial value.
	 * @param min			Minimum value.
	 * @param max			Maximum value.
	 * @param minStep		Minimum value for stepping.
	 * @param maxStep		Maximum value for stepping.
	 * @param constrained	If set to false minimum and maximum values will not have any effect.
	 * @param resources		The styles and images used by this widget.
	 */
	public ValueSpinner(double value, int min, int max, int minStep, int maxStep, boolean constrained, ValueSpinnerResources resources) {
		this(value, min, max, minStep, maxStep, constrained, resources, null);
	}

	/**
	 * @param value			Initial value.
	 * @param min			Minimum value.
	 * @param max			Maximum value.
	 * @param minStep		Minimum value for stepping.
	 * @param maxStep		Maximum value for stepping.
	 * @param constrained	If set to false minimum and maximum values will not have any effect.
	 * @param resources		The styles and images used by this widget.
	 * @param images		The images used by the spinner.
	 */
	public ValueSpinner(double value, int min, int max, int minStep, int maxStep, boolean constrained, ValueSpinnerResources resources, SpinnerResources images) {
		super();
		setStylePrimaryName(STYLENAME_DEFAULT);
		if (images == null) {
			spinner = new Spinner(spinnerListener, value, min, max, minStep,
					maxStep, constrained);
		} else {
			spinner = new Spinner(spinnerListener, value, min, max, minStep,
					maxStep, constrained, images);
		}
		valueBox.setStyleName("textBox");
		valueBox.addKeyPressHandler(keyPressHandler);
		setVerticalAlignment(ALIGN_MIDDLE);
		add(valueBox);
		VerticalPanel arrowsPanel = new VerticalPanel();
		arrowsPanel.setStylePrimaryName("arrows");
		arrowsPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		arrowsPanel.add(spinner.getIncrementArrow());
		arrowsPanel.add(spinner.getDecrementArrow());
		add(arrowsPanel);
	}

	/**
	 * @return	The Spinner used by this widget.
	 */
	public Spinner getSpinner() {
		return spinner;
	}

	/**
	 * @return	The SpinnerListener used to listen to the {@link Spinner} events.
	 */
	public SpinnerListener getSpinnerListener() {
		return spinnerListener;
	}

	/**
	 * @return	The TextBox used by this widget.
	 */
	public TextBox getTextBox() {
		return valueBox;
	}

	/**
	 * @return	Whether this widget is enabled.
	 */
	public boolean isEnabled() {
		return spinner.isEnabled();
	}

	/**
	 * Sets whether this widget is enabled.
	 * 
	 * @param enabled	True to enable the widget, false to disable it.
	 */
	public void setEnabled(boolean enabled) {
		spinner.setEnabled(enabled);
		valueBox.setEnabled(enabled);
	}

	/**
	 * @param value	The value to format.
	 * 
	 * @return	The formatted value.
	 */
	protected String formatValue(double value) {
		return String.valueOf(value);
	}

	/**
	 * @param value	The value to parse.
	 * 
	 * @return	The parsed value.
	 */
	protected double parseValue(String value) {
		return Long.valueOf(value);
	}
	
    /**
	 * Clears the value from the spinner.
	 */
	public void clearValue() {
		getTextBox().setValue("");
	}
	
	/**
	 * Returns true if the spinner has a value and false otherwise.
	 * 
	 * @return
	 */
	public boolean hasValue() {
		String v = getTextBox().getValue();
		return ((null != v) && (0 < v.length()));
	}
}
