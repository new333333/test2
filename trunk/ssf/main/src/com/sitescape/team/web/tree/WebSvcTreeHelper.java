package com.sitescape.team.web.tree;

import org.dom4j.Element;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.module.rss.util.UrlUtil;
import com.sitescape.team.util.AllModulesInjected;

public class WebSvcTreeHelper extends WsActionTreeHelper {
	public String getTreeNameKey() {return "webservice";};
	
	public void customize(AllModulesInjected bs, Object source, int type, Element element) {
		super.customize(bs, source, type, element);
		if (type == DomTreeBuilder.TYPE_SKIPLIST) return;
		Binder binder = (Binder) source;
		if(binder.getPosting() != null) {
			PostingDef posting = binder.getPosting();
			if(posting.isEnabled()) {
				element.addAttribute("postingAddress", posting.getEmailAddress());
			}
		}
		if ((type == DomTreeBuilder.TYPE_FOLDER)) {
			element.addAttribute("rss", UrlUtil.getFeedURL(null, binder.getId().toString()));
			element.addAttribute("ical", com.sitescape.team.ical.util.UrlUtil.getICalURL(null, binder.getId().toString(), null));

		}
	}

}
