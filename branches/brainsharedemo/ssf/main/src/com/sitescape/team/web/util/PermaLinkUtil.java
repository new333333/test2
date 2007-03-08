package com.sitescape.team.web.util;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;

public class PermaLinkUtil {

	public static String getURL(Binder binder) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getEntityIdentifier().getEntityId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, binder.getEntityType().toString());

		return adapterUrl.toString();
	}
}
