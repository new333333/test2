package com.sitescape.team.remoting.ws.util.attachments;

import javax.activation.DataHandler;

import net.fortuna.ical4j.model.Calendar;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.remoting.ws.util.CalendarDataSource;
import com.sitescape.team.util.AllModulesInjected;
public class CalendarHelper {
	public static void handleEvents(AllModulesInjected bs, DefinableEntity entity) {
		if(!entity.getEvents().isEmpty()) {
			Calendar eventCalendar = bs.getIcalModule().generate(entity, entity.getEvents(), null);
			DataHandler dh = new DataHandler(new CalendarDataSource(eventCalendar));
			MessageContext messageContext = MessageContext.getCurrentContext();
			Message responseMessage = messageContext.getResponseMessage();
			responseMessage.addAttachmentPart(new AttachmentPart(dh));
		}
	}
}
