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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * The {@link Spinner} provide two arrows for in- and decreasing values. A
 * linked {@link SpinnerListener}
 * 
 * 20110712 (DRF):
 *    I copied this class from the GWT incubator into the Vibe OnPrem
 *    ssf (open) source tree.
 */
public class Spinner {
	/**
	 * Default resources for spinning arrows.
	 */
	public interface SpinnerResources extends ClientBundle {
		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/arrowDown.png")
		ImageResource arrowDown();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/arrowDownDisabled.png")
		ImageResource arrowDownDisabled();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/arrowDownHover.png")
		ImageResource arrowDownHover();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/arrowDownPressed.png")
		ImageResource arrowDownPressed();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/arrowUp.png")
		ImageResource arrowUp();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/arrowUpDisabled.png")
		ImageResource arrowUpDisabled();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/arrowUpHover.png")
		ImageResource arrowUpHover();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("org/kablink/teaming/gwt/public/images/Widgets/arrowUpPressed.png")
		ImageResource arrowUpPressed();
	}

	private static final int INITIAL_SPEED = 7;

	private SpinnerResources images;
	private final Image decrementArrow = new Image();
	private final Image incrementArrow = new Image();

	private List<SpinnerListener> spinnerListeners = new ArrayList<SpinnerListener>();
	private int step, minStep, maxStep, initialSpeed = 7;
	private double value, min, max;
	private boolean increment;
	private boolean constrained;
	private boolean enabled = true;

	private final Timer timer = new Timer() {
		private int counter = 0;
		private int speed = 7;

		@Override
		public void cancel() {
			super.cancel();
			speed = initialSpeed;
			counter = 0;
		}

		@Override
		public void run() {
			counter++;
			if (speed <= 0 || counter % speed == 0) {
				speed--;
				counter = 0;
				if (increment) {
					increase();
				} else {
					decrease();
				}
			}
			if (speed < 0 && step < maxStep) {
				step += 1;
			}
		}
	};

	private MouseDownHandler mouseDownHandler = new MouseDownHandler() {
		@Override
		public void onMouseDown(MouseDownEvent event) {
			if (enabled) {
				Image sender = (Image) event.getSource();
				if (sender == incrementArrow) {
					sender.setResource(images.arrowUpPressed());
					increment = true;
					increase();
				} else {
					sender.setResource(images.arrowDownPressed());
					increment = false;
					decrease();
				}
				timer.scheduleRepeating(30);
			}
		}
	};

