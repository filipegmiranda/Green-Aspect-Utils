package org.greentea.aspect.test;

import org.greentea.aspect.log.LoggableObjects;
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
		return 1;
	}
	
	
	@SuppressWarnings("unused")
	@LoggableObject(pluggableLoggerClass=LoggerTestImplementation.class)
	private void testOtherPluggableLoggerClasses(){
		int i = 1 + 2;
	}
	
	@Test
	public void logAnnotMethodsInDifferentModes(){
		methodWithArgumentsAndReturnType(null, null, null, null, null);
		methodWithNoReturn();
		checkCache();
	}
	
	@Test
	public void testPluggLoggerClasses(){
		testOtherPluggableLoggerClasses();
		System.out.println("Chached Map String: "+LoggableObjects.cachedLoggers);
		System.out.println("--------- Testing Logglable methods with default or system default Pluggable Logger");
		methodWithNoReturn();
	}
	
	private void checkCache(){
		System.out.println("Chached Map String: "+LoggableObjects.cachedLoggers);
	}

}
