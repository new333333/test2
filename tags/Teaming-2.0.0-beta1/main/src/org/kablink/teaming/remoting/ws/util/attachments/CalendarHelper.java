package org.kablink.teaming.remoting.ws.util.attachments;

import javax.activation.DataHandler;

import net.fortuna.ical4j.model.Calendar;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.remoting.ws.util.CalendarDataSource;
import org.kablink.teaming.util.AllModulesInjected;

public class CalendarHelper {
	public static void handleEvents(AllModulesInjected bs, DefinableEntity entity) {
		if(!entity.getEvents().isEmpty()) {
			Calendar eventCalendar = bs.getIcalModule().generate(entity, entity.getEvents(), null);
			DataHandler dh = new DataHandler(new CalendarDataSource(eventCalendar));
			MessageContext messageContext = MessageContext.getCurrentContext();
			Message responseMessage = messageContext.getResponseMessage();
			AttachmentPart part = new AttachmentPart(dh);
			part.setMimeHeader("Content-Disposition",
					"attachment;filename=\"entry-" + entity.getId() + ".ics\"");
			responseMessage.addAttachmentPart(part);
		}
	}
}
