package com.javabi.arduino.util;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

	private final Logger log;
	private final AtomicBoolean enabled = new AtomicBoolean(true);

	public Log(Class<?> type) {
		this.log = LoggerFactory.getLogger(type);
	}

	public Log enable(boolean enable) {
		enabled.set(enable);
		return this;
	}

	public boolean isEnabled() {
		return enabled.get();
	}

	public void info(String text) {
		if (isEnabled()) {
			log.info(text);
		}
	}

	public void info(String text, Object... args) {
		if (isEnabled()) {
			log.info(text, args);
		}
	}

	public void warn(String text, Object... args) {
		log.warn(text, args);
	}

	public void warn(String text) {
		log.warn(text);
	}

	public void error(String text, Object... args) {
		log.error(text, args);
	}

	public void error(String text) {
		log.error(text);
	}

	public void error(String text, Throwable cause) {
		log.error(text, cause);
	}
}
