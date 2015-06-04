package org.greentea.aspect.test;

import org.greentea.aspect.log.annotation.LoggableObject;
import org.junit.Test;

public class LoggableTest {
	
	@SuppressWarnings("unused")
	@LoggableObject(logMode={LoggableObject.LogModes.ALL})
	private void methodWithNoReturn(){
		//sum of 2 variables
		byte a = 1;
		byte b = 2;
		byte c = (byte) (a + b);
		//Some more computation
		String str = "ABCDEFG";
		String computedString = str.replaceAll("A", "b");
		computedString += "a";
		StringBuilder strB = new StringBuilder(computedString);
		strB.append("abc");
	}
	
	@LoggableObject(logMode={LoggableObject.LogModes.ALL})
	private int methodWithArgumentsAndReturnType(String a, Integer b, String y, Object o, String c){
		return b;
	}
	
	@Test
	public void logAnnotMethods(){
		System.out.println("\n\n\n\nTesting this amazing API...\n\n");
		methodWithArgumentsAndReturnType(null, null, null, null, null);
	}
}
