package com.sitescape.team.module.definition.notify;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.Node;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderEvent extends AbstractNotifyBuilder {
	
	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
		   DefinableEntity entity = attribute.getOwner().getEntity();
		   	Object obj = attribute.getValue();
	    	if (obj == null) return true;
	    	if (obj instanceof Event) {
	    		Event event = (Event)obj;
	    		Element value = element.addElement("startDate");
	    		value.setText(notifyDef.getDateFormat().format(event.getDtStart().getTime()));
	    		value = element.addElement("endDate");
	    		value.setText(notifyDef.getDateFormat().format(event.getDtEnd().getTime()));
	    		notifyDef.addEvent(entity.getId(), event);
	    	} else { 
	    		element.setText(obj.toString());
	    	}
	    	return true;
	    }
}
