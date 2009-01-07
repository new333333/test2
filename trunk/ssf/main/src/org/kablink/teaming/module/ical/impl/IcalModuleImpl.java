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
package org.kablink.teaming.module.ical.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.joda.time.DateTimeZone;
import org.joda.time.YearMonthDay;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.AttendedEntries;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.util.Validator;
import org.kablink.util.cal.DayAndPosition;

/**
 * iCalendar generator.
 * 
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class IcalModuleImpl extends CommonDependencyInjection implements IcalModule {
	protected Log logger = LogFactory.getLog(getClass());
	
	private static final ProdId PROD_ID = new ProdId("-//SiteScape Inc//"
			+ ObjectKeys.PRODUCT_NAME_DEFAULT);

	private BinderModule binderModule;
	private FolderModule folderModule;

	
	/*
	 * When Loaded, linuxTZIDMappings will hold a HashMap of the
	 * items from LINUX_TZID_MAPPINGS that aren't mapped to themselves.
	 * (For the sake of completeness, I included EVERYTHING in the
	 * definition of LINUX_TZID_MAPPINGS.)
	 * 
	 * linuxTZIDMappings will be loaded on the first call to the
	 * method loadLinuxTZIDMappings() which occurs on the first call to
	 * the method mapLinuxTZIDToJavaTZID().
	 */
	private static HashMap<String, String> linuxTZIDMappings;

	/*
	 * This table contains mappings from Linux time zone IDs to Java
	 * 1.6 time zone IDs.  It was defined using information from:
	 * 
	 *    http://tinyurl.com/76ylrz
	 *         -or-
	 *    http://74.125.95.132/search?q=cache:-KG8flcy8-4J:www.njdj.gov.cn/qhdj/Adx/images/xxmyimg_1039.jsp%3Fsort%3D3%26downfile%3D%252Fopt%252Foracle%252Fjre%252F1.3.1%252Flib%252Ftzmappings+tzmappings&hl=en&ct=clnk&cd=28&gl=us
	 */
	private final static String[][] LINUX_TZID_MAPPINGS = new String[][]
     {
     	new String[]{"Africa/Abidjan", "Africa/Abidjan"},
     	new String[]{"Africa/Accra", "Africa/Accra"},
     	new String[]{"Africa/Addis_Ababa", "Africa/Addis_Ababa"},
     	new String[]{"Africa/Algiers", "Africa/Algiers"},
     	new String[]{"Africa/Asmera", "Africa/Asmera"},
     	new String[]{"Africa/Bamako", "GMT"},
     	new String[]{"Africa/Bangui", "Africa/Bangui"},
     	new String[]{"Africa/Banjul", "Africa/Banjul"},
     	new String[]{"Africa/Bissau", "Africa/Bissau"},
     	new String[]{"Africa/Blantyre", "Africa/Blantyre"},
     	new String[]{"Africa/Brazzaville", "Africa/Luanda"},
     	new String[]{"Africa/Bujumbura", "Africa/Bujumbura"},
     	new String[]{"Africa/Cairo", "Africa/Cairo"},
     	new String[]{"Africa/Casablanca", "Africa/Casablanca"},
     	new String[]{"Africa/Ceuta", "Europe/Paris"},
     	new String[]{"Africa/Conakry", "Africa/Conakry"},
     	new String[]{"Africa/Dakar", "Africa/Dakar"},
     	new String[]{"Africa/Dar_es_Salaam", "Africa/Dar_es_Salaam"},
     	new String[]{"Africa/Djibouti", "Africa/Djibouti"},
     	new String[]{"Africa/Douala", "Africa/Douala"},
     	new String[]{"Africa/El_Aaiun", "Africa/Casablanca"},
     	new String[]{"Africa/Freetown", "Africa/Freetown"},
     	new String[]{"Africa/Gaborone", "Africa/Gaborone"},
     	new String[]{"Africa/Harare", "Africa/Harare"},
     	new String[]{"Africa/Johannesburg", "Africa/Johannesburg"},
     	new String[]{"Africa/Kampala", "Africa/Kampala"},
     	new String[]{"Africa/Khartoum", "Africa/Khartoum"},
     	new String[]{"Africa/Kigali", "Africa/Kigali"},
     	new String[]{"Africa/Kinshasa", "Africa/Kinshasa"},
     	new String[]{"Africa/Lagos", "Africa/Lagos"},
     	new String[]{"Africa/Libreville", "Africa/Libreville"},
     	new String[]{"Africa/Lome", "Africa/Lome"},
     	new String[]{"Africa/Luanda", "Africa/Luanda"},
     	new String[]{"Africa/Lubumbashi", "Africa/Lubumbashi"},
     	new String[]{"Africa/Lusaka", "Africa/Lusaka"},
     	new String[]{"Africa/Malabo", "Africa/Malabo"},
     	new String[]{"Africa/Maputo", "Africa/Maputo"},
     	new String[]{"Africa/Maseru", "Africa/Maseru"},
     	new String[]{"Africa/Mbabane", "Africa/Mbabane"},
     	new String[]{"Africa/Mogadishu", "Africa/Mogadishu"},
     	new String[]{"Africa/Monrovia", "Africa/Monrovia"},
     	new String[]{"Africa/Nairobi", "Africa/Nairobi"},
     	new String[]{"Africa/Ndjamena", "Africa/Ndjamena"},
     	new String[]{"Africa/Niamey", "Africa/Niamey"},
     	new String[]{"Africa/Nouakchott", "Africa/Nouakchott"},
     	new String[]{"Africa/Ouagadougou", "Africa/Ouagadougou"},
     	new String[]{"Africa/Porto-Novo", "Africa/Porto-Novo"},
     	new String[]{"Africa/Sao_Tome", "Africa/Sao_Tome"},
     	new String[]{"Africa/Timbuktu", "Africa/Timbuktu"},
     	new String[]{"Africa/Tripoli", "Africa/Tripoli"},
     	new String[]{"Africa/Tunis", "Africa/Tunis"},
     	new String[]{"Africa/Windhoek", "Africa/Windhoek"},
     	new String[]{"America/Adak", "America/Adak"},
     	new String[]{"America/Anchorage", "America/Anchorage"},
     	new String[]{"America/Anguilla", "America/Anguilla"},
     	new String[]{"America/Antigua", "America/Antigua"},
     	new String[]{"America/Araguaina", "America/Sao_Paulo"},
     	new String[]{"America/Aruba", "America/Aruba"},
     	new String[]{"America/Asuncion", "America/Asuncion"},
     	new String[]{"America/Atka", "America/Adak"},
     	new String[]{"America/Barbados", "America/Barbados"},
     	new String[]{"America/Belize", "America/Belize"},
     	new String[]{"America/Bogota", "America/Bogota"},
     	new String[]{"America/Boise", "America/Denver"},
     	new String[]{"America/Buenos_Aires", "America/Buenos_Aires"},
     	new String[]{"America/Cancun", "America/Chicago"},
     	new String[]{"America/Caracas", "America/Caracas"},
     	new String[]{"America/Cayenne", "America/Cayenne"},
     	new String[]{"America/Cayman", "America/Cayman"},
     	new String[]{"America/Chicago", "America/Chicago"},
     	new String[]{"America/Chihuahua", "America/Denver"},
     	new String[]{"America/Costa_Rica", "America/Costa_Rica"},
     	new String[]{"America/Cuiaba", "America/Cuiaba"},
     	new String[]{"America/Curacao", "America/Curacao"},
     	new String[]{"America/Dawson", "America/Los_Angeles"},
     	new String[]{"America/Dawson_Creek", "America/Dawson_Creek"},
     	new String[]{"America/Denver", "America/Denver"},
     	new String[]{"America/Detroit", "America/New_York"},
     	new String[]{"America/Dominica", "America/Dominica"},
     	new String[]{"America/Edmonton", "America/Edmonton"},
     	new String[]{"America/El_Salvador", "America/El_Salvador"},
     	new String[]{"America/Ensenada", "America/Tijuana"},
     	new String[]{"America/Fort_Wayne", "America/Indianapolis"},
     	new String[]{"America/Fortaleza", "America/Fortaleza"},
     	new String[]{"America/Glace_Bay", "America/Halifax"},
     	new String[]{"America/Godthab", "America/Godthab"},
     	new String[]{"America/Goose_Bay", "America/Thule"},
     	new String[]{"America/Grand_Turk", "America/Grand_Turk"},
     	new String[]{"America/Grenada", "America/Grenada"},
     	new String[]{"America/Guadeloupe", "America/Guadeloupe"},
     	new String[]{"America/Guatemala", "America/Guatemala"},
     	new String[]{"America/Guayaquil", "America/Guayaquil"},
     	new String[]{"America/Guyana", "America/Guyana"},
     	new String[]{"America/Halifax", "America/Halifax"},
     	new String[]{"America/Havana", "America/Havana"},
     	new String[]{"America/Indiana/Indianapolis", "America/Indianapolis"},
     	new String[]{"America/Indianapolis", "America/Indianapolis"},
     	new String[]{"America/Inuvik", "America/Denver"},
     	new String[]{"America/Iqaluit", "America/New_York"},
     	new String[]{"America/Jamaica", "America/Jamaica"},
     	new String[]{"America/Juneau", "America/Anchorage"},
     	new String[]{"America/La_Paz", "America/La_Paz"},
     	new String[]{"America/Lima", "America/Lima"},
     	new String[]{"America/Los_Angeles", "America/Los_Angeles"},
     	new String[]{"America/Louisville", "America/New_York"},
     	new String[]{"America/Managua", "America/Managua"},
     	new String[]{"America/Manaus", "America/Manaus"},
     	new String[]{"America/Martinique", "America/Martinique"},
     	new String[]{"America/Mazatlan", "America/Mazatlan"},
     	new String[]{"America/Menominee", "America/Winnipeg"},
     	new String[]{"America/Mexico_City", "America/Mexico_City"},
     	new String[]{"America/Miquelon", "America/Miquelon"},
     	new String[]{"America/Montevideo", "America/Montevideo"},
     	new String[]{"America/Montreal", "America/Montreal"},
     	new String[]{"America/Montserrat", "America/Montserrat"},
     	new String[]{"America/Nassau", "America/Nassau"},
     	new String[]{"America/New_York", "America/New_York"},
     	new String[]{"America/Nipigon", "America/New_York"},
     	new String[]{"America/Nome", "America/Anchorage"},
     	new String[]{"America/Noronha", "America/Noronha"},
     	new String[]{"America/Panama", "America/Panama"},
     	new String[]{"America/Pangnirtung", "America/Thule"},
     	new String[]{"America/Paramaribo", "America/Paramaribo"},
     	new String[]{"America/Phoenix", "America/Phoenix"},
     	new String[]{"America/Port-au-Prince", "America/Port-au-Prince"},
     	new String[]{"America/Port_of_Spain", "America/Port_of_Spain"},
     	new String[]{"America/Porto_Acre", "America/Porto_Acre"},
     	new String[]{"America/Puerto_Rico", "America/Puerto_Rico"},
     	new String[]{"America/Rainy_River", "America/Chicago"},
     	new String[]{"America/Rankin_Inlet", "America/Chicago"},
     	new String[]{"America/Regina", "America/Regina"},
     	new String[]{"America/Rio_Branco", "America/Rio_Branco"},
     	new String[]{"America/Santiago", "America/Santiago"},
     	new String[]{"America/Santo_Domingo", "America/Santo_Domingo"},
     	new String[]{"America/Sao_Paulo", "America/Sao_Paulo"},
     	new String[]{"America/Scoresbysund", "America/Scoresbysund"},
     	new String[]{"America/Shiprock", "America/Denver"},
     	new String[]{"America/St_Johns", "America/St_Johns"},
     	new String[]{"America/St_Kitts", "America/St_Kitts"},
     	new String[]{"America/St_Lucia", "America/St_Lucia"},
     	new String[]{"America/St_Thomas", "America/St_Thomas"},
     	new String[]{"America/St_Vincent", "America/St_Vincent"},
     	new String[]{"America/Tegucigalpa", "America/Tegucigalpa"},
     	new String[]{"America/Thule", "America/Thule"},
     	new String[]{"America/Thunder_Bay", "America/New_York"},
     	new String[]{"America/Tijuana", "America/Tijuana"},
     	new String[]{"America/Tortola", "America/Tortola"},
     	new String[]{"America/Vancouver", "America/Vancouver"},
     	new String[]{"America/Virgin", "America/St_Thomas"},
     	new String[]{"America/Whitehorse", "America/Los_Angeles"},
     	new String[]{"America/Winnipeg", "America/Winnipeg"},
     	new String[]{"America/Yakutat", "America/Anchorage"},
     	new String[]{"America/Yellowknife", "America/Denver"},
     	new String[]{"Antarctica/Casey", "Antarctica/Casey"},
     	new String[]{"Antarctica/DumontDUrville", "Antarctica/DumontDUrville"},
     	new String[]{"Antarctica/Mawson", "Antarctica/Mawson"},
     	new String[]{"Antarctica/McMurdo", "Antarctica/McMurdo"},
     	new String[]{"Antarctica/Palmer", "Antarctica/Palmer"},
     	new String[]{"Antarctica/South_Pole", "Antarctica/McMurdo"},
     	new String[]{"Arctic/Longyearbyen", "Europe/Oslo"},
     	new String[]{"Asia/Aden", "Asia/Aden"},
     	new String[]{"Asia/Almaty", "Asia/Almaty"},
     	new String[]{"Asia/Amman", "Asia/Amman"},
     	new String[]{"Asia/Anadyr", "Asia/Anadyr"},
     	new String[]{"Asia/Aqtau", "Asia/Aqtau"},
     	new String[]{"Asia/Aqtobe", "Asia/Aqtobe"},
     	new String[]{"Asia/Ashkhabad", "Asia/Ashkhabad"},
     	new String[]{"Asia/Baghdad", "Asia/Baghdad"},
     	new String[]{"Asia/Bahrain", "Asia/Bahrain"},
     	new String[]{"Asia/Baku", "Asia/Baku"},
     	new String[]{"Asia/Bangkok", "Asia/Bangkok"},
     	new String[]{"Asia/Beirut", "Asia/Beirut"},
     	new String[]{"Asia/Bishkek", "Asia/Bishkek"},
     	new String[]{"Asia/Brunei", "Asia/Brunei"},
     	new String[]{"Asia/Calcutta", "Asia/Calcutta"},
     	new String[]{"Asia/Chungking", "Asia/Shanghai"},
     	new String[]{"Asia/Colombo", "Asia/Colombo"},
     	new String[]{"Asia/Dacca", "Asia/Dacca"},
     	new String[]{"Asia/Damascus", "Asia/Damascus"},
     	new String[]{"Asia/Dhaka", "Asia/Dhaka"},
     	new String[]{"Asia/Dubai", "Asia/Dubai"},
     	new String[]{"Asia/Dushanbe", "Asia/Dushanbe"},
     	new String[]{"Asia/Gaza", "Asia/Amman"},
     	new String[]{"Asia/Harbin", "Asia/Shanghai"},
     	new String[]{"Asia/Hong_Kong", "Asia/Hong_Kong"},
     	new String[]{"Asia/Irkutsk", "Asia/Irkutsk"},
     	new String[]{"Asia/Istanbul", "Europe/Istanbul"},
     	new String[]{"Asia/Jakarta", "Asia/Jakarta"},
     	new String[]{"Asia/Jayapura", "Asia/Jayapura"},
     	new String[]{"Asia/Jerusalem", "Asia/Jerusalem"},
     	new String[]{"Asia/Kabul", "Asia/Kabul"},
     	new String[]{"Asia/Kamchatka", "Asia/Kamchatka"},
     	new String[]{"Asia/Karachi", "Asia/Karachi"},
     	new String[]{"Asia/Kashgar", "Asia/Shanghai"},
     	new String[]{"Asia/Katmandu", "Asia/Katmandu"},
     	new String[]{"Asia/Krasnoyarsk", "Asia/Krasnoyarsk"},
     	new String[]{"Asia/Kuala_Lumpur", "Asia/Kuala_Lumpur"},
     	new String[]{"Asia/Kuwait", "Asia/Kuwait"},
     	new String[]{"Asia/Macao", "Asia/Macao"},
     	new String[]{"Asia/Magadan", "Asia/Magadan"},
     	new String[]{"Asia/Manila", "Asia/Manila"},
     	new String[]{"Asia/Muscat", "Asia/Muscat"},
     	new String[]{"Asia/Nicosia", "Asia/Nicosia"},
     	new String[]{"Asia/Novosibirsk", "Asia/Novosibirsk"},
     	new String[]{"Asia/Omsk", "Asia/Novosibirsk"},
     	new String[]{"Asia/Phnom_Penh", "Asia/Phnom_Penh"},
     	new String[]{"Asia/Pyongyang", "Asia/Pyongyang"},
     	new String[]{"Asia/Qatar", "Asia/Qatar"},
     	new String[]{"Asia/Rangoon", "Asia/Rangoon"},
     	new String[]{"Asia/Riyadh", "Asia/Riyadh"},
     	new String[]{"Asia/Saigon", "Asia/Saigon"},
     	new String[]{"Asia/Seoul", "Asia/Seoul"},
     	new String[]{"Asia/Shanghai", "Asia/Shanghai"},
     	new String[]{"Asia/Singapore", "Asia/Singapore"},
     	new String[]{"Asia/Taipei", "Asia/Taipei"},
     	new String[]{"Asia/Tashkent", "Asia/Tashkent"},
     	new String[]{"Asia/Tbilisi", "Asia/Tbilisi"},
     	new String[]{"Asia/Tehran", "Asia/Tehran"},
     	new String[]{"Asia/Tel_Aviv", "Asia/Jerusalem"},
     	new String[]{"Asia/Thimbu", "Asia/Thimbu"},
     	new String[]{"Asia/Tokyo", "Asia/Tokyo"},
     	new String[]{"Asia/Ujung_Pandang", "Asia/Ujung_Pandang"},
     	new String[]{"Asia/Ulan_Bator", "Asia/Ulaanbaatar"},
     	new String[]{"Asia/Urumqi", "Asia/Shanghai"},
     	new String[]{"Asia/Vientiane", "Asia/Vientiane"},
     	new String[]{"Asia/Vladivostok", "Asia/Vladivostok"},
     	new String[]{"Asia/Yakutsk", "Asia/Yakutsk"},
     	new String[]{"Asia/Yekaterinburg", "Asia/Yekaterinburg"},
     	new String[]{"Asia/Yerevan", "Asia/Yerevan"},
     	new String[]{"Atlantic/Azores", "Atlantic/Azores"},
     	new String[]{"Atlantic/Bermuda", "Atlantic/Bermuda"},
     	new String[]{"Atlantic/Canary", "Atlantic/Canary"},
     	new String[]{"Atlantic/Cape_Verde", "Atlantic/Cape_Verde"},
     	new String[]{"Atlantic/Faeroe", "Atlantic/Faeroe"},
     	new String[]{"Atlantic/Jan_Mayen", "Atlantic/Jan_Mayen"},
     	new String[]{"Atlantic/Madeira", "Europe/London"},
     	new String[]{"Atlantic/Reykjavik", "Atlantic/Reykjavik"},
     	new String[]{"Atlantic/South_Georgia", "Atlantic/South_Georgia"},
     	new String[]{"Atlantic/St_Helena", "Atlantic/St_Helena"},
     	new String[]{"Atlantic/Stanley", "Atlantic/Stanley"},
     	new String[]{"Australia/ACT", "Australia/Sydney"},
     	new String[]{"Australia/Adelaide", "Australia/Adelaide"},
     	new String[]{"Australia/Brisbane", "Australia/Brisbane"},
     	new String[]{"Australia/Broken_Hill", "Australia/Broken_Hill"},
     	new String[]{"Australia/Canberra", "Australia/Sydney"},
     	new String[]{"Australia/Darwin", "Australia/Darwin"},
     	new String[]{"Australia/Hobart", "Australia/Hobart"},
     	new String[]{"Australia/LHI", "Australia/Lord_Howe"},
     	new String[]{"Australia/Lord_Howe", "Australia/Lord_Howe"},
     	new String[]{"Australia/Melbourne", "Australia/Sydney"},
     	new String[]{"Australia/NSW", "Australia/Sydney"},
     	new String[]{"Australia/North", "Australia/Darwin"},
     	new String[]{"Australia/Perth", "Australia/Perth"},
     	new String[]{"Australia/Queensland", "Australia/Brisbane"},
     	new String[]{"Australia/South", "Australia/Adelaide"},
     	new String[]{"Australia/Sydney", "Australia/Sydney"},
     	new String[]{"Australia/Tasmania", "Australia/Hobart"},
     	new String[]{"Australia/Victoria", "Australia/Sydney"},
     	new String[]{"Australia/West", "Australia/Perth"},
     	new String[]{"Australia/Yancowinna", "Australia/Broken_Hill"},
     	new String[]{"Brazil/Acre", "America/Rio_Branco"},
     	new String[]{"Brazil/DeNoronha", "America/Noronha"},
     	new String[]{"Brazil/East", "America/Sao_Paulo"},
     	new String[]{"Brazil/West", "America/Manaus"},
     	new String[]{"CET", "Europe/Paris"},
     	new String[]{"CST6CDT", "America/Chicago"},
     	new String[]{"Canada/Atlantic", "America/Halifax"},
     	new String[]{"Canada/Central", "America/Winnipeg"},
     	new String[]{"Canada/East-Saskatchewan", "America/Regina"},
     	new String[]{"Canada/Eastern", "America/Montreal"},
     	new String[]{"Canada/Mountain", "America/Edmonton"},
     	new String[]{"Canada/Newfoundland", "America/St_Johns"},
     	new String[]{"Canada/Pacific", "America/Vancouver"},
     	new String[]{"Canada/Saskatchewan", "America/Regina"},
     	new String[]{"Canada/Yukon", "America/Los_Angeles"},
     	new String[]{"Chile/Continental", "America/Santiago"},
     	new String[]{"Chile/EasterIsland", "Pacific/Easter"},
     	new String[]{"Cuba", "America/Havana"},
     	new String[]{"EET", "Europe/Istanbul"},
     	new String[]{"EST", "America/Indianapolis"},
     	new String[]{"EST5EDT", "America/New_York"},
     	new String[]{"Egypt", "Africa/Cairo"},
     	new String[]{"Eire", "Europe/Dublin"},
     	new String[]{"Etc/GMT", "GMT"},
     	new String[]{"Etc/GMT0", "GMT"},
     	new String[]{"Etc/Greenwich", "GMT"},
     	new String[]{"Etc/UCT", "UTC"},
     	new String[]{"Etc/UTC", "UTC"},
     	new String[]{"Etc/Universal", "UTC"},
     	new String[]{"Etc/Zulu", "UTC"},
     	new String[]{"Europe/Amsterdam", "Europe/Amsterdam"},
     	new String[]{"Europe/Andorra", "Europe/Andorra"},
     	new String[]{"Europe/Athens", "Europe/Athens"},
     	new String[]{"Europe/Belfast", "Europe/London"},
     	new String[]{"Europe/Belgrade", "Europe/Belgrade"},
     	new String[]{"Europe/Berlin", "Europe/Berlin"},
     	new String[]{"Europe/Bratislava", "Europe/Prague"},
     	new String[]{"Europe/Brussels", "Europe/Brussels"},
     	new String[]{"Europe/Bucharest", "Europe/Bucharest"},
     	new String[]{"Europe/Budapest", "Europe/Budapest"},
     	new String[]{"Europe/Chisinau", "Europe/Chisinau"},
     	new String[]{"Europe/Copenhagen", "Europe/Copenhagen"},
     	new String[]{"Europe/Dublin", "Europe/Dublin"},
     	new String[]{"Europe/Gibraltar", "Europe/Gibraltar"},
     	new String[]{"Europe/Helsinki", "Europe/Helsinki"},
     	new String[]{"Europe/Istanbul", "Europe/Istanbul"},
     	new String[]{"Europe/Kaliningrad", "Europe/Kaliningrad"},
     	new String[]{"Europe/Kiev", "Europe/Kiev"},
     	new String[]{"Europe/Lisbon", "Europe/Lisbon"},
     	new String[]{"Europe/Ljubljana", "Europe/Belgrade"},
     	new String[]{"Europe/London", "Europe/London"},
     	new String[]{"Europe/Luxembourg", "Europe/Luxembourg"},
     	new String[]{"Europe/Madrid", "Europe/Madrid"},
     	new String[]{"Europe/Malta", "Europe/Malta"},
     	new String[]{"Europe/Minsk", "Europe/Minsk"},
     	new String[]{"Europe/Monaco", "Europe/Monaco"},
     	new String[]{"Europe/Moscow", "Europe/Moscow"},
     	new String[]{"Europe/Oslo", "Europe/Oslo"},
     	new String[]{"Europe/Paris", "Europe/Paris"},
     	new String[]{"Europe/Prague", "Europe/Prague"},
     	new String[]{"Europe/Riga", "Europe/Riga"},
     	new String[]{"Europe/Rome", "Europe/Rome"},
     	new String[]{"Europe/Samara", "Europe/Samara"},
     	new String[]{"Europe/San_Marino", "Europe/Rome"},
     	new String[]{"Europe/Sarajevo", "Europe/Belgrade"},
     	new String[]{"Europe/Simferopol", "Europe/Simferopol"},
     	new String[]{"Europe/Skopje", "Europe/Belgrade"},
     	new String[]{"Europe/Sofia", "Europe/Sofia"},
     	new String[]{"Europe/Stockholm", "Europe/Stockholm"},
     	new String[]{"Europe/Tallinn", "Europe/Tallinn"},
     	new String[]{"Europe/Tirane", "Europe/Tirane"},
     	new String[]{"Europe/Vaduz", "Europe/Vaduz"},
     	new String[]{"Europe/Vatican", "Europe/Rome"},
     	new String[]{"Europe/Vienna", "Europe/Vienna"},
     	new String[]{"Europe/Vilnius", "Europe/Vilnius"},
     	new String[]{"Europe/Warsaw", "Europe/Warsaw"},
     	new String[]{"Europe/Zagreb", "Europe/Belgrade"},
     	new String[]{"Europe/Zurich", "Europe/Zurich"},
     	new String[]{"Factory", "GMT"},
     	new String[]{"GB", "Europe/London"},
     	new String[]{"GMT", "GMT"},
     	new String[]{"GMT0", "GMT"},
     	new String[]{"Greenwich", "GMT"},
     	new String[]{"HST", "Pacific/Honolulu"},
     	new String[]{"Hongkong", "Asia/Hong_Kong"},
     	new String[]{"Iceland", "Atlantic/Reykjavik"},
     	new String[]{"Indian/Antananarivo", "Indian/Antananarivo"},
     	new String[]{"Indian/Chagos", "Indian/Chagos"},
     	new String[]{"Indian/Christmas", "Indian/Christmas"},
     	new String[]{"Indian/Cocos", "Indian/Cocos"},
     	new String[]{"Indian/Comoro", "Indian/Comoro"},
     	new String[]{"Indian/Kerguelen", "Indian/Kerguelen"},
     	new String[]{"Indian/Mahe", "Indian/Mahe"},
     	new String[]{"Indian/Maldives", "Indian/Maldives"},
     	new String[]{"Indian/Mauritius", "Indian/Mauritius"},
     	new String[]{"Indian/Mayotte", "Indian/Mayotte"},
     	new String[]{"Indian/Reunion", "Indian/Reunion"},
     	new String[]{"Iran", "Asia/Tehran"},
     	new String[]{"Israel", "Asia/Jerusalem"},
     	new String[]{"Jamaica", "America/Jamaica"},
     	new String[]{"Japan", "Asia/Tokyo"},
     	new String[]{"Libya", "Africa/Tripoli"},
     	new String[]{"MET", "Europe/Paris"},
     	new String[]{"MST", "America/Phoenix"},
     	new String[]{"MST7MDT", "America/Denver"},
     	new String[]{"Mexico/BajaNorte", "America/Tijuana"},
     	new String[]{"Mexico/BajaSur", "America/Mazatlan"},
     	new String[]{"Mexico/General", "America/Mexico_City"},
     	new String[]{"NZ", "Pacific/Auckland"},
     	new String[]{"NZ-CHAT", "Pacific/Chatham"},
     	new String[]{"Navajo", "America/Denver"},
     	new String[]{"PRC", "Asia/Shanghai"},
     	new String[]{"PST8PDT", "America/Los_Angeles"},
     	new String[]{"Pacific/Apia", "Pacific/Apia"},
     	new String[]{"Pacific/Auckland", "Pacific/Auckland"},
     	new String[]{"Pacific/Chatham", "Pacific/Chatham"},
     	new String[]{"Pacific/Easter", "Pacific/Easter"},
     	new String[]{"Pacific/Efate", "Pacific/Efate"},
     	new String[]{"Pacific/Enderbury", "Pacific/Enderbury"},
     	new String[]{"Pacific/Fakaofo", "Pacific/Fakaofo"},
     	new String[]{"Pacific/Fiji", "Pacific/Fiji"},
     	new String[]{"Pacific/Funafuti", "Pacific/Funafuti"},
     	new String[]{"Pacific/Galapagos", "Pacific/Galapagos"},
     	new String[]{"Pacific/Gambier", "Pacific/Gambier"},
     	new String[]{"Pacific/Guadalcanal", "Pacific/Guadalcanal"},
     	new String[]{"Pacific/Guam", "Pacific/Guam"},
     	new String[]{"Pacific/Honolulu", "Pacific/Honolulu"},
     	new String[]{"Pacific/Kiritimati", "Pacific/Kiritimati"},
     	new String[]{"Pacific/Kosrae", "Pacific/Kosrae"},
     	new String[]{"Pacific/Majuro", "Pacific/Majuro"},
     	new String[]{"Pacific/Marquesas", "Pacific/Marquesas"},
     	new String[]{"Pacific/Nauru", "Pacific/Nauru"},
     	new String[]{"Pacific/Niue", "Pacific/Niue"},
     	new String[]{"Pacific/Norfolk", "Pacific/Norfolk"},
     	new String[]{"Pacific/Noumea", "Pacific/Noumea"},
     	new String[]{"Pacific/Pago_Pago", "Pacific/Pago_Pago"},
     	new String[]{"Pacific/Palau", "Pacific/Palau"},
     	new String[]{"Pacific/Pitcairn", "Pacific/Pitcairn"},
     	new String[]{"Pacific/Ponape", "Pacific/Ponape"},
     	new String[]{"Pacific/Port_Moresby", "Pacific/Port_Moresby"},
     	new String[]{"Pacific/Rarotonga", "Pacific/Rarotonga"},
     	new String[]{"Pacific/Saipan", "Pacific/Saipan"},
     	new String[]{"Pacific/Samoa", "Pacific/Pago_Pago"},
     	new String[]{"Pacific/Tahiti", "Pacific/Tahiti"},
     	new String[]{"Pacific/Tarawa", "Pacific/Tarawa"},
     	new String[]{"Pacific/Tongatapu", "Pacific/Tongatapu"},
     	new String[]{"Pacific/Truk", "Pacific/Truk"},
     	new String[]{"Pacific/Wake", "Pacific/Wake"},
     	new String[]{"Pacific/Wallis", "Pacific/Wallis"},
     	new String[]{"Poland", "Europe/Warsaw"},
     	new String[]{"Portugal", "Europe/Lisbon"},
     	new String[]{"ROC", "Asia/Taipei"},
     	new String[]{"ROK", "Asia/Seoul"},
     	new String[]{"Singapore", "Asia/Singapore"},
     	new String[]{"SystemV/AST4ADT", "America/Thule"},
     	new String[]{"SystemV/CST6CDT", "America/Chicago"},
     	new String[]{"SystemV/EST5EDT", "America/New_York"},
     	new String[]{"SystemV/MST7MDT", "America/Denver"},
     	new String[]{"SystemV/PST8PDT", "America/Los_Angeles"},
     	new String[]{"SystemV/YST9YDT", "America/Anchorage"},
     	new String[]{"Turkey", "Europe/Istanbul"},
     	new String[]{"UCT", "UTC"},
     	new String[]{"US/Alaska", "America/Anchorage"},
     	new String[]{"US/Aleutian", "America/Adak"},
     	new String[]{"US/Arizona", "America/Phoenix"},
     	new String[]{"US/Central", "America/Chicago"},
     	new String[]{"US/East-Indiana", "America/Indianapolis"},
     	new String[]{"US/Eastern", "America/New_York"},
     	new String[]{"US/Hawaii", "Pacific/Honolulu"},
     	new String[]{"US/Michigan", "America/New_York"},
     	new String[]{"US/Mountain", "America/Denver"},
     	new String[]{"US/Pacific", "America/Los_Angeles"},
     	new String[]{"US/Samoa", "Pacific/Pago_Pago"},
     	new String[]{"UTC", "UTC"},
     	new String[]{"Universal", "UTC"},
     	new String[]{"W-SU", "Europe/Moscow"},
     	new String[]{"WET", "WET"},
     	new String[]{"Zulu", "UTC"},
     	new String[]{"posix/Africa/Abidjan", "Africa/Abidjan"},
     	new String[]{"posix/Africa/Accra", "Africa/Accra"},
     	new String[]{"posix/Africa/Addis_Ababa", "Africa/Addis_Ababa"},
     	new String[]{"posix/Africa/Algiers", "Africa/Algiers"},
     	new String[]{"posix/Africa/Asmera", "Africa/Asmera"},
     	new String[]{"posix/Africa/Bamako", "GMT"},
     	new String[]{"posix/Africa/Bangui", "Africa/Bangui"},
     	new String[]{"posix/Africa/Banjul", "Africa/Banjul"},
     	new String[]{"posix/Africa/Bissau", "Africa/Bissau"},
     	new String[]{"posix/Africa/Blantyre", "Africa/Blantyre"},
     	new String[]{"posix/Africa/Brazzaville", "Africa/Luanda"},
     	new String[]{"posix/Africa/Bujumbura", "Africa/Bujumbura"},
     	new String[]{"posix/Africa/Cairo", "Africa/Cairo"},
     	new String[]{"posix/Africa/Casablanca", "Africa/Casablanca"},
     	new String[]{"posix/Africa/Ceuta", "Europe/Paris"},
     	new String[]{"posix/Africa/Conakry", "Africa/Conakry"},
     	new String[]{"posix/Africa/Dakar", "Africa/Dakar"},
     	new String[]{"posix/Africa/Dar_es_Salaam", "Africa/Dar_es_Salaam"},
     	new String[]{"posix/Africa/Djibouti", "Africa/Djibouti"},
     	new String[]{"posix/Africa/Douala", "Africa/Douala"},
     	new String[]{"posix/Africa/El_Aaiun", "Africa/Casablanca"},
     	new String[]{"posix/Africa/Freetown", "Africa/Freetown"},
     	new String[]{"posix/Africa/Gaborone", "Africa/Gaborone"},
     	new String[]{"posix/Africa/Harare", "Africa/Harare"},
     	new String[]{"posix/Africa/Johannesburg", "Africa/Johannesburg"},
     	new String[]{"posix/Africa/Kampala", "Africa/Kampala"},
     	new String[]{"posix/Africa/Khartoum", "Africa/Khartoum"},
     	new String[]{"posix/Africa/Kigali", "Africa/Kigali"},
     	new String[]{"posix/Africa/Kinshasa", "Africa/Kinshasa"},
     	new String[]{"posix/Africa/Lagos", "Africa/Lagos"},
     	new String[]{"posix/Africa/Libreville", "Africa/Libreville"},
     	new String[]{"posix/Africa/Lome", "Africa/Lome"},
     	new String[]{"posix/Africa/Luanda", "Africa/Luanda"},
     	new String[]{"posix/Africa/Lubumbashi", "Africa/Lubumbashi"},
     	new String[]{"posix/Africa/Lusaka", "Africa/Lusaka"},
     	new String[]{"posix/Africa/Malabo", "Africa/Malabo"},
     	new String[]{"posix/Africa/Maputo", "Africa/Maputo"},
     	new String[]{"posix/Africa/Maseru", "Africa/Maseru"},
     	new String[]{"posix/Africa/Mbabane", "Africa/Mbabane"},
     	new String[]{"posix/Africa/Mogadishu", "Africa/Mogadishu"},
     	new String[]{"posix/Africa/Monrovia", "Africa/Monrovia"},
     	new String[]{"posix/Africa/Nairobi", "Africa/Nairobi"},
     	new String[]{"posix/Africa/Ndjamena", "Africa/Ndjamena"},
     	new String[]{"posix/Africa/Niamey", "Africa/Niamey"},
     	new String[]{"posix/Africa/Nouakchott", "Africa/Nouakchott"},
     	new String[]{"posix/Africa/Ouagadougou", "Africa/Ouagadougou"},
     	new String[]{"posix/Africa/Porto-Novo", "Africa/Porto-Novo"},
     	new String[]{"posix/Africa/Sao_Tome", "Africa/Sao_Tome"},
     	new String[]{"posix/Africa/Timbuktu", "Africa/Timbuktu"},
     	new String[]{"posix/Africa/Tripoli", "Africa/Tripoli"},
     	new String[]{"posix/Africa/Tunis", "Africa/Tunis"},
     	new String[]{"posix/Africa/Windhoek", "Africa/Windhoek"},
     	new String[]{"posix/America/Adak", "America/Adak"},
     	new String[]{"posix/America/Anchorage", "America/Anchorage"},
     	new String[]{"posix/America/Anguilla", "America/Anguilla"},
     	new String[]{"posix/America/Antigua", "America/Antigua"},
     	new String[]{"posix/America/Araguaina", "America/Sao_Paulo"},
     	new String[]{"posix/America/Aruba", "America/Aruba"},
     	new String[]{"posix/America/Asuncion", "America/Asuncion"},
     	new String[]{"posix/America/Atka", "America/Adak"},
     	new String[]{"posix/America/Barbados", "America/Barbados"},
     	new String[]{"posix/America/Belize", "America/Belize"},
     	new String[]{"posix/America/Bogota", "America/Bogota"},
     	new String[]{"posix/America/Boise", "America/Denver"},
     	new String[]{"posix/America/Buenos_Aires", "America/Buenos_Aires"},
     	new String[]{"posix/America/Cancun", "America/Chicago"},
     	new String[]{"posix/America/Caracas", "America/Caracas"},
     	new String[]{"posix/America/Cayenne", "America/Cayenne"},
     	new String[]{"posix/America/Cayman", "America/Cayman"},
     	new String[]{"posix/America/Chicago", "America/Chicago"},
     	new String[]{"posix/America/Chihuahua", "America/Denver"},
     	new String[]{"posix/America/Costa_Rica", "America/Costa_Rica"},
     	new String[]{"posix/America/Cuiaba", "America/Cuiaba"},
     	new String[]{"posix/America/Curacao", "America/Curacao"},
     	new String[]{"posix/America/Dawson", "America/Los_Angeles"},
     	new String[]{"posix/America/Dawson_Creek", "America/Dawson_Creek"},
     	new String[]{"posix/America/Denver", "America/Denver"},
     	new String[]{"posix/America/Detroit", "America/New_York"},
     	new String[]{"posix/America/Dominica", "America/Dominica"},
     	new String[]{"posix/America/Edmonton", "America/Edmonton"},
     	new String[]{"posix/America/El_Salvador", "America/El_Salvador"},
     	new String[]{"posix/America/Ensenada", "America/Tijuana"},
     	new String[]{"posix/America/Fort_Wayne", "America/Indianapolis"},
     	new String[]{"posix/America/Fortaleza", "America/Fortaleza"},
     	new String[]{"posix/America/Glace_Bay", "America/Halifax"},
     	new String[]{"posix/America/Godthab", "America/Godthab"},
     	new String[]{"posix/America/Goose_Bay", "America/Thule"},
     	new String[]{"posix/America/Grand_Turk", "America/Grand_Turk"},
     	new String[]{"posix/America/Grenada", "America/Grenada"},
     	new String[]{"posix/America/Guadeloupe", "America/Guadeloupe"},
     	new String[]{"posix/America/Guatemala", "America/Guatemala"},
     	new String[]{"posix/America/Guayaquil", "America/Guayaquil"},
     	new String[]{"posix/America/Guyana", "America/Guyana"},
     	new String[]{"posix/America/Halifax", "America/Halifax"},
     	new String[]{"posix/America/Havana", "America/Havana"},
     	new String[]{"posix/America/Indiana/Indianapolis", "America/Indianapolis"},
     	new String[]{"posix/America/Indianapolis", "America/Indianapolis"},
     	new String[]{"posix/America/Inuvik", "America/Denver"},
     	new String[]{"posix/America/Iqaluit", "America/New_York"},
     	new String[]{"posix/America/Jamaica", "America/Jamaica"},
     	new String[]{"posix/America/Juneau", "America/Anchorage"},
     	new String[]{"posix/America/La_Paz", "America/La_Paz"},
     	new String[]{"posix/America/Lima", "America/Lima"},
     	new String[]{"posix/America/Los_Angeles", "America/Los_Angeles"},
     	new String[]{"posix/America/Louisville", "America/New_York"},
     	new String[]{"posix/America/Managua", "America/Managua"},
     	new String[]{"posix/America/Manaus", "America/Manaus"},
     	new String[]{"posix/America/Martinique", "America/Martinique"},
     	new String[]{"posix/America/Mazatlan", "America/Mazatlan"},
     	new String[]{"posix/America/Menominee", "America/Winnipeg"},
     	new String[]{"posix/America/Mexico_City", "America/Mexico_City"},
     	new String[]{"posix/America/Miquelon", "America/Miquelon"},
     	new String[]{"posix/America/Montevideo", "America/Montevideo"},
     	new String[]{"posix/America/Montreal", "America/Montreal"},
     	new String[]{"posix/America/Montserrat", "America/Montserrat"},
     	new String[]{"posix/America/Nassau", "America/Nassau"},
     	new String[]{"posix/America/New_York", "America/New_York"},
     	new String[]{"posix/America/Nipigon", "America/New_York"},
     	new String[]{"posix/America/Nome", "America/Anchorage"},
     	new String[]{"posix/America/Noronha", "America/Noronha"},
     	new String[]{"posix/America/Panama", "America/Panama"},
     	new String[]{"posix/America/Pangnirtung", "America/Thule"},
     	new String[]{"posix/America/Paramaribo", "America/Paramaribo"},
     	new String[]{"posix/America/Phoenix", "America/Phoenix"},
     	new String[]{"posix/America/Port-au-Prince", "America/Port-au-Prince"},
     	new String[]{"posix/America/Port_of_Spain", "America/Port_of_Spain"},
     	new String[]{"posix/America/Porto_Acre", "America/Porto_Acre"},
     	new String[]{"posix/America/Puerto_Rico", "America/Puerto_Rico"},
     	new String[]{"posix/America/Rainy_River", "America/Chicago"},
     	new String[]{"posix/America/Rankin_Inlet", "America/Chicago"},
     	new String[]{"posix/America/Regina", "America/Regina"},
     	new String[]{"posix/America/Rio_Branco", "America/Rio_Branco"},
     	new String[]{"posix/America/Santiago", "America/Santiago"},
     	new String[]{"posix/America/Santo_Domingo", "America/Santo_Domingo"},
     	new String[]{"posix/America/Sao_Paulo", "America/Sao_Paulo"},
     	new String[]{"posix/America/Scoresbysund", "America/Scoresbysund"},
     	new String[]{"posix/America/Shiprock", "America/Denver"},
     	new String[]{"posix/America/St_Johns", "America/St_Johns"},
     	new String[]{"posix/America/St_Kitts", "America/St_Kitts"},
     	new String[]{"posix/America/St_Lucia", "America/St_Lucia"},
     	new String[]{"posix/America/St_Thomas", "America/St_Thomas"},
     	new String[]{"posix/America/St_Vincent", "America/St_Vincent"},
     	new String[]{"posix/America/Tegucigalpa", "America/Tegucigalpa"},
     	new String[]{"posix/America/Thule", "America/Thule"},
     	new String[]{"posix/America/Thunder_Bay", "America/New_York"},
     	new String[]{"posix/America/Tijuana", "America/Tijuana"},
     	new String[]{"posix/America/Tortola", "America/Tortola"},
     	new String[]{"posix/America/Vancouver", "America/Vancouver"},
     	new String[]{"posix/America/Virgin", "America/St_Thomas"},
     	new String[]{"posix/America/Whitehorse", "America/Los_Angeles"},
     	new String[]{"posix/America/Winnipeg", "America/Winnipeg"},
     	new String[]{"posix/America/Yakutat", "America/Anchorage"},
     	new String[]{"posix/America/Yellowknife", "America/Denver"},
     	new String[]{"posix/Antarctica/Casey", "Antarctica/Casey"},
     	new String[]{"posix/Antarctica/DumontDUrville", "Antarctica/DumontDUrville"},
     	new String[]{"posix/Antarctica/Mawson", "Antarctica/Mawson"},
     	new String[]{"posix/Antarctica/McMurdo", "Antarctica/McMurdo"},
     	new String[]{"posix/Antarctica/Palmer", "Antarctica/Palmer"},
     	new String[]{"posix/Antarctica/South_Pole", "Antarctica/McMurdo"},
     	new String[]{"posix/Arctic/Longyearbyen", "Europe/Oslo"},
     	new String[]{"posix/Asia/Aden", "Asia/Aden"},
     	new String[]{"posix/Asia/Almaty", "Asia/Almaty"},
     	new String[]{"posix/Asia/Amman", "Asia/Amman"},
     	new String[]{"posix/Asia/Anadyr", "Asia/Anadyr"},
     	new String[]{"posix/Asia/Aqtau", "Asia/Aqtau"},
     	new String[]{"posix/Asia/Aqtobe", "Asia/Aqtobe"},
     	new String[]{"posix/Asia/Ashkhabad", "Asia/Ashkhabad"},
     	new String[]{"posix/Asia/Baghdad", "Asia/Baghdad"},
     	new String[]{"posix/Asia/Bahrain", "Asia/Bahrain"},
     	new String[]{"posix/Asia/Baku", "Asia/Baku"},
     	new String[]{"posix/Asia/Bangkok", "Asia/Bangkok"},
     	new String[]{"posix/Asia/Beirut", "Asia/Beirut"},
     	new String[]{"posix/Asia/Bishkek", "Asia/Bishkek"},
     	new String[]{"posix/Asia/Brunei", "Asia/Brunei"},
     	new String[]{"posix/Asia/Calcutta", "Asia/Calcutta"},
     	new String[]{"posix/Asia/Chungking", "Asia/Shanghai"},
     	new String[]{"posix/Asia/Colombo", "Asia/Colombo"},
     	new String[]{"posix/Asia/Dacca", "Asia/Dacca"},
     	new String[]{"posix/Asia/Damascus", "Asia/Damascus"},
     	new String[]{"posix/Asia/Dubai", "Asia/Dubai"},
     	new String[]{"posix/Asia/Dushanbe", "Asia/Dushanbe"},
     	new String[]{"posix/Asia/Gaza", "Asia/Amman"},
     	new String[]{"posix/Asia/Harbin", "Asia/Shanghai"},
     	new String[]{"posix/Asia/Hong_Kong", "Asia/Hong_Kong"},
     	new String[]{"posix/Asia/Irkutsk", "Asia/Irkutsk"},
     	new String[]{"posix/Asia/Istanbul", "Europe/Istanbul"},
     	new String[]{"posix/Asia/Jakarta", "Asia/Jakarta"},
     	new String[]{"posix/Asia/Jayapura", "Asia/Jayapura"},
     	new String[]{"posix/Asia/Jerusalem", "Asia/Jerusalem"},
     	new String[]{"posix/Asia/Kabul", "Asia/Kabul"},
     	new String[]{"posix/Asia/Kamchatka", "Asia/Kamchatka"},
     	new String[]{"posix/Asia/Karachi", "Asia/Karachi"},
     	new String[]{"posix/Asia/Kashgar", "Asia/Shanghai"},
     	new String[]{"posix/Asia/Katmandu", "Asia/Katmandu"},
     	new String[]{"posix/Asia/Krasnoyarsk", "Asia/Krasnoyarsk"},
     	new String[]{"posix/Asia/Kuala_Lumpur", "Asia/Kuala_Lumpur"},
     	new String[]{"posix/Asia/Kuwait", "Asia/Kuwait"},
     	new String[]{"posix/Asia/Macao", "Asia/Macao"},
     	new String[]{"posix/Asia/Magadan", "Asia/Magadan"},
     	new String[]{"posix/Asia/Manila", "Asia/Manila"},
     	new String[]{"posix/Asia/Muscat", "Asia/Muscat"},
     	new String[]{"posix/Asia/Nicosia", "Asia/Nicosia"},
     	new String[]{"posix/Asia/Novosibirsk", "Asia/Novosibirsk"},
     	new String[]{"posix/Asia/Omsk", "Asia/Novosibirsk"},
     	new String[]{"posix/Asia/Phnom_Penh", "Asia/Phnom_Penh"},
     	new String[]{"posix/Asia/Pyongyang", "Asia/Pyongyang"},
     	new String[]{"posix/Asia/Qatar", "Asia/Qatar"},
     	new String[]{"posix/Asia/Rangoon", "Asia/Rangoon"},
     	new String[]{"posix/Asia/Riyadh", "Asia/Riyadh"},
     	new String[]{"posix/Asia/Saigon", "Asia/Saigon"},
     	new String[]{"posix/Asia/Seoul", "Asia/Seoul"},
     	new String[]{"posix/Asia/Shanghai", "Asia/Shanghai"},
     	new String[]{"posix/Asia/Singapore", "Asia/Singapore"},
     	new String[]{"posix/Asia/Taipei", "Asia/Taipei"},
     	new String[]{"posix/Asia/Tashkent", "Asia/Tashkent"},
     	new String[]{"posix/Asia/Tbilisi", "Asia/Tbilisi"},
     	new String[]{"posix/Asia/Tehran", "Asia/Tehran"},
     	new String[]{"posix/Asia/Tel_Aviv", "Asia/Jerusalem"},
     	new String[]{"posix/Asia/Thimbu", "Asia/Thimbu"},
     	new String[]{"posix/Asia/Tokyo", "Asia/Tokyo"},
     	new String[]{"posix/Asia/Ujung_Pandang", "Asia/Ujung_Pandang"},
     	new String[]{"posix/Asia/Ulan_Bator", "Asia/Ulaanbaatar"},
     	new String[]{"posix/Asia/Urumqi", "Asia/Shanghai"},
     	new String[]{"posix/Asia/Vientiane", "Asia/Vientiane"},
     	new String[]{"posix/Asia/Vladivostok", "Asia/Vladivostok"},
     	new String[]{"posix/Asia/Yakutsk", "Asia/Yakutsk"},
     	new String[]{"posix/Asia/Yekaterinburg", "Asia/Yekaterinburg"},
     	new String[]{"posix/Asia/Yerevan", "Asia/Yerevan"},
     	new String[]{"posix/Atlantic/Azores", "Atlantic/Azores"},
     	new String[]{"posix/Atlantic/Bermuda", "Atlantic/Bermuda"},
     	new String[]{"posix/Atlantic/Canary", "Atlantic/Canary"},
     	new String[]{"posix/Atlantic/Cape_Verde", "Atlantic/Cape_Verde"},
     	new String[]{"posix/Atlantic/Faeroe", "Atlantic/Faeroe"},
     	new String[]{"posix/Atlantic/Jan_Mayen", "Atlantic/Jan_Mayen"},
     	new String[]{"posix/Atlantic/Madeira", "Europe/London"},
     	new String[]{"posix/Atlantic/Reykjavik", "Atlantic/Reykjavik"},
     	new String[]{"posix/Atlantic/South_Georgia", "Atlantic/South_Georgia"},
     	new String[]{"posix/Atlantic/St_Helena", "Atlantic/St_Helena"},
     	new String[]{"posix/Atlantic/Stanley", "Atlantic/Stanley"},
     	new String[]{"posix/Australia/ACT", "Australia/Sydney"},
     	new String[]{"posix/Australia/Adelaide", "Australia/Adelaide"},
     	new String[]{"posix/Australia/Brisbane", "Australia/Brisbane"},
     	new String[]{"posix/Australia/Broken_Hill", "Australia/Broken_Hill"},
     	new String[]{"posix/Australia/Canberra", "Australia/Sydney"},
     	new String[]{"posix/Australia/Darwin", "Australia/Darwin"},
     	new String[]{"posix/Australia/Hobart", "Australia/Hobart"},
     	new String[]{"posix/Australia/LHI", "Australia/Lord_Howe"},
     	new String[]{"posix/Australia/Lord_Howe", "Australia/Lord_Howe"},
     	new String[]{"posix/Australia/Melbourne", "Australia/Sydney"},
     	new String[]{"posix/Australia/NSW", "Australia/Sydney"},
     	new String[]{"posix/Australia/North", "Australia/Darwin"},
     	new String[]{"posix/Australia/Perth", "Australia/Perth"},
     	new String[]{"posix/Australia/Queensland", "Australia/Brisbane"},
     	new String[]{"posix/Australia/South", "Australia/Adelaide"},
     	new String[]{"posix/Australia/Sydney", "Australia/Sydney"},
     	new String[]{"posix/Australia/Tasmania", "Australia/Hobart"},
     	new String[]{"posix/Australia/Victoria", "Australia/Sydney"},
     	new String[]{"posix/Australia/West", "Australia/Perth"},
     	new String[]{"posix/Australia/Yancowinna", "Australia/Broken_Hill"},
     	new String[]{"posix/Brazil/Acre", "America/Rio_Branco"},
     	new String[]{"posix/Brazil/DeNoronha", "America/Noronha"},
     	new String[]{"posix/Brazil/East", "America/Sao_Paulo"},
     	new String[]{"posix/Brazil/West", "America/Manaus"},
     	new String[]{"posix/CET", "Europe/Paris"},
     	new String[]{"posix/CST6CDT", "America/Chicago"},
     	new String[]{"posix/Canada/Atlantic", "America/Halifax"},
     	new String[]{"posix/Canada/Central", "America/Winnipeg"},
     	new String[]{"posix/Canada/East-Saskatchewan", "America/Regina"},
     	new String[]{"posix/Canada/Eastern", "America/Montreal"},
     	new String[]{"posix/Canada/Mountain", "America/Edmonton"},
     	new String[]{"posix/Canada/Newfoundland", "America/St_Johns"},
     	new String[]{"posix/Canada/Pacific", "America/Vancouver"},
     	new String[]{"posix/Canada/Saskatchewan", "America/Regina"},
     	new String[]{"posix/Canada/Yukon", "America/Los_Angeles"},
     	new String[]{"posix/Chile/Continental", "America/Santiago"},
     	new String[]{"posix/Chile/EasterIsland", "Pacific/Easter"},
     	new String[]{"posix/Cuba", "America/Havana"},
     	new String[]{"posix/EET", "Europe/Istanbul"},
     	new String[]{"posix/EST", "America/Indianapolis"},
     	new String[]{"posix/EST5EDT", "America/New_York"},
     	new String[]{"posix/Egypt", "Africa/Cairo"},
     	new String[]{"posix/Eire", "Europe/Dublin"},
     	new String[]{"posix/Etc/GMT", "GMT"},
     	new String[]{"posix/Etc/GMT0", "GMT"},
     	new String[]{"posix/Etc/Greenwich", "GMT"},
     	new String[]{"posix/Etc/UCT", "UTC"},
     	new String[]{"posix/Etc/UTC", "UTC"},
     	new String[]{"posix/Etc/Universal", "UTC"},
     	new String[]{"posix/Etc/Zulu", "UTC"},
     	new String[]{"posix/Europe/Amsterdam", "Europe/Amsterdam"},
     	new String[]{"posix/Europe/Andorra", "Europe/Andorra"},
     	new String[]{"posix/Europe/Athens", "Europe/Athens"},
     	new String[]{"posix/Europe/Belfast", "Europe/London"},
     	new String[]{"posix/Europe/Belgrade", "Europe/Belgrade"},
     	new String[]{"posix/Europe/Berlin", "Europe/Berlin"},
     	new String[]{"posix/Europe/Bratislava", "Europe/Prague"},
     	new String[]{"posix/Europe/Brussels", "Europe/Brussels"},
     	new String[]{"posix/Europe/Bucharest", "Europe/Bucharest"},
     	new String[]{"posix/Europe/Budapest", "Europe/Budapest"},
     	new String[]{"posix/Europe/Chisinau", "Europe/Chisinau"},
     	new String[]{"posix/Europe/Copenhagen", "Europe/Copenhagen"},
     	new String[]{"posix/Europe/Dublin", "Europe/Dublin"},
     	new String[]{"posix/Europe/Gibraltar", "Europe/Gibraltar"},
     	new String[]{"posix/Europe/Helsinki", "Europe/Helsinki"},
     	new String[]{"posix/Europe/Istanbul", "Europe/Istanbul"},
     	new String[]{"posix/Europe/Kaliningrad", "Europe/Kaliningrad"},
     	new String[]{"posix/Europe/Kiev", "Europe/Kiev"},
     	new String[]{"posix/Europe/Lisbon", "Europe/Lisbon"},
     	new String[]{"posix/Europe/Ljubljana", "Europe/Belgrade"},
     	new String[]{"posix/Europe/London", "Europe/London"},
     	new String[]{"posix/Europe/Luxembourg", "Europe/Luxembourg"},
     	new String[]{"posix/Europe/Madrid", "Europe/Madrid"},
     	new String[]{"posix/Europe/Malta", "Europe/Malta"},
     	new String[]{"posix/Europe/Minsk", "Europe/Minsk"},
     	new String[]{"posix/Europe/Monaco", "Europe/Monaco"},
     	new String[]{"posix/Europe/Moscow", "Europe/Moscow"},
     	new String[]{"posix/Europe/Oslo", "Europe/Oslo"},
     	new String[]{"posix/Europe/Paris", "Europe/Paris"},
     	new String[]{"posix/Europe/Prague", "Europe/Prague"},
     	new String[]{"posix/Europe/Riga", "Europe/Riga"},
     	new String[]{"posix/Europe/Rome", "Europe/Rome"},
     	new String[]{"posix/Europe/Samara", "Europe/Samara"},
     	new String[]{"posix/Europe/San_Marino", "Europe/Rome"},
     	new String[]{"posix/Europe/Sarajevo", "Europe/Belgrade"},
     	new String[]{"posix/Europe/Simferopol", "Europe/Simferopol"},
     	new String[]{"posix/Europe/Skopje", "Europe/Belgrade"},
     	new String[]{"posix/Europe/Sofia", "Europe/Sofia"},
     	new String[]{"posix/Europe/Stockholm", "Europe/Stockholm"},
     	new String[]{"posix/Europe/Tallinn", "Europe/Tallinn"},
     	new String[]{"posix/Europe/Tirane", "Europe/Tirane"},
     	new String[]{"posix/Europe/Vaduz", "Europe/Vaduz"},
     	new String[]{"posix/Europe/Vatican", "Europe/Rome"},
     	new String[]{"posix/Europe/Vienna", "Europe/Vienna"},
     	new String[]{"posix/Europe/Vilnius", "Europe/Vilnius"},
     	new String[]{"posix/Europe/Warsaw", "Europe/Warsaw"},
     	new String[]{"posix/Europe/Zagreb", "Europe/Belgrade"},
     	new String[]{"posix/Europe/Zurich", "Europe/Zurich"},
     	new String[]{"posix/Factory", "GMT"},
     	new String[]{"posix/GB", "Europe/London"},
     	new String[]{"posix/GMT", "GMT"},
     	new String[]{"posix/GMT0", "GMT"},
     	new String[]{"posix/Greenwich", "GMT"},
     	new String[]{"posix/HST", "Pacific/Honolulu"},
     	new String[]{"posix/Hongkong", "Asia/Hong_Kong"},
     	new String[]{"posix/Iceland", "Atlantic/Reykjavik"},
     	new String[]{"posix/Indian/Antananarivo", "Indian/Antananarivo"},
     	new String[]{"posix/Indian/Chagos", "Indian/Chagos"},
     	new String[]{"posix/Indian/Christmas", "Indian/Christmas"},
     	new String[]{"posix/Indian/Cocos", "Indian/Cocos"},
     	new String[]{"posix/Indian/Comoro", "Indian/Comoro"},
     	new String[]{"posix/Indian/Kerguelen", "Indian/Kerguelen"},
     	new String[]{"posix/Indian/Mahe", "Indian/Mahe"},
     	new String[]{"posix/Indian/Maldives", "Indian/Maldives"},
     	new String[]{"posix/Indian/Mauritius", "Indian/Mauritius"},
     	new String[]{"posix/Indian/Mayotte", "Indian/Mayotte"},
     	new String[]{"posix/Indian/Reunion", "Indian/Reunion"},
     	new String[]{"posix/Iran", "Asia/Tehran"},
     	new String[]{"posix/Israel", "Asia/Jerusalem"},
     	new String[]{"posix/Jamaica", "America/Jamaica"},
     	new String[]{"posix/Japan", "Asia/Tokyo"},
     	new String[]{"posix/Libya", "Africa/Tripoli"},
     	new String[]{"posix/MET", "Europe/Paris"},
     	new String[]{"posix/MST", "America/Phoenix"},
     	new String[]{"posix/MST7MDT", "America/Denver"},
     	new String[]{"posix/Mexico/BajaNorte", "America/Tijuana"},
     	new String[]{"posix/Mexico/BajaSur", "America/Mazatlan"},
     	new String[]{"posix/Mexico/General", "America/Mexico_City"},
     	new String[]{"posix/NZ", "Pacific/Auckland"},
     	new String[]{"posix/NZ-CHAT", "Pacific/Chatham"},
     	new String[]{"posix/Navajo", "America/Denver"},
     	new String[]{"posix/PRC", "Asia/Shanghai"},
     	new String[]{"posix/PST8PDT", "America/Los_Angeles"},
     	new String[]{"posix/Pacific/Apia", "Pacific/Apia"},
     	new String[]{"posix/Pacific/Auckland", "Pacific/Auckland"},
     	new String[]{"posix/Pacific/Chatham", "Pacific/Chatham"},
     	new String[]{"posix/Pacific/Easter", "Pacific/Easter"},
     	new String[]{"posix/Pacific/Efate", "Pacific/Efate"},
     	new String[]{"posix/Pacific/Enderbury", "Pacific/Enderbury"},
     	new String[]{"posix/Pacific/Fakaofo", "Pacific/Fakaofo"},
     	new String[]{"posix/Pacific/Fiji", "Pacific/Fiji"},
     	new String[]{"posix/Pacific/Funafuti", "Pacific/Funafuti"},
     	new String[]{"posix/Pacific/Galapagos", "Pacific/Galapagos"},
     	new String[]{"posix/Pacific/Gambier", "Pacific/Gambier"},
     	new String[]{"posix/Pacific/Guadalcanal", "Pacific/Guadalcanal"},
     	new String[]{"posix/Pacific/Guam", "Pacific/Guam"},
     	new String[]{"posix/Pacific/Honolulu", "Pacific/Honolulu"},
     	new String[]{"posix/Pacific/Kiritimati", "Pacific/Kiritimati"},
     	new String[]{"posix/Pacific/Kosrae", "Pacific/Kosrae"},
     	new String[]{"posix/Pacific/Majuro", "Pacific/Majuro"},
     	new String[]{"posix/Pacific/Marquesas", "Pacific/Marquesas"},
     	new String[]{"posix/Pacific/Nauru", "Pacific/Nauru"},
     	new String[]{"posix/Pacific/Niue", "Pacific/Niue"},
     	new String[]{"posix/Pacific/Norfolk", "Pacific/Norfolk"},
     	new String[]{"posix/Pacific/Noumea", "Pacific/Noumea"},
     	new String[]{"posix/Pacific/Pago_Pago", "Pacific/Pago_Pago"},
     	new String[]{"posix/Pacific/Palau", "Pacific/Palau"},
     	new String[]{"posix/Pacific/Pitcairn", "Pacific/Pitcairn"},
     	new String[]{"posix/Pacific/Ponape", "Pacific/Ponape"},
     	new String[]{"posix/Pacific/Port_Moresby", "Pacific/Port_Moresby"},
     	new String[]{"posix/Pacific/Rarotonga", "Pacific/Rarotonga"},
     	new String[]{"posix/Pacific/Saipan", "Pacific/Saipan"},
     	new String[]{"posix/Pacific/Samoa", "Pacific/Pago_Pago"},
     	new String[]{"posix/Pacific/Tahiti", "Pacific/Tahiti"},
     	new String[]{"posix/Pacific/Tarawa", "Pacific/Tarawa"},
     	new String[]{"posix/Pacific/Tongatapu", "Pacific/Tongatapu"},
     	new String[]{"posix/Pacific/Truk", "Pacific/Truk"},
     	new String[]{"posix/Pacific/Wake", "Pacific/Wake"},
     	new String[]{"posix/Pacific/Wallis", "Pacific/Wallis"},
     	new String[]{"posix/Poland", "Europe/Warsaw"},
     	new String[]{"posix/Portugal", "Europe/Lisbon"},
     	new String[]{"posix/ROC", "Asia/Taipei"},
     	new String[]{"posix/ROK", "Asia/Seoul"},
     	new String[]{"posix/Singapore", "Asia/Singapore"},
     	new String[]{"posix/SystemV/AST4ADT", "America/Thule"},
     	new String[]{"posix/SystemV/CST6CDT", "America/Chicago"},
     	new String[]{"posix/SystemV/EST5EDT", "America/New_York"},
     	new String[]{"posix/SystemV/MST7MDT", "America/Denver"},
     	new String[]{"posix/SystemV/PST8PDT", "America/Los_Angeles"},
     	new String[]{"posix/SystemV/YST9YDT", "America/Anchorage"},
     	new String[]{"posix/Turkey", "Europe/Istanbul"},
     	new String[]{"posix/UCT", "UTC"},
     	new String[]{"posix/US/Alaska", "America/Anchorage"},
     	new String[]{"posix/US/Aleutian", "America/Adak"},
     	new String[]{"posix/US/Arizona", "America/Phoenix"},
     	new String[]{"posix/US/Central", "America/Chicago"},
     	new String[]{"posix/US/East-Indiana", "America/Indianapolis"},
     	new String[]{"posix/US/Eastern", "America/New_York"},
     	new String[]{"posix/US/Hawaii", "Pacific/Honolulu"},
     	new String[]{"posix/US/Michigan", "America/New_York"},
     	new String[]{"posix/US/Mountain", "America/Denver"},
     	new String[]{"posix/US/Pacific", "America/Los_Angeles"},
     	new String[]{"posix/US/Samoa", "Pacific/Pago_Pago"},
     	new String[]{"posix/UTC", "UTC"},
     	new String[]{"posix/Universal", "UTC"},
     	new String[]{"posix/W-SU", "Europe/Moscow"},
     	new String[]{"posix/WET", "WET"},
     	new String[]{"posix/Zulu", "UTC"},
     	new String[]{"right/Africa/Abidjan", "Africa/Abidjan"},
     	new String[]{"right/Africa/Accra", "Africa/Accra"},
     	new String[]{"right/Africa/Addis_Ababa", "Africa/Addis_Ababa"},
     	new String[]{"right/Africa/Algiers", "Africa/Algiers"},
     	new String[]{"right/Africa/Asmera", "Africa/Asmera"},
     	new String[]{"right/Africa/Bamako", "GMT"},
     	new String[]{"right/Africa/Bangui", "Africa/Bangui"},
     	new String[]{"right/Africa/Banjul", "Africa/Banjul"},
     	new String[]{"right/Africa/Bissau", "Africa/Bissau"},
     	new String[]{"right/Africa/Blantyre", "Africa/Blantyre"},
     	new String[]{"right/Africa/Brazzaville", "Africa/Luanda"},
     	new String[]{"right/Africa/Bujumbura", "Africa/Bujumbura"},
     	new String[]{"right/Africa/Cairo", "Africa/Cairo"},
     	new String[]{"right/Africa/Casablanca", "Africa/Casablanca"},
     	new String[]{"right/Africa/Ceuta", "Europe/Paris"},
     	new String[]{"right/Africa/Conakry", "Africa/Conakry"},
     	new String[]{"right/Africa/Dakar", "Africa/Dakar"},
     	new String[]{"right/Africa/Dar_es_Salaam", "Africa/Dar_es_Salaam"},
     	new String[]{"right/Africa/Djibouti", "Africa/Djibouti"},
     	new String[]{"right/Africa/Douala", "Africa/Douala"},
     	new String[]{"right/Africa/El_Aaiun", "Africa/Casablanca"},
     	new String[]{"right/Africa/Freetown", "Africa/Freetown"},
     	new String[]{"right/Africa/Gaborone", "Africa/Gaborone"},
     	new String[]{"right/Africa/Harare", "Africa/Harare"},
     	new String[]{"right/Africa/Johannesburg", "Africa/Johannesburg"},
     	new String[]{"right/Africa/Kampala", "Africa/Kampala"},
     	new String[]{"right/Africa/Khartoum", "Africa/Khartoum"},
     	new String[]{"right/Africa/Kigali", "Africa/Kigali"},
     	new String[]{"right/Africa/Kinshasa", "Africa/Kinshasa"},
     	new String[]{"right/Africa/Lagos", "Africa/Lagos"},
     	new String[]{"right/Africa/Libreville", "Africa/Libreville"},
     	new String[]{"right/Africa/Lome", "Africa/Lome"},
     	new String[]{"right/Africa/Luanda", "Africa/Luanda"},
     	new String[]{"right/Africa/Lubumbashi", "Africa/Lubumbashi"},
     	new String[]{"right/Africa/Lusaka", "Africa/Lusaka"},
     	new String[]{"right/Africa/Malabo", "Africa/Malabo"},
     	new String[]{"right/Africa/Maputo", "Africa/Maputo"},
     	new String[]{"right/Africa/Maseru", "Africa/Maseru"},
     	new String[]{"right/Africa/Mbabane", "Africa/Mbabane"},
     	new String[]{"right/Africa/Mogadishu", "Africa/Mogadishu"},
     	new String[]{"right/Africa/Monrovia", "Africa/Monrovia"},
     	new String[]{"right/Africa/Nairobi", "Africa/Nairobi"},
     	new String[]{"right/Africa/Ndjamena", "Africa/Ndjamena"},
     	new String[]{"right/Africa/Niamey", "Africa/Niamey"},
     	new String[]{"right/Africa/Nouakchott", "Africa/Nouakchott"},
     	new String[]{"right/Africa/Ouagadougou", "Africa/Ouagadougou"},
     	new String[]{"right/Africa/Porto-Novo", "Africa/Porto-Novo"},
     	new String[]{"right/Africa/Sao_Tome", "Africa/Sao_Tome"},
     	new String[]{"right/Africa/Timbuktu", "Africa/Timbuktu"},
     	new String[]{"right/Africa/Tripoli", "Africa/Tripoli"},
     	new String[]{"right/Africa/Tunis", "Africa/Tunis"},
     	new String[]{"right/Africa/Windhoek", "Africa/Windhoek"},
     	new String[]{"right/America/Adak", "America/Adak"},
     	new String[]{"right/America/Anchorage", "America/Anchorage"},
     	new String[]{"right/America/Anguilla", "America/Anguilla"},
     	new String[]{"right/America/Antigua", "America/Antigua"},
     	new String[]{"right/America/Araguaina", "America/Sao_Paulo"},
     	new String[]{"right/America/Aruba", "America/Aruba"},
     	new String[]{"right/America/Asuncion", "America/Asuncion"},
     	new String[]{"right/America/Atka", "America/Adak"},
     	new String[]{"right/America/Barbados", "America/Barbados"},
     	new String[]{"right/America/Belize", "America/Belize"},
     	new String[]{"right/America/Bogota", "America/Bogota"},
     	new String[]{"right/America/Boise", "America/Denver"},
     	new String[]{"right/America/Buenos_Aires", "America/Buenos_Aires"},
     	new String[]{"right/America/Cancun", "America/Chicago"},
     	new String[]{"right/America/Caracas", "America/Caracas"},
     	new String[]{"right/America/Cayenne", "America/Cayenne"},
     	new String[]{"right/America/Cayman", "America/Cayman"},
     	new String[]{"right/America/Chicago", "America/Chicago"},
     	new String[]{"right/America/Chihuahua", "America/Denver"},
     	new String[]{"right/America/Costa_Rica", "America/Costa_Rica"},
     	new String[]{"right/America/Cuiaba", "America/Cuiaba"},
     	new String[]{"right/America/Curacao", "America/Curacao"},
     	new String[]{"right/America/Dawson", "America/Los_Angeles"},
     	new String[]{"right/America/Dawson_Creek", "America/Dawson_Creek"},
     	new String[]{"right/America/Denver", "America/Denver"},
     	new String[]{"right/America/Detroit", "America/New_York"},
     	new String[]{"right/America/Dominica", "America/Dominica"},
     	new String[]{"right/America/Edmonton", "America/Edmonton"},
     	new String[]{"right/America/El_Salvador", "America/El_Salvador"},
     	new String[]{"right/America/Ensenada", "America/Tijuana"},
     	new String[]{"right/America/Fort_Wayne", "America/Indianapolis"},
     	new String[]{"right/America/Fortaleza", "America/Fortaleza"},
     	new String[]{"right/America/Glace_Bay", "America/Halifax"},
     	new String[]{"right/America/Godthab", "America/Godthab"},
     	new String[]{"right/America/Goose_Bay", "America/Thule"},
     	new String[]{"right/America/Grand_Turk", "America/Grand_Turk"},
     	new String[]{"right/America/Grenada", "America/Grenada"},
     	new String[]{"right/America/Guadeloupe", "America/Guadeloupe"},
     	new String[]{"right/America/Guatemala", "America/Guatemala"},
     	new String[]{"right/America/Guayaquil", "America/Guayaquil"},
     	new String[]{"right/America/Guyana", "America/Guyana"},
     	new String[]{"right/America/Halifax", "America/Halifax"},
     	new String[]{"right/America/Havana", "America/Havana"},
     	new String[]{"right/America/Indiana/Indianapolis", "America/Indianapolis"},
     	new String[]{"right/America/Indianapolis", "America/Indianapolis"},
     	new String[]{"right/America/Inuvik", "America/Denver"},
     	new String[]{"right/America/Iqaluit", "America/New_York"},
     	new String[]{"right/America/Jamaica", "America/Jamaica"},
     	new String[]{"right/America/Juneau", "America/Anchorage"},
     	new String[]{"right/America/La_Paz", "America/La_Paz"},
     	new String[]{"right/America/Lima", "America/Lima"},
     	new String[]{"right/America/Los_Angeles", "America/Los_Angeles"},
     	new String[]{"right/America/Louisville", "America/New_York"},
     	new String[]{"right/America/Managua", "America/Managua"},
     	new String[]{"right/America/Manaus", "America/Manaus"},
     	new String[]{"right/America/Martinique", "America/Martinique"},
     	new String[]{"right/America/Mazatlan", "America/Mazatlan"},
     	new String[]{"right/America/Menominee", "America/Winnipeg"},
     	new String[]{"right/America/Mexico_City", "America/Mexico_City"},
     	new String[]{"right/America/Miquelon", "America/Miquelon"},
     	new String[]{"right/America/Montevideo", "America/Montevideo"},
     	new String[]{"right/America/Montreal", "America/Montreal"},
     	new String[]{"right/America/Montserrat", "America/Montserrat"},
     	new String[]{"right/America/Nassau", "America/Nassau"},
     	new String[]{"right/America/New_York", "America/New_York"},
     	new String[]{"right/America/Nipigon", "America/New_York"},
     	new String[]{"right/America/Nome", "America/Anchorage"},
     	new String[]{"right/America/Noronha", "America/Noronha"},
     	new String[]{"right/America/Panama", "America/Panama"},
     	new String[]{"right/America/Pangnirtung", "America/Thule"},
     	new String[]{"right/America/Paramaribo", "America/Paramaribo"},
     	new String[]{"right/America/Phoenix", "America/Phoenix"},
     	new String[]{"right/America/Port-au-Prince", "America/Port-au-Prince"},
     	new String[]{"right/America/Port_of_Spain", "America/Port_of_Spain"},
     	new String[]{"right/America/Porto_Acre", "America/Porto_Acre"},
     	new String[]{"right/America/Puerto_Rico", "America/Puerto_Rico"},
     	new String[]{"right/America/Rainy_River", "America/Chicago"},
     	new String[]{"right/America/Rankin_Inlet", "America/Chicago"},
     	new String[]{"right/America/Regina", "America/Regina"},
     	new String[]{"right/America/Rio_Branco", "America/Rio_Branco"},
     	new String[]{"right/America/Santiago", "America/Santiago"},
     	new String[]{"right/America/Santo_Domingo", "America/Santo_Domingo"},
     	new String[]{"right/America/Sao_Paulo", "America/Sao_Paulo"},
     	new String[]{"right/America/Scoresbysund", "America/Scoresbysund"},
     	new String[]{"right/America/Shiprock", "America/Denver"},
     	new String[]{"right/America/St_Johns", "America/St_Johns"},
     	new String[]{"right/America/St_Kitts", "America/St_Kitts"},
     	new String[]{"right/America/St_Lucia", "America/St_Lucia"},
     	new String[]{"right/America/St_Thomas", "America/St_Thomas"},
     	new String[]{"right/America/St_Vincent", "America/St_Vincent"},
     	new String[]{"right/America/Tegucigalpa", "America/Tegucigalpa"},
     	new String[]{"right/America/Thule", "America/Thule"},
     	new String[]{"right/America/Thunder_Bay", "America/New_York"},
     	new String[]{"right/America/Tijuana", "America/Tijuana"},
     	new String[]{"right/America/Tortola", "America/Tortola"},
     	new String[]{"right/America/Vancouver", "America/Vancouver"},
     	new String[]{"right/America/Virgin", "America/St_Thomas"},
     	new String[]{"right/America/Whitehorse", "America/Los_Angeles"},
     	new String[]{"right/America/Winnipeg", "America/Winnipeg"},
     	new String[]{"right/America/Yakutat", "America/Anchorage"},
     	new String[]{"right/America/Yellowknife", "America/Denver"},
     	new String[]{"right/Antarctica/Casey", "Antarctica/Casey"},
     	new String[]{"right/Antarctica/DumontDUrville", "Antarctica/DumontDUrville"},
     	new String[]{"right/Antarctica/Mawson", "Antarctica/Mawson"},
     	new String[]{"right/Antarctica/McMurdo", "Antarctica/McMurdo"},
     	new String[]{"right/Antarctica/Palmer", "Antarctica/Palmer"},
     	new String[]{"right/Antarctica/South_Pole", "Antarctica/McMurdo"},
     	new String[]{"right/Arctic/Longyearbyen", "Europe/Oslo"},
     	new String[]{"right/Asia/Aden", "Asia/Aden"},
     	new String[]{"right/Asia/Almaty", "Asia/Almaty"},
     	new String[]{"right/Asia/Amman", "Asia/Amman"},
     	new String[]{"right/Asia/Anadyr", "Asia/Anadyr"},
     	new String[]{"right/Asia/Aqtau", "Asia/Aqtau"},
     	new String[]{"right/Asia/Aqtobe", "Asia/Aqtobe"},
     	new String[]{"right/Asia/Ashkhabad", "Asia/Ashkhabad"},
     	new String[]{"right/Asia/Baghdad", "Asia/Baghdad"},
     	new String[]{"right/Asia/Bahrain", "Asia/Bahrain"},
     	new String[]{"right/Asia/Baku", "Asia/Baku"},
     	new String[]{"right/Asia/Bangkok", "Asia/Bangkok"},
     	new String[]{"right/Asia/Beirut", "Asia/Beirut"},
     	new String[]{"right/Asia/Bishkek", "Asia/Bishkek"},
     	new String[]{"right/Asia/Brunei", "Asia/Brunei"},
     	new String[]{"right/Asia/Calcutta", "Asia/Calcutta"},
     	new String[]{"right/Asia/Chungking", "Asia/Shanghai"},
     	new String[]{"right/Asia/Colombo", "Asia/Colombo"},
     	new String[]{"right/Asia/Dacca", "Asia/Dacca"},
     	new String[]{"right/Asia/Damascus", "Asia/Damascus"},
     	new String[]{"right/Asia/Dubai", "Asia/Dubai"},
     	new String[]{"right/Asia/Dushanbe", "Asia/Dushanbe"},
     	new String[]{"right/Asia/Gaza", "Asia/Amman"},
     	new String[]{"right/Asia/Harbin", "Asia/Shanghai"},
     	new String[]{"right/Asia/Hong_Kong", "Asia/Hong_Kong"},
     	new String[]{"right/Asia/Irkutsk", "Asia/Irkutsk"},
     	new String[]{"right/Asia/Istanbul", "Europe/Istanbul"},
     	new String[]{"right/Asia/Jakarta", "Asia/Jakarta"},
     	new String[]{"right/Asia/Jayapura", "Asia/Jayapura"},
     	new String[]{"right/Asia/Jerusalem", "Asia/Jerusalem"},
     	new String[]{"right/Asia/Kabul", "Asia/Kabul"},
     	new String[]{"right/Asia/Kamchatka", "Asia/Kamchatka"},
     	new String[]{"right/Asia/Karachi", "Asia/Karachi"},
     	new String[]{"right/Asia/Kashgar", "Asia/Shanghai"},
     	new String[]{"right/Asia/Katmandu", "Asia/Katmandu"},
     	new String[]{"right/Asia/Krasnoyarsk", "Asia/Krasnoyarsk"},
     	new String[]{"right/Asia/Kuala_Lumpur", "Asia/Kuala_Lumpur"},
     	new String[]{"right/Asia/Kuwait", "Asia/Kuwait"},
     	new String[]{"right/Asia/Macao", "Asia/Macao"},
     	new String[]{"right/Asia/Magadan", "Asia/Magadan"},
     	new String[]{"right/Asia/Manila", "Asia/Manila"},
     	new String[]{"right/Asia/Muscat", "Asia/Muscat"},
     	new String[]{"right/Asia/Nicosia", "Asia/Nicosia"},
     	new String[]{"right/Asia/Novosibirsk", "Asia/Novosibirsk"},
     	new String[]{"right/Asia/Omsk", "Asia/Novosibirsk"},
     	new String[]{"right/Asia/Phnom_Penh", "Asia/Phnom_Penh"},
     	new String[]{"right/Asia/Pyongyang", "Asia/Pyongyang"},
     	new String[]{"right/Asia/Qatar", "Asia/Qatar"},
     	new String[]{"right/Asia/Rangoon", "Asia/Rangoon"},
     	new String[]{"right/Asia/Riyadh", "Asia/Riyadh"},
     	new String[]{"right/Asia/Saigon", "Asia/Saigon"},
     	new String[]{"right/Asia/Seoul", "Asia/Seoul"},
     	new String[]{"right/Asia/Shanghai", "Asia/Shanghai"},
     	new String[]{"right/Asia/Singapore", "Asia/Singapore"},
     	new String[]{"right/Asia/Taipei", "Asia/Taipei"},
     	new String[]{"right/Asia/Tashkent", "Asia/Tashkent"},
     	new String[]{"right/Asia/Tbilisi", "Asia/Tbilisi"},
     	new String[]{"right/Asia/Tehran", "Asia/Tehran"},
     	new String[]{"right/Asia/Tel_Aviv", "Asia/Jerusalem"},
     	new String[]{"right/Asia/Thimbu", "Asia/Thimbu"},
     	new String[]{"right/Asia/Tokyo", "Asia/Tokyo"},
     	new String[]{"right/Asia/Ujung_Pandang", "Asia/Ujung_Pandang"},
     	new String[]{"right/Asia/Ulan_Bator", "Asia/Ulaanbaatar"},
     	new String[]{"right/Asia/Urumqi", "Asia/Shanghai"},
     	new String[]{"right/Asia/Vientiane", "Asia/Vientiane"},
     	new String[]{"right/Asia/Vladivostok", "Asia/Vladivostok"},
     	new String[]{"right/Asia/Yakutsk", "Asia/Yakutsk"},
     	new String[]{"right/Asia/Yekaterinburg", "Asia/Yekaterinburg"},
     	new String[]{"right/Asia/Yerevan", "Asia/Yerevan"},
     	new String[]{"right/Atlantic/Azores", "Atlantic/Azores"},
     	new String[]{"right/Atlantic/Bermuda", "Atlantic/Bermuda"},
     	new String[]{"right/Atlantic/Canary", "Atlantic/Canary"},
     	new String[]{"right/Atlantic/Cape_Verde", "Atlantic/Cape_Verde"},
     	new String[]{"right/Atlantic/Faeroe", "Atlantic/Faeroe"},
     	new String[]{"right/Atlantic/Jan_Mayen", "Atlantic/Jan_Mayen"},
     	new String[]{"right/Atlantic/Madeira", "Europe/London"},
     	new String[]{"right/Atlantic/Reykjavik", "Atlantic/Reykjavik"},
     	new String[]{"right/Atlantic/South_Georgia", "Atlantic/South_Georgia"},
     	new String[]{"right/Atlantic/St_Helena", "Atlantic/St_Helena"},
     	new String[]{"right/Atlantic/Stanley", "Atlantic/Stanley"},
     	new String[]{"right/Australia/ACT", "Australia/Sydney"},
     	new String[]{"right/Australia/Adelaide", "Australia/Adelaide"},
     	new String[]{"right/Australia/Brisbane", "Australia/Brisbane"},
     	new String[]{"right/Australia/Broken_Hill", "Australia/Broken_Hill"},
     	new String[]{"right/Australia/Canberra", "Australia/Sydney"},
     	new String[]{"right/Australia/Darwin", "Australia/Darwin"},
     	new String[]{"right/Australia/Hobart", "Australia/Hobart"},
     	new String[]{"right/Australia/LHI", "Australia/Lord_Howe"},
     	new String[]{"right/Australia/Lord_Howe", "Australia/Lord_Howe"},
     	new String[]{"right/Australia/Melbourne", "Australia/Sydney"},
     	new String[]{"right/Australia/NSW", "Australia/Sydney"},
     	new String[]{"right/Australia/North", "Australia/Darwin"},
     	new String[]{"right/Australia/Perth", "Australia/Perth"},
     	new String[]{"right/Australia/Queensland", "Australia/Brisbane"},
     	new String[]{"right/Australia/South", "Australia/Adelaide"},
     	new String[]{"right/Australia/Sydney", "Australia/Sydney"},
     	new String[]{"right/Australia/Tasmania", "Australia/Hobart"},
     	new String[]{"right/Australia/Victoria", "Australia/Sydney"},
     	new String[]{"right/Australia/West", "Australia/Perth"},
     	new String[]{"right/Australia/Yancowinna", "Australia/Broken_Hill"},
     	new String[]{"right/Brazil/Acre", "America/Rio_Branco"},
     	new String[]{"right/Brazil/DeNoronha", "America/Noronha"},
     	new String[]{"right/Brazil/East", "America/Sao_Paulo"},
     	new String[]{"right/Brazil/West", "America/Manaus"},
     	new String[]{"right/CET", "Europe/Paris"},
     	new String[]{"right/CST6CDT", "America/Chicago"},
     	new String[]{"right/Canada/Atlantic", "America/Halifax"},
     	new String[]{"right/Canada/Central", "America/Winnipeg"},
     	new String[]{"right/Canada/East-Saskatchewan", "America/Regina"},
     	new String[]{"right/Canada/Eastern", "America/Montreal"},
     	new String[]{"right/Canada/Mountain", "America/Edmonton"},
     	new String[]{"right/Canada/Newfoundland", "America/St_Johns"},
     	new String[]{"right/Canada/Pacific", "America/Vancouver"},
     	new String[]{"right/Canada/Saskatchewan", "America/Regina"},
     	new String[]{"right/Canada/Yukon", "America/Los_Angeles"},
     	new String[]{"right/Chile/Continental", "America/Santiago"},
     	new String[]{"right/Chile/EasterIsland", "Pacific/Easter"},
     	new String[]{"right/Cuba", "America/Havana"},
     	new String[]{"right/EET", "Europe/Istanbul"},
     	new String[]{"right/EST", "America/Indianapolis"},
     	new String[]{"right/EST5EDT", "America/New_York"},
     	new String[]{"right/Egypt", "Africa/Cairo"},
     	new String[]{"right/Eire", "Europe/Dublin"},
     	new String[]{"right/Etc/GMT", "GMT"},
     	new String[]{"right/Etc/GMT0", "GMT"},
     	new String[]{"right/Etc/Greenwich", "GMT"},
     	new String[]{"right/Etc/UCT", "UTC"},
     	new String[]{"right/Etc/UTC", "UTC"},
     	new String[]{"right/Etc/Universal", "UTC"},
     	new String[]{"right/Etc/Zulu", "UTC"},
     	new String[]{"right/Europe/Amsterdam", "Europe/Amsterdam"},
     	new String[]{"right/Europe/Andorra", "Europe/Andorra"},
     	new String[]{"right/Europe/Athens", "Europe/Athens"},
     	new String[]{"right/Europe/Belfast", "Europe/London"},
     	new String[]{"right/Europe/Belgrade", "Europe/Belgrade"},
     	new String[]{"right/Europe/Berlin", "Europe/Berlin"},
     	new String[]{"right/Europe/Bratislava", "Europe/Prague"},
     	new String[]{"right/Europe/Brussels", "Europe/Brussels"},
     	new String[]{"right/Europe/Bucharest", "Europe/Bucharest"},
     	new String[]{"right/Europe/Budapest", "Europe/Budapest"},
     	new String[]{"right/Europe/Chisinau", "Europe/Chisinau"},
     	new String[]{"right/Europe/Copenhagen", "Europe/Copenhagen"},
     	new String[]{"right/Europe/Dublin", "Europe/Dublin"},
     	new String[]{"right/Europe/Gibraltar", "Europe/Gibraltar"},
     	new String[]{"right/Europe/Helsinki", "Europe/Helsinki"},
     	new String[]{"right/Europe/Istanbul", "Europe/Istanbul"},
     	new String[]{"right/Europe/Kaliningrad", "Europe/Kaliningrad"},
     	new String[]{"right/Europe/Kiev", "Europe/Kiev"},
     	new String[]{"right/Europe/Lisbon", "Europe/Lisbon"},
     	new String[]{"right/Europe/Ljubljana", "Europe/Belgrade"},
     	new String[]{"right/Europe/London", "Europe/London"},
     	new String[]{"right/Europe/Luxembourg", "Europe/Luxembourg"},
     	new String[]{"right/Europe/Madrid", "Europe/Madrid"},
     	new String[]{"right/Europe/Malta", "Europe/Malta"},
     	new String[]{"right/Europe/Minsk", "Europe/Minsk"},
     	new String[]{"right/Europe/Monaco", "Europe/Monaco"},
     	new String[]{"right/Europe/Moscow", "Europe/Moscow"},
     	new String[]{"right/Europe/Oslo", "Europe/Oslo"},
     	new String[]{"right/Europe/Paris", "Europe/Paris"},
     	new String[]{"right/Europe/Prague", "Europe/Prague"},
     	new String[]{"right/Europe/Riga", "Europe/Riga"},
     	new String[]{"right/Europe/Rome", "Europe/Rome"},
     	new String[]{"right/Europe/Samara", "Europe/Samara"},
     	new String[]{"right/Europe/San_Marino", "Europe/Rome"},
     	new String[]{"right/Europe/Sarajevo", "Europe/Belgrade"},
     	new String[]{"right/Europe/Simferopol", "Europe/Simferopol"},
     	new String[]{"right/Europe/Skopje", "Europe/Belgrade"},
     	new String[]{"right/Europe/Sofia", "Europe/Sofia"},
     	new String[]{"right/Europe/Stockholm", "Europe/Stockholm"},
     	new String[]{"right/Europe/Tallinn", "Europe/Tallinn"},
     	new String[]{"right/Europe/Tirane", "Europe/Tirane"},
     	new String[]{"right/Europe/Vaduz", "Europe/Vaduz"},
     	new String[]{"right/Europe/Vatican", "Europe/Rome"},
     	new String[]{"right/Europe/Vienna", "Europe/Vienna"},
     	new String[]{"right/Europe/Vilnius", "Europe/Vilnius"},
     	new String[]{"right/Europe/Warsaw", "Europe/Warsaw"},
     	new String[]{"right/Europe/Zagreb", "Europe/Belgrade"},
     	new String[]{"right/Europe/Zurich", "Europe/Zurich"},
     	new String[]{"right/Factory", "GMT"},
     	new String[]{"right/GB", "Europe/London"},
     	new String[]{"right/GMT", "GMT"},
     	new String[]{"right/GMT0", "GMT"},
     	new String[]{"right/Greenwich", "GMT"},
     	new String[]{"right/HST", "Pacific/Honolulu"},
     	new String[]{"right/Hongkong", "Asia/Hong_Kong"},
     	new String[]{"right/Iceland", "Atlantic/Reykjavik"},
     	new String[]{"right/Indian/Antananarivo", "Indian/Antananarivo"},
     	new String[]{"right/Indian/Chagos", "Indian/Chagos"},
     	new String[]{"right/Indian/Christmas", "Indian/Christmas"},
     	new String[]{"right/Indian/Cocos", "Indian/Cocos"},
     	new String[]{"right/Indian/Comoro", "Indian/Comoro"},
     	new String[]{"right/Indian/Kerguelen", "Indian/Kerguelen"},
     	new String[]{"right/Indian/Mahe", "Indian/Mahe"},
     	new String[]{"right/Indian/Maldives", "Indian/Maldives"},
     	new String[]{"right/Indian/Mauritius", "Indian/Mauritius"},
     	new String[]{"right/Indian/Mayotte", "Indian/Mayotte"},
     	new String[]{"right/Indian/Reunion", "Indian/Reunion"},
     	new String[]{"right/Iran", "Asia/Tehran"},
     	new String[]{"right/Israel", "Asia/Jerusalem"},
     	new String[]{"right/Jamaica", "America/Jamaica"},
     	new String[]{"right/Japan", "Asia/Tokyo"},
     	new String[]{"right/Libya", "Africa/Tripoli"},
     	new String[]{"right/MET", "Europe/Paris"},
     	new String[]{"right/MST", "America/Phoenix"},
     	new String[]{"right/MST7MDT", "America/Denver"},
     	new String[]{"right/Mexico/BajaNorte", "America/Tijuana"},
     	new String[]{"right/Mexico/BajaSur", "America/Mazatlan"},
     	new String[]{"right/Mexico/General", "America/Mexico_City"},
     	new String[]{"right/NZ", "Pacific/Auckland"},
     	new String[]{"right/NZ-CHAT", "Pacific/Chatham"},
     	new String[]{"right/Navajo", "America/Denver"},
     	new String[]{"right/PRC", "Asia/Shanghai"},
     	new String[]{"right/PST8PDT", "America/Los_Angeles"},
     	new String[]{"right/Pacific/Apia", "Pacific/Apia"},
     	new String[]{"right/Pacific/Auckland", "Pacific/Auckland"},
     	new String[]{"right/Pacific/Chatham", "Pacific/Chatham"},
     	new String[]{"right/Pacific/Easter", "Pacific/Easter"},
     	new String[]{"right/Pacific/Efate", "Pacific/Efate"},
     	new String[]{"right/Pacific/Enderbury", "Pacific/Enderbury"},
     	new String[]{"right/Pacific/Fakaofo", "Pacific/Fakaofo"},
     	new String[]{"right/Pacific/Fiji", "Pacific/Fiji"},
     	new String[]{"right/Pacific/Funafuti", "Pacific/Funafuti"},
     	new String[]{"right/Pacific/Galapagos", "Pacific/Galapagos"},
     	new String[]{"right/Pacific/Gambier", "Pacific/Gambier"},
     	new String[]{"right/Pacific/Guadalcanal", "Pacific/Guadalcanal"},
     	new String[]{"right/Pacific/Guam", "Pacific/Guam"},
     	new String[]{"right/Pacific/Honolulu", "Pacific/Honolulu"},
     	new String[]{"right/Pacific/Kiritimati", "Pacific/Kiritimati"},
     	new String[]{"right/Pacific/Kosrae", "Pacific/Kosrae"},
     	new String[]{"right/Pacific/Majuro", "Pacific/Majuro"},
     	new String[]{"right/Pacific/Marquesas", "Pacific/Marquesas"},
     	new String[]{"right/Pacific/Nauru", "Pacific/Nauru"},
     	new String[]{"right/Pacific/Niue", "Pacific/Niue"},
     	new String[]{"right/Pacific/Norfolk", "Pacific/Norfolk"},
     	new String[]{"right/Pacific/Noumea", "Pacific/Noumea"},
     	new String[]{"right/Pacific/Pago_Pago", "Pacific/Pago_Pago"},
     	new String[]{"right/Pacific/Palau", "Pacific/Palau"},
     	new String[]{"right/Pacific/Pitcairn", "Pacific/Pitcairn"},
     	new String[]{"right/Pacific/Ponape", "Pacific/Ponape"},
     	new String[]{"right/Pacific/Port_Moresby", "Pacific/Port_Moresby"},
     	new String[]{"right/Pacific/Rarotonga", "Pacific/Rarotonga"},
     	new String[]{"right/Pacific/Saipan", "Pacific/Saipan"},
     	new String[]{"right/Pacific/Samoa", "Pacific/Pago_Pago"},
     	new String[]{"right/Pacific/Tahiti", "Pacific/Tahiti"},
     	new String[]{"right/Pacific/Tarawa", "Pacific/Tarawa"},
     	new String[]{"right/Pacific/Tongatapu", "Pacific/Tongatapu"},
     	new String[]{"right/Pacific/Truk", "Pacific/Truk"},
     	new String[]{"right/Pacific/Wake", "Pacific/Wake"},
     	new String[]{"right/Pacific/Wallis", "Pacific/Wallis"},
     	new String[]{"right/Poland", "Europe/Warsaw"},
     	new String[]{"right/Portugal", "Europe/Lisbon"},
     	new String[]{"right/ROC", "Asia/Taipei"},
     	new String[]{"right/ROK", "Asia/Seoul"},
     	new String[]{"right/Singapore", "Asia/Singapore"},
     	new String[]{"right/SystemV/AST4ADT", "America/Thule"},
     	new String[]{"right/SystemV/CST6CDT", "America/Chicago"},
     	new String[]{"right/SystemV/EST5EDT", "America/New_York"},
     	new String[]{"right/SystemV/MST7MDT", "America/Denver"},
     	new String[]{"right/SystemV/PST8PDT", "America/Los_Angeles"},
     	new String[]{"right/SystemV/YST9YDT", "America/Anchorage"},
     	new String[]{"right/Turkey", "Europe/Istanbul"},
     	new String[]{"right/UCT", "UTC"},
     	new String[]{"right/US/Alaska", "America/Anchorage"},
     	new String[]{"right/US/Aleutian", "America/Adak"},
     	new String[]{"right/US/Arizona", "America/Phoenix"},
     	new String[]{"right/US/Central", "America/Chicago"},
     	new String[]{"right/US/East-Indiana", "America/Indianapolis"},
     	new String[]{"right/US/Eastern", "America/New_York"},
     	new String[]{"right/US/Hawaii", "Pacific/Honolulu"},
     	new String[]{"right/US/Michigan", "America/New_York"},
     	new String[]{"right/US/Mountain", "America/Denver"},
     	new String[]{"right/US/Pacific", "America/Los_Angeles"},
     	new String[]{"right/US/Samoa", "Pacific/Pago_Pago"},
     	new String[]{"right/UTC", "UTC"},
     	new String[]{"right/Universal", "UTC"},
     	new String[]{"right/W-SU", "Europe/Moscow"},
     	new String[]{"right/WET", "WET"},
     	new String[]{"right/Zulu", "UTC"},
     };
	
	protected BinderModule getBinderModule() {
		return binderModule;
	}

	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}

	protected FolderModule getFolderModule() {
		return folderModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	private static enum ComponentType {

		Task, Calendar;
	}

	/**
	 * parseEvents
	 * 
	 * Extracts the VEVENTs and VTODOs from an ical input stream, converts each to an Event, and calls
	 *   the supplied handler with the event, along with the SUMMARY and DESCRIPTION, if any,
	 *   from the VEVENT (VTODO).
	 * 
	 * @param icalData
	 * @param handler
	 * @throws IOException
	 * @throws ParserException
	 */
	public void parseEvents(Reader icalData, EventHandler handler)
		throws IOException, ParserException
	{
		Event event = null;
		Map<String, TimeZone> timeZones = new HashMap();
		try {
			Calendar cal = (new CalendarBuilder()).build(new UnfoldingReader(icalData));
			for(Object comp : cal.getComponents("VTIMEZONE")) {
				VTimeZone timeZoneComponent = (VTimeZone) comp;
				timeZones.put(timeZoneComponent.getTimeZoneId().getValue(), new TimeZone(timeZoneComponent));
			}
			for(Object comp : cal.getComponents("VEVENT")) {
				VEvent eventComponent = (VEvent) comp;

				event = parseEvent(eventComponent.getStartDate(), eventComponent.getEndDate(), null, 
									eventComponent.getDuration(), (RRule) eventComponent.getProperty("RRULE"),
									eventComponent.getRecurrenceId(), eventComponent.getUid(), timeZones);
				
				String description = null;
				if(eventComponent.getDescription() != null) {
					description = eventComponent.getDescription().getValue();
				}
				String summary = null;
				if(eventComponent.getSummary() != null) {
					summary = eventComponent.getSummary().getValue();
				}
								
				handler.handleEvent(event, description, summary);
			}
			for(Object comp : cal.getComponents("VTODO")) {
				VToDo todoComponent = (VToDo) comp;
				
				event = parseEvent(todoComponent.getStartDate(), null, todoComponent.getDue(), 
						todoComponent.getDuration(), (RRule) todoComponent.getProperty("RRULE"),
						todoComponent.getRecurrenceId(), todoComponent.getUid(), timeZones);
	
				String description = null;
				if(todoComponent.getDescription() != null) {
					description = todoComponent.getDescription().getValue();
				}
				
				String summary = null;
				if(todoComponent.getSummary() != null) {
					summary = todoComponent.getSummary().getValue();
				}
				
				org.kablink.teaming.ical.util.Priority priority = org.kablink.teaming.ical.util.Priority.fromIcalPriority(todoComponent.getPriority());
				org.kablink.teaming.ical.util.Status status = org.kablink.teaming.ical.util.Status.fromIcalStatus(todoComponent.getStatus());		
				org.kablink.teaming.ical.util.PercentComplete percentComplete = org.kablink.teaming.ical.util.PercentComplete.fromIcalPercentComplete(todoComponent.getPercentComplete());
				
				String location = null;
				if(todoComponent.getLocation() != null) {
					location = todoComponent.getLocation().getValue();
				}	
							
				handler.handleTodo(event, description, summary, 
						priority!=null?priority.name():null, 
						status!=null?status.name():null, 
						percentComplete!=null?percentComplete.name():null, location);
			}
		} catch(IOException e) {
			logger.debug("IOException while parsing iCal stream", e);
			throw e;
		} catch(ParserException e) {
			logger.debug("ParserException while parsing iCal stream", e);
			throw e;
		}
	}
	
	/**
	 * Parse VEVENT or VTODO.
	 * 
	 * @param start
	 * @param end
	 * @param due
	 * @param duration
	 * @param recurrence
	 * @param recurrenceId
	 * @param timeZones
	 * @return
	 */
	private Event parseEvent(DtStart start, DtEnd end, Due due, Duration duration, RRule recurrence,
			RecurrenceId recurrenceId, Uid uid, Map<String, TimeZone> timeZones) {	
		if (start == null && end == null && due == null && duration == null && uid == null) {
			return null;
		}
		
		Event event = new Event();
		
		if (uid != null) {
			event.setUid(uid.getValue());
		}
		if (start != null) {
			GregorianCalendar startCal = new GregorianCalendar();
			startCal.setTime(start.getDate());
			event.setDtStart(startCal);
		}
		
		if (isAllDaysEvent(start)) {
			event.allDaysEvent();
		} else {
			Parameter tzId = start.getParameter(Parameter.TZID);
			if (start.getParameter(Parameter.TZID) != null) {
				TimeZone tz = timeZones.get(tzId.getValue());
				event.setTimeZone(tz);
			}
		}
		if(end != null) {
			java.util.Date endDate = end.getDate();
			if (end.getParameter(Value.DATE.getName()) != null) {
				// only date (no time) so it's all days event
				// intern we store the date of last event's day - so get one day before
	 			endDate = new org.joda.time.DateTime(endDate).minusDays(1).toDate();
			}
			GregorianCalendar endCal = new GregorianCalendar();
			endCal.setTime(endDate);
			event.setDtEnd(endCal);
		} else if (due != null) {
			java.util.Date endDate = due.getDate();
			if (due.getParameter(Value.DATE.getName()) != null) {
				// only date (no time) so it's all days event
				// intern we store the date of last event's day - so get one day before
				endDate = new org.joda.time.DateTime(endDate).minusDays(1).toDate();
			}
			GregorianCalendar endCal = new GregorianCalendar();
			endCal.setTime(endDate);
			event.setDtEnd(endCal);			
		} else if (duration != null) {
			event.setDuration(new org.kablink.util.cal.Duration(duration.toString()));
		}
		if (recurrence != null && (recurrenceId == null)) {
			Recur recur = recurrence.getRecur();
			event.setFrequency(recur.getFrequency());
			event.setInterval(recur.getInterval());
			if(recur.getUntil() != null) {
				GregorianCalendar untilCal = new GregorianCalendar();
				untilCal.setTime(recur.getUntil());
				event.setUntil(untilCal);
			} else {
				event.setCount(recur.getCount());
			}
			if(recur.getDayList() != null) {
				event.setByDay(recur.getDayList().toString());
			}
			if(recur.getHourList() != null) {
				event.setByHour(recur.getHourList().toString());
			}
			if(recur.getMinuteList() != null) {
				event.setByMinute(recur.getMinuteList().toString());
			}
			if(recur.getMonthDayList() != null) {
				event.setByMonthDay(recur.getMonthDayList().toString());
			}
			if(recur.getMonthList() != null) {
				event.setByMonth(recur.getMonthList().toString());
			}
			if(recur.getSecondList() != null) {
				event.setBySecond(recur.getSecondList().toString());
			}
			if(recur.getWeekNoList() != null) {
				event.setByWeekNo(recur.getWeekNoList().toString());
			}
			if(recur.getYearDayList() != null) {
				event.setByYearDay(recur.getYearDayList().toString());
			}
			if(recur.getWeekStartDay() != null) {
				event.setWeekStart(recur.getWeekStartDay().toString());
			}
		}
		return event;
	}

	private boolean isAllDaysEvent(DtStart start) {
		return start != null && !((start.getParameter(Parameter.TZID) != null) || 
				(start.getParameter(Parameter.TZID) == null && 
					start.getParameter(Value.DATE.getName()) == null));
	}

	/**
	 * parseEvents
	 * 
	 * Convenience method that returns the parsed events as a list, rather than calling a handler.
	 *  SUMMARY and DESCRIPTION from the VEVENTS is discarded.
	 * 
	 * @param icalData
	 * @param handler
	 * @throws IOException
	 * @throws ParserException
	 */
	public List<Event> parseEvents(Reader icalData)
		throws IOException, ParserException
	{
		final List<Event> events = new LinkedList<Event>();
		
		EventHandler myHandler = new EventHandler() {
			public void handleEvent(Event e, String description, String summary)
			{
				events.add(e);
			}

			public void handleTodo(Event event, String description, String summary, String priority, String status, String completed, String location) {
				events.add(event);
			}
		};

		parseEvents(icalData, myHandler);
		
		return events;
	}
	/**
	 * parseToEntries
	 * 
	 * Creates an entry in the given folder for each VEVENT in the given ical input stream, returning a list of
	 *  the added IDs.
	 * 
	 * @param folderId
	 * @param icalFile
	 * @return id list of created entries
	 * @throws IOException
	 * @throws ParserException
	 */
	public AttendedEntries parseToEntries (final Long folderId, InputStream icalFile) throws IOException, ParserException
	{
		Folder folder = (Folder)folderModule.getFolder(folderId);
		return parseToEntries(folder, null, icalFile);
	}
	public AttendedEntries parseToEntries (final Folder folder, Definition def, InputStream icalFile) throws IOException, ParserException {

		final AttendedEntries attendedEntries = new AttendedEntries();
		if (def == null) {
			def = folder.getDefaultEntryDef();
			if (def == null) return attendedEntries;
		} 
		final String entryType = def.getId();
		final String eventName= getEventName(def.getDefinition());
		if(eventName == null) {
			return attendedEntries;
		}

		EventHandler entryCreator = new EventHandler() {
			
			public void handleEvent(Event event, String description, String summary) {
				Map<String, Object> formData = new HashMap<String, Object>();
				
				shorterSummary(formData, description, summary);
				formData.put(eventName, event);
				
				addOrModifyEntry(event, new MapInputData(formData));
			}

			public void handleTodo(Event event, String description, String summary, String priority, String status, String completed, String location) {
				Map<String, Object> formData = new HashMap<String, Object>();
				
				shorterSummary(formData, description, summary);
				formData.put(eventName, event);
				formData.put("priority", new String[] {priority});
				formData.put("status", new String[] {status});
				formData.put("completed", new String[] {completed});
				formData.put("location", new String[] {location});
				
				// TODO: add attachments support
				// TODO: alert's support
				
				addOrModifyEntry(event, new MapInputData(formData));
			}
			
			private void shorterSummary(Map<String, Object> formData, String description, String summary) {
				if (summary != null && summary.length() > 255) {
					String summmaryTemp = summary.substring(0, 252);
					int indexLastAllowedSpace = summmaryTemp.lastIndexOf(" ");
					summmaryTemp = summary.substring(0, indexLastAllowedSpace) + "...";
					description = "..." + summary.substring(indexLastAllowedSpace, summary.length()) + "\n\n" + (description != null ? description : "");
					summary = summmaryTemp;
				}
				formData.put("description", new String[] {description != null ? description : ""});
				formData.put("title", new String[] {summary != null ? summary : ""});
			}
			
			private void addOrModifyEntry(Event event, MapInputData inputData) {
				try {
					Event eventToUpdate = findEventByUid(folder.getId(), event);
					if (eventToUpdate != null && eventToUpdate.getOwner() != null &&
							eventToUpdate.getOwner().getEntity() != null) {
						DefinableEntity entity = eventToUpdate.getOwner().getEntity();
						folderModule.modifyEntry(folder.getId(), entity.getId(), inputData, null, null, null, null);
						attendedEntries.modified.add(entity.getId());
					} else {
						Long entryId = folderModule.addEntry(folder.getId(), entryType, inputData, null, null);
						attendedEntries.added.add(entryId);
					}
				} catch (AccessControlException e) {
					logger.warn("Can not create entry from iCal file.", e);
				} catch (WriteFilesException e) {
					logger.warn("Can not create entry from iCal file.", e);
				}
			}			
		};

		parseEvents(new InputStreamReader(icalFile), entryCreator);
		return attendedEntries;
	}
	
	protected Event findEventByUid(Long folderId, Event event) {
		if (event == null || event.getUid() == null) {
			return null;
		}
		FilterControls fc = new FilterControls("uid", event.getUid());
		fc.add("owner.owningBinderId", folderId);
		List events = getCoreDao().loadObjects(Event.class, fc, RequestContextHolder.getRequestContext().getZoneId());
		if (events.size() > 0) {
			return (Event)events.iterator().next();
		}
		
		// check if this is update of v1.0 event (uid is not stored)
		// find event by folder id and event id
		Event.UidBuilder uidBuilder = event.parseUid();
		if (uidBuilder.getBinderId() == null || 
				uidBuilder.getEntryId() == null || 
				uidBuilder.getEventId() == null) {
			// uid is not intern uid
			return null;
		}
		if (!uidBuilder.getBinderId().equals(folderId)) {
			// wrong folder
			return null;
		}
		
		fc = new FilterControls("id", uidBuilder.getEventId());
		fc.add("owner.owningBinderId", uidBuilder.getBinderId());
		events = getCoreDao().loadObjects(Event.class, fc, RequestContextHolder.getRequestContext().getZoneId());
		if (events.size() > 0) {
			return (Event)events.iterator().next();
		}
		
		return null;
	}

	private String getEventName(Document definition) {
		Element configRoot = definition.getRootElement();
		Element eventEl = (Element) configRoot.selectSingleNode("//item[@name='event']//property[@name='name']");
		if (eventEl != null) {
			return eventEl.attributeValue("value");
		}
		logger.debug("Entry defintion ["+ definition.asXML() +"] has no events. iCalendar import aborted.");
		return null;
	}
	
	public Calendar generate(DefinableEntity entry,
			Collection events, String defaultTimeZoneId) {
		Calendar calendar = createICalendar(null);
		generate(calendar, entry, events, defaultTimeZoneId);
		return calendar;
	}
	
	public Calendar generate(String calendarName,  List folderEntries, String defaultTimeZoneId) {
		Calendar calendar = createICalendar(calendarName);
		
		if (folderEntries == null) {
			return calendar;
		}
		
		Iterator it = folderEntries.iterator();
		while (it.hasNext()) {
			DefinableEntity entry = (DefinableEntity)it.next();
			generate(calendar, entry, entry.getEvents(), defaultTimeZoneId);
		}
		
		// Calendar without any components can not exists
		// so put time zone
		if (calendar.getComponents().isEmpty()) {
			TimeZone timeZone = getTimeZone(null, defaultTimeZoneId);
				
			calendar.getComponents().add(timeZone.getVTimeZone());
		}
	
		return calendar;
	}
	
	protected void generate(Calendar calendar, DefinableEntity entry,
			Collection events, String defaultTimeZoneId) {
		if (calendar == null) {
			throw new InvalidParameterException("'calendar' can't be null.");
		}
		
		if (entry == null) {
			throw new InvalidParameterException("'entry' can't be null.");
		}

		if (events == null || events.isEmpty()) {
			return;
		}

		ComponentType componentType = getComponentType(entry);

		Iterator eventsIt = events.iterator();
		while (eventsIt.hasNext()) {
			Event event = (Event) eventsIt.next();
			addEventToICalendar(calendar, entry, event, defaultTimeZoneId,
					componentType);
		}
	}

	/**
	 * Creates new iCalendar object and sets fields.
	 */
	private Calendar createICalendar(String calendarName) {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(PROD_ID);
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		calendar.getProperties().add(Method.REQUEST);
		if (calendarName != null) {
			calendar.getProperties().add(new XProperty("X-WR-CALNAME", calendarName));
		}
		return calendar;
	}

	private ComponentType getComponentType(DefinableEntity entry) {
		Definition entryDef = entry.getEntryDef();
		
		String family = DefinitionUtils.getFamily(entryDef.getDefinition());

		if (family != null && family.equals(ObjectKeys.FAMILY_TASK)) {
			return ComponentType.Task;
		} else if (family != null && family.equals(ObjectKeys.FAMILY_CALENDAR)) {
			return ComponentType.Calendar;
		}
		return ComponentType.Calendar;
	}

	private void addEventToICalendar(Calendar calendar, DefinableEntity entry,
			Event event, String defaultTimeZoneId, ComponentType componentType) {
		// there is probably a bug in iCal4j or in Java: for some time zones
		// the date after setting the time zone is wrong, it means: the other
		// time offset is supplied as it should be...
		TimeZone timeZone = getTimeZone(event.getTimeZone(), defaultTimeZoneId);
		if (timeZone != null) {
			VTimeZone tz = timeZone.getVTimeZone();
			if (!calendar.getComponents(Component.VTIMEZONE).contains(tz)) {
				calendar.getComponents().add(tz);
			}
		}

		if (componentType.equals(ComponentType.Task)) {
			calendar.getComponents().add(createVTodo(entry, event, timeZone));
		} else if (componentType.equals(ComponentType.Calendar)) {
			calendar.getComponents().add(createVEvent(entry, event, timeZone));
		}
	}

	private VToDo createVTodo(DefinableEntity entry, Event event,
			TimeZone timeZone) {
		VToDo vToDo = null;
		
		if (!event.isAllDayEvent()) {
			DateTime start = new DateTime(event.getDtStart().getTime());
			if (timeZone != null) {
				start.setTimeZone(timeZone);
			}

			Dur duration = null;
			if (event.getDuration().getWeeks() > 0) {
				duration = new Dur(event.getDuration().getWeeks());
			} else {
				duration = new Dur(event.getDuration().getDays(), event
						.getDuration().getHours(), event.getDuration()
						.getMinutes(), event.getDuration().getSeconds());
			}
			vToDo = new VToDo(start, duration, entry.getTitle());
			vToDo.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE_TIME);
		} else {
			Date start = new Date(event.getDtStart().getTime());
			Date end = (Date)start.clone();
			if (event.getDtEnd() != null) {
				end = new Date(event.getDtEnd().getTime());
			}
			end = new Date(new org.joda.time.DateTime(end).plusDays(1).toDate());
			vToDo = new VToDo(start, end, entry.getTitle());
			vToDo.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE);
			vToDo.getProperties().getProperty(Property.DUE)
					.getParameters().add(Value.DATE);			
		}
		
		setComponentDescription(vToDo, entry.getDescription().getText());
		setComponentUID(vToDo, entry, event);

		addToDoPriority(vToDo, entry);
		addToDoStatus(vToDo, entry);
		addComponentCompleted(vToDo, entry);
		setComponentAttendee(vToDo, entry);
		setComponentOrganizer(vToDo, entry);
		setComponentLocation(vToDo, entry);
		
		addRecurrences(vToDo, event);

		return vToDo;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> findUserListAttributes(Document definitionConfig) {
		List<String> result = new ArrayList<String>();
		
    	List nodes = definitionConfig.selectNodes("//item[@type='form']//item[@name='entryFormForm']//item[@type='data' and (@name='user_list' or @name='group_list' or @name='team_list')]/properties/property[@name='name']/@value");
    	if (nodes == null) {
    		return result;
    	}
    	
    	Iterator it = nodes.iterator();
    	while (it.hasNext()) {
    		Node node = (Node)it.next();
    		result.add(node.getStringValue());
    	}
    	return result;
	}

	private void setComponentAttendee(Component component, DefinableEntity entry) {
		Long zoneId = entry.getZoneId();
		ArrayList<Long> attendeeIds = new ArrayList<Long>();
		
		// Scan the possible attendee list attributes,
		Iterator<String> listAttributes = findUserListAttributes(entry.getEntryDef().getDefinition()).iterator();
		while (listAttributes.hasNext()) {
			// Skip any attributes that aren't defined on the entry.
			String attributeName = ((String) listAttributes.next());
			CustomAttribute customAttribute = entry.getCustomAttribute(attributeName);
			if (null == customAttribute) {
				continue;
			}

			// Read and handle this attribute's values.
			Set<Long>ids = LongIdUtil.getIdsAsLongSet(customAttribute.getValueSet());
			attendeeIds.addAll(ids);
			addTeamMembers(attendeeIds, getCoreDao().loadObjects(ids, Binder.class, zoneId), zoneId);
		}

		// Did we find any attendees in any of the attributes?
		if (0 < attendeeIds.size()) {
			// Yes!  Explode the groups into their constituent users,
			// load and scan them...
			Set<Long> userAttendeeIds = getProfileDao().explodeGroups(attendeeIds, zoneId);
			List<User> users = getProfileDao().loadUsers(userAttendeeIds, zoneId);
			for (User user:users) {
				// ...adding information about each to the component.
				ParameterList attendeeParams = new ParameterList();
				attendeeParams.add(new Cn(user.getTitle()));
				attendeeParams.add(Role.REQ_PARTICIPANT);
				String uri = "MAILTO:" + user.getEmailAddress();
				try {
					component.getProperties().add(new Attendee(attendeeParams, uri));
				} catch (URISyntaxException e) {
					logger.warn("Can not add attendee because of URI [" + uri
							+ "] parsing problem");
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addTeamMembers(ArrayList<Long> attendeeIds, List binders, Long zoneId) {
		// Add any team members from the binders.
		for (Object binder:binders) {
			attendeeIds.addAll(((Binder) binder).getTeamMemberIds());
		}
	}
	
	private void setComponentOrganizer(Component component, DefinableEntity entry) {

		Principal principal = (Principal) entry.getCreation().getPrincipal();
		ParameterList organizerParams = new ParameterList();
		organizerParams.add(new Cn(principal.getTitle()));

		String uri = "MAILTO:" + principal.getEmailAddress();
		try {
			component.getProperties().add(new Organizer(organizerParams, uri));
		} catch (URISyntaxException e) {
			logger.warn("Can not add organizer because of URI [" + uri
					+ "] parsing problem");
		}

	}
	
	private void setComponentLocation(Component component, DefinableEntity entry) {

		CustomAttribute customAttribute = entry
			.getCustomAttribute("location");

		if (customAttribute == null) {
			return;
		}
		
		String value = (String) customAttribute.getValue();		
		if (Validator.isNull(value)) return;

		component.getProperties().add(new Location(value));


	}

	private void addComponentCompleted(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValueSet();

		if (value == null) {
			return;
		}

		org.kablink.teaming.ical.util.PercentComplete completed = null;

		if (value.contains("c000")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c000;
		} else if (value.contains("c010")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c010;
		} else if (value.contains("c020")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c020;
		} else if (value.contains("c030")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c030;
		} else if (value.contains("c040")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c040;
		} else if (value.contains("c050")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c050;
		} else if (value.contains("c060")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c060;
		} else if (value.contains("c070")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c070;
		} else if (value.contains("c080")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c080;
		} else if (value.contains("c090")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c090;
		} else if (value.contains("c100")) {
			completed = org.kablink.teaming.ical.util.PercentComplete.c100;
		} else {
			logger.error("The task compleded has wrong value [" + value + "].");
			return;
		}

		toDo.getProperties().add(completed.toIcalPercentComplete());
	}

	private void addToDoStatus(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValueSet();

		if (value == null) {
			return;
		}

		org.kablink.teaming.ical.util.Status status = null;

		if (value.contains("s1")) {
			status = org.kablink.teaming.ical.util.Status.s1;
		} else if (value.contains("s2")) {
			status = org.kablink.teaming.ical.util.Status.s2;
		} else if (value.contains("s3")) {
			status = org.kablink.teaming.ical.util.Status.s3;
		} else if (value.contains("s4")) {
			status = org.kablink.teaming.ical.util.Status.s4;
		} else {
			logger.error("The task status has wrong value [" + value + "].");
			return;
		}

		if (status == null) {
			logger.error("The task status is not defined.");
			return;
		}

		toDo.getProperties().add(status.toIcalStatus());
	}

	private void addToDoPriority(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValueSet();
		
		org.kablink.teaming.ical.util.Priority priority = org.kablink.teaming.ical.util.Priority.p1;
		if (value != null) {
			if (value.contains("p5")) {
				priority = org.kablink.teaming.ical.util.Priority.p5;
			} else if (value.contains("p4")) {
				priority = org.kablink.teaming.ical.util.Priority.p4;
			} else if (value.contains("p3")) {
				priority = org.kablink.teaming.ical.util.Priority.p3;
			} else if (value.contains("p2")) {
				priority = org.kablink.teaming.ical.util.Priority.p2;
			} else if (value.contains("p1")) {
				priority = org.kablink.teaming.ical.util.Priority.p1;
			}
		}
		toDo.getProperties().add(priority.toIcalPriority());
	}

	private VEvent createVEvent(DefinableEntity entry, Event event,
			TimeZone timeZone) {
		VEvent vEvent = null;
		if (!event.isAllDayEvent()) {
			DateTime start = new DateTime(event.getDtStart().getTime());
			if (timeZone != null) {
				start.setTimeZone(timeZone);
			}

			Dur duration = null;
			if (event.getDuration().getWeeks() > 0) {
				duration = new Dur(event.getDuration().getWeeks());
			} else {
				duration = new Dur(event.getDuration().getDays(), event
						.getDuration().getHours(), event.getDuration()
						.getMinutes(), event.getDuration().getSeconds());
			}
			vEvent = new VEvent(start, duration, entry.getTitle());
			vEvent.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE_TIME);
			vEvent.getProperties().add(Transp.OPAQUE);
		} else {
			Date start = new Date(event.getDtStart().getTime());
			Date end = (Date)start.clone();
			if (event.getDtEnd() != null) {
				end = new Date(event.getDtEnd().getTime());
			}
			end = new Date(new org.joda.time.DateTime(end).plusDays(1).toDate());
			vEvent = new VEvent(start, end, entry.getTitle());
			
			// one day events mark as TRANSPARENT
			// An 'event on a day' - anniversaries and birthdays
			if ((new YearMonthDay(start)).equals((new YearMonthDay(end)).minusDays(1))) {
				vEvent.getProperties().add(Transp.TRANSPARENT);
			} else {
				vEvent.getProperties().add(Transp.OPAQUE);
			}
		}

		setComponentDescription(vEvent, entry.getDescription().getText());
		setComponentUID(vEvent, entry, event);
		setComponentAttendee(vEvent, entry);
		setComponentOrganizer(vEvent, entry);
		setComponentLocation(vEvent, entry);
		addRecurrences(vEvent, event);

		return vEvent;
	}

	public static TimeZone getTimeZone(java.util.TimeZone timeZone, String defaultTimeZone) {
		TimeZoneRegistry iCal4jTZRegistry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone iCal4jTZ = null;
		
		if (timeZone != null) {
			iCal4jTZ = mapJavaTZToICal4jTZ(iCal4jTZRegistry, TimeZoneHelper.fixTimeZone(timeZone));
		}
		
		if (iCal4jTZ == null) {
			// get current user time zone
			User user = RequestContextHolder.getRequestContext().getUser();
			iCal4jTZ = mapJavaTZToICal4jTZ(iCal4jTZRegistry, user.getTimeZone());
		}
		
		if (iCal4jTZ == null && defaultTimeZone != null) {
			iCal4jTZ = iCal4jTZRegistry.getTimeZone(defaultTimeZone);
		}
		return iCal4jTZ;
	}

	/*
	 * Maps a Java TimeZone object to an equivalent ical4j TimeZone
	 * object.  Null is returned if the mapping can't be performed.
	 * 
	 * Note that the core algorithm used here was abstracted from the
	 * original implementation of getTimeZone(), including the jodatime
	 * stuff.  What I added was if the initial stuff failed, it now
	 * tries to map the time zone ID as if it were from the Linux
	 * namespace to the Java namespace and tries to map that.
	 */
	private static TimeZone mapJavaTZToICal4jTZ(TimeZoneRegistry iCal4jTZRegistry, java.util.TimeZone javaTZ) {
		TimeZone iCal4jTZ = null;
		// use jodatime to convert 3-characters zone ids to ical names 
		DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(javaTZ);
		if (null != dateTimeZone) {
			String tzID = dateTimeZone.getID();
			iCal4jTZ = iCal4jTZRegistry.getTimeZone(tzID);
			if (iCal4jTZ == null) {
				tzID = mapLinuxTZIDToJavaTZID(tzID);
				if ((null != tzID) && (0 < tzID.length())) {
					iCal4jTZ = iCal4jTZRegistry.getTimeZone(tzID);
				}
			}
		}
		return iCal4jTZ;
	}

	/*
	 * If they're different, returns the Java TZ ID equivalent of a
	 * Linux TZ ID.  Otherwise, returns null.
	 */
	private static String mapLinuxTZIDToJavaTZID(String tzID) {
		loadLinuxTZIDMappings();
		return linuxTZIDMappings.get(tzID.toLowerCase());
	}

	/*
	 * If linuxTZIDMappings hasn't been defined yet, it's defined and
	 * loaded using the information from LINUX_TZID_MAPPINGS.
	 */
	private static void loadLinuxTZIDMappings() {
		if (null != linuxTZIDMappings) {
			return;
		}

		linuxTZIDMappings = new HashMap<String, String>();
		for (int i = 0; i < LINUX_TZID_MAPPINGS.length; i += 1) {
			String[] tzMap = LINUX_TZID_MAPPINGS[i];
			String tzFrom = tzMap[0].toLowerCase();
			String tzTo = tzMap[1];
			if (tzFrom.equals(tzTo.toLowerCase())) {
				continue;
			}
			linuxTZIDMappings.put(tzFrom, tzTo);
		}
	}
	
	public static void addRecurrences(CalendarComponent component, Event event) {
		if (event.getFrequency() == Event.NO_RECURRENCE) {
			return;
		}

		try {
			Recur recur = null;

			if (event.getFrequency() == Event.DAILY) {
				recur = new Recur("FREQ;=" + Recur.DAILY);
			} else if (event.getFrequency() == Event.HOURLY) {
				recur = new Recur("FREQ;=" + Recur.HOURLY);
			} else if (event.getFrequency() == Event.MINUTELY) {
				recur = new Recur("FREQ;=" + Recur.MINUTELY);
			} else if (event.getFrequency() == Event.MONTHLY) {
				recur = new Recur("FREQ;=" + Recur.MONTHLY);
			} else if (event.getFrequency() == Event.SECONDLY) {
				recur = new Recur("FREQ;=" + Recur.SECONDLY);
			} else if (event.getFrequency() == Event.WEEKLY) {
				recur = new Recur("FREQ;=" + Recur.WEEKLY);
			} else if (event.getFrequency() == Event.YEARLY) {
				recur = new Recur("FREQ;=" + Recur.YEARLY);
			}

			if (event.getCount() > 0) {
				recur.setCount(event.getCount());
			} else {
				java.util.Calendar until = event.getUntilWithMaxLoopsIfNeeded();
				// if time is before start time, repeat doesn't occures in
				// calendar view - ??
				// TODO: test it again
				until.set(java.util.Calendar.HOUR_OF_DAY, 23);
				until.set(java.util.Calendar.MINUTE, 59);
				until.set(java.util.Calendar.SECOND, 59);
				recur.setUntil(new DateTime(until.getTime()));
			}
			recur.setInterval(event.getInterval());

			if (event.getByDay() != null && event.getByDay().length > 0) {
				for (int i = 0; i < event.getByDay().length; i++) {
					DayAndPosition dp = event.getByDay()[i];
					String day = "";
					if (dp.getDayOfWeek() == java.util.Calendar.SUNDAY) {
						day = "SU";
					} else if (dp.getDayOfWeek() == java.util.Calendar.MONDAY) {
						day = "MO";
					} else if (dp.getDayOfWeek() == java.util.Calendar.TUESDAY) {
						day = "TU";
					} else if (dp.getDayOfWeek() == java.util.Calendar.WEDNESDAY) {
						day = "WE";
					} else if (dp.getDayOfWeek() == java.util.Calendar.THURSDAY) {
						day = "TH";
					} else if (dp.getDayOfWeek() == java.util.Calendar.FRIDAY) {
						day = "FR";
					} else if (dp.getDayOfWeek() == java.util.Calendar.SATURDAY) {
						day = "SA";
					}

					recur.getDayList().add(
							new WeekDay(day, dp.getDayPosition()));
				}
			}

			if (event.getByHour() != null && event.getByHour().length > 0) {
				for (int i = 0; i < event.getByHour().length; i++) {
					recur.getHourList().add(event.getByHour()[i]);
				}
			}

			if (event.getByMinute() != null && event.getByMinute().length > 0) {
				for (int i = 0; i < event.getByMinute().length; i++) {
					recur.getMinuteList().add(event.getByMinute()[i]);
				}
			}

			if (event.getByMonth() != null && event.getByMonth().length > 0) {
				for (int i = 0; i < event.getByMonth().length; i++) {
					recur.getMonthList().add(event.getByMonth()[i]);
				}
			}

			if (event.getByMonthDay() != null
					&& event.getByMonthDay().length > 0) {
				for (int i = 0; i < event.getByMonthDay().length; i++) {
					recur.getMonthDayList().add(event.getByMonthDay()[i]);
				}
			}

			if (event.getBySecond() != null && event.getBySecond().length > 0) {
				for (int i = 0; i < event.getBySecond().length; i++) {
					recur.getSecondList().add(event.getBySecond()[i]);
				}
			}

			if (event.getByWeekNo() != null && event.getByWeekNo().length > 0) {
				for (int i = 0; i < event.getByWeekNo().length; i++) {
					recur.getWeekNoList().add(event.getByWeekNo()[i]);
				}
			}

			if (event.getByYearDay() != null && event.getByYearDay().length > 0) {
				for (int i = 0; i < event.getByYearDay().length; i++) {
					recur.getYearDayList().add(event.getByYearDay()[i]);
				}
			}

			// TODO: recur.setWeekStartDay(arg0);
			// TODO: recur.setPosList ?

			RRule rrule = new RRule(recur);
			component.getProperties().add(rrule);

		} catch (ParseException e) {
			//logger.error("Error by creating RRule by iCal export. ", e);
			return;
		}
	}

	private void setComponentDescription(CalendarComponent component,
			String descr) {
		//grwise was ignore mail without a description
		if (Validator.isNotNull(descr))
			component.getProperties().add(new Description(descr));
	}

	private void setComponentUID(CalendarComponent component,
			DefinableEntity entry, Event event) {
		String uid = event.getUid();
		if (uid == null) {
			// v1.0 compatibility - old events don't have stored uid
			uid = event.generateUid(entry);
		}

		component.getProperties().add(new Uid(uid));
	}
}
