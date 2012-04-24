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
 * Licensed under the Apache License, Version 2.0 (the 'License'); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * 20120412 (DRF): I copied this class from the GWT incubator into the
 * 		Vibe ssf (open) source tree.
 * 
 * A widget that allows the user to select a value within a range of
 * possible values using a sliding bar that responds to mouse events.
 * 
 * <h3>Keyboard Events</h3>
 * <p>
 * SliderBar listens for the following key events. Holding down a key will
 * repeat the action until the key is released.
 * <ul class='css'>
 * <li>left arrow - shift left one step</li>
 * <li>right arrow - shift right one step</li>
 * <li>ctrl+left arrow - jump left 10% of the distance</li>
 * <li>ctrl+right arrow - jump right 10% of the distance</li>
 * <li>home - jump to min value</li>
 * <li>end - jump to max value</li>
 * <li>space - jump to middle value</li>
 * </ul>
 * </p>
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-SliderBar-shell { primary style }</li>
 * <li>.gwt-SliderBar-shell gwt-SliderBar-line { the line that the knob moves along }</li>
 * <li>.gwt-SliderBar-shell gwt-SliderBar-line-sliding { the line that the knob moves along when sliding }</li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-knob { the sliding knob }</li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-knob-sliding { the sliding knob when sliding }</li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-tick { the ticks along the line }</li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-label { the text labels along the line }</li>
 * </ul>
 */
public class SliderBar extends FocusPanel implements RequiresResize, HasValue<Double>, HasValueChangeHandlers<Double> {
	/**
	 * A formatter used to format the labels displayed in the widget.
	 */
	public static interface LabelFormatter {
		/**
		 * Generate the text to display in each label based on the
		 * label's value.
		 * 
		 * Override this method to change the text displayed within the
		 * SliderBar.
		 * 
		 * @param slider	The Slider bar.
		 * @param value		The value the label displays.
		 * 
		 * @return	The text to display for the label.
		 */
		String formatLabel(SliderBar slider, double value);
	}

	/**
	 * A {@link ClientBundle} that provides images for
	 * {@link SliderBar}.
	 */
	public static interface SliderBarImages extends ClientBundle {
		public static final SliderBarImages INSTANCE = GWT.create(SliderBarImages.class);

		// Image used for the sliding knob.
		@Source("org/kablink/teaming/gwt/public/images/Widgets/SliderBar/slider.gif")
		ImageResource slider();

		// Image used for the sliding knob.
		@Source("org/kablink/teaming/gwt/public/images/Widgets/SliderBar/sliderDisabled.gif")
		ImageResource sliderDisabled();

		// An image used for the sliding knob while sliding.
		@Source("org/kablink/teaming/gwt/public/images/Widgets/SliderBar/sliderSliding.gif")
		ImageResource sliderSliding();

		@NotStrict
		@Source("org/kablink/teaming/gwt/public/GwtSliderBar.css")
		CssResource sliderBarCss();
	}

	/*
	 * The timer used to continue to shift the knob as the user holds
	 * down one of the left/right arrow keys. Only IE auto-repeats, so
	 * we just keep catching the events.
	 */
	private class KeyTimer extends Timer {
		private boolean	m_firstRun    = true;	// A bit indicating that this is the first run.
		private boolean	m_shiftRight  = false;	// A bit indicating whether we are shifting to a higher or lower value.
		private int		m_multiplier  = 1;		// The number of steps to shift with each press.
		private int		m_repeatDelay = 30;		// The delay between shifts, which shortens as the user holds down the button.

		/**
		 * This method will be called when a timer fires. Override it
		 * to implement the timer's logic.
		 */
		@Override
		public void run() {
			// Highlight the knob on first run.
			if (m_firstRun) {
				m_firstRun = false;
				startSliding(true, false);
			}

			// Slide the slider bar.
			if (m_shiftRight)
			     setCurrentValue(m_curValue + m_multiplier * m_stepSize);
			else setCurrentValue(m_curValue - m_multiplier * m_stepSize);

			// Repeat this timer until cancelled by key up event.
			schedule(m_repeatDelay);
		}

