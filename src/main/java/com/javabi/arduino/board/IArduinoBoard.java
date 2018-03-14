package com.javabi.arduino.board;

import java.util.Set;

import com.javabi.arduino.command.ICommandList;
import com.javabi.arduino.connection.Connection;
import com.javabi.arduino.pin.Pin;

public interface IArduinoBoard extends AutoCloseable {

	ICommandList newBatch();

	Connection getConnection();

	Set<Pin> getAnalogPins();

	Set<Pin> getDigitalPins();

	Set<Pin> getDigitalPwmPins();

	void close();
}
