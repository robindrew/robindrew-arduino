package com.robindrew.arduino;

public class ArduinoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ArduinoException(Exception cause) {
		super(cause);
	}

	public ArduinoException(String message) {
		super(message);
	}
}
