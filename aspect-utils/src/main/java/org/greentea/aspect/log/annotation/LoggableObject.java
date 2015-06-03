package org.greentea.aspect.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Filipe Gonzaga Miranda
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface LoggableObject {
	
	LogType[] value(); 
	
	enum LogType{
		PROFILE,
		METHOD_NAME,
		ARGS
	}
}
