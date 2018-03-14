package com.javabi.arduino.command;

import java.util.concurrent.Future;

import com.javabi.arduino.connection.Connection;
import com.javabi.arduino.connection.InputBuffer;
import com.javabi.arduino.connection.OutputBuffer;

public interface ICommand<D> extends Future<D> {

	String getName();

	D getData();

	boolean hasData();

	void setData(D data);

	D execute(Connection connection);

	void send(OutputBuffer output);

	void receive(InputBuffer input);

	boolean isExecuted();
}