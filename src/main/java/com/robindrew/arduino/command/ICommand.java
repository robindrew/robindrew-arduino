package com.robindrew.arduino.command;

import java.util.concurrent.Future;

import com.robindrew.arduino.connection.Connection;
import com.robindrew.arduino.connection.InputBuffer;
import com.robindrew.arduino.connection.OutputBuffer;

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