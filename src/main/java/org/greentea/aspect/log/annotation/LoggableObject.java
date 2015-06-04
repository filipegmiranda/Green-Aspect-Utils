package org.greentea.aspect.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation in every method you want to track
 * information, configuring different level logs
 * @author Filipe Gonzaga Miranda
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface LoggableObject{
	/**
	 * A list containing the Log Modes applicable to  the methods
	 * Note: If LogModes.ALL is defined all other options will be enabled too, ALL means all modes, which is the more 
	 * complete and verbose LogMode<br>
	 * The default is LogMode.Args
	 * @return
	 */
	LogModes[] logMode() default {LogModes.ARGS}; 
	
	/**
	 * The default Behavior of the Green LOg API is to capture the exception if it occurs while
	 * executing the Decorated Method, Log the Exception and rethrow it.
	 * <br>
	 * If you want to, you suppress the exceptions and require the Green Logger to only log them
	 * 
	 * <h2> Be Aware that if this option is enabled the application might have strange results and unpredictable outcome<br>
	 * Since the return value of the returning object will be null(does not apply for void methods)
	 * </h2>
	 * 
	 * @return true if the exceptions should only be logged and not rethrown 
	 */
	boolean exceptionOnlyToExceptions() default false;
	
	/**
	 * Disables all behavior defined in this annotation properties, as if there were no Annotation
	 * Default is false
	 * @return true if the log is disabled, false otherwise
	 */
	boolean disable() default false;
	
	/**
	 * The LogModes Availaible
	 * @author Filipe Gonzaga Miranda
	 *
	 */
	enum LogModes{
		/**
		 * Only logs the profile information
		 * about the method executions, such as how long the method took
		 * to execute and the calling thread
		 */
		PROFILE,
		/**
		 * Only Logs the methods name 
		 */
		METHOD_NAME,
		/**
		 * Logs the method's name plus its arguments and toString Method
		 */
		ARGS,
		/**
		 * Highest Log Mode, logging information about time execution, methods name, calling thread, args values and return toStrignMethod
		 */
		ALL
	}
}
