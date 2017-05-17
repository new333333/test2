/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.dao.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * ?
 * 
 * @author ?
 */
public class Restrictions {
	abstract static class SingleFieldCriterion implements Criterion
	{
		String name;

		public SingleFieldCriterion(String name) {
			this.name = name;
		}
		
		@Override
		public List<Object> getParameterValues()
		{
			return new LinkedList<Object>();
		}
		protected String getFieldName(String alias)
		{
  			int pos = name.lastIndexOf('(');
  			if (pos == -1)
  				return alias + "." + name;
  			else {
  				++pos;
  	  			return name.substring(0, pos) + alias + "." + name.substring(pos, name.length());
  			}
		}
	}
	abstract static class SingleValueCriterion extends SingleFieldCriterion
	{
		Object value;
		public SingleValueCriterion(String name, Object value) {
			super(name);
			this.value = value;
		}
		
		@Override
		public List<Object> getParameterValues()
		{
			return Arrays.asList(value);
		}
		@Override
		public String toSQLString(String alias)
		{
			return getFieldName(alias) + getComparator() + "? ";
		}
		abstract protected String getComparator();
	}
	static class EqCriterion extends SingleValueCriterion
	{
		public EqCriterion(String name, Object value)
		{
			super(name, value);
		}
		@Override
		protected String getComparator() { return "="; }
	}
	
	static class NotEqCriterion extends SingleValueCriterion
	{
		public NotEqCriterion(String name, Object value)
		{
			super(name, value);
		}
		@Override
		protected String getComparator() { return "<>"; }
	}
	
	static class EqOrNullCriterion extends EqCriterion
	{
		public EqOrNullCriterion(String name, Object value)
		{
			super(name, value);
		}
		@Override
		public String toSQLString(String alias)
		{
			return "(" + getFieldName(alias) + " is null or " + super.toSQLString(alias) + ")";
		}
	}
	
	static class NotNullCriterion extends SingleFieldCriterion
	{
		public NotNullCriterion(String name)
		{
			super(name);
		}
		@Override
		public String toSQLString(String alias)
		{
			return getFieldName(alias) + " is not null";
		}
	}
	
	static class IsNullCriterion extends SingleFieldCriterion
	{
		public IsNullCriterion(String name)
		{
			super(name);
		}
		@Override
		public String toSQLString(String alias)
		{
			return getFieldName(alias) + " is null";
		}
	}
	
	public static Criterion eq(String name, Object value)
	{
		return new EqCriterion(name, value);
	}

	public static Criterion notEq(String name, Object value)
	{
		return new NotEqCriterion(name, value);
	}

	public static Criterion eqOrNull(String name, Object value)
	{
		return new EqOrNullCriterion(name, value);
	}

	public static Criterion notNull(String name)
	{
		return new NotNullCriterion(name);
	}

	public static Criterion isNull(String name)
	{
		return new IsNullCriterion(name);
	}	
}
