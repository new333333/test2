package com.sitescape.team.lucene;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

public class LPropUtils {
	private static Properties props;
	private String propsFileName = "";
	private static LPropUtils instance;
	

	public LPropUtils(String filename) throws IOException {
		propsFileName = filename;
		try {

			java.io.InputStream stream = LPropUtils.class.getClassLoader()
					.getResourceAsStream(propsFileName);
			props = new Properties();
			props.load(stream);
			stream.close();
		} catch (IOException ioe) {
			throw new IOException("The " + propsFileName + " file could not be found");
		}
		instance = this;
	}
	
	public static String getProp(String propName) {	
		String retVal =props.getProperty(propName);
		if (retVal == null) retVal = "";
		return retVal;
	}
}
