package com.robindrew.arduino.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Stopwatch;
import com.robindrew.arduino.Arduino;
import com.robindrew.arduino.connection.Connection;
import com.robindrew.arduino.connection.InputBuffer;
import com.robindrew.arduino.connection.OutputBuffer;
import com.robindrew.arduino.pin.Level;
import com.robindrew.arduino.pin.Mode;
import com.robindrew.arduino.pin.Pin;
import com.robindrew.arduino.util.Log;

public class CommandBatch implements ICommandList {

	private static final Log log = new Log(CommandBatch.class).enable(Arduino.COMMAND_LOGGING);

	private final List<ICommand<?>> list = new ArrayList<>();

	private final Connection connection;

	public CommandBatch(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void execute() {
		Stopwatch timer = Stopwatch.createStarted();

		OutputBuffer output = connection.getOutput();
		InputBuffer input = connection.getInput();

		// Send
		output.writeNumber(list.size());
		for (ICommand<?> command : list) {

			// Logging
			log.info("[Request] {}", command);

			// Write the command to the output buffer
			command.send(output);
		}

		// Flush the commands to the Ardunio
		output.flush(true);

		// Receive
		for (ICommand<?> command : list) {

			// Read the command from the input buffer
			command.receive(input);

			// Logging
			if (command.hasData()) {
				log.info("[Response] {} -> {}", command, command.getData());
			} else {
				log.info("[Response] {}", command);
			}
		}

		timer.stop();
		log.info("[Executed] {} commands in {}", list.size(), timer);
	}

	@Override
	public ICommandList add(ICommand<?> command) {
		if (command == null) {
			throw new NullPointerException("command");
		}
		list.add(command);
		return this;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public ICommandList clear() {
		list.clear();
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <D> ICommand<D> get(int index) {
		return (ICommand<D>) list.get(index);
	}

	@Override
	public <D> D getData(int index) {
		ICommand<D> command = get(index);
		return command.getData();
	}

	@Override
	public ICommandList echo(String text) {
		return add(new Echo(text));
	}

	@Override
	public ICommandList delay(int millis) {
		return add(new Delay(millis));
	}

	@Override
	public ICommandList pinMode(int pin, int mode) {
		return add(new PinMode(pin, mode));
	}

	@Override
	public ICommandList digitalWrite(int pin, int value) {
		return add(new DigitalWrite(pin, value));
	}

	@Override
	public ICommandList analogWrite(int pin, int value) {
		return add(new AnalogWrite(pin, value));
	}

	@Override
	public ICommandList pinMode(Pin pin, Mode mode) {
		return add(new PinMode(pin, mode));
	}

	public ICommandList setOutputPins(Pin... pins) {
		for (Pin pin : pins) {
			pinMode(pin, Mode.OUTPUT);
		}
		return this;
	}

	public ICommandList setInputPins(Pin... pins) {
		for (Pin pin : pins) {
			pinMode(pin, Mode.INPUT);
		}
		return this;
	}

	@Override
	public ICommandList digitalWrite(Pin pin, int value) {
		return add(new DigitalWrite(pin, value));
	}

	@Override
	public ICommandList digitalWrite(Pin pin, Level level) {
		return add(new DigitalWrite(pin, level));
	}

	@Override
	public ICommandList digitalWrite(int pin, Level level) {
		return add(new DigitalWrite(pin, level));
	}

	@Override
	public ICommandList analogWrite(Pin pin, int value) {
		return add(new AnalogWrite(pin, value));
	}

	@Override
	public DigitalRead digitalRead(int pin) {
		DigitalRead command = new DigitalRead(pin);
		add(command);
		return command;
	}

	@Override
	public DigitalRead digitalRead(Pin pin) {
		DigitalRead command = new DigitalRead(pin);
		add(command);
		return command;
	}

	@Override
	public List<DigitalRead> digitalRead(Pin... pins) {
		List<Pin> pinList = Arrays.asList(pins);
		return digitalRead(pinList);
	}

	@Override
	public List<DigitalRead> digitalRead(Collection<? extends Pin> pins) {
		List<DigitalRead> list = new ArrayList<>();
		for (Pin pin : pins) {
			DigitalRead command = new DigitalRead(pin);
			add(command);
			list.add(command);
		}
		return list;
	}

	@Override
	public AnalogRead analogRead(int pin) {
		AnalogRead command = new AnalogRead(pin);
		add(command);
		return command;
	}

	@Override
	public AnalogRead analogRead(Pin pin) {
		AnalogRead command = new AnalogRead(pin);
		add(command);
		return command;
	}

	@Override
	public List<AnalogRead> analogRead(Pin... pins) {
		List<Pin> pinList = Arrays.asList(pins);
		return analogRead(pinList);
	}

	@Override
	public List<AnalogRead> analogRead(Collection<? extends Pin> pins) {
		List<AnalogRead> list = new ArrayList<>();
		for (Pin pin : pins) {
			AnalogRead command = new AnalogRead(pin);
			add(command);
			list.add(command);
		}
		return list;
	}

}
