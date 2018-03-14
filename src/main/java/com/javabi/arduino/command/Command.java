package com.javabi.arduino.command;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Stopwatch;
import com.javabi.arduino.Arduino;
import com.javabi.arduino.connection.Connection;
import com.javabi.arduino.connection.InputBuffer;
import com.javabi.arduino.connection.OutputBuffer;
import com.javabi.arduino.util.Log;

public abstract class Command<D> implements ICommand<D> {

	private static final Log log = new Log(Command.class).enable(Arduino.COMMAND_LOGGING);

	public static final int COMMAND_READY = 0;
	public static final int COMMAND_ECHO = 1;
	public static final int COMMAND_PIN_MODE = 2;
	public static final int COMMAND_DIGITAL_WRITE = 3;
	public static final int COMMAND_DIGITAL_READ = 4;
	public static final int COMMAND_ANALOG_WRITE = 5;
	public static final int COMMAND_ANALOG_READ = 6;
	public static final int COMMAND_DELAY = 7;
	public static final int COMMAND_ANALOG_REFERENCE = 8;
	public static final int COMMAND_DIGITAL_READ_WAIT = 9;
	public static final int COMMAND_ANALOG_READ_WAIT = 10;

	public static String getCommandName(int id) {
		switch (id) {
		case COMMAND_READY:
			return "ready";
		case COMMAND_ECHO:
			return "echo";
		case COMMAND_PIN_MODE:
			return "pinMode";
		case COMMAND_DIGITAL_READ:
			return "digitalRead";
		case COMMAND_DIGITAL_WRITE:
			return "digitalWrite";
		case COMMAND_ANALOG_READ:
			return "analogRead";
		case COMMAND_ANALOG_WRITE:
			return "analogWrite";
		default:
			return "unknown";
		}
	}

	private volatile D data = null;
	private final AtomicBoolean started = new AtomicBoolean(false);
	private final AtomicBoolean finished = new AtomicBoolean(false);
	private final AtomicBoolean executed = new AtomicBoolean(false);

	public boolean isExecuted() {
		return executed.get();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	public abstract String toString();

	@Override
	public D getData() {
		return data;
	}

	@Override
	public boolean hasData() {
		return data != null;
	}

	@Override
	public void setData(D data) {
		if (data == null) {
			throw new NullPointerException("data");
		}
		this.data = data;
	}

	@Override
	public D execute(Connection connection) {
		Stopwatch timer = Stopwatch.createStarted();

		OutputBuffer output = connection.getOutput();
		InputBuffer input = connection.getInput();

		// Logging
		log.info("[Request] {}", this);

		// Send request
		output.writeNumber(1);
		sendRequest(output);
		output.flush(true);

		// Receive response
		receiveResponse(input);

		// Logging
		if (hasData()) {
			log.info("[Response] {} -> {}", this, getData());
		} else {
			log.info("[Response] {}", this);
		}

		timer.stop();
		log.info("[Executed] {} in {}", getName(), timer);
		return getData();
	}

	@Override
	public final void send(OutputBuffer output) {
		if (!started.compareAndSet(false, true)) {
			throw new IllegalStateException("command already started: " + this);
		}

		// Send the request
		sendRequest(output);
	}

	@Override
	public void receive(InputBuffer input) {
		if (!finished.compareAndSet(false, true)) {
			throw new IllegalStateException("command already finished: " + this);
		}

		// Receive the response
		receiveResponse(input);

		// Executed!
		executed.set(true);
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return isExecuted();
	}

	@Override
	public D get() {
		while (!isExecuted()) {
			Arduino.sleep(20);
		}
		return getData();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	@Override
	public D get(long timeout, TimeUnit unit) {
		throw new UnsupportedOperationException();
	}

	protected abstract void sendRequest(OutputBuffer output);

	protected abstract void receiveResponse(InputBuffer input);

}
