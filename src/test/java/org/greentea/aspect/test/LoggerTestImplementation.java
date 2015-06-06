package org.greentea.aspect.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.greentea.aspect.log.PluggableLogger;

public class LoggerTestImplementation implements PluggableLogger {

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
		return "***** Test Implementations ==> LoggerTestImplementation.class  - It is the way to Know It's flexible *****";
	}

}
