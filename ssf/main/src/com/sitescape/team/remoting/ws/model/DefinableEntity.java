/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.remoting.ws.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefinableEntity {
	
	private long id;
	private long binderId;
	private String definitionId;
	private String title;
	private Description description;
	private AverageRating averageRating;
	private Timestamp creation;
	private Timestamp modification;
	private AttachmentsField attachmentsField;
	// Using Lists as internal representation to make it a bit easier to build them.
	// But the public getter/setter must use array representation.
	private List<BooleanField> booleanFieldList = new ArrayList<BooleanField>();
	private List<DateField> dateFieldList = new ArrayList<DateField>();
	private List<LongArrayField> longArrayFieldList = new ArrayList<LongArrayField>();
	private List<StringArrayField> stringArrayFieldList = new ArrayList<StringArrayField>();
	private List<StringField> stringFieldList = new ArrayList<StringField>();

	public long getBinderId() {
		return binderId;
	}

	public void setBinderId(long binderId) {
		this.binderId = binderId;
	}

	public String getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public void setAttachmentsField(AttachmentsField attachmentsField) {
		this.attachmentsField = attachmentsField;
	}

	public BooleanField[] getBooleanFields() {
		BooleanField[] array = new BooleanField[booleanFieldList.size()];
		booleanFieldList.toArray(array);
		return array;
	}

	public void setBooleanFields(BooleanField[] booleanFields) {
		if(booleanFields != null)
			this.booleanFieldList = Arrays.asList(booleanFields); 
		else
			this.booleanFieldList = new ArrayList<BooleanField>();
	}

	// Convenience method. Not part of the API. 
	public void addBooleanField(BooleanField booleanField) {
		this.booleanFieldList.add(booleanField);
	}
	
	public DateField[] getDateFields() {
		DateField[] array = new DateField[dateFieldList.size()];
		dateFieldList.toArray(array);
		return array;
	}

	public void setDateFields(DateField[] dateFields) {
		if(dateFields != null)
			this.dateFieldList = Arrays.asList(dateFields); 
		else
			this.dateFieldList = new ArrayList<DateField>();
	}
	
	// Convenience method. Not part of the API. 
	public void addDateField(DateField dateField) {
		this.dateFieldList.add(dateField);
	}

	public LongArrayField[] getLongArrayFields() {
		LongArrayField[] array = new LongArrayField[longArrayFieldList.size()];
		longArrayFieldList.toArray(array);
		return array;
	}

	public void setLongArrayFields(LongArrayField[] longArrayFields) {
		if(longArrayFields != null)
			this.longArrayFieldList = Arrays.asList(longArrayFields); 
		else
			this.longArrayFieldList = new ArrayList<LongArrayField>();
	}
	
	// Convenience method. Not part of the API. 
	public void addLongArrayField(LongArrayField longArrayField) {
		this.longArrayFieldList.add(longArrayField);
	}

	public StringArrayField[] getStringArrayFields() {
		StringArrayField[] array = new StringArrayField[stringArrayFieldList.size()];
		stringArrayFieldList.toArray(array);
		return array;
	}

	public void setStringArrayFields(StringArrayField[] stringArrayFields) {
		if(stringArrayFields != null)
			this.stringArrayFieldList = Arrays.asList(stringArrayFields); 
		else
			this.stringArrayFieldList = new ArrayList<StringArrayField>();
	}
	
	// Convenience method. Not part of the API. 
	public void addStringArrayField(StringArrayField stringArrayField) {
		this.stringArrayFieldList.add(stringArrayField);
	}

	public StringField[] getStringFields() {
		StringField[] array = new StringField[stringFieldList.size()];
		stringFieldList.toArray(array);
		return array;
	}

	public void setStringFields(StringField[] stringFields) {
		if(stringFields != null)
			this.stringFieldList = Arrays.asList(stringFields); 
		else
			this.stringFieldList = new ArrayList<StringField>();
	}
	
	// Convenience method. Not part of the API. 
	public void addStringField(StringField stringField) {
		this.stringFieldList.add(stringField);
	}
}
