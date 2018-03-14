package com.javabi.arduino.connection;

import com.javabi.arduino.ArduinoException;

public class ConnectException extends ArduinoException {

	private static final long serialVersionUID = 1L;

	public ConnectException(String message) {
		super(message);
	}

	public ConnectException(Exception e) {
		super(e);
	}
}
