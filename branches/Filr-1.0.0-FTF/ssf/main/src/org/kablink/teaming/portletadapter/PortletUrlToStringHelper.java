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
package org.kablink.teaming.portletadapter;

import java.util.Iterator;
import java.util.Map;

import org.kablink.teaming.portletadapter.support.KeyNames;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Http;
import org.kablink.util.Validator;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class PortletUrlToStringHelper implements PortletUrlToStringHelperInterface {
	@Override
	public String toString(AdaptedPortletURL adaptedPortletUrl) {
		StringBuffer sb = new StringBuffer();
		
		if (adaptedPortletUrl.adapterUrlString == null) {		
			String adapterRootURL;
			if(adaptedPortletUrl.secure != null && adaptedPortletUrl.hostname != null && adaptedPortletUrl.port != null)
				adapterRootURL = WebUrlUtil.getAdapterRootURL(adaptedPortletUrl.secure, adaptedPortletUrl.hostname, adaptedPortletUrl.port);
			else if(adaptedPortletUrl.sreq != null)
				adapterRootURL = WebUrlUtil.getAdapterRootURL(adaptedPortletUrl.sreq, adaptedPortletUrl.secure);
			else
				adapterRootURL = WebUrlUtil.getAdapterRootURL(adaptedPortletUrl.preq, adaptedPortletUrl.secure);
		
			sb.append(adapterRootURL);
					
			if(adaptedPortletUrl.crawler) {
				sb.append("c/");
				
				sb.append(KeyNames.PORTLET_URL_PORTLET_NAME);
				sb.append(Constants.SLASH);
				sb.append(Http.encodeURL(adaptedPortletUrl.portletName));
				sb.append(Constants.SLASH);
			
				sb.append(KeyNames.PORTLET_URL_ACTION);
				sb.append(Constants.SLASH);
				sb.append(adaptedPortletUrl.action? Http.encodeURL(adaptedPortletUrl.ACTION_TRUE) : Http.encodeURL(adaptedPortletUrl.ACTION_FALSE));				
			}
			else {
				sb.append("do?");
			
				sb.append(KeyNames.PORTLET_URL_PORTLET_NAME);
				sb.append(Constants.EQUAL);
				sb.append(Http.encodeURL(adaptedPortletUrl.portletName));
				sb.append(Constants.AMPERSAND);
			
				sb.append(KeyNames.PORTLET_URL_ACTION);
				sb.append(Constants.EQUAL);
				sb.append(adaptedPortletUrl.action? Http.encodeURL(adaptedPortletUrl.ACTION_TRUE) : Http.encodeURL(adaptedPortletUrl.ACTION_FALSE));
			}
		} else {
			sb.append(adaptedPortletUrl.adapterUrlString);
		}
		Iterator itr = adaptedPortletUrl.params.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();

			String name = (String)entry.getKey();
			String[] values = (String[])entry.getValue();

			for (int i = 0; i < values.length; i++) {
				if(adaptedPortletUrl.crawler) {
					if(Validator.isNull(values[i]))
							continue;
					sb.append(Constants.SLASH);
				}
				else {
					sb.append(Constants.AMPERSAND);
				}
				sb.append(name);
				if(adaptedPortletUrl.crawler)
					sb.append(Constants.SLASH);
				else
					sb.append(Constants.EQUAL);
				String value = values[i];
				if ((!(name.equals(WebKeys.URL_FILE_NAME))) || (0 <= value.indexOf(' '))) {
					// Note:  This check is to stop
					// PermaLinkUtil.getFilePermalink() from getting a
					// double encoded filename.
					value = Http.encodeURL(value);
				}
				sb.append(value);

			}
		}
		
		// Add the GWT URL parameter to every URL.  This is needed starting in
		// Durango so that we don't display the pre-Durango ui.  LoginFilter.java will look for
		// this parameter.
		{
			if( adaptedPortletUrl.crawler)
			{
				sb.append( Constants.SLASH );
			}
			else
			{
				sb.append( Constants.AMPERSAND );
			}
			
			sb.append( WebKeys.URL_NOVL_URL_FLAG );
			
			if( adaptedPortletUrl.crawler )
				sb.append( Constants.SLASH );
			else
				sb.append( Constants.EQUAL );
			
			sb.append("1" );
		}		
		
		return sb.toString();
	}
}
