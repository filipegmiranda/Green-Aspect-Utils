package org.greentea.aspect.log;

import java.lang.reflect.Modifier;
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

		Class<?> clazzTypes[] = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			Class<?> clazz = null;
			int loadedClasses = 0;
			if (args[i] == null) {
				System.out.println("value of arg:\n" + args[i]);
				
				String completeSignature = methodSignature.toLongString();

				System.out.println("n\n"+completeSignature+"\n\n");
				
				Pattern p = Pattern
						.compile("[\\w+.-]+([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*");

				Matcher m = p.matcher(completeSignature);
				
				
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
				System.out.println(clazzName);
				clazz = Class.forName(clazzName);
				loadedClasses++;
			}else{
				clazz = args[i].getClass();
				loadedClasses++;
			}
			
			System.out.println("\n\n"+clazz+"\n\n");

			clazzTypes[i] = clazz;
		}

		@SuppressWarnings("unchecked")
		LoggableObject loggObject = methodSignature.getDeclaringType()
				.getDeclaredMethod(methodName, clazzTypes)
				.getAnnotation(LoggableObject.class);

		if (loggObject.disable()) {
			return proJoinPoint.proceed();
		}

		LoggableObject.LogModes[] logModes = loggObject.logMode();

		boolean profileMode = false;
		boolean argsMode = false;
		boolean methodMode = false;

		loModesLoop: for (int i = 0; i < logModes.length; i++) {
			switch (logModes[i]) {
			case ALL:
				profileMode = true;
				argsMode = true;
				methodMode = true;
				break loModesLoop;
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

		// TODO support different strategies for log
		Logger logger = Logger.getLogger("greentea.logger");
		//

		// getting modifiers
		if (methodMode || argsMode) {
			logger.info(String.format(
					"Entering execution of method %s, of class %s", methodName,
					declaringClass));
		}

		if (argsMode && args.length > 0) {
			logger.info(String.format("Arguments of method %s are: %s",
					methodName, java.util.Arrays.toString(args)));
		}

		Object returnObject;
		try {
			long startNanoTime = System.nanoTime();
			returnObject = proJoinPoint.proceed();
			long execTime = System.nanoTime() - startNanoTime;
			if (profileMode) {
				logger.info(String.format(
						"Finished execution of %s in %s nanoTime", methodName,
						execTime));
			}
		} catch (Exception e) {
			logger.info("\nException in the method " + methodName
					+ " of class: " + methodSignature.getDeclaringTypeName());
			if (!loggObject.exceptionOnlyToExceptions()) {
				throw e;
			}
			returnObject = null;
		}
		return returnObject;
	}

}
