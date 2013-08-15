package org.kabling.teaming.install.shared;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.BCodec;



import org.kabling.teaming.install.shared.ConfigurationSaveException;

public class EncodingUtils {
	public static String encode(String text) {
		try {
			return getCodec().encode(text);
		} catch (Exception e) {
			throw new ConfigurationSaveException("Unable to encode string: " + text);
		}
	}
	
	public static String decode(String text) {
		try {
			return getCodec().decode(text);
		} catch (Exception e) {
			throw new ConfigurationSaveException("Unable to decode string: " + text);
		}
	}
	
	private static BCodec getCodec() {
		return new BCodec() {
			
			@Override
			public String encode(String text) throws EncoderException {
				if (text != null) {
					if (text.startsWith("=?") && text.endsWith("?=")) {
						return text;
					}
				}
				
				return super.encode(text);
			}
	
			@Override
			public String decode(String text) throws DecoderException {
				if (text != null) {
					if (!text.startsWith("=?") || !text.endsWith("?=")) {
						return text;
					}
				}
	
				return super.decode(text);
			}
		};
	}
}