		/**
		 * Schedules a timer to elapse in the future.
		 * 
		 * @param delayMillis	How long to wait before the timer elapses, in milliseconds.
		 * @param shiftRight	Whether to shift up or not.
		 * @param multiplier	The number of steps to shift.
		 */
		public void schedule(int delayMillis, boolean shiftRight, int multiplier) {
			m_firstRun   = true;
			m_shiftRight = shiftRight;
			m_multiplier = multiplier;
			super.schedule(delayMillis);
		}
	}

	private boolean			m_slidingKeyboard = false;					// A bit indicating whether or not we are currently sliding the slider bar due to keyboard events.
	private boolean			m_slidingMouse    = false;					// A bit indicating whether or not we are currently sliding the slider bar due to mouse events.
	private boolean			m_enabled         = true;					// A bit indicating whether or not the slider is enabled.
	private double			m_curValue;									// The current value.
	private double			m_maxValue;									// The maximum slider value.
	private double			m_minValue;									// The minimum slider value.
	private double			m_stepSize;									// The size of the increments between knob positions.
	private Element			m_lineElement;								// The line that the knob moves over.
	private Element			m_knobElement;								//
	private Image			m_knobImage       = new Image();			// The knob that slides across the line.
	private int				m_enabledTabIndex = 0;						//
	private int				m_lineLeftOffset  = 0;						// The offset between the edge of the shell and the line.
	private int				m_numLabels       = 0;						// The number of labels to show.
	private int				m_numTicks        = 0;						// The number of tick marks to show.
	private KeyTimer		m_keyTimer        = new KeyTimer();			// The timer used to continue to shift the knob if the user holds down a key.
	private LabelFormatter	m_labelFormatter;							// The formatter used to generate label text.
	private List<Element>	m_labelElements = new ArrayList<Element>();	// The elements used to display labels above the ticks.
	private List<Element>	m_tickElements  = new ArrayList<Element>();	// The elements used to display tick marks, which are the vertical lines along the slider bar.
	private SliderBarImages	m_images;									// The images used with the sliding bar.

	/**
	 * Create a slider bar.
	 * 
	 * @param minValue	The minimum value in the range.
	 * @param maxValue	The maximum value in the range.
	 */
	public SliderBar(double minValue, double maxValue) {
		this(minValue, maxValue, null);
	}

	/**
	 * Create a slider bar.
	 * 
	 * @param minValue			The minimum value in the range.
	 * @param maxValue			The maximum value in the range.
	 * @param labelFormatter	The label formatter.
	 */
	public SliderBar(double minValue, double maxValue, LabelFormatter labelFormatter) {
		this(minValue, maxValue, labelFormatter, SliderBarImages.INSTANCE);
	}

