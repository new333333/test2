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
package org.kablink.teaming.remoting.ws.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class DefinableEntity implements Serializable {
	
	private Long id;
	private Long parentBinderId;
	private String definitionId;
	private String title;
	private Description description;
	private AverageRating averageRating;
	private Timestamp creation;
	private Timestamp modification;
	private boolean eventAsIcalString;
	private AttachmentsField attachmentsField;
	// Using Map as internal representation for convenience and efficiency.
	// But the public getter/setter must use array representation.
	private Map<String,CustomBooleanField> booleanFieldMap = new HashMap<String,CustomBooleanField>();
	private Map<String,CustomDateField> dateFieldMap = new HashMap<String,CustomDateField>();
	private Map<String,CustomLongArrayField> longArrayFieldMap = new HashMap<String,CustomLongArrayField>();
	private Map<String,CustomStringArrayField> stringArrayFieldMap = new HashMap<String,CustomStringArrayField>();
	private Map<String,CustomStringField> stringFieldMap = new HashMap<String,CustomStringField>();
	private Map<String,CustomEventField> eventFieldMap = new HashMap<String,CustomEventField>();
    private String permaLink;
	private String entityType;
	private String family;

	public Long getParentBinderId() {
		return parentBinderId;
	}

	public void setParentBinderId(Long binderId) {
		this.parentBinderId = binderId;
	}

	public String getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public AverageRating getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(AverageRating averageRating) {
		this.averageRating = averageRating;
	}

	public Timestamp getCreation() {
		return creation;
	}

	public void setCreation(Timestamp creation) {
		this.creation = creation;
	}

	public Timestamp getModification() {
		return modification;
	}

	public void setModification(Timestamp modification) {
		this.modification = modification;
	}

	public AttachmentsField getAttachmentsField() {
		return attachmentsField;
	}

	public boolean isEventAsIcalString() {
		return eventAsIcalString;
	}

	public void setEventAsIcalString(boolean eventAsIcalString) {
		this.eventAsIcalString = eventAsIcalString;
	}

	public void setAttachmentsField(AttachmentsField attachmentsField) {
		this.attachmentsField = attachmentsField;
	}

	public CustomBooleanField[] getCustomBooleanFields() {
		CustomBooleanField[] array = new CustomBooleanField[booleanFieldMap.size()];
		return booleanFieldMap.values().toArray(array);
	}

	public void setCustomBooleanFields(CustomBooleanField[] booleanFields) {
		this.booleanFieldMap = new HashMap<String,CustomBooleanField>();
		if(booleanFields != null) {
			for(int i = 0; i < booleanFields.length; i++)
				this.booleanFieldMap.put(booleanFields[i].getName(), booleanFields[i]);
		}
	}

	// Convenience method. Not part of the API exposed through the web services.
	public void addCustomBooleanField(CustomBooleanField booleanField) {
		this.booleanFieldMap.put(booleanField.getName(), booleanField);
	}
	
	public CustomBooleanField findCustomBooleanField(String name) {
		return this.booleanFieldMap.get(name);
	}
	
	public CustomDateField[] getCustomDateFields() {
		CustomDateField[] array = new CustomDateField[dateFieldMap.size()];
		return dateFieldMap.values().toArray(array);
	}

	public void setCustomDateFields(CustomDateField[] dateFields) {
		this.dateFieldMap = new HashMap<String,CustomDateField>();
		if(dateFields != null) {
			for(int i = 0; i < dateFields.length; i++)
				this.dateFieldMap.put(dateFields[i].getName(), dateFields[i]);
		}
	}
	
	// Convenience method. Not part of the API. 
	public void addCustomDateField(CustomDateField dateField) {
		this.dateFieldMap.put(dateField.getName(), dateField);
	}

	public CustomDateField findCustomDateField(String name) {
		return this.dateFieldMap.get(name);
	}
	
	public CustomLongArrayField[] getCustomLongArrayFields() {
		CustomLongArrayField[] array = new CustomLongArrayField[longArrayFieldMap.size()];
		return longArrayFieldMap.values().toArray(array);
	}

	public void setCustomLongArrayFields(CustomLongArrayField[] longArrayFields) {
		this.longArrayFieldMap = new HashMap<String,CustomLongArrayField>();
		if(longArrayFields != null) {
			for(int i = 0; i < longArrayFields.length; i++)
				this.longArrayFieldMap.put(longArrayFields[i].getName(), longArrayFields[i]);
		}
	}
	
	// Convenience method. Not part of the API. 
	public void addCustomLongArrayField(CustomLongArrayField longArrayField) {
		this.longArrayFieldMap.put(longArrayField.getName(), longArrayField);
	}

	public CustomLongArrayField findCustomLongArrayField(String name) {
		return this.longArrayFieldMap.get(name);
	}
	
	public CustomStringArrayField[] getCustomStringArrayFields() {
		CustomStringArrayField[] array = new CustomStringArrayField[stringArrayFieldMap.size()];
		return stringArrayFieldMap.values().toArray(array);
	}

	public void setCustomStringArrayFields(CustomStringArrayField[] stringArrayFields) {
		this.stringArrayFieldMap = new HashMap<String,CustomStringArrayField>();
		if(stringArrayFields != null) {
			for(int i = 0; i < stringArrayFields.length; i++)
				this.stringArrayFieldMap.put(stringArrayFields[i].getName(), stringArrayFields[i]);
		}
	}
	
	// Convenience method. Not part of the API. 
	public void addCustomStringArrayField(CustomStringArrayField stringArrayField) {
		this.stringArrayFieldMap.put(stringArrayField.getName(), stringArrayField);
	}

	public CustomStringArrayField findCustomStringArrayField(String name) {
		return this.stringArrayFieldMap.get(name);
	}
	
	public Set<String> customStringArrayFieldNames() {
		return this.stringArrayFieldMap.keySet();
	}
	
	public CustomStringField[] getCustomStringFields() {
		CustomStringField[] array = new CustomStringField[stringFieldMap.size()];
		return stringFieldMap.values().toArray(array);
	}

	public void setCustomStringFields(CustomStringField[] stringFields) {
		this.stringFieldMap = new HashMap<String,CustomStringField>();
		if(stringFields != null) {
			for(int i = 0; i < stringFields.length; i++)
				this.stringFieldMap.put(stringFields[i].getName(), stringFields[i]);
		}
	}
	
	// Convenience method. Not part of the API. 
	public void addCustomStringField(CustomStringField stringField) {
		this.stringFieldMap.put(stringField.getName(), stringField);
	}

	public CustomStringField findCustomStringField(String name) {
		return this.stringFieldMap.get(name);
	}
	
	public Set<String> customStringFieldNames() {
		return this.stringFieldMap.keySet();
	}
	
	public CustomEventField[] getCustomEventFields() {
		CustomEventField[] array = new CustomEventField[eventFieldMap.size()];
		return eventFieldMap.values().toArray(array);
	}

	public void setCustomEventFields(CustomEventField[] eventFields) {
		this.eventFieldMap = new HashMap<String,CustomEventField>();
		if(eventFields != null) {
			for(int i = 0; i < eventFields.length; i++)
				this.eventFieldMap.put(eventFields[i].getName(), eventFields[i]);
		}
	}
	
	// Convenience method. Not part of the API. 
	public void addCustomEventField(CustomEventField eventField) {
		this.eventFieldMap.put(eventField.getName(), eventField);
	}

	public CustomEventField findCustomEventField(String name) {
		return this.eventFieldMap.get(name);
	}
	
	public String getPermaLink() {
		return permaLink;
	}
	public void setPermaLink(String permaLink) {
		this.permaLink = permaLink;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

}
