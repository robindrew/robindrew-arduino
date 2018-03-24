package com.robindrew.arduino.board;

import java.util.Set;

import com.robindrew.arduino.command.ICommandList;
import com.robindrew.arduino.connection.Connection;
import com.robindrew.arduino.pin.Pin;

public interface IArduinoBoard extends AutoCloseable {

	ICommandList newBatch();

	Connection getConnection();

	Set<Pin> getAnalogPins();

	Set<Pin> getDigitalPins();

	Set<Pin> getDigitalPwmPins();

	void close();
}
