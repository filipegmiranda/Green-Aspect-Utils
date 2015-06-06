package org.greentea.aspect.log;

/**
 * This class will allow better flexibility when logging, because,
 * There might different techniques to log, such as log to a FIle, 
 * sending the log message to a JMS Topics or using other logging strategies
 * <br>
 * The implemented class should privided at least one non-argument contructor
 * 
 * @author Filipe Gonzaga Miranda
 */
public interface PluggableLogger{

	/**
	 * Implementors should implement this method to log Info level log information
	 */
	void logInfo(String msg);
	
	/**
	 * Implementors should implement this method to log Error level information
	 */
	void logError(String msg);
	
	/**
	 * Implementors should implement this method to log Error level information
	 */
	void logWarning(String msg);
	
	
	/**
	 * Implement this method to allow an INstance of This Log to be cached, so that, when Required, it will then return
	 * this instance instead of creating a new one
	 * @return
	 */
	PluggableLogger getForCache();
	
	
}
