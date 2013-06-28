package org.kabling.teaming.install.shared;

import java.io.Serializable;
import java.util.Comparator;

public class TimeZoneNameComparator implements Comparator<String>, Serializable
{

	private static final long serialVersionUID = 8291524187015698503L;

	@Override
	public int compare(String o1, String o2)
	{
		//(GMT +7:00 Some Name)
		String hour1 = o1.substring(5, o1.indexOf(":"));
		String hour2 = o2.substring(5, o2.indexOf(":"));

		//If hours are the same, we need to sort by minutes
		if (hour1.equals(hour2))
		{
			int startIndex1 = o1.indexOf(":") + 1;
			int startIndex2 = o2.indexOf(":") + 1;
			
			//Get the minutes 
			String min1 = o1.substring(startIndex1, startIndex1 + 2);
			String min2 = o2.substring(startIndex2, startIndex2 + 2);

			//If the hours and minutes are the same, we sort by the whole name
			if (min1.compareTo(min2) == 0)
				return o2.compareTo(o1);
			
			//Compare the minutes
			return min1.compareTo(min2);
		}
		
		//Get the integer value for the hours
		int hour1Int = Integer.valueOf(hour1.startsWith("+") ? hour1.substring(1) : hour1);
		int hour2Int = Integer.valueOf(hour2.startsWith("+") ?hour2.substring(1) : hour2);
		
		//Hours are not the same, we show negative values first
		return hour2Int > hour1Int ? -1 : 1;
	}
}