	/**
	 * Create a slider bar.
	 * 
	 * @param minValue			The minimum value in the range.
	 * @param maxValue			The maximum value in the range.
	 * @param labelFormatter	The label formatter.
	 * @param images			The images to use for the slider.
	 */
	public SliderBar(double minValue, double maxValue, LabelFormatter labelFormatter, SliderBarImages images) {
		super();
		images.sliderBarCss().ensureInjected();
		m_minValue = minValue;
		m_maxValue = maxValue;
		m_images   = images;
		setLabelFormatter(labelFormatter);

		// Create the outer shell.
		DOM.setStyleAttribute(getElement(), "position", "relative");

		// Create the line.
		m_lineElement = DOM.createDiv();
		DOM.appendChild(getElement(), m_lineElement);
		DOM.setStyleAttribute( m_lineElement, "position", "absolute");

		// Create the knob.
		m_knobImage.setResource(images.slider());
		m_knobElement = m_knobImage.getElement();
		DOM.appendChild(getElement(), m_knobElement);
		DOM.setStyleAttribute( m_knobElement, "position", "absolute");
		
		// Set the styles for the slider elements.
		setSliderStyles();

		// Sink the events we care about.
		sinkEvents(Event.MOUSEEVENTS | Event.KEYEVENTS | Event.FOCUSEVENTS);

		// Workaround to render properly when parent Widget does not
		// implement ProvidesResize since DOM doesn't provide element
		// height and width until onModuleLoad() finishes.
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				onResize();
			}
		});
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean        isEnabled()         {return m_enabled;       }
	public double         getCurrentValue()   {return m_curValue;      }
	public double         getMaxValue()       {return m_maxValue;      }
	public double         getMinValue()       {return m_minValue;      }
	public double         getStepSize()       {return m_stepSize;      }
	public int            getNumLabels()      {return m_numLabels;     }
	public int            getNumTicks()       {return m_numTicks;      }
	public LabelFormatter getLabelFormatter() {return m_labelFormatter;}

	/**
	 * Return the total range between the minimum and maximum values.
	 * 
	 * @return	The total range.
	 */
	public double getTotalRange() {
		if (m_minValue > m_maxValue) {
			return 0;
		}
		
		return (m_maxValue - m_minValue);
	}

	@Override
	public Double getValue() {
		return m_curValue;
	}

	/**
	 * Listen for events that will move the knob.
	 * 
	 * @param event	The event that occurred.
	 */
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		if (m_enabled) {
			switch (DOM.eventGetType(event)) {
			// Remove highlight and cancel keyboard events.
			case Event.ONBLUR:
				m_keyTimer.cancel();
				if (m_slidingMouse) {
					DOM.releaseCapture(getElement());
					m_slidingMouse = false;
					slideKnob(event);
					stopSliding(true, true);
				}
				
				else if (m_slidingKeyboard) {
					m_slidingKeyboard = false;
					stopSliding(true, true);
				}
				
				unhighlight();
				break;

			// Highlight on focus.
			case Event.ONFOCUS:
				highlight();
				break;

			// Mouse wheel events.
			case Event.ONMOUSEWHEEL:
				int velocityY = DOM.eventGetMouseWheelVelocityY(event);
				DOM.eventPreventDefault(event);
				if (velocityY > 0)
				     shiftRight(1);
				else shiftLeft( 1);
				break;

			// Shift left or right on key press.
			case Event.ONKEYDOWN:
				if (!m_slidingKeyboard) {
					int multiplier = 1;
					if (DOM.eventGetCtrlKey(event)) {
						multiplier = (int) (getTotalRange() / m_stepSize / 10);
					}

					switch (DOM.eventGetKeyCode(event)) {
					case KeyCodes.KEY_HOME:
						DOM.eventPreventDefault(event);
						setCurrentValue(m_minValue);
						break;
						
					case KeyCodes.KEY_END:
						DOM.eventPreventDefault(event);
						setCurrentValue(m_maxValue);
						break;
						
					case KeyCodes.KEY_LEFT:
						DOM.eventPreventDefault(event);
						m_slidingKeyboard = true;
						startSliding(false, true);
						shiftLeft(multiplier);
						m_keyTimer.schedule(400, false, multiplier);
						break;
						
					case KeyCodes.KEY_RIGHT:
						DOM.eventPreventDefault(event);
						m_slidingKeyboard = true;
						startSliding(false, true);
						shiftRight(multiplier);
						m_keyTimer.schedule(400, true, multiplier);
						break;
						
					case 32:
						DOM.eventPreventDefault(event);
						setCurrentValue(m_minValue + getTotalRange() / 2);
						break;
					}
				}
				break;
				
			// Stop shifting on key up.
			case Event.ONKEYUP:
				m_keyTimer.cancel();
				if (m_slidingKeyboard) {
					m_slidingKeyboard = false;
					stopSliding(true, true);
				}
				break;

			// Mouse Events.
			case Event.ONMOUSEDOWN:
				setFocus(true);
				m_slidingMouse = true;
				DOM.setCapture(getElement());
				startSliding(true, true);
				DOM.eventPreventDefault(event);
				slideKnob(event);
				break;
				
			case Event.ONMOUSEUP:
				if (m_slidingMouse) {
					DOM.releaseCapture(getElement());
					m_slidingMouse = false;
					slideKnob(event);
					stopSliding(true, true);
				}
				break;
				
			case Event.ONMOUSEMOVE:
				if (m_slidingMouse) {
					slideKnob(event);
				}
				break;
			}
		}
	}

	/**
	 * This method is called when the dimensions of the parent element
	 * change.  Subclasses should override this method as needed.
	 * 
	 * @param width		The new client width of the element.
	 * @param height	The new client height of the element.
	 */
	public void onResize(int width, int height) {
		// Center the line in the shell.
		int lineWidth = m_lineElement.getOffsetWidth();
		m_lineLeftOffset = (width / 2) - (lineWidth / 2);
		DOM.setStyleAttribute(m_lineElement, "left", m_lineLeftOffset + "px");

		// Draw the other components.
		drawLabels();
		drawTicks();
		drawKnob();
	}

	/**
	 * Redraw the progress bar when something changes the layout.
	 */
	public void redraw() {
		if (isAttached()) {
			int width  = getElement().getClientWidth();
			int height = getElement().getClientHeight();
			onResize(width, height);
		}
	}

	/**
	 * Set the current value and fire the onValueChange event.
	 * 
	 * @param curValue	The current value.
	 */
	public void setCurrentValue(double curValue) {
		setCurrentValue(curValue, true);
	}

	/**
	 * Set the current value and optionally fire the onValueChange
	 * event.
	 * 
	 * @param curValue	The current value.
	 * @param fireEvent	Fire the onValue change event if true.
	 */
	public void setCurrentValue(double curValue, boolean fireEvent) {
		// Confine the value to the range.
		m_curValue = Math.max(m_minValue, Math.min(m_maxValue, curValue));
		double remainder = (m_curValue - m_minValue) % m_stepSize;
		m_curValue -= remainder;

		// Go to next step if more than halfway there.
		if ((remainder > (m_stepSize / 2)) && ((m_curValue + m_stepSize) <= m_maxValue)) {
			m_curValue += m_stepSize;
		}

		// Redraw the knob.
		drawKnob();

		// Fire the ValueChangeEvent.
		if (fireEvent) {
			ValueChangeEvent.fire(this, m_curValue);
		}
	}

	/**
	 * Sets whether this widget is enabled.
	 * 
	 * @param enabled	true to enable the widget, false to disable it.
	 */
	public void setEnabled(boolean enabled) {
		boolean wasEnabled = m_enabled;
		m_enabled = enabled;
		if (enabled) {
			if (!wasEnabled) {
				setTabIndex(m_enabledTabIndex);
				removeStyleName("gwt-SliderBar-shell-disabled");
			}
			DOM.setElementProperty(m_lineElement, "className", "gwt-SliderBar-line " + ((0 < m_numLabels) ? "gwt-SliderBar-line1" : "gwt-SliderBar-line2"));
			DOM.setElementProperty(m_knobElement, "className", "gwt-SliderBar-knob " + ((0 < m_numLabels) ? "gwt-SliderBar-knob1" : "gwt-SliderBar-knob2"));
			m_knobImage.setResource(m_images.slider());
		}
		
		else {
			if (wasEnabled) {
				m_enabledTabIndex = getTabIndex();
				setTabIndex(-1);
				addStyleName("gwt-SliderBar-shell-disabled");
			}
			DOM.setElementProperty(m_lineElement, "className", "gwt-SliderBar-line gwt-SliderBar-line-disabled " + ((0 < m_numLabels) ? "gwt-SliderBar-line1" : "gwt-SliderBar-line2"));
			DOM.setElementProperty(m_knobElement, "className", "gwt-SliderBar-knob gwt-SliderBar-knob-disabled " + ((0 < m_numLabels) ? "gwt-SliderBar-knob1" : "gwt-SliderBar-knob2"));
			m_knobImage.setResource(m_images.sliderDisabled());
		}
		
		redraw();
	}

	/**
	 * Set the label formatter.
	 * 
	 * @param labelFormatter	The label formatter.
	 */
	public void setLabelFormatter(LabelFormatter labelFormatter) {
		m_labelFormatter = labelFormatter;
	}

	/**
	 * Set the max value.
	 * 
	 * @param maxValue	The current value.
	 */
	public void setMaxValue(double maxValue) {
		m_maxValue = maxValue;
		drawLabels();
		resetCurrentValue();
	}

	/**
	 * Set the minimum value.
	 * 
	 * @param minValue	The current value.
	 */
	public void setMinValue(double minValue) {
		m_minValue = minValue;
		drawLabels();
		resetCurrentValue();
	}

	/**
	 * Set the number of labels to show on the line. Labels indicate
	 * the value of the slider at that point. Use this method to enable
	 * labels.
	 * 
	 * If you set the number of labels equal to the total range divided
	 * by the step size, you will get a properly aligned 'jumping'
	 * effect where the knob jumps between labels.
	 * 
	 * Note that the number of labels displayed will be one more than
	 * the number you specify, so specify 1 labels to show labels on
	 * either end of the line. In other words, numLabels is really the
	 * number of slots between the labels.
	 * 
	 * setNumLabels(0) will disable labels.
	 * 
	 * @param numLabels	The number of labels to show.
	 */
	public void setNumLabels(int numLabels) {
		m_numLabels = numLabels;
		setSliderStyles();
		drawLabels();
	}

	/**
	 * Set the number of ticks to show on the line. A tick is a
	 * vertical line that represents a division of the overall line.
	 * Use this method to enable ticks.
	 * 
	 * If you set the number of ticks equal to the total range divided
	 * by the step size, you will get a properly aligned 'jumping'
	 * effect where the knob jumps between ticks.
	 * 
	 * Note that the number of ticks displayed will be one more than
	 * the number you specify, so specify 1 tick to show ticks on
	 * either end of the line.  In other words, numTicks is really the
	 * number of slots between the ticks.
	 * 
	 * setNumTicks(0) will disable ticks.
	 * 
	 * @param numTicks	The number of ticks to show.
	 */
	public void setNumTicks(int numTicks) {
		m_numTicks = numTicks;
		drawTicks();
	}

	/**
	 * Set the step size.
	 * 
	 * @param stepSize	The current value.
	 */
	public void setStepSize(double stepSize) {
		m_stepSize = stepSize;
		resetCurrentValue();
	}

	@Override
	public void setValue(Double value) {
		setCurrentValue(value, false);
	}

	@Override
	public void setValue(Double value, boolean fireEvent) {
		setCurrentValue(value, fireEvent);
	}

	/**
	 * Shift to the left (smaller value).
	 * 
	 * @param numSteps	The number of steps to shift.
	 */
	public void shiftLeft(int numSteps) {
		setCurrentValue(getCurrentValue() - numSteps * m_stepSize);
	}

	/**
	 * Shift to the right (greater value).
	 * 
	 * @param numSteps	The number of steps to shift.
	 */
	public void shiftRight(int numSteps) {
		setCurrentValue(getCurrentValue() + numSteps * m_stepSize);
	}

	/**
	 * Format the label to display above the ticks.
	 * 
	 * Override this method in a subclass to customize the format. By
	 * default, this method returns the integer portion of the value.
	 * 
	 * @param value	The value at the label.
	 * 
	 * @return	The text to put in the label.
	 */
	protected String formatLabel(double value) {
		if (m_labelFormatter != null) {
			return m_labelFormatter.formatLabel(this, value);
		}
		
		return (int) (10 * value) / 10.0 + "";
	}

	/**
	 * Get the percentage of the knob's position relative to the size
	 * of the line.  The return value will be between 0.0 and 1.0.
	 * 
	 * @return	The current percent complete.
	 */
	protected double getKnobPercent() {
		// If we have no range.
		if (m_maxValue <= m_minValue) {
			return 0;
		}

		// Calculate the relative progress.
		double percent = (m_curValue - m_minValue) / (m_maxValue - m_minValue);
		return Math.max(0.0, Math.min(1.0, percent));
	}

	/**
	 * This method is called immediately after a widget becomes
	 * attached to the browser's document.
	 */
	@Override
	protected void onLoad() {
		// Reset the position attribute of the parent element.
		DOM.setStyleAttribute(getElement(), "position", "relative");
	}

	/*
	 * Draw the knob where it is supposed to be relative to the line.
	 */
	private void drawKnob() {
		// Abort if not attached.
		if (!isAttached()) {
			return;
		}

		// Move the knob to the correct position.
		int lineWidth = m_lineElement.getOffsetWidth();
		int knobWidth = m_knobElement.getOffsetWidth();
		int knobLeftOffset = (int) (m_lineLeftOffset + (getKnobPercent() * lineWidth) - (knobWidth / 2));
		knobLeftOffset = Math.min(knobLeftOffset, m_lineLeftOffset + lineWidth - (knobWidth / 2) - 1);
		DOM.setStyleAttribute(m_knobElement, "left", knobLeftOffset + "px");
	}

	/*
	 * Draw the labels along the line.
	 */
	private void drawLabels() {
		// Abort if not attached.
		if (!isAttached()) {
			return;
		}

		// Draw the labels.
		int lineWidth = m_lineElement.getOffsetWidth();
		if (m_numLabels > 0) {
			// Create the labels or make them visible.
			for (int i = 0; i <= m_numLabels; i += 1) {
				Element label = null;
				if (i < m_labelElements.size()) {
					label = m_labelElements.get(i);
				}
				
				else {
					// Create the new label.
					label = DOM.createDiv();
					DOM.setStyleAttribute(label, "position", "absolute");
					DOM.setStyleAttribute(label, "display",  "none"    );
					if (m_enabled)
					     DOM.setElementProperty(label, "className", "gwt-SliderBar-label"         );
					else DOM.setElementProperty(label, "className", "gwt-SliderBar-label-disabled");
					DOM.appendChild(getElement(), label);
					m_labelElements.add(label);
				}

				// Set the label text.
				double value = m_minValue + (getTotalRange() * i / m_numLabels);
				DOM.setStyleAttribute(label,  "visibility", "hidden"          );
				DOM.setStyleAttribute(label,  "display",    ""                );
				DOM.setElementProperty(label, "innerHTML",  formatLabel(value));

				// Move to the left so the label width is not clipped
				// by the shell.
				DOM.setStyleAttribute(label, "left", "0px");

				// Position the label and make it visible.
				int labelWidth = label.getOffsetWidth();
				int labelLeftOffset = m_lineLeftOffset + (lineWidth * i / m_numLabels) - (labelWidth / 2);
				labelLeftOffset = Math.min(labelLeftOffset, m_lineLeftOffset + lineWidth - labelWidth);
				labelLeftOffset = Math.max(labelLeftOffset, m_lineLeftOffset);
				DOM.setStyleAttribute(label, "left",       (labelLeftOffset + "px"));
				DOM.setStyleAttribute(label, "visibility", "visible"               );
			}

			// Hide unused labels.
			for (int i = (m_numLabels + 1); i < m_labelElements.size(); i += 1) {
				DOM.setStyleAttribute(m_labelElements.get(i), "display", "none");
			}
		}
		
		else {
			// Hide all labels.
			for (Element elem : m_labelElements) {
				DOM.setStyleAttribute(elem, "display", "none");
			}
		}
	}

	/*
	 * Draw the tick along the line.
	 */
	private void drawTicks() {
		// Abort if not attached.
		if (!isAttached()) {
			return;
		}

		// Draw the ticks.
		int lineWidth = m_lineElement.getOffsetWidth();
		if (m_numTicks > 0) {
			// Create the ticks or make them visible.
			for (int i = 0; i <= m_numTicks; i += 1) {
				Element tick = null;
				if (i < m_tickElements.size()) {
					tick = m_tickElements.get(i);
				}
				
				else {
					// Create the new tick.
					tick = DOM.createDiv();
					DOM.setStyleAttribute(tick, "position", "absolute");
					DOM.setStyleAttribute(tick, "display",  "none"    );
					DOM.appendChild(getElement(), tick);
					m_tickElements.add(tick);
				}
				
				if (m_enabled)
				     DOM.setElementProperty(tick, "className", "gwt-SliderBar-tick "                             + ((0 < m_numLabels) ? "gwt-SliderBar-tick1" : "gwt-SliderBar-tick2"));
				else DOM.setElementProperty(tick, "className", "gwt-SliderBar-tick gwt-SliderBar-tick-disabled " + ((0 < m_numLabels) ? "gwt-SliderBar-tick1" : "gwt-SliderBar-tick2"));

				// Position the tick and make it visible.
				DOM.setStyleAttribute(tick, "visibility", "hidden");
				DOM.setStyleAttribute(tick, "display",    ""      );
				int tickWidth = tick.getOffsetWidth();
				int tickLeftOffset = m_lineLeftOffset + (lineWidth * i / m_numTicks) - (tickWidth / 2);
				tickLeftOffset = Math.min(tickLeftOffset, m_lineLeftOffset + lineWidth - tickWidth);
				DOM.setStyleAttribute(tick, "left",       (tickLeftOffset + "px"));
				DOM.setStyleAttribute(tick, "visibility", "visible"              );
			}

			// Hide unused ticks.
			for (int i = (m_numTicks + 1); i < m_tickElements.size(); i += 1) {
				DOM.setStyleAttribute(m_tickElements.get(i), "display", "none");
			}
		}
		
		else {
			// Hide all ticks.
			for (Element elem : m_tickElements) {
				DOM.setStyleAttribute(elem, "display", "none");
			}
		}
	}

	/*
	 * Highlight this widget.
	 */
	private void highlight() {
		// Nothing to do.
	}

	/*
	 * Reset the progress to constrain the progress to the current
	 * range and redraw the knob as needed.
	 */
	private void resetCurrentValue() {
		setCurrentValue(getCurrentValue());
	}

	/*
	 * Sets the appropriate styles for the slider bar elements.
	 */
	private void setSliderStyles() {
		setStyleName(                     "gwt-SliderBar-shell"                           );
		addStyleName(((0 < m_numLabels) ? "gwt-SliderBar-shell1" : "gwt-SliderBar-shell2"));
		
		DOM.setElementProperty(m_lineElement, "className", "gwt-SliderBar-line "  + ((0 < m_numLabels) ? "gwt-SliderBar-line1"  : "gwt-SliderBar-line2") );
		DOM.setElementProperty(m_knobElement, "className", "gwt-SliderBar-knob "  + ((0 < m_numLabels) ? "gwt-SliderBar-knob1"  : "gwt-SliderBar-knob2") );
	}
	
	/*
	 * Slide the knob to a new location.
	 */
	private void slideKnob(Event event) {
		int x = DOM.eventGetClientX(event);
		if (x > 0) {
			int lineWidth = m_lineElement.getOffsetWidth();
			int lineLeft  = m_lineElement.getAbsoluteLeft();
			double percent = (double) (x - lineLeft) / lineWidth * 1.0;
			setCurrentValue(getTotalRange() * percent + m_minValue, true);
		}
	}

	/*
	 * Start sliding the knob.
	 */
	private void startSliding(boolean highlight, boolean fireEvent) {
		if (highlight) {
			DOM.setElementProperty(m_lineElement, "className", "gwt-SliderBar-line gwt-SliderBar-line-sliding " + ((0 < m_numLabels) ? "gwt-SliderBar-line1" : "gwt-SliderBar-line2"));
			DOM.setElementProperty(m_knobElement, "className", "gwt-SliderBar-knob gwt-SliderBar-knob-sliding " + ((0 < m_numLabels) ? "gwt-SliderBar-knob1" : "gwt-SliderBar-knob2"));
			m_knobImage.setResource(m_images.sliderSliding());
		}
	}

	/*
	 * Stop sliding the knob.
	 */
	private void stopSliding(boolean unhighlight, boolean fireEvent) {
		if (unhighlight) {
			DOM.setElementProperty(m_lineElement, "className", "gwt-SliderBar-line " + ((0 < m_numLabels) ? "gwt-SliderBar-line1" : "gwt-SliderBar-line2"));
			DOM.setElementProperty(m_knobElement, "className", "gwt-SliderBar-knob " + ((0 < m_numLabels) ? "gwt-SliderBar-knob1" : "gwt-SliderBar-knob2"));
			m_knobImage.setResource(m_images.slider());
		}
	}

	/*
	 * Removes highlight from this widget.
	 */
	private void unhighlight() {
		// Nothing to do.
	}

	@Override
	public void onResize() {
		redraw();
	}
}
