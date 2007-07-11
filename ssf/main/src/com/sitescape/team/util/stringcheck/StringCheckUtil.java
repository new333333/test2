package com.sitescape.team.util.stringcheck;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SPropsUtil;

public class StringCheckUtil implements InitializingBean {

	private static StringCheckUtil instance; // A singleton instance
	
	private static StringCheck[] checkers;
	
	public StringCheckUtil() {
		if(instance != null)
			throw new SingletonViolationException(StringCheckUtil.class);
		
		instance = this;
	}
	
	public void afterPropertiesSet() throws Exception {
		String[] classNames = SPropsUtil.getStringArray("string.check.checker.classes", ",");
	
		checkers = new StringCheck[classNames.length];
		
		Class checkerClass;
		for(int i = 0; i < classNames.length; i++) {
			checkerClass = ReflectHelper.classForName(classNames[i]);
			checkers[i] = (StringCheck) checkerClass.newInstance();
		}
	}

    private static StringCheckUtil getInstance() {
    	return instance;
    }

	public static String check(String input) throws StringCheckException {
		return getInstance().checkAll(input);
	}
	
	public static Map<String,String[]> check(Map<String,String[]> input) throws StringCheckException {
		return getInstance().checkAll(input);
	}

	private String checkAll(String input) throws StringCheckException {
		for(int i = 0; i < checkers.length; i++) {
			input = checkers[i].check(input);
		}
		return input;
	}
	
	private Map<String,String[]> checkAll(Map<String,String[]> input) throws StringCheckException {
		for(int i = 0; i < checkers.length; i++) {
			input = checkAll(checkers[i], input);
		}
		return input;
	}

	private Map<String,String[]> checkAll(StringCheck checker, Map<String,String[]> input) throws StringCheckException {
		Map output = new TreeMap<String, String[]>();
		
		String[] value=null,newValue=null;
		for(String key : input.keySet()) {
			value = input.get(key);
			if(value != null) {
				newValue = new String[value.length];
				for(int i = 0; i < value.length; i++) {
					newValue[i] = checker.check(value[i]);
				}
			}
			output.put(key, newValue);
		}	
		
		return output;
	}
}
