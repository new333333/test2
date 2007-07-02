package com.sitescape.team.lucene;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

public class LPropUtils {
	private static Properties props;
	private String propsFileName = "";
	private static LPropUtils instance = null;
	

	public LPropUtils(String filename) throws IOException {
		if (instance != null) return;
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
	
	
	public static Properties getProps() throws IOException {	
		if (instance == null) {
			try {
				new LPropUtils("lucene-server.properties");
			} catch (IOException ioe) {
				throw new IOException(
						"The lucene-server.properties could not be found");
			}
		}
		return instance.props;
	}
}
