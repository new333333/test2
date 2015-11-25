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

/*
 * Copyright 2010 Google Inc.
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
package org.kablink.teaming.gwt.client.datatable;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A {@link Cell} used to render a checkbox. The value of the checkbox may be
 * toggled using the ENTER key as well as via mouse click.
 * 
 * 20120418 (DRF):
 *   I created this from the GWT CheckboxCell.java class.  It includes
 *   multiple changes to that to fix checkbox click issue with IE.
 *   
 * 20150930 (DRF):
 *   I added the VibeCheckboxData stuff to allow the creation of check
 *   boxes that are disabled.
 */
public class VibeCheckboxCell extends AbstractEditableCell<VibeCheckboxCell.VibeCheckboxData, VibeCheckboxCell.VibeCheckboxData> {
  // An HTML string representation of a checked input box.
  private static final SafeHtml INPUT_CHECKED          = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked/>"                      );
  private static final SafeHtml INPUT_CHECKED_DISABLED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked disabled=\"disabled\"/>");

  // An HTML string representation of an unchecked input box.
  private static final SafeHtml INPUT_UNCHECKED          = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\"/>"                      );
  private static final SafeHtml INPUT_UNCHECKED_DISABLED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" disabled=\"disabled\"/>");

  private final boolean dependsOnSelection;
  private final boolean handlesSelection;

  /**
   * Inner class used to represent how the render and/or interact with
   * the check box.
   */
  public static class VibeCheckboxData {
	  private Boolean	m_disabled;	//
	  private Boolean	m_value;	//

	  /**
	   * Constructor method.
	   * 
	   * @param value
	   * @param disabled
	   */
	  public VibeCheckboxData(Boolean value, Boolean disabled) {
		  // Initialize the super class...
		  super();
		  
		  // ...and store the parameters.
		  setValue(   value   );
		  setDisabled(disabled);
	  }

	  /**
	   * Get'er methods.
	   * 
	   * @return
	   */
	  public Boolean getDisabled() {return m_disabled;}
	  public Boolean getValue()    {return m_value;   }
	  
	  /**
	   * Set'er methods.
	   * 
	   * @param
	   */
	  public void setDisabled(Boolean disabled) {m_disabled = disabled;}
	  public void setValue(   Boolean value)    {m_value    = value;   }
  }
  /**
   * Construct a new {@link VibeCheckboxCell}.
   */
  public VibeCheckboxCell() {
    this(false);
  }

  /**
   * Construct a new {@link VibeCheckboxCell} that optionally controls selection.
   *
   * @param isSelectBox true if the cell controls the selection state
   * @deprecated use {@link #VibeCheckboxCell(boolean, boolean)} instead
   */
  @Deprecated
  public VibeCheckboxCell(boolean isSelectBox) {
    this(isSelectBox, isSelectBox);
  }

  /**
   * Construct a new {@link VibeCheckboxCell} that optionally controls selection.
   *
   * @param dependsOnSelection true if the cell depends on the selection state
   * @param handlesSelection true if the cell modifies the selection state
   */
  public VibeCheckboxCell(boolean dependsOnSelection, boolean handlesSelection) {
    super("click", "keydown");
    this.dependsOnSelection = dependsOnSelection;
    this.handlesSelection = handlesSelection;
  }

  @Override
  public boolean dependsOnSelection() {
    return dependsOnSelection;
  }

  @Override
  public boolean handlesSelection() {
    return handlesSelection;
  }

  @Override
  public boolean isEditing(Context context, Element parent, VibeCheckboxData data) {
    // A checkbox is never in "edit mode". There is no intermediate state
    // between checked and unchecked.
    return false;
  }

  @Override
  public void onBrowserEvent(Context context, Element parent, VibeCheckboxData data, 
      NativeEvent event, ValueUpdater<VibeCheckboxData> valueUpdater) {
    String type = event.getType();

    boolean cbClicked = "click".equals(type);
    boolean enterPressed = "keydown".equals(type)
        && event.getKeyCode() == KeyCodes.KEY_ENTER;
    if (cbClicked || enterPressed) {		//! 2012018 (DRF):  Fixed click issue with IE.
      InputElement input = parent.getFirstChild().cast();
      Boolean isChecked = input.isChecked();

      /*
       * Toggle the value if the enter key was pressed and the cell handles
       * selection or doesn't depend on selection. If the cell depends on
       * selection but doesn't handle selection, then ignore the enter key and
       * let the SelectionEventManager determine which keys will trigger a
       * change.
       */
      if ((cbClicked || enterPressed) && (handlesSelection() || !dependsOnSelection())) {
        isChecked = !isChecked;
        data.setValue(isChecked);
        input.setChecked(isChecked);
      }

      /*
       * Save the new value. However, if the cell depends on the selection, then
       * do not save the value because we can get into an inconsistent state.
       */
      Boolean value = data.getValue();
      if (value != isChecked && !dependsOnSelection()) {
        setViewData(context.getKey(), data);
      } else {
        clearViewData(context.getKey());
      }

      if (valueUpdater != null) {
        valueUpdater.update(data);
      }
    }
  }

  @Override
  public void render(Context context, VibeCheckboxData data, SafeHtmlBuilder sb) {
    // Get the view data.
    Object key = context.getKey();
    VibeCheckboxData viewData = getViewData(key);
    if (viewData != null && viewData.equals(data)) {
      clearViewData(key);
      viewData = null;
    }

    Boolean value    = data.getValue();
    boolean disabled = ((null == data) ? false : data.getDisabled());
    if (value != null && ((viewData != null) ? viewData.getValue() : value))
         sb.append(disabled ? INPUT_CHECKED_DISABLED   : INPUT_CHECKED  );
    else sb.append(disabled ? INPUT_UNCHECKED_DISABLED : INPUT_UNCHECKED);
  }
}
