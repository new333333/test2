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
package org.kablink.util.search;

import java.util.Arrays;
import java.util.Collection;

import org.dom4j.Branch;
import org.dom4j.Element;


public class Restrictions
{
	public static Criterion eq(String field, String value)
	{
		return new LikeCriterion(field, value, true);
	}
	
	public static Criterion like(String field, String value)
	{
		return new LikeCriterion(field, value, false);
	}
	
	public static Criterion in(String field, Collection<String> values)
	{
		return new InCriterion(field, values, true);
	}
	
	public static Criterion in(String field, String[] values)
	{
		return new InCriterion(field, Arrays.asList(values), true);
	}
	
	public static Criterion between(String field, String lo, String hi)
	{
		return new BetweenCriterion(field, lo, hi);
	}
	
	public static Junction.Conjunction conjunction()
	{
		return new Junction.Conjunction();
	}

	public static Junction.Disjunction disjunction()
	{
		return new Junction.Disjunction();
	}

	public static Junction.Not not()
	{
		return new Junction.Not();
	}
	
	abstract static class FieldCriterion implements Criterion
	{
		protected String fieldName;
		
		public FieldCriterion(String fieldName)
		{
			this.fieldName = fieldName;
		}
		
		protected Element toQuery(Branch parent, String value)
		{
			Element root = parent.addElement(Constants.FIELD_ELEMENT);
			root.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, fieldName);
        	Element child = root.addElement(Constants.FIELD_TERMS_ELEMENT);
    		child.setText(value);
    		
			return root;
		}
	}
	
	static class LikeCriterion extends FieldCriterion
	{
		private String value;
		private boolean exact;

		public LikeCriterion(String fieldName, String value, boolean exact)
		{
			super(fieldName);
			this.value = value;
			this.exact = exact;
		}
		
		public Element toQuery(Branch parent)
		{
			Element element = super.toQuery(parent, value);
			if(exact) {
				element.addAttribute(Constants.EXACT_PHRASE_ATTRIBUTE, Constants.EXACT_PHRASE_TRUE);
			}
			else {
				element.addAttribute(Constants.EXACT_PHRASE_ATTRIBUTE, Constants.EXACT_PHRASE_FALSE);				
			}
			return element;
		}
	}
	
	static class InCriterion extends FieldCriterion
	{
		private Collection<String> values;
		private boolean exact;
		
		public InCriterion(String fieldName, Collection<String> values, boolean exact)
		{
			super(fieldName);
			this.values = values;
			this.exact = exact;
		}
		
		public Element toQuery(Branch parent)
		{
			Element root = parent.addElement(Constants.OR_ELEMENT);
			for(String value : values)
			{
				Element element = super.toQuery(root, value);
				if(exact) {
					element.addAttribute(Constants.EXACT_PHRASE_ATTRIBUTE, Constants.EXACT_PHRASE_TRUE);
				}
				else {
					element.addAttribute(Constants.EXACT_PHRASE_ATTRIBUTE, Constants.EXACT_PHRASE_FALSE);					
				}
			}
			
			return root;
		}
	}
	
	static class BetweenCriterion implements Criterion
	{
		protected String fieldName;
		protected String lo;
		protected String hi;

		public BetweenCriterion(String fieldName, String lo, String hi)
		{
			this.fieldName = fieldName;
			this.lo = lo;
			this.hi = hi;
		}

		public Element toQuery(Branch parent)
		{
			Element root = parent.addElement(Constants.RANGE_ELEMENT);
			root.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, fieldName);
			root.addAttribute(Constants.INCLUSIVE_ATTRIBUTE, Constants.INCLUSIVE_TRUE);
			Element child = root.addElement(Constants.RANGE_START);
			child.setText(lo);
			child = root.addElement(Constants.RANGE_FINISH);
			child.setText(hi);

			return root;
		}
	}
}
