package org.greentea.aspect.log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.greentea.aspect.log.annotation.LoggableObject;

/**
 * LoggableObjects are either Methods or Constructors that migh be logged <br>
 * The information logged is the method signature, its arguments and execution
 * time
 * 
 * The level of verbosity, and information might be configurable through the use
 * of the annotation @LoggableObjet
 * 
 * @author Filipe Gonzaga Miranda
 */
@Aspect
public class LoggableObjects {

	//PluggableLogger pluggableLogger;
	
	public static ConcurrentMap<Class<? extends PluggableLogger>, PluggableLogger> cachedLoggers = new ConcurrentHashMap<>();
	
	private static final Pattern pMethodsName = Pattern
			.compile("[\\w+.-]+([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*");
	
	static{
		cachedLoggers.putIfAbsent(DefaultPluggableLoggerIfNotInjected.class, new DefaultPluggableLoggerIfNotInjected());
	}
	
	/**
	 * Captures the Annotations {@link LoggableObjects}
	 * 
	 * And applies the logic to decide how to log the information based on the
	 * LogModes
	 * 
	 * @param proJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* *(..)) && @annotation(LoggableObject)")
	public Object aroundObjects(ProceedingJoinPoint proJoinPoint)
			throws Throwable {
		Signature methodSignature = proJoinPoint.getSignature();
		String declaringClass = methodSignature.getDeclaringTypeName();
		String methodName = methodSignature.getName();
		Object[] args = proJoinPoint.getArgs();

		LoggableObject loggObject = getLoggableObjectAnnt(args, methodSignature);

		if (loggObject.disable()) {
			return proJoinPoint.proceed();
		}
		
		PluggableLogger logger;

		
		Class<? extends PluggableLogger> clazzPluggLogg = loggObject.pluggableLoggerClass();
		
		if(clazzPluggLogg != DefaultPluggableLoggerIfNotInjected.class){
			if(cachedLoggers.containsKey(clazzPluggLogg)){
				logger = cachedLoggers.get(clazzPluggLogg);
			}else{
				logger = clazzPluggLogg.newInstance();
				cachedLoggers.putIfAbsent(clazzPluggLogg, logger);
			}
		}else{
			logger = cachedLoggers.get(clazzPluggLogg);
		}

		LoggableObject.LogModes[] logModes = loggObject.logMode();

		boolean profileMode = false;
		boolean argsMode = false;
		boolean methodMode = false;
		
		logModesLoop: for (int i = 0; i < logModes.length; i++) {
			switch (logModes[i]) {
			case ALL:
				profileMode = true;
				argsMode = true;
				methodMode = true;
				break logModesLoop;
			case PROFILE:
				profileMode = true;
				break;
			case METHOD_NAME:
				methodMode = true;
				break;
			case ARGS:
				argsMode = true;
				break;
			default:
				throw new AssertionError("Operation not supported. "
						+ logModes[i].name());
			}
		}


		// getting modifiers
		if (methodMode || argsMode) {
			logger.logInfo(String.format(
					"Entering execution of method %s, of class %s", methodName,
					declaringClass));
		}

		
		if (argsMode && args.length > 0) {
			logger.logInfo(String.format("Arguments of method %s are: %s",
					methodName, java.util.Arrays.toString(args)));
		}

		Object returnObject;
		try {
			long startNanoTime = System.nanoTime();
			returnObject = proJoinPoint.proceed();
			long execTime = System.nanoTime() - startNanoTime;
			if (profileMode) {
				logger.logInfo(String.format(
						"Finished execution of %s in %s nanoTime", methodName,
						execTime));
			}
		} catch (Exception e) {
			logger.logError("\nException in the method " + methodName
					+ " of class: " + methodSignature.getDeclaringTypeName());
			if (!loggObject.exceptionOnlyToExceptions()) {
				throw e;
			}
			logger.logWarning("Exception " + e + " supressed. Setting returning value to null...");
			
			returnObject = null;
		}
		return returnObject;
	}
	
	@SuppressWarnings("unchecked")
	private LoggableObject getLoggableObjectAnnt(Object[] args, Signature methodSignature) throws Exception{
		int loadedClasses = 0;
		Class<?> clazzTypes[] = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			Class<?> clazz = null;
			if (args[i] == null) {
				String completeSignature = methodSignature.toLongString();

				Matcher m = pMethodsName.matcher(completeSignature);
				
				m.find();
				m.find();
				m.find();
				int pos = 0;
				while(pos < loadedClasses){
					m.find();
					pos++;
				}
				m.find();
				String clazzName = m.group();
				clazz = Class.forName(clazzName);
				loadedClasses++;
			}else{
				clazz = args[i].getClass();
				loadedClasses++;
			}
			clazzTypes[i] = clazz;
		}

		
		return methodSignature.getDeclaringType()
				.getDeclaredMethod(methodSignature.getName(), clazzTypes)
				.getAnnotation(LoggableObject.class);

	}
	
	/**
	 * Default implementation of {@link PluggableLogger} using
	 * {@link java.util.logging.Logger} internally to Log msgs
	 * @author Filipe Gonzaga Miranda
	 */
	public static class DefaultPluggableLoggerIfNotInjected implements PluggableLogger{

		@Override
		public void logInfo(String msg) {
			Logger logger = Logger.getLogger("greentea.logger");
			logger.log(Level.INFO, msg);
		}

		@Override
		public void logError(String msg) {
			Logger logger = Logger.getLogger("greentea.logger");
			logger.log(Level.SEVERE, msg);
		}

		@Override
		public void logWarning(String msg) {
			Logger logger = Logger.getLogger("greentea.logger");			
			logger.log(Level.WARNING, msg);
		}

		@Override
		public PluggableLogger getForCache() {
			return this;
		}
		
		@Override
		public String toString() {
			return "DefaultPluggableLoggerIfNotInjected - It has a java.util.logging.Logger";
		}
	}

}