	private MouseOverHandler mouseOverHandler = new MouseOverHandler() {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			if (enabled) {
				Image sender = (Image) event.getSource();
				if (sender == incrementArrow) {
					sender.setResource(images.arrowUpHover());
				} else {
					sender.setResource(images.arrowDownHover());
				}
			}
		}
	};

	private MouseOutHandler mouseOutHandler = new MouseOutHandler() {
		@Override
		public void onMouseOut(MouseOutEvent event) {
			if (enabled) {
				cancelTimer((Widget) event.getSource());
			}
		}
	};

	private MouseUpHandler mouseUpHandler = new MouseUpHandler() {
		@Override
		public void onMouseUp(MouseUpEvent event) {
			if (enabled) {
				cancelTimer((Widget) event.getSource());
			}
		}
	};

	/**
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 */
	public Spinner(SpinnerListener spinner, double value) {
		this(spinner, value, 0, 0, 1, 99, false);
	}

	/**
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 */
	public Spinner(SpinnerListener spinner, double value, double min, double max) {
		this(spinner, value, min, max, 1, 99, true);
	}

	/**
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * @param minStep
	 *            min value for stepping
	 * @param maxStep
	 *            max value for stepping
	 */
	public Spinner(SpinnerListener spinner, double value, double min, double max,
			int minStep, int maxStep) {
		this(spinner, value, min, max, minStep, maxStep, true);
	}

	/**
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * @param minStep
	 *            min value for stepping
	 * @param maxStep
	 *            max value for stepping
	 * @param constrained
	 *            determines if min and max value will take effect
	 */
	public Spinner(SpinnerListener spinner, double value, double min, double max,
			int minStep, int maxStep, boolean constrained) {
		this(spinner, value, min, max, minStep, maxStep, constrained,
				(SpinnerResources) GWT.create(SpinnerResources.class));
	}

	/**
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * @param minStep
	 *            min value for stepping
	 * @param maxStep
	 *            max value for stepping
	 * @param constrained
	 *            determines if min and max value will take effect
	 * @param images
	 *            the arrows images
	 */
	public Spinner(SpinnerListener spinner, double value, double min, double max,
			int minStep, int maxStep, boolean constrained,
			SpinnerResources images) {
		super();
		spinnerListeners.add(spinner);
		this.images = images;
		this.value = value;
		this.constrained = constrained;
		this.step = minStep;
		this.minStep = minStep;
		this.maxStep = maxStep;
		this.min = min;
		this.max = max;
		this.initialSpeed = INITIAL_SPEED;
		incrementArrow.addMouseUpHandler(mouseUpHandler);
		incrementArrow.addMouseDownHandler(mouseDownHandler);
		incrementArrow.addMouseOverHandler(mouseOverHandler);
		incrementArrow.addMouseOutHandler(mouseOutHandler);
		incrementArrow.setResource(images.arrowUp());
		decrementArrow.addMouseUpHandler(mouseUpHandler);
		decrementArrow.addMouseDownHandler(mouseDownHandler);
		decrementArrow.addMouseOverHandler(mouseOverHandler);
		decrementArrow.addMouseOutHandler(mouseOutHandler);
		decrementArrow.setResource(images.arrowDown());
		fireOnValueChanged();
	}

	/**
	 * @param listener
	 *            the listener to add
	 */
	public void addSpinnerListener(SpinnerListener listener) {
		spinnerListeners.add(listener);
	}

	/**
	 * @return the image representing the decreating arrow
	 */
	public Image getDecrementArrow() {
		return decrementArrow;
	}

	/**
	 * @return the image representing the increasing arrow
	 */
	public Image getIncrementArrow() {
		return incrementArrow;
	}

	/**
	 * @return the maximum value
	 */
	public double getMax() {
		return max;
	}

	/**
	 * @return the maximum spinner step
	 */
	public int getMaxStep() {
		return maxStep;
	}

	/**
	 * @return the minimum value
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @return the minimum spinner step
	 */
	public int getMinStep() {
		return minStep;
	}

	/**
	 * @return the current value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @return true is min and max values are active, false if not
	 */
	public boolean isConstrained() {
		return constrained;
	}

	/**
	 * @return Gets whether this widget is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param listener
	 *            the listener to remove
	 */
	public void removeSpinnerListener(SpinnerListener listener) {
		spinnerListeners.remove(listener);
	}

	/**
	 * Sets whether this widget is enabled.
	 * 
	 * @param enabled
	 *            true to enable the widget, false to disable it
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			incrementArrow.setResource(images.arrowUp());
			decrementArrow.setResource(images.arrowDown());
		} else {
			incrementArrow.setResource(images.arrowUpDisabled());
			decrementArrow.setResource(images.arrowDownDisabled());
		}
		if (!enabled) {
			timer.cancel();
		}
	}

	/**
	 * @param initialSpeed
	 *            the initial speed of the spinner. Higher values mean lower
	 *            speed, default value is 7
	 */
	public void setInitialSpeed(int initialSpeed) {
		this.initialSpeed = initialSpeed;
	}

	/**
	 * @param max
	 *            the maximum value. Will not have any effect if constrained is
	 *            set to false
	 */
	public void setMax(double max) {
		this.max = max;
	}
	
	/**
	 * @param maxStep
	 *            the maximum step for this spinner
	 */
	public void setMaxStep(int maxStep) {
		this.maxStep = maxStep;
	}

	/**
	 * @param min
	 *            the minimum value. Will not have any effect if constrained is
	 *            set to false
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * @param minStep
	 *            the minimum step for this spinner
	 */
	public void setMinStep(int minStep) {
		this.minStep = minStep;
	}

	/**
	 * @param value
	 *            sets the current value of this spinner
	 * @param fireEvent
	 *            fires value changed event if set to true
	 */
	public void setValue(double value, boolean fireEvent) {
		this.value = value;
		if (fireEvent) {
			fireOnValueChanged();
		}
	}
	
	/**
	 * Decreases the current value of the spinner by subtracting current step
	 */
	protected void decrease() {
		value -= step;
		if (constrained && value < min) {
			value = min;
			timer.cancel();
		}
		fireOnValueChanged();
	}

	/**
	 * Increases the current value of the spinner by adding current step
	 */
	protected void increase() {
		value += step;
		if (constrained && value > max) {
			value = max;
			timer.cancel();
		}
		fireOnValueChanged();
	}

	private void cancelTimer(Widget sender) {
		step = minStep;
		if (sender == incrementArrow) {
			((Image) sender).setResource(images.arrowUp());
		} else {
			((Image) sender).setResource(images.arrowDown());
		}
		timer.cancel();
	}

	private void fireOnValueChanged() {
		for (SpinnerListener listener : spinnerListeners) {
			listener.onSpinning(value);
		}
	}
}
