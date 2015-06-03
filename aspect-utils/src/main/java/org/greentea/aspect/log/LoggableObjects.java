package org.greentea.aspect.log;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.greentea.aspect.log.annotation.LoggableObject;

/**
 * Loggableobjects are either Methods or Constructors that migh be logged <br>
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
	 * Captures the
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
			clazzTypes[i] = args[i].getClass();
		}
		
		@SuppressWarnings("unchecked")
		LoggableObject loggObject = methodSignature.getDeclaringType().getDeclaredMethod(methodName, clazzTypes).getAnnotation(LoggableObject.class);
		
		String strArgs = java.util.Arrays.toString(args);
		Logger logger = Logger.getLogger("greentea.logger");
		logger.info(String.format(
				"Entering execution of method %s, of class %s", methodName,
				declaringClass));
		logger.info(String.format("Arguments of method %s are: %", methodName,
				strArgs));
		long startTime = System.currentTimeMillis();
		Object returnObject = proJoinPoint.proceed();
		logger.info(String.format("Finished execution of % in %d", methodName,
				System.currentTimeMillis() - startTime));
		return returnObject;
	}

}